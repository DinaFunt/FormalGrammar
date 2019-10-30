import java.util.ArrayList;

public class RunGrammar {
    private String currentWord;
    private ArrayList<String> grammar;
    private ArrayList<String> prods;

    public RunGrammar(ArrayList<String> grammar) {
        this.grammar = grammar;
        this.currentWord = "A0";
        this.prods = new ArrayList<>();
    }

    public ArrayList<String> getUsedProds(int number) {
        int depth = 2000;
        constructNumber(number);
        run(depth);
        return prods;
    }

    private void constructNumber(int number) {
        String s1;

        s1 = grammar.get(0);
        applyProd(s1);

        s1 = grammar.get(1);
        applyProd(s1);

        s1 = grammar.get(2);
        while (number != 0) {
            applyProd(s1);
            number--;
        }

        s1 = grammar.get(3);
        applyProd(s1);

        s1 = grammar.get(4);
        applyProd(s1);

        s1 = grammar.get(5);
        applyProd(s1);
    }

    private void applyProd(String s) {
        String[] parts;
        parts = s.split(" - ");
        currentWord = currentWord.replace(parts[0], parts[1]);
        prods.add(s);
    }

    private void run(int depth) {
        if (depth == 0) {
            System.out.println("Your number is too big");
            System.exit(-1);
        }

        String[] parts;
        for (String s : grammar) {
            parts = s.split(" - ");
            if (currentWord.contains(parts[0])) {
                applyProd(s);
                run(--depth);
                return;
            }
        }
    }
}
