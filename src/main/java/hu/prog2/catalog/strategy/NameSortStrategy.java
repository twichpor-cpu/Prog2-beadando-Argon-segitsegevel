package hu.prog2.catalog.strategy;

import hu.prog2.catalog.domain.Product;
import java.util.List;

public class NameSortStrategy implements ProductSortStrategy {
    @Override
    public void sort(List<Product> products) {
        products.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
    }
}
