package hu.prog2.catalog.service;

import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;
import hu.prog2.catalog.strategy.PriceSortStrategy;
import hu.prog2.catalog.strategy.ProductSortStrategy;

import java.util.ArrayList;
import java.util.List;

public class ProductCatalog {
    private ArrayList<Product> products = new ArrayList<>();
    private ProductSortStrategy sortStrategy = new PriceSortStrategy();

    public void setSortStrategy(ProductSortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    public void addProduct(Product product) throws InvalidProductException {
        if (findProduct(product.getId()) != null) {
            throw new InvalidProductException("Mar letezik ilyen ID: " + product.getId());
        }
        products.add(product);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void sortProducts() {
        sortStrategy.sort(products);
    }

    // Tulterhelt kereses 1: ID alapjan
    public Product findProduct(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Tulterhelt kereses 2: Nev alapjan
    public Product findProduct(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public double calculateTotalValue() {
        double sum = 0;
        for (Product p : products) {
            sum += p.getPrice() * p.getStock();
        }
        return sum;
    }
}
