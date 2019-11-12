package util;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Marcin
 * Date: Aug 13, 2005
 * Time: 12:12:25 PM
 */
public class DelimFile {

    String delimiter = "\t";
    final static String commentchar = "#";

    /**
     * @param f
     * @param delim
     * @param skip
     * @return
     */
    public static String[][] readtoArray(String f, String delim, int skip, boolean debug) {
        ArrayList[] get = read(f, delim, skip, debug);
        if (debug)
            System.out.println("get.length " + get.length);
        int maxsize = MoreArray.maxArrayListElementSize(get);// - skip;
        if (debug)
            System.out.println("maxsize " + maxsize);
        //System.out.println("WARNING : USING SKIPPING LEADS TO LOSS OF FIRST DATA ROW readtoArray columns " + maxsize + "\t" + get.length);
        String[][] store = new String[maxsize][get.length];
        for (int i = 0; i < get.length; i++) {
            if (get[i].size() != maxsize)
                System.out.println("WARNING DelimFile.readtoArray uneven sizes " + get[i].size() + "\t" + maxsize);
            //get current row
            for (int j = 0; j < get[i].size(); j++) {
                store[j][i] = (String) get[i].get(j);
                if (i == 0 && debug)
                    System.out.println("readtoArray " + i + "\t" + j + "\t" + store[j][i]);
            }
        }
        if (debug)
            System.out.println("store " + store.length);
        //MoreArray.printArray(store[0]);
        return store;
    }

    /**
     * @param f
     * @param delim
     * @param skip
     * @return
     */
    public static String[][] readtoArray(String f, int skip, String delim) {

        ArrayList[] get = read(f, delim, false);
        int maxsize = MoreArray.maxArrayListElementSize(get) - skip;
        //System.out.println("WARNING : USING SKIPPING LEADS TO LOSS OF FIRST DATA ROW readtoArray columns " + maxsize + "\t" + get.length);

        String[][] store = new String[maxsize][get.length];
        for (int i = 0; i < get.length; i++) {
            //get current row
            ArrayList one = get[i];
            for (int j = skip; j < one.size(); j++) {
                store[j - skip][i] = (String) one.get(j);
                //System.out.println("readtoArray " + i + "\t" + j + "\t" + store[j][i]);
            }
        }
        return store;
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public static String[][] readtoArray(String f, String delim, boolean debug) {
        return readtoArray(f, delim, 0, debug);
    }

    /**
     * @param f
     * @param delim
     * @return
     */
    public static String[][] readtoArrayfromBinary(String f, String delim, boolean debug) {
        ArrayList[] get = read(f, delim, debug);
        int maxsize = MoreArray.maxArrayListElementSize(get);
        //System.out.println("readtoArray columns " + maxsize + "\t" + get.length);
        String[][] store = new String[maxsize][get.length];
        for (int i = 0; i < get.length; i++) {
            ArrayList one = get[i];
            for (int j = 0; j < one.size(); j++) {
                store[j][i] = (String) one.get(j);
                //System.out.println(i+"\t"+j+"\t"+store[j][i]);
            }
        }
        return store;
    }

    /**
     * Method read tab delim data from a file truncating all rows to the least number of columns found.
     *
     * @param f
     * @param delim
     * @return
     */
    public static String[][] readtoArraytrimR(String f, String delim, boolean debug) {
        ArrayList[] get = read(f, delim, debug);
        int maxsize = MoreArray.minArrayListElementSize(get);
        //System.out.println("readtoArray columns " + maxsize + "\t" + get.length);
        String[][] store = new String[maxsize][get.length];
        for (int i = 0; i < get.length; i++) {
            ArrayList one = get[i];
            for (int j = 0; j < maxsize; j++) {
                store[j][i] = (String) one.get(j);
                //System.out.println(i+"\t"+j+"\t"+store[j][i]);
            }
        }
        return store;
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return String[] array
     */
    public final static String[] readtoSingleArray(String f, String delim, boolean debug) {
        ArrayList[] get = read(f, delim, debug);
        ArrayList one = get[0];
        System.out.println("readtoSingleArray rows/columns " + one.size() + "\t" + get.length);
        String[] store = new String[one.size()];
        for (int i = 0; i < get.length; i++) {
            one = get[i];
            store[i] = (String) one.get(0);
            if (debug)
                System.out.println("readtoSingleArray " + i + "\t" + store[i]);
        }
        return store;
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return double[][] array
     */
    public final static double[][] readtoDoubleArray(String f, String delim, boolean debug) {
        ArrayList[] get = read(f, delim, debug);
        ArrayList one = (ArrayList) get[0];
        //System.out.println("readtoArray columns " + one.size() + "\t" + get.length);
        double[][] store = new double[one.size()][get.length];
        for (int i = 0; i < get.length; i++) {
            one = (ArrayList) get[i];
            for (int j = 0; j < one.size(); j++) {
                Double test = null;
                try {
                    test = Double.valueOf((String) one.get(j));
                } catch (NumberFormatException e) {
                    store[j][i] = Double.NaN;
                }
                try {
                    store[j][i] = test.doubleValue();
                } catch (Exception e) {
                    store[j][i] = Double.NaN;
                }
            }
        }
        return store;
    }


    /**
     * Method read delimited data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static String[] readtoArrayOne(String f, String delim, boolean debug) {
        ArrayList[] get = read(f, delim, debug);
        ArrayList one = (ArrayList) get[0];
        //System.out.println("readtoArray columns " + one.size() + "\t" + get.length);
        String[] store = new String[one.size()];
        for (int i = 0; i < get[0].size(); i++) {
            store[i] = (String) get[0].get(i);
        }
        return store;
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static ArrayList[] readChar(String f, String delim) {
        int cols = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            while (line != null) {
                StringTokenizer tok = new StringTokenizer(line, delim);
                int all = tok.countTokens();
                for (int i = 0; i < all; i++) {
                    cols++;
                }
                break;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        ArrayList[] store = new ArrayList[cols];
        for (int i = 0; i < cols; i++) {
            store[i] = new ArrayList();
        }
        //System.out.println("detected " + cols + " columns.");
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            int count = 0;
            while (line != null) {
                StringTokenizer tok = new StringTokenizer(line, delim);
                int all = tok.countTokens();
                //System.out.println("row " + count + "\ttokens " + all);
                for (int i = 0; i < all; i++) {
                    String cur = (String) (String) tok.nextToken();
                    char[] storec = new char[cur.length()];
                    cur.getChars(0, cur.length(), storec, 0);
                    store[i].add(storec);
                    //System.out.println("added "+(string)add.get(add.size()-1));
                }
                //System.out.println(count+" loaded "+store[i].size());
                count++;
                line = in.readLine();
            }
            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        /*
        for (int i = 0; i < store.length; i++) {
            System.out.println("Column " + i + " loaded " + store[i].size() + " rows.");
        }
        */
        return store;
    }

    /**
     * @param f
     * @param delim
     * @return
     */
    public static String[][] readtoArrayFirstDelim(String f, String delim) {
        ArrayList[] get = readFirstDelim(f, delim);
        int maxsize = MoreArray.maxArrayListElementSize(get);
        //System.out.println("readtoArray columns " + maxsize + "\t" + get.length);
        String[][] store = new String[maxsize][get.length];
        for (int i = 0; i < get.length; i++) {
            //get current row
            ArrayList one = get[i];
            for (int j = 0; j < one.size(); j++) {
                //System.out.println("readtoArrayFirstDelim " + j + "\t" + i + "\t" +
                //        one.size() + "\t" + store.length + "\t" + store[0].length);
                store[j][i] = (String) one.get(j);
                //System.out.println("readtoArrayFirstDelim " + i + "\t" + j + "\t" + store[j][i]);
            }
        }
        return store;
    }

    /**
     * Method read delim data from a file.
     * Assumes each row has same number of entries as first row.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static ArrayList[] readFirstDelim(String f, String delim) {
        ArrayList[] store = MoreArray.initArrayListArray(2);
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            while (line.indexOf(commentchar) == 0) {
                line = in.readLine();
            }
            int index = line.indexOf(delim);
            //System.out.println("readFirstDelim " + line);
            while (line != null) {
                String one = line.substring(0, index);
                store[0].add(one);
                String two = line.substring(index + delim.length(), line.length());
                store[1].add(two);
                //System.out.println("readFirstDelim " + one+"\t"+two+"\t"+store[0].size());
                line = in.readLine();
                if (line != null) {
                    index = line.indexOf(delim);
                    //System.out.println("readFirstDelim " + line);
                }
            }
            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return store;
    }

    /**
     * Method read delim data from a file.
     * Assumes each row has same number of entries as first row.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static ArrayList[] read(String f, String delim, boolean debug) {
        return read(f, delim, 0, debug);
    }

    /**
     * Method read delim data from a file.
     * Assumes each row has same number of entries as first row.
     *
     * @param f
     * @param delim
     * @param skip
     * @param debug
     * @return ArrayList of ArrayLists
     */
    public final static ArrayList[] read(String f, String delim, int skip, boolean debug) {
        if (debug)
            System.out.println("DelimFile read " + f);
        //System.out.println("WARNING : USING SKIPPING LEADS TO LOSS OF FIRST DATA ROW readtoArray columns");

        int cols = 0;
        ArrayList[] store = null;
        int[] warnining = new int[2];
        try {
            //System.out.println("reading " + f);
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            while (skip > 0) {
                line = in.readLine();
                skip--;
                if (debug)
                    System.out.println("DelimFile skip " + line);
            }
            if (debug)
                System.out.println("DelimFile a/f skip " + line);
            if (debug)
                System.out.println("DelimFile read 1 " + line);
            while (line.indexOf(commentchar) == 0) {
                line = in.readLine();
                if (debug)
                    System.out.println("DelimFile skipping " + line);
            }

            int count = StringUtil.countOccur(line, delim);
            int[] delim_index = StringUtil.occurIndex(line, delim);
            if (count == 0) {
                delim_index = new int[1];
                delim_index[0] = 0;
            }

            if (debug) {
                System.out.println("delim_index " + delim_index.length);
                MoreArray.printArray(delim_index);
            }
            cols = delim_index.length;
            if (count != 0)
                cols += 1;
            String[] split = new String[cols];
            if (debug)
                System.out.println("DelimFile read cols " + cols);
            store = new ArrayList[cols];
            if (debug)
                System.out.println("DelimFile first " + line);
            /*Split for list line determines column number.*/
            for (int i = 0; i < cols; i++) {
                if (i != 0 || (delim_index != null && delim_index[0] != 0)) {
                    int start = 0;
                    int stop = line.length();
                    if (i > 0)
                        start = delim_index[i - 1] + 1;
                    if (i != cols - 1)
                        stop = delim_index[i];
                    String substring = line.substring(start, stop);
                    //if (debug)
                    //    System.out.println("substring " + substring);
                    split[i] = substring;
                    store[i] = new ArrayList();
                } else if (cols == 1) {
                    split[i] = line;
                    store[i] = new ArrayList();
                } else {//if (cols == 0) {
                    split[i] = "";
                    store[i] = new ArrayList();
                }
            }
            if (debug) {
                System.out.println("DelimFile first row");
                MoreArray.printArray(split);
                System.out.println("DelimFile.read detected " + f + "\t" + cols + " columns.");
            }
            //int count = 0;
            if (debug) {
                System.out.println("DelimFile.read bf while " + line);
                MoreArray.printArray(split);
            }
            int countloop = 10;
            while (line != null) {
                if (debug) {
                    System.out.println("DelimFile read line " + countloop + "\t" + split.length + "\n" + line);
                }
                for (int i = 0; i < cols; i++) {
                    //try {

                    if (i < split.length)
                        store[i].add(split[i]);
                    else
                        store[i].add("");

                    /*  if (debug && countloop < 10)
                           System.out.println("DelimFile read column " + countloop + "\t" + i + "\t" +
                                   store[i].size() + "\t" + split[i]);
                   } catch (Exception e) {
                       store[i].add(null);
                       if (debug) {
                           if (i < split.length) {
                               System.out.println("DelimFile index within array bounds: " + i + "\t"
                                       + split[i] + "\t" + store.length);
                               System.out.println("DelimFile read " + split.length + "\t" + line);
                           } else {
                               System.out.println("DelimFile error not enough cols " + i + "\t" + split.length +
                                       "\t" + cols + "\t" + store.length + "\n" + line);
                               System.out.println("DelimFile read " + split.length + "\t" + line);
                           }
                           e.printStackTrace();
                       }
                   } */
                }
                line = in.readLine();
                if (line != null) {
                    split = line.split(delim);
                    boolean warning = false;
                    if (split.length < cols) {
                        warning = true;
                        warnining[0]++;
                    } else if (split.length > cols) {
                        warning = true;
                        warnining[1]++;
                    }
                    if (warning && debug) {
                        System.out.println("line " + countloop + "\t" + line);
                        MoreArray.printArray(split);
                        if (split.length != cols) {
                            System.out.println("line has different number of columns " +
                                    split.length + "\t(vs first line) " + cols);

                            String str = "";
                            for (int i = 0; i < store.length; i++) {
                                int last = store[i].size() - 1;
                                str += store[i].get(last) + ",";
                            }
                            System.out.println("line has different number of columns " + str);
                        }
                    }
                }
                countloop++;
            }
            if (warnining[0] > 0 || warnining[1] > 0)
                System.out.println("DelimFile Warning: File has fewer number of columns in " +
                        warnining[0] + "\tand more in " + warnining[1] + " lines");
            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        if (debug)
            for (int i = 0; i < store.length; i++)
                System.out.println("Column " + i + " loaded " + store[i].size() + " rows.");
        return store;
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static ArrayList readtoList(String f, String delim) {
        boolean test = util.ASCII.detect(f);
        ArrayList store = new ArrayList();
        if (test) {
            //int cols = 0;
            /*try {
                BufferedReader in = new BufferedReader(new FileReader(new File(f)));
                String line = in.readLine();

                while (line != null) {

                    String[] split = line.split(delim);
                    System.out.println("readtoList split line length :" + delim + ":" + split.length + "\t" + 0);
                    int all = split.length;
                    for (int i = 0; i < all; i++) {
                        cols++;
                    }
                    break;
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }*/
            // System.out.println("detected " + cols + " columns with delimiter  '"+delim+"'");
            try {
                BufferedReader in = new BufferedReader(new FileReader(new File(f)));
                String line = in.readLine();

                int count = 0;
                while (line != null) {
                    String[] split = line.split(delim);
                    //System.out.println("readtoList split line length :" + delim + ":" + split.length + "\t" + count);
                    int all = split.length;
                    ArrayList addlist = new ArrayList();

                    //System.out.println("row " + count + "\ttokens " + all);
                    for (int i = 0; i < all; i++) {
                        addlist.add((String) split[i]);
                        //System.out.println("added "+(String)addlist.get(addlist.size()-1));
                    }
                    store.add(addlist);
                    //System.out.println(count+" loaded "+store.size());
                    count++;
                    line = in.readLine();
                }
                in.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            /*for (int i = 0; i < store.length; i++) {
                System.out.println("Column " + i + " loaded " + store[i].size() + " rows.");
            }*/
        }
        return store;
    }


    /**
     * @param f
     * @param delim
     * @param head
     * @return
     */
    public final static ArrayList readtoList(String f, String delim, String head) {
        boolean test = util.ASCII.detect(f);
        ArrayList store = new ArrayList();
        if (test) {
            //int cols = 0;
            /*try {
                BufferedReader in = new BufferedReader(new FileReader(new File(f)));
                String line = in.readLine();

                while (line != null) {

                    String[] split = line.split(delim);
                    System.out.println("readtoList split line length :" + delim + ":" + split.length + "\t" + 0);
                    int all = split.length;
                    for (int i = 0; i < all; i++) {
                        cols++;
                    }
                    break;
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }*/
            // System.out.println("detected " + cols + " columns with delimiter  '"+delim+"'");
            try {
                BufferedReader in = new BufferedReader(new FileReader(new File(f)));
                String line = in.readLine();

                int count = 0;
                while (line != null) {
                    while (line.indexOf(head) == 0)
                        line = in.readLine();
                    String[] split = line.split(delim);
                    //System.out.println("readtoList split line length :" + delim + ":" + split.length + "\t" + count);
                    int all = split.length;
                    ArrayList addlist = new ArrayList();

                    //System.out.println("row " + count + "\ttokens " + all);
                    for (int i = 0; i < all; i++) {
                        addlist.add(split[i]);
                        //System.out.println("added "+(string)addlist.get(addlist.size()-1));
                    }
                    store.add(addlist);
                    //System.out.println(count+" loaded "+store.size());
                    count++;
                    line = in.readLine();
                }
                in.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            /*for (int i = 0; i < store.length; i++) {
                System.out.println("Column " + i + " loaded " + store[i].size() + " rows.");
            }*/
        }
        return store;
    }

    /**
     * @param f
     * @param delim
     * @param head
     * @param skipAfterHead
     * @return
     */
    public final static ArrayList readtoList(String f, String delim, String head, int skipAfterHead) {
        boolean testASCII = util.ASCII.detect(f);
        ArrayList store = new ArrayList();
        if (testASCII) {
            // System.out.println("detected " + cols + " columns with delimiter  '"+delim+"'");
            try {
                BufferedReader in = new BufferedReader(new FileReader(new File(f)));
                String line = in.readLine();

                int count = 0;
                while (line != null) {
                    while (line.indexOf(head) == 0)
                        line = in.readLine();
                    while (skipAfterHead > 0) {
                        line = in.readLine();
                        skipAfterHead--;
                    }
                    String[] split = line.split(delim);
                    //System.out.println("readtoList split line length :" + delim + ":" + split.length + "\t" + count);
                    int all = split.length;
                    ArrayList addlist = new ArrayList();

                    //System.out.println("row " + count + "\ttokens " + all);
                    for (int i = 0; i < all; i++) {
                        addlist.add(split[i]);
                        //System.out.println("added "+(string)addlist.get(addlist.size()-1));
                    }
                    store.add(addlist);
                    //System.out.println(count+" loaded "+store.size());
                    count++;
                    line = in.readLine();
                }
                in.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            /*for (int i = 0; i < store.length; i++) {
                System.out.println("Column " + i + " loaded " + store[i].size() + " rows.");
            }*/
        }
        return store;
    }

    /**
     * @param zf
     * @param delim
     * @param head
     * @param skipAfterHead
     * @return
     */
    public final static ArrayList readtoListFromZip(ZipFile zf, ZipEntry ze, String delim, String head, int skipAfterHead) {
        //boolean testASCII = util.ASCII.detect(f);
        ArrayList store = new ArrayList();
        //if (testASCII) {
        try {
            BufferedReader zipReader = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
            String line = zipReader.readLine();
            int count = 0;
            while (line != null) {
                while (line.indexOf(head) == 0)
                    line = zipReader.readLine();
                while (skipAfterHead > 0) {
                    line = zipReader.readLine();
                    skipAfterHead--;
                }
                String[] split = line.split(delim);
                int all = split.length;
                ArrayList addlist = new ArrayList();
                for (int i = 0; i < all; i++) {
                    addlist.add(split[i]);
                }
                store.add(addlist);
                count++;
                line = zipReader.readLine();
            }
            zipReader.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        //}
        return store;
    }


    /**
     * @param f
     * @param delim
     * @return
     */
    public final static ArrayList readtoListWithCommentsandXLabels(String f, String delim) {
        boolean test = util.ASCII.detect(f);
        ArrayList store = new ArrayList();
        if (test) {
            // int cols = 0;
            /*try {
                BufferedReader in = new BufferedReader(new FileReader(new File(f)));
                String line = in.readLine();

                while (line != null) {
                    while (line.indexOf("#") == 0)
                        line = in.readLine();

                    //should be xlabels with columns as data proper
                    line = in.readLine();
                    String[] split = line.split(delim);
                    //System.out.println("readtoList split line length :" + delim + ":" + split.length + "\t" + 0);
                    int all = split.length;
                    for (int i = 0; i < all; i++) {
                        cols++;
                    }
                    break;
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }*/
            // System.out.println("detected " + cols + " columns with delimiter  '"+delim+"'");
            try {
                BufferedReader in = new BufferedReader(new FileReader(new File(f)));
                String line = in.readLine();

                //skip comments
                while (line.indexOf("#") == 0)
                    line = in.readLine();

                int count = 0;
                while (line != null) {
                    String[] split = line.split(delim);
                    //System.out.println("readtoList split line length :" + delim + ":" + split.length + "\t" + count);
                    int all = split.length;
                    ArrayList addlist = new ArrayList();

                    //System.out.println("row " + count + "\ttokens " + all);
                    for (int i = 0; i < all; i++) {
                        addlist.add(split[i]);
                        //System.out.println("added "+(string)addlist.get(addlist.size()-1));
                    }
                    store.add(addlist);
                    //System.out.println(count + " loaded " + store.size() + "\t" + addlist.size());
                    count++;
                    line = in.readLine();
                }
                in.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            /*for (int i = 0; i < store.length; i++) {
                System.out.println("Column " + i + " loaded " + store[i].size() + " rows.");
            }*/
        }
        return store;
    }

    /**
     * Method to write tab delim data from an ArrayList of ArrayLists.
     *
     * @param a ArrayList of ArrayLists
     * @param f input file
     */
    public final static void write(ArrayList[] a, String f, String delim) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(f));
            for (int j = 0; j < a[0].size(); j++) {
                for (int i = 0; i < a.length; i++) {
                    if (i < a.length - 1)
                        out.print(a[i].get(j) + delim);
                    else if (i == a.length - 1)
                        out.print(a[i].get(j) + delim);
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + f);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Method to write tab delim data from a 2D ArrayList of ArrayLists.
     *
     * @param a ArrayList of ArrayLists
     * @param f input file
     */
    public final static void write(ArrayList[][] a, String f, String delim) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(f));
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[0].length; j++) {
                    double[] cur = MoreArray.ArrayListtoDouble(a[i][j]);
                    if (cur.length > 0)
                        out.println(i + delim + j + delim + MoreArray.toString(cur, delim));
                    else {
                        out.println(i + delim + j);
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + f);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Method to write tab delim data from an ArrayList of ArrayLists.
     *
     * @param a ArrayList of ArrayLists
     * @param f input file
     */
    public final static void write(String[][] a, String f, String delim) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(f));
            for (int i = 0; i < a.length; i++) {
                String str = "";
                for (int j = 0; j < a[i].length; j++) {
                    /*if (a[i][j] != null && a[i][j].length() == 0)
                        a[i][j] = delim;*/
                    if (j < a[i].length - 1)
                        str += a[i][j] + delim;
                    else
                        str += a[i][j];
                }
                out.println(str);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + f);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param a
     * @param f
     * @param delim
     */
    public final static void write(String[] a, String f, String delim) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(f));

            for (int j = 0; j < a.length; j++) {
                if (j < a.length - 1)
                    out.print(a[j] + delim);
                else if (j == a.length - 1)
                    out.print(a[j] + "\n");
                out.println();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + f);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Method to write tab delim data from an ArrayList of ArrayLists.
     *
     * @param a ArrayList of ArrayLists
     * @param f input file
     */
    public final static void write(String[][] a, String f, String delim, String header) {
        //System.out.println("write " + f);
        try {
            PrintWriter out = new PrintWriter(new FileWriter(f));
            out.print(header);
            for (int j = 0; j < a.length; j++) {
                for (int i = 0; i < a[j].length; i++) {
                    if (i < a[j].length - 1)
                        out.print(a[j][i] + delim);
                    else if (i == a[j].length - 1)
                        out.print(a[j][i] + "\n");
                }
                //out.println();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + f);
            System.out.println("IOException: " + e.getMessage());
        }
    }


    /**
     * @param a
     * @param file
     * @param delim
     * @param header
     */
    public final static void write(String[][] a, String file, String[] xlabels, String delim, String header) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            if (header != null)
                out.print(header);
            for (int j = 0; j < xlabels.length; j++) {
                //System.out.println(j+"\t"+xlabels[j]);
                if (j < xlabels.length - 1)
                    out.print(xlabels[j] + delim);
                else if (j == xlabels.length - 1)
                    out.print(xlabels[j] + "\n");
            }
            for (int j = 0; j < a.length; j++) {
                for (int i = 0; i < a[0].length; i++) {
                    if (i < a[0].length - 1)
                        out.print(a[j][i] + delim);
                    else if (i == a[0].length - 1)
                        out.print(a[j][i] + "\n");
                }
                //out.println();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + file);
            System.out.println("IOException: " + e.getMessage());
        }


    }

    /**
     * @param a
     * @param file
     * @param xlabels
     * @param delim
     * @param header
     */
    public final static void write(String[] a, String file, String[] xlabels, String delim, String header) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            if (header != null)
                out.print(header);
            for (int j = 0; j < a.length; j++) {
                out.print(xlabels[j] + "\t" + a[j] + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + file);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param a
     * @param file
     * @param xlabels
     * @param delim
     * @param header
     * @param footer
     */
    public final static void write(String[][] a, String file, String[] xlabels, String delim, String header, String footer) {
        write(a, file, xlabels, null, delim, header, footer);
    }

    /**
     * @param a
     * @param file
     * @param xlabels
     * @param ylabels
     * @param delim
     * @param header
     * @param footer
     */
    public final static void write(String[][] a, String file, String[] xlabels, String[] ylabels, String delim, String header, String footer) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            if (header != null)
                out.print(header);
            if (xlabels != null) {
                out.print("\t");
                for (int j = 0; j < xlabels.length; j++) {
                    //System.out.println(j+"\t"+xlabels[j]);
                    if (j < xlabels.length - 1)
                        out.print(xlabels[j] + delim);
                    else
                        out.print(xlabels[j] + "\n");
                }
            }
            for (int i = 0; i < a.length; i++) {
                if (ylabels != null)
                    out.print(ylabels[i] + delim);
                for (int j = 0; j < a[i].length; j++) {
                    if (j < a[i].length - 1)
                        out.print(a[i][j] + delim);
                    else
                        out.print(a[i][j] + "\n");
                }
                //out.println();
            }
            if (footer != null)
                out.print(footer);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + file);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param a
     * @param file
     * @param xlabels
     * @param ylabels
     * @param delim
     * @param header
     * @param footer
     */
    public final static void write(int[][] a, String file, String[] xlabels, String[] ylabels, String delim, String header, String footer) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            if (header != null)
                out.print(header);
            if (xlabels != null) {
                out.print("\t");
                for (int j = 0; j < xlabels.length; j++) {
                    //System.out.println(j+"\t"+xlabels[j]);
                    if (j < xlabels.length - 1)
                        out.print(xlabels[j] + delim);
                    else
                        out.print(xlabels[j] + "\n");
                }
            }
            for (int i = 0; i < a.length; i++) {
                if (ylabels != null)
                    out.print(ylabels[i] + delim);
                for (int j = 0; j < a[i].length; j++) {
                    if (j < a[i].length - 1)
                        out.print(a[i][j] + delim);
                    else
                        out.print(a[i][j] + "\n");
                }
                //out.println();
            }
            if (footer != null)
                out.print(footer);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + file);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param a
     * @param file
     * @param xlabels
     * @param ylabels
     * @param delim
     * @param header
     * @param footer
     */
    public final static void write(double[][] a, String file, String[] xlabels, String[] ylabels, String delim, String header, String footer) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            if (header != null)
                out.print(header);
            if (xlabels != null) {
                out.print("\t");
                for (int j = 0; j < xlabels.length; j++) {
                    //System.out.println(j+"\t"+xlabels[j]);
                    if (j < xlabels.length - 1)
                        out.print(xlabels[j] + delim);
                    else
                        out.print(xlabels[j] + "\n");
                }
            }
            for (int i = 0; i < a.length; i++) {
                if (ylabels != null) {
                    try {
                        out.print(ylabels[i] + delim);
                    } catch (Exception e) {
                        System.out.println("Delim.write current " + i + "\trows " + a.length + "\tcols " + a[0].length +
                                "\ty/x label " + ylabels.length + "\t" + xlabels.length);
                        e.printStackTrace();
                    }
                    //System.out.println("write " + a.length + "\t" + ylabels.length);
                }
                for (int j = 0; j < a[i].length; j++) {
                    if (j < a[i].length - 1)
                        out.print(a[i][j] + delim);
                    else
                        out.print(a[i][j] + "\n");
                }
                //out.println();
            }
            if (footer != null)
                out.print(footer);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + file);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param a
     * @param file
     * @param xlabels
     * @param ylabels
     * @param delim
     * @param header
     * @param footer
     */
    public final static void write(String[] a, String file, String[] xlabels, String[] ylabels, String delim, String header, String footer) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            if (header != null)
                out.print(header);
            if (xlabels != null) {
                out.print("\t");
                for (int j = 0; j < xlabels.length; j++) {
                    //System.out.println(j+"\t"+xlabels[j]);
                    if (j < xlabels.length - 1)
                        out.print(xlabels[j] + delim);
                    else
                        out.print(xlabels[j] + "\n");
                }
            }
            for (int i = 0; i < a.length; i++) {
                if (ylabels != null)
                    out.print(ylabels[i] + delim);
                if (i < a.length - 1)
                    out.print(a[i] + delim);
                else
                    out.print(a[i] + "\n");
            }
            if (footer != null)
                out.print(footer);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + file);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param a
     * @param file
     * @param xlabel
     * @param ylabels
     * @param delim
     * @param header
     * @param footer
     */
    public final static void write(String[] a, String file, String xlabel, String[] ylabels, String delim, String header, String footer) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            if (header != null)
                out.print(header);
            if (xlabel != null) {
                out.print(delim);
                out.print(xlabel + delim);
            }
            for (int i = 0; i < a.length; i++) {
                if (ylabels != null)
                    out.print(ylabels[i] + delim);
                out.print(a[i] + delim);
                out.print("\n");
            }
            if (footer != null)
                out.print(footer);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing tab delim file " + file);
            System.out.println("IOException: " + e.getMessage());
        }
    }

}
