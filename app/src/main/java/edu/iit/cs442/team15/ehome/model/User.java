package edu.iit.cs442.team15.ehome.model;

public class User {
    public int id;
    public String email;
    public String password;
    public String name;
    public String phone;

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

}
