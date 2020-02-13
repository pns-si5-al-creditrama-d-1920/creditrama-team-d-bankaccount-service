package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TransferStreams {
    String MAKE_TRANSFER_TOPIC = "CreditRama.Transaction.BankAccount.MakeTransfer";
    String REVERSE_TRANSFER_TOPIC = "CreditRama.Transaction.BankAccount.ReverseTransfer";

    @Input(MAKE_TRANSFER_TOPIC)
    MessageChannel makeTransfer();

    @Input(REVERSE_TRANSFER_TOPIC)
    MessageChannel reverseTransfer();

    @Output("CreditRama.Transaction.BankAccount.TransferDone")
    MessageChannel transferDone();

    @Output("CreditRama.Transaction.BankAccount.TransferRejected")
    MessageChannel transferError();
}
