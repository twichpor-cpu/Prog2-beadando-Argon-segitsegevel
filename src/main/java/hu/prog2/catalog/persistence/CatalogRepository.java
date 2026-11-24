package hu.prog2.catalog.persistence;

import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Perzisztencia réteg interfésze a termékkatalógus adatainak fájlba mentéséhez
 * és fájlból történő visszaolvasásához.
 */
public interface CatalogRepository {
    /**
     * Elmenti a termékek listáját a megadott fájlútvonalra.
     *
     * @param products a mentendő termékek listája
     * @param filePath a célfájl elérési útja
     * @throws IOException ha írási hiba történik
     */
    void save(List<Product> products, Path filePath) throws IOException;

    /**
     * Betölti a termékek listáját a megadott fájlútvonalról.
     *
     * @param filePath a forrásfájl elérési útja
     * @return a betöltött termékek listája
     * @throws IOException             ha olvasási hiba történik
     * @throws InvalidProductException ha a fájlban érvénytelen termékadat szerepel
     */
    List<Product> load(Path filePath) throws IOException, InvalidProductException;
}
