package hu.prog2.catalog.domain;

/**
 * Saját (domain-specifikus) kivételosztály, amely akkor dobódik,
 * ha egy termék adatai megsértik az üzleti szabályokat (pl. negatív ár vagy készlet),
 * vagy ha duplikált azonosítójú terméket próbálnak regisztrálni.
 */
public class InvalidProductException extends Exception {
    public InvalidProductException(String message) {
        super(message);
    }

    public InvalidProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
