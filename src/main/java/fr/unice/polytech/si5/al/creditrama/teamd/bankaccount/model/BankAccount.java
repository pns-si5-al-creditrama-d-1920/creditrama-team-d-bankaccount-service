package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Set;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BankAccount implements Serializable {

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Min(0)
    private Double balance;

    @Id
    @Column(nullable = false, unique = true)
    private String iban;


    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private long client;

    @ElementCollection
    private Set<Long> cards;

    public void addMoney(double amount) {
        this.balance += amount;
    }

    public void removeMoney(double amount) {
        this.balance -= amount;
    }
}
