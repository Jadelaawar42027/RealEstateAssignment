package edu.uni.realestate.model;

import java.util.Map;
import java.util.Scanner;

public final class ApartmentRental extends Apartment {
    private final RentalTerms terms;

    public ApartmentRental(Map<String, String> data) {
        super(data);
        terms = new RentalTerms(data);
        assignId();
    }

    public RentalTerms getTerms() { return terms; }

    @Override
    public String getClosedLabel() { return "RENTED"; }

    @Override
    public void display() {
        super.display();
        terms.display();
    }

    public static Map<String, String> promptInit(Scanner in) {
        Map<String, String> data = Apartment.promptInit(in);
        data.putAll(RentalTerms.promptInit(in));
        return data;
    }
}
