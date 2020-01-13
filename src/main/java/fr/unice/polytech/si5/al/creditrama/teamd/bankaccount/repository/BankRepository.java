package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.repository;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

}
