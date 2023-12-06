package com.ebisustats.ui.stock;

public class ListElement {
    //Atributos
    private String name;
    private String stock;

    //Contructor
    public ListElement(String name, String stock) {
        this.name = name;
        this.stock = stock;
    }

    //Getter n Setters de los atributos

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
