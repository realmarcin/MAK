package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ParseWebLog {


    String emails = "";

    public ParseWebLog(String[] args) {

        int count = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(args[0])));

            String data = in.readLine();

            while (data != null) {

                if (data.indexOf("------") == 0 || data.indexOf("=") == 0) {

                    boolean add = false;

                    data = in.readLine();
                    if (data != null && data.indexOf("JEVTRACE") != -1)
                        add = true;
                    if (data == null)
                        break;
                    data = in.readLine();
                    data = in.readLine();

                    //System.out.println(data);

                    if (add)
                        data = in.readLine();

                    //System.out.println(data);


                    String curmail = data;
                    if (curmail.indexOf("Email") == 0 && curmail.length() > 5) {
                        curmail = curmail.substring(curmail.indexOf("=") + 1, curmail.length());
                        curmail = util.StringUtil.replace(curmail, "%60", "");
                        curmail = util.StringUtil.replace(curmail, "%40", "@");
                    }

                    System.out.println(curmail);

                    data = in.readLine();
                    while (data != null && (data.indexOf("------") != 0 && data.indexOf("=") != 0)) {

                        if (data.indexOf("JEvTrace") != -1)
                            add = true;
                        data = in.readLine();

                    }

                    if (add && curmail.length() > 0) {
                        emails += curmail + ", ";
                        count++;
                    }


                }

                System.out.println(count + "\t" + emails);

                //data = in.readLine();
            }


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void main(String[] args) {

        if (args.length == 1) {

            ParseWebLog pwl = new ParseWebLog(args);
        } else {

            System.out.println("Syntax: java util.ParseWebLog <web log file>");
        }

    }

}