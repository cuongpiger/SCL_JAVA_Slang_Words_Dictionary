import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

public class Dictionary {
    private static HashMap<String, Word> dict = null;
    private static HashMap<String, Word> dict_rev = null;
    private static final String db = "DATABASES/slang.txt";

    Dictionary() {
        dict = new HashMap<String, Word>();
        dict_rev = new HashMap<String, Word>();
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
            System.out.println("==> Inside the method Dictionary.loadDB() errors occurred!!!");
        }
    }

    public void searchBasedSlang(String keyword) {
        if (dict.containsKey(keyword)) {
            var word = dict.get(keyword);

            System.out.println("\uD83D\uDD0E The meaning of '" + word.word + "' is:");
            for (var def: word.defs) {
                System.out.println("  \uD83D\uDD38 " + def);
            }
        } else {
            System.out.println("\uD83D\uDCAC Your word does not exist in the data!!!");
        }
    }

    public void searchBasedDefinition(String keyword) {
        if (dict_rev.containsKey(keyword)) {
            var word = dict_rev.get(keyword);

            System.out.println("\uD83D\uDD0E The slang-word of '" + word.word + "' is:");
            for (var def: word.defs) {
                System.out.println("  \uD83D\uDD38 " + dict.get(def).word);
            }
        } else {
            System.out.println("\uD83D\uDCAC Your definition does not exist in the data!!!");
        }
    }
}
