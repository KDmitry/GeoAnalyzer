package edu.Dmitry.geodownloader.exeption;

public class InstagramApiExeption extends Exception {
    private String message;
    private Exception internalException;

    public InstagramApiExeption(String message, Exception internalException) {
        this.message = message;
        this.internalException = internalException;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Exception getInternalException() {
        return internalException;
    }
}
