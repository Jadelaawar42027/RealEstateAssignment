package edu.uni.realestate.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class Property {
    private static int nextId = 1;

    private int id;
    private int squareFeet;
    private int bedrooms;
    private int bathrooms;
    private boolean available;

    public Property(Map<String, String> data) {
        setSquareFeet(Integer.parseInt(data.get("sqft")));
        setBedrooms(Integer.parseInt(data.get("beds")));
        setBathrooms(Integer.parseInt(data.get("baths")));
        available = true;
    }

    // ----- getters -----
    public int getId()          { return id; }
    public int getSquareFeet()  { return squareFeet; }
    public int getBedrooms()    { return bedrooms; }
    public int getBathrooms()   { return bathrooms; }
    public boolean isAvailable() { return available; }

    // ----- private validating setters -----
    private void setSquareFeet(int value) {
        if (value < 1) throw new IllegalArgumentException("Square feet must be at least 1");
        squareFeet = value;
    }

    private void setBedrooms(int value) {
        if (value < 0) throw new IllegalArgumentException("Bedrooms cannot be negative");
        bedrooms = value;
    }

    private void setBathrooms(int value) {
        if (value < 1) throw new IllegalArgumentException("Bathrooms must be at least 1");
        bathrooms = value;
    }

    protected final void assignId() {
        if (id != 0) throw new IllegalStateException("Property #" + id + " already has an id");
        id = nextId++;
    }

    static boolean parseYesNo(String text, String field) {
        if ("yes".equalsIgnoreCase(text)) return true;
        if ("no".equalsIgnoreCase(text)) return false;
        throw new IllegalArgumentException(field + " must be yes or no");
    }

    // ----- behavior -----
    public void close() {
        if (!available) {
            throw new IllegalStateException("Property #" + id + " is already " + getClosedLabel());
        }
        available = false;
    }

    public abstract String getClosedLabel();

    public void display() {
        String status = available ? "AVAILABLE" : getClosedLabel();
        System.out.println("PROPERTY #" + id + " [" + status + "]");
        System.out.println("  Square feet: " + squareFeet);
        System.out.println("  Bedrooms:    " + bedrooms);
        System.out.println("  Bathrooms:   " + bathrooms);
    }

    public static Map<String, String> promptInit(Scanner in) {
        Map<String, String> data = new HashMap<>();
        data.put("sqft",  InputHelper.readInt(in, "Square feet: ", 1));
        data.put("beds",  InputHelper.readInt(in, "Bedrooms: ", 0));
        data.put("baths", InputHelper.readInt(in, "Bathrooms: ", 1));
        return data;
    }
}
