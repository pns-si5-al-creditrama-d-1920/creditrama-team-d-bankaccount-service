package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
public class CardRequest implements Serializable {
    Long number;
}
