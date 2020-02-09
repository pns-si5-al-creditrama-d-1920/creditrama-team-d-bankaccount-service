package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.repository.BankAccountRepository;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service.BankAccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("disable-kafka")
@Transactional
public class BankAccountTests {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private Long clientId;

    @Before
    public void setUp() {
        clientId = 42L;
    }

    @Test
    public void updateBalanceOfBankAccount() {
        assertEquals(new ArrayList<>(), bankAccountRepository.findBankAccountByClient(clientId));
        BankAccount account = bankAccountService.createAccount(clientId, 1000.0);

        assertEquals(Optional.of(account), bankAccountRepository.findBankAccountByIban(account.getIban()));
        assertTrue(bankAccountRepository.findAll().contains(account));
        assertTrue(bankAccountRepository.findBankAccountByClient(clientId).contains(account));

        assertEquals(Double.valueOf(1000.0), bankAccountRepository.findBankAccountByIban(account.getIban()).get().getBalance());
        bankAccountService.updateBalance(account.getIban(), 200.0);
        assertEquals(Double.valueOf(200.0), bankAccountRepository.findBankAccountByIban(account.getIban()).get().getBalance());
    }

    @Test
    public void newCardToBankAccount() {
        BankAccount account = bankAccountService.createAccount(clientId, 1000.0);
        Long cardNumber = 242406L;

        assertTrue(account.getCards().isEmpty());
        bankAccountService.addCard(account.getIban(), cardNumber);

        account = bankAccountRepository.findBankAccountByIban(account.getIban()).get();

        assertTrue(account.getCards().contains(cardNumber));
        assertEquals(1, account.getCards().size());

        bankAccountService.addCard(account.getIban(), cardNumber);

        account = bankAccountRepository.findBankAccountByIban(account.getIban()).get();
        assertEquals(1, account.getCards().size());
    }


    /**
     @Test public void manageBankAccountsOfClient() throws Exception {
     BankAccount expected = BankAccount.builder().balance(500.0).build();
     BankAccount received = business.createClientBankAccount(clientId, expected);
     String id = received.getAccountNumber();

     expected.setAccountNumber(id);
     Optional<BankAccount> actual = bankAccountRepository.findById(id);
     assertEquals(Optional.of(expected), actual);

     Optional<Client> client = clientRepository.findById(clientId);

     assertTrue(client.get().getBankAccounts().contains(expected));
     assertEquals(business.retrieveClientBankAccounts(clientId), client.get().getBankAccounts());
     }

     @Test public void manageRecipients() throws Exception {
     BankAccount account = BankAccount.builder().balance(999.99).build();
     String expectedBankAccountId = bankAccountRepository.save(account).getAccountNumber();

     Client client = new Client(null, "théo", "password", "n@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
     client.getBankAccounts().add(account);
     client = clientRepository.save(client);
     Integer clientId = client.getUserId();

     // Add a recipient
     String recipient = business.createClientRecipient(this.clientId, expectedBankAccountId);
     assertTrue(business.retrieveClientRecipients(this.clientId).contains(recipient));

     recipient = business.createClientRecipient(this.clientId, expectedBankAccountId);
     assertNull(recipient);

     recipient = business.createClientRecipient(clientId, expectedBankAccountId);
     assertNull(recipient);

     // Remove a recipient
     business.removeRecipient(this.clientId, expectedBankAccountId);
     assertFalse(business.retrieveClientRecipients(this.clientId).contains(recipient));
     }

     @Test public void transferToRecipient() throws Exception {
     BankAccount account = BankAccount.builder().balance(1000.0).build();
     Integer bankAccountId = bankAccountRepository.save(account).getAccountNumber();

     Client client = new Client(new Integer(0), "alexis", "password", "al@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
     client.getBankAccounts().add(account);
     Integer clientId = clientService.save(client).getUserId();

     assertEquals(100.0, bankAccountRepository.findById(this.bankAccountId).get().getBalance(), 0.01);
     assertEquals(1000.0, bankAccountRepository.findById(bankAccountId).get().getBalance(), 0.01);

     business.createClientRecipient(clientId, this.bankAccountId);

     BankTransaction transaction = BankTransaction.builder()
     .sourceId(bankAccountId)
     .destinationId(this.bankAccountId)
     .amount(500.0)
     .build();

     business.createClientTransaction(clientId, transaction);

     assertEquals(500.0, bankAccountRepository.findById(bankAccountId).get().getBalance(), 0.01);
     assertEquals(600.0, bankAccountRepository.findById(this.bankAccountId).get().getBalance(), 0.01);

     }

     @Test(expected = InvalidBankTransactionException.class)
     public void transferToSameAccount() throws Exception {
     BankTransaction transaction = BankTransaction.builder()
     .sourceId(bankAccountId)
     .destinationId(this.bankAccountId)
     .amount(500.0)
     .build();
     business.createClientTransaction(clientId, transaction);
     }

     @Test(expected = InvalidBankTransactionException.class)
     public void invalidTransaction() throws Exception {
     BankAccount account = BankAccount.builder().balance(1000.0).build();
     Integer bankAccountId = bankAccountRepository.save(account).getAccountNumber();

     Client client = new Client(null, "alexis", "password", "al@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
     client.getBankAccounts().add(account);
     Integer clientId = clientRepository.save(client).getUserId();

     BankTransaction transaction = BankTransaction.builder()
     .sourceId(this.bankAccountId)
     .destinationId(bankAccountId)
     .amount(500.0)
     .build();

     business.createClientTransaction(clientId, transaction);
     }

     @Test(expected = InvalidBankTransactionException.class)
     public void transferToNonRecipient() throws Exception {
     BankAccount account = BankAccount.builder().balance(1000.0).build();
     Integer bankAccountId = bankAccountRepository.save(account).getAccountNumber();

     Client client = new Client(null, "alexis", "password", "al@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
     client.getBankAccounts().add(account);
     Integer clientId = clientRepository.save(client).getUserId();

     BankTransaction transaction = BankTransaction.builder()
     .sourceId(bankAccountId)
     .destinationId(this.bankAccountId)
     .amount(500.0)
     .build();

     business.createClientTransaction(clientId, transaction);
     }

     @Test(expected = BankAccountNotFoundException.class)
     public void removeRecipientNotPresent() throws Exception {
     BankAccount account = BankAccount.builder().balance(999.99).build();
     Integer bankAccountId = bankAccountRepository.save(account).getAccountNumber();

     business.removeRecipient(this.clientId, bankAccountId);
     }
     **/
}
