package com.greasy.fighters.coffee.machine;

import java.util.ArrayList;

import com.greasy.fighters.Main;
import com.greasy.fighters.data.handler.DataHandler;
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

    private final ArrayList<Coffee> coffees;
    private final ControlPanel controlPanel = new ControlPanel(this);
    private final int[] coins = new int[]{200, 100, 50, 20, 10, 5};

    private int sugarNeeded;

    private int insertedMoney;


    // Конструктор. Приема като параметър списъка с наличните кафета.
    public CoffeeMachine(ArrayList<Coffee> coffees) {
        this.coffees = coffees;
    }

    // Метод за добавяне на ново кафе
    public void addNewCoffee(Coffee coffee) {
        coffees.add(coffee);
        DataHandler.saveCoffees(coffees);
    }

    // Метод за изтриване на кафе
    public void deleteCoffee(Coffee coffee) {
        coffees.remove(coffee);
        DataHandler.saveCoffees(coffees);
    }

    /*
        Метод за промяна на количеството захар. Потребителят само решава дали ще увеличава или намалява количеството захар.
        Той не може да определя колко захар да добави, това количество се определя от controlPanel
     */
    public void changeSugarQuantity(boolean increment) {
        if (increment) {
            if (sugarNeeded != controlPanel.getSugarMax()) sugarNeeded += controlPanel.getSugarChangeBy();
        } else {
            if (sugarNeeded != 0) sugarNeeded -= controlPanel.getSugarChangeBy();
        }
    }

    // Добавяне на пари
    public void insertMoney(int amount) {
        insertedMoney += amount;
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
                processCoffeePurchase(coffee); // Кафето се приготва
                Statistics.addCoffeeToDailyStatistic(coffee); // Покупката се отбелязва в статистиките
            } else { // Ако парите не са достатъчно се изписва надпис.
                Main.visualManager.setOutputText("Not enough money.");
            }
        } else { // Ако съставките не са налични се изписва надпис.
            Main.visualManager.setOutputText("Sorry, this coffee is currently not available.");
        }
    }

    // Връщане на вкараните в машината пари.
    public void returnMoney() {
        if (insertedMoney > 0) {
            Main.visualManager.setOutputText(calculateChange(insertedMoney));
            insertedMoney = 0;
        }
    }

    // Приготвяне на кафето
    private void processCoffeePurchase(Coffee coffee) {
        controlPanel.updateInternalValues(coffee, sugarNeeded); // Промените в наличните съставки се отбелязват
        prepareCoffee(coffee); // Извиква се метода, който принтира информация за покупката на кафето и върнатото ресто
        insertedMoney = 0; // insertedMoney става нула, защото след покупка автоматично се връща рестото
    }

    // Метод, който принтира информация за кафето и върнатото ресто
    private void prepareCoffee(Coffee coffee) {
        Main.visualManager.setOutputText(
                String.format("Your %s is ready. (%dg coffee, %dml water, %dg sugar%s%nDropping %s.",
                        coffee.getName(), coffee.getCoffeeNeeded(), coffee.getWaterNeeded(), sugarNeeded, (coffee.hasMilk() ? String.format(", %dml milk)", controlPanel.getMilkNeeded()) : ")"), calculateChange(insertedMoney - coffee.getPrice())));
    }

    // Метод за изчисляване на рестото. Имплементация на Евристичния алгоритъм за номиналите на монетите (Вж. Задачи::Тема_07 и Лекции::Тема_05 от УАСД (3-ти курс))
    private String calculateChange(int amountMoney) {
        StringBuilder result = new StringBuilder(); // Създаване на StringBuilder. Използва се за по-оптимална конкатенация на низове
        for (int i : coins) { // Обхожда се масива, съдържащ номиналите на монетите. За всяка итерация i e един номинал
            if (controlPanel.getAmountOfCoins().get(i) != 0) { // Проверка дали кафе машината има налични монети от този номинал. Ако няма, итерацията продължава със следващия номинал.
                int coinAmount = amountMoney / i; // Изчисляване на броя монети от този номинал, които са нужни
                if (coinAmount != 0) { // Проверка дали са нужни 0 монети. Ако не, към резултатния низ се конкатенира номинала монети и броя
                    result.append(String.format(moneyAsString(i, coinAmount)));
                }
                amountMoney %= i; // amountMoney става равно на остатъка при деление на използвания номинал
            }
        }
        return result.toString(); // Връща се цял низ съдържащ всеки използван номинал, и броя монети от него.


    }

    // Помощен метод за calculateChange(), който форматира номинал и брой от него в един низ.
    private String moneyAsString(int coinNominal, int moneyAmount) {
        // Ако ниминалът е сто или по-голям (тоест 1 или 2 лева), той се дели на сто преди да се принтира за да може резултатът да се изкарва в левове, вместо в стотинки
        return String.format("  %s%d coins:%d ; ", (coinNominal >= 100 ? coinNominal / 100 : "0."), coinNominal, moneyAmount);
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
                .map(Coffee::getName) // Extract the name from each Coffee object
                .toArray(String[]::new); // Collect the results into an array

    }

    public Integer[] getCoffeePrices() {
        return coffees.stream()
                .map(Coffee::getPrice) // Extract the name from each Coffee object
                .toArray(Integer[]::new); // Collect the results into an array

    }

    public int[] getCoins() {
        return coins;
    }

    // Метод за показване на процента захар. Тя не се изписва като грамаж, а като процент от максималното количество захар (което се определя от controlPanel)
    public int getSugarPercentage() {
        return (sugarNeeded * 100) / controlPanel.getSugarMax();
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
}