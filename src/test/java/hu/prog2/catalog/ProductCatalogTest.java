package hu.prog2.catalog;

import hu.prog2.catalog.domain.ElectronicProduct;
import hu.prog2.catalog.domain.FoodProduct;
import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;
import hu.prog2.catalog.domain.ProductCategory;
import hu.prog2.catalog.persistence.BinaryCatalogRepository;
import hu.prog2.catalog.persistence.CatalogRepository;
import hu.prog2.catalog.persistence.CsvCatalogRepository;
import hu.prog2.catalog.service.ProductCatalog;
import hu.prog2.catalog.strategy.NameSortStrategy;
import hu.prog2.catalog.strategy.PriceAscendingSortStrategy;
import hu.prog2.catalog.strategy.PriceDescendingSortStrategy;
import hu.prog2.catalog.strategy.StockSortStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Egységtesztek (JUnit 5) a Webáruház Termékkatalógus rendszerhez.
 * Lefedi az egységbezárást, öröklődést, polimorfizmust, kivételkezelést,
 * a Strategy tervezési mintát, a túlterhelt keresőmetódusokat és a perzisztenciát.
 */
class ProductCatalogTest {

    private ProductCatalog catalog;

    @BeforeEach
    void setUp() throws InvalidProductException {
        Product.resetCreatedProductCount();
        catalog = new ProductCatalog("Teszt Katalógus");
        catalog.loadDefaultSampleProducts();
    }

    @Test
    @DisplayName("1. Statikus példányszámláló és alapértelmezett 10 termék betöltése")
    void testDefaultCatalogSizeAndStaticCounter() {
        assertEquals(10, catalog.getProductCount(), "A katalógusnak 10 mintaterméket kell tartalmaznia.");
        assertEquals(10, Product.getCreatedProductCount(), "A statikus számlálónak 10-et kell mutatnia.");
    }

    @Test
    @DisplayName("2. Polimorfizmus: ElectronicProduct és FoodProduct getDescription() felülírása")
    void testPolymorphicGetDescription() throws InvalidProductException {
        Product laptop = new ElectronicProduct(301, "Dell XPS Laptop", 550000.0, 3, 24);
        Product honey = new FoodProduct(302, "Bio Virágméz", 2900.0, 15, 365, true);

        assertTrue(laptop.getDescription().contains("Garancia: 24 hó"));
        assertTrue(laptop.getDescription().contains("Elektronika"));
        assertTrue(honey.getDescription().contains("Szavatosság: 365 nap"));
        assertTrue(honey.getDescription().contains("Bio: Igen"));
    }

    @Test
    @DisplayName("3. Saját kivétel (InvalidProductException) dobása érvénytelen adatok esetén")
    void testValidationThrowsInvalidProductException() {
        assertThrows(InvalidProductException.class, () ->
                new ElectronicProduct(0, "Érvénytelen ID", 1000.0, 5, 12));
        assertThrows(InvalidProductException.class, () ->
                new ElectronicProduct(10, "   ", 1000.0, 5, 12));
        assertThrows(InvalidProductException.class, () ->
                new ElectronicProduct(11, "Negatív ár", -10.0, 5, 12));
        assertThrows(InvalidProductException.class, () ->
                new ElectronicProduct(12, "Negatív készlet", 1000.0, -1, 12));
        assertThrows(InvalidProductException.class, () ->
                new ElectronicProduct(13, "Negatív garancia", 1000.0, 5, -6));
        assertThrows(InvalidProductException.class, () ->
                new FoodProduct(14, "Nulla szavatosság", 500.0, 5, 0, false));
    }

    @Test
    @DisplayName("4. Duplikált azonosító tiltása a katalógusban")
    void testDuplicateProductIdThrowsException() {
        assertThrows(InvalidProductException.class, () ->
                catalog.addProduct(new ElectronicProduct(101, "Másik Laptop", 200000.0, 2, 12)));
    }

    @Test
    @DisplayName("5. Strategy minta: Ár szerinti növekvő és csökkenő rendezés")
    void testPriceSortingStrategies() {
        catalog.setSortStrategy(new PriceAscendingSortStrategy());
        List<Product> asc = catalog.getSortedProducts();
        assertEquals(201, asc.get(0).getId(), "A legolcsóbb terméknek a Bio Zabpehelynek (201) kell lennie.");
        assertEquals(101, asc.get(asc.size() - 1).getId(), "A legdrágábbnak az ASUS ROG Laptopnak (101) kell lennie.");

        catalog.setSortStrategy(new PriceDescendingSortStrategy());
        List<Product> desc = catalog.getSortedProducts();
        assertEquals(101, desc.get(0).getId(), "Csökkenő sorrendben az elsőnek a legdrágábbnak kell lennie.");
        assertEquals(201, desc.get(desc.size() - 1).getId(), "Csökkenő sorrendben az utolsónak a legolcsóbbnak kell lennie.");
    }

    @Test
    @DisplayName("6. Strategy minta: Név és raktárkészlet szerinti rendezés")
    void testNameAndStockSortingStrategies() {
        catalog.setSortStrategy(new NameSortStrategy());
        List<Product> byName = catalog.getSortedProducts();
        assertEquals("ASUS ROG Gamer Laptop", byName.get(0).getName());

        catalog.setSortStrategy(new StockSortStrategy());
        List<Product> byStock = catalog.getSortedProducts();
        assertEquals(5, byStock.get(0).getStock());
        assertEquals(120, byStock.get(byStock.size() - 1).getStock());
    }

    @Test
    @DisplayName("7. Túlterhelt keresés: findProduct(int id) és findProduct(String name)")
    void testOverloadedFindProduct() {
        Product byId = catalog.findProduct(105);
        assertNotNull(byId);
        assertEquals("Sony WH-1000XM5 Fejhallgató", byId.getName());

        Product byName = catalog.findProduct("akácméz");
        assertNotNull(byName);
        assertEquals(203, byName.getId());

        assertNull(catalog.findProduct(9999));
        assertNull(catalog.findProduct("NemLétezőTermék"));
    }

    @Test
    @DisplayName("8. Legdrágább termék és teljes raktárkészlet érték számítása")
    void testMostExpensiveAndTotalInventoryValue() {
        Product mostExpensive = catalog.getMostExpensiveProduct();
        assertNotNull(mostExpensive);
        assertEquals(101, mostExpensive.getId());
        assertEquals(489990.0, mostExpensive.getPrice(), 0.001);

        // 101: 489990*5 = 2449950
        // 102: 74990*14 = 1049860
        // 103: 39990*25 = 999750
        // 104: 42500*12 = 510000
        // 105: 129990*8 = 1039920
        // 201: 890*120 = 106800
        // 202: 7490*45 = 337050
        // 203: 3290*30 = 98700
        // 204: 1450*80 = 116000
        // 205: 1190*60 = 71400
        // Összesen: 6779430.0
        assertEquals(6779430.0, catalog.calculateTotalInventoryValue(), 0.01);
    }

    @Test
    @DisplayName("9. CSV és Bináris perzisztencia mentés és visszaolvasás")
    void testCsvAndBinaryPersistence(@TempDir Path tempDir) throws IOException, InvalidProductException {
        CatalogRepository csvRepo = new CsvCatalogRepository();
        Path csvFile = tempDir.resolve("test_catalog.csv");
        csvRepo.save(catalog.getProducts(), csvFile);
        assertTrue(Files.exists(csvFile));

        List<Product> loadedFromCsv = csvRepo.load(csvFile);
        assertEquals(10, loadedFromCsv.size());
        assertEquals(catalog.getProducts().get(0).getName(), loadedFromCsv.get(0).getName());

        CatalogRepository binRepo = new BinaryCatalogRepository();
        Path binFile = tempDir.resolve("test_catalog.bin");
        binRepo.save(catalog.getProducts(), binFile);
        assertTrue(Files.exists(binFile));

        List<Product> loadedFromBin = binRepo.load(binFile);
        assertEquals(10, loadedFromBin.size());
        assertEquals(catalog.getProducts().get(0).getId(), loadedFromBin.get(0).getId());
    }

    @Test
    @DisplayName("10. Kategória szerinti szűrés")
    void testFindByCategory() {
        List<Product> electronics = catalog.findByCategory(ProductCategory.ELECTRONICS);
        List<Product> foods = catalog.findByCategory(ProductCategory.FOOD);
        assertEquals(5, electronics.size());
        assertEquals(5, foods.size());
    }
}
