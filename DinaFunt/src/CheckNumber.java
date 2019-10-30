import java.io.*;

public class CheckNumber {
    private String grammar;
    private String derivation;
    private String currentWord;
    private StringBuilder sb;

    public CheckNumber(String grammar) {
        this.grammar = grammar;
        this.derivation = "derivation.txt";
        this.currentWord = "A0"; //start symbol
        sb = new StringBuilder();
    }

    public boolean isAPrime(int number) {

        constructNumber(number);
        int depth = 3000;
        run(depth);

        currentWord = currentWord.replace("e", "").replace(" ", "");
//        System.out.println(currentWord);

        if (currentWord.matches("1*")) {
            try {
                FileWriter fw = new FileWriter(derivation);
                fw.write(sb.toString());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    private void constructNumber(int number) {
        BufferedReader reader;

        try {
            String s1;
            reader = new BufferedReader(new FileReader(grammar));

            s1 = reader.readLine();
            applyProd(s1);

            s1 = reader.readLine();
            applyProd(s1);

            s1 = reader.readLine();
            while (number != 0) {
                applyProd(s1);
                number--;
            }

            s1 = reader.readLine();
            applyProd(s1);

            s1 = reader.readLine();
            applyProd(s1);

            s1 = reader.readLine();
            applyProd(s1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyProd(String s1) {
        String[] parts;
        parts = s1.split(" - ");
        currentWord = currentWord.replace(parts[0], parts[1]);
        sb.append(s1 + "\n");
        sb.append(currentWord + "\n\n");
    }

    private void run(int depth) {
        if (depth == 0) {
            System.out.println("Your number is too big");
            System.exit(-1);
        }

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(grammar));
            String s = reader.readLine();

            String[] parts;
            while (s != null) {
                parts = s.split(" - ");
                if (currentWord.contains(parts[0])) {
                    applyProd(s);
                    run(--depth);
                    return;
                }

                s = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
