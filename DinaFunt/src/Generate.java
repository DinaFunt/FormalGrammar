import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Generate {
    private String in;
    private String out;
    private ArrayList<String> states;

    public Generate(String in, String out) {
        this.in = in;
        this.out = out;
        this.states = new ArrayList<>();
    }

    private String[] AddElement(String[] a, String[] e) {
        for (String var : e) {
            a = Arrays.copyOf(a, a.length + 1);
            a[a.length - 1] = var;
        }
        return a;
    }

    public void CreateGrammar() {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(in));
            reader.readLine();
            String[] sigma = reader.readLine().split(" ");
            String[] gamma = AddElement(reader.readLine().split(" "), sigma);

            states.add("A0 - (e, B) A1");
            states.add("A1 - " + reader.readLine() + " A2");
            for (String a : sigma) {
                states.add("A2 - (" + a + ", " + a + ") A2");
            }

            String[] fin = reader.readLine().split(" ");

            states.add("A2 - A3");
            states.add("A3 - (e, B) A3");
            states.add("A3 - e");

            sigma = AddElement(sigma, new String[]{"e"});

            String s = reader.readLine();
            while (s != null) {
                if (!s.equals("")) {
                    String[] part = s.split(" -> ");
                    String[] from = part[0].split(" ");
                    String[] to = part[1].split(" ");

                    if (to[2].equals("r")) {
                        for (String a : sigma) {
                            states.add(from[0] + " (" + a + ", " + from[1] + ") - (" + a + ", " + to[1] + ") " + to[0]);
                        }
                    } else {
                        for (String a : sigma) {
                            for (String b : sigma) {
                                for (String C : gamma) {
                                    states.add("(" + b + ", " + C + ") " + from[0] + " (" + a + ", " + from[1] + ") -"
                                            + " " + to[0] + " (" + b + ", " + C + ") (" + a + ", " + to[1] + ")");
                                }
                            }
                        }
                    }
                }
                s = reader.readLine();
            }

            for (String q : fin) {
                for (String C : gamma) {
                    for (String a : sigma) {
                        states.add("(" + a + ", " + C + ") " + q + " - " + q + " " + a + " " + q);
                        states.add(q + " " + "(" + a + ", " + C + ") - " + q + " " + a + " " + q);
                    }
                }
            }

            for (String q : fin) {
                states.add(q + " - e");
            }

            states = Optimize();
            WriteToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WriteToFile() throws IOException {
        FileWriter writer = new FileWriter(out);
        for (String str : states) {
            writer.append(str + "\n");
        }
        writer.flush();
        writer.close();
    }

    private ArrayList<String> Optimize() {
        int startParam = 2;
        int endParam = 17;
        ArrayList<String> fin = new ArrayList<>();
        for (int i = startParam; i < endParam; i++) {
            AddProds(i, fin);
        }
        return fin;
    }

    private void AddProds(int num, ArrayList<String> fin) {

        RunGrammar rg = new RunGrammar(states);
        ArrayList<String> usedProds = rg.getUsedProds(num);

        for (String str : usedProds) {
            if (!fin.contains(str)) {
                fin.add(str);
            }
        }
    }
}
