package edu.uni.realestate.service;

import edu.uni.realestate.model.ApartmentPurchase;
import edu.uni.realestate.model.ApartmentRental;
import edu.uni.realestate.model.HousePurchase;
import edu.uni.realestate.model.HouseRental;
import edu.uni.realestate.model.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

public class Agent {

    private static final Map<String, Function<Scanner, Map<String, String>>> PROMPTS = new LinkedHashMap<>();
    private static final Map<String, Function<Map<String, String>, Property>> CONSTRUCTORS = new LinkedHashMap<>();

    static {
        register("house rental", HouseRental::promptInit, HouseRental::new);
        register("house purchase", HousePurchase::promptInit, HousePurchase::new);
        register("apartment rental", ApartmentRental::promptInit, ApartmentRental::new);
        register("apartment purchase", ApartmentPurchase::promptInit, ApartmentPurchase::new);
    }

    private static void register(String key, Function<Scanner, Map<String, String>> prompt,
                                 Function<Map<String, String>, Property> constructor) {
        PROMPTS.put(key, prompt);
        CONSTRUCTORS.put(key, constructor);
    }

    private final List<Property> properties;

    public Agent() {
        properties = new ArrayList<>();
    }

    public void addProperty(Property p) {
        if (p == null) throw new IllegalArgumentException("Property cannot be null");
        properties.add(p);
    }

    public void addProperty(Scanner in) {
        String type = askChoice(in, "Property type", 0);
        String deal = askChoice(in, "Deal type", 1);
        String key = type + " " + deal;
        if (!CONSTRUCTORS.containsKey(key)) {
            throw new IllegalArgumentException("There is no property class for " + key);
        }
        Map<String, String> data = PROMPTS.get(key).apply(in);
        Property p = CONSTRUCTORS.get(key).apply(data);
        addProperty(p);
        System.out.println("Added property #" + p.getId() + ".");
    }

    public void displayProperties(boolean showAll) {
        boolean shown = false;
        for (Property p : properties) {
            if (showAll || p.isAvailable()) {
                if (shown) System.out.println();
                p.display();
                shown = true;
            }
        }
        if (!shown) System.out.println("No properties to show.");
    }

    public boolean closeDeal(int id) {
        Property p = findById(id);
        if (p == null || !p.isAvailable()) return false;
        p.close();
        return true;
    }

    public int countAvailable() {
        int count = 0;
        for (Property p : properties) {
            if (p.isAvailable()) count++;
        }
        return count;
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    private Property findById(int id) {
        for (Property p : properties) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    private static String askChoice(Scanner in, String label, int part) {
        Set<String> options = new LinkedHashSet<>();
        for (String key : CONSTRUCTORS.keySet()) {
            options.add(key.split(" ")[part]);
        }
        String list = String.join(", ", options);
        while (true) {
            System.out.print(label + " (" + list + "): ");
            String answer = in.nextLine().trim().toLowerCase();
            if (options.contains(answer)) return answer;
            System.out.println("  Please choose one of: " + list);
        }
    }
}
