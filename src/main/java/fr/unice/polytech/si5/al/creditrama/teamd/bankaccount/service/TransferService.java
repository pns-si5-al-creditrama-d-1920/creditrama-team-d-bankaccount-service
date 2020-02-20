package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.kafka.TransferStreams;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.TransferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import java.util.Optional;

import static fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.kafka.TransferStreams.MAKE_TRANSFER_TOPIC;
import static fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.kafka.TransferStreams.REVERSE_TRANSFER_TOPIC;

@Service
@EnableBinding(TransferStreams.class)
@Profile("!disable-kafka")
public class TransferService {
    private BankAccountService bankAccountService;
    private TransferStreams transferStreams;

    @Autowired
    public TransferService(BankAccountService bankAccountService, TransferStreams transferStreams) {
        this.bankAccountService = bankAccountService;
        this.transferStreams = transferStreams;
    }

    @Transactional
    void transferMoney(TransferDTO transferDTO) {
        System.out.println("Processing transfer...");
        Optional<BankAccount> sourceBankAccount = bankAccountService.getBankAccountByIban(transferDTO.getSourceIban());
        Optional<BankAccount> destBankAccount = bankAccountService.getBankAccountByIban(transferDTO.getDestIban());

        if (!sourceBankAccount.isPresent() || !destBankAccount.isPresent()) {
            //publish inside kafka error topic
            System.out.println("Error in transfer");
            transferError(transferDTO);
        } else {
            sourceBankAccount.get().removeMoney(transferDTO.getAmount());
            destBankAccount.get().addMoney(transferDTO.getAmount());
            bankAccountService.updateBalance(transferDTO.getSourceIban(), sourceBankAccount.get().getBalance());
            bankAccountService.updateBalance(transferDTO.getDestIban(), destBankAccount.get().getBalance());
        }
    }

    public void transferDone(TransferDTO transferDTO) {
        MessageChannel messageChannel = transferStreams.transferDone();
        messageChannel.send(MessageBuilder
                .withPayload(transferDTO)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }

    public void transferError(TransferDTO transferDTO) {
        MessageChannel messageChannel = transferStreams.transferError();
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
