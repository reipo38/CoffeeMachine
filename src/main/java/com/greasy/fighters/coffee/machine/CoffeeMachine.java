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
    private ControlPanel controlPanel;

    private int sugarSelected;

    private final HashMap<String, Integer> insertedCoins;
    private int insertedFunds;

    public CoffeeMachine() {
        insertedCoins = new HashMap<>();
        coffees = new ArrayList<>();
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
        controlPanel.addCoin(nominal);
        insertedCoins.put(nominal.toString(), insertedCoins.getOrDefault(nominal.toString(), 0) + 1);
    }

    // Връщане на вкараните в машината пари
    public void returnCoins() {
        if (insertedFunds > 0) {
            HashMap<String, Integer> change = calculateChangeCoinsAmount(insertedFunds);
            displayOutputMassage(formatChange(change));
            resetInsertedCoins(change);
        }
    }

    public void buyCoffee(int id) {
        Coffee coffee = coffees.get(id);
        if (validateCoffeePurchase(coffee)) {
            HashMap<String, Integer> change = calculateChangeCoinsAmount(insertedFunds - coffee.getPrice());
            processPurchase(coffee, change);
            updateDailyStatistics(coffee);
        }
    }

    // Метод за добавяне на ново кафе
    protected void addCoffee(Coffee coffee) {
        coffees.add(coffee);
    }

    // Метод за изтриване на кафе
    protected void removeCoffee(Coffee coffee) {
        coffees.remove(coffee);
    }

    private void processPurchase(Coffee coffee, HashMap<String, Integer> change) {
        updateInventory(coffee);
        prepareCoffee(coffee, formatChange(change));
        resetInsertedCoins(change);
    }

    // Проверка дали е възможно да се приготви избраното кафе
    private boolean validateCoffeePurchase(Coffee coffee) {
        if (!controlPanel.ingredientsAvailable(coffee)) return insufficientIngredientsError();
        if (!hasSufficientFunds(coffee))                return insufficientMoneyError();
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

    private HashMap<String, Integer> calculateChangeCoinsAmount(int changeAmount) {
        HashMap<String, Integer> coins = new HashMap<>();
        for (Nominals nominal : Nominals.values()) {
            if (changeAmount == 0) break;   // Ако ресто е нула, няма нужда да продължаваме
            if (!isCoinAvailable(nominal)) continue;    // Проверка дали има налични монети от този номинал
            int coinsToReturn = calculateCoinsForNominal(changeAmount, nominal);
            if (coinsToReturn > 0) coins.put(nominal.toString(), coinsToReturn);   // Добавяне на монетите към резултата, само ако са повече от 0
            changeAmount -= coinsToReturn * nominal.toInt();    // Намаляване на стойността на ресто, което трябва да бъде върнато
        }
        return coins;
    }

    private int calculateCoinsForNominal(int changeAmount, Nominals nominal) {
        int requiredCoins = changeAmount / nominal.toInt();    // Изчисляване на броя монети, които са нужни
        int availableCoins = getAvailableCoins(nominal);   // Получаване на наличните монети от този номинал
        return Math.min(requiredCoins, availableCoins);      // Определяне колко монети да се върнат (минимум от нужните и наличните)
    }

    private int getAvailableCoins(Nominals nominal) {
        return controlPanel.getCoins().getOrDefault(nominal.toString(), 0);
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

    private void updateDailyStatistics(Coffee coffee) {
        controlPanel.addCoffeeToDailyStatistics(coffee);
    }

    private boolean isCoinAvailable(Nominals nominal) {
        return controlPanel.getCoins().get(nominal.toString()) != 0;
    }

    private void updateInventory(Coffee coffee) {
        controlPanel.updateInventory(coffee, insertedCoins); // Промените в наличните съставки се отбелязват
    }

    private void resetInsertedCoins(HashMap<String, Integer> insertedCoins) {
        controlPanel.removeCoins(insertedCoins);
        insertedFunds = 0;
    }

    // get метод за парите, вкарани в кафемашината
    public int getInsertedFunds() {
        return insertedFunds;
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
                .findFirst() // Този метод се използва за да се върне само един обект
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

    public String getMoneySymbol() {
        return controlPanel.getMoneySymbol();
    }

    protected void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    private boolean insufficientIngredientsError() {
        displayOutputMassage("Sorry, this coffee is currently not available.");
        return false;
    }

    private boolean insufficientMoneyError() {
        displayOutputMassage("Not enough money.");
        return false;
    }
}