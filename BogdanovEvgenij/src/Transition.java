public class Transition {

    private String X;
    private String Y;
    private String Q;
    private String P;
    private String dir;

    public Transition(String production) {
        String[] strings = production.split(" ");
        Q = strings[0];
        X = strings[1];
        Y = strings[4];
        P = strings[3];
        dir = strings[5];
    }

    public String getReadSymbol() {
        return X;
    }

    public String getY() {
        return Y;
    }

    public String getCurrState() {
        return Q;
    }
    public String getNewState() {
        return P;
    }

    public String getDir() {
        return dir;
    }
}
