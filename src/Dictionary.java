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
    private HashMap<String, Word> dict = null;
    private HashMap<String, String> dict_rev = null;
    private final String db = "data/slang_.txt";

    Dictionary() {
        dict = new HashMap<String, Word>();
        dict_rev = new HashMap<String, String>();
    }

    private void loadDB() {
        try (BufferedReader br = new BufferedReader(new FileReader(db))) {
            String row;
            br.readLine();

            while ((row = br.readLine()) != null) {
                String[] splits = row.split("`", 2);

                if (splits.length != 2) {
                    splits = new String[] {splits[0], ""};
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

                for (var def: defs_key) {
                    if (!dict_rev.containsKey(def)) {
                        dict_rev.put(def, slang_key);
                    }
                }
            }
        } catch (IOException err) {
            System.out.println("==> Inside the method Dictionary.loadDB() errors occurred!!!");
        }
    }

    public static void main(String[] args) {
        Dictionary dict = new Dictionary();
        dict.loadDB();

//        String string = "I will come and meet you at the 123woods";
//        String keyword = "123woods";
//
//        Boolean found = Arrays.asList(string.split(" ")).contains(keyword);
//        if(found){
//            System.out.println("Keyword matched the string");
//        }
    }
}
