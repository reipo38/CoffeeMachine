package com.greasy.fighters.coffee.machine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Coffee {
    /*** Клас Coffee
     * поле име - името на кафето. Иползва се само за зареждане на кафето в потребителския интерфейс. Вътрешно кафетата се управяват по id (вж. клас CoffeeMachine)
     * поле цена - Целочислено число, представляващо цената на кафето в стотинки
     * поле coffeeNeeded - количеството чисто кафе, нужно за приготвяне на това кафе
     * поле hasMilk - дали кафето се нуждае от мляко. От Coffee зависи само дали ще има или няма мляко. Количеството мляко за всички кафета е еднакво и то се определя от ControlPanel
     * поле waterNeeded - количеството нужна вода
     */
    private String name;
    private int price;
    private int coffeeNeeded;
    private boolean hasMilk;
    private int waterNeeded;

    // Конструктора на класа.
    // Всяко поле в този клас се връзва с поле от json файл (вж. /data/coffee.json и com/greasy/fighters/data/handler/DataHandler)
    @JsonCreator
    public Coffee(
            @JsonProperty("name") String name,
            @JsonProperty("price") int price,
            @JsonProperty("coffeeNeeded") int needAmountOfCoffee,
            @JsonProperty("hasMilk") boolean hasMilk,
            @JsonProperty("waterNeeded") int waterNeeded
    ) {
        this.name = name;
        this.price = price;
        this.coffeeNeeded = needAmountOfCoffee;
        this.hasMilk = hasMilk;
        this.waterNeeded = waterNeeded;
    }

    /*
        get методи за всяко едно поле. Използват се в CoffeeMachine за обработка на поръчки
        няма set методи, понеже няма да се извършват промени по самите кафета.
        При нужда кафето се трие и се създава ново от администраторския панел (Вж. AdminInterface)
     */

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

    @Override
    public String toString() {
        return String.format("Name: %s%nPrice: %d%nCoffee: %d%nWater: %d%nMilk: %s", name, price, coffeeNeeded, waterNeeded, hasMilk);
    }
}
