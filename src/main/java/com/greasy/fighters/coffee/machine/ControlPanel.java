package com.greasy.fighters.coffee.machine;

import java.util.HashMap;
import com.greasy.fighters.data.handler.DataHandler;

public class ControlPanel {
    /***
     * Клас ControlPanel. Отговаря за администраторската логика в кафемашината. Управлява се количествата на всички съставки,
     * общият брой пари, максималното количество захар, използваното количество мляко и т.н.
     *
     * Полета:
     * moneySymbol - лесен начин за промяна на символа за парите (например "лв", "bgn")
     * coffeeMachine - кафемашината, която този контролен панел управлява
     * consumables - съдържа количеството пари и всички съставки, налични в кафемашината (кофе, вода, мляко, захар и т.н.)
     * milkNeeded - количеството мляко, което се използва за приготвянето на едно кафе с мляко
     * sugarMax - максималното количество захар, което може да бъде избрано от потребителя
     * sugarChangeBy - количество захар, с което избраното количество се променя. То винаги е една пета от максималното количество захар
     */
    private String moneySymbol = "bgn"; // Символ за парите (може да бъде променен)

    private CoffeeMachine coffeeMachine; // Инстанция на кафемашината, която управляваме

    private HashMap<String, Integer> consumables; // Речник със съставките в кафемашината

    // Речник, който пази количеството монети от всеки номинал. Логиката е временна. Ще бъде променена скоро
    private HashMap<Integer, Integer> amountOfCoins = createAmountOfCoins();

    private HashMap<Integer, Integer> createAmountOfCoins() {
        // HashMap<Integer, Integer> amountOfCoins = new HashMap<>();
        // Инициализация на количествата монети от различни номинали
        // amountOfCoins.put(200, 0); // 2 лева
        // amountOfCoins.put(100, 0); // 1 лев
        // amountOfCoins.put(50, 10); // 50 стотинки
        // amountOfCoins.put(20, 10); // 20 стотинки
        // amountOfCoins.put(10, 10); // 10 стотинки
        // amountOfCoins.put(5, 10);  // 5 стотинки
        return DataHandler.loadMoney();
    }

    private int milkNeeded; // Количеството мляко, което е нужно за всяко кафе с мляко

    private int sugarChangeBy; // Количеството захар, с което се променя избора за захар
    private int sugarMax; // Максималното количество захар, което може да бъде избрано

    // Изброими стойности за различните видове съставки в кафемашината
    public enum Consumable {
        Money,   // Парите в кафемашината
        Coffee,  // Кафето (боб, смляно кафе)
        Water,   // Водата
        Milk,    // Млякото
        Sugar    // Захарта
    }

    // Конструктор на ControlPanel, инициализиращ кафемашината и зареждащ съставките
    public ControlPanel(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
        this.consumables = DataHandler.loadConsumables(); // Зареждаме съставките от външно хранилище (например JSON файл)
    }

    // Метод за добавяне на ново кафе към кафемашината
    public void addNewCoffee(String coffeeType, int price, int needAmountOfCoffee, boolean hasMilk, int waterNeeded) {
        coffeeMachine.addNewCoffee(new Coffee(coffeeType, price, needAmountOfCoffee, hasMilk, waterNeeded));
    }

    // Метод за изтриване на кафе по име от кафемашината
    public void deleteCoffeeByName(String coffeeName) {
        Coffee coffee = coffeeMachine.getCoffeeByName(coffeeName);
        coffeeMachine.deleteCoffee(coffee);
    }

    // Метод за проверка дали всички нужни съставки за приготвянето на кафе са налични
    public boolean ingredientsAvailable(Coffee coffee) {
        // Проверяваме дали имаме достатъчно кафе, вода и евентуално мляко
        return consumables.get("Coffee") >= coffee.getCoffeeNeeded() &&
                ((coffee.hasMilk() && consumables.get("Milk") >= milkNeeded) || !coffee.hasMilk());
    }

    // Метод за актуализиране на вътрешните стойности на съставките, когато се приготвя кафе
    public void updateInternalValues(Coffee coffee, int sugar) {
        // Обновяваме съставките (намаляваме количеството на използваните съставки)
        consumables.put("Money", consumables.get("Money") + coffee.getPrice()); // Добавяме стойността на кафето
        consumables.put("Coffee", consumables.get("Coffee") - coffee.getCoffeeNeeded()); // Използваме количество кафе
        if (coffee.hasMilk()) {
            consumables.put("Milk", consumables.get("Milk") - milkNeeded); // Използваме мляко, ако кафето го съдържа
        }
        consumables.put("Water", consumables.get("Water") - coffee.getWaterNeeded()); // Използваме вода
        consumables.put("Sugar", consumables.get("Sugar") - sugar); // Използваме захар
    }

    // Метод за получаване на символа за парите
    public String getMoneySymbol() {
        return moneySymbol;
    }

    // Метод за задаване на максималното количество захар
    public int getSugarMax() {
        return sugarMax;
    }

    // set метод за задаване на максималното количество захар и автоматично изчисляване на sugarChangeBy
    public void setSugarMax(int sugarMax) {
        this.sugarMax = sugarMax;
        sugarChangeBy = sugarMax / 5; // Захарта се променя с една пета от максималното количество
    }

    // Метод за получаване на количеството, с което се променя захарта
    public int getSugarChangeBy() {
        return sugarChangeBy;
    }

    // Метод за получаване на количеството мляко, необходимо за приготвяне на кафе с мляко
    public int getMilkNeeded() {
        return milkNeeded;
    }

    // Метод за задаване на количеството мляко, необходимо за приготвяне на кафе с мляко
    public void setMilkNeeded(int milkNeeded) {
        this.milkNeeded = milkNeeded;
    }

    // Метод за получаване на имената на всички съставки
    public String[] getConsumablesNames() {
        return new String[]{"Money", "Coffee", "Water", "Milk", "Sugar"};
    }

    // Метод за получаване на количеството на дадена съставка
    public int getConsumableValue(Consumable consumable) {
        try {
            return consumables.get(consumable.toString()); // Връщаме стойността на съставката от речника
        } catch (Exception e) {
            System.out.println("Consumable " + consumable.toString() + " is not found!"); // Ако не е намерена съставката
            throw new RuntimeException(e);
        }
    }

    // Метод за промяна на стойността на съставка
    public void changeConsumableValue(String consumable, int amount) {
        consumables.put(consumable, consumables.get(consumable) + amount); // Променяме количеството на съставката
        DataHandler.saveConsumables(consumables);
    }

    // Метод за получаване на монетите по номинали
    public HashMap<Integer, Integer> getAmountOfCoins() {
        return amountOfCoins;
    }

    // Метод за получаване на имената на наличните кафета
    public String[] getCoffeeNames() {
        return coffeeMachine.getCoffeeNames();
    }
}
