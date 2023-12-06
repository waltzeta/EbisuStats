package com.ebisustats.BBDD;

public class Product {

    private String name;
    private int precioCosto;
    private int precioVenta;
    private boolean disponibilidad;
    private int stock;

    //Constructor Vacio
    public Product(){}

    // Constructor sin el atributo de disponibilidad
    public Product(String name, int precioCosto, int precioVenta, int stock) {
        this.name = name;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.disponibilidad = true;  // Valor predeterminado
    }

    // Constructor completo
    public Product(String name, int precioCosto, int precioVenta, boolean disponibilidad, int stock) {
        this.name = name;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.disponibilidad = disponibilidad;
    }

    //Getters

    public String getName() {
        return name;
    }

    public int getPrecioCosto() {
        return precioCosto;
    }

    public int getPrecioVenta() {
        return precioVenta;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public int getStock() {
        return stock;
    }

    //Setters

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public void setPrecioVenta(int precioVenta) {
        this.precioVenta = precioVenta;
    }

    public void setPrecioCosto(int precioCosto) {
        this.precioCosto = precioCosto;
    }


    public void setName(String name) {
        this.name = name;
    }
}
