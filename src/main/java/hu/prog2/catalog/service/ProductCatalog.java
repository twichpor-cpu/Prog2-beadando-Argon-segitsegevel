package hu.prog2.catalog.service;

import hu.prog2.catalog.domain.ElectronicProduct;
import hu.prog2.catalog.domain.FoodProduct;
import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;
import hu.prog2.catalog.domain.ProductCategory;
import hu.prog2.catalog.strategy.PriceAscendingSortStrategy;
import hu.prog2.catalog.strategy.ProductSortStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A webáruház termékkatalógusát kezelő szolgáltatás osztály.
 * Aggregációs kapcsolatban áll a {@link Product} objektumokkal (1..* multiplicitás),
 * és a Strategy tervezési minta Context szerepét tölti be a {@link ProductSortStrategy} révén.
 */
public class ProductCatalog {
    private final String catalogName;
    private final List<Product> products;
    private ProductSortStrategy sortStrategy;

    public ProductCatalog(String catalogName) {
        if (catalogName == null || catalogName.trim().isEmpty()) {
            throw new IllegalArgumentException("A katalógus neve nem lehet üres.");
        }
        this.catalogName = catalogName.trim();
        this.products = new ArrayList<>();
        this.sortStrategy = new PriceAscendingSortStrategy();
    }

    public String getCatalogName() {
        return catalogName;
    }

    public ProductSortStrategy getSortStrategy() {
        return sortStrategy;
    }

    /**
     * Futásidőben lecseréli az aktuális rendezési stratégiát (Strategy minta).
     *
     * @param sortStrategy az új rendezési stratégia
     */
    public void setSortStrategy(ProductSortStrategy sortStrategy) {
        this.sortStrategy = Objects.requireNonNull(sortStrategy, "A rendezési stratégia nem lehet null.");
    }

    /**
     * Hozzáad egy új terméket a katalógushoz, ellenőrizve az azonosító egyediségét.
     *
     * @param product a hozzáadandó termék
     * @throws InvalidProductException ha a termék null vagy az azonosító már foglalt
     */
    public void addProduct(Product product) throws InvalidProductException {
        if (product == null) {
            throw new InvalidProductException("Null termék nem adható a katalógushoz.");
        }
        if (findProduct(product.getId()) != null) {
            throw new InvalidProductException(
                    "Már létezik termék a következő azonosítóval: " + product.getId());
        }
        products.add(product);
    }

    /**
     * Eltávolít egy terméket azonosító alapján.
     *
     * @param id a törlendő termék azonosítója
     * @return true, ha történt törlés, egyébként false
     */
    public boolean removeProduct(int id) {
        return products.removeIf(p -> p.getId() == id);
    }

    /**
     * Túlterhelt keresőmetódus (Method Overloading 1.): termék keresése egyedi azonosító alapján.
     *
     * @param id a keresett termék azonosítója
     * @return a megtalált {@link Product}, vagy null ha nem található
     */
    public Product findProduct(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    /**
     * Túlterhelt keresőmetódus (Method Overloading 2.): termék keresése név alapján
     * (kis- és nagybetű-független részleges vagy teljes egyezés).
     *
     * @param name a keresett termék neve vagy névrészlete
     * @return az első illeszkedő {@link Product}, vagy null ha nem található
     */
    public Product findProduct(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String query = name.trim().toLowerCase();
        for (Product product : products) {
            if (product.getName().toLowerCase().equals(query)) {
                return product;
            }
        }
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(query)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Visszaadja az adott kategóriába tartozó termékeket.
     *
     * @param category a szűrendő kategória
     * @return a kategóriába tartozó termékek listája
     */
    public List<Product> findByCategory(ProductCategory category) {
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategory() == category) {
                result.add(product);
            }
        }
        return result;
    }

    /**
     * Visszaadja a katalógusban szereplő összes termék módosíthatatlan nézetét.
     *
     * @return a termékek listája
     */
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    /**
     * Lecseréli a katalógus teljes tartalmát (pl. fájlból történő betöltéskor).
     *
     * @param loadedProducts a betöltött termékek listája
     * @throws InvalidProductException ha érvénytelen vagy duplikált termék szerepel a listában
     */
    public void replaceAllProducts(List<Product> loadedProducts) throws InvalidProductException {
        products.clear();
        if (loadedProducts != null) {
            for (Product product : loadedProducts) {
                addProduct(product);
            }
        }
    }

    public int getProductCount() {
        return products.size();
    }

    /**
     * Visszaadja a termékeket az aktuálisan beállított {@link ProductSortStrategy} szerint rendezve.
     *
     * @return rendezett terméklista
     */
    public List<Product> getSortedProducts() {
        return sortStrategy.sort(products);
    }

    /**
     * Megkeresi a legdrágább terméket a katalógusban.
     *
     * @return a legmagasabb árú {@link Product}, vagy null ha a katalógus üres
     */
    public Product getMostExpensiveProduct() {
        if (products.isEmpty()) {
            return null;
        }
        return Collections.max(products);
    }

    /**
     * Kiszámítja a teljes raktárkészlet értékét (ár * készletmennyiség, az összes termékre összegezve).
     *
     * @return a teljes raktárkészlet összértéke
     */
    public double calculateTotalInventoryValue() {
        double total = 0.0;
        for (Product product : products) {
            total += product.getTotalStockValue();
        }
        return total;
    }

    /**
     * Feltölti a katalógust legalább 10 alapértelmezett mintatermékkel
     * (elektronikai és élelmiszer termékekkel vegyesen).
     *
     * @throws InvalidProductException ha a mintatermékek adatai érvénytelenek
     */
    public void loadDefaultSampleProducts() throws InvalidProductException {
        products.clear();
        addProduct(new ElectronicProduct(101, "ASUS ROG Gamer Laptop", 489990.0, 5, 36));
        addProduct(new ElectronicProduct(102, "Samsung 27\" IPS Monitor", 74990.0, 14, 24));
        addProduct(new ElectronicProduct(103, "Logitech MX Master 3S Egér", 39990.0, 25, 24));
        addProduct(new ElectronicProduct(104, "Keychron K8 Mechanikus Billentyűzet", 42500.0, 12, 12));
        addProduct(new ElectronicProduct(105, "Sony WH-1000XM5 Fejhallgató", 129990.0, 8, 24));
        addProduct(new FoodProduct(201, "Bio Zabpehely (500g)", 890.0, 120, 365, true));
        addProduct(new FoodProduct(202, "Prémium Arabica Szemes Kávé (1kg)", 7490.0, 45, 540, false));
        addProduct(new FoodProduct(203, "Házi Akácméz (500g)", 3290.0, 30, 730, true));
        addProduct(new FoodProduct(204, "Belga Étcsokoládé 85%", 1450.0, 80, 270, false));
        addProduct(new FoodProduct(205, "Bio Mandulatej (1L)", 1190.0, 60, 180, true));
    }
}
