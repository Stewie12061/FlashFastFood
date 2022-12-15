package com.example.flashfastfood.Data;

public class Order {
    String orderItemQuantity, orderPrice ,orderTotalPrice, orderDate, orderTime, orderLocation, orderShippingCharges, orderStatus, orderPayment;

    public Order(){}

    public Order(String orderItemQuantity, String orderPrice, String orderTotalPrice, String orderDate, String orderTime, String orderLocation, String orderShippingCharges, String orderStatus, String orderPayment) {
        this.orderItemQuantity = orderItemQuantity;
        this.orderPrice = orderPrice;
        this.orderTotalPrice = orderTotalPrice;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderLocation = orderLocation;
        this.orderShippingCharges = orderShippingCharges;
        this.orderStatus = orderStatus;
        this.orderPayment = orderPayment;
    }
}
