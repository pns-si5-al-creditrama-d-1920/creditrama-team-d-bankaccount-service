package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.kafka.BankAccountStreams;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.Bank;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.TransferDTO;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.repository.BankAccountRepository;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.kafka.BankAccountStreams.MAKE_TRANSFER_TOPIC;
import static fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.kafka.BankAccountStreams.REVERSE_TRANSFER_TOPIC;

@Service
@EnableBinding(BankAccountStreams.class)
@Profile("!disable-kafka")
public class BankAccountService {
    private BankAccountRepository bankAccountRepository;
    private BankService bankService;
    private BankAccountStreams bankAccountStreams;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, BankService bankService, BankAccountStreams bankAccountStreams) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankService = bankService;
        this.bankAccountStreams = bankAccountStreams;
    }

    public BankAccount createAccount(long clientId, double amount) {
        Bank bank = bankService.getCurrentBank().get();
        Iban iban = new Iban.Builder()
                .countryCode(CountryCode.valueOf(bank.getCountryCode()))
                .bankCode(Long.toString(bank.getBankCode()))
                .buildRandom();
        BankAccount bankAccount = generateBankAccount(clientId, iban, amount);
        return bankAccountRepository.save(bankAccount);
    }

    private BankAccount generateBankAccount(long clientId, Iban iban, double amount) {
        return BankAccount.builder().balance(amount)
                .iban(iban.toString())
                .accountNumber(iban.getAccountNumber())
                .client(clientId)
                .accountNumber(iban.getAccountNumber())
                .bankCode(iban.getBankCode())
                .cards(new HashSet<>())
                .build();
    }

    public List<BankAccount> getBankAccountsByClient(final long clientid) {
        return bankAccountRepository.findBankAccountByClient(clientid);
    }

    public Optional<BankAccount> getBankAccountByIban(final String iban) {
        return bankAccountRepository.findBankAccountByIban(iban);
    }

    public List<BankAccount> getBankAccountByIban(final List<String> iban) {
        return bankAccountRepository.findBankAccountByIbanIn(iban);
    }

    public BankAccount updateBalance(String iban, double balance) {
        BankAccount bankAccountByIban = getBankAccountByIban(iban).orElseThrow(IllegalArgumentException::new);
        bankAccountByIban.setBalance(balance);
        bankAccountRepository.save(bankAccountByIban);
        return bankAccountByIban;
    }

    public BankAccount addCard(String iban, Long number) {
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findBankAccountByIban(iban);
        if (!optionalBankAccount.isPresent()) {
            return null;
        }
        BankAccount bankAccount = optionalBankAccount.get();
        Set<Long> newCards = bankAccount.getCards();
        newCards.add(number);
        bankAccount.setCards(newCards);
        return bankAccountRepository.save(bankAccount);
    }

    private void transferMoney(TransferDTO transferDTO) {
        System.out.println("Processing transfer...");
        Optional<BankAccount> sourceBankAccount = getBankAccountByIban(transferDTO.getSourceIban());
        Optional<BankAccount> destBankAccount = getBankAccountByIban(transferDTO.getDestIban());

        if (!sourceBankAccount.isPresent() || !destBankAccount.isPresent()) {
            //publish inside kafka error topic
            System.out.println("Error in transfer");
            transferError(transferDTO);
        } else {
            sourceBankAccount.get().removeMoney(transferDTO.getAmount());
            destBankAccount.get().addMoney(transferDTO.getAmount());
            updateBalance(transferDTO.getSourceIban(), sourceBankAccount.get().getBalance());
            updateBalance(transferDTO.getDestIban(), destBankAccount.get().getBalance());
        }
    }

    public void transferDone(TransferDTO transferDTO) {
        MessageChannel messageChannel = bankAccountStreams.transferDone();
        messageChannel.send(MessageBuilder
                .withPayload(transferDTO)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }

    public void transferError(TransferDTO transferDTO) {
        MessageChannel messageChannel = bankAccountStreams.transferError();
        messageChannel.send(MessageBuilder
                .withPayload(transferDTO)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }

    @StreamListener(REVERSE_TRANSFER_TOPIC)
    public void reverseTransfer(TransferDTO transferDTO) {
        transferMoney(transferDTO);
        // publish inside kafka reverse topic
        transferError(transferDTO);
    }

    @StreamListener(MAKE_TRANSFER_TOPIC)
    public void makeTransfer(TransferDTO transferDTO) {
        transferMoney(transferDTO);
        // publish inside kafka done topic
        transferDone(transferDTO);
    }
}
