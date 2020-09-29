package util;

import java.io.*;

/**
 * Hacks the Root CVS entries to change repository path.
 *
 * User: marcin
 * Date: Jul 30, 2008
 * Time: 8:13:27 PM
 */
public class HackCVS {

    /**
     * @param args
     */
    public HackCVS(String[] args) {
        String indir = args[0];
        String[] list = (new File(indir)).list();

        for (int i = 0; i < list.length; i++) {
            String[] list2 = (new File(indir + "/" + list[i])).list();
            if (list2 != null) {
                for (int j = 0; j < list2.length; j++) {
                    if (list2[j].equals("CVS")) {
                        String path = indir + "/" + list[i] + "/" + list2[j];
                        String[] list3 = (new File(path)).list();
                        for (int k = 0; k < list3.length; k++) {
                            if (list3[k].equals("Root")) {
                                String infile = indir + "/" + list[i] + "/" + list2[j] + "/" + list3[k];
                                System.out.println(i + "\t" + j + "\t" + k + "\t" + infile);
                                try {
                                    BufferedReader in = new BufferedReader(new FileReader(infile));
                                    String data = in.readLine();
                                    if (data != null) {
                                        if (data.equals(":ext:marcin@gobi.lbl.gov:/usr2/people/gtl/cvsroot")) {
                                            data = ":ext:marcin@gobi.qb3.berkeley.edu:/usr2/people/gtl/cvsroot";
                                            System.out.println(data);
                                        }
                                        in.close();
                                        try {
                                            System.out.println("writing  ........ " + infile);
                                            PrintWriter out = new PrintWriter(new FileWriter(infile), true);
                                            out.println(data);
                                            out.close();
                                        } catch (IOException e) {
                                            System.out.println(e.getMessage());
                                        }

                                    } else
                                        in.close();
                                } catch (IOException
                                        e) {
                                    System.out.println("IOException: " + e.getMessage());
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

    }


    /**
     * Main function
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            HackCVS kr = new HackCVS(args);
        }
    }
}
