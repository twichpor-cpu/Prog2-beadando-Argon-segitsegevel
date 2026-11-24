package hu.prog2.catalog.strategy;

import hu.prog2.catalog.domain.Product;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Konkrét Strategy implementáció, amely a termékeket név szerint
 * ábécésorrendbe rendezi {@link Comparator} használatával.
 */
public class NameSortStrategy implements ProductSortStrategy {
    private static final Comparator<Product> NAME_COMPARATOR =
            Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                      .thenComparingInt(Product::getId);

    @Override
    public String getStrategyName() {
        return "Név szerint ábécésorrendben (Comparator<Product>)";
    }

    @Override
    public List<Product> sort(List<Product> products) {
        List<Product> copy = new ArrayList<>(products);
        copy.sort(NAME_COMPARATOR);
        return copy;
    }
}
