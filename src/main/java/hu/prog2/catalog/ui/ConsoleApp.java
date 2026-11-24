package hu.prog2.catalog.ui;

import hu.prog2.catalog.domain.ElectronicProduct;
import hu.prog2.catalog.domain.FoodProduct;
import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;
import hu.prog2.catalog.persistence.BinaryCatalogRepository;
import hu.prog2.catalog.persistence.CatalogRepository;
import hu.prog2.catalog.persistence.CsvCatalogRepository;
import hu.prog2.catalog.service.ProductCatalog;
import hu.prog2.catalog.strategy.NameSortStrategy;
import hu.prog2.catalog.strategy.PriceAscendingSortStrategy;
import hu.prog2.catalog.strategy.PriceDescendingSortStrategy;
import hu.prog2.catalog.strategy.ProductSortStrategy;
import hu.prog2.catalog.strategy.StockSortStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Konzolos felhasználói felület (CLI) a Webáruház Termékkatalógus rendszerhez.
 * Támogatja az interaktív menüvezérelt működést, valamint a {@code --demo}
 * kapcsolóval történő automatikus bemutató futtatást.
 */
public class ConsoleApp {
    private static final Path DEFAULT_CSV_PATH = Path.of("data", "catalog.csv");
    private static final Path DEFAULT_BIN_PATH = Path.of("data", "catalog.bin");

    private final ProductCatalog catalog;
    private final CatalogRepository csvRepository;
    private final CatalogRepository binaryRepository;

    public ConsoleApp() {
        this.catalog = new ProductCatalog("Prog2 Webáruház Katalógus");
        this.csvRepository = new CsvCatalogRepository();
        this.binaryRepository = new BinaryCatalogRepository();
    }

    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp();
        app.initializeCatalog();

        for (String arg : args) {
            if ("--demo".equalsIgnoreCase(arg)) {
                app.runDemo();
                return;
            }
        }

        if (System.console() == null && !hasStandardInput()) {
            app.runDemo();
            return;
        }

        app.runInteractiveMenu();
    }

    private static boolean hasStandardInput() {
        try {
            return System.in.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Inicializálja a katalógust: ha létezik mentett CSV fájl, betölti onnan,
     * különben feltölti a 10 alapértelmezett mintatermékkel és elmenti.
     */
    public void initializeCatalog() {
        try {
            if (Files.exists(DEFAULT_CSV_PATH)) {
                List<Product> loaded = csvRepository.load(DEFAULT_CSV_PATH);
                catalog.replaceAllProducts(loaded);
                System.out.println("[INFO] Katalógus sikeresen betöltve innen: " + DEFAULT_CSV_PATH
                        + " (" + catalog.getProductCount() + " termék).");
            } else {
                catalog.loadDefaultSampleProducts();
                csvRepository.save(catalog.getProducts(), DEFAULT_CSV_PATH);
                System.out.println("[INFO] Alapértelmezett 10 mintatermék betöltve és elmentve ide: " + DEFAULT_CSV_PATH);
            }
        } catch (IOException | InvalidProductException e) {
            System.err.println("[FIGYELMEZTETÉS] Hiba az inicializálás során: " + e.getMessage());
            try {
                catalog.loadDefaultSampleProducts();
            } catch (InvalidProductException ex) {
                System.err.println("[KRITIKUS HIBA] Nem sikerült a mintatermékek betöltése: " + ex.getMessage());
            }
        }
    }

    /**
     * Automatikus demonstrációs mód, amely bemutatja az összes követelményt:
     * öröklődés, polimorfizmus, Strategy tervezési minta szerinti rendezések,
     * túlterhelt keresés, statisztikák, kivételkezelés és perzisztencia.
     */
    public void runDemo() {
        System.out.println("=========================================================================");
        System.out.println("   " + catalog.getCatalogName() + " - AUTOMATIKUS BEMUTATÓ (--demo)");
        System.out.println("=========================================================================");

        printProducts("1. Ár szerint növekvő rendezés (Comparable / PriceAscendingSortStrategy)",
                new PriceAscendingSortStrategy());
        printProducts("2. Ár szerint csökkenő rendezés (Lambda / PriceDescendingSortStrategy)",
                new PriceDescendingSortStrategy());
        printProducts("3. Név szerinti rendezés (Comparator / NameSortStrategy)",
                new NameSortStrategy());
        printProducts("4. Raktárkészlet szerinti rendezés (Comparator / StockSortStrategy)",
                new StockSortStrategy());

        System.out.println("\n--- 5. Statisztikák és összesítések ---");
        Product mostExpensive = catalog.getMostExpensiveProduct();
        System.out.println("Legdrágább termék: " + (mostExpensive != null ? mostExpensive.getDescription() : "Nincs"));
        System.out.printf(Locale.US, "Teljes raktárkészlet értéke: %.2f Ft%n", catalog.calculateTotalInventoryValue());
        System.out.println("Létrehozott termékpéldányok száma (statikus számláló): " + Product.getCreatedProductCount() + " db");

        System.out.println("\n--- 6. Túlterhelt keresés (findProduct(int) és findProduct(String)) ---");
        Product byId = catalog.findProduct(103);
        System.out.println("Keresés ID=103 alapján: " + (byId != null ? byId.getDescription() : "Nem található"));
        Product byName = catalog.findProduct("Kávé");
        System.out.println("Keresés 'Kávé' névrészlet alapján: " + (byName != null ? byName.getDescription() : "Nem található"));

        System.out.println("\n--- 7. Saját kivétel (InvalidProductException) kezelésének bemutatása ---");
        try {
            new ElectronicProduct(999, "Hibás árú termék", -5000.0, 10, 12);
        } catch (InvalidProductException e) {
            System.out.println("Elkapott üzleti szabály kivétel: " + e.getMessage());
        }

        System.out.println("\n--- 8. Perzisztencia (CSV és Bináris szerializáció try-with-resources-szal) ---");
        try {
            csvRepository.save(catalog.getProducts(), DEFAULT_CSV_PATH);
            binaryRepository.save(catalog.getProducts(), DEFAULT_BIN_PATH);
            System.out.println("Sikeres mentés CSV fájlba (" + DEFAULT_CSV_PATH + ") és bináris fájlba (" + DEFAULT_BIN_PATH + ").");
        } catch (IOException e) {
            System.err.println("Mentési hiba: " + e.getMessage());
        }
        System.out.println("=========================================================================");
    }

    private void printProducts(String title, ProductSortStrategy strategy) {
        catalog.setSortStrategy(strategy);
        System.out.println("\n--- " + title + " ---");
        System.out.println("Aktív stratégia: " + catalog.getSortStrategy().getStrategyName());
        for (Product product : catalog.getSortedProducts()) {
            System.out.println("  " + product.getDescription());
        }
    }

    /**
     * Interaktív konzolos menü futtatása.
     */
    public void runInteractiveMenu() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> printProducts("Termékek listája (Ár szerint növekvő)", new PriceAscendingSortStrategy());
                    case "2" -> printProducts("Termékek listája (Ár szerint csökkenő)", new PriceDescendingSortStrategy());
                    case "3" -> printProducts("Termékek listája (Név szerint)", new NameSortStrategy());
                    case "4" -> printProducts("Termékek listája (Raktárkészlet szerint)", new StockSortStrategy());
                    case "5" -> handleSearchById(scanner);
                    case "6" -> handleSearchByName(scanner);
                    case "7" -> handleAddProduct(scanner);
                    case "8" -> handleStatistics();
                    case "9" -> handleSaveAndLoad(scanner);
                    case "0" -> {
                        saveQuietly();
                        System.out.println("Kilépés... Viszlát!");
                        running = false;
                    }
                    default -> System.out.println("Ismeretlen menüpont. Kérlek, 0 és 9 közötti számot adj meg!");
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\n==================== " + catalog.getCatalogName() + " ====================");
        System.out.println("1 - Listázás: Ár szerint növekvő (Comparable / Strategy)");
        System.out.println("2 - Listázás: Ár szerint csökkenő (Lambda Comparator / Strategy)");
        System.out.println("3 - Listázás: Név szerint (Comparator / Strategy)");
        System.out.println("4 - Listázás: Raktárkészlet szerint (Comparator / Strategy)");
        System.out.println("5 - Keresés azonosító alapján (findProduct(int id))");
        System.out.println("6 - Keresés név alapján (findProduct(String name))");
        System.out.println("7 - Új termék felvétele (Elektronika / Élelmiszer)");
        System.out.println("8 - Katalógus statisztikák (Legdrágább termék, Összérték, Példányszám)");
        System.out.println("9 - Mentés / Betöltés (CSV és Bináris szerializáció)");
        System.out.println("0 - Mentés és Kilépés");
        System.out.print("Válassz menüpontot: ");
    }

    private void handleSearchById(Scanner scanner) {
        System.out.print("Add meg a keresett termék azonosítóját (pl. 101): ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Product found = catalog.findProduct(id);
            if (found != null) {
                System.out.println("Találat: " + found.getDescription());
            } else {
                System.out.println("Nem található termék ezzel az azonosítóval: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("[HIBA] Érvénytelen egész szám formátum.");
        }
    }

    private void handleSearchByName(Scanner scanner) {
        System.out.print("Add meg a keresett termék nevét vagy névrészletét: ");
        String name = scanner.nextLine().trim();
        Product found = catalog.findProduct(name);
        if (found != null) {
            System.out.println("Találat: " + found.getDescription());
        } else {
            System.out.println("Nem található termék ezzel a névvel: " + name);
        }
    }

    private void handleAddProduct(Scanner scanner) {
        try {
            System.out.print("Terméktípus (1 = Elektronika, 2 = Élelmiszer): ");
            String type = scanner.nextLine().trim();
            System.out.print("Azonosító (pozitív egész): ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Név: ");
            String name = scanner.nextLine().trim();
            System.out.print("Ár (Ft): ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Raktárkészlet (db): ");
            int stock = Integer.parseInt(scanner.nextLine().trim());

            Product newProduct;
            if ("1".equals(type)) {
                System.out.print("Garanciaidő (hónap): ");
                int warranty = Integer.parseInt(scanner.nextLine().trim());
                newProduct = new ElectronicProduct(id, name, price, stock, warranty);
            } else if ("2".equals(type)) {
                System.out.print("Szavatossági idő (nap): ");
                int shelfLife = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Bio minősítésű? (i/n): ");
                boolean organic = scanner.nextLine().trim().equalsIgnoreCase("i");
                newProduct = new FoodProduct(id, name, price, stock, shelfLife, organic);
            } else {
                System.out.println("[HIBA] Érvénytelen terméktípus választás.");
                return;
            }

            catalog.addProduct(newProduct);
            System.out.println("[SIKER] Termék hozzáadva: " + newProduct.getDescription());
        } catch (NumberFormatException e) {
            System.out.println("[HIBA] Számformátum hiba a bevitel során: " + e.getMessage());
        } catch (InvalidProductException e) {
            System.out.println("[ÜZLETI SZABÁLY HIBA] " + e.getMessage());
        }
    }

    private void handleStatistics() {
        System.out.println("\n--- Katalógus Statisztikák ---");
        System.out.println("Termékek száma a katalógusban: " + catalog.getProductCount() + " db");
        System.out.println("Összes létrehozott Product példány (statikus számláló): " + Product.getCreatedProductCount() + " db");
        Product mostExpensive = catalog.getMostExpensiveProduct();
        System.out.println("Legdrágább termék: " + (mostExpensive != null ? mostExpensive.getDescription() : "Nincs"));
        System.out.printf(Locale.US, "Teljes raktárkészlet értéke: %.2f Ft%n", catalog.calculateTotalInventoryValue());
    }

    private void handleSaveAndLoad(Scanner scanner) {
        System.out.println("1 - Mentés CSV és Bináris fájlba");
        System.out.println("2 - Betöltés CSV fájlból");
        System.out.println("3 - Betöltés Bináris fájlból");
        System.out.println("4 - Alapértelmezett 10 mintatermék visszaállítása");
        System.out.print("Választás: ");
        String subChoice = scanner.nextLine().trim();
        try {
            switch (subChoice) {
                case "1" -> {
                    csvRepository.save(catalog.getProducts(), DEFAULT_CSV_PATH);
                    binaryRepository.save(catalog.getProducts(), DEFAULT_BIN_PATH);
                    System.out.println("[SIKER] Mentve ide: " + DEFAULT_CSV_PATH + " és " + DEFAULT_BIN_PATH);
                }
                case "2" -> {
                    catalog.replaceAllProducts(csvRepository.load(DEFAULT_CSV_PATH));
                    System.out.println("[SIKER] CSV fájlból betöltve " + catalog.getProductCount() + " termék.");
                }
                case "3" -> {
                    catalog.replaceAllProducts(binaryRepository.load(DEFAULT_BIN_PATH));
                    System.out.println("[SIKER] Bináris fájlból betöltve " + catalog.getProductCount() + " termék.");
                }
                case "4" -> {
                    catalog.loadDefaultSampleProducts();
                    System.out.println("[SIKER] Alapértelmezett 10 mintatermék visszaállítva.");
                }
                default -> System.out.println("Ismeretlen művelet.");
            }
        } catch (IOException | InvalidProductException e) {
            System.out.println("[HIBA] Fájlműveleti hiba: " + e.getMessage());
        }
    }

    private void saveQuietly() {
        try {
            csvRepository.save(catalog.getProducts(), DEFAULT_CSV_PATH);
            binaryRepository.save(catalog.getProducts(), DEFAULT_BIN_PATH);
        } catch (IOException e) {
            System.err.println("[FIGYELMEZTETÉS] Automatikus mentés sikertelen: " + e.getMessage());
        }
    }
}
