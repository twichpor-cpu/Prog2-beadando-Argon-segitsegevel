# Felhasználói Történetek (User Stories) és Fejlesztési Backlog

Ez a dokumentum tartalmazza a **Webáruház Termékkatalógus és Rendezési Rendszer (Prog 2 Projektmunka)** felhasználói történeteit (User Stories) és elfogadási kritériumait.

---

## US-01: Különböző típusú termékek egységes kezelése (Öröklődés és Polimorfizmus)
- **Történet:** *Mint webáruház-üzemeltető, szeretném az elektronikai termékeket (garanciaidővel) és az élelmiszereket (szavatossági idővel és bio jelöléssel) egyetlen közös katalógusban tárolni, hogy egységesen kezelhessem a teljes árukészletet.*
- **Elfogadási kritériumok:**
  - Létezik a `Describable` interfész és az absztrakt `Product` ősosztály.
  - Az `ElectronicProduct` és a `FoodProduct` osztályok a `Product` osztályból származnak, és felülírják a `getDescription()` metódust.
  - A `Product` statikus számlálóval (`getCreatedProductCount()`) tartja nyilván a létrehozott példányok számát.

## US-02: Termékek rendezése többféle szempont szerint (Strategy tervezési minta)
- **Történet:** *Mint vásárló vagy raktárkezelő, szeretném a katalógus termékeit ár szerint növekvő, ár szerint csökkenő, név szerint ábécésorrendben, valamint raktárkészlet szerint rendezni, hogy gyorsan áttekinthessem a kínálatot.*
- **Elfogadási kritériumok:**
  - A `Product` megvalósítja a `Comparable<Product>` interfészt (természetes rendezés ár szerint növekvő sorrendben).
  - A **Strategy tervezési minta** (`ProductSortStrategy`) segítségével futásidőben váltható a rendezési irányelv (`PriceAscendingSortStrategy`, `PriceDescendingSortStrategy`, `NameSortStrategy`, `StockSortStrategy`).
  - A rendezések `Comparator<Product>` és lambda-kifejezés használatát is bemutatják.

## US-03: Termékek keresése azonosító és név alapján (Metódus-túlterhelés)
- **Történet:** *Mint raktárkezelő, szeretnék terméket keresni mind egyedi azonosító (`int id`), mind pedig név vagy névrészlet (`String name`) alapján.*
- **Elfogadási kritériumok:**
  - A `ProductCatalog` osztályban túlterhelt `findProduct(int id)` és `findProduct(String name)` metódusok állnak rendelkezésre.
  - A név szerinti keresés kis- és nagybetű-függetlenül működik.

## US-04: Készletstatisztikák és legdrágább termék lekérdezése
- **Történet:** *Mint üzletvezető, szeretném egyetlen gombnyomással lekérdezni a katalógus legdrágább termékét és a teljes raktárkészlet összértékét (`ár × készletmennyiség`).*
- **Elfogadási kritériumok:**
  - A `getMostExpensiveProduct()` visszaadja a legmagasabb egységárú terméket.
  - A `calculateTotalInventoryValue()` pontosan összegzi az összes termék `price * stock` értékét.

## US-05: Adatvalidáció és saját kivételkezelés
- **Történet:** *Mint rendszeradminisztrátor, szeretném, ha a rendszer megakadályozná a hibás (pl. negatív árú, negatív készletű, üres nevű vagy duplikált azonosítójú) termékek rögzítését.*
- **Elfogadási kritériumok:**
  - Üzleti szabály megsértésekor a rendszer saját `InvalidProductException` kivételt dob.
  - A `Product` ősosztály `final validateBasicFields(...)` metódusa garantálja, hogy a leszármazottak ne írhatják felül az alapvető ellenőrzést.

## US-06: Adatok mentése és visszatöltése (Perzisztencia)
- **Történet:** *Mint felhasználó, szeretném, ha a katalógus adatai megmaradnának a program leállítása után is, és szöveges (CSV) vagy bináris formátumban menthetők/betölthetők lennének.*
- **Elfogadási kritériumok:**
  - A `CsvCatalogRepository` és `BinaryCatalogRepository` osztályok `try-with-resources` szerkezettel végzik a fájlkezelést.
  - A program induláskor automatikusan betölti a mentett katalógust (vagy létrehozza a 10 alapértelmezett mintaterméket).
