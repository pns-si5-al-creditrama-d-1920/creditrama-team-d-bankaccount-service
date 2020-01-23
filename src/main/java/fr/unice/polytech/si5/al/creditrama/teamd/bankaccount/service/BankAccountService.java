package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.Bank;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.repository.BankAccountRepository;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {
    private BankAccountRepository bankAccountRepository;
    private BankService bankService;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, BankService bankService) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankService = bankService;
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
}
