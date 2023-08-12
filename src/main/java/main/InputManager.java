package main;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InputManager {
    Scanner scanner;
    List<String> confirms;
    public InputManager() {
        this.scanner = new Scanner(System.in);
        this.confirms = Arrays.asList("Yes", "yes", "Y", "y");
    }

    public int getIntInput(int min, int max) {
        int result;
        try {
            System.out.print("\n> Enter option: ");
            result = Integer.parseInt(this.scanner.nextLine());
            if(result < min || result > max) result = 0;
        } catch(NumberFormatException e) {
            result = 0;
        }
        return result;
    }

    public String getStringInput(String var_name) {
        System.out.print("> Enter "+var_name+": ");
        return this.scanner.nextLine();
    }

    public boolean getConfirmation() {
        System.out.print("> Enter confirmation (yes/no): ");
        String input = this.scanner.nextLine();
        return this.confirms.contains(input);
    }
}
