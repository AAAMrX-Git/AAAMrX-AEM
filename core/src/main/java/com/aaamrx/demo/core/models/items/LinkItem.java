package com.aaamrx.demo.core.models.items;

public class LinkItem {

    private final String label;
    private final String url;

    public LinkItem(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }
}