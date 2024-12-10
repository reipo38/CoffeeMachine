package com.greasy.fighters.coffee.machine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Coffee {
    
    private String name;
    private int price;
    private int coffeeNeeded;
    private boolean hasMilk; // * Направи го int, за да видим колко мляко е необходимо
    private int waterNeeded;
    private boolean alcoholNeeded;

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

    public boolean isAlcoholNeeded() {
        return alcoholNeeded;
    }
}
