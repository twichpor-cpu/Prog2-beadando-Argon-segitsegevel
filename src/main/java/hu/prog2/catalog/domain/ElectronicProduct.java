package hu.prog2.catalog.domain;

import java.util.Locale;

/**
 * Elektronikai terméket reprezentáló konkrét leszármazott osztály.
 * Tartalmazza a garanciaidőt hónapokban.
 */
public class ElectronicProduct extends Product {
    private static final long serialVersionUID = 1L;

    private int warrantyMonths;

    public ElectronicProduct(int id, String name, double price, int stock, int warrantyMonths)
            throws InvalidProductException {
        super(id, name, price, stock, ProductCategory.ELECTRONICS);
        if (warrantyMonths < 0) {
            throw new InvalidProductException("A garanciaidő nem lehet negatív (kapott: " + warrantyMonths + ").");
        }
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) throws InvalidProductException {
        if (warrantyMonths < 0) {
            throw new InvalidProductException("A garanciaidő nem lehet negatív.");
        }
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public String getDescription() {
        return String.format(Locale.US,
                "[#%d] %s (%s) | Ár: %.2f Ft | Készlet: %d db | Garancia: %d hó",
                getId(), getName(), getCategory().getDisplayName(), getPrice(), getStock(), warrantyMonths);
    }
}
