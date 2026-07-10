package com.ayaan.UrlShortner.Exceptions;

public class CustomExceptions {

    public static class DuplicateAliasException extends RuntimeException {
        public DuplicateAliasException(String message) {
            super(message);
        }}

        public static class UrlNotFoundException extends RuntimeException {
            public UrlNotFoundException(String message) {
                super(message);
            }}



            public static class UrlExpiredException extends RuntimeException {
                public UrlExpiredException(String message) {
                    super(message);
                }}

    public static class PlanRestrictionException extends RuntimeException {
        public PlanRestrictionException(String message) {
            super(message);
        }
    }

}
