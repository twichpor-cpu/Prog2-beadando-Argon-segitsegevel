package hu.prog2.catalog.domain;

public abstract class Product implements Comparable<Product> {
    private static int productCount = 0;

    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(int id, String name, double price, int stock) throws InvalidProductException {
        if (id <= 0) {
            throw new InvalidProductException("Hibas ID!");
        }
        if (name == null || name.isEmpty()) {
            throw new InvalidProductException("A nev nem lehet ures!");
        }
        if (price < 0 || stock < 0) {
            throw new InvalidProductException("Az ar es a keszlet nem lehet negativ!");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        productCount++;
    }

    public static int getProductCount() {
        return productCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public int compareTo(Product other) {
        return Double.compare(this.price, other.price);
    }

    public abstract String getDescription();

    @Override
    public String toString() {
        return getDescription();
    }
}
