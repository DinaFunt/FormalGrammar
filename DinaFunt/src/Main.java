public class Main {

    public static void main(String[] args) {
        Generate generate = new Generate("machine.txt", "grammar.txt");
        generate.CreateGrammar();

        int number = Integer.parseInt(args[0]); //for example
        boolean res;
        CheckNumber check = new CheckNumber("grammar.txt");
        res = check.isAPrime(number);

        if (res) {
            System.out.println("the number is prime");
        } else {
            System.out.println("the number is not prime");
        }
    }
}
