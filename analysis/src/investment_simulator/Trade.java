package investment_simulator;

import java.time.LocalDate;

public class Trade {
    public enum Type { BUY, SELL }

    private String ticker;
    private LocalDate date;
    private double price;
    private Type type;

    public Trade(String ticker, LocalDate date, double price, Type type) {
        this.ticker = ticker;
        this.date = date;
        this.price = price;
        this.type = type;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
