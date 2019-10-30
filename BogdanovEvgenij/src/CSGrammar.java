import java.io.*;

public class CSGrammar {
    private String[] gamma;
    private String fileName;
    private StringBuilder builder;

    public CSGrammar(String lbaPath, String fileName) throws IOException {
        System.out.println("Start creating grammar from LBA..");
        builder = new StringBuilder();
        makeGrammar(lbaPath);

        this.fileName = fileName;
        saveToFile(fileName);

        System.out.println("Finished");
    }

    private void saveToFile(String outputName) throws IOException {
        FileWriter writer = new FileWriter(new File(outputName));
        writer.write(builder.toString());
        writer.close();
    }

    private void makeGrammar(String lbaPath) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(lbaPath));
        gamma = reader.readLine().split(" ");
        String buff = "";

        addStartProductions();

        while ((buff = reader.readLine()) != null) {
            if (buff.equals("") || buff.charAt(0) == '-' || buff.charAt(1) == ':') {
                continue;
            }

            Transition transition = new Transition(buff);
            addNotAdmittingProductions(transition);
        }

        addAdmittingProductions();

    }

    private void addStartProductions() {
        builder.append("A1 -> 1\n");
        builder.append("A1 -> [$,q1,1,1]A2\n");
        builder.append("A2 -> [1,1]A2\n");
        builder.append("A2 -> [1,1,@]\n");
    }

    private void addNotAdmittingProductions(Transition transition) {
        switch (transition.getReadSymbol()) {
            case "$":
                for (String X: gamma) {
                    String s = "[q" + transition.getCurrState() + ",$,"
                            + X + ",1] -> [$,q"
                            + transition.getNewState() + "," + X + ",1]\n"; //5.1

                    builder.append(s);
                }

                break;
            case "@":

                for (String X : gamma) {
                    String s1 = "[" + X + ",1,q"
                            + transition.getCurrState() + ",@] -> [q"
                            + transition.getNewState() + "," + X + ",1,@]\n"; //7.2

                    builder.append(s1);
                }

                break;
            default:
                if (transition.getDir().equals("l")) {
                    addLeftProduction(transition);
                } else {
                    addRightProduction(transition);
                }

                break;
        }
    }

    private void addLeftProduction(Transition transition) {
        String s2 = "[$,q" + transition.getCurrState() + "," + transition.getReadSymbol() +
                ",1] -> [q" + transition.getNewState() + ",$," + transition.getY() + ",1]\n";

        builder.append(s2);

        for (String Z :gamma) {
            String s3 = "[" + Z  +",1][q"
                    + transition.getCurrState() + "," + transition.getReadSymbol() +
                    ",1] -> [q" + transition.getNewState() + "," + Z + ",1]" +
                    "[" + transition.getY() + ",1]\n"; //6.2

            String s4 = "[$," + Z  +",1][q"
                    + transition.getCurrState() + "," + transition.getReadSymbol() +
                    ",1] -> [$,q" + transition.getNewState() + "," + Z + ",1]" +
                    "[" + transition.getY() + ",1]\n";  //6.4

            String s5 = "[" + Z  +",1][q"
                    + transition.getCurrState() + "," + transition.getReadSymbol() +
                    ",1,@] -> [q" + transition.getNewState() + "," + Z + ",1]" +
                    "[" + transition.getY() + ",1,@]\n"; //7.3


            builder.append(s3).append(s4).append(s5);
        }
    }

    private void addRightProduction(Transition transition) {
        String s2 = "[q"+ transition.getCurrState() + "," + transition.getReadSymbol() +
                ",1,@] -> [" + transition.getY() + ",1,q" + transition.getNewState() + ",@]\n";

        builder.append(s2);

        for (String Z :gamma) {
            String s3 = "[$,q" + transition.getCurrState() + "," +
                    transition.getReadSymbol() + ",1][" + Z + ",1] -> [$," +
                    transition.getY() + ",1][q" + transition.getNewState() + "," + Z + ",1]\n"; //5.3

            String s4 = "[q" + transition.getCurrState()  + "," + transition.getReadSymbol()  +",1]["
                    + Z + ",1] -> [" + transition.getY() + ",1][q" + transition.getNewState() + "," + Z + ",1]\n"; //6.1 ?

            String s5 = "[q" + transition.getCurrState() + "," + transition.getReadSymbol()  +",1]["
                    + Z + ",1,@] -> [" + transition.getY() + ",1]" +
                    "[q" + transition.getNewState() + "," + Z + ",1,@]\n"; //6.3

            builder.append(s3).append(s4).append(s5);
        }
    }

    private void addAdmittingProductions() {
        for (String X : gamma) {
            String s1 = "[q888,$," + X + ",1] -> 1\n";
            String s2 = "[$,q888," + X + ",1] -> 1\n";

            String s3 = "[q888," + X + ",1] -> 1\n";

            String s4 = "[q888," + X + ",1,@] -> 1\n";
            String s5 = "[" + X + ",1,q888,@] -> 1\n";

            String s6 = "1[" + X + ",1] -> 11\n";
            String s7 = "1[" + X + ",1,@] -> 11\n";

            String s8 = "[" + X + ",1]1 -> 11\n";
            String s9 = "[$," + X + ",1]1 -> 11\n";

            builder.append(s1).append(s2).append(s3).append(s4).append(s5)
                    .append(s5).append(s6).append(s7).append(s8).append(s9);
        }
    }

    public String getGrammar() {
        return fileName;
    }
}
