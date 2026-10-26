package edu.uni.realestate.app;

import edu.uni.realestate.model.Property;
import edu.uni.realestate.service.Agent;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final String MENU =
            "1) Add property   2) List available   3) List all   4) Mark sold/rented   0) Exit";

    public static void main(String[] args) {
        Agent agent = new Agent();
        System.out.println("=== Real Estate Agent ===");
        System.out.println(MENU);
        try (Scanner in = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.print("Choice: ");
                String choice = in.nextLine().trim();
                switch (choice) {
                    case "1" -> addProperty(agent, in);
                    case "2" -> agent.displayProperties(false);
                    case "3" -> agent.displayProperties(true);
                    case "4" -> closeDeal(agent, in);
                    case "0" -> running = false;
                    default -> {
                        System.out.println("  Unknown choice \"" + choice + "\". Please choose 0-4.");
                        System.out.println(MENU);
                    }
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
        }
        System.out.println("Goodbye.");
    }

    private static void addProperty(Agent agent, Scanner in) {
        try {
            agent.addProperty(in);
        } catch (IllegalArgumentException e) {
            System.out.println("  Property not added: " + e.getMessage());
        }
    }

    private static void closeDeal(Agent agent, Scanner in) {
        System.out.print("Property id: ");
        String text = in.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("  \"" + text + "\" is not a valid id.");
            return;
        }
        if (agent.closeDeal(id)) {
            String label = "";
            for (Property p : agent.getProperties()) {
                if (p.getId() == id) label = p.getClosedLabel();
            }
            System.out.println("Property #" + id + " marked as " + label + ".");
        } else {
            System.out.println("Property #" + id + " is not available.");
        }
    }
}
