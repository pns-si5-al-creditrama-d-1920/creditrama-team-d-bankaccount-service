package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.aggregator;


import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.repository.BankRepository;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service.BankAccountService;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service.BankService;
import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.commands.MakeTransferCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.commands.ReverseTransferCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.events.TransactionRejectedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.events.TransferDoneEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.events.TransferReversedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Optional;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class BankAccountAggregate {

    @AggregateIdentifier
    private String bankUuid;

    private String transactionUuid;

    public BankAccountAggregate() {
    }

    @CommandHandler
    public BankAccountAggregate(MakeTransferCommand makeTransferCommand, BankAccountService bankAccountService) {
        System.out.println("Dans @CommandHandler MakeTransferCommand " + makeTransferCommand.toString());

        try {
            Optional<BankAccount> sourceBankAccountOpt = bankAccountService.getBankAccountByIban(makeTransferCommand.getSourceIban());
            Optional<BankAccount> destBankAccountOpt = bankAccountService.getBankAccountByIban(makeTransferCommand.getDestIban());

            BankAccount sourceBankAccount = sourceBankAccountOpt.orElse(null);
            BankAccount destBankAccount = destBankAccountOpt.orElse(null);

            bankAccountService.updateBalance(makeTransferCommand.getSourceIban(), sourceBankAccount.getBalance() - makeTransferCommand.getAmount());
            bankAccountService.updateBalance(makeTransferCommand.getDestIban(), destBankAccount.getBalance() + makeTransferCommand.getAmount());

            apply(new TransferDoneEvent(makeTransferCommand.getBankUuid(), makeTransferCommand.getUuid()));
        } catch (Exception e) {
            //FIXME reverse transfer ?
            apply(new TransactionRejectedEvent(makeTransferCommand.getUuid()));
        }
    }

    @SagaEventHandler(associationProperty = "bankUuid")
    protected void on(TransferDoneEvent transferDoneEvent) {
        System.out.println("Dans @EventHandler on " + transferDoneEvent.toString());
        this.bankUuid = transferDoneEvent.getBankUuid();
        this.transactionUuid = transferDoneEvent.getTransactionUuid();
    }

    @CommandHandler
    public void reverseTransfer(ReverseTransferCommand reverseTransferCommand, BankAccountService bankAccountService) {
        System.out.println("Dans @CommandHandler ReverseTransferCommand " + reverseTransferCommand.toString());

        try {
            Optional<BankAccount> sourceBankAccountOpt = bankAccountService.getBankAccountByIban(reverseTransferCommand.getSourceIban());
            Optional<BankAccount> destBankAccountOpt = bankAccountService.getBankAccountByIban(reverseTransferCommand.getDestIban());

            BankAccount sourceBankAccount = sourceBankAccountOpt.orElse(null);
            BankAccount destBankAccount = destBankAccountOpt.orElse(null);

            bankAccountService.updateBalance(reverseTransferCommand.getSourceIban(), sourceBankAccount.getBalance() + reverseTransferCommand.getAmount());
            bankAccountService.updateBalance(reverseTransferCommand.getDestIban(), destBankAccount.getBalance() - reverseTransferCommand.getAmount());

            apply(new TransferReversedEvent(reverseTransferCommand.getBankUuid(), reverseTransferCommand.getUuid()));
        } catch (Exception e) {
            apply(new TransactionRejectedEvent(reverseTransferCommand.getUuid()));
        }
    }

    //TODO vraiment besoin de ça ?
    @SagaEventHandler(associationProperty = "bankUuid")
    protected void on(TransferReversedEvent transferReversedEvent) {
        System.out.println("Dans @EventHandler on " + transferReversedEvent.toString());
        this.transactionUuid = transferReversedEvent.getTransactionUuid();
        this.bankUuid = transferReversedEvent.getBankUuid();
    }
}
