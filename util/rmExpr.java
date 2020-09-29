package util;


public class rmExpr {

    String dir;
    String expr;

    public rmExpr(String[] pass) {
        dir = pass[0];
        expr = pass[1];

        runShell rs = new runShell();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String rmnow = "rm -f " + dir + "/" + expr + i + j + "*";
                System.out.println("Exec: " + rmnow);
                rs.execute(rmnow);
            }
        }

    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Syntax:  java util.rmExpr <dir> <expr>");
        } else {
            rmExpr rdl = new rmExpr(args);
        }
    }
}
