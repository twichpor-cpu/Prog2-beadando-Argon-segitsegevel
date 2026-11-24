package hu.prog2.catalog.strategy;

import hu.prog2.catalog.domain.Product;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Konkrét Strategy implementáció, amely a termékeket ár szerint csökkenő
 * sorrendbe rendezi lambda-kifejezés és {@link Comparator} segítségével.
 */
public class PriceDescendingSortStrategy implements ProductSortStrategy {
    @Override
    public String getStrategyName() {
        return "Ár szerint csökkenő (Lambda Comparator)";
    }

    @Override
    public List<Product> sort(List<Product> products) {
        List<Product> copy = new ArrayList<>(products);
        copy.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        return copy;
    }
}
