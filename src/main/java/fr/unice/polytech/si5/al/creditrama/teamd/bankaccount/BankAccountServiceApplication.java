package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount;

import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model.Bank;
import fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.service.BankService;
import org.iban4j.BicFormatException;
import org.iban4j.BicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@SpringBootApplication
@EnableConfigurationProperties
public class BankAccountServiceApplication implements CommandLineRunner {

    @Autowired
    private BankService bankService;

    public static void main(String[] args) {
        SpringApplication.run(BankAccountServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!bankService.haveBank()) {
            String bic = "CREDFRPPXXX";
            try {
                BicUtil.validate(bic);
                bankService.createBank(Bank.builder().bankCode(22041L)
                        .countryCode("FR")
                        .bankName("CreditRama")
                        .bic(bic)
                        .build());
            } catch (BicFormatException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
