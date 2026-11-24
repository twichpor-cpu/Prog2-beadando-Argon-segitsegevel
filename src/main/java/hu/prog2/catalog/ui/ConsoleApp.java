package hu.prog2.catalog.ui;

import hu.prog2.catalog.domain.ElectronicProduct;
import hu.prog2.catalog.domain.FoodProduct;
import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;
import hu.prog2.catalog.persistence.FileStorage;
import hu.prog2.catalog.service.ProductCatalog;
import hu.prog2.catalog.strategy.NameSortStrategy;
import hu.prog2.catalog.strategy.PriceSortStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleApp {
    private static final String FILE_NAME = "data/catalog.csv";

    public static void main(String[] args) {
        ProductCatalog catalog = new ProductCatalog();
        FileStorage storage = new FileStorage();

        try {
            File f = new File(FILE_NAME);
            if (f.exists()) {
                ArrayList<Product> loaded = storage.loadFromFile(FILE_NAME);
                for (Product p : loaded) {
                    catalog.addProduct(p);
                }
                System.out.println("Adatok betoltve fajlbol (" + loaded.size() + " db).");
            } else {
                catalog.addProduct(new ElectronicProduct(1, "Laptop", 300000, 5, 24));
                catalog.addProduct(new ElectronicProduct(2, "Eger", 8000, 20, 12));
                catalog.addProduct(new FoodProduct(3, "Alma", 500, 100, 14));
                catalog.addProduct(new FoodProduct(4, "Kave", 3500, 30, 180));
                f.getParentFile().mkdirs();
                storage.saveToFile(catalog.getProducts(), FILE_NAME);
                System.out.println("Alap adatok letrehozva es elmentve.");
            }
        } catch (Exception e) {
            System.out.println("Hiba: " + e.getMessage());
        }

        if (args.length > 0 && args[0].equals("--demo")) {
            runSimpleDemo(catalog, storage);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Listazas ar szerint (PriceSortStrategy)");
            System.out.println("2. Listazas nev szerint (NameSortStrategy)");
            System.out.println("3. Kereses ID alapjan");
            System.out.println("4. Uj elektronikai termek hozzaadasa");
            System.out.println("5. Teljes raktarkeszlet erteke");
            System.out.println("0. Mentes es kilepes");
            System.out.print("Valasztas: ");

            if (!scanner.hasNextLine()) {
                break;
            }
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                catalog.setSortStrategy(new PriceSortStrategy());
                catalog.sortProducts();
                for (Product p : catalog.getProducts()) {
                    System.out.println(p.getDescription());
                }
            } else if (choice.equals("2")) {
                catalog.setSortStrategy(new NameSortStrategy());
                catalog.sortProducts();
                for (Product p : catalog.getProducts()) {
                    System.out.println(p.getDescription());
                }
            } else if (choice.equals("3")) {
                System.out.print("Add meg az ID-t: ");
                int id = Integer.parseInt(scanner.nextLine());
                Product found = catalog.findProduct(id);
                if (found != null) {
                    System.out.println("Talalat: " + found.getDescription());
                } else {
                    System.out.println("Nincs ilyen termek.");
                }
            } else if (choice.equals("4")) {
                try {
                    System.out.print("ID: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Nev: ");
                    String name = scanner.nextLine();
                    System.out.print("Ar: ");
                    double price = Double.parseDouble(scanner.nextLine());
                    System.out.print("Keszlet: ");
                    int stock = Integer.parseInt(scanner.nextLine());
                    System.out.print("Garancia (ho): ");
                    int warranty = Integer.parseInt(scanner.nextLine());

                    catalog.addProduct(new ElectronicProduct(id, name, price, stock, warranty));
                    System.out.println("Termek hozzaadva!");
                } catch (InvalidProductException e) {
                    System.out.println("Hiba: " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Szamot adj meg!");
                }
            } else if (choice.equals("5")) {
                System.out.println("Osszertek: " + catalog.calculateTotalValue() + " Ft");
            } else if (choice.equals("0")) {
                try {
                    storage.saveToFile(catalog.getProducts(), FILE_NAME);
                    System.out.println("Elmentve. Kilepes...");
                } catch (Exception e) {
                    System.out.println("Mentesi hiba: " + e.getMessage());
                }
                running = false;
            }
        }
    }

    private static void runSimpleDemo(ProductCatalog catalog, FileStorage storage) {
        System.out.println("\n--- Rendezve ar szerint (PriceSortStrategy) ---");
        catalog.setSortStrategy(new PriceSortStrategy());
        catalog.sortProducts();
        for (Product p : catalog.getProducts()) {
            System.out.println(p.getDescription());
        }

        System.out.println("\n--- Rendezve nev szerint (NameSortStrategy) ---");
        catalog.setSortStrategy(new NameSortStrategy());
        catalog.sortProducts();
        for (Product p : catalog.getProducts()) {
            System.out.println(p.getDescription());
        }

        System.out.println("\nKereses ID=1: " + catalog.findProduct(1));
        System.out.println("Kereses Nev='Alma': " + catalog.findProduct("Alma"));
        System.out.println("Teljes keszlet ertek: " + catalog.calculateTotalValue() + " Ft");

        try {
            storage.saveToFile(catalog.getProducts(), FILE_NAME);
        } catch (Exception e) {
            System.out.println("Hiba: " + e.getMessage());
        }
    }
}
