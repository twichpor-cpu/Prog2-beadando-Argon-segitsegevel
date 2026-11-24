package hu.prog2.catalog.domain;

public class FoodProduct extends Product {
    private int shelfLifeDays;

    public FoodProduct(int id, String name, double price, int stock, int shelfLifeDays) throws InvalidProductException {
        super(id, name, price, stock);
        if (shelfLifeDays <= 0) {
            throw new InvalidProductException("A szavatossag legyen pozitiv!");
        }
        this.shelfLifeDays = shelfLifeDays;
    }

    public int getShelfLifeDays() {
        return shelfLifeDays;
    }

    @Override
    public String getDescription() {
        return "Elelmiszer [ID=" + getId() + ", Nev=" + getName() + ", Ar=" + getPrice()
                + ", Keszlet=" + getStock() + ", Szavatossag=" + shelfLifeDays + " nap]";
    }
}
