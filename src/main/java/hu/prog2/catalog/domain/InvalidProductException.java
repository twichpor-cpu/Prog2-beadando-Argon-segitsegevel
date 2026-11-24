package hu.prog2.catalog.domain;

public class InvalidProductException extends Exception {
    public InvalidProductException(String message) {
        super(message);
    }
}
