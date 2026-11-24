package hu.prog2.catalog.strategy;

import hu.prog2.catalog.domain.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Konkrét Strategy implementáció, amely a termékeket ár szerint növekvő
 * (természetes, {@link Comparable}) sorrendbe rendezi.
 */
public class PriceAscendingSortStrategy implements ProductSortStrategy {
    @Override
    public String getStrategyName() {
        return "Ár szerint növekvő (Comparable)";
    }

    @Override
    public List<Product> sort(List<Product> products) {
        List<Product> copy = new ArrayList<>(products);
        Collections.sort(copy);
        return copy;
    }
}
