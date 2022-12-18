package com.example.flashfastfood.Data;

public class Order {
    String orderItemQuantity, orderTotalPrice, orderDate, orderTime, orderLocation, orderStatus, orderPayment;

    public Order(){}

    public Order(String orderItemQuantity, String orderTotalPrice, String orderDate, String orderTime, String orderLocation, String orderStatus, String orderPayment) {
        this.orderItemQuantity = orderItemQuantity;
        this.orderTotalPrice = orderTotalPrice;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderLocation = orderLocation;
        this.orderStatus = orderStatus;
        this.orderPayment = orderPayment;
    }

    public String getOrderItemQuantity() {
        return orderItemQuantity;
    }

    public void setOrderItemQuantity(String orderItemQuantity) {
        this.orderItemQuantity = orderItemQuantity;
    }

    public String getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(String orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderLocation() {
        return orderLocation;
    }

    public void setOrderLocation(String orderLocation) {
        this.orderLocation = orderLocation;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderPayment() {
        return orderPayment;
    }

    public void setOrderPayment(String orderPayment) {
        this.orderPayment = orderPayment;
    }
}
