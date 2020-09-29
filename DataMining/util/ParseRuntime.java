package DataMining.util;

import util.MoreArray;
import util.TextFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 1/11/14
 * Time: 11:41 PM
 */
public class ParseRuntime {


    /**
     * @param args
     */
    public ParseRuntime(String[] args) {

        File dir = new File(args[0]);
        String[] list = dir.list();

        int count = 0;
        ArrayList times = new ArrayList();
        ArrayList nums = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            //if (list[i].indexOf("DS_Store") == -1) {
            String path = args[0] + "/" + list[i];
            File curdir = new File(path);
            System.out.println(path);

            int ind1 = list[i].indexOf("round");
            int ind2 = list[i].indexOf("_", ind1 + 1);

            System.out.println(ind1 + "\t" + ind2);

            if (ind2 == -1) {
                ind2 = list[i].length();
            }
            String label = null;
            try {
                label = list[i].substring(ind1, ind2);
            } catch (Exception e) {
                label = "1";
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            String[] listcur = curdir.list();
            for (int j = 0; j < listcur.length; j++) {
                String curfile = path + "/" + listcur[j];
                //if (curfile.indexOf("DS_Store") == -1) {
                Scanner sc = null;
                try {
                    sc = new Scanner(new File(curfile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("curfile :" + curfile);
                sc.useDelimiter("\\n");
                String s = sc.nextLine();
                sc.close();
                //System.out.println("s :"+s);
                double[] data = null;

                data = countLinesAndFinalCrit(curfile);
                if (data != null) {
                    int lines = (int) data[0];
                    double sec = (Double) data[1];
                    //double crit = data[1];
                    //int index = s.indexOf(" ");
                    //int index2 = s.indexOf(" ", index + 1);

                    //System.out.println(s);
                    //times.add(label + "\t" + Double.parseDouble(s.substring(index, index2)) + "\t" + lines + "\t" + crit);
                    times.add(label + "\t" + lines + "\t" + sec);
                    nums.add(sec);
                    count++;
                }
                //}
                //}
            }
        }

        TextFile.write(times, args[0] + "_runtimestats.txt");

        double[] allnums = MoreArray.toDoubleArray(nums);
        double sum = 0;
        double max = 0;
        for (int i = 0; i < allnums.length; i++) {
            sum += allnums[i];

            if (allnums[i] > max)
                max = allnums[i];
        }

        sum = sum / (60 * 60);
        String[] out = {"" + sum + "\n" + "max time " + max + " s\nsuccessfully processed " + count};
        System.out.println(sum);
        TextFile.write(out, args[0] + "_totaltime.txt");

    }

    /**
     * @param path
     * @return
     * @throws IOException
     */
    public double[] countLinesAndFinalCrit(String path) {
        double[] ret = null;
        try {
            LineNumberReader rdr = new LineNumberReader(new FileReader(path));


            ret = new double[2];
            String line = "";
            String last = "";
            while ((line = rdr.readLine()) != null) {
                last = line;
            }

            ret[0] = rdr.getLineNumber();
            rdr.close();

            //System.out.println(last);
            String[] split = last.split(" ");
            //System.out.println(MoreArray.toString(split));
            //System.out.println(":"+split[3]+":");
            try {
                ret[1] = Double.parseDouble(split[3]);
            } catch (Exception e) {
                ret = null;
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NumberFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return ret;
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 1) {
            ParseRuntime rm = new ParseRuntime(args);
        } else {
            System.out.println("syntax: java DataMining.util.ParseRuntime\n" +
                    "<input dir with roundX subdirs>"
            );
        }
    }
}
