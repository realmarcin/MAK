package util;

import java.util.ArrayList;


/**
 * Class with methods to read/write from/to tab delim text file.
 */
public class TabFile extends DelimFile {

    final static String delimiter = "\t";


    public TabFile() {

    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static ArrayList[] read(String f, boolean debug) {
        return read(f, delimiter, debug);
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static ArrayList[] read(String f) {
        return read(f, delimiter, false);
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static ArrayList readtoList(String f) {
        return readtoList(f, delimiter);
    }

    /**
     * @param f
     * @return
     */
    public final static ArrayList readtoListWithCommentsandXLabels(String f) {
        return readtoListWithCommentsandXLabels(f, delimiter);
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static String[][] readtoArray(String f, boolean debug) {
        return readtoArray(f, delimiter, debug);
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static String[][] readtoArray(String f) {
        return readtoArray(f, delimiter, false);
    }

    /**
     * @param f
     * @param skip
     * @return
     */
    public final static String[][] readtoArray(String f, int skip, boolean debug) {
        return readtoArray(f, delimiter, skip, debug);
    }

    /**
     * Method read tab delim data from a file truncating all rows to the least number of columns found.
     *
     * @param f
     * @return
     */
    public final static String[][] readtoArraytrimR(String f, boolean debug) {
        return readtoArraytrimR(f, delimiter, debug);
    }

    /**
     * Method read tab delim data from a file truncating all rows to the least number of columns found.
     *
     * @param f
     * @return
     */
    public final static String[][] readtoArraytrimR(String f) {
        return readtoArraytrimR(f, delimiter, false);
    }

    /**
     * Method read tab delim data from a file.
     *
     * @param f
     * @return ArrayList of ArrayLists
     */
    public final static String[] readtoSingleArray(String f, boolean debug) {
        return readtoSingleArray(f, delimiter, debug);
    }

    /**
     * Method to write tab delim data from an ArrayList of ArrayLists.
     *
     * @param a ArrayList of ArrayLists
     * @param f input file
     */
    public final static void write(String[][] a, String f) {
        write(a, f, delimiter);
    }

    /**
     * @param a
     * @param f
     */
    public final static void write(String[] a, String f) {
        write(a, f, delimiter);
    }

    /**
     * @param a
     * @param f
     */
    public final static void write(String[][] a, String[] xlabels, String f) {
        write(a, f, xlabels, delimiter, null);
    }

    /**
     * @param a
     * @param f
     */
    public final static void write(String[][] a, String f, String[] xlabels, String[] ylabels) {
        write(a, f, xlabels, ylabels, delimiter, null, null);
    }

    /**
     * @param a
     * @param f
     */
    public final static void write(int[][] a, String f, String[] xlabels, String[] ylabels) {
        write(a, f, xlabels, ylabels, delimiter, null, null);
    }

    /**
     * @param a
     * @param f
     */
    public final static void write(double[][] a, String f, String[] xlabels, String[] ylabels) {
        write(a, f, xlabels, ylabels, delimiter, null, null);
    }

    /**
     * @param a
     * @param f
     */
    public final static void write(String[][] a, String f, String[] xlabels, String[] ylabels, String header) {
        write(a, f, xlabels, ylabels, delimiter, header, null);
    }

    /**
     * @param a
     * @param f
     */
    public final static void write(String[] a, String f, String[] xlabels, String[] ylabels) {
        write(a, f, xlabels, ylabels, delimiter, null, null);
    }

    /**
     * @param a
     * @param f
     */
    public final static void write(String[] a, String[] xlabels, String f) {
        write(a, f, xlabels, delimiter, null);
    }

    /**
     * Method to write tab delim data from an ArrayList of ArrayLists.
     *
     * @param a ArrayList of ArrayLists
     * @param f input file
     */
    public final static void writeWithHeader(String[][] a, String f, String header) {
        write(a, f, delimiter, header);
    }

    /**
     * Method to write tab delim data from an ArrayList of ArrayLists.
     *
     * @param a ArrayList of ArrayLists
     * @param f input file
     */
    public final static void write(ArrayList[] a, String f) {
        write(a, f, delimiter);
    }


    /**
     * Method read tab delim data from a file and return a double[][] array.
     *
     * @param f
     * @return double[][] array
     */
    public final static double[][] readtoDoubleArray(String f, boolean debug) {
        return readtoDoubleArray(f, delimiter, debug);
    }

    /**
     * Method read tab delim data from a file and return a double[][] array.
     *
     * @param f
     * @return double[][] array
     */
    public final static double[][] readtoDoubleArray(String f) {
        return readtoDoubleArray(f, delimiter, false);
    }

    /**
     * Method read delimited data from a file.
     *
     * @param f
     * @return string array
     */
    public final static String[] readtoArrayOne(String f, boolean debug) {
        return readtoArrayOne(f, delimiter, debug);
    }

    /**
     * Method read delimited data from a file.
     *
     * @param f
     * @return string array
     */
    public final static String[] readtoArrayOne(String f) {
        return readtoArrayOne(f, delimiter, false);
    }

}
