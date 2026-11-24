package hu.prog2.catalog;

import hu.prog2.catalog.domain.ElectronicProduct;
import hu.prog2.catalog.domain.FoodProduct;
import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;
import hu.prog2.catalog.service.ProductCatalog;
import hu.prog2.catalog.strategy.NameSortStrategy;
import hu.prog2.catalog.strategy.PriceSortStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductCatalogTest {

    @Test
    public void testAddAndFindProduct() throws InvalidProductException {
        ProductCatalog catalog = new ProductCatalog();
        Product p1 = new ElectronicProduct(1, "Laptop", 300000, 2, 24);
        Product p2 = new FoodProduct(2, "Alma", 500, 10, 7);

        catalog.addProduct(p1);
        catalog.addProduct(p2);

        assertEquals(p1, catalog.findProduct(1));
        assertEquals(p2, catalog.findProduct("Alma"));
        assertEquals(605000.0, catalog.calculateTotalValue());
    }

    @Test
    public void testInvalidProductThrowsException() {
        assertThrows(InvalidProductException.class, () -> {
            new ElectronicProduct(1, "Hibas", -100, 5, 12);
        });
    }

    @Test
    public void testSortingStrategies() throws InvalidProductException {
        ProductCatalog catalog = new ProductCatalog();
        catalog.addProduct(new ElectronicProduct(1, "Laptop", 300000, 2, 24));
        catalog.addProduct(new FoodProduct(2, "Alma", 500, 10, 7));

        catalog.setSortStrategy(new PriceSortStrategy());
        catalog.sortProducts();
        assertEquals("Alma", catalog.getProducts().get(0).getName());

        catalog.setSortStrategy(new NameSortStrategy());
        catalog.sortProducts();
        assertEquals("Alma", catalog.getProducts().get(0).getName());
    }
}
