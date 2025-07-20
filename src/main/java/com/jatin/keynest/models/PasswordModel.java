package com.jatin.keynest.models;

public class PasswordModel {
    private int id;
    private String title;
    private String username;
    private String encryptedPassword;

    public PasswordModel(int id, String title, String username, String encryptedPassword) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }
}

