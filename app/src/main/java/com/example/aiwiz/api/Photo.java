package com.example.aiwiz.api;

// Photo.java

import java.io.Serializable;

public class Photo implements Serializable {
    private String id;
    private String description;
    private String alt_description;
    private Urls urls;
    private User user;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    public String getAlt_description() {
        return alt_description;
    }
    public Urls getUrls() {
        return urls;
    }

    public User getUser() {
        return user;
    }

    public class Urls implements Serializable{
        private String small;
        private String regular;

        // Getters
        public String getSmall() {
            return small;
        }

        public String getRegular() {
            return regular;
        }
    }

    public class User implements Serializable{
        private String name;

        // Getter
        public String getName() {
            return name;
        }
    }
}
