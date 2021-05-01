import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Word implements Serializable {
    String word;
    List<String> defs;

    Word(String w, List<String> d) {
        word = w;
        defs = d;
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", defs='" + defs + '\'' +
                "}\n";
    }
}

class History implements Serializable {
    String key;
    String word;

    History(String k, String w) {
        key = k;
        word = w;
    }

    @Override
    public String toString() {
        return "History{" +
                "key='" + key + '\'' +
                ", word='" + word + '\'' +
                "}\n";
    }
}

public class Dictionary {
    private static HashMap<String, Word> dict;
    private static HashMap<String, Word> dict_rev;
    private static ArrayList<History> history_dict;
    private static ArrayList<History> history_dict_rev;
    private static final String db = "DATABASES/slang.txt";
    private static final String db_history_dict = "DATABASES/db_history_dict.ser";
    private static final String db_history_dict_rev = "DATABASES/db_history_dict_rev.ser";
    private static final String db_dict = "DATABASES/db_dict.ser";
    private static final String db_dict_rev = "DATABASES/db_dict_rev.ser";


    Dictionary() {
        dict = (HashMap<String, Word>) loadDB(db_dict);
        dict_rev = (HashMap<String, Word>) loadDB(db_dict_rev);
        history_dict = (ArrayList<History>) loadDB(db_history_dict);
        history_dict_rev = (ArrayList<History>) loadDB(db_history_dict_rev);

        // generate if they are null value
        if (dict == null) dict = new HashMap<>();
        if (dict_rev == null) dict_rev = new HashMap<>();
        if (history_dict == null) history_dict = new ArrayList<>();
        if (history_dict_rev == null) history_dict_rev = new ArrayList<>();
    }

    public HashMap<String, Word> get_dict() {
        return dict;
    }

    public HashMap<String, Word> get_dict_rev() {
        return dict_rev;
    }

    @Deprecated
    public void loadDBFormTxtFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(db))) {
            String row;
            br.readLine();

            while ((row = br.readLine()) != null) {
                String[] splits = row.split("`", 2);

                if (splits.length != 2) {
                    splits = new String[]{splits[0], ""};
                }

                String slang_key = splits[0].trim().toLowerCase();
                String slang_ori = splits[0];
                List<String> defs_ori = Arrays.stream(splits[1].split(Pattern.quote("|"))).map(String::trim).collect(Collectors.toList());
                List<String> defs_key = defs_ori.stream().map(String::toLowerCase).collect(Collectors.toList());

                if (!dict.containsKey(slang_key)) {
                    Word word = new Word(slang_ori, defs_ori);
                    dict.put(slang_key, word);
                } else {
                    Word word = dict.get(slang_key);
                    word.defs.addAll(defs_ori);
                }

                for (int i = 0; i < defs_key.size(); ++i) {
                    if (!dict_rev.containsKey(defs_key.get(i))) {
                        Word word = new Word(defs_ori.get(i), new ArrayList<String>(Arrays.asList(slang_key)));
                        dict_rev.put(defs_key.get(i), word);
                    } else {
                        Word word = dict_rev.get(defs_key.get(i));
                        word.defs.add(slang_key);
                    }
                }
            }
        } catch (IOException err) {
            System.out.println("⛔ Inside the method Dictionary.loadDB() errors occurred!!!");
        }
    }

    private Object loadDB(String db_name) {
        try {
            FileInputStream fis = new FileInputStream(db_name);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Object res = ois.readObject();

            ois.close();
            fis.close();

            return res;
        } catch (Exception err) {
            return null;
        }
    }

    private boolean saveDB(String db_name, Object obj) {
        try {
            FileOutputStream fos = new FileOutputStream(db_name);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);

            oos.flush();
            oos.close();
            fos.close();
        } catch (IOException err) {
            return false;
        }

        return true;
    }

    private boolean clearDB(String db_name) {
        try {
            Path p = Paths.get(db_name);
            Files.deleteIfExists(p);
        } catch (IOException err) {
            return false;
        }

        return true;
    }

    public void clearEntireDB() {
        String[] dbs = {db_dict, db_dict_rev, db_history_dict, db_history_dict_rev};

        for (var db : dbs) {
            clearDB(db);
        }
    }

    public Word searchSlang(String slang_word) {
        // if `slang_word` exists in `dict`
        if (dict.containsKey(slang_word)) {
            var word = dict.get(slang_word);

            // save `slang_word` to the history
            history_dict.add(new History(slang_word, word.word));
            if (history_dict.size() > 10) history_dict.remove(0); // remove the oldest element if array is overflow
            saveDB(db_history_dict, history_dict); // save to the database

            return word;
        }

        return null;
    }

    public Word searchDefinition(String definition) {
        // if `definition` exists in `dict_rev`
        if (dict_rev.containsKey(definition)) {
            var word = dict_rev.get(definition);

            // save `definition` to the history
            history_dict_rev.add(new History(definition, word.word));
            if (history_dict_rev.size() > 10)
                history_dict_rev.remove(0); // remove the oldest element if array is overflow

            saveDB(db_history_dict_rev, history_dict_rev);

            return word;
        }

        return null;
    }

    public void showHistory() {
        if (history_dict == null && history_dict_rev == null) {
            System.out.println("\uD83D\uDCAC Program's history is empty!!!.");
            return;
        }

        System.out.println("10 most recent search words:");
        System.out.format("%20s%40s%n", "Slang-word", "Definition");
        int lim = Math.max(history_dict.size(), history_dict_rev.size());

        for (int i = lim - 1; i >= 0; --i) {
            String slang = i < history_dict.size() ? history_dict.get(i).word : "";
            String def = i < history_dict_rev.size() ? history_dict_rev.get(i).word : "";
            System.out.format("%40s%40s%n", slang, def);
        }
    }

    public boolean addSlang(String word, String def, int option) {
        if (option == 1) { // overwrite
            var word_dict = dict.get(word.toLowerCase()); // find

            for (var d : word_dict.defs) {
                var tmp = dict_rev.get(d);

                if (tmp.defs.size() == 1) dict_rev.remove(d);
                else tmp.defs.remove(word.toLowerCase());
            }

            word_dict.defs.clear();
            word_dict.defs.add(def);

            var new_word_rev = new Word(def, new ArrayList<String>(Arrays.asList(word.toLowerCase())));
            dict_rev.put(def.toLowerCase(), new_word_rev);
        } else if (option == 2) { // duplicate
            var defs = dict.get(word.toLowerCase()).defs;

            for (var d : defs) {
                if (d.equals(def)) return false;
            }

            defs.add(def);

            if (dict_rev.containsKey(def.toLowerCase())) {
                dict_rev.get(def.toLowerCase()).defs.add(word.toLowerCase());
            } else {
                Word new_word = new Word(def, new ArrayList<String>(Arrays.asList(word.toLowerCase())));
                dict_rev.put(def.toLowerCase(), new_word);
            }
        } else if (option == 4) { // new word
            Word new_word = new Word(word, new ArrayList<String>(Arrays.asList(def)));
            dict.put(word.toLowerCase(), new_word);

            if (dict_rev.containsKey(def.toLowerCase())) {
                dict_rev.get(def.toLowerCase()).defs.add(word.toLowerCase());
            } else {
                Word new_word_rev = new Word(def, new ArrayList<String>(Arrays.asList(word.toLowerCase())));
                dict_rev.put(def.toLowerCase(), new_word_rev);
            }
        }

        saveDB(db_dict, dict);
        saveDB(db_dict_rev, dict_rev);

        return true;
    }

    public void updateSlang(String word, String n_def, int id) {
        var dict_word = dict.get(word.toLowerCase());
        var old_def = dict_word.defs.get(id);
        var dict_rev_word = dict_rev.get(old_def.toLowerCase());

        if (dict_rev_word.defs.size() == 1) dict_rev.remove(old_def.toLowerCase());
        else dict_rev_word.defs.remove(word.toLowerCase());

        if (dict_rev.containsKey(n_def.toLowerCase())) dict_rev.get(n_def.toLowerCase()).defs.add(word.toLowerCase());
        else {
            var new_dict_rev_word = new Word(n_def.toLowerCase(), new ArrayList<String>(Arrays.asList(word.toLowerCase())));
            dict_rev.put(n_def.toLowerCase(), new_dict_rev_word);
        }

        dict_word.defs.set(id, n_def);

        saveDB(db_dict, dict);
        saveDB(db_dict_rev, dict_rev);
    }

    public void deleteSlang(String word) {
        var defs_dict = dict.get(word).defs;

        for (var def : defs_dict) {
            def = def.toLowerCase();
            var defs_dict_rev = dict_rev.get(def).defs;

            if (defs_dict_rev.size() == 1) {
                dict_rev.remove(def);
            } else {
                defs_dict_rev.remove(word);
            }
        }

        dict.remove(word);
        saveDB(db_dict, dict);
        saveDB(db_dict_rev, dict_rev);
    }

    public Word onThisDaySlangWord() {
        Random generator = new Random();
        Object[] values = dict.values().toArray();
        return (Word) values[generator.nextInt(values.length)];
    }

    public void gameSlang() {
        Random generator = new Random();
        Object[] slangs = dict.values().toArray();
        Object[] defs = dict_rev.values().toArray();
        int choice = -1;
        String tmp = "";

        while (choice == -1) {
            Word word = (Word) slangs[generator.nextInt(slangs.length)];
            String[] answers = {"", "", "", ""};

            var word_word = word.word;
            var word_answer = word.defs.get(generator.nextInt(word.defs.size()));
            int i = 0;

            while (i < 4) {
                var tmp_def = (Word) defs[generator.nextInt(defs.length)];

                if (word_answer != tmp_def.word) {
                    answers[i++] = tmp_def.word;
                }
            }

            answers[generator.nextInt(4)] = word_answer;
            String screen = "\uD83D\uDCFA Enter ':exit' when you would like to stop";
            screen += "\nSlang-word: " + word_word;

            for (int id = 0; id < 4; ++id) {
                screen += "\n   " + (id + 1) + ". " + answers[id];
            }

            screen += "\n\nEnter your choice: ";

            do {
                App.clearScreen();
                System.out.print(screen);
                try {
                    tmp = (new Scanner(System.in)).nextLine().trim().toLowerCase();

                    if (tmp.equals(":exit")) return;

                    choice = Integer.parseInt(tmp);
                } catch (Exception err) {
                    choice = 5;
                }
            } while (choice == 5);

            if (!answers[choice - 1].equals(word_answer)) {
                App.clearScreen();

                screen += "\n\uD83D\uDCFA Wrong, correct answer is '" + word_answer + "'";
                System.out.println(screen);
            }

            choice = -1;
            App.pressEnter();
        }
    }

    public void gameDefinition() {
        Random generator = new Random();
        Object[] defs = dict_rev.values().toArray();
        Object[] slangs = dict.values().toArray();
        int choice = -1;
        String tmp = "";

        while (choice == -1) {
            Word word = (Word) defs[generator.nextInt(defs.length)];
            String[] answers = {"", "", "", ""};

            var word_word = word.word;
            var word_answer = word.defs.get(generator.nextInt(word.defs.size()));
            int i = 0;

            while (i < 4) {
                var tmp_slang = (Word) slangs[generator.nextInt(slangs.length)];

                if (word_answer != tmp_slang.word) {
                    answers[i++] = tmp_slang.word;
                }
            }

            answers[generator.nextInt(4)] = word_answer;
            String screen = "\uD83D\uDCFA Enter ':exit' when you would like to stop";
            screen += "\nSlang-word: " + word_word;

            for (int id = 0; id < 4; ++id) {
                screen += "\n   " + (id + 1) + ". " + answers[id];
            }

            screen += "\n\nEnter your choice: ";

            do {
                App.clearScreen();
                System.out.print(screen);
                try {
                    tmp = (new Scanner(System.in)).nextLine().trim().toLowerCase();

                    if (tmp.equals(":exit")) return;

                    choice = Integer.parseInt(tmp);
                } catch (Exception err) {
                    choice = 5;
                }
            } while (choice == 5);

            if (!answers[choice - 1].equals(word_answer)) {
                App.clearScreen();

                screen += "\n\uD83D\uDCFA Wrong, correct answer is '" + word_answer + "'";
                System.out.println(screen);
            }

            choice = -1;
            App.pressEnter();
        }
    }
}
