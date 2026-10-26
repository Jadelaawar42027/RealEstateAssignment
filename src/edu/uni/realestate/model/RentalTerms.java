package edu.uni.realestate.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class RentalTerms {
    private double monthlyRent;
    private boolean furnished;
    private String utilities;

    public RentalTerms(Map<String, String> data) {
        setMonthlyRent(Double.parseDouble(data.get("rent")));
        setFurnished(Property.parseYesNo(data.get("furnished"), "Furnished"));
        setUtilities(data.get("utilities"));
    }

    public double getMonthlyRent() { return monthlyRent; }
    public boolean isFurnished() { return furnished; }
    public String getUtilities() { return utilities; }

    private void setMonthlyRent(double value) {
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException("Monthly rent must be greater than 0");
        }
        monthlyRent = value;
    }

    private void setFurnished(boolean value) {
        furnished = value;
    }

    private void setUtilities(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Utilities description cannot be blank");
        }
        utilities = value.trim();
    }

    public void display() {
        System.out.printf(Locale.ROOT, "  Monthly rent: %.2f EUR%n", monthlyRent);
        System.out.println("  Furnished:    " + (furnished ? "yes" : "no"));
        System.out.println("  Utilities:    " + utilities);
    }

    public static Map<String, String> promptInit(Scanner in) {
        Map<String, String> data = new HashMap<>();
        data.put("rent", InputHelper.readDouble(in, "Monthly rent: ", 0.01));
        data.put("furnished", InputHelper.readYesNo(in, "Furnished? (yes/no): "));
        String utilities = "";
        while (utilities.isEmpty()) {
            System.out.print("Utilities (included or estimated cost): ");
            utilities = in.nextLine().trim();
            if (utilities.isEmpty()) System.out.println("  Please describe the utilities.");
        }
        data.put("utilities", utilities);
        return data;
    }
}
