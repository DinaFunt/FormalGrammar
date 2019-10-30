import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarUtil {

    private static int LeftPart = 0;
    private static int RightPart = 1;
    private ArrayList<String> used;

    private String grammar;

    public GrammarUtil(String grammar) {
        this.grammar = grammar;
        used = new ArrayList<>();
    }

    public void setUsingGrammar(String newGrammar) {
        grammar = newGrammar;
    }

    public void run(int num, String outputFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            if (num == 1) {
                writer.write("A1 -> 1");
                writer.flush();
                writer.close();
                return;
            }

            if (num == 2) {
                writer.write("A1 -> 11");
                writer.flush();
                writer.close();
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(grammar));
            String dur = generateBeginning(num, writer);

            String buff = "";
            String prod[];

            while ((buff = reader.readLine()) != null) {
                if (dur.matches("1*")) {
                    System.out.println("FINISHED");
                    writer.close();
                    return;
                }
                prod = buff.split(" -> ");

                if (dur.contains(prod[LeftPart].trim())) {
                    String s1 = dur.replaceFirst(
                            Pattern.quote(prod[LeftPart].trim()), Matcher.quoteReplacement(prod[RightPart].trim()));

                    StringBuilder builder = new StringBuilder(buff);
                    builder.append('\n').append(s1).append('\n').append('\n');
                    String new_out = req(s1, writer, builder, 10, s1);

                    if (new_out.equals("end")){
                        System.out.println("FINISHED");
                        writer.close();
                        return;
                    }

                    if (!new_out.equals("")) {
                        dur = new_out;
                        reader.close();
                        reader = new BufferedReader(new FileReader(grammar));
                    }
                }
            }
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String req(String str, BufferedWriter writer, StringBuilder buffer, int deep, String start) throws IOException {

        if (deep == 0) {
            writer.write(buffer.toString());
            saveUsed(buffer.toString());
            return str;
        }

        if (str.matches("1*")) {
            writer.write(buffer.toString());
            saveUsed(buffer.toString());
            return "end";
        }

        BufferedReader reader = new BufferedReader(new FileReader(grammar));
        String buff = "";
        String prod[];

        while ((buff = reader.readLine()) != null) {
            prod = buff.split(" -> ");

            if (str.contains(prod[LeftPart].trim())) {
                String new_out = str.replaceFirst(
                        Pattern.quote(prod[LeftPart].trim()), Matcher.quoteReplacement(prod[RightPart].trim()));

                if (new_out.equals(start)) {
                    return "";
                }

                buffer.append(buff).append('\n').append(new_out).append('\n').append('\n');
                return req(new_out, writer, buffer, deep - 1, start);
            }
        }

        return "";
    }

    private void saveUsed(String s) {
        String[] prods = s.split("\n");

        for (String p : prods) {
            if (p.contains("->") && !used.contains(p)) {
                used.add(p);
            }
        }
    }

    private void toFile(String newFileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFileName));
        writer.write("A1 -> 1\n");
        writer.write("A1 -> 11\n");
        writer.write("A1 -> [$,q1,1,1]A2\n");
        writer.write("A2 -> [1,1]A2\n");
        writer.write("A2 -> [1,1,@]\n");

        for (String s : used) {
            writer.write(s);
            writer.write('\n');
        }
        writer.flush();
        writer.close();
    }

    private String generateBeginning(int num, BufferedWriter writer) throws IOException {
        StringBuilder s = new StringBuilder("[$,q1,1,1]");
        writer.write("A1 -> [$,q1,1,1]A2\n");
        writer.write("[$,q1,1,1]A2\n\n");

        for (int i = 0; i < num - 2; i++) {
            s.append("[1,1]");
            writer.write("A2 -> [1,1]A2\n");
            writer.write(s + "A2\n\n");
        }

        s.append("[1,1,@]");
        writer.write("A2 -> [1,1,@]\n");
        writer.write(s + "\n\n");

        return s.toString();
    }

    public void simplify(String newFileName) {

        System.out.println("Start reachability check from the start non-terminal..");

        ArrayList<String> list = new ArrayList<>();
        list.add("A1");
        list.add("1");
        list.add("[$,q1,1,1]");
        list.add("A2");
        list.add("[1,1]");
        list.add("[1,1,@]");
        list.add("[$,1,1]");
        list.add("[q2,1,1]");
        list.add("11");
        simplify(list, newFileName);

        System.out.println("Finished");
    }

    private void simplify(ArrayList<String> visited, String newFileName) {
        try {
            ArrayList<String> n_terms = new ArrayList<>(visited);

            boolean isFinished = true;

            for (String nt1 : n_terms) {
                BufferedReader reader = new BufferedReader(new FileReader(grammar));
                skipFirstFour(reader);
                String buff = "";
                String[] prod;

                while ((buff = reader.readLine()) != null) {
                    prod = buff.split(" -> ");

                    if(prod[LeftPart].equals(nt1)) {
                        boolean flag = addNonTerminals(prod, visited);

                        if (isFinished) {
                            isFinished = flag;
                        }
                    }
                }

                for (String nt2: n_terms) {
                    BufferedReader reader2 = new BufferedReader(new FileReader(grammar));

                    while ((buff = reader2.readLine()) != null) {
                        prod = buff.split(" -> ");

                        if(prod[LeftPart].equals(nt1 + nt2)) {
                            boolean flag = addNonTerminals(prod, visited);

                            if (isFinished) {
                                isFinished = flag;
                            }
                        }
                    }
                }
            }

            if (isFinished) {
                saveUpdateGrammar(visited, newFileName);
            } else {
                simplify(visited, newFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void skipFirstFour(BufferedReader reader) throws IOException {
        reader.readLine();
        reader.readLine();
        reader.readLine();
        reader.readLine();
    }

    private boolean addNonTerminals(String[] prod, ArrayList<String> visited) {

        //System.out.println("added");
        boolean isFinished = true;

        if (prod[RightPart].contains("A2")) {
            String[] nontermR = prod[RightPart].split("A");
            nontermR[1] = "A" + nontermR[1];

            if (!visited.contains(nontermR[0])) {
                visited.add(nontermR[0]);
                isFinished = false;
            }

            if (!visited.contains(nontermR[1])) {
                visited.add(nontermR[1]);
                isFinished = false;
            }

            return isFinished;
        }

        String[] nontermR = prod[RightPart].split("]\\[");

        if (nontermR.length == 1) {
            if (!visited.contains(nontermR[0])) {
                isFinished = false;
                visited.add(nontermR[0]);
            }
        } else {
            nontermR[0] = nontermR[0] + "]";
            nontermR[1] = "[" + nontermR[1];

            if (!visited.contains(nontermR[0])) {
                isFinished = false;
                visited.add(nontermR[0]);
            }

            if (!visited.contains(nontermR[1])) {
                isFinished = false;
                visited.add(nontermR[1]);
            }
        }

        return isFinished;
    }

    private void saveUpdateGrammar(ArrayList<String> visited, String newFileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFileName));

            for (String nt1 : visited) {
                BufferedReader reader = new BufferedReader(new FileReader(grammar));
                String buff = "";
                String[] prod;

                while ((buff = reader.readLine()) != null) {
                    prod = buff.split(" -> ");

                    if(prod[LeftPart].equals(nt1)) {
                        writer.write(buff + "\n");
                    }
                }

                for (String nt2: visited) {
                    BufferedReader reader2 = new BufferedReader(new FileReader(grammar));

                    while ((buff = reader2.readLine()) != null) {
                        prod = buff.split(" -> ");

                        if(prod[LeftPart].equals(nt1 + nt2)) {
                            writer.write(buff + "\n");
                        }
                    }
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simplifyMore(String newFileName) {
        System.out.println("Start terminal reachability check..");

        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("11");
        simplifyMore(list, newFileName);

        System.out.println("Finished");
    }

    private void simplifyMore(ArrayList<String> goodTerms, String newFileName) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(grammar));
            String buff = "";

            boolean isfinished = true;

            while ((buff = reader.readLine()) != null) {

                String[] prod = buff.split(" -> ");
                String[] nonTerm = prod[RightPart].split("]\\[");

                if (!goodTerms.contains(prod[LeftPart])) {
                    if (goodTerms.contains(prod[RightPart]) || isCombinable(goodTerms, prod[RightPart])) {
                        isfinished = false;
                        goodTerms.add(prod[LeftPart]);

                    }

                    if (nonTerm.length == 2) {
                        nonTerm[0] = nonTerm[0] + "]";
                        nonTerm[1] = "[" + nonTerm[1];

                        if ((goodTerms.contains(nonTerm[0]) || isCombinable(goodTerms, nonTerm[0])) &&
                                (goodTerms.contains(nonTerm[1]) || isCombinable(goodTerms, nonTerm[1]))
                                && !goodTerms.contains(prod[LeftPart])) {
                            isfinished = false;
                            goodTerms.add(prod[LeftPart]);
                        }
                    }

                    if (prod[RightPart].contains("A2")) {
                        nonTerm = prod[RightPart].split("A");
                        nonTerm[1] = "A" + nonTerm[1];

                        if ((goodTerms.contains(nonTerm[0]) || isCombinable(goodTerms, nonTerm[0])) &&
                                (goodTerms.contains(nonTerm[1]) || isCombinable(goodTerms, nonTerm[1]))
                                && !goodTerms.contains(prod[LeftPart])) {
                            isfinished = false;
                            goodTerms.add(prod[LeftPart]);
                        }
                    }
                }

            }

            if (isfinished) {
                saveMoreUpdateGrammar(goodTerms, newFileName);
            } else {
                simplifyMore(goodTerms, newFileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isCombinable(ArrayList<String> goodTerms, String nonterm) {
        for (String nt1 : goodTerms) {
            if (goodTerms.contains(nonterm + nt1) || goodTerms.contains(nt1 + nonterm) || nt1.contains(nonterm)) {
                return true;
            }
        }

        return false;
    }

    private void saveMoreUpdateGrammar(ArrayList<String> goodTerms, String newFileName) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(newFileName));

        BufferedReader reader = new BufferedReader(new FileReader(grammar));
        String buff = "";

        while ((buff = reader.readLine()) != null) {
            String[] prod = buff.split(" -> ");
            String[] nonTerm = prod[RightPart].split("]\\[");

            if (goodTerms.contains(prod[RightPart]) || isCombinable(goodTerms, prod[RightPart])) {
                writer.write(buff);
                writer.write('\n');

            }

            if (nonTerm.length == 2) {
                nonTerm[0] = nonTerm[0] + "]";
                nonTerm[1] = "[" + nonTerm[1];

                if ((goodTerms.contains(nonTerm[0]) || isCombinable(goodTerms, nonTerm[0])) &&
                        (goodTerms.contains(nonTerm[1]) || isCombinable(goodTerms, nonTerm[1]))) {
                    writer.write(buff);
                    writer.write('\n');
                }
            }

            if (prod[RightPart].contains("A2")) {
                nonTerm = prod[RightPart].split("A");
                nonTerm[1] = "A" + nonTerm[1];

                if ((goodTerms.contains(nonTerm[0]) || isCombinable(goodTerms, nonTerm[0])) &&
                        (goodTerms.contains(nonTerm[1]) || isCombinable(goodTerms, nonTerm[1]))) {
                    writer.write(buff);
                    writer.write('\n');
                }
            }
        }

        writer.flush();
        writer.close();
    }

    public void lastUpdate(String newFileName) {
        System.out.println("Start finding useful productions..");

        run(11, "test.txt");
        run(13, "test.txt");
        run(17, "test.txt");
        run(19, "test.txt");

        System.out.println("Finished");
        try {
            toFile(newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}