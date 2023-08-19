package verification;

import java.util.Random;

public class Pancakes {
    private static final int MAX_PANCAKES = 5;
    private static final int MAX_PANCAKES_SHOPKEEPER = 12;
    private static final int NUM_USERS = 3;

    private static void simulateSlot(int slotNumber) {
        System.out.println("30 Seconds Slot " + slotNumber + ":");
        int shopkeeperPancakes = makePancakes();
        System.out.println("Pancakes made by shopkeeper: " + shopkeeperPancakes);

        int[] userPancakes = eatPancakes();
        int totalPancakesEaten = calculateTotalPancakesEaten(userPancakes);

        printPancakesEaten(userPancakes, totalPancakesEaten);

        if (shopkeeperPancakes >= totalPancakesEaten) {
            System.out.println("Shopkeeper met the needs of all users.");
        } else {
            int unmetOrders = totalPancakesEaten - shopkeeperPancakes;
            System.out.println("Shopkeeper was not able to meet the needs of all users.");
            System.out.println("Unmet pancake orders: " + unmetOrders);
        }

        int wastage = shopkeeperPancakes - totalPancakesEaten;
        if (wastage > 0) {
            System.out.println("Pancake wastage: " + wastage);
        }

        System.out.println();
    }

    private static int makePancakes() {
        Random random = new Random();
        return random.nextInt(MAX_PANCAKES_SHOPKEEPER + 1);
    }

    private static int[] eatPancakes() {
        Random random = new Random();
        int[] userPancakes = new int[NUM_USERS];
        for (int j = 0; j < NUM_USERS; j++) {
            userPancakes[j] = random.nextInt(MAX_PANCAKES + 1);
        }
        return userPancakes;
    }

    private static int calculateTotalPancakesEaten(int[] userPancakes) {
        int totalPancakesEaten = 0;
        for (int pancakes : userPancakes) {
            totalPancakesEaten += pancakes;
        }
        return totalPancakesEaten;
    }



    private static void printPancakesEaten(int[] userPancakes, int totalPancakesEaten) {
        System.out.print("Pancakes eaten by users: ");
        for (int j = 0; j < NUM_USERS; j++) {
            System.out.print("User " + (j + 1) + ": " + userPancakes[j] + " ");
        }
        System.out.println("\nTotal pancakes eaten: " + totalPancakesEaten);
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            simulateSlot(i);
        }
    }

}
