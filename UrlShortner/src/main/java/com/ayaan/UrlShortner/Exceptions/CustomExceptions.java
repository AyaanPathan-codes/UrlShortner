package com.ayaan.UrlShortner.Exceptions;

public class CustomExceptions {

    public class DuplicateAliasException extends RuntimeException {
        public DuplicateAliasException(String message) {
            super(message);
        }}

        public class UrlNotFoundException extends RuntimeException {
            public UrlNotFoundException(String message) {
                super(message);
            }}



            public class UrlExpiredException extends RuntimeException {
                public UrlExpiredException(String message) {
                    super(message);
                }}
}
