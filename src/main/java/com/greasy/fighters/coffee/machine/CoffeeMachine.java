package com.greasy.fighters.coffee.machine;

import java.util.ArrayList;
import java.util.HashMap;

import com.greasy.fighters.Main;
import com.greasy.fighters.data.handler.DataHandler;
import com.greasy.fighters.enums.Nominals;
import com.greasy.fighters.statistic.Statistics;

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
     * поле insertedMoney - съдържа парите вкарани в кафе машината до момента
     */

    private ArrayList<Coffee> coffees;
    private final ControlPanel controlPanel = new ControlPanel(this);

    private int sugarSelected;

    private final HashMap<String, Integer> insertedCoins;
    private int insertedMoney;

    public CoffeeMachine() {
        insertedCoins = new HashMap<>();
    }

    // Конструктор. Приема като параметър списъка с наличните кафета.
    public CoffeeMachine(ArrayList<Coffee> coffees) {
        this.coffees = coffees;
        insertedCoins = new HashMap<>();
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

    /*
        Метод за промяна на количеството захар. Потребителят само решава дали ще увеличава или намалява количеството захар.
        Той не може да определя колко захар да добави, това количество се определя от controlPanel
     */
    public void changeSugarQuantity(boolean increment) {
        if (increment) {
            if (sugarSelected != controlPanel.getSugarMax()) sugarSelected += controlPanel.getSugarChangeBy();
        } else {
            if (sugarSelected != 0) sugarSelected -= controlPanel.getSugarChangeBy();
        }
    }

    // Добавяне на пари
    public void insertMoney(Nominals nominal) {
        insertedMoney += nominal.toInt();
        insertedCoins.put(nominal.toString(), insertedCoins.getOrDefault(nominal.toString(), 0) + 1);
    }

    // Логика за купуване на кафе
    public void buyCoffee(int id) {
        /*
            Търси се кафе по id (id = индекс от coffees)
            Ако кафето се поръчва през GUI coffee никога няма да бъде достъпен индекс, неналичен в списъка.
            Това може да се случи само ако методът е извикан директно. Тогава програмата крашва.
         */
        Coffee coffee = coffees.get(id);
        if (controlPanel.ingredientsAvailable(coffee)) { // Проверка дали нужните съставки са налични
            if (hasSufficientMoney(coffee)) { // Проверка дали са вкарани достатъчно пари
                prepareAndChargeForCoffee(coffee); // Кафето се приготва
                controlPanel.getStatistics().addCoffeeToDailyStatistic(coffee); // Покупката се отбелязва в статистиките
            } else { // Ако парите не са достатъчно се изписва надпис.
                Main.visualManager.setOutputText("Not enough money.");
            }
        } else { // Ако съставките не са налични се изписва надпис.
            Main.visualManager.setOutputText("Sorry, this coffee is currently not available.");
        }
    }

    // Връщане на вкараните в машината пари.
    public void returnMoney() {
        HashMap<String, Integer> coins = getChangeHashMap(insertedMoney);
        if (insertedMoney > 0) {
            Main.visualManager.setOutputText(getCoinsAmountString(coins));
            insertedMoney = 0;
        }
    }

    // Приготвяне на кафето
    private void prepareAndChargeForCoffee(Coffee coffee) {
        controlPanel.updateInternalValues(coffee, insertedCoins); // Промените в наличните съставки се отбелязват
        HashMap<String, Integer> change = getChangeHashMap(insertedMoney - coffee.getPrice());
        controlPanel.dropCoins(change);
        prepareCoffee(coffee, getCoinsAmountString(change)); // Извиква се метода, който принтира информация за покупката на кафето и върнатото ресто
        insertedMoney = 0; // insertedMoney става нула, защото след покупка автоматично се връща рестото
    }

    // Метод, който принтира информация за кафето и върнатото ресто
    private void prepareCoffee(Coffee coffee, String change) {
        Main.visualManager.setOutputText(
                String.format("Your %s is ready. (%dg coffee, %dml water, %dg sugar%s%nDropping %s.",
                        coffee.getName(), coffee.getCoffeeNeeded(), coffee.getWaterNeeded(), sugarSelected, (coffee.hasMilk() ? String.format(", %dml milk)", controlPanel.getMilkNeeded()) : ")"), change));
    }

    private HashMap<String, Integer> getChangeHashMap(int amount) {
        HashMap<String, Integer> coins = new HashMap<>();
        for (Nominals nominal : Nominals.values()) { // Обхожда се масива, съдържащ номиналите на монетите. За всяка итерация i e един номинал
            if (controlPanel.getCoins().get(nominal.toString()) != 0) { // Проверка дали кафе машината има налични монети от този номинал. Ако няма, итерацията продължава със следващия номинал.
                int coinAmount = amount / nominal.toInt(); // Изчисляване на броя монети от този номинал, които са нужни
                if (coinAmount != 0) { // Проверка дали са нужни 0 монети. Ако не, към резултатния низ се конкатенира номинала монети и броя
                    coins.put(nominal.toString(), coinAmount);
                }
                amount %= nominal.toInt(); // amountMoney става равно на остатъка при деление на използвания номинал
            }
        }
        return coins;
    }

    // Помощен метод за calculateChange(), който форматира номинал и брой от него в един низ.
    private String getCoinsAmountString(HashMap<String, Integer> coins) {
        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<String, Integer> entry : coins.entrySet()) {
            result.append(String.format("  %s coins:%d ; ", entry.getKey(), entry.getValue()));
        }
        String resultStr = result.toString();
        return resultStr.isEmpty() ? "0" : resultStr;
    }

    // Метод за проверка дали са вкарани достатъчно пари за поръчка на кафето
    private boolean hasSufficientMoney(Coffee coffee) {
        return insertedMoney >= coffee.getPrice();
    }

    // get метод за парите, вкарани в кафемашината
    public int getInsertedMoney() {
        return insertedMoney;
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
        for (Coffee coffee : coffees) {
            if (coffee.getName().equals(name)) {
                return coffee;
            }
        }
        return null;
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