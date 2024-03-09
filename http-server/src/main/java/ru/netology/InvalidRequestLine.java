package ru.netology;

public class InvalidRequestLine extends RuntimeException {
    public InvalidRequestLine(String message) {
        super(message);
    }
}
