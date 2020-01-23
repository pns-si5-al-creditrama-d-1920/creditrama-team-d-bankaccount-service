package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.repository;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    List<BankAccount> findBankAccountByClient(final long clientId);

    Optional<BankAccount> findBankAccountByIban(final String iban);

    List<BankAccount> findBankAccountByIbanIn(final List<String> iban);
}
