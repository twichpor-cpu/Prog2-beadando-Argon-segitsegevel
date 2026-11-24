package hu.prog2.catalog.strategy;

import hu.prog2.catalog.domain.Product;
import java.util.List;

/**
 * Strategy tervezési minta (Design Pattern) közös interfésze.
 * Lehetővé teszi a termékek rendezési irányelvének (algoritmusának)
 * futásidejű cseréjét a kliens (ProductCatalog) kódjának módosítása nélkül.
 */
public interface ProductSortStrategy {
    /**
     * Visszaadja az adott rendezési stratégia ember által olvasható nevét.
     *
     * @return a stratégia megnevezése
     */
    String getStrategyName();

    /**
     * A megadott terméklistát új, rendezett listaként adja vissza az adott stratégia szerint.
     *
     * @param products a rendezendő termékek listája
     * @return a rendezett termékek új listája
     */
    List<Product> sort(List<Product> products);
}
