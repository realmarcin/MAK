package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class listNum {
    int start = -200;
    int max = 16;
    String writeall = "";

    public listNum(String[] args) {
//this.max = Integer.parseInt(args[0]);
        System.out.println("max " + max);


        for (int i = start; i < max; i++)
            writeall += i + "\n";

        String out = (max + ".txt");
        try {

            PrintWriter p = new PrintWriter(new FileWriter(out));
            p.print(writeall);
            p.close();
        } catch (IOException e) {
            System.out.println("Could not write to file ...");
        }


    }

    public static void main(String[] argv) {
        if (argv.length == 1) {
            listNum mad = new listNum(argv);
        } else {
            System.out.println("syntax:  java util.listNum <max>\n");
        }
    }

}
