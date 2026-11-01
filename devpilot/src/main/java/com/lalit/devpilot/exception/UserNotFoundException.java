package com.lalit.devpilot.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Integer userId) {
        super("User not found with id: " + userId);
    }
}
