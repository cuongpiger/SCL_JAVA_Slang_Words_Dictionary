import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Dictionary {
    private HashMap<String, ArrayList<String>> dict = null;
    private HashMap<String, ArrayList<String>> dict_rev = null;
    private final String db = "data/slang.txt";

    private void loadDB() {
        int i = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(db))) {
            String row;
            br.readLine();

            while ((row = br.readLine()) != null) {
                String[] splits = row.split("`", 2);
                String slang = splits[0];
                List<String> defs = Arrays.stream(splits[1].trim().split(Pattern.quote("|"))).map(String::trim).map(String::toLowerCase).collect(Collectors.toList());

                for (var def:defs) {
                    System.out.print(def + "|");
                }
                System.out.println(defs.getClass().getName());

                i += 1;

                if (i > 12) break;
            }
        } catch (IOException err) {
            System.out.println("==> Inside the method Dictionary.loadDB() errors occurred!!!");
        }
    }

    public static void main(String[] args) {
        Dictionary dict = new Dictionary();
        dict.loadDB();
    }
}
