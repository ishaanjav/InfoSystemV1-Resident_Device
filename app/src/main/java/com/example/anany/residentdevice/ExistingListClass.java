package com.example.anany.residentdevice;

public class ExistingListClass {

    private String name, username, password, description, phone_number, email, relations, time;

    public ExistingListClass(String name, String username, String password, String description, String phone_number, String email, String relations, String time) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.description = description;
        this.phone_number = phone_number;
        this.email = email;
        this.relations = relations;
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public String getRelations() {
        return relations;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}


