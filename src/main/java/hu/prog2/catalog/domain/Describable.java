package hu.prog2.catalog.domain;

/**
 * Interfész olyan tartománybeli objektumok számára, amelyek egyedi azonosítóval
 * és szöveges leírással rendelkeznek.
 */
public interface Describable {
    /**
     * Visszaadja az objektum egyedi egész számos azonosítóját.
     *
     * @return egyedi azonosító
     */
    int getId();

    /**
     * Visszaadja az objektum részletes szöveges leírását.
     *
     * @return részletes leírás
     */
    String getDescription();
}
