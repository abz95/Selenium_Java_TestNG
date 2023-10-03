package models;

public class Products {
    private String name;
    private String variant;
    private double price;

    public Products(String name, String variant, double price) {
        this.name = name;
        this.variant = variant;
        this.price = price;
    }

    // Getter and Setter for Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for Variant
    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    // Getter and Setter for Price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}