public class App {
    static Dictionary dictionary;

    static {
        dictionary = new Dictionary();
        dictionary.loadDB();
    }

    public static void main(String[] args) {
        var dict = dictionary.get_dict();
        var dict_rev = dictionary.get_dict_rev();

        for (var key : dict.keySet()) {
            System.out.print(key + " --> ");

            var defs = dict.get(key).defs;

            for (var def : defs) {
                System.out.print(def + ", ");
            }

            System.out.println();
        }
        System.out.println();

        for (var key : dict_rev.keySet()) {
            System.out.print(key + " --> ");

            var defs = dict_rev.get(key).defs;

            for (var def : defs) {
                System.out.print(def + ", ");
            }

            System.out.println();
        }
//        String string = "I will come and meet you at the 123woods";
//        String keyword = "123woods";
//
//        Boolean found = Arrays.asList(string.split(" ")).contains(keyword);
//        if(found){
//            System.out.println("Keyword matched the string");
//        }
    }
}
