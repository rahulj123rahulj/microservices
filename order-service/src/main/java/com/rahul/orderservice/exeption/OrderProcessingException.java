package com.rahul.orderservice.exeption;

public class OrderProcessingException extends RuntimeException {
        public OrderProcessingException(String message) {
            super(message);
        }

        public OrderProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
}
