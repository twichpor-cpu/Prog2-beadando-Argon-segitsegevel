# Termékkatalógus Projekt (Prog 2 Beadandó)

Egyszerű Java OOP konzolos alkalmazás egy bolt termékkatalógusának kezelésére (3. gyakorló feladat alapján).

## 1. Projekt leírás
A programban elektronikai termékeket (`ElectronicProduct` - garanciaidővel) és élelmiszereket (`FoodProduct` - szavatossági idővel) lehet tárolni egy közös `ProductCatalog` listában. A termékeket ár vagy név szerint lehet rendezni, ID és név alapján keresni, valamint szöveges fájlba (`data/catalog.csv`) menteni.

## 2. Előfeltételek
- Java 21
- Maven

## 3. Fordítás, tesztelés és futtatás

**Tesztek futtatása:**
```bash
mvn clean test
```

**Program indítása (interaktív menü):**
```bash
mvn exec:java
```

**Gyors bemutató futtatása:**
```bash
mvn exec:java -Dexec.args="--demo"
```

## 4. Minta kimenet (`--demo`)
```text
Adatok betoltve fajlbol (4 db).

--- Rendezve ar szerint (PriceSortStrategy) ---
Elelmiszer [ID=3, Nev=Alma, Ar=500.0, Keszlet=100, Szavatossag=14 nap]
Elelmiszer [ID=4, Nev=Kave, Ar=3500.0, Keszlet=30, Szavatossag=180 nap]
Elektronika [ID=2, Nev=Eger, Ar=8000.0, Keszlet=20, Garancia=12 ho]
Elektronika [ID=1, Nev=Laptop, Ar=300000.0, Keszlet=5, Garancia=24 ho]

--- Rendezve nev szerint (NameSortStrategy) ---
Elelmiszer [ID=3, Nev=Alma, Ar=500.0, Keszlet=100, Szavatossag=14 nap]
Elektronika [ID=2, Nev=Eger, Ar=8000.0, Keszlet=20, Garancia=12 ho]
Elelmiszer [ID=4, Nev=Kave, Ar=3500.0, Keszlet=30, Szavatossag=180 nap]
Elektronika [ID=1, Nev=Laptop, Ar=300000.0, Keszlet=5, Garancia=24 ho]
```

## 5. Megvalósított OOP követelmények
- **Encapsulation (Egységbezárás):** `private` mezők és getterek a `Product` és leszármazott osztályokban, ellenőrzés a konstruktorban.
- **Abstraction (Absztrakció):** `Product` absztrakt ősosztály (`abstract String getDescription()`) és `ProductSortStrategy` interfész.
- **Inheritance & Polymorphism (Öröklődés és polimorfizmus):** `ElectronicProduct` és `FoodProduct` a `Product`-ból származnak, és felülírják a `getDescription()` metódust. Túlterhelt keresés: `findProduct(int id)` és `findProduct(String name)`.
- **Relationships (Kapcsolatok):** A `ProductCatalog` aggregációval (`ArrayList<Product>`) tárolja a termékeket.
- **Collections & Generics:** `ArrayList<Product>`, természetes rendezés `Comparable<Product>`-tal (ár szerint), név szerinti rendezés lambda kifejezéssel.
- **Exceptions (Kivételkezelés):** Saját `InvalidProductException` kivétel hibás (pl. negatív) ár, készlet vagy duplikált ID esetén.
- **Persistence (Fájlkezelés):** `FileStorage` osztály `BufferedReader` és `BufferedWriter` + `try-with-resources` használatával (`data/catalog.csv`).

## 6. Tervezési minta (Design Pattern): Strategy
- **Minta neve:** Strategy (Stratégia)
- **Osztályok:** `ProductSortStrategy` (interfész), `PriceSortStrategy` és `NameSortStrategy` (konkrét stratégiák), `ProductCatalog` (Context).
- **Előny:** A `ProductCatalog` osztályban a `setSortStrategy(...)` metódussal egyszerűen lecserélhető, hogy ár vagy név szerint rendezze a listát.

## 7. Dokumentáció
- [`docs/uml-class-diagram.md`](docs/uml-class-diagram.md) és [`docs/uml-class-diagram.puml`](docs/uml-class-diagram.puml)
- [`docs/user-stories.md`](docs/user-stories.md)

## 8. Készítő
- twichpor-cpu
