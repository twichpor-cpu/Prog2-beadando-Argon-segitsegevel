package hu.prog2.catalog.domain;

import java.util.Locale;

/**
 * Élelmiszer terméket reprezentáló konkrét leszármazott osztály.
 * Tartalmazza a szavatossági időt (napokban) és a bio minősítést.
 */
public class FoodProduct extends Product {
    private static final long serialVersionUID = 1L;

    private int shelfLifeDays;
    private boolean organic;

    public FoodProduct(int id, String name, double price, int stock, int shelfLifeDays, boolean organic)
            throws InvalidProductException {
        super(id, name, price, stock, ProductCategory.FOOD);
        if (shelfLifeDays <= 0) {
            throw new InvalidProductException("A szavatossági időnek pozitívnak kell lennie (kapott: " + shelfLifeDays + ").");
        }
        this.shelfLifeDays = shelfLifeDays;
        this.organic = organic;
    }

    public int getShelfLifeDays() {
        return shelfLifeDays;
    }

    public void setShelfLifeDays(int shelfLifeDays) throws InvalidProductException {
        if (shelfLifeDays <= 0) {
            throw new InvalidProductException("A szavatossági időnek pozitívnak kell lennie.");
        }
        this.shelfLifeDays = shelfLifeDays;
    }

    public boolean isOrganic() {
        return organic;
    }

    public void setOrganic(boolean organic) {
        this.organic = organic;
    }

    @Override
    public String getDescription() {
        return String.format(Locale.US,
                "[#%d] %s (%s) | Ár: %.2f Ft | Készlet: %d db | Szavatosság: %d nap | Bio: %s",
                getId(), getName(), getCategory().getDisplayName(), getPrice(), getStock(),
                shelfLifeDays, organic ? "Igen" : "Nem");
    }
}
