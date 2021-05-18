package demo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class Comida implements Serializable {
    private String name;
    private double price;
    private List<String> domain = new LinkedList<>();
    private int discount = 0;

    public Comida(String name, double price, List<String> domain) {
        this.name = name;
        this.price = price;
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getDomain(){
        return domain;
    }

    public void increaseDiscount(){
        this.discount = this.discount + 30;
        System.out.println("Prato : " + getName() + " está a venda. Valor original R$ " + getPrice()
                + " Novo valor R$ " + (this.price - (price * (0.01 * this.discount))));
        this.price = (this.price - (this.price * (0.01 * this.discount)));
    }

    public boolean isDiscount() {
        return discount > 0;
    }
}