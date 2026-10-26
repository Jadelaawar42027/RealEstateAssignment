package edu.uni.realestate.model;

import java.util.Map;
import java.util.Scanner;

public final class ApartmentPurchase extends Apartment {
    private final PurchaseTerms terms;

    public ApartmentPurchase(Map<String, String> data) {
        super(data);
        terms = new PurchaseTerms(data);
        assignId();
    }

    public PurchaseTerms getTerms() { return terms; }

    @Override
    public String getClosedLabel() { return "SOLD"; }

    @Override
    public void display() {
        super.display();
        terms.display();
    }

    public static Map<String, String> promptInit(Scanner in) {
        Map<String, String> data = Apartment.promptInit(in);
        data.putAll(PurchaseTerms.promptInit(in));
        return data;
    }
}
