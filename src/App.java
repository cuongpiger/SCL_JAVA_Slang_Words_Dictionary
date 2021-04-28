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
            while (choose < 1 || choose > 4) {
                clearScreen();

                System.out.println("\uD83D\uDCD6 SLANG-WORD DICTIONARY \uD83D\uDCD6");
                System.out.println("   1. Search based on slang-word");
                System.out.println("   2. Search based on definition");
                System.out.println("   3. Show search history");
                System.out.println("   4. Add a new slang-word");
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

            if (choose == 3){
                return "";
            } else {
                return keyword.trim() + "`" + choose + "`" + def;
            }
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


//        var dict = dictionary.get_dict();
//        var dict_rev = dictionary.get_dict_rev();
//
//        for (var key : dict.keySet()) {
//            System.out.print(key + " --> ");
//
//            var defs = dict.get(key).defs;
//
//            for (var def : defs) {
//                System.out.print(def + ", ");
//            }
//
//            System.out.println();
//        }
//        System.out.println();
//
//        for (var key : dict_rev.keySet()) {
//            System.out.print(key + " --> ");
//
//            var defs = dict_rev.get(key).defs;
//
//            for (var def : defs) {
//                System.out.print(def + ", ");
//            }
//
//            System.out.println();
//        }
//        String string = "I will come and meet you at the 123woods";
//        String keyword = "123woods";
//
//        Boolean found = Arrays.asList(string.split(" ")).contains(keyword);
//        if(found){
//            System.out.println("Keyword matched the string");
//        }
    }
}
