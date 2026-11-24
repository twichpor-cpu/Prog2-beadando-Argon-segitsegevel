package hu.prog2.catalog.persistence;

import hu.prog2.catalog.domain.ElectronicProduct;
import hu.prog2.catalog.domain.FoodProduct;
import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;
import hu.prog2.catalog.domain.ProductCategory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Szöveges (CSV formátumú) fájlkezelést megvalósító adattár osztály.
 * Használja a try-with-resources szerkezetet a biztonságos erőforrás-kezeléshez.
 */
public class CsvCatalogRepository implements CatalogRepository {
    private static final String HEADER = "CATEGORY;ID;NAME;PRICE;STOCK;EXTRA_1;EXTRA_2";

    @Override
    public void save(List<Product> products, Path filePath) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();
            for (Product product : products) {
                writer.write(serializeRow(product));
                writer.newLine();
            }
        }
    }

    private String serializeRow(Product product) {
        if (product instanceof ElectronicProduct electronic) {
            return String.format(Locale.US, "%s;%d;%s;%.2f;%d;%d;",
                    ProductCategory.ELECTRONICS.name(),
                    electronic.getId(),
                    electronic.getName().replace(";", ","),
                    electronic.getPrice(),
                    electronic.getStock(),
                    electronic.getWarrantyMonths());
        } else if (product instanceof FoodProduct food) {
            return String.format(Locale.US, "%s;%d;%s;%.2f;%d;%d;%b",
                    ProductCategory.FOOD.name(),
                    food.getId(),
                    food.getName().replace(";", ","),
                    food.getPrice(),
                    food.getStock(),
                    food.getShelfLifeDays(),
                    food.isOrganic());
        }
        throw new IllegalArgumentException("Ismeretlen terméktípus: " + product.getClass().getName());
    }

    @Override
    public List<Product> load(Path filePath) throws IOException, InvalidProductException {
        List<Product> loaded = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.equalsIgnoreCase(HEADER)) {
                    continue;
                }
                loaded.add(parseRow(trimmed, lineNumber));
            }
        }
        return loaded;
    }

    private Product parseRow(String line, int lineNumber) throws InvalidProductException {
        String[] parts = line.split(";", -1);
        if (parts.length < 6) {
            throw new InvalidProductException("Hibás CSV sor (" + lineNumber + ". sor): túl kevés oszlop.");
        }
        try {
            ProductCategory category = ProductCategory.valueOf(parts[0].trim());
            int id = Integer.parseInt(parts[1].trim());
            String name = parts[2].trim();
            double price = Double.parseDouble(parts[3].trim());
            int stock = Integer.parseInt(parts[4].trim());

            if (category == ProductCategory.ELECTRONICS) {
                int warrantyMonths = Integer.parseInt(parts[5].trim());
                return new ElectronicProduct(id, name, price, stock, warrantyMonths);
            } else if (category == ProductCategory.FOOD) {
                if (parts.length < 7) {
                    throw new InvalidProductException("Hiányzó bio mező az élelmiszer sorban (" + lineNumber + ". sor).");
                }
                int shelfLifeDays = Integer.parseInt(parts[5].trim());
                boolean organic = Boolean.parseBoolean(parts[6].trim());
                return new FoodProduct(id, name, price, stock, shelfLifeDays, organic);
            }
            throw new InvalidProductException("Nem támogatott kategória: " + category);
        } catch (NumberFormatException e) {
            throw new InvalidProductException("Számformátum hiba a(z) " + lineNumber + ". sorban: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new InvalidProductException("Érvénytelen kategória a(z) " + lineNumber + ". sorban: " + e.getMessage(), e);
        }
    }
}
