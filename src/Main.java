
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        SuggestionEngine engine = new SuggestionEngine();

        System.out.println("==================================");
        System.out.println("       SMARTSUGGEST SYSTEM");
        System.out.println("==================================");

        while (true) {

            System.out.print("\nEnter search prefix (or exit): ");

            String prefix = sc.nextLine().trim();

            if (prefix.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for using SmartSuggest!");
                break;
            }

            List<Suggestion> suggestions =
                    engine.getSuggestions(prefix, 5);

            if (suggestions.isEmpty()) {

                System.out.println("No suggestions found.");

            } else {

                System.out.println("\nTop Suggestions:");

                for (int i = 0; i < suggestions.size(); i++) {

                    System.out.println(
                        (i + 1) + ". " + suggestions.get(i)
                    );
                }

                System.out.print(
                    "\nSelect a suggestion number (0 to skip): "
                );

                String choice = sc.nextLine().trim();

                try {

                    int selected = Integer.parseInt(choice);

                    if (selected >= 1 &&
                            selected <= suggestions.size()) {

                        String selectedWord =
                            suggestions.get(selected - 1).getWord();

                        engine.recordSearch(selectedWord);

                        System.out.println(
                            "Selected: " + selectedWord
                        );

                        System.out.println(
                            "Search frequency updated!"
                        );

                    } else if (selected != 0) {

                        System.out.println("Invalid selection.");

                    }

                } catch (NumberFormatException e) {

                    System.out.println("Please enter a valid number.");
                }
            }
        }

        sc.close();
    }
}
