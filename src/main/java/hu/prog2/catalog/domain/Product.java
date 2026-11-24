package hu.prog2.catalog.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Absztrakt ősosztály a webáruházban található termékek reprezentálására.
 * Megvalósítja a {@link Describable}, {@link Comparable} és {@link Serializable} interfészeket.
 * A természetes rendezési sorrendet az ár (növekvő) határozza meg.
 */
public abstract class Product implements Describable, Comparable<Product>, Serializable {
    private static final long serialVersionUID = 1L;

    /** Statikus számláló a létrehozott termékpéldányok számának nyilvántartására. */
    private static int createdProductCount = 0;

    private final int id;
    private String name;
    private double price;
    private int stock;
    private final ProductCategory category;

    /**
     * Létrehoz egy új terméket és ellenőrzi az üzleti szabályokat.
     *
     * @param id       egyedi pozitív azonosító
     * @param name     a termék neve (nem lehet üres)
     * @param price    a termék egységára (nem lehet negatív)
     * @param stock    a raktárkészlet darabszáma (nem lehet negatív)
     * @param category a termék kategóriája (nem lehet null)
     * @throws InvalidProductException ha bármely bemeneti adat érvénytelen
     */
    protected Product(int id, String name, double price, int stock, ProductCategory category)
            throws InvalidProductException {
        validateBasicFields(id, name, price, stock, category);
        this.id = id;
        this.name = name.trim();
        this.price = price;
        this.stock = stock;
        this.category = category;
        createdProductCount++;
    }

    /**
     * Final ellenőrző metódus, amely garantálja az alapvető adatok érvényességét.
     * A leszármazott osztályok nem írhatják felül, így az alapinvariánsok nem megkerülhetők.
     */
    public final void validateBasicFields(int id, String name, double price, int stock, ProductCategory category)
            throws InvalidProductException {
        if (id <= 0) {
            throw new InvalidProductException("A termék azonosítója csak pozitív egész szám lehet (kapott: " + id + ").");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductException("A termék neve nem lehet üres.");
        }
        if (price < 0) {
            throw new InvalidProductException("A termék ára nem lehet negatív (kapott: " + price + ").");
        }
        if (stock < 0) {
            throw new InvalidProductException("A raktárkészlet nem lehet negatív (kapott: " + stock + ").");
        }
        if (category == null) {
            throw new InvalidProductException("A termék kategóriája nem lehet null.");
        }
    }

    public static int getCreatedProductCount() {
        return createdProductCount;
    }

    public static void resetCreatedProductCount() {
        createdProductCount = 0;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidProductException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductException("A termék neve nem lehet üres.");
        }
        this.name = name.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws InvalidProductException {
        if (price < 0) {
            throw new InvalidProductException("A termék ára nem lehet negatív.");
        }
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) throws InvalidProductException {
        if (stock < 0) {
            throw new InvalidProductException("A raktárkészlet nem lehet negatív.");
        }
        this.stock = stock;
    }

    public ProductCategory getCategory() {
        return category;
    }

    /**
     * Kiszámítja az adott termék teljes készletértékét (ár * raktárkészlet).
     *
     * @return a készlet összértéke
     */
    public double getTotalStockValue() {
        return price * stock;
    }

    /**
     * Természetes rendezési sorrend ár szerint növekvő sorrendben
     * (azonos ár esetén azonosító szerint).
     */
    @Override
    public int compareTo(Product other) {
        int priceComparison = Double.compare(this.price, other.price);
        if (priceComparison != 0) {
            return priceComparison;
        }
        return Integer.compare(this.id, other.id);
    }

    /**
     * Polimorf leíró metódus, amelyet a konkrét leszármazott osztályok valósítanak meg.
     */
    @Override
    public abstract String getDescription();

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
