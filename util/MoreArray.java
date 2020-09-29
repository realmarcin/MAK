package util;

/*
import mathy.Distrib;
import mathy.Matrix;
import mathy.stat;
*/

import mathy.stat;

import java.awt.*;
import java.util.*;
import java.util.List;


public class MoreArray {


    /**
     * Tests the identity of two char arrays, using the first arrays length as the reference range.
     *
     * @param a
     * @param b
     * @return true if all elements in a match b
     */
    public static boolean matchArray(char[] a, char[] b) {
        int count = 0;
        int len = a.length;
        for (int i = 0; i < len; i++) {
            if (a[i] == b[i])
                count++;
        }
        if (count == len)
            return true;
        else
            return false;
    }

    /**
     * Creates an String from the indices of the array which do not have the value to be ignored.
     *
     * @param array
     * @return list of Integer array entries
     */
    public static ArrayList intarraytoList(int[] array, int ignore_value) {
        ArrayList ret = new ArrayList();
        int s_size = array.length;
        if (array == null || s_size == 0)
            return null;
        else {
            int i = 0;
            for (i = 0; i < s_size; i++) {
                if (array[i] != ignore_value)
                    ret.add(new Integer(i));
            }
        }
        return ret;
    }

    /**
     * Creates an String from the contents of an int array.
     *
     * @param array
     * @return list of Integer array entries
     */
    public static ArrayList intarraytoList(int[] array) {
        ArrayList ret = new ArrayList();
        int s_size = array.length;
        if (array == null || s_size == 0)
            return null;
        else {
            int i = 0;
            for (i = 0; i < s_size; i++) {
                ret.add(new Integer(array[i]));
            }
        }
        return ret;
    }

    /**
     * Creates an String from an array of Strings using the optional delimiter.
     *
     * @param alist
     * @param delim
     * @return String of concatenated array entries
     */
    public static String intarraytoString(ArrayList alist, String delim) {
        String delim_default = " ,";
        int s_size = alist.size();
        if (alist == null || s_size == 0)
            return null;
        else {
            if (delim != null)
                delim = delim_default;
            String ret = "";
            int i = 0;
            for (i = 0; i < s_size - 1; i++) {
                ret += (((Integer) alist.get(i)).intValue()) + delim;
                //System.out.println("intarraytoString ret "+ret);
            }
            ret += (((Integer) alist.get(i)).intValue());
            //System.out.println("intarraytoString ret last "+ret);
            return ret;
        }
    }

    /**
     * Creates an String from an array of Strings using the optional delimiter.
     *
     * @param alist
     * @param delim
     * @return String of concatenated array entries
     */
    public static String intarraytoString(ArrayList alist, String delim, int add) {

        String delim_default = " ,";

        int s_size = alist.size();
        if (alist == null || s_size == 0)
            return null;
        else {
            if (delim != null)
                delim = delim_default;

            String ret = "";
            int i = 0;
            for (i = 0; i < s_size - 1; i++) {
                ret += (((Integer) alist.get(i)).intValue() + add) + delim;
                //System.out.println("intarraytoString ret "+ret);
            }
            ret += (((Integer) alist.get(i)).intValue() + add);
            //System.out.println("intarraytoString ret last "+ret);
            return ret;
        }
    }

    /**
     * Creates an String from an array of Strings using the optional delimiter.
     *
     * @param s
     * @param d delimiter
     * @return String of concatenated array entries
     */
    public static String intarraytoString(int[] s, String d) {

        if (s == null || s.length == 0)
            return null;
        else {
            String delim = " ,";
            if (d != null)
                delim = d;

            String ret = "";
            int i = 0;
            for (i = 0; i < s.length - 1; i++) {
                ret += s[i] + delim;
            }
            ret += s[i];
            return ret;
        }
    }

    /**
     * Creates an String from an array of Strings using the optional delimiter.
     *
     * @param s
     * @param delim delimiter
     * @return String of concatenated array entries
     */
    public static String toString(String[] s, String delim) {
        if (s == null || s.length == 0)
            return null;
        else {
            if (delim == null)
                delim = " ,";
            String ret = "";
            int i = 0;
            for (i = 0; i < s.length - 1; i++) {
                ret += s[i] + delim;
            }
            ret += s[i];
            return ret;
        }
    }

    /**
     * Creates an String from an array of Strings using the optional delimiter.
     *
     * @param s
     * @param delim delimiter
     * @return String of concatenated array entries
     */
    public static String toString(boolean[] s, String delim) {
        if (s == null || s.length == 0)
            return null;
        else {
            if (delim == null)
                delim = " ,";
            String ret = "";
            int i = 0;
            for (i = 0; i < s.length - 1; i++) {
                ret += s[i] + delim;
            }
            ret += s[i];
            return ret;
        }
    }


    /**
     * @param s
     * @param delim
     * @return
     */
    public static String arraytoString(String[][] s, String delim, String end_delim) {
        if (s == null || s.length == 0 || s[0].length == 0)
            return null;
        else {
            if (delim == null)
                delim = " ,";
            String ret = "";
            int i = 0;
            for (i = 0; i < s.length; i++) {
                int j = 0;
                for (j = 0; j < s[i].length - 1; j++) {
                    ret += s[i][j] + delim;
                }
                ret += s[i][j] + end_delim;
            }
            return ret;
        }
    }

    /**
     * @param from
     * @return
     */
    public static boolean hasNaN(double[] from) {
        for (int i = 0; i < from.length; i++)
            if (Double.isNaN(from[i]))
                return true;
        return false;
    }


    /**
     * @param from
     * @return
     */
    public static String[] copy(String[] from) {
        String[] to = new String[from.length];
        for (int i = 0; i < from.length; i++)
            to[i] = from[i];
        return to;
    }

    /**
     * @param from
     * @return
     */
    public static double[] copy(double[] from) {
        double[] to = new double[from.length];
        for (int i = 0; i < from.length; i++)
            to[i] = from[i];
        return to;
    }

    /**
     * @param from
     * @return
     */
    public static double[][] copy(double[][] from) {
        double[][] to = new double[from.length][from[0].length];
        for (int i = 0; i < from.length; i++)
            for (int j = 0; j < from[0].length; j++)
                to[i][j] = from[i][j];
        return to;
    }

    /**
     * @param from
     * @return
     */
    public static int[] copy(int[] from) {
        int[] to = new int[from.length];
        for (int i = 0; i < from.length; i++)
            to[i] = from[i];
        return to;
    }

    /**
     * @param g
     * @return
     */
    public static double[][] conv(ArrayList g) {
        double[] first = (double[]) g.get(0);
        int x = first.length;
        int y = g.size();
        System.out.println("MoreArray.conv x " + x + "\ty " + y);
        double[][] ret = new double[x][y];
        for (int i = 0; i < y; i++) {
            double[] tmp = new double[0];
            try {
                tmp = (double[]) g.get(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 0; j < x; j++) {
                ret[j][i] = tmp[j];
            }
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static int[][] convtoInt(ArrayList g) {
        int[] first = (int[]) g.get(0);
        int x = first.length;
        int y = g.size();
        System.out.println("MoreArray.conv x " + x + "\ty " + y);
        int[][] ret = new int[x][y];
        for (int i = 0; i < y; i++) {
            int[] tmp = new int[0];
            try {
                tmp = (int[]) g.get(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 0; j < x; j++) {
                ret[j][i] = tmp[j];
            }
        }
        return ret;
    }


    /**
     * @param g
     * @return
     */
    public static int[][] convtoIntNoRotate(ArrayList g) {
        int[] first = (int[]) g.get(0);
        int x = first.length;
        int y = g.size();
        System.out.println("MoreArray.conv x " + x + "\ty " + y);
        int[][] ret = new int[y][x];
        for (int i = 0; i < g.size(); i++) {
            int[] cur = (int[]) g.get(i);
            ret[i] = cur;
        }
        return ret;
    }


    /**
     * @param g
     * @return
     */
    public static String[] convtoStringArray(ArrayList g) {
        String[] ret = new String[g.size()];
        for (int i = 0; i < g.size(); i++) {
            try {
                ret[i] = (String) g.get(i);
            } catch (Exception e) {
                System.out.println(i + "\t" + g.get(i));
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * @param g
     * @param width
     * @return
     */
    public static String[][] convtoString2DArray(ArrayList g, int width) {
        String[][] ret = new String[g.size()][width];
        for (int i = 0; i < g.size(); i++) {
            try {
                ret[i] = (String[]) g.get(i);
            } catch (Exception e) {
                try {
                    ret[i] = MoreArray.ArrayListtoString((ArrayList) g.get(i));
                } catch (Exception ee) {
                    System.out.println(i + "\t" + g.get(i));
                    ee.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * @param g
     * @param width
     * @return
     */
    public static double[][] convtodouble2DArray(ArrayList g, int width) {
        double[][] ret = new double[g.size()][width];
        for (int i = 0; i < g.size(); i++) {
            try {
                ret[i] = (double[]) g.get(i);
            } catch (Exception e) {
                try {
                    ret[i] = MoreArray.ArrayListtoDouble((ArrayList) g.get(i));
                } catch (Exception ee) {
                    System.out.println(i + "\t" + g.get(i));
                    ee.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * @param g
     * @param width
     * @return
     */
    public static int[][] convtoint2DArray(ArrayList g, int width) {
        int[][] ret = new int[g.size()][width];
        for (int i = 0; i < g.size(); i++) {
            try {
                ret[i] = (int[]) g.get(i);
            } catch (Exception e) {
                try {
                    ret[i] = MoreArray.ArrayListtoInt((ArrayList) g.get(i));
                } catch (Exception ee) {
                    System.out.println(i + "\t" + g.get(i));
                    ee.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * @param g
     * @param width
     * @param delim
     * @return
     */
    public static String[][] convtoString2DArray(ArrayList g, int width, String delim) {
        String[][] ret = new String[g.size()][width];
        for (int i = 0; i < g.size(); i++) {
            try {
                ret[i] = ((String) g.get(i)).split(delim);
            } catch (Exception e) {
                System.out.println(i + "\t" + g.get(i));
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * @param store
     * @param add
     * @return
     */
    public static ArrayList addStringstoArrayList(ArrayList store, ArrayList add) {
        return addStringstoArrayList(store, add, null);
    }


    /**
     * @param store
     * @param add
     * @param delim
     * @return
     */
    public static ArrayList addStringstoArrayList(ArrayList store, ArrayList add, String delim) {
        if (delim != null) {
            for (int i = 0; i < add.size(); i++) {
                store.add(add.get(i) + delim);
            }
        } else {
            for (int i = 0; i < add.size(); i++) {
                store.add(add.get(i));
            }
        }
        return store;
    }

    /**
     * @param store
     * @param add
     * @return
     */
    public static ArrayList addArrayListtoArrayList(ArrayList store, ArrayList add) {
        for (int i = 0; i < add.size(); i++) {
            store.add(add.get(i));
        }
        return store;
    }

    /**
     * Converts a matrix stored as an list of lists of String objects.
     * Expects x and y labels or placeholders.
     *
     * @param g
     * @return
     */
    public static double[][] convfromString(ArrayList g) {
        ArrayList first = (ArrayList) g.get(0);
        //subtract label from total length
        int size_cols = first.size();
        int size_rows = g.size();
        //System.out.println("MoreArray.convfromString x " + size_cols + "\ty " + size_rows);
        double[][] ret = new double[size_rows][size_cols];
        for (int i = 0; i < size_rows; i++) {
            ArrayList now = (ArrayList) g.get(i);
            int now_size = now.size();
            if (now_size != size_cols) {
                System.out.println("MoreArray.convfromString list " + now_size + " length does not match first " + size_cols);
            }
            for (int j = 0; j < now_size; j++) {
                String s = (String) now.get(j);
                int ind = s.indexOf(",");
                if (ind != -1) {
                    s = s.substring(0, ind);
                }
                try {
                    ret[i][j] = (Double.parseDouble(s));
                } catch (Exception e) {
                    System.out.println("MoreArray.convfromString " + s + "\ti " + i + "\tret.length" + ret.length + "\tj " + j + "\tret[0].length " + ret[0].length);
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Converts a 2D string array to an array of doubles.
     *
     * @param g
     * @return
     */
    public static double[][] convfromString(String[][] g) {
        int size_rows = g.length;
        int size_cols = g[0].length;
        //System.out.println("MoreArray.convfromString x " + size_cols + "\ty " + size_rows);
        double[][] ret = MoreArray.initArray(size_rows, size_cols, Double.NaN);
        for (int i = 0; i < size_rows; i++) {
            for (int j = 0; j < size_cols; j++) {
                try {
                    ret[i][j] = Double.parseDouble(g[i][j]);
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Converts a 2D string array to an array of ints.
     *
     * @param g
     * @return
     */
    public static int[][] convfromStringtoInt(String[][] g) {
        int size_rows = g.length;
        int size_cols = g[0].length;
        //System.out.println("MoreArray.convfromString x " + size_cols + "\ty " + size_rows);
        int[][] ret = MoreArray.initArray(size_rows, size_cols, 0);
        for (int i = 0; i < size_rows; i++) {
            for (int j = 0; j < size_cols; j++) {
                try {
                    ret[i][j] = Integer.parseInt(g[i][j]);
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Converts a 1D string array to an array of doubles.
     *
     * @param g
     * @return
     */
    public static double[] convfromString(String[] g) {
        int size_rows = g.length;
        //System.out.println("MoreArray.convfromString x " + size_rows);
        double[] ret = MoreArray.initArray(size_rows, Double.NaN);
        if (g != null) {
            for (int i = 0; i < size_rows; i++) {
                if (g[i] != null) {
                    try {
                        ret[i] = Double.parseDouble(g[i]);
                    } catch (NumberFormatException e) {
                        System.out.println("failed " + g[i]);
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("input element is null " + i);
                }
            }
        } else {
            System.out.println("input arg is null");
        }
        return ret;
    }


    /**
     * @param g
     * @return
     */
    public static String[] convtoString(ArrayList g) {
        String[] ret = new String[g.size()];
        for (int i = 0; i < g.size(); i++) {
            ret[i] = (String) g.get(i);
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static ArrayList convtoArrayList(int[] g) {
        int length = g.length;
        ArrayList ret = new ArrayList();
        for (int i = 0; i < length; i++) {
            ret.add(new Integer(g[i]));
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static ArrayList convtoArrayList(double[] g) {
        int length = g.length;
        ArrayList ret = new ArrayList();
        for (int i = 0; i < length; i++) {
            ret.add(new Double(g[i]));
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static ArrayList convtoArrayList(String[] g) {
        int length = g.length;
        ArrayList ret = new ArrayList();
        for (int i = 0; i < length; i++) {
            //System.out.println("convtoArrayList g[i] " + i + "\t" + g[i]);
            ret.add(g[i]);
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static ArrayList convtoArrayListInteger(String[] g) {
        int length = g.length;
        ArrayList ret = new ArrayList();
        for (int i = 0; i < length; i++) {
            ret.add(g[i]);
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static ArrayList convtoArrayList(String[][] g) {
        ArrayList ret = new ArrayList();
        for (int i = 0; i < g.length; i++) {
            String add = "";
            for (int j = 0; j < g[0].length; j++) {
                add += g[i][j];
            }
            ret.add(add);
        }
        return ret;
    }

    /**
     * Converts a string array to an ArrayList of strings, one string per array row.
     *
     * @param g
     * @param col_delim column delimiter
     * @return
     */
    public static ArrayList convtoArrayList(String[][] g, String col_delim) {
        int col_delim_len = col_delim.length();
        ArrayList ret = new ArrayList();
        for (int i = 0; i < g.length; i++) {
            String add = "";
            for (int j = 0; j < g[0].length; j++) {
                add += g[i][j] + col_delim;
            }
            int add_len = add.length();
            add = add.substring(0, add_len - col_delim_len);
            ret.add(add);
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static Vector convtoVector(String[] g) {
        int length = g.length;
        Vector ret = new Vector(length);
        for (int i = 0; i < length; i++) {
            ret.add(g[i]);
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static char[][] convtoChar(String[] g) {
        int length = g.length;
        char[][] ret = new char[length][];
        for (int i = 0; i < length; i++) {
            ret[i] = g[i].toCharArray();
        }
        return ret;
    }

    /**
     * @param g
     * @return
     */
    public static StringBuffer[] convtoStringBuffer(ArrayList g) {
        StringBuffer[] ret = new StringBuffer[g.size()];
        for (int i = 0; i < g.size(); i++) {
            ret[i] = (StringBuffer) g.get(i);
            //System.out.println("convtoStringBuffer "+i+"\t"+ret[i]);
        }
        return ret;
    }

    /**
     * @param a
     */
    public static void printArray(int[] a) {
        //System.out.println("Printing int array of length " + a.length);
        for (int i = 0; i < a.length; i++)
            System.out.println(i + "\t" + a[i]);
    }

    /**
     * @param a
     */
    public static void printArray(double[] a) {
        if (a != null) {
            //System.out.println("printArray int array of length " + a.length);
            for (int i = 0; i < a.length; i++)
                System.out.println(i + "\t" + a[i]);
        } else {
            System.out.println("printArray int array is null");
        }
    }

    /**
     * @param a
     */
    public static void printArray(Double[] a) {
        if (a != null) {
            //System.out.println("printArray int array of length " + a.length);
            for (int i = 0; i < a.length; i++)
                System.out.println(i + "\t" + a[i]);
        } else {
            System.out.println("printArray int array is null");
        }
    }

    /**
     * @param a
     */
    public static void printArray(double[] a, double[] labels) {
        if (a != null) {
            //System.out.println("printArray int array of length " + a.length);
            for (int i = 0; i < a.length; i++)
                System.out.println(labels[i] + "\t" + a[i]);
        } else {
            System.out.println("printArray int array is null");
        }
    }

    /**
     * @param a
     * @param k
     */
    public static void printArray(double[] a, int k) {
        if (a != null) {
            //System.out.println("printArray int array of length " + a.length);
            for (int i = 0; i < Math.min(k, a.length); i++)
                System.out.println(i + "\t" + a[i]);
        } else {
            System.out.println("printArray int array is null");
        }
    }

    /**
     * @param a
     */
    public static void printArray(String[] a) {
        //System.out.println("Printing int array of length " + a.length);
        for (int i = 0; i < a.length; i++)
            System.out.println(i + "\t" + a[i]);
    }

    /**
     * @param a
     */
    public static void printArray(ArrayList a, int index) {
        String[] first = (String[]) a.get(0);
        for (int i = 0; i < first.length; i++) {
            //System.out.println("Printing int array of length " + a.length);
            String out = "";
            if (index != -1)
                out += (i + index) + "\t";
            for (int j = 0; j < a.size(); j++) {
                String[] s = new String[0];
                try {
                    s = (String[]) a.get(j);
                    out += s[i];
                    if (j < a.size() - 1)
                        out += "\t";
                } catch (Exception e) {
                    try {
                        ArrayList sa = (ArrayList) a.get(j);
                        out += (String) sa.get(i);
                        if (j < a.size() - 1)
                            out += "\t";
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }
            System.out.println(out);
        }
    }

    /**
     * @param a
     */
    public static void printArray(String[] a, String delim) {
        //System.out.println("Printing int array of length " + a.length);
        for (int i = 0; i < a.length; i++)
            System.out.println(i + "\t:" + a[i] + ":");
    }

    /**
     * @param a
     */
    public static void printArray(String[][] a) {
        //System.out.println("Printing int array of length " + a.length);
        for (int i = 0; i < a.length; i++) {
            System.out.print(i);
            for (int j = 0; j < a[0].length; j++) {
                System.out.print("\t" + a[i][j]);
            }
            System.out.print("\n");
        }
    }

    /**
     * @param a
     * @param delim
     */
    public static void printArrayList(ArrayList a, String delim) {
        //System.out.println("Printing int array of length " + a.length);
        String out = a.get(0).toString();
        for (int i = 1; i < a.size(); i++)
            out += "," + a.get(i).toString();
        System.out.println(out);
    }

    /**
     * @param a
     * @param delim
     */
    public static void printStringArray(String[] a, String delim) {
        //System.out.println("Printing int array of length " + a.length);
        String out = "" + a[0];
        for (int i = 1; i < a.length; i++)
            out += delim + a[i];
        System.out.println(out);
    }

    /**
     * @param a
     */
    public static void printArray(double[][] a) {
        //System.out.println("Printing int array of length " + a.length);
        for (int i = 0; i < a.length; i++) {
            System.out.print(i);
            for (int j = 0; j < a[0].length; j++) {
                System.out.print("\t" + a[i][j]);
            }
            System.out.print("\n");
        }
    }

    /**
     * @param a
     */
    public static void printArray(int[][] a) {
        //System.out.println("Printing int array of length " + a.length);
        for (int i = 0; i < a.length; i++) {
            System.out.print(i);
            for (int j = 0; j < a[0].length; j++) {
                System.out.print("\t" + a[i][j]);
            }
            System.out.print("\n");
        }
    }

    /**
     * @param a
     */
    public static void printArray(int[][] a, String[] x, String[] y) {
        //System.out.println("Printing int array of length " + a.length);
        MoreArray.printArray(x);
        for (int i = 0; i < a.length; i++) {
            System.out.print(y[i]);
            for (int j = 0; j < a[0].length; j++) {
                System.out.print("\t" + a[i][j]);
            }
            System.out.print("\n");
        }
    }

    /**
     * @param a
     */
    public static void printArray(int[] a, int cutoff) {
        //System.out.println("Printing int array of length " + a.length);
        for (int i = 0; i < a.length; i++) {
            if (a[i] > cutoff)
                System.out.println(i + "\t" + a[i]);
        }
    }

    /**
     * @param delimiter
     * @param a
     * @return
     */
    public static String printArray(String delimiter, int[] a) {
        String ret = "";
        for (int i = 0; i < a.length; i++) {
            if (i < a.length - 1)
                ret += "" + a[i] + delimiter;
            else
                ret += "" + a[i];
        }
        return ret;
    }

    /**
     * @param delimiter
     * @param a
     * @return
     */
    public static String printArray(String delimiter, double[] a) {
        String ret = "";
        for (int i = 0; i < a.length; i++) {
            if (i < a.length - 1)
                ret += "" + a[i] + delimiter;
            else
                ret += "" + a[i];
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static int getArrayNonEmptySize(ArrayList a) {
        int count = 0;
        for (int i = 0; i < a.size(); i++) {
            String s = (String) a.get(i);
            if (s != null) {
                int len = s.length();
                if (len > 0)
                    count++;
            }
        }
        return count;

    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayInd(ArrayList a, String s) {
        ArrayList track = new ArrayList();
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.size(); i++) {
            String n = (String) a.get(i);
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (n != null)// && n.length() > 0)
                if (n.equals(s)) {
                    //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                    Integer add = new Integer(i);
                    track.add(add);
                }
        }
        if (track.size() == 1) {
            return ((Integer) track.get(0)).intValue();
        } else if (track.size() > 1) {
            System.out.println("multiple hits for " + s);
            for (int i = 0; i < track.size(); i++) {
                int curind = ((Integer) track.get(i)).intValue();
                System.out.println(i + "\t" + curind + "\t" + (String) a.get(curind));
            }
            return ((Integer) track.get(0)).intValue();
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndexOf(int[] a, int s) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == s)
                return i;
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndexOf(String s, String[] a) {
        ArrayList b = MoreArray.convtoArrayList(a);
        return getArrayIndexOf(s, b);
    }

    /**
     * @param s
     * @param a
     * @return
     */
    public static int getArrayIndexOf(String s, ArrayList a) {
        ArrayList track = new ArrayList();
        if (s.length() == 0)
            System.out.println("getArrayInd query string has zero length :" + s + ":");
        else {
            for (int i = 0; i < a.size(); i++) {
                String n = (String) a.get(i);
                //System.out.println("getArrayInd " + i + "\t" + n);
                if (n != null)// && n.length() > 0)
                    if (s.length() != 0 && s.indexOf(n) != -1) {
                        //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                        Integer add = new Integer(i);
                        track.add(add);
                    }
            }
            if (track.size() == 1) {
                return ((Integer) track.get(0)).intValue();
            } else if (track.size() > 1) {
                System.out.println("multiple hits for :" + s + ":");
                for (int i = 0; i < track.size(); i++) {
                    int curind = ((Integer) track.get(i)).intValue();
                    System.out.println(i + "\t" + curind + "\t" + (String) a.get(curind));
                }
                return ((Integer) track.get(0)).intValue();
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndexOf(String[] a, String s) {
        ArrayList b = MoreArray.convtoArrayList(a);
        return getArrayIndexOf(b, s);
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndexOf(ArrayList a, String s) {
        ArrayList track = new ArrayList();
        if (s.length() == 0)
            System.out.println("getArrayInd query string has zero length :" + s + ":");
        else {
            for (int i = 0; i < a.size(); i++) {
                String n = (String) a.get(i);
                //System.out.println("getArrayInd " + i + "\t" + n);
                if (n != null)// && n.length() > 0)
                    if (s.length() != 0 && n.indexOf(s) != -1) {
                        //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                        Integer add = new Integer(i);
                        track.add(add);
                    }
            }
            if (track.size() == 1) {
                return ((Integer) track.get(0)).intValue();
            } else if (track.size() > 1) {
                /*System.out.println("multiple hits for :" + s + ":");
                for (int i = 0; i < track.size(); i++) {
                    int curind = ((Integer) track.get(i)).intValue();
                    System.out.println(i + "\t" + curind + "\t" + (String) a.get(curind));
                }*/
                return ((Integer) track.get(0)).intValue();
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndUnique(String[] a, String s) {
        ArrayList b = MoreArray.convtoArrayList(a);
        return getArrayIndUnique(b, s, false);
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndUniqueIgnoreCase(String[] a, String s) {
        ArrayList b = MoreArray.convtoArrayList(a);
        return getArrayIndUnique(b, s, true);
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndUnique(ArrayList a, String s, boolean ignorecase) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.size(); i++) {
            String n = (String) a.get(i);
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (n != null) {
                if (!ignorecase && n.equals(s)) {
                    return i;
                } else if (ignorecase && n.equalsIgnoreCase(s)) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayInd(ArrayList a, int s) {
        ArrayList track = new ArrayList();
        //System.out.println("getArrayInd start " + s);        
        Integer integer = null;
        for (int i = 0; i < a.size(); i++) {
            //int n = -1;
            try {
                integer = (Integer) a.get(i);
            } catch (Exception e) {
                integer = new Integer((String) a.get(i));
                //n = integer.intValue();
            }
            //System.out.println("getArrayInd " + i + "\t" + n);
            int n = integer.intValue();
            if (n == s) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                //Integer add = new Integer(i);
                track.add(new Integer(i));
            }
        }
        if (track.size() == 1) {
            int curind = ((Integer) track.get(0)).intValue();
            //System.out.println("getArrayInd returning " +curind);
            return curind;
        } else if (track.size() > 1) {
            System.out.println("multiple hits for " + s);
            for (int i = 0; i < track.size(); i++) {
                int curind = ((Integer) track.get(i)).intValue();
                System.out.println(i + "\t" + curind + "\t" + a.get(curind));
            }
            return -1;
        } else {
            return -1;
        }
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndUnique(ArrayList a, int s) {
        for (int i = 0; i < a.size(); i++) {
            Integer integer = null;
            try {
                integer = new Integer((String) a.get(i));
            } catch (Exception e) {
                integer = (Integer) a.get(i);
            }
            //System.out.println("getArrayInd " + i + "\t" + n);
            int n = integer.intValue();
            if (n == s) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndInteger(ArrayList a, int s) {
        ArrayList track = new ArrayList();
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.size(); i++) {
            int n = ((Integer) a.get(i)).intValue();
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (n == s) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                Integer add = new Integer(i);
                track.add(add);
            }
        }
        if (track.size() == 1) {
            int curind = ((Integer) track.get(0)).intValue();
            return curind;
        } else if (track.size() > 1) {
            System.out.println("multiple hits for " + s);
            for (int i = 0; i < track.size(); i++) {
                int curind = ((Integer) track.get(i)).intValue();
                System.out.println(i + "\t" + curind + "\t" + (String) a.get(curind));
            }
            return -1;
        } else {
            return -1;
        }
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayInd(String[] a, String s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] == null)
                System.out.println("getArrayInd " + i + " is null");
            if (a[i].equals(s)) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndStartsWith(String[] a, String s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] == null)
                System.out.println("getArrayInd " + i + " is null");
            if (a[i].startsWith(s)) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndEndsWith(String[] a, String s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] == null)
                System.out.println("getArrayInd " + i + " is null");
            if (a[i].endsWith(s)) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndIgnoreCase(String[] a, String s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i].equalsIgnoreCase(s)) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndexOfAtZero(String[] a, String s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] != null)
                if (a[i].indexOf(s) == 0) {
                    //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                    return i;
                }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndIfArrayIndexAtZero(String[] a, String s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (s.indexOf(a[i]) == 0) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndIfArrayIndexAtZero(ArrayList a, String s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.size(); i++) {
            String str = (String) a.get(i);
            //System.out.println("getArrayInd " + i + "\t:" + str+":");
            if (str.indexOf(s) == 0) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayInd(int[] a, int s) throws Exception {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] == s) {
                //System.out.println("getArrayInd found " + i + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * /**
     *
     * @param a
     * @param s
     * @return
     */
    public static int[] getArrayIndAll(int[] a, int s) throws Exception {
        //System.out.println("getArrayInd start " + s);
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] == s) {
                //System.out.println("getArrayInd found " + i + "\t" + s);
                ret.add((Integer) i);
            }
        }
        return listtoint(ret);
    }

    /**
     * @param a
     * @return
     * @throws Exception
     */
    public static int[] listtoint(ArrayList<Integer> a) throws Exception {
        int size = a.size();
        int[] ret = new int[size];
        for (int i = 0; i < size; i++) {
            ret[i] = a.get(i).intValue();
        }
        return ret;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayInd(Color[] a, Color s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] == s) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayInd(char[] a, char s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] == s) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayInd(double[] a, double s) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if (a[i] == s) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public static int getArrayIndUnique(double[] a, double s, int[] done) {
        //System.out.println("getArrayInd start " + s);
        for (int i = 0; i < a.length; i++) {
            //System.out.println("getArrayInd " + i + "\t" + n);
            if ((a[i] == s || (Double.isNaN(s) && Double.isNaN(a[i]))) && done[i] != 1) {
                //System.out.println("getArrayInd found " + i + "\t" + n + "\t" + s);
                return i;
            }
        }
        return -1;
    }

    /**
     * Function to copy object contents of a vector.
     *
     * @param v
     * @return
     */
    public static ArrayList copyArrayList(ArrayList v) {
        ArrayList r = null;
        if (v != null) {
            r = initArrayList(v.size());
            for (int i = 0; i < v.size(); i++) {
                r.set(i, v.get(i));
            }
        }
        return r;
    }

    /**
     * @param v
     * @return
     */
    public static List copyList(List v) {
        ArrayList r = null;
        if (v != null) {
            r = initArrayList(v.size());
            for (int i = 0; i < v.size(); i++) {
                r.set(i, v.get(i));
            }
        }
        return r;
    }

    /**
     * @param v
     * @return
     */
    public static ArrayList[] copyArrayListArrayList(ArrayList[] v) {
        ArrayList[] r = null;
        if (v != null) {
            r = new ArrayList[v.length];
            for (int i = 0; i < v.length; i++) {
                r[i] = copyArrayList(v[i]);
                /*System.out.println("copyArrayListArrayList " + v[i].size() + "\t" + r[i].size());
                MoreArray.printArrayList(r[i], ",");*/
            }
        }
        return r;
    }


    /**
     * Converts an ArrayList of Integers to an int array.
     *
     * @param a
     * @return
     */
    public static int[] tointArray(ArrayList a) {
        int[] inta = new int[a.size()];
        int count = 0;
        Iterator i = a.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            try {
                inta[count] = ((Integer) o).intValue();
            } catch (Exception e) {
                try {
                    inta[count] = (Integer.parseInt((String) o));
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                //System.out.println("tointArray ERROR "+o.toString());
                //e.printStackTrace();
            }
            count++;
        }
        return inta;
    }

    /**
     * Converts an ArrayList of Integers to an int array.
     *
     * @param a
     * @return
     */
    public static double[] toDoubleArray(ArrayList a) {
        double[] inta = new double[a.size()];
        int count = 0;
        Iterator i = a.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            try {
                inta[count] = ((Double) o).intValue();
            } catch (Exception e) {
                inta[count] = (Double.parseDouble((String) o));
                //System.out.println("tointArray ERROR "+o.toString());
                //e.printStackTrace();
            }
            count++;
        }
        return inta;
    }

    /**
     * Converts a String array to an int array.
     *
     * @param a
     * @return
     */
    public static int[] tointArray(String[] a) throws NumberFormatException {
        int[] inta = new int[a.length];
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            inta[count] = Integer.parseInt(a[i]);
            count++;
        }
        return inta;
    }

    /**
     * @param in
     * @return
     */
    public static double[] convInttoDouble(int[] in) {
        double[] d = new double[in.length];
        for (int i = 0; i < in.length; i++) {
            d[i] = in[i];
        }
        return d;
    }

    /**
     * @param in
     * @return
     */
    public static double[] convStringtoDouble(String[] in) {
        double[] d = new double[in.length];
        for (int i = 0; i < in.length; i++) {
            try {
                d[i] = Double.parseDouble(in[i]);
                //System.out.println(i + "\t" + d[i]);
            } catch (NumberFormatException e) {
                d[i] = Double.NaN;
                //e.printStackTrace();
            }
        }
        return d;
    }

    /**
     * @param list
     * @param query
     * @param store
     * @param num
     * @return
     */
    public static ArrayList saveStartsWithArray(String[] list, String query, int[] store, int num) {
        int count = 0;
        for (int i = 0; i < list.length; i++) {
            //System.out.println("saveindexOfArray "+query+"\t"+list[i]);
            if (list[i] != null && list[i].indexOf(query) == 0) {
                store[i] = num;
                count++;
            }
        }
        ArrayList ret = new ArrayList();
        ret.add(store);
        ret.add(new Integer(count));
        return ret;
    }


    /**
     * @param list
     * @param query
     * @param store
     * @param num
     * @return
     */
    public static ArrayList saveStartsWithArray(StringBuffer[] list, String query, int[] store, int num) {
        int count = 0;
        for (int i = 0; i < list.length; i++) {
            //System.out.println("saveindexOfArray "+query+"\t"+list[i]);
            if (list[i] != null && list[i].indexOf(query) == 0) {
                store[i] = num;
                count++;
            }
        }
        ArrayList ret = new ArrayList();
        ret.add(store);
        ret.add(new Integer(count));
        return ret;
    }

    /**
     * @param list
     * @param query
     * @param store
     * @param num
     * @return
     */
    public static int[] saveStartsWith(String[] list, String query, int[] store, int num) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null && list[i].startsWith(query)) {
                store[i] = num;
            }
        }
        return store;
    }

    /**
     * @param list
     * @param query
     * @param store
     * @return
     */
    public static int[] saveIndex(String[] list, String query, int[] store) {
        return saveIndex(list, query, store, 10);
    }

    /**
     * @param list
     * @param query
     * @param store
     * @param num
     * @return
     */
    public static int[] saveIndex(String[] list, String query, int[] store, int num) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].indexOf(query) != -1) {
                store[i] = num;
            }
        }
        return store;
    }


    /**
     * Indexes values in one array to the other, assumes they share the same elements
     * and assigns an index only to the first unmasked instance of that value.
     *
     * @param a
     * @param b
     * @return
     */
    public static int[] crossIndex(double[] a, double[] b) {
        int len = a.length;
        int[] ret = new int[len];
        boolean[] done = new boolean[len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (!done[j]) {
                    if (b[j] == a[i]) {
                        ret[i] = j;
                        done[j] = true;
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Indexes values in one array to the other, assumes they share the same elements
     * and assigns an index only to the first unmasked instance of that value.
     *
     * @param a
     * @param b
     * @return
     */
    public static int[] crossIndex(int[] a, int[] b) {
        int len = a.length;
        int[] ret = MoreArray.initArray(len, -1);// new int[len];
        boolean[] done = new boolean[len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (!done[j]) {
                    if (b[j] == a[i]) {
                        ret[i] = j;
                        done[j] = true;
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Indexes values in one array to the other, assumes they share the same elements
     * and assigns an index only to the first unmasked instance of that value.
     *
     * @param a
     * @param b
     * @return
     */
    public static int[] crossFirstIndex(int[] a, int[] b) {
        int alen = a.length;
        int blen = b.length;
        int[] ret = MoreArray.initArray(a.length, -1);
        for (int i = 0; i < alen; i++) {
            for (int j = 0; j < blen; j++) {
                //if (!done[j]) {
                if (b[j] == a[i]) {
                    ret[i] = j;
                    break;
                }
                //}
            }
        }
        return ret;
    }

    /**
     * Method to retrieve a subarray.
     *
     * @param c
     * @param start
     * @param end
     * @return
     */
    public static char[] cutCharArray(char[] c, int start, int end) {
        int maxlen = end - start + 1;
        char[] ret = new char[maxlen];
        for (int i = 0; i < maxlen; i++) {
//System.out.println("new "+i+"\told "+(start+i)+"\tret array maxlen "+maxlen+"\tstart "+start+"\tend "+end);
            ret[i] = c[start + i];
        }
        return ret;
    }


    /**
     * Reverses an int array.
     *
     * @param d
     * @return
     */
    public static int[] reverse(int[] d) {
        int[] r = new int[d.length];
        for (int i = 0; i < d.length; i++) {
            r[d.length - 1 - i] = d[i];
        }
        return r;
    }

    /**
     * Reverses a double array.
     *
     * @param d
     * @return
     */
    public static double[] reverse(double[] d) {
        double[] r = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            r[d.length - 1 - i] = d[i];
        }
        return r;
    }

    /**
     * Reverses a String array.
     *
     * @param d
     * @return
     */
    public static String[] reverse(String[] d) {
        String[] r = new String[d.length];
        for (int i = 0; i < d.length; i++) {
            r[d.length - 1 - i] = d[i];
        }
        return r;
    }

    /**
     * @param d
     * @return
     */
    public static ArrayList reverse(ArrayList d) {
        int size = d.size();
        ArrayList r = initArrayList(size);
        for (int i = 0; i < size; i++) {
            r.set(size - 1 - i, d.get(i));
        }
        return r;
    }

    /**
     * Extracts a column from the2D array - column numbering starts at 1.
     *
     * @param sarray
     * @param col
     * @return
     */
    public static String[] extractColumnStr(String[][] sarray, int col) {
        col--;
        if (col >= 0) {
            String[] ret = new String[sarray.length];
            for (int i = 0; i < sarray.length; i++) {
                //System.out.println("MoreArray.extractColumnStr " + i + "\t" + col + "\t" + sarray[i][col]);
                ret[i] = sarray[i][col];
            }
            return ret;
        }
        //System.out.println("MoreArray.extractColumnStr column index < 0, " + col + ". Note that column indexing begins at 1.");
        return null;
    }

    /**
     * Extracts a column from the 2D array - column numbering starts at 1.
     *
     * @param sarray
     * @param col
     * @return
     */
    public static double[] extractColumn(String[][] sarray, int col) {
        col--;
        double[] ret = new double[sarray.length];
        if (col >= 0)
            for (int i = 0; i < sarray.length; i++) {
                /*System.out.println("MoreArray.extractColumn");
                MoreArray.printArray(sarray[i]);
                System.out.println(i+"\t"+col+"\t"+sarray[i].length);
                MoreArray.printArray(sarray[i]);*/
                String s = sarray[i][col];
                //System.out.println("MoreArray.extractColumn " + s);
                try {
                    ret[i] = Double.parseDouble(s);
                } catch (Exception e) {
                    ret[i] = Double.NaN;
                }
            }
        return ret;
    }

    /**
     * Extracts a column from the 2D array - column numbering starts at 1.
     *
     * @param sarray
     * @param col
     * @return
     */
    public static int[] extractColumnInt(String[][] sarray, int col) {
        col--;
        int[] ret = new int[sarray.length];
        if (col >= 0)
            for (int i = 0; i < sarray.length; i++) {
                /*System.out.println("MoreArray.extractColumn");
                MoreArray.printArray(sarray[i]);
                System.out.println(i+"\t"+col+"\t"+sarray[i].length);
                MoreArray.printArray(sarray[i]);*/
                String s = sarray[i][col];
                //System.out.println("MoreArray.extractColumn " + s);
                try {
                    ret[i] = Integer.parseInt(s);
                } catch (Exception e) {
                }
            }
        return ret;
    }

    /**
     * Converts an ArrayList of Double to double[];
     *
     * @param a
     * @return
     */
    public static int[] ArrayListtoInt(ArrayList a) {
        int[] ret = new int[a.size()];
        if (a == null)
            return null;
        for (int i = 0; i < a.size(); i++) {
            try {
                ret[i] = ((Integer) a.get(i)).intValue();
            } catch (Exception e) {
                try {
                    ret[i] = Integer.parseInt(((String) a.get(i)));
                } catch (Exception ee) {
                }
                //System.out.println("ArrayListtoInt " + ret[i]);
            }
        }
        return ret;
    }

    /**
     *
     * @param a
     * @return
     */
    /*public static double[] ArrayListtoDouble(ArrayList a) {
        double[] ret = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            try {
                ret[i] = ((Integer) a.get(i)).intValue();
            } catch (Exception e) {
                try {
                    ret[i] = (((Double) a.get(i)).doubleValue());
                } catch (Exception ee) {
                }
                //System.out.println("ArrayListtoInt " + ret[i]);
            }
        }
        return ret;
    }*/

    /**
     * @param a
     * @return
     */
    public static int[] VectortoInt(Vector a) {
        int[] ret = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            try {
                ret[i] = ((Integer) a.get(i)).intValue();
            } catch (Exception e) {
                try {
                    ret[i] = Integer.parseInt(((String) a.get(i)));
                } catch (Exception ee) {
                }
                //System.out.println("VectortoInt " + ret[i]);
            }
        }
        return ret;
    }

    /**
     * Converts an ArrayList of Double to double[];
     *
     * @param a
     * @return
     */
    public static double[] ArrayListtoDouble(ArrayList a) {
        double[] ret = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            try {
                //System.out.println("ArrayListtoDouble "+i+"\t"+((Double) a.get(i)));
                ret[i] = ((Double) a.get(i)).doubleValue();
                //System.out.println("ArrayListtoDouble "+i+"\t"+ret[i]);
            } catch (Exception e) {
                //e.printStackTrace();
                try {
                    ret[i] = Double.parseDouble(((String) a.get(i)));
                } catch (Exception ee) {
                    //e.printStackTrace();
                }
            }
            //System.out.println("ArrayListtoDouble "+i+"\t"+ret[i]);
        }
        return ret;
    }

    /**
     * Converts an ArrayList of Double to double[];
     *
     * @param a
     * @return
     */
    public static String[] IntArrayListtoString(ArrayList a) {
        String[] ret = new String[a.size()];
        for (int i = 0; i < a.size(); i++) {
            int[] cur = (int[]) a.get(i);
            ret[i] = "";
            int j = 0;
            for (j = 0; j < cur.length - 1; j++) {
                ret[i] += cur[j] + "\t";
            }
            //j++;
            //System.out.println("IntArrayListtoString "+i+"\t"+ret.length+"\t"+j+"\t"+cur.length);
            ret[i] += cur[j] + "\n";
        }
        return ret;
    }

    /**
     * Converts an ArrayList of Double to double[];
     *
     * @param a
     * @return
     */
    public static String[] IntArrayListtoString(ArrayList a, String[] xlabels, String[] ylabels) {
        String[] ret = new String[a.size()];
        for (int i = 0; i < a.size(); i++) {
            int[] cur = (int[]) a.get(i);
            ret[i] = "";
            if (ylabels != null)
                ret[i] += ylabels[i] + "\t";
            int j = 0;
            for (j = 0; j < cur.length - 1; j++) {
                ret[i] += cur[j] + "\t";
            }
            //j++;
            //System.out.println("IntArrayListtoString "+i+"\t"+ret.length+"\t"+j+"\t"+cur.length);
            ret[i] += cur[j] + "\n";
        }
        return ret;
    }

    /**
     * Converts an ArrayList of String to String[];
     *
     * @param a
     * @return
     */
    public static String[] ArrayListtoString(ArrayList a) {
        String[] ret = new String[a.size()];
        for (int i = 0; i < a.size(); i++) {
            try {
                //System.out.println("ArrayListtoString " + i + "\t" + a.get(i));
                ret[i] = (String) a.get(i);
                //System.out.println("ArrayListtoString " + i + "\t" + ret[i]);
            } catch (Exception e) {
                try {
                    ret[i] = ((Integer) a.get(i)).toString();
                } catch (Exception e1) {
                    System.out.println("ArrayListtoString error: neither String nor Integer");
                    e1.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Converts an Set of String to String[];
     *
     * @param a
     * @return
     */
    public static int[] intSettoint(Set a) {
        int[] ret = new int[a.size()];
        Iterator it = a.iterator();
        int count = 0;
        while (it.hasNext()) {
            int s = (Integer) it.next();
            try {
                //System.out.println("SettoString " + i + "\t" + a.get(i));
                ret[count] = s;
                count++;
                //System.out.println("SettoString " + i + "\t" + ret[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Converts an Set of String to String[];
     *
     * @param a
     * @return
     */
    public static String[] SettoString(Set a) {
        String[] ret = new String[a.size()];
        Iterator it = a.iterator();
        int count = 0;
        while (it.hasNext()) {
            String s = (String) it.next();
            try {
                //System.out.println("SettoString " + i + "\t" + a.get(i));
                ret[count] = s;
                count++;
                //System.out.println("SettoString " + i + "\t" + ret[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static String arrayListtoString(ArrayList a) {
        return arrayListtoString(a, ",");
    }

    /**
     * @param a
     * @param delim
     * @return
     */
    public static String arrayListtoString(ArrayList a, String delim) {
        //String ret = "";
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < a.size(); i++) {
            try {
                ret.append((a.get(i).toString()));
                if (i != a.size() - 1)
                    ret.append(delim);
                //ret += (a.get(i).toString()) + delim;
                //System.out.println("arrayListtoString "+ret);
            } catch (Exception ee) {
            }
        }
        return ret.toString();
    }


    /**
     * Converts an ArrayList of Double
     * or
     * an ArrayList of double[]
     * to double[][]
     *
     * @param a
     * @return
     */
    public static double[][] ArrayListto2DDouble(ArrayList a) {
        int[] sizes = null;
        try {
            sizes = MoreArray.getListSize(a);
        } catch (Exception e) {
            //e.printStackTrace();
            sizes = MoreArray.getDoubleArrayInListSize(a);
        }
        int max = stat.findMax(sizes);
        double[][] ret = new double[a.size()][max];
        for (int i = 0; i < a.size(); i++) {
            try {
                ArrayList b = (ArrayList) a.get(i);
                for (int j = 0; j < b.size(); j++) {
                    try {
                        ret[i][j] = ((Double) b.get(j)).doubleValue();
                    } catch (Exception e) {
                        try {
                            ret[i][j] = Double.parseDouble(((String) b.get(j)));
                        } catch (Exception ee) {
                            try {
                                ret[i][j] = (Integer) b.get(j);
                            } catch (Exception eee) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                double[] get = (double[]) a.get(i);
                for (int k = 0; k < get.length; k++) {
                    ret[i][k] = get[k];
                }
            }
        }
        return ret;
    }

    /**
     * Converts an ArrayList of Double
     * or
     * an ArrayList of double[]
     * to double[][]
     *
     * @param a
     * @return
     */
    public static int[][] ArrayListto2DInt(ArrayList a) {
        int[] sizes = null;
        try {
            sizes = MoreArray.getListSize(a);
        } catch (Exception e) {
            //e.printStackTrace();
            sizes = MoreArray.getIntArrayInListSize(a);
        }
        int max = stat.findMax(sizes);
        int[][] ret = new int[a.size()][max];
        for (int i = 0; i < a.size(); i++) {
            try {
                ArrayList b = (ArrayList) a.get(i);
                for (int j = 0; j < b.size(); j++) {
                    try {
                        ret[i][j] = (Integer) b.get(j);
                    } catch (Exception e) {
                        try {
                            ret[i][j] = Integer.parseInt(((String) b.get(j)));
                        } catch (Exception ee) {
                        }
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                int[] get = (int[]) a.get(i);
                for (int k = 0; k < get.length; k++) {
                    ret[i][k] = get[k];
                }
            }
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static int[] getListSize(ArrayList a) {
        int[] ret = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            ArrayList b = (ArrayList) a.get(i);
            ret[i] = b.size();
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static int[] getDoubleArrayInListSize(ArrayList a) {
        int[] ret = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            try {
                double[] b = (double[]) a.get(i);
                ret[i] = b.length;
            } catch (Exception e) {
                try {
                    String[] b = (String[]) a.get(i);
                    ret[i] = b.length;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static int[] getIntArrayInListSize(ArrayList a) {
        int[] ret = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            try {
                int[] b = (int[]) a.get(i);
                ret[i] = b.length;
            } catch (Exception e) {
                try {
                    String[] b = (String[]) a.get(i);
                    ret[i] = b.length;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Merges two String[][] arrays.
     *
     * @param one
     * @param two
     * @return
     */
    public static String[][] mergeArraySingle(String[] one, String[] two) {
        String[][] ret = new String[one.length][2];
        for (int j = 0; j < one.length; j++) {
            ret[j][0] = one[j];
            ret[j][1] = two[j];
        }
        return ret;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public static String[] mergeArray(String[] one, String[] two) {
        int max = one.length + two.length;
        String[] ret = new String[max];
        for (int j = 0; j < one.length; j++) {
            ret[j] = one[j];
        }
        for (int j = one.length; j < max; j++) {
            ret[j] = two[j - one.length];
        }
        return ret;
    }


    /**
     * Merges two String[][] arrays.
     *
     * @param one
     * @param two
     * @return
     */
    public static String[][] mergeArray(String[][] one, String[][] two) {
        String[][] ret = new String[one.length + two.length][Math.max(one[0].length, two[0].length)];
        for (int j = 0; j < ret[0].length; j++) {
            for (int i = 0; i < ret.length; i++) {
                if (i < one.length)
                    ret[i][j] = one[i][j];
                else if (i >= one.length)
                    ret[i][j] = two[i - one.length][j];
            }
        }
        /* for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[0].length; j++) {

                if (i < one.length && j < one[0].length)// && j < one[0].length)
                    ret[i][j] = one[i][j];
                else if (i >= one.length && j - one[0].length < two.length)// && j >= one[0].length)
                    ret[i][j] = two[i - one.length][j];
            }

        }*/
        return ret;
    }


    /**
     * Merges two String[][] arrays rows of one before rows of two with the specified row spacing.
     *
     * @param one
     * @param two
     * @return
     */
    public static String[][] mergeArrayVertical(String[][] one, String[][] two, int spacing) {
        String[][] ret = new String[one.length + two.length + spacing][Math.max(one[0].length, two[0].length)];
        for (int i = 0; i < ret.length; i++) {
            if (i == one.length) {
                for (int ki = 0; ki < spacing; ki++) {
                    for (int j = 0; j < ret[0].length; j++) {
                        ret[i + ki][j] = "";
                    }
                    i++;
                }
            }
            for (int j = 0; j < ret[0].length; j++) {
                if (i < one.length)
                    ret[i][j] = one[i][j];
                else if (i >= one.length + spacing) {
                    ret[i][j] = two[i - spacing - one.length][j];
                }
            }
        }
        return ret;
    }

    /**
     * Merges two String[][] arrays columns of one before columns of two with the specified column spacing.
     *
     * @param one
     * @param two
     * @param spacing
     * @return
     */
    public static String[][] mergeArrayHorizontal(String[][] one, String[][] two, int spacing) {
        String[][] ret = new String[Math.max(one.length, two.length)][one[0].length + two[0].length + spacing];
        for (int i = 0; i < one.length; i++) {
            for (int j = 0; j < one[0].length; j++) {
                ret[i][j] = one[i][j];
            }
        }
        for (int i = 0; i < two.length; i++) {
            for (int j = 0; j < two[0].length; j++) {
                ret[i][j + one[0].length + spacing] = two[i][j];
            }
        }
        return ret;
    }

    /**
     * Inserts a column into an array, column index starts at 1.
     * Array replacing the data can be of variable length and is left aligned.
     *
     * @param one
     * @param two
     * @return
     */
    public static String[][] insertColumn(String[][] one, String[] two, int insertcolpos) {
        insertcolpos--;
        int orig_length = one.length;
        int orig_width = one[0].length;
        int insert_length = two.length;
        int max_length = Math.max(orig_length, insert_length);
        //System.out.println("insertColumn " + orig_length + "\t" + orig_width + "\t" +
        //        insert_length + "\t" + max_length);
        String[][] ret = new String[max_length][orig_width + 1];

        for (int row = 0; row < max_length; row++) {
            for (int col = 0; col < orig_width; col++) {
                //if to the left of the insertcolpos point and before padding
                if (col < insertcolpos && row < orig_length) {
                    ret[row][col] = one[row][col];
                }
                //if to the left of the insertcolpos point and padding
                else if (col < insertcolpos) {
                    ret[row][col] = null;
                }
                //if insertion point 
                else if (col == insertcolpos) {
                    //if within length of insertcolpos array insertcolpos value
                    if (row < insert_length)
                        ret[row][col] = two[row];
                        //pad
                    else
                        ret[row][col] = null;

                    //shift values from original by one column
                    if (row < orig_length)
                        ret[row][col + 1] = one[row][col];
                        //pad
                    else
                        ret[row][col + 1] = null;
                }
                //if greater than insertcolpos add values shifted by one column
                else if (col > insertcolpos && row < orig_length) {
                    try {
                        ret[row][col + 1] = one[row][col];
                    } catch (Exception e) {
                        System.out.println("insertColumn ret " + ret.length + "\t" + ret[0].length + "\t" + row + "\t" + (col + 1));
                        System.out.println("insertColumn one " + one.length + "\t" + one[0].length + "\t" + row + "\t" + (col));
                        System.out.println("0");
                        MoreArray.printArray(one[0]);
                        System.out.println("insertColumn 1");
                        MoreArray.printArray(one[1]);
                        System.out.println("insertColumn one " + one[row][col]);
                        e.printStackTrace();
                    }
                }
                //if greater than insertcolpos and padding
                else if (col > insertcolpos) {
                    ret[row][col + 1] = null;
                }
            }
        }

        return ret;
    }

    /**
     * Inserts a column into an array, column index starts at 1.
     * Array replacing the data can be of variable length and is left aligned.
     *
     * @param one
     * @param two
     * @return
     */
    public final static String[][] insertColumnPad(String[][] one, String[] two, int insert) {
        insert--;
        String[][] ret = new String[one.length][one[0].length + 1];
        for (int i = 0; i < one.length; i++) {
            for (int j = 0; j < one[0].length; j++) {
                if (j < insert && i < one.length)
                    ret[i][j] = one[i][j];
                else if (j < insert)
                    ret[i][j] = null;
                else if (j == insert && i < two.length) {
                    ret[i][j] = two[i];
                    //System.out.println("insertColumn " + two[i]);
                    if (i < one.length) {
                        ret[i][j + 1] = one[i][j];
                    } else {
                        ret[i][j + 1] = null;
                    }
                } else if (j == insert) {
                    ret[i][j] = null;
                    if (i < one.length) {
                        ret[i][j + 1] = one[i][j];
                    } else {
                        ret[i][j + 1] = null;
                    }
                } else if (j > insert && i < one.length) {
                    ret[i][j + 1] = one[i][j];
                } else if (j > insert) {
                    ret[i][j + 1] = null;
                }
            }
        }
        return ret;
    }

    /**
     * Inserts a row into an array, row index starts at 1.
     *
     * @param one
     * @param two
     * @param insert
     * @return
     */
    public static String[][] insertRow(String[][] one, String[] two, int insert) {
        insert--;
        int orig_width = one[0].length;
        int max_width = Math.max(orig_width, two.length);
        String[][] ret = new String[one.length + 1][max_width];
        //assign all rows b/f insertion point
        for (int i = 0; i < insert; i++) {
            ret[i] = one[i];
            if (max_width > orig_width) {
                for (int j = orig_width; j < max_width; j++) {
                    System.out.println(i + "\t" + j + "\t" + orig_width + "\t" + max_width);
                    ret[i][j] = null;
                }
            }
            /*System.out.println("insertRow pre     " + i);
            MoreArray.printArray(one[i]);*/
        }
        //insert row
        ret[insert] = two;
        /* System.out.println("insertRow insert " + insert);
        MoreArray.printArray(two);*/
        //assign all rows a/f insertion point
        for (int i = insert; i + 1 < ret.length; i++) {
            for (int j = 0; j < orig_width; j++) {
                ret[i + 1][j] = one[i][j];
            }
            if (max_width > orig_width) {
                for (int j = orig_width; j < max_width; j++) {
                    //System.out.println("insertRow padding " + (i + 1) + "\t" + j + "\t" + ret.length + "\t" + ret[i + 1].length);
                    ret[i + 1][j] = null;
                }
            }
            /* System.out.println("insertRow post    " + (i + 1));
            MoreArray.printArray(one[i]);*/
        }
        return ret;
    }


    /**
     * @param d
     * @return
     */
    public static ArrayList splitArray(double[][] d) {
        ArrayList a = new ArrayList();
        for (int i = 0; i < d[0].length; i++) {
            double[] add = new double[d.length];
            for (int j = 0; j < d.length; j++) {
                add[j] = d[j][i];
            }
            a.add(add);
        }
        return a;
    }

    /**
     * Extracts a column from the 2D array - column numbering starts at 1.
     *
     * @param sarray
     * @param col
     * @return
     */
    public static double[][] extractArray(ArrayList sarray, int col) {

        int max = ((String[][]) sarray.get(0)).length;
        int si = sarray.size();
        double[][] ret = new double[max][sarray.size()];

        //System.out.println("extractArray "+max);
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < si; j++) {

                try {
                    ret[i][j] = Double.parseDouble(((String[][]) sarray.get(j))[i][col]);
                } catch (Exception e) {
                    ret[i][j] = -1;
                }
            }
        }

        return ret;
    }

    /**
     * Extracts a column from the 2D array - column numbering starts at 1.
     *
     * @param sarray
     * @param col
     * @return
     */
    public static double[][] extractArrayDouble(ArrayList sarray, int col) {

        int max = ((double[][]) sarray.get(0)).length;
        int si = sarray.size();
        double[][] ret = new double[max][sarray.size()];

        //System.out.println("extractArray "+max);
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < si; j++) {

                try {
                    ret[i][j] = ((double[][]) sarray.get(j))[i][col];
                } catch (Exception e) {
                    ret[i][j] = -1;
                }
            }
        }

        return ret;
    }

    /**
     * Prints a double array to a String.
     *
     * @param array
     * @return
     */
    public static String toString(int[] array) {
        return toString(array, "\t");
    }

    /**
     * @param array
     * @param delim
     * @return
     */
    public static String toString(int[] array, String delim) {
        StringBuffer ret = new StringBuffer();
        ret.append("");
        ret.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            ret.append(delim);
            ret.append(array[i]);
        }
        return ret.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String[] toStringArray(int[] array) {
        String[] ret = new String[array.length];//"" + array[0];
        for (int i = 0; i < array.length; i++) {
            ret[i] = "" + array[i];
        }
        return ret;
    }

    /**
     * @param array
     * @return
     */
    public static String[] toStringArray(double[] array) {
        String[] ret = new String[array.length];//"" + array[0];
        for (int i = 0; i < array.length; i++) {
            ret[i] = "" + array[i];
        }
        return ret;
    }

    /**
     * @param array
     * @return
     */
    public static String toString(String[] array) {
        String ret = "";
        if (array.length > 0)
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null && array[i].length() > 0)
                    if (i != 0)
                        ret += "\t" + array[i];
                    else
                        ret += array[i];
            }
        return ret;
    }

    /**
     * @param array
     * @return
     */
    public static String toString(boolean[] array) {
        String ret = "";
        if (array.length > 0)
            for (int i = 0; i < array.length; i++) {
                if (i != 0)
                    ret += "\t" + array[i];
                else
                    ret += array[i];
            }
        return ret;
    }

    /**
     * Prints a double array to a String.
     *
     * @param array
     * @return
     */
    public static String[][] toString(double[][] array) {
        return toString(array, "\t", "\n");
    }

    /**
     * @param a
     */
    public static void printArray(double[][] a, String[] x, String[] y) {
        //System.out.println("Printing int array of length " + a.length);
        System.out.println(MoreArray.toString(x));
        for (int i = 0; i < a.length; i++) {
            System.out.print(y[i]);
            for (int j = 0; j < a[0].length; j++) {
                System.out.print("\t" + a[i][j]);
            }
            System.out.print("\n");
        }
    }

    /**
     * @param array
     * @param delim
     * @return
     */
    public static String[][] toString(double[][] array, String delim) {
        return toString(array, delim, "\n");

    }

    /**
     * @param array
     * @param delim
     * @param linedelim
     * @return
     */
    public static String[][] toString(double[][] array, String delim, String linedelim) {
        String[][] ret = new String[array.length][array[0].length];
        for (int i = 0; i < ret.length; i++) {
            int j = 0;
            for (j = 0; j < ret[0].length - 1; j++) {
                ret[i][j] = "" + array[i][j] + delim;
            }
            ret[i][j] = "" + array[i][j] + linedelim;
        }
        return ret;
    }

    /**
     * @param array
     * @return
     */
    public static String[][] toStringWithoutDelim(double[][] array) {
        return toString(array, "", "");
    }

    /**
     * @param array
     * @return
     */
    public static String toString(double[] array) {
        return toString(array, "\t");
    }

    /**
     * @param array
     * @param delim
     * @return
     */
    public static String toString(double[] array, String delim) {
        StringBuffer ret = new StringBuffer("");
        int i = 0;
        for (i = 0; i < array.length - 1; i++) {
            ret.append(array[i]);
            ret.append(delim);
        }
        ret.append(array[i]);
        return ret.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toPage(double[] array) {
        String ret = "";
        int i = 0;
        for (i = 0; i < array.length - 1; i++) {
            ret += "" + array[i] + "\n";
        }
        ret += "" + array[i] + "\n";
        return ret;
    }

    /**
     * @param array
     * @return
     */
    public static String toString(double[] array, int digits) {
        String ret = "";
        int i = 0;
        for (i = 0; i < array.length - 1; i++) {
            ret += "" + mathy.stat.roundUp(array[i], digits) + "\t";
        }
        ret += "" + array[i];
        return ret;
    }

    /**
     * Prints an int array to a String.
     *
     * @param array
     * @return
     */
    public static String[][] toString(int[][] array) {
        return toString(array, "\t", "\n");
    }

    /**
     * Prints an int array to a String.
     *
     * @param array
     * @return
     */
    public static String[][] toString(int[][] array, String delim, String endline) {
        String[][] ret = new String[array.length][array[0].length];
        for (int i = 0; i < ret.length; i++) {
            int j = 0;
            for (j = 0; j < ret[0].length - 1; j++) {
                ret[i][j] = "" + array[i][j] + delim;
            }
            ret[i][j] = "" + array[i][j] + endline;
        }
        return ret;
    }

    /**
     * @param array
     * @param delim
     * @return
     */
    public static String toStringNonZero(int[] array, String delim) {
        StringBuffer ret = new StringBuffer();
        ret.append("");
        if (array[0] != 0)
            ret.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            if (array[i] != 0) {
                if (ret.length() > 0)
                    ret.append(delim);
                ret.append(array[i]);
            }
        }
        return ret.toString();
    }

    /**
     * Extracts the specified subarray from the array.
     *
     * @param src
     * @param start
     * @param stop
     * @return subarray
     */
    public static char[] getSub(char[] src, int start, int stop) {

        char[] ret = new char[stop - start];
        for (int i = start; i < stop; i++)
            ret[i - start] = src[i];

        //System.out.println("getSub return "+start+"\t"+stop+"\tstring "+cur+"\tchar "+chartoString(ret));
        return ret;
    }

    /**
     * Extracts the specified subarray from the array.
     *
     * @param src
     * @param start
     * @param stop
     * @return subarray
     */
    public static double[] getSub(double[] src, int start, int stop) {

        double[] ret = new double[stop - start];
        for (int i = start; i < stop; i++)
            ret[i - start] = src[i];

        //System.out.println("getSub return "+start+"\t"+stop+"\tstring "+cur+"\tchar "+chartoString(ret));
        return ret;
    }

    /**
     * Returns the next instance of the query array in the src String, within the specified start and end coordinates.
     *
     * @param src
     * @param query
     * @param start
     * @param end
     * @return
     */
    public static int getNextInstance(char[] src, char[] query, int start, int end) {

        String cur = "";

        int qlen = query.length;
        int slen = src.length;
        if (end > slen)
            end = slen;

        for (int i = start; i + qlen < end; i++) {

            char[] cur_from_cdata = getSub(src, i, i + qlen);
            //System.out.println("cur_from_cdata "+i+"\t"+chartoString(cur_from_cdata));

            int count = 0;
            for (int j = 0; j < qlen; j++) {

                if (cur_from_cdata[j] == query[j])
                    count++;
            }

            if (count == qlen) {
                //System.out.println("query " + query + "\n");
                return i;
            }
        }
        return -1;
    }


    /**
     * Creates a new double array with the specified length and value.
     *
     * @param array
     * @param rows
     * @param cols
     * @return
     */
    public static double[][] initializeNaN(double[][] array, int rows, int cols) {

        array = new double[rows][cols];
        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                array[i][j] = Double.NaN;
            }
        }

        return array;
    }

    /**
     * Initializes a String array with the specified value.
     *
     * @param n
     * @param value
     * @return initialized array
     */
    public static String[][] initArray(String[][] n, String value) {
        for (int i = 0; i < n.length; i++)
            for (int j = 0; j < n[0].length; j++)
                n[i][j] = value;
        return n;
    }


    /**
     * @param n
     * @param replace
     * @param with
     * @return
     */
    public static String[][] replace(String[][] n, String replace, String with) {
        for (int i = 0; i < n.length; i++)
            for (int j = 0; j < n[0].length; j++) {
                if (n[i][j] == replace) {
                    n[i][j] = with;
                }
            }
        return n;
    }

    /**
     * @param n
     * @param replace
     * @param with
     * @return
     */
    public static String[][] replaceAll(String[][] n, String replace, String with) {
        for (int i = 0; i < n.length; i++)
            for (int j = 0; j < n[0].length; j++)
                n[i][j] = StringUtil.replace(n[i][j], replace, with);
        return n;
    }

    /**
     * @param n
     * @param replace
     * @param with
     * @return
     */
    public static String[] replaceAll(String[] n, String replace, String with) {
        for (int i = 0; i < n.length; i++)
            if (n[i] != null)
                n[i] = StringUtil.replace(n[i], replace, with);
        return n;
    }

    /**
     * @param n
     * @param value
     * @return
     */
    public static String[] initArray(String[] n, String value) {
        for (int i = 0; i < n.length; i++)
            n[i] = value;
        return n;
    }

    /**
     * @param len
     * @param value
     * @return
     */
    public static String[] initArray(int len, String value) {
        String[] ret = new String[len];
        for (int i = 0; i < ret.length; i++)
            ret[i] = value;
        return ret;
    }

    /**
     * @param x
     * @param y
     * @param value
     * @return
     */
    public static String[][] initArray(int x, int y, String value) {
        String[][] n = new String[x][y];
        for (int i = 0; i < n.length; i++)
            for (int j = 0; j < n[0].length; j++)
                n[i][j] = value;
        return n;
    }

    /**
     * Initializes a double array with the specified value.
     *
     * @param n
     * @param value
     * @return initialized array
     */
    public static double[][] initArray(double[][] n, double value) {
        for (int i = 0; i < n.length; i++)
            for (int j = 0; j < n[0].length; j++)
                n[i][j] = value;
        return n;
    }

    /**
     * Initializes a double array with the specified value.
     *
     * @param rows
     * @param cols
     * @param value
     * @return
     */
    public static double[][] initArray(int rows, int cols, double value) {

        double[][] ret = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                ret[i][j] = value;

        return ret;
    }

    /**
     * @param rows
     * @param cols
     * @param value
     * @return
     */
    public static int[][] initArray(int rows, int cols, int value) {

        int[][] ret = new int[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                ret[i][j] = value;

        return ret;
    }

    /**
     * Initiales an double array with the specified length and value.
     *
     * @param size
     * @param value
     * @return
     */
    public static double[] initArray(int size, double value) {
        double[] n = new double[size];
        for (int i = 0; i < n.length; i++)
            n[i] = value;

        return n;
    }

    /**
     * @param size
     * @param first
     * @param incr
     * @return
     */
    public static double[] initArraySeries(int size, double first, double incr) {
        double[] n = new double[size];
        for (int i = 0; i < n.length; i++)
            n[i] = first + ((double) i * incr);
        return n;
    }

    /**
     * Initiales an double array with the specified length and value.
     *
     * @param n
     * @param value
     * @return initialized array
     */
    public static double[] initArray(double[] n, double value) {

        for (int i = 0; i < n.length; i++)
            n[i] = value;

        return n;
    }

    /**
     * Initiales an int array with the specified length and value.
     *
     * @param n
     * @param value
     * @return initialized array
     */
    public static int[][] initArray(int[][] n, int value) {

        for (int i = 0; i < n.length; i++)
            for (int j = 0; j < n[0].length; j++)
                n[i][j] = value;

        return n;
    }

    /**
     * Initializes a double array with the specified value.
     *
     * @param rows
     * @param cols
     * @param value
     * @return
     */
    public static int[][][] initArray(int rows, int cols, int depth, int value) {

        int[][][] ret = new int[rows][cols][depth];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                for (int k = 0; k < depth; k++)
                    ret[i][j][k] = value;

        return ret;
    }


    /**
     * Initiales an int array with the specified length and value.
     *
     * @param n
     * @param value
     * @return initialized array
     */
    public static int[] initArray(int[] n, int value) {

        for (int i = 0; i < n.length; i++)
            n[i] = value;

        return n;
    }

    /**
     * Creates a new int array with the specified length and value.
     *
     * @param length
     * @param value
     * @return initialized array
     */
    public static int[] initArray(int length, int value) {
        int[] n = new int[length];
        for (int i = 0; i < n.length; i++)
            n[i] = value;
        return n;
    }

    /**
     * @param length
     * @param value
     * @return
     */
    public static double[] initArrayD(int length, double value) {
        double[] n = new double[length];
        for (int i = 0; i < n.length; i++)
            n[i] = value;
        return n;
    }


    /**
     * Returns a String from the elements of the array.
     *
     * @param src
     * @return String representation of array elements
     */
    public static String chartoString(char[] src) {

        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < src.length; i++) {
            sb.append(src[i]);
        }
        return sb.toString();
    }

    /**
     * @param p
     * @return
     */
    private double[] pad(double[] p, int l) {
        if (p != null) {
            double[] tmp = p;
            p = new double[l];
            for (int i = 0; i < tmp.length; i++)
                p[i] = tmp[i];
        }
        return p;
    }

    /**
     * Prints all the ArrayList elements as Object.toString().
     *
     * @param arraylist
     */
    public static void print(ArrayList arraylist) {

        for (int i = 0; i < arraylist.size(); i++)
            System.out.println((Object) arraylist.get(i));
    }


    /**
     * Indexes the ArrayList elements to the TreeSet elements.
     *
     * @param list
     * @param treeset
     * @return
     */
    public static int[] createOnetoTwoArrayIndex(ArrayList list, TreeSet treeset) {

        int[] index = new int[list.size()];
        index = initArray(index, -1);

        for (int i = 0; i < list.size(); i++) {

            Double d = (Double) list.get(i);
            SortedSet sub = treeset.tailSet(d);

            int size = sub.size() - 1;
            System.out.println("createOnetoTwoArrayIndex ArrayList TreeSet " + i + "\t" + size);

            try {
                index[i] = size;
            } catch (Exception e) {
                System.out.println("index out of bounds all_to_sorted_index " + index.length + "\tsub.size() - 1 " + size);
                //e.printStackTrace();
            }
        }
        return index;
    }

    /**
     * @param treeset
     * @param top
     * @return
     */
    public static int[] createOnetoTwoArrayIndex(TreeSet treeset, SortedSet top) {

        int[] index = new int[treeset.size()];
        index = initArray(index, -1);

        Iterator it = treeset.iterator();
        int count = 0;
        while (it.hasNext()) {

            Double d = (Double) it.next();//list.get(i);
            SortedSet sub = top.tailSet(d);
            int size = sub.size() - 1;

            System.out.println("createOnetoTwoArrayIndex TreeSet SortedSet " + count + "\t" + size);

            try {
                index[count] = size;
            } catch (Exception e) {
                System.out.println("index out of bounds all_to_sorted_index " + index.length + "\tsub.size() - 1 " + size);
                //e.printStackTrace();
            }
            count++;
        }
        return index;
    }


    /**
     * Adds the elements from the 'data' ArrayList to the 'store' ArrayList.
     *
     * @param store
     * @param data
     * @return
     */
    public static ArrayList addArrayList(ArrayList store, ArrayList data) {
        if (store == null)
            store = new ArrayList();
        if (data != null && data.size() > 0)
            for (int i = 0; i < data.size(); i++)
                store.add(data.get(i));
        return store;
    }

    /**
     * Adds the elements from the 'data' ArrayList to the 'store' ArrayList.
     *
     * @param store
     * @param data
     * @return
     */
    public static ArrayList addArrayListUnique(ArrayList store, ArrayList data) {
        if (store == null)
            store = new ArrayList();
        if (data != null && data.size() > 0)
            for (int i = 0; i < data.size(); i++) {
                Object o = data.get(i);
                if (store.indexOf(o) == -1)
                    store.add(o);
            }
        return store;
    }

    /**
     * Removes null rows from the String 2D, returns a 1D (per row) String array.
     *
     * @param s
     * @return
     */
    public static String[][] removeNullRows(String[][] s) {
        ArrayList a = new ArrayList();
        int count = 0;
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[0].length; j++) {
                if (s[i][j] == null)
                    count++;
            }
            if (count < s[0].length)
                a.add(s[i]);
            count = 0;
        }
        return MoreArray.convtoString2DArray(a, s[0].length);
    }

    /**
     * @param s
     * @return
     */
    public static String[] removeNull(String[] s) {
        ArrayList a = new ArrayList();
        int count = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i] != null)
                a.add(s[i]);
        }
        return MoreArray.convtoStringArray(a);
    }

    /**
     * @param s
     * @return
     */
    public static ArrayList removeWhiteSpace(ArrayList s) {
        ArrayList a = new ArrayList();
        int count = 0;
        for (int i = 0; i < s.size(); i++) {
            String str = (String) s.get(i);
            str = StringUtil.replace(str, " ", "");
            str = StringUtil.replace(str, "/t", "");
            str = StringUtil.replace(str, "/n", "");
            str = StringUtil.replace(str, "/c", "");
            str = StringUtil.replace(str, "/r", "");
            a.set(i, str);
        }
        return a;
    }

    /**
     * @param s
     * @return
     */
    public static String[] removeNullAndEmpty(String[] s) {
        ArrayList a = new ArrayList();
        int count = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i] != null && s[i].length() > 0)
                a.add(s[i]);
        }
        return MoreArray.convtoStringArray(a);
    }

    /**
     * Removed the rows specified as Integer in the ArrayList
     * Row index starts at 1.
     *
     * @param s
     * @param rows
     * @return
     */
    public static String[][] removeRows(String[][] s, ArrayList rows) {
        int total_rows = s.length;
        int[] not = new int[total_rows];
        //int remove = 0;
        for (int i = 0; i < rows.size(); i++) {
            int index = ((Integer) rows.get(i)).intValue();
            if (not[index - 1] != -1) {
                not[index - 1] = 1;
                //remove++;
            }
        }
        ArrayList tmp = new ArrayList();
        int row_len = s[0].length;
        for (int i = 0; i < total_rows; i++) {
            if (not[i] != 1)
                tmp.add(s[i]);
        }
        return MoreArray.convtoString2DArray(tmp, row_len);
    }

    /**
     * Remove array entry, indexing starts at 1.
     *
     * @param s
     * @return
     */
    public static String[] removeEntry(String[] s, int[] rems) {
        Arrays.sort(rems);
        rems = MoreArray.reverse(rems);
        ArrayList tmp = MoreArray.convtoArrayList(s);
        //remove from highest to lowest index
        for (int i = 0; i < rems.length; i++) {
            if (rems[i] != -1)
                tmp.remove(rems[i]);
        }

        return MoreArray.ArrayListtoString(tmp);
    }

    /**
     * Remove array entry, indexing starts at 1.
     *
     * @param s
     * @return
     */
    public static String[] removeEntry(String[] s, int rem) {
        ArrayList tmp = MoreArray.convtoArrayList(s);
        tmp.remove(rem - 1);
        return MoreArray.ArrayListtoString(tmp);
    }

    /**
     * Remove array entry, indexing starts at 1.
     *
     * @param s
     * @return
     */
    public static String[] addEntry(String[] s, String add, int pos) {
        ArrayList tmp = MoreArray.convtoArrayList(s);
        tmp.add(pos - 1, add);
        return MoreArray.ArrayListtoString(tmp);
    }

    /**
     * Row index starts at 1.
     *
     * @param s
     * @param remove
     * @return
     */
    public static String[][] removeRow(String[][] s, int remove) {
        int total_rows = s.length;
        remove--;
        ArrayList tmp = new ArrayList();
        int row_len = s[0].length;
        for (int i = 0; i < total_rows; i++) {
            /*if(i == 0) {
                System.out.println("MoreArray.removeRow " + i+"\t"+remove);
                MoreArray.printArray(s[i]);
            }*/
            if (i != remove)
                tmp.add(s[i]);
            /*else {
                System.out.println("MoreArray.removeRow " + i+"\t"+remove);
                MoreArray.printArray(s[i]);
            }*/
        }
        return MoreArray.convtoString2DArray(tmp, row_len);
    }

    /**
     * Removed the columns specified as Integer in the ArrayList
     * Column index starts at 1.
     *
     * @param s
     * @param cols
     * @return
     */
    public static String[][] removeColumns(String[][] s, ArrayList cols) {
        int orig_cols = s[0].length;
        int rows = s.length;
        int[] not = new int[orig_cols];
        int remove = 0;
        for (int i = 0; i < cols.size(); i++) {
            int index = ((Integer) cols.get(i)).intValue();
            if (index < 0) {
                //System.out.println("MoreArray.extractColumnStr column index < 0, " + index
                //       + ". Note that column indexing begins at 1.");
            } else {
                try {
                    if (not[index - 1] != 1) {
                        not[index - 1] = 1;
                        remove++;
                    }
                } catch (Exception e) {
                    System.out.println("MoreArray.removeColumns " + not.length + "\t" + (index - 1));
                    e.printStackTrace();
                }
            }
        }
        ArrayList tmp = new ArrayList();
        int new_len = orig_cols - remove;
        //System.out.println("removeColumns new_len " + new_len + "\t" + orig_cols + "\t" + remove);

        for (int i = 0; i < rows; i++) {
            String[] add = new String[new_len];
            int count = 0;
            for (int j = 0; j < orig_cols; j++) {
                if (not[j] != 1) {
                    add[count] = s[i][j];
                    count++;
                }
            }
            //System.out.println("removeColumns " + add.length + "\t" + tmp.size());
            tmp.add(add);
        }
        //System.out.println("removeColumns " + tmp.size());
        return MoreArray.convtoString2DArray(tmp, new_len);
    }

    /**
     * Removed the columns specified by the int
     * Column index starts at 1.
     *
     * @param s
     * @param remove
     * @return
     */
    public static String[][] removeColumn(String[][] s, int remove) {
        remove--;
        int orig_cols = s[0].length;
        int rows = s.length;
        ArrayList tmp = new ArrayList();
        int new_len = orig_cols - 1;
        for (int i = 0; i < rows; i++) {
            String[] add = new String[new_len];
            int count = 0;
            for (int j = 0; j < orig_cols; j++) {
                if (j != remove) {
                    add[count] = s[i][j];
                    count++;
                }
            }
            //System.out.println("removeColumns " + add.length + "\t" + tmp.size());
            tmp.add(add);
        }
        //System.out.println("removeColumns " + tmp.size());
        return MoreArray.convtoString2DArray(tmp, new_len);
    }

    /**
     * Removed the columns specified by the int
     * Column index starts at 1.
     *
     * @param s
     * @param remove
     * @return
     */
    public static int[][] removeColumn(int[][] s, int remove) {
        remove--;
        int orig_cols = s[0].length;
        int rows = s.length;
        ArrayList tmp = new ArrayList();
        int new_len = orig_cols - 1;
        for (int i = 0; i < rows; i++) {
            int[] add = new int[new_len];
            int count = 0;
            for (int j = 0; j < orig_cols; j++) {
                if (j != remove) {
                    add[count] = s[i][j];
                    count++;
                }
            }
            //System.out.println("removeColumns " + add.length + "\t" + tmp.size());
            tmp.add(add);
        }
        //System.out.println("removeColumns " + tmp.size());
        return MoreArray.convtoint2DArray(tmp, new_len);
    }

    /**
     * Rotates a 2D array 90 degrees clockwise.
     *
     * @param matrix
     * @return
     */
    public static String[][] rotateMatrix90(String[][] matrix) {
        //System.out.println("rotateMatrix ");
        String[][] ret = new String[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                ret[i][j] = matrix[matrix.length - 1 - j][i];
            }
        }
        return ret;
    }

    /**
     * @param matrix
     * @return
     */
    public static double[][] rotateMatrix90(double[][] matrix) {
        //System.out.println("rotateMatrix ");
        double[][] ret = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                ret[i][j] = matrix[matrix.length - 1 - j][i];
            }
        }
        return ret;
    }

    /**
     * Rotates a 2D array 90 degrees clockwise and moves the row labels.
     *
     * @param matrix
     * @return
     */
    public static String[][] rotateMatrix90MoveRowLabels(String[][] matrix) {
        //System.out.println("rotateMatrix ");
        String[][] ret = new String[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                ret[i][j] = matrix[matrix.length - 1 - j][i];
            }
        }
        String[][] ret2 = new String[matrix[0].length][matrix.length];
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[0].length - 1; j++) {
                ret2[i][j + 1] = ret[i][j];
            }
        }
        for (int i = 0; i < ret.length; i++) {
            ret2[i][0] = ret[i][ret[0].length - 1];
        }


        return ret2;
    }

    /**
     * Returns the maximum size of and ArrayList in the ArrayList[].
     *
     * @param a
     * @return
     */
    public static int maxArrayListElementSize(ArrayList[] a) {
        double ret = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            int size = a[i].size();
            if (size > ret)
                ret = size;
        }
        return (int) ret;
    }

    /**
     * Returns the minimum size of and ArrayList in the ArrayList[].
     *
     * @param a
     * @return
     */
    public static int minArrayListElementSize(ArrayList[] a) {
        double ret = Double.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            int size = a[i].size();
            if (size < ret)
                ret = size;
        }
        return (int) ret;
    }

    /**
     * @param a
     * @param rem
     * @return
     */
    public static String[] remove(String[] a, int rem) {
        /*String[] ret = new String[a.length - 1];
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            if (i != rem) {
                ret[count] = a[i];
                count++;
            }
        }*/

        String[] ret = new String[a.length - 1];
        if (rem != 0)
            System.arraycopy(a, 0, ret, 0, rem);
        System.arraycopy(a, rem + 1, ret, rem, a.length - rem - 1);
        return ret;
    }

    /**
     * @param a
     * @param rem
     * @return
     */
    public static double[] remove(double[] a, int rem) {
        /* double[] ret = new double[a.length - 1];
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            if (i != rem) {
                ret[count] = a[i];
                count++;
            }
        }*/

        double[] ret = new double[a.length - 1];
        if (rem != 0)
            System.arraycopy(a, 0, ret, 0, rem);
        System.arraycopy(a, rem + 1, ret, rem, a.length - rem - 1);
        return ret;
    }

    /**
     * @param a
     * @param rem
     * @return
     */
    public static int[] remove(int[] a, int rem) {
        /*int[] ret = new int[a.length - 1];
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            if (i != rem) {
                ret[count] = a[i];
                count++;
            }
        }*/

        int[] ret = new int[a.length - 1];
        if (rem != 0)
            System.arraycopy(a, 0, ret, 0, rem);
        System.arraycopy(a, rem + 1, ret, rem, a.length - rem - 1);
        return ret;
    }

    /**
     * @param a
     * @param rem
     * @return
     */
    public static int[] removeByVal(int[] a, int[] rem) throws Exception {
        int[] ret = a;
        for (int i = 0; i < rem.length; i++) {
            int ind = MoreArray.getArrayInd(ret, rem[i]);
            while (ind != -1) {
                //System.out.println("removeByVal " + i + "\t" + rem[i] + "\t" + ind + "\t" + ret.length);
                ret = remove(ret, ind);
                ind = MoreArray.getArrayInd(ret, rem[i]);
            }
        }
        return ret;
    }

    /**
     * @param array
     * @param val
     * @param offset
     * @return
     */
    public static int[] offsetGreater(int[] array, int val, int offset) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] > val)
                array[i] += offset;
        }
        return array;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static int[] add(int[] a, int b) {
        int[] ret = new int[a.length + 1];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        ret[ret.length - 1] = b;
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static int[] add(int[] a, int[] b) {
        int[] ret = new int[a.length + b.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        for (int i = a.length; i < a.length + b.length; i++) {
            ret[i] = b[i - a.length];
        }
        return ret;
    }

    /**
     * Subtracts a double array to each (double[]) element in the ArrayList.
     *
     * @param a
     * @param b
     * @return
     */
    public static ArrayList subtract(ArrayList a, double[] b) {
        for (int i = 0; i < a.size(); i++) {
            double[] data = (double[]) a.get(i);
            data = stat.subtract(data, b);
            a.set(i, data);
        }
        return a;
    }

    /**
     * Adds a double array to each (double[]) element in the ArrayList.
     *
     * @param a
     * @param b
     * @return
     */
    public static ArrayList add(ArrayList a, double[] b) {
        for (int i = 0; i < a.size(); i++) {
            double[] data = (double[]) a.get(i);
            data = stat.add(data, b);
            a.set(i, data);
        }
        return a;
    }

    /**
     * Adds a int array to each (int[]) element in the ArrayList.
     *
     * @param a
     * @param b
     * @return
     */
    public static ArrayList add(ArrayList a, int[] b) {
        for (int i = 0; i < a.size(); i++) {
            int[] data = (int[]) a.get(i);
            data = stat.add(data, b);
            a.set(i, data);
        }
        return a;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static ArrayList addElements(ArrayList a, int[] b) {
        if (a == null)
            a = new ArrayList();
        for (int i = 0; i < b.length; i++) {
            a.add(new Integer(b[i]));
        }
        return a;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static int[] join(int[] a, int[] b) {
        int[] ret = new int[a.length + b.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            ret[i + a.length] = b[i];
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] join(double[] a, double[] b) {
        double[] ret = new double[a.length + b.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            ret[i + a.length] = b[i];
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static int[] removeWhereEqual(int[] a, int[] b) {
        //ArrayList ar = new ArrayList();
        int[] ret = null;
        if (a.length != b.length) {
            System.out.println("removeWhereEqual array lengths do not match: " + a.length + "\t" + b.length);
            return null;
        } else {
            int[] remove = new int[a.length];
            int count = 0;
            for (int i = 0; i < a.length; i++) {
                if (a[i] == b[i]) {
                    remove[i] = 1;
                    count++;
                }
            }
            count = 0;
            ret = new int[a.length - count];
            for (int i = 0; i < a.length; i++) {
                if (remove[i] != 1) {
                    ret[count] = a[i];
                    count++;
                }
            }
        }
        return ret;
    }


    /**
     * @param a
     * @param t
     * @return
     */
    public static ArrayList truncateList(ArrayList a, int t) {
        while (a.size() > t) {
            a.remove(a.size() - 1);
        }
        return a;
    }

    /**
     * @param ar
     */
    public static int arrayofArrayListsSize(ArrayList[] ar) {
        int ret = 0;
        if (ar != null) {
            int index = 0;
            while (index < ar.length) {
                if (ar[index] != null)
                    ret += ar[index].size();
                index++;
            }
        }
        return ret;
    }

    /**
     * @param ar
     * @return
     */
    public static int arrayofArrayListNonNullSize(ArrayList[] ar) {
        int ret = 0;
        if (ar != null) {
            int index = 0;
            while (index < ar.length)
                if (ar[index] != null)
                    ret++;
        }
        return ret;
    }

    /**
     * Counts non-null entries in the ArrayList array.
     *
     * @param ar
     * @return
     */
    public static ArrayList[] removeNullArrayListinArray(ArrayList[] ar) {
        int nonnull = arrayofArrayListNonNullSize(ar);
        ArrayList[] ret = new ArrayList[nonnull];
        int count = 0;
        for (int i = 0; i < ar.length; i++) {
            if (ar[i] != null) {
                //ret[count] = MoreArray.copyArrayList(ar[i]);
                ret[count] = ar[i];
                count++;
            }
        }
        return ret;
    }

    /**
     * @param ar
     * @param size
     * @return
     */
    public static ArrayList[] removeArrayListLessThanSizeinArray(ArrayList[] ar, int size) {
        int greater = 0;
        for (int i = 0; i < ar.length; i++) {
            if (ar[i].size() >= size) {
                greater++;
            }
        }
        ArrayList[] ret = new ArrayList[greater];
        int count = 0;
        for (int i = 0; i < ar.length; i++) {
            if (ar[i].size() >= size) {
                ret[count] = MoreArray.copyArrayList(ar[i]);
                count++;
            }
        }
        return ret;
    }


    /**
     * '
     *
     * @param ar
     * @param size
     * @param extra
     * @return
     */
    public static ArrayList removeArrayListLessThanSizeinArray(ArrayList[] ar, int size, ArrayList extra) {
        int[] rem = new int[ar.length];
        for (int i = 0; i < ar.length; i++) {
            if (ar[i].size() < size) {
                rem[i] = 1;
            }
        }
        int count = 0;
        for (int i = 0; i < rem.length; i++) {
            if (rem[i] == 1) {
                extra.remove(i - count);
                count++;
            }
        }
        return extra;
    }


    /**
     * @param ar
     * @param rem
     * @return
     */
    public static ArrayList removeArrayListEntries(ArrayList ar, int[] rem) {
        System.out.println("removeArrayListEntries");
        int count = 0;
        for (int i = 0; i < ar.size(); i++) {
            if (rem[i + count] == 1) {
                ar.remove(i);
                i--;
                count++;
                System.out.println("removing " + i + "\t" + count);
            }
        }
        return ar;
    }

    /**
     * @param ar
     * @param rem
     * @return
     */
    public static ArrayList removeArrayListEntries(ArrayList ar, ArrayList rem) {
        System.out.println("removeArrayListEntries");
        int[] remint = new int[ar.size()];
        for (int i = 0; i < rem.size(); i++) {
            int c = ((Integer) rem.get(i)).intValue();
            remint[c] = 1;
        }
        return removeArrayListEntries(ar, remint);
    }

    /**
     * @param a
     * @param b
     * @param insert
     * @return
     */
    public static ArrayList insertArrayListElementsintoArrayList(ArrayList a, ArrayList b, int insert) {
        for (int i = 0; i < b.size(); i++) {
            a.add(insert + i, b.get(i));
        }
        return a;
    }

    /**
     * Returns an ArrayList array of the specified size, initializing each ArrayList.
     *
     * @param size
     * @return
     */
    public static ArrayList[] initArrayListArray(int size) {
        ArrayList[] ret = new ArrayList[size];
        for (int i = 0; i < size; i++)
            ret[i] = new ArrayList();
        return ret;
    }


    /**
     * @param a
     * @return
     */
    public static int[] getArrayListArray(ArrayList[] a) {
        int[] ret = new int[a.length];
        for (int i = 0; i < a.length; i++)
            ret[i] = a[i].size();
        return ret;
    }

    /**
     * Returns an ArrayList of the specified size.
     *
     * @param size
     * @return
     */
    public static ArrayList initArrayList(int size) {
        ArrayList ret = new ArrayList(size);
        for (int i = 0; i < size; i++)
            ret.add(null);
        return ret;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public static ArrayList[][] initArrayList(int x, int y) {
        ArrayList[][] ret = new ArrayList[x][y];
        for (int i = 0; i < x; i++)
            for (int j = 0; j < y; j++)
                ret[i][j] = new ArrayList();
        return ret;
    }

    /**
     * Returns an ArrayList of the specified size.
     *
     * @param size
     * @return
     */
    public static List initList(int size) {
        List ret = new ArrayList(size);
        for (int i = 0; i < size; i++)
            ret.add(null);
        return ret;
    }

    /**
     * Returns an ArrayList of ArrayLists, of the specified size.
     *
     * @param size
     * @return
     */
    public static ArrayList initArrayListList(int size) {
        ArrayList ret = new ArrayList(size);
        for (int i = 0; i < size; i++)
            ret.add(new ArrayList());
        return ret;
    }

    /**
     * Returns an ArrayList of ArrayLists, of the specified size.
     *
     * @param size
     * @return
     */
    public static ArrayList initArrayListList(int size, String init) {
        ArrayList ret = new ArrayList(size);
        for (int i = 0; i < size; i++)
            ret.add(init);
        return ret;
    }


    /**
     * @param size
     * @return
     */
    public static HashMap[] initHashMapArray(int size) {
        HashMap[] ret = new HashMap[size];
        for (int i = 0; i < size; i++)
            ret[i] = new HashMap();
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static boolean arrayEquals(int[] a, int[] b) {
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static boolean arrayEquals(double[] a, double[] b) {
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static boolean arrayEqualsIgnoreNaN(double[] a, double[] b) {
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (!Double.isNaN(a[i]) && !Double.isNaN(b[i]) && a[i] != b[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static boolean arrayEquals(String[] a, String[] b) {
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static boolean arrayEqualsIgnoreNull(String[] a, String[] b) {
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != null && b[i] != null && !a[i].equals(b[i]))
                    return false;
            }
            return true;
        }
        return false;
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public static int[] diffArray(int[] a, int[] b) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < b.length; i++) {
            boolean f = false;
            for (int j = 0; j < a.length; j++) {
                if (b[i] == a[j]) {
                    f = true;
                    break;
                }
            }
            if (!f)
                list.add(new Integer(b[i]));
        }
        return MoreArray.ArrayListtoInt(list);
    }


    /**
     * @param o
     * @return
     */
    public static String[] toString(Object[] o) {
        String[] ret = new String[o.length];
        for (int i = 0; i < o.length; i++) {
            ret[i] = o[i].toString();
        }
        return ret;
    }

    /**
     * @param in
     * @param targetsize
     * @param step
     * @return
     */
    public final static double[] expandVector(double[] in, int targetsize, int step) {
        int maxGene = in.length - 1;
        double[] out = new double[targetsize];
//do final borders
        for (int i = 0; i < in.length - 1; i++) {
            double geneincr = (in[i + 1] - in[i]) / (double) step;
//fill borders based on direct ratio
            for (int a = 0; a < step; a++) {
                out[i * step + a] = in[i] + a * geneincr;
            }
        }
        out[maxGene * step] = in[maxGene];
        return out;
    }

    /**
     * @param rawe
     * @param finale
     * @return
     */
    public final static double[][] expandExpr(double[][] rawe, double[][] finale, int step) {
        //genes
        for (int i = 0; i < rawe.length - 1; i++) {
            int curIoff = i * step;
//exps
            for (int j = 0; j < rawe[0].length - 1; j++) {
                int curJoff = j * step;
/*System.out.println("index i " + i + "\tj " + j +
"\tcurIoff " + curIoff + "\tcurJoff " + curJoff);*/
                double geneincr = (rawe[i + 1][j] - rawe[i][j]) / (double) step;
                double expincr = (rawe[i][j + 1] - rawe[i][j]) / (double) step;
                double combincr = (rawe[i + 1][j + 1] - rawe[i][j]) / (double) step;

//fill borders and diagonal based on direct ratio
                for (int a = 0; a < step; a++) {
                    finale[curIoff + a][curJoff] = rawe[i][j] + a * geneincr;
                    finale[curIoff][curJoff + a] = rawe[i][j] + a * expincr;
                    finale[curIoff + a][curJoff + a] = rawe[i][j] + a * combincr;
                }

                //fill left interior based on border-to-filled-diagonal ratio
                for (int a = 2; a < step; a++) {
                    int i1 = curIoff + a;
                    int j1 = curJoff + a;
                    if (i1 < finale.length - 1 && j1 < finale[0].length - 1) {
                        /*System.out.println("index i " + i + "\tj " + j +
             "\tcurIoff " + curIoff + "\tcurJoff " + curJoff + "\ti1 "
             + i1 + "\tfinale.length " + finale.length + "\tfinale[0].length " + finale[0].length);*/
                        double fillincr = (finale[i1][j1] - finale[i1][curJoff]) / (double) a;
                        for (int c = 1; c < a; c++) {
                            finale[i1][curJoff + c] = finale[i1][curJoff] + c * fillincr;
                        }
                    } else {
                        System.out.println("non symmetric " + i + "\t" + j + "\t" + curIoff + "\t" + curJoff);
                    }
                }
                //fill right interior based on border-to-filled-diagonal ratio
                for (int b = 2; b < step; b++) {
                    int j1 = curJoff + b;
                    int i1 = curIoff + b;
                    if (i1 < finale.length - 1 && j1 < finale[0].length - 1) {
                        /*System.out.println("index i " + i + "\tj " + j +
             "\tcurIoff " + curIoff + "\tcurJoff " + curJoff + "\ti1 "
             + i1 + "\tfinale.length " + finale.length + "\tfinale[0].length " + finale[0].length);*/
                        double fillincr = (finale[i1][j1] - finale[curIoff][j1]) / (double) b;
                        for (int d = 1; d < b; d++) {
                            finale[curIoff + d][j1] = finale[curIoff][j1] + d * fillincr;
                        }
                    } else {
                        System.out.println("non symmetric " + i + "\t" + j + "\t" + curIoff + "\t" + curJoff);
                    }
                }
            }
        }

        int maxGene = rawe.length - 1;
        int maxExp = rawe[0].length - 1;
//do final borders
        for (int i = 0; i < rawe.length - 1; i++) {
            double geneincr = (rawe[i + 1][maxExp] - rawe[i][maxExp]) / (double) step;
//fill borders based on direct ratio
            for (int a = 0; a < step; a++) {
                finale[i * step + a][maxExp * step] = rawe[i][maxExp] + a * geneincr;
            }
        }
        for (int i = 0; i < rawe[0].length - 1; i++) {
            double geneincr = (rawe[maxGene][i + 1] - rawe[maxGene][i]) / (double) step;
//fill borders based on direct ratio
            for (int a = 0; a < step; a++) {
                finale[maxGene * step][i * step + a] = rawe[maxGene][i] + a * geneincr;
            }
        }
        //do final corner
        finale[maxGene * step][maxExp * step] = rawe[maxGene][maxExp];

        return finale;
    }


    /**
     * @param d
     * @return
     */
    public final static int[][] doubletoIntMatrix(double[][] d) {
        int[][] ret = new int[d.length][d[0].length];
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[0].length; j++) {
                ret[i][j] = (int) d[i][j];
            }
        }
        return ret;
    }

    /**
     * @param d
     * @return
     */
    public final static int[] doubletoInt(double[] d) {
        int[] ret = new int[d.length];
        for (int i = 0; i < d.length; i++) {
            ret[i] = (int) d[i];
        }
        return ret;
    }

    /**
     * @param ar
     * @param width
     * @return
     */
    public final static double[][] listto2DArray(ArrayList ar, int width) {
        int ysize = 0;
        for (int i = 0; i < ar.size(); i++) {
            double[][] cur = (double[][]) ar.get(i);
            ysize += cur.length;
        }
        double[][] ret = new double[ysize][width];
        int offset = 0;
        for (int i = 0; i < ar.size(); i++) {
            double[][] cur = (double[][]) ar.get(i);
            for (int j = 0; j < cur.length; j++) {
                ret[offset + j] = cur[j];
            }
            offset += cur.length;
        }
        return ret;
    }


    /**
     * @param ar
     * @return
     */
    public final static String[] toOneArray(ArrayList ar) {
        int len = 0;
        for (int i = 0; i < ar.size(); i++) {
            String[] cur = (String[]) ar.get(i);
            len += cur.length;
        }
        String[] ret = new String[len];
        int offset = 0;
        for (int i = 0; i < ar.size(); i++) {
            String[] cur = (String[]) ar.get(i);
            for (int j = 0; j < cur.length; j++) {
                ret[offset + j] = cur[j];
            }
            offset += cur.length;
        }
        return ret;
    }

    /**
     * @param I
     * @return
     */
    public final static int[] intfromIntegerArray(Set<Integer> I) {
       /* int[] ret = new int[I.length];
        for (int i = 0; i < I.length; i++) {
            ret[i] = I[i].intValue();
        }*/
        int[] ret = new int[I.size()];

        int index = 0;

        for (Integer i : I) {
            ret[index++] = i; //note the autounboxing here
        }
        return ret;
    }


    /**
     * @param index
     * @param s
     * @return
     */
    public final static String[] getIndexed(int[] index, String[] s) {
        ArrayList ar = new ArrayList();

        int count = 0;
        for (int i = 0; i < index.length; i++) {
            if (index[i] > -1) {
                ar.add(s[index[i]]);
            } else {
                //System.out.println("getIndexed " + i + "\t" + index[i]);
                count++;
            }
        }

        if (count > 0) {
            System.out.println("getIndexed " + count + " indices where < 0");
        }
        return MoreArray.ArrayListtoString(ar);
    }
}