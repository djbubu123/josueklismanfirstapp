package com.example.josueklisman.Models;

public class Product {
    private String idproduct;
    private String name;
    private Double price;

    public Product() {
    }

    public String getIdproduct() {
        return idproduct;
    }

    public void setIdproduct(String idproduct) {
        this.idproduct = idproduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "idproduct='" + idproduct + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
