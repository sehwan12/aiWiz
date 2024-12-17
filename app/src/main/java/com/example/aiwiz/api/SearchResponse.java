package com.example.aiwiz.api;

// SearchResponse.java

import java.util.List;

public class SearchResponse {
    private int total;
    private int total_pages;
    private List<Photo> results;

    // Getters
    public int getTotal() {
        return total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public List<Photo> getResults() {
        return results;
    }
}
