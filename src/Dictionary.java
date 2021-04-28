import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Word {
    String word;
    List<String> defs;

    Word(String w, List<String> d) {
        word = w;
        defs = d;
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
    private static HashMap<String, Word> dict = null;
    private static HashMap<String, Word> dict_rev = null;
    private static ArrayList<History> history_dict = null;
    private static ArrayList<History> history_dict_rev = null;
    private static final String db = "DATABASES/slang_.txt";
    private static final String db_history_dict = "DATABASES/db_history_dict.ser";
    private static final String db_history_dict_rev = "DATABASES/db_history_dict_rev.ser";

    Dictionary() {
        dict = new HashMap<String, Word>();
        dict_rev = new HashMap<String, Word>();
        history_dict = readFromHistory(db_history_dict);
        history_dict_rev = readFromHistory(db_history_dict_rev);

        if (history_dict == null) {
            history_dict = new ArrayList<>();
        }

        if (history_dict_rev == null) {
            history_dict_rev = new ArrayList<>();
        }
    }

    public HashMap<String, Word> get_dict() {
        return dict;
    }

    public HashMap<String, Word> get_dict_rev() {
        return dict_rev;
    }

    public void loadDB() {
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

    public void searchBasedSlang(String keyword) {
        if (dict.containsKey(keyword)) {
            var word = dict.get(keyword);

            System.out.println("\uD83D\uDD0E The meaning of '" + word.word + "' is:");

            int id = 0;
            for (var def : word.defs) {
                System.out.println("  \uD83D\uDD38 " + ++id + ". " + def);
            }

            // save to history
            history_dict.add(new History(keyword, word.word));

            if (history_dict.size() > 10) {
                history_dict.remove(0);
            }

            saveToHistory(db_history_dict, history_dict);
        } else {
            System.out.println("\uD83D\uDCAC Your word does not exist in the data!!!");
        }
    }

    public void searchBasedDefinition(String keyword) {
        if (dict_rev.containsKey(keyword)) {
            var word = dict_rev.get(keyword);

            System.out.println("\uD83D\uDD0E The slang-word of '" + word.word + "' is:");
            int id = 0;

            for (var def : word.defs) {
                System.out.println("  \uD83D\uDD38 " + ++id + ". " + dict.get(def).word);
            }

            history_dict_rev.add(new History(keyword, word.word));

            if (history_dict_rev.size() > 10) {
                history_dict_rev.remove(0);
            }

            saveToHistory(db_history_dict_rev, history_dict_rev);

        } else {
            System.out.println("\uD83D\uDCAC Your definition does not exist in the data!!!");
        }
    }

    public void saveToHistory(String db_name, ArrayList<History> history) {
        try {
            FileOutputStream writeData = new FileOutputStream(db_name);
            ObjectOutputStream writeStream = new ObjectOutputStream(writeData);

            writeStream.writeObject(history);
            writeStream.flush();
            writeStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<History> readFromHistory(String db_name) {
        try {
            FileInputStream readData = new FileInputStream(db_name);
            ObjectInputStream readStream = new ObjectInputStream(readData);

            ArrayList<History> dict = (ArrayList<History>) readStream.readObject();
            readStream.close();

            return dict;
        } catch (Exception e) {
            return null;
        }
    }

    public void showHistory() {
        System.out.println("10 most recent search words:");
        System.out.format("%20s%40s%n", "Slang-word", "Definition");
        int lim = Math.max(history_dict.size(), history_dict_rev.size());

        for (int i = 0; i < lim; ++i) {
            String slang = i < history_dict.size() ? history_dict.get(i).word : "";
            String def = i < history_dict_rev.size() ? history_dict_rev.get(i).word : "";
            System.out.format("%40s%40s%n", slang, def);
        }
    }

    public void addSlangWord(String word, String def, int option) {
        if (option == 1) {
            var defs = dict.get(word.toLowerCase()).defs;
            defs.clear();
            defs.add(def);

            Word tmp_word = dict_rev.get(word);

            for (var d : tmp_word.defs) {
                if (d.equals(def)) {
                    System.out.println("\uD83D\uDCAC You defined it!!!");

                    return;
                }
            }

            tmp_word.defs.add(def);
        } else if (option == 2) {
            var defs = dict.get(word.toLowerCase()).defs;
            defs.add(def);

            Word tmp_word = dict_rev.get(word);
            tmp_word.defs.clear();
            tmp_word.defs.add(def);
        } else if (option == 4) {
            Word new_word = new Word(word, new ArrayList<String>(Arrays.asList(def)));
            dict.put(word.toLowerCase(), new_word);

            Word new_word_rev = new Word(def, new ArrayList<String>(Arrays.asList(word.toLowerCase())));

            if (dict_rev.containsKey(def.toLowerCase())) {
                dict_rev.get(def.toLowerCase()).defs.add(word.toLowerCase());
            } else {
                dict_rev.put(def.toLowerCase(), new_word_rev);
            }
        }

        System.out.println("\uD83D\uDCFA This word has been added to the data successfully.");
    }

    public void updateSlang(String word, int id, String n_def) {
        var defs = dict.get(word).defs;
        var del_def = defs.get(id);

        if (defs.contains(n_def)) {
            defs.remove(id);

            dict_rev.get(del_def.toLowerCase()).defs.remove(dict_rev.get(del_def.toLowerCase()).defs.indexOf(word.toLowerCase()));

            if (dict_rev.get(del_def.toLowerCase()).defs.size() == 0) {
                dict_rev.remove(del_def.toLowerCase());
            }

            System.out.println("\uD83D\uDCFA This word has been edited successfully.");

            return;
        }

        defs.set(id, n_def);

        if (!dict_rev.containsKey(n_def.toLowerCase())) {
            Word new_word = new Word(n_def, new ArrayList<String>(Arrays.asList(word.toLowerCase())));
            dict_rev.put(n_def.toLowerCase(), new_word);
        } else {
            var tmp_word = dict_rev.get(n_def);
            tmp_word.defs.set(tmp_word.defs.indexOf(word.toLowerCase()), word.toLowerCase());
        }

        System.out.println("\uD83D\uDCFA This word has been edited successfully.");
    }
}
