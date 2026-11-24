package hu.prog2.catalog.persistence;

import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Bináris Java szerializációt ({@link ObjectOutputStream} / {@link ObjectInputStream})
 * megvalósító adattár osztály try-with-resources használatával.
 */
public class BinaryCatalogRepository implements CatalogRepository {
    @Override
    public void save(List<Product> products, Path filePath) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(products));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> load(Path filePath) throws IOException, InvalidProductException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            Object readObject = ois.readObject();
            if (readObject instanceof List<?> rawList) {
                List<Product> products = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof Product product) {
                        products.add(product);
                    } else {
                        throw new InvalidProductException("A bináris fájl nem Product típusú elemet tartalmaz.");
                    }
                }
                return products;
            }
            throw new InvalidProductException("A bináris fájl nem tartalmaz érvényes terméklistát.");
        } catch (ClassNotFoundException e) {
            throw new InvalidProductException("Ismeretlen osztály a deszerializálás során: " + e.getMessage(), e);
        }
    }
}
