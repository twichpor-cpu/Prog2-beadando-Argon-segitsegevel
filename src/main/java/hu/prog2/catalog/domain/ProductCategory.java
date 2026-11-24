package hu.prog2.catalog.domain;

/**
 * A termékek kategóriáit reprezentáló felsorolási típus (enum).
 */
public enum ProductCategory {
    ELECTRONICS("Elektronika"),
    FOOD("Élelmiszer");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
