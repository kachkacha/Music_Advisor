package advisor;

import advisor.enums.ACTION;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        DataBase dataBase = new DataBase(args);

        Controller controller = new Controller(dataBase);

        Scanner scanner = new Scanner(System.in);

        for (boolean on = true; on;) {

            String[] input = scanner.nextLine().split(" ", 2);
            String argument = input.length == 1 ? null : input[1];

            ACTION action;
            try {
                action = ACTION.valueOf(input[0].toUpperCase());
                if (action == ACTION.EXIT) on = false;
            } catch (IllegalArgumentException exception) {
                outputMessage("Invalid action: " + input[0]);
                continue;
            }

            controller.processInput(action, argument);
        }
    }

    public static void outputMessage(String message) {
        System.out.println(message);
    }


}