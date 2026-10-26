package edu.uni.realestate.model;

import java.util.Scanner;

class InputHelper {

    static String readInt(Scanner in, String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            try {
                int value = Integer.parseInt(text);
                if (value >= min) return String.valueOf(value);
            } catch (NumberFormatException ignored) {
            }
            System.out.println("  Please enter a number of at least " + min + ".");
        }
    }

    static String readDouble(Scanner in, String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            try {
                double value = Double.parseDouble(text);
                if (Double.isFinite(value) && value >= min) return String.valueOf(value);
            } catch (NumberFormatException ignored) {
            }
            if (min == (int) min) {
                System.out.println("  Please enter a number of at least " + (int) min + ".");
            } else {
                System.out.println("  Please enter a number of at least " + min + ".");
            }
        }
    }

    static String readYesNo(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            if (text.equalsIgnoreCase("yes")) return "yes";
            if (text.equalsIgnoreCase("no")) return "no";
            System.out.println("  Please answer yes or no.");
        }
    }

    static String readChoice(Scanner in, String prompt, String... options) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            for (String option : options) {
                if (option.equalsIgnoreCase(text)) return option.toLowerCase();
            }
            System.out.println("  Please choose one of: " + String.join(", ", options));
        }
    }
}
