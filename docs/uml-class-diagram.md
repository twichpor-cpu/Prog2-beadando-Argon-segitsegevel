# Tervezési Dokumentáció és UML Osztálydiagram

## 1. Problémafelvetés (Problem Statement)
- **Felhasználók:** Egy kis bolt vagy webáruház kezelője.
- **Cél:** Elektronikai és élelmiszer termékek egyszerű nyilvántartása, keresése, rendezése ár vagy név szerint, valamint fájlba mentése.
- **Megoldott probléma:** A közös `Product` ősosztály segítségével különböző típusú termékeket (`ElectronicProduct`, `FoodProduct`) tudunk egyetlen `ArrayList<Product>` listában tárolni, és a `FileStorage` osztállyal szöveges fájlba menteni.

## 2. UML Osztálydiagram

```mermaid
classDiagram
    class Product {
        <<abstract>>
        -static int productCount
        -int id
        -String name
        -double price
        -int stock
        +Product(int id, String name, double price, int stock)
        +getId() int
        +getName() String
        +getPrice() double
        +getStock() int
        +compareTo(Product other) int
        +getDescription()* String
    }

    class ElectronicProduct {
        -int warrantyMonths
        +ElectronicProduct(int id, String name, double price, int stock, int warrantyMonths)
        +getWarrantyMonths() int
        +getDescription() String
    }

    class FoodProduct {
        -int shelfLifeDays
        +FoodProduct(int id, String name, double price, int stock, int shelfLifeDays)
        +getShelfLifeDays() int
        +getDescription() String
    }

    class InvalidProductException {
        <<Exception>>
        +InvalidProductException(String message)
    }

    class ProductSortStrategy {
        <<interface>>
        +sort(List~Product~ products) void
    }

    class PriceSortStrategy {
        +sort(List~Product~ products) void
    }

    class NameSortStrategy {
        +sort(List~Product~ products) void
    }

    class ProductCatalog {
        -ArrayList~Product~ products
        -ProductSortStrategy sortStrategy
        +setSortStrategy(ProductSortStrategy sortStrategy) void
        +addProduct(Product product) void
        +sortProducts() void
        +findProduct(int id) Product
        +findProduct(String name) Product
        +calculateTotalValue() double
    }

    class FileStorage {
        +saveToFile(ArrayList~Product~ products, String fileName) void
        +loadFromFile(String fileName) ArrayList~Product~
    }

    class ConsoleApp {
        +main(String[] args) void
    }

    Product <|-- ElectronicProduct
    Product <|-- FoodProduct
    ProductSortStrategy <|.. PriceSortStrategy
    ProductSortStrategy <|.. NameSortStrategy
    ProductCatalog "1" o-- "0..*" Product : products
    ProductCatalog "1" --> "1" ProductSortStrategy : sortStrategy
    ConsoleApp --> ProductCatalog
    ConsoleApp --> FileStorage
```

## 3. Tervezési minta: Strategy (Stratégia)
- **Minta neve:** Strategy
- **Osztályok:** `ProductSortStrategy` (interfész), `PriceSortStrategy` (ár szerinti rendezés `Comparable`-lel), `NameSortStrategy` (név szerinti rendezés lambda kifejezéssel), és `ProductCatalog` (ami használja a stratégiát).
- **Miért jó:** A `ProductCatalog.setSortStrategy(...)` meghívásával futás közben tudunk váltani az ár szerinti és a név szerinti rendezés között anélkül, hogy a `ProductCatalog` kódját átírnánk.

## 4. Tervezési döntések és korlátok
- **Döntések:** Egyszerű pontosvesszővel (`;`) tagolt szöveges fájlba (`data/catalog.csv`) mentünk `BufferedWriter` és `BufferedReader` (`try-with-resources`) használatával.
- **Korlátok:** Csak konzolos felület van, és az adatok egyszerű szöveges fájlban tárolódnak, nincs adatbázis.
