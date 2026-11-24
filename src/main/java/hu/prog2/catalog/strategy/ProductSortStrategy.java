package hu.prog2.catalog.strategy;

import hu.prog2.catalog.domain.Product;
import java.util.List;

public interface ProductSortStrategy {
    void sort(List<Product> products);
}
