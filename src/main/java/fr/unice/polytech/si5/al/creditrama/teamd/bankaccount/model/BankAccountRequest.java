package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model;


import lombok.*;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BankAccountRequest {
    private double amount;
}
