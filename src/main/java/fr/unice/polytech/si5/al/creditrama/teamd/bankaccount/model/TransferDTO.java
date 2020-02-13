package fr.unice.polytech.si5.al.creditrama.teamd.bankaccount.model;

public class TransferDTO {
    private String uuid;
    private String sourceIban;
    private String destIban;
    private double amount;

    public TransferDTO(String uuid, String sourceIban, String destIban, double amount) {
        this.uuid = uuid;
        this.sourceIban = sourceIban;
        this.destIban = destIban;
        this.amount = amount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSourceIban() {
        return sourceIban;
    }

    public void setSourceIban(String sourceIban) {
        this.sourceIban = sourceIban;
    }

    public String getDestIban() {
        return destIban;
    }

    public void setDestIban(String destIban) {
        this.destIban = destIban;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
