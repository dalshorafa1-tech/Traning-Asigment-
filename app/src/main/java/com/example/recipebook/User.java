package com.example.recipebook;

public class User {

        public String name, email, imageUrl;
        boolean isonline;

        public User() {}

        public User(String name, String email, String imageUrl, Boolean isonline) {
            this.name = name;
            this.email = email;
            this.imageUrl = imageUrl;
            this.isonline=isonline;

        }
    }


