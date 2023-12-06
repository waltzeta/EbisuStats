package com.ebisustats.BBDD;

import java.util.ArrayList;
import java.util.List;


public class Sale {
    private List<SaleItem> saleItems;
    private int totalCost;
    private int totalSalePrice;

    public Sale() {
        this.saleItems = new ArrayList<>();
        this.totalCost = 0;
        this.totalSalePrice = 0;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
        calculateTotals();
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getTotalSalePrice() {
        return totalSalePrice;
    }

    public void addSaleItem(Product product, int quantity) {
        // Verificar si el producto ya está en la venta
        for (SaleItem saleItem : saleItems) {
            if (saleItem.getProduct().equals(product)) {
                // El producto ya está en la venta, reemplazar la cantidad
                saleItem.setQuantity(quantity);
                calculateTotals();
                return;
            }
        }

        // Si el producto no estaba en la venta, agregarlo
        SaleItem saleItem = new SaleItem(product, quantity);
        saleItems.add(saleItem);
        calculateTotals();
    }

    private void calculateTotals() {
        totalCost = 0;
        totalSalePrice = 0;

        for (SaleItem saleItem : saleItems) {
            Product product = saleItem.getProduct();
            int quantity = saleItem.getQuantity();

            totalCost += product.getPrecioCosto() * quantity;
            totalSalePrice += product.getPrecioVenta() * quantity;
        }
    }

    public static class SaleItem {
        private Product product;
        private int quantity;

        public SaleItem() {
            // Constructor vacío requerido para Firebase
        }

        public SaleItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}



