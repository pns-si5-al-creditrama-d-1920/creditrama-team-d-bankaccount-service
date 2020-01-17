package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Bank {
    @Id
    private Long bankCode;

    @Column(nullable = false)
    @NotNull
    @Pattern(regexp = "[A-Z]{2,3}")
    private String countryCode;

    @Column(nullable = false)
    private String bankName;

    private String bic;

}
