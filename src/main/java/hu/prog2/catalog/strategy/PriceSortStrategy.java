package hu.prog2.catalog.strategy;

import hu.prog2.catalog.domain.Product;
import java.util.Collections;
import java.util.List;

public class PriceSortStrategy implements ProductSortStrategy {
    @Override
    public void sort(List<Product> products) {
        Collections.sort(products);
    }
}
