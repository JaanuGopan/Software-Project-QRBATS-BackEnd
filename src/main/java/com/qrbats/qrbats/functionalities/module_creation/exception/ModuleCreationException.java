package com.qrbats.qrbats.functionalities.module_creation.exception;

public class ModuleCreationException extends RuntimeException {

    public ModuleCreationException(String message) {
        super(message);
    }

    public ModuleCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
