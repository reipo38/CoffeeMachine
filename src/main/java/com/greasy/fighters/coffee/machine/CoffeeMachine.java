package com.greasy.fighters.coffee.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.greasy.fighters.Main;
import com.greasy.fighters.enums.Nominals;

public class CoffeeMachine {

    /*** Клас CoffeeMachine
     * Тук се съдържа логиката за взаимодействие с клиента. Тук се обработват и приготвят поръчаните кафета и се съдържа логика
     * за вкарване на пари от клиента и връщане на ресто
     * поле coffees - списък, съдържащ всички кафета в машината. id на едно кафе отговаря на индекса от този списък.
     * поле controlPanel - клас, който съдържа всичката администраторка логика. Нужен е тук за взаимодействие с вътрешните показатели на кафе машината
     *                      Напр. да се правят проверки дали са налични всички съставки на едно кафе в нужните количества. Да се добавят пари към общия брой изкарани.
     *                      Да се прави справка какви монети са налични и т.н.
     * поле coins - масив съдържащ номиналите на монетите (в стотинки. Напр. 2 лева са 200 стотинки)
     * поле sugarNeeded - количеството захар, която ще се използва за едно кафе. Определя се от потребителя
     * поле insertedFunds - съдържа парите вкарани в кафе машината до момента
     */

    private ArrayList<Coffee> coffees;
    private final ControlPanel controlPanel;

    private int sugarSelected;

    private final HashMap<String, Integer> insertedCoins;
    private int insertedFunds;

    public CoffeeMachine() {
        insertedCoins = new HashMap<>();
        coffees = new ArrayList<>();
        controlPanel = new ControlPanel(this);
    }

    // Метод за добавяне на ново кафе
    public void addNewCoffee(Coffee coffee) {
        coffees.add(coffee);
        controlPanel.getDataHandler().saveCoffees(coffees);
    }

    // Метод за изтриване на кафе
    public void deleteCoffee(Coffee coffee) {
        coffees.remove(coffee);
        controlPanel.getDataHandler().saveCoffees(coffees);
    }

    // Метод за промяна на количеството захар. Потребителят само решава дали ще увеличава или намалява количеството захар.
    // Той не може да определя колко захар да добави, това количество се определя от controlPanel
    public void changeSugarQuantity(boolean increment) {
        if (increment) {
            if (sugarSelected != controlPanel.getSugarMax()) sugarSelected += controlPanel.getSugarChangeBy();
        } else {
            if (sugarSelected != 0) sugarSelected -= controlPanel.getSugarChangeBy();
        }
    }

    // Добавяне на пари
    public void insertCoin(Nominals nominal) {
        insertedFunds += nominal.toInt();
        insertedCoins.put(nominal.toString(), insertedCoins.getOrDefault(nominal.toString(), 0) + 1);
    }

    public void buyCoffee(int id) {
        Coffee coffee = findCoffeeById(id);
        if (validateCoffeePurchase(coffee) && coffee != null) {
            updateInventory(coffee);
            prepareCoffee(coffee, calculateChange(coffee)); // Извиква се метода, който принтира информация за покупката на кафето и върнатото ресто
            insertedFunds = 0;                              // insertedFunds става нула, защото след покупка автоматично се връща рестото
            updateDailyStatistics(coffee);
        }
    }

    // Проверка дали е възможно да се приготви избраното кафе
    private boolean validateCoffeePurchase(Coffee coffee) {
        if (!controlPanel.ingredientsAvailable(coffee)) {   // Проверка дали са налични нужните съставки
            displayOutputMassage("Sorry, this coffee is currently not available.");
            return false;
        }
        if (!hasSufficientFunds(coffee)) {  // Проверка дали са вкарани достатъчно пари
            displayOutputMassage("Not enough money.");
            return false;
        }
        return true;
    }

    // Метод, който принтира информация за кафето и върнатото ресто
    private void prepareCoffee(Coffee coffee, String change) {
        displayOutputMassage(formatCoffeeMessage(coffee, change));
    }

    // Форматира изходното съобщение, като изписва всички съставки на кафето и върнатото ресто
    private String formatCoffeeMessage(Coffee coffee, String changeString) {
        return String.format("Your %s is ready. (%dg coffee, %dml water, %dg sugar%s)%nDropping %s.",
                coffee.getName(), coffee.getCoffeeNeeded(), coffee.getWaterNeeded(), sugarSelected, (coffee.hasMilk() ? String.format(", %dml milk)", controlPanel.getMilkNeeded()) : ")"),
                changeString);
    }

    // Връщане на вкараните в машината пари
    public void returnCoins() {
        if (insertedFunds > 0) {
            displayOutputMassage(calculateCoinsAmount(insertedFunds));
            insertedFunds = 0;
        }
    }

    private String calculateChange(Coffee coffee) {
        return calculateCoinsAmount(insertedFunds - coffee.getPrice());
    }

    private String calculateCoinsAmount(int changeAmount) {
        HashMap<String, Integer> coins = new HashMap<>();
        for (Nominals nominal : Nominals.values()) {            // Обхожда се масива, съдържащ номиналите на монетите. За всяка итерация i e един номинал
            if (isCoinAvailable(nominal)) {                     // Проверка дали кафе машината има налични монети от този номинал. Ако няма, итерацията продължава със следващия номинал.
                int coinAmount = changeAmount / nominal.toInt();      // Изчисляване на броя монети от този номинал, които са нужни
                if (coinAmount != 0) {                          // Проверка дали са нужни 0 монети. Ако не, към резултатния низ се конкатенира номинала монети и броя
                    coins.put(nominal.toString(), coinAmount);
                }
                changeAmount %= nominal.toInt();                      // amount става равно на остатъка при деление на използвания номинал
            }
        }
        return formatChange(coins);
    }

    // Помощен метод за calculateCoinsAmount(), който форматира номинал и брой от него в един низ.
    private String formatChange(HashMap<String, Integer> coins) {
        if (coins.isEmpty()) return "0";
        return coins.entrySet().stream()
                .map(entry -> String.format("  %s coins:%d ; ", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining());
    }

    // Метод за проверка дали са вкарани достатъчно пари за поръчка на кафето
    private boolean hasSufficientFunds(Coffee coffee) {
        return insertedFunds >= coffee.getPrice();
    }

    private void displayOutputMassage(String message) {
        Main.visualManager.setOutputText(message);
    }

    private Coffee findCoffeeById(int id) {
        if (id < 0 || id >= coffees.size()) {
            return null; // Индексът е извън допустимия диапазон
        }
        return coffees.get(id);
    }

    private void updateDailyStatistics(Coffee coffee) {
        controlPanel.addCoffeeToDailyStatistics(coffee);

    }

    private boolean isCoinAvailable(Nominals nominal) {
        return controlPanel.getCoins().get(nominal.toString()) != 0;
    }

    private void updateInventory(Coffee coffee) {
        controlPanel.updateInventory(coffee, insertedCoins); // Промените в наличните съставки се отбелязват
    }

    // get метод за парите, вкарани в кафемашината
    public int getInsertedFunds() {
        return insertedFunds;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    // Метод, който връща масив от имената на всички кафета, които се продават в машината, заедно с техните цени
    public String[] getCoffeeNames() {
        return coffees.stream()
                .map(Coffee::getName)
                .toArray(String[]::new);
    }

    // Метод за показване на процента захар. Тя не се изписва като грамаж, а като процент от максималното количество захар (което се определя от controlPanel)
    public int getSugarPercentage() {
        return (sugarSelected * 100) / controlPanel.getSugarMax();
    }

    // Метод за търсене на кафе по неговото име
    public Coffee getCoffeeByName(String name) {
        return coffees.stream()
                .filter(coffee -> coffee.getName().equals(name))
                .findFirst() // Този метод се използва за да се върне само един обект. В противен слу
                .orElse(null);
    }

    public ArrayList<Coffee> getCoffees() {
        return coffees;
    }

    public int getSugarSelected() {
        return sugarSelected;
    }

    public void setCoffees(ArrayList<Coffee> coffees) {
        this.coffees = coffees;
    }
}