package edu.uni.realestate.model;

import java.util.Map;
import java.util.Scanner;

public final class HouseRental extends House {
    private final RentalTerms terms;

    public HouseRental(Map<String, String> data) {
        super(data);                      // House validates its keys, then Property validates its keys
        terms = new RentalTerms(data);    // composition: RentalTerms validates rent, furnished, utilities
        assignId();
    }

    public RentalTerms getTerms() { return terms; }   // safe: RentalTerms has no public setters

    @Override
    public String getClosedLabel() { return "RENTED"; }

    @Override
    public void display() {
        super.display();
        terms.display();
    }

    public static Map<String, String> promptInit(Scanner in) {
        Map<String, String> data = House.promptInit(in);
        data.putAll(RentalTerms.promptInit(in));
        return data;
    }
}
