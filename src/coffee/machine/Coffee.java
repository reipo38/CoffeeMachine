package coffee.machine; // ! ПАКЕТИТЕ СЕ ПИШАТ С МАЛИ БУКВИ

public class Coffee {
    private String name;
    private int price;
    private int coffeeNeeded;
    private boolean hasMilk;
    private int waterNeeded;

    public Coffee(String name, int price, int needAmountOfCoffee, boolean hasMilk, int waterNeeded) {
        this.name = name;
        this.price = price;
        this.coffeeNeeded = needAmountOfCoffee;
        this.hasMilk = hasMilk;
        this.waterNeeded = waterNeeded;
    }

    public String getName() {
        return name;
    }

    public int getCoffeeNeeded() {
        return coffeeNeeded;
    }

    public int getPrice() {
        return price;
    }

    public int getWaterNeeded() {
        return waterNeeded;
    }

    public boolean hasMilk() {
        return hasMilk;
    }
}
