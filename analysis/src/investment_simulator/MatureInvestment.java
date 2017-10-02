package investment_simulator;

public class MatureInvestment {
    public final Trade buy;
    public final Trade sell;

    public MatureInvestment(Trade buy, Trade sell) {
        this.buy = buy;
        this.sell = sell;
    }

    public String getTicker() {
        return buy.getTicker();
    }

    public double calculateReturn() {
        return 100 * (sell.getPrice() - buy.getPrice()) / buy.getPrice();
    }

    public long calculateLengthInDays() {
        return sell.getDate().toEpochDay() - buy.getDate().toEpochDay();
    }
}
