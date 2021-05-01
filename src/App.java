import javax.imageio.IIOException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
    static Dictionary dictionary;

    static {
        dictionary = new Dictionary();
//        dictionary.loadDBFormTxtFile();
    }

    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pressEnter() {
        String key_press = "";
        do {
            System.out.println("\uD83D\uDCFA Press 'ENTER' to continue...");
            key_press = (new Scanner(System.in)).nextLine();
        } while (key_press == "");
    }

    static int showMenu() throws IOException {
        int choose = 0;
        Scanner scanner = new Scanner(System.in);

        try {
            while (choose < 1 || choose > 10) {
                clearScreen();

                System.out.println("\uD83D\uDCD6 SLANG-WORD DICTIONARY \uD83D\uDCD6");
                System.out.println("   1. Search based on slang-word");
                System.out.println("   2. Search based on definition");
                System.out.println("   3. Show search history");
                System.out.println("   4. Add a new slang-word");
                System.out.println("   5. Edit a slang-word");
                System.out.println("   6. Delete a slang-word");
                System.out.println("   7. Reset entire data");
                System.out.print("\nEnter your choice: ");

                choose = scanner.nextInt();
            }
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println("⛔ Inside the method App.showMenu() errors occurred!!!");
        }

        return choose;
    }

    static String input1() {
        String sw = "";
        String screen = "Enter the slang-word which you want to look up (slang-word cannot be empty): ";

        do {
            clearScreen();

            System.out.print(screen);
            sw = (new Scanner(System.in)).nextLine().trim();
        } while (sw.isEmpty());

        return sw;
    }

    static int function1(Word word, String slang_word) {
        if (word == null) {
            String screen = String.format("\uD83D\uDCAC This slang-word does not exist in the database!\n\uD83D\uDCA1 Would you like to add '%s' to the database? [Y/N]: ", slang_word);
            String confirm = "";

            do {
                clearScreen();

                System.out.print(screen);
                confirm = (new Scanner(System.in)).nextLine().trim().toLowerCase();
            } while (!confirm.equals("y") && !confirm.equals("n"));

            if (confirm.equals("y")) {
                screen += "YES";
                screen += String.format("\nEnter the '%s'\'s definition (the definition cannot be empty): ", slang_word);
                String new_def = "";

                do {
                    clearScreen();

                    System.out.print(screen);
                    new_def = (new Scanner(System.in)).nextLine().trim();
                } while (new_def.isEmpty());

                boolean catcher = dictionary.addSlang(slang_word, new_def, 4);

                if (catcher) System.out.println("\uD83D\uDCFA This word has been added to the data successfully.");
                else System.out.println("⛔ Cannot add this word to the database!");
            } else {
                clearScreen();

                screen += "NO";
                System.out.println(screen);
            }
        } else {
            String screen = "\uD83D\uDD0E The meanings of '" + word.word + "' are:";
            for (int id = 0; id < word.defs.size(); ++id) {
                screen += ("\n  \uD83D\uDD38 " + (id + 1) + ". " + word.defs.get(id));
            }
            screen += "\n\n\uD83D\uDCA1 Enter your choice:";
            screen += "\n   1. Delete";
            screen += "\n   2. Edit";
            screen += "\n   ENTER. Skip";
            String confirm = "";

            do {
                clearScreen();
                System.out.println(screen);
                confirm = (new Scanner(System.in)).nextLine().trim();
            } while (!confirm.isEmpty() && !confirm.equals("1") && !confirm.equals("2"));

            if (!confirm.isEmpty()) return Integer.parseInt(confirm);
        }

        return 0;
    }

    static String input2() {
        String def = "";
        String screen = "Enter the definition which you want to look up (definition cannot be empty): ";

        do {
            clearScreen();

            System.out.print(screen);
            def = (new Scanner(System.in)).nextLine().trim();
        } while (def.isEmpty());

        return def;
    }

    static void function2(Word definition) {
        if (definition == null) {
            clearScreen();
            System.out.println("\uD83D\uDCAC This definition does not exist in the data!");
        } else {
            String screen = "\uD83D\uDD0E Slang-words of '" + definition.word + "' are:";
            for (int id = 0; id < definition.defs.size(); ++id) {
                screen += ("\n  \uD83D\uDD38 " + (id + 1) + ". " + definition.defs.get(id));
            }

            System.out.println(screen);
        }
    }

    static String[] input4() {
        String screen = "Enter the slang-word which you need to add to the database (slang-word cannot be empty): ";
        String sw = "";
        String def = "";
        int choice = 4;

        do {
            clearScreen();

            System.out.print(screen);
            sw = (new Scanner(System.in)).nextLine().trim();
        } while (sw.isEmpty());

        screen += sw;

        if (dictionary.searchSlang(sw.toLowerCase()) != null) {
            screen += ("\n\uD83D\uDCAC '" + sw + "' was already existed in the data. Do you want...");
            screen += "\n   1. Overwrite\n   2. Duplicate\n   3. Cancel\n\nEnter your choice: ";

            do {
                clearScreen();

                System.out.print(screen);
                try {
                    choice = (new Scanner(System.in)).nextInt();
                } catch (Exception err) {
                    choice = 0;
                }
            } while (choice < 1 || choice > 3);

            if (choice == 3) return null;

            screen += Integer.toString(choice);
        }

        screen += String.format("\nEnter the '%s'\'s definition (the definition cannot be empty): ", sw);

        do {
            clearScreen();

            System.out.print(screen);
            def = (new Scanner(System.in)).nextLine().trim();
        } while (def.isEmpty());

        String[] res = {sw, def, Integer.toString(choice)};
        return res;
    }

    static void function4(String slang_word, String definition, int option) {
        boolean catcher = dictionary.addSlang(slang_word, definition, option);

        if (catcher) System.out.println("\uD83D\uDCFA This word has been added to the database successfully.");
        else System.out.println("⛔ An error occurred while adding this word to the database!");
    }

    static String[] input5() {
        String screen = "Enter your slang-word which you need to edit (slang-word cannot be empty): ";
        String sw = "";
        String new_def = "";
        int id = -1;

        do {
            clearScreen();

            System.out.print(screen);
            sw = (new Scanner(System.in)).nextLine().trim();
        } while (sw.isEmpty());

        var word = dictionary.searchSlang(sw.toLowerCase());

        if (word == null) {
            System.out.println("\uD83D\uDCAC This slang-word does not exist in the database!");
            return null;
        } else {
            screen += sw;

            if (word.defs.size() > 1) {
                screen = "\n\uD83D\uDD0E The meanings of '" + sw + "' are:";

                for (int i = 0; i < word.defs.size(); ++i) {
                    screen += ("\n  \uD83D\uDD38 " + (i + 1) + ". " + word.defs.get(i));
                }

                screen += "\nEnter the slang-word ID which you need to edit (slang-word ID cannot be empty): ";

                do {
                    clearScreen();

                    System.out.print(screen);
                    try {
                        id = (new Scanner(System.in)).nextInt();
                    } catch (Exception err) {
                        id = -1;
                    }
                } while (id < 1 || id > word.defs.size());

                screen += Integer.toString(id);
            } else {
                id = 1;
            }

            screen += "\nEnter your new definition: ";
            id -= 1;

            do {
                clearScreen();

                System.out.print(screen);
                new_def = (new Scanner(System.in)).nextLine().trim();
            } while (new_def.isEmpty());
        }

        String[] rt = { sw, new_def, Integer.toString(id) } ;
        return rt;
    }

    static void function5(String slang_word, String definition, int option) {
        dictionary.updateSlang(slang_word, definition, option);
        System.out.println("\uD83D\uDCFA This word has been updated to the database successfully.");
    }

    static String input6() {
        String screen = "Enthe the slang-word which you want to delete: ";
        String confirm = "";
        String sw = "";

        do {
            clearScreen();

            System.out.print(screen);
            sw = (new Scanner(System.in)).nextLine().trim();
        } while (sw.isEmpty());

        screen += sw;
        screen += String.format("\nDo you really want to delete '%s' forever? [Y/N]: ", sw);

        do {
            clearScreen();

            System.out.print(screen);
            confirm = (new Scanner(System.in)).nextLine().trim().toLowerCase();
        } while (!confirm.equals("y") && !confirm.equals("n"));

        if (confirm.equals("y")) {
            return sw;
        }

        return null;
    }

    static void function6(String slang_word) {
        if (dictionary.searchSlang(slang_word) != null) {
            dictionary.deleteSlang(slang_word);
            System.out.println("\uD83D\uDCFA This word has been deleted successfully.");
        } else {
            System.out.println("\uD83D\uDCAC This slang-word does not exist in the database!");
        }
    }

    static String enterKeyword(int choice) {
        String keyword = "";

        if (choice == 7) {
            String confirm = "";

            do {
                clearScreen();
                System.out.print("Do you really want to delete entire the data? [Y/N]: ");
                confirm = (new Scanner(System.in)).nextLine().trim().toLowerCase();
            } while (!confirm.equals("y") && !confirm.equals("n"));

            return confirm;
        }

        return "";
    }

    static void handle(int choice) {
        String keyword = "";
        switch (choice) {
            case 1:
                String sw = input1();
                int catcher = function1(dictionary.searchSlang(sw.toLowerCase()), sw);

                break;

            case 2:
                String def = input2();
                function2(dictionary.searchDefinition(def));
                break;

            case 3:
                dictionary.showHistory();
                break;

            case 4:
                String[] keeper = input4();
                if (keeper != null) function4(keeper[0], keeper[1], Integer.parseInt(keeper[2]));
                break;

            case 5:
                String[] keeperr = input5();
                if (keeperr != null) function5(keeperr[0], keeperr[1], Integer.parseInt(keeperr[2]));
                break;

            case 6:
                String sww = input6();
                if (sww != null) function6(sww.toLowerCase());
                break;

            case 7:
                keyword = enterKeyword(choice);

                if (keyword.equals("y")) {
                    dictionary.clearDB();
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
