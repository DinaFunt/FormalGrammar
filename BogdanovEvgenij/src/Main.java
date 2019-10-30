
import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        switch (args[0]) {
            case "-d":
                GrammarUtil util = new GrammarUtil(args[2]);
                util.run(Integer.parseInt(args[1]), args[1] + ".txt");
                break;
            case "-b":
                try {
                    makeOptimalGrammar("lba_3.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private static void makeOptimalGrammar(String lba) throws IOException {
        String n1 = "firstImprovement.txt";
        String n2 = "secondImprovement.txt";
        String n3 = "final.txt";

        CSGrammar g = new CSGrammar(lba, "base.txt");
        GrammarUtil util = new GrammarUtil(g.getGrammar());

        util.simplify(n1);
        util.setUsingGrammar(n1);

        util.simplifyMore(n2);
        util.setUsingGrammar(n2);

        util.lastUpdate(n3);
        System.out.println("results saved in " + n3);
    }
}
