package util;

import java.util.HashMap;

/**
 * User: marcin
 * Date: Apr 25, 2007
 * Time: 4:17:52 PM
 */
public class MapArgOptions {

    /**
     * @param args
     * @param possible
     * @return
     */
    public final static String[] map(String[] args, String[] possible) {
        String[] ret = new String[possible.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                System.out.println(args[i]);
                int dash = args[i].indexOf("-");
                if (dash == 0) {
                    int spa = args[i].indexOf(" ", dash + 1);
                    if (spa != -1) {
                        //int nextdash = args[i].indexOf("-", spa);
                        String sub = args[i].substring(0, spa);
                        int k = MoreArray.getArrayIndUnique(possible, sub);
                        if (k != -1) {//nextdash != spa + 1 &&
                            String s = args[i].substring(spa + 1, args[i].length());
                            if (s.length() > 0 && !s.equals(" ")) {
                                ret[k] = s;
                                //System.out.println("MapArgOptions.map " + s + "\t" + possible[k]);
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @param args
     * @param arglabels
     * @return
     */
    public final static HashMap maptoMap(String[] args, String[] arglabels) {
        System.out.println("args");
        MoreArray.printArray(args);
        System.out.println("arglabels");
        MoreArray.printArray(arglabels);
        HashMap ret = new HashMap();
        int[] ind = new int[arglabels.length];
        for (int i = 0; i < arglabels.length; i++) {
            //int index = MoreArray.getArrayIndexOfAtZero(args, arglabels[i]);
            int index = MoreArray.getArrayIndUnique(args, arglabels[i]);
            System.out.println("MapArgOptions.map " + i + "\t" + arglabels[i] + "\t" + index + "\t" + args.length);
            if (index != -1) {
                ind[i] = index;
                int dash = args[index + 1].indexOf("-");

                boolean testint = false;

                try {
                    int testi = Integer.parseInt(args[index + 1]);
                    testint = true;
                } catch (NumberFormatException e) {
                    testint = false;
                    //e.printStackTrace();
                }

                if (dash != 0 || testint) {
                    System.out.println("MapArgOptions put " + arglabels[i] + "\t" + args[index + 1]);
                    ret.put(arglabels[i], args[index + 1]);
                }
            }
        }

        return ret;
    }

    /**
     * BROKEN!!
     *
     * @param args
     * @return
     */
    public final static String[] compactArgs(String[] args) {
        int args_size = StringUtil.countOccur(args, "-") + 1;
        String[] next = new String[args_size];
        int count = 0;
        for (int i = 0; i < args_size; i++) {
            //System.out.println("compactArgs " + i + "\t" + args_size + "\t" + args.length);
            //System.out.println("compactArgs "+i + "\t" + count + "\t" + args[i]);
            //System.out.println("compactArgs "+(i + 1) + "\t" + count + "\t" + args[i + 1]);
            if (i + 1 < args_size && args[i + 1].indexOf("-") != 0) {
                next[count] = args[i] + " " + args[i + 1];
                //System.out.println("compactArgs " + count + "\t" + next[count]);
            } else {
                next[count] = args[i];
            }
            i++;
            count++;
        }
        return next;
    }

    /**
     * @param args
     * @return
     */
    public final static String[] compactArgs(String[] args, String[] arglabels) {
        String[] next = new String[arglabels.length];
        //int[] ind = new int[arglabels.length];
        for (int i = 0; i < arglabels.length; i++) {
            int index = MoreArray.getArrayIndexOfAtZero(args, arglabels[i]);
            if (index != -1) {
                //System.out.println("compactArgs " + arglabels[i] + "\t" + args[index] + "\t" + args[index + 1]);
                //ind[i] = index;
                int dash = args[index + 1].indexOf("-");
                if (dash != 0) {
                    next[i] = args[index + 1];
                }
            }
        }
        //System.out.println("compactArgs");
        //MoreArray.printArray(next);
        return next;
    }

}
