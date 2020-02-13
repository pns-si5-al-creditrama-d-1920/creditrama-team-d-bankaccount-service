package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.controller;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.kafka.TransferStreams;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccountRequest;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.CardRequest;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "content-type")
@RestController
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("clients/{clientId}/accounts")
    public ResponseEntity<BankAccount> createAccount(@PathVariable("clientId") Long clientId, @RequestBody BankAccountRequest bankAccountRequest) {
        return new ResponseEntity<>(bankAccountService.createAccount(clientId, bankAccountRequest.getAmount()), HttpStatus.OK);
    }

    @GetMapping("clients/{clientId}/accounts")
    public ResponseEntity<List<BankAccount>> getBankAccountsById(@PathVariable("clientId") Long clientId) {
        return new ResponseEntity<>(bankAccountService.getBankAccountsByClient(clientId), HttpStatus.OK);
    }

    @PostMapping("accounts/{iban}/cards")
    public ResponseEntity<BankAccount> addCard(@PathVariable("iban") String iban, @RequestBody CardRequest card) {
        return new ResponseEntity<>(bankAccountService.addCard(iban, card.getNumber()), HttpStatus.OK);
    }

    @GetMapping("accounts/{iban}")
    public ResponseEntity<Optional<BankAccount>> getBankAccountsByIban(@PathVariable("iban") String iban) {
        return new ResponseEntity<>(bankAccountService.getBankAccountByIban(iban), HttpStatus.OK);
    }

    @PostMapping("clients/{id}/recipients")
    public ResponseEntity<List<BankAccount>> getBankAccountsByIban(@PathVariable long id, @RequestBody List<String> ibans) {
        return new ResponseEntity<>(bankAccountService.getBankAccountByIban(ibans), HttpStatus.OK);
    }

    //TODO SECURE THIS
    @PatchMapping("accounts/{iban}")
    public ResponseEntity<BankAccount> updateBanAccount(@PathVariable String iban, @RequestParam Double balance) {
        return new ResponseEntity<>(bankAccountService.updateBalance(iban, balance), HttpStatus.OK);
    }


}
