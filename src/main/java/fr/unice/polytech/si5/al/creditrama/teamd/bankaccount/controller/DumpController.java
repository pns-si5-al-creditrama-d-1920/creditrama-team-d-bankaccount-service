package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.controller;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.repository.BankAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class DumpController {
    private BankAccountRepository repository;

    public DumpController(BankAccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/dump")
    public ResponseEntity<List<BankAccount>> instantPrettyDump() {
        return new ResponseEntity<>(this.repository.findAll(), HttpStatus.OK);
    }
}
