package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.Bank;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankService {
    private final BankRepository bankRepository;

    @Autowired
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public boolean haveBank() {
        return bankRepository.count() == 1;
    }

    public Optional<Bank> getCurrentBank() {
        return Optional.of(bankRepository.findAll().get(0));
    }

    public void createBank(Bank bank) {
        bankRepository.save(bank);
    }

}
