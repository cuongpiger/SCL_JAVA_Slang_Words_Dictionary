import java.io.IOException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
    static Dictionary dictionary;

    static {
        dictionary = new Dictionary();
        dictionary.loadDB();
    }

    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static int showMenu() throws IOException {
        int choose = 0;
        Scanner scanner = new Scanner(System.in);

        try {
            while (choose < 1 || choose > 6) {
                clearScreen();

                System.out.println("\uD83D\uDCD6 SLANG-WORD DICTIONARY \uD83D\uDCD6");
                System.out.println("   1. Search based on slang-word");
                System.out.println("   2. Search based on definition");
                System.out.println("   3. Show search history");
                System.out.println("   4. Add a new slang-word");
                System.out.println("   5. Edit a slang-word");
                System.out.println("   6. Delete a slang-word");
                System.out.print("\nEnter your choice: ");

                choose = scanner.nextInt();
            }
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println("⛔ Inside the method App.showMenu() errors occurred!!!");
        }

        return choose;
    }

    static void pressEnter() {
        String key_press = "";
        do {
            System.out.println("\uD83D\uDCFA Press 'ENTER' to continue...");
            key_press = (new Scanner(System.in)).nextLine();
        } while (key_press == "");
    }

    static String enterKeyword(int choice) {
        String keyword = "";

        if (choice == 1 || choice == 2) {
            do {
                clearScreen();

                System.out.print("Enter your keyword (keyword cannot be empty): ");
                keyword = (new Scanner(System.in)).nextLine().trim();
            } while (keyword == "");

            return keyword.trim().toLowerCase();
        }

        if (choice == 4) {
            int choose = 0;
            String def = "";
            String screen = "Enter your new slang-word (slang-word cannot be empty): ";

            do {
                clearScreen();

                System.out.print(screen);
                keyword = (new Scanner(System.in)).nextLine().trim();
            } while (keyword.isBlank() || keyword.isEmpty());

            screen += keyword;

            if (dictionary.get_dict().containsKey(keyword.toLowerCase())) {
                Scanner scanner = new Scanner(System.in);
                screen += ("\n\uD83D\uDCAC '" + keyword + "' was already existed in the data. Do you want...");
                screen += "\n   1. Overwrite\n   2. Duplicate\n   3. Cancel\n\nEnter your choice: ";

                try {
                    while (choose < 1 || choose > 3) {
                        clearScreen();
                        System.out.print(screen);

                        choose = scanner.nextInt();

                        if (choose == 3) {
                            return "";
                        }
                    }
                } catch (IllegalStateException | NoSuchElementException e) {
                    System.out.println("⛔ Inside the method App.enterKeyword() errors occurred!!!");
                }

                screen += choose;
            } else {
                choose = 4;
            }

            screen += "\nEnter your definition (definition cannot be empty): ";

            do {
                clearScreen();

                System.out.print(screen);
                def = (new Scanner(System.in)).nextLine().trim();
            } while (def.isBlank() || def.isEmpty());

            if (choose == 3) {
                return "";
            } else {
                return keyword.trim() + "`" + choose + "`" + def;
            }
        }

        if (choice == 5) {
            String screen = "Enter your slang-word which you need to edit (slang-word cannot be empty): ";
            int id = -1;
            String new_definition = "";

            do {
                clearScreen();

                System.out.print(screen);
                keyword = (new Scanner(System.in)).nextLine().trim();
            } while (keyword.isBlank() || keyword.isEmpty());

            screen += keyword + "\nEnter the definition ID which you want to edit: ";

            if (!dictionary.get_dict().containsKey(keyword.toLowerCase())) {
                System.out.println("\uD83D\uDCAC Your word does not exist in the data!!!");

                return "";
            } else {
                do {
                    clearScreen();
                    dictionary.searchBasedSlang(keyword);

                    System.out.print(screen);

                    try {
                        id = (new Scanner(System.in)).nextInt();
                    } catch (Exception err) {
                        id = -1;
                    }
                } while (id < 1 || id > dictionary.get_dict().get(keyword.toLowerCase()).defs.size());

                do {
                    System.out.print("\uD83D\uDCAC Enter your new definition: ");
                    new_definition = (new Scanner(System.in)).nextLine().trim();
                } while (new_definition.isBlank() || new_definition.isEmpty());

                return keyword + "`" + (id - 1) + "`" + new_definition;
            }
        }

        if (choice == 6) {
            String sw = "";
            String confirm = "";
            String screen = "Enthe the slang-word which you want to delete: ";

            do {
                clearScreen();
                System.out.print(screen);
                sw = (new Scanner(System.in)).nextLine().trim();
            } while (sw.isBlank() || sw.isEmpty());

            screen += sw;

            do {
                clearScreen();
                screen += String.format("\nDo you really want to delete '%s' forever? [Y/N]: ", sw);
                System.out.print(screen);

                confirm = (new Scanner(System.in)).nextLine().trim().toLowerCase();
            } while (!confirm.equals("y") && !confirm.equals("n"));

            return (sw + "`" + confirm);
        }

        return "";
    }

    static void handle(int choice) {
        String keyword = "";
        switch (choice) {
            case 1:
                keyword = enterKeyword(choice);
                dictionary.searchBasedSlang(keyword);
                break;

            case 2:
                keyword = enterKeyword(choice);
                dictionary.searchBasedDefinition(keyword);
                break;

            case 3:
                dictionary.showHistory();
                break;

            case 4:
                keyword = enterKeyword(choice);

                if (keyword != "") {
                    String[] splits = keyword.split("`");
                    String word = splits[0];
                    int choose = Integer.parseInt(splits[1]);
                    String def = splits[2].trim();
                    dictionary.addSlangWord(word, def, choose);
                }

                break;

            case 5:
                keyword = enterKeyword(choice);

                if (keyword != "") {
                    String[] splits = keyword.split("`");
                    dictionary.updateSlang(splits[0], Integer.parseInt(splits[1]), splits[2]);
                }

            case 6:
                keyword = enterKeyword(choice);
                String[] splits = keyword.split("`");
                splits[0] = splits[0].toLowerCase();

                if (splits[1].equals("y")) {
                    dictionary.deleteASlang(splits[0]);
                }
        }

        pressEnter();
        clearScreen();
    }

    public static void main(String[] args) {
        int choose = 0;

        while (true) {
            try {
                while (choose == 0) {
                    choose = showMenu();
                }
            } catch (IOException err) {
                System.out.println("⛔ Inside the method App.main() errors occurred!!!");
            }

            clearScreen();
            handle(choose);
            choose = 0;
        }
    }
}
