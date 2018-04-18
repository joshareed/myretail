package com.myretail

class BadRequestException extends RuntimeException {

    BadRequestException(String message) {
        super(message)
    }
}
