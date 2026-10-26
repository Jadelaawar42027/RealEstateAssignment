package edu.uni.realestate.model;

import java.util.Map;
import java.util.Scanner;

public abstract class Apartment extends Property {
    private boolean balcony;
    private LaundryType laundry;

    public Apartment(Map<String, String> data) {
        super(data);
        setBalcony(parseYesNo(data.get("balcony"), "Balcony"));
        setLaundry(parseLaundry(data.get("laundry")));
    }

    public boolean hasBalcony() { return balcony; }
    public LaundryType getLaundry() { return laundry; }

    private void setBalcony(boolean value) {
        balcony = value;
    }

    private void setLaundry(LaundryType value) {
        if (value == null) throw new IllegalArgumentException("Laundry type is required");
        laundry = value;
    }

    private static LaundryType parseLaundry(String text) {
        if (text == null) return null;
        try {
            return LaundryType.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Laundry must be ensuite, coin or offsite (got \"" + text + "\")");
        }
    }

    @Override
    public void display() {
        super.display();
        System.out.println("  Balcony:     " + (balcony ? "yes" : "no"));
        System.out.println("  Laundry:     " + laundry);
    }

    public static Map<String, String> promptInit(Scanner in) {
        Map<String, String> data = Property.promptInit(in);
        data.put("balcony", InputHelper.readYesNo(in, "Balcony? (yes/no): "));
        data.put("laundry", InputHelper.readChoice(in, "Laundry (ensuite, coin, offsite): ",
                "ensuite", "coin", "offsite"));
        return data;
    }
}
