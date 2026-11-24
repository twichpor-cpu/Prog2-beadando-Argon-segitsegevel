package hu.prog2.catalog.domain;

public class ElectronicProduct extends Product {
    private int warrantyMonths;

    public ElectronicProduct(int id, String name, double price, int stock, int warrantyMonths) throws InvalidProductException {
        super(id, name, price, stock);
        if (warrantyMonths < 0) {
            throw new InvalidProductException("A garancia nem lehet negativ!");
        }
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    @Override
    public String getDescription() {
        return "Elektronika [ID=" + getId() + ", Nev=" + getName() + ", Ar=" + getPrice()
                + ", Keszlet=" + getStock() + ", Garancia=" + warrantyMonths + " ho]";
    }
}
