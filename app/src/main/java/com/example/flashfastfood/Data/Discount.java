package com.example.flashfastfood.Data;

public class Discount {
    String name, des, type, expDay, condition;
    int value;

    public Discount(){}

    public Discount(String name, String des, String type, String expDay, int value,String condition) {
        this.name = name;
        this.des = des;
        this.type = type;
        this.expDay = expDay;
        this.value = value;
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpDay() {
        return expDay;
    }

    public void setExpDay(String expDay) {
        this.expDay = expDay;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
