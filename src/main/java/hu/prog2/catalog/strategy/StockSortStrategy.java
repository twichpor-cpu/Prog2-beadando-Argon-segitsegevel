package hu.prog2.catalog.strategy;

import hu.prog2.catalog.domain.Product;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Konkrét Strategy implementáció, amely a termékeket raktárkészlet szerint
 * növekvő sorrendbe rendezi {@link Comparator} segítségével.
 */
public class StockSortStrategy implements ProductSortStrategy {
    private static final Comparator<Product> STOCK_COMPARATOR =
            Comparator.comparingInt(Product::getStock)
                      .thenComparingInt(Product::getId);

    @Override
    public String getStrategyName() {
        return "Raktárkészlet szerint növekvő (Comparator<Product>)";
    }

    @Override
    public List<Product> sort(List<Product> products) {
        List<Product> copy = new ArrayList<>(products);
        copy.sort(STOCK_COMPARATOR);
        return copy;
    }
}
