package edu.uni.realestate.app;

import edu.uni.realestate.model.ApartmentPurchase;
import edu.uni.realestate.model.ApartmentRental;
import edu.uni.realestate.model.HousePurchase;
import edu.uni.realestate.model.HouseRental;
import edu.uni.realestate.model.Property;
import edu.uni.realestate.service.Agent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AgentDemo {

    public static void main(String[] args) {
        Agent agent = new Agent();

        heading("1. One object of each concrete class, built from a Map");
        agent.addProperty(new HouseRental(houseRental()));
        agent.addProperty(new HousePurchase(housePurchase()));
        agent.addProperty(new ApartmentRental(apartmentRental()));
        agent.addProperty(new ApartmentPurchase(apartmentPurchase()));
        agent.displayProperties(true);
        System.out.println();
        System.out.println("countAvailable() = " + agent.countAvailable());

        heading("(a) Invalid values are rejected");
        tryCreate("sqft=0 (HouseRental)", HouseRental::new, with(houseRental(), "sqft", "0"));
        tryCreate("beds=-1 (ApartmentRental)", ApartmentRental::new, with(apartmentRental(), "beds", "-1"));
        tryCreate("baths=0 (HouseRental)", HouseRental::new, with(houseRental(), "baths", "0"));
        tryCreate("stories=0 (HousePurchase)", HousePurchase::new, with(housePurchase(), "stories", "0"));
        tryCreate("garage=\"garage\" (HousePurchase)", HousePurchase::new, with(housePurchase(), "garage", "garage"));
        tryCreate("fenced=\"maybe\" (HouseRental)", HouseRental::new, with(houseRental(), "fenced", "maybe"));
        tryCreate("balcony missing (ApartmentRental)", ApartmentRental::new, with(apartmentRental(), "balcony", null));
        tryCreate("laundry missing (ApartmentPurchase)", ApartmentPurchase::new, with(apartmentPurchase(), "laundry", null));
        tryCreate("rent=-5 (ApartmentRental)", ApartmentRental::new, with(apartmentRental(), "rent", "-5"));
        tryCreate("furnished=\"sort of\" (ApartmentRental)", ApartmentRental::new, with(apartmentRental(), "furnished", "sort of"));
        tryCreate("utilities=\"   \" (HouseRental)", HouseRental::new, with(houseRental(), "utilities", "   "));
        tryCreate("price=0 (ApartmentPurchase)", ApartmentPurchase::new, with(apartmentPurchase(), "price", "0"));
        tryCreate("taxes=-100 (HousePurchase)", HousePurchase::new, with(housePurchase(), "taxes", "-100"));
        tryCreate("sqft=\"abc\" (HouseRental)", HouseRental::new, with(houseRental(), "sqft", "abc"));

        Property next = new HouseRental(houseRental());
        agent.addProperty(next);
        System.out.println("After all the rejected objects, the next valid property gets id #" + next.getId()
                + ", so no ids were skipped.");

        heading("(b) Closing a property twice is rejected");
        System.out.println("closeDeal(1)  first time  -> " + agent.closeDeal(1));
        System.out.println("closeDeal(1)  second time -> " + agent.closeDeal(1));
        System.out.println("closeDeal(4)              -> " + agent.closeDeal(4));
        System.out.println("closeDeal(99) unknown id  -> " + agent.closeDeal(99));
        Property first = agent.getProperties().get(0);
        try {
            first.close();
            System.out.println("ERROR: second close() was accepted");
        } catch (IllegalStateException e) {
            System.out.println("Calling close() directly again throws IllegalStateException: " + e.getMessage());
        }

        heading("(c) Closed properties leave the available list but stay in the full list");
        System.out.println("countAvailable() = " + agent.countAvailable() + " out of " + agent.getProperties().size());
        System.out.println();
        System.out.println("--- displayProperties(false): available only ---");
        agent.displayProperties(false);
        System.out.println();
        System.out.println("--- displayProperties(true): all ---");
        agent.displayProperties(true);

        heading("(d) getProperties() is read-only");
        try {
            agent.getProperties().clear();
            System.out.println("ERROR: the internal list was modified");
        } catch (UnsupportedOperationException e) {
            System.out.println("getProperties().clear() throws UnsupportedOperationException, size is still "
                    + agent.getProperties().size());
        }
    }

    private static void heading(String title) {
        System.out.println();
        System.out.println("===== " + title + " =====");
    }

    private static void tryCreate(String label, Function<Map<String, String>, Property> constructor,
                                  Map<String, String> data) {
        try {
            constructor.apply(data);
            System.out.println("ERROR: " + label + " was accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected " + label + ": " + e.getMessage());
        }
    }

    private static Map<String, String> with(Map<String, String> data, String key, String value) {
        Map<String, String> copy = new HashMap<>(data);
        if (value == null) {
            copy.remove(key);
        } else {
            copy.put(key, value);
        }
        return copy;
    }

    private static Map<String, String> house() {
        Map<String, String> d = new HashMap<>();
        d.put("sqft", "1400");
        d.put("beds", "3");
        d.put("baths", "2");
        d.put("stories", "2");
        d.put("garage", "attached");
        d.put("fenced", "yes");
        return d;
    }

    private static Map<String, String> apartment() {
        Map<String, String> d = new HashMap<>();
        d.put("sqft", "750");
        d.put("beds", "0");
        d.put("baths", "1");
        d.put("balcony", "yes");
        d.put("laundry", "ensuite");
        return d;
    }

    private static Map<String, String> houseRental() {
        Map<String, String> d = house();
        d.put("rent", "1850");
        d.put("furnished", "no");
        d.put("utilities", "about 120 EUR/month");
        return d;
    }

    private static Map<String, String> housePurchase() {
        Map<String, String> d = house();
        d.put("garage", "detached");
        d.put("fenced", "no");
        d.put("price", "250000");
        d.put("taxes", "3000");
        return d;
    }

    private static Map<String, String> apartmentRental() {
        Map<String, String> d = apartment();
        d.put("laundry", "coin");
        d.put("rent", "950");
        d.put("furnished", "yes");
        d.put("utilities", "included");
        return d;
    }

    private static Map<String, String> apartmentPurchase() {
        Map<String, String> d = apartment();
        d.put("beds", "2");
        d.put("balcony", "no");
        d.put("laundry", "offsite");
        d.put("price", "180000");
        d.put("taxes", "1500");
        return d;
    }
}
