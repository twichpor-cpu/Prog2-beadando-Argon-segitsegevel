# Webáruház Termékkatalógus és Rendezési Rendszer (Prog 2 Projektmunka)

Ez a projekt a **Programozás 2 (Java OOP Projectwork Assignment)** tárgyra készült, és egy moduláris, objektumorientált **Webáruház Termékkatalógus és Rendezési Rendszert** valósít meg Java nyelven, Maven build rendszerrel és JUnit 5 egységtesztekkel. A téma közvetlenül a kurzus *Java OOP — Gyakorló feladatok (3. feladat: Termékkatalógus és rendezés + Extra kihívás)* feladatsorára épül, kiegészítve a projektkiírás összes technikai és tervezési követelményével.

---

## 1. Projekt összefoglaló és problémafelvetés (Problem Statement)

- **Felhasználók:** Webáruház-üzemeltetők, raktárkezelők és vásárlók.
- **Felhasználói cél:** Különböző kategóriájú termékek (elektronikai eszközök és élelmiszerek) egységes nyilvántartása, gyors keresése (azonosító vagy név alapján), többféle szempont szerinti rendezése (ár szerint növekvő/csökkenő, név, raktárkészlet), készletérték-statisztikák lekérdezése, valamint az adatok futások közötti megőrzése (CSV és bináris fájlokban).
- **Megoldott probléma:** A különböző attribútumokkal rendelkező terméktípusok (garanciaidős `ElectronicProduct` és szavatossági idős / bio `FoodProduct`) közös absztrakció (`Describable`, `Product`) mögé szervezése, az üzleti szabályok szigorú ellenőrzése saját kivétellel (`InvalidProductException`), valamint a rendezési algoritmusok futásidejű rugalmas cserélhetősége a **Strategy (Stratégia) tervezési minta** segítségével.

---

## 2. Előfeltételek (Prerequisites)

- **Java Development Kit (JDK):** Java 21 (vagy újabb)
- **Build eszköz:** Apache Maven 3.8+
- **Verziókezelő:** Git

---

## 3. Fordítás, tesztelés és futtatás (Build, Test & Run Commands)

Klónozd a repository-t, majd a projekt gyökérkönyvtárában futtasd az alábbi parancsokat:

### Projekt fordítása és JUnit 5 tesztek futtatása
```bash
mvn clean test
```

### Automatikus bemutató mód futtatása (`--demo`)
Bemutatja az összes rendezési stratégiát, a polimorfizmust, a túlterhelt keresést, a készletérték-számítást, a saját kivétel kezelését és a fájlba mentést:
```bash
mvn exec:java -Dexec.args="--demo"
```

### Interaktív konzolos menü indítása
```bash
mvn exec:java
```

### Futtatható JAR csomagolása és indítása
```bash
mvn package
java -jar target/webshop-catalog-1.0.0.jar --demo
```

---

## 4. Minta használat és kimenet (Sample Usage)

A `mvn exec:java -Dexec.args="--demo"` parancs futtatásakor a program betölti a 10 mintaterméket (`5 db ElectronicProduct` és `5 db FoodProduct`), majd egymás után bemutatja a funkciókat:

```text
=========================================================================
   Prog2 Webáruház Katalógus - AUTOMATIKUS BEMUTATÓ (--demo)
=========================================================================

--- 1. Ár szerint növekvő rendezés (Comparable / PriceAscendingSortStrategy) ---
Aktív stratégia: Ár szerint növekvő (Comparable)
  [#201] Bio Zabpehely (500g) (Élelmiszer) | Ár: 890.00 Ft | Készlet: 120 db | Szavatosság: 365 nap | Bio: Igen
  [#205] Bio Mandulatej (1L) (Élelmiszer) | Ár: 1190.00 Ft | Készlet: 60 db | Szavatosság: 180 nap | Bio: Igen
  ...
  [#101] ASUS ROG Gamer Laptop (Elektronika) | Ár: 489990.00 Ft | Készlet: 5 db | Garancia: 36 hó

--- 5. Statisztikák és összesítések ---
Legdrágább termék: [#101] ASUS ROG Gamer Laptop (Elektronika) | Ár: 489990.00 Ft | Készlet: 5 db | Garancia: 36 hó
Teljes raktárkészlet értéke: 6779430.00 Ft
Létrehozott termékpéldányok száma (statikus számláló): 10 db

--- 6. Túlterhelt keresés (findProduct(int) és findProduct(String)) ---
Keresés ID=103 alapján: [#103] Logitech MX Master 3S Egér (Elektronika) | Ár: 39990.00 Ft | Készlet: 25 db | Garancia: 24 hó
Keresés 'Kávé' névrészlet alapján: [#202] Prémium Arabica Szemes Kávé (1kg) (Élelmiszer) | Ár: 7490.00 Ft | Készlet: 45 db | Szavatosság: 540 nap | Bio: Nem

--- 7. Saját kivétel (InvalidProductException) kezelésének bemutatása ---
Elkapott üzleti szabály kivétel: A termék ára nem lehet negatív (kapott: -5000.0).
```

---

## 5. Megvalósított technikai követelmények (Implemented Requirements)

| Követelmény terület | Megvalósítás a projektben |
| :--- | :--- |
| **Egységbezárás (Encapsulation)** | Minden osztályban `private` adattagok, érvényes állapotot garantáló konstruktorok, ellenőrzött getter/setter metódusok és `final validateBasicFields(...)` metódus a `Product` osztályban. Statikus példányszámláló (`Product.getCreatedProductCount()`). |
| **Absztrakció (Abstraction)** | `Describable` (domain interfész), `Product` (absztrakt ősosztály), `ProductSortStrategy` (rendezési stratégia interfész), `CatalogRepository` (perzisztencia interfész). |
| **Öröklődés és polimorfizmus (Inheritance & Polymorphism)** | Az `ElectronicProduct` és a `FoodProduct` konkrét osztályok a `Product` absztrakt osztályból öröklődnek, és futásidőben polimorf módon írják felül a `getDescription()` metódust. Metódus-túlterhelés: `findProduct(int id)` és `findProduct(String name)`. |
| **Kapcsolatok (Relationships)** | **Aggregáció:** `ProductCatalog` (1) o-- (0..*) `Product`. **Kompozíció:** `Product` (1) *-- (1) `ProductCategory`. **Asszociáció / Függőség:** `ProductCatalog` (1) --> (1) `ProductSortStrategy`. |
| **Kollekciók és generikusok (Collections & Generics)** | `List<Product>`, `ArrayList<Product>`, természetes rendezés `Comparable<Product>`-tal, egyedi rendezések `Comparator<Product>`-tal és lambda-kifejezéssel (`(p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice())`). |
| **Kivételkezelés (Exceptions)** | Saját ellenőrzött üzleti kivétel: `InvalidProductException` (negatív ár, negatív készlet, érvénytelen ID, üres név, negatív garanciaidő, duplikált termékazonosító vagy hibás CSV sor esetén). |
| **Perzisztencia (Persistence)** | Szöveges CSV (`CsvCatalogRepository` -> `data/catalog.csv`) és Java bináris objektum-szerializáció (`BinaryCatalogRepository` -> `data/catalog.bin`), mindkettő `try-with-resources` blokkokkal. |
| **Egységtesztek (Testing)** | 10 átfogó JUnit 5 teszteset a `src/test/java/hu/prog2/catalog/ProductCatalogTest.java` osztályban. |

---

## 6. Alkalmazott tervezési minta: Strategy (Stratégia)

- **Tervezési minta neve:** **Strategy (Stratégia)**
- **Megoldott tervezési probléma:** A termékkatalógust különböző üzleti szempontok szerint kell rendezni (ár szerint növekvő, ár szerint csökkenő, név szerint, raktárkészlet szerint). Ha ezeket feltételes elágazásokkal (`if-else` / `switch`) égetnénk bele a `ProductCatalog` osztályba, az megsértené a *Single Responsibility* és *Open/Closed* elveket.
- **Résztvevő osztályok:**
  - `ProductSortStrategy` (Strategy interfész a `hu.prog2.catalog.strategy` csomagban)
  - `PriceAscendingSortStrategy` (ConcreteStrategy 1 — `Comparable<Product>` alapú)
  - `PriceDescendingSortStrategy` (ConcreteStrategy 2 — Lambda `Comparator` alapú)
  - `NameSortStrategy` (ConcreteStrategy 3 — Név szerinti `Comparator<Product>`)
  - `StockSortStrategy` (ConcreteStrategy 4 — Raktárkészlet szerinti `Comparator<Product>`)
  - `ProductCatalog` (Context — tárolja az aktuális `ProductSortStrategy` példányt, amely a `setSortStrategy(...)` metódussal futásidőben lecserélhető).
- **Előny a tervezésben:** Új rendezési algoritmus hozzáadásához nem kell módosítani a `ProductCatalog` osztályt, és a rendezési irányelv futásidőben dinamikusan váltható.

---

## 7. Dokumentáció és Csomagstruktúra

- [`docs/uml-class-diagram.md`](docs/uml-class-diagram.md) — Problémafelvetés, Mermaid UML osztálydiagram, Strategy minta részletes kifejtése, tervezési döntések és korlátok.
- [`docs/uml-class-diagram.puml`](docs/uml-class-diagram.puml) — Szerkeszthető PlantUML osztálydiagram forrásfájl.
- [`docs/user-stories.md`](docs/user-stories.md) — Felhasználói történetek (User Stories) és elfogadási kritériumok.
- [`LICENSE`](LICENSE) — MIT Licenc.

---

## 8. Közreműködő (Contributor)

- **Készítette:** twichpor-cpu (Programozás 2 — Java OOP Projektmunka, 2026)
