package model;
import java.util.List;
import java.util.Objects;

public class Product {
    private String name;
    private String variant;
    private double price;

    public Product(String name, String variant, double price) {
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

    public static boolean compareProductListsWithoutVariant(List<Product> listA, List<Product> listB) {
        if(listA.size()!=listB.size())
            return false;

        for (int i = 0; i < listA.size(); i++) {
            Product productA = listA.get(i);
            Product productB = listB.get(i);

            if (!productA.equalsWithoutVariant(productB)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 &&
                Objects.equals(name, product.name) &&
                Objects.equals(variant, product.variant);
    }

    public boolean equalsWithoutVariant(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 &&
                Objects.equals(name, product.name);
    }

    public static int countProductsByPrice(List<Product> products, double price) {
        int count = 0;
        for (Product product : products) {
            if (product.getPrice() == price) {
                count++;
            }
        }
        return count;
    }
}