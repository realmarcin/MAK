package mathy;

import dtype.DTriple;
import util.MoreArray;
import util.TabFile;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class representing the Matrix object and family of methods.
 * <p/>
 * Version 2.0 1/1/04
 * Version 1.0 1/1/00
 * Author Marcin P. Joachimiak, Ph.D.
 */
public final class Matrix {

    public double[][] mat;
    public int rows = -1;
    public int columns = -1;
    public String[][] labels = null;
    public String[] xlabels;
    public String[] ylabels;
    public int imax;
    public int jmax;

    /**
     * basic constructor
     */
    public Matrix() {
        mat = null;
    }

    /**
     * Reads a matrix from String s.
     *
     * @param s
     */
    public Matrix(String s) {
        mat = readMatrix(s);
        setDimensions();
    }

    /**
     * Reads a matrix from String s.
     *
     * @param s
     * @param data
     */
    public Matrix(String s, boolean data) {
        if (data) {
            mat = parseMatrix(s);
        } else
            mat = readMatrix(s);
        setDimensions();
    }

    /**
     * @param path
     * @param useXlabels
     * @param useYlabels
     */
    public Matrix(String path, boolean useXlabels, boolean useYlabels) {
        String[][] data = TabFile.readtoArray(path);
        ArrayList list = new ArrayList();
        list.add(new Integer(1));
        if (useYlabels) {
            //first row is assumed blank
            String[] genes = MoreArray.extractColumnStr(data, 1);
            ArrayList tmp = MoreArray.convtoArrayList(genes);
            if (useXlabels) {
                tmp.remove(0);
            }
            ylabels = MoreArray.ArrayListtoString(tmp);
            //remove the ylabels column
            data = MoreArray.removeColumns(data, list);
        }
        if (useXlabels) {
            if (!useYlabels) {
                //remove the name column
                data = MoreArray.removeColumns(data, list);
            }
            xlabels = data[0];
            //remove the ylabels column
            data = MoreArray.removeRows(data, list);
        }
        mat = MoreArray.convfromString(data);
    }


    /**
     * creates a new double matrix.
     *
     * @param a
     * @param b
     */
    public Matrix(int a, int b) {
        mat = new double[a][b];
        setDimensions();
    }

    /**
     * @param pass
     */
    public Matrix(double[][] pass) {
        mat = pass;
        setDimensions();
    }

    /**
     * @param pass
     */
    public Matrix(double[][] pass, String[] x, String[] y) {
        mat = pass;
        xlabels = x;
        ylabels = y;
        setDimensions();
    }

    /**
     * @param pass
     */
    public Matrix(float[][] pass) {
        mat = new double[pass.length][pass.length];
        for (int ja = 0; ja < pass.length; ja++)
            for (int jb = 0; jb < pass.length; jb++)
                mat[ja][jb] = (double) pass[ja][jb];
        setDimensions();
    }

    /**
     * @param pass
     */
    public Matrix(int[][] pass) {
        mat = new double[pass.length][pass.length];
        for (int ja = 0; ja < pass.length; ja++)
            for (int jb = 0; jb < pass.length; jb++)
                mat[ja][jb] = (double) pass[ja][jb];
        setDimensions();
    }

    /**
     * Prints the matrix to stdout.
     */
    public final void printMatrix() {
        for (int i = 0; i < mat.length; i++) {
            if (xlabels != null)
                System.out.println(xlabels[i]);
            for (int j = 0; j < mat[i].length; j++) {
                if (labels != null) {
                    System.out.println(i + "\t" + j + "\t" + labels[i][j] + "\t" + mat[i][j]);
                } else {
                    System.out.println(i + "\t" + j + "\t" + mat[i][j]);
                }

            }
        }
    }

    /**
     * Prints the array to stdout.
     *
     * @param a
     */
    public static void print(float[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println(i + "\t" + a[i]);
        }
    }

    /**
     * Prints the array to stdout.
     *
     * @param a
     */
    public static void print(double[] a) {
        for (int i = 0; i < a.length; i++) {

            System.out.println(i + "\t" + a[i]);
        }
    }

    /**
     * Prints the array to stdout.
     *
     * @param a
     */
    public static void print(double[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                System.out.println(i + "\t" + j + "\t" + a[i][j]);
            }
        }
    }


    /**
     * Prints the array to stdout.
     *
     * @param a
     */
    public static void print(int[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println(i + "\t" + a[i]);
        }
    }

    /**
     * Sets the dimensions for the Matrix mat double[][] array.
     */
    public final void setDimensions() {
        if (mat != null) {
            columns = mat.length;
            rows = mat[0].length;
        }
    }


    /**
     * Returns an int[][] version of the current Matrix mat - up to decimal point.
     *
     * @return
     */
    public final int[][] retIntMat() {
        int[][] ret = new int[mat.length][mat.length];
        for (int ja = 0; ja < mat.length; ja++)
            for (int jb = 0; jb < mat.length; jb++) {
                String curstr = "" + mat[ja][jb];
                ret[ja][jb] = Integer.parseInt(curstr.substring(0, curstr.indexOf(".")));
            }
        return ret;
    }

    /**
     * updates individual matrix entry
     *
     * @param a
     * @param b
     * @param x
     */
    public final void updateEntry(int a, int b, double x) {
        mat[a][b] = x;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public final double retEntry(int a, int b) {
        double ret = -1;
        if (mat != null) {
            ret = mat[a][b];
        }
        return ret;
    }

    /**
     * @param a
     * @param x
     */
    public final void updateRow(int a, double[] x) {
        for (int i = 0; i < mat[0].length; i++) {
            mat[a][i] = x[i];
        }
    }

    /**
     * @param a
     * @param x
     */
    public final void updateColumn(int a, double[] x) {
        for (int i = 0; i < mat.length; i++) {
            mat[i][a] = x[i];
        }
    }

    /**
     * Removes the specified column from the matrix, changes the column dimensions by -1.
     *
     * @param a
     */
    public final void removeColumn(int a) {
        double[][] copymat = new double[mat.length - 1][mat[0].length];

        for (int i = 0; i < mat.length; i++) {
            if (i < a)
                copymat[i] = mat[i];
            else if (i > a)
                copymat[i - 1] = mat[i];
        }

        mat = copymat;
    }

    /**
     * Removes the specified column from the matrix, changes the column dimensions by -1.
     *
     * @param a
     */
    public final void removeRow(int a) {
        double[][] copymat = new double[mat.length][mat[0].length - 1];

        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                if (j < a)
                    copymat[i][j] = mat[i][j];
                else if (j > a)
                    copymat[i][j - 1] = mat[i][j];
            }
        }
        mat = copymat;
    }

    /**
     * Removes the specified column from the matrix, changes the column dimensions by -1.
     *
     * @param a
     */
    public final void removeRowColumnEntry(int a) {
        double[][] copymat = new double[mat.length - 1][mat[0].length - 1];

        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                if (i < a && j < a)
                    copymat[i][j] = mat[i][j];
                else if (i < a && j > a)
                    copymat[i][j - 1] = mat[i][j];
                else if (i > a && j < a)
                    copymat[i - 1][j] = mat[i - 1][j];
                else if (i > a && j > a)
                    copymat[i - 1][j - 1] = mat[i - 1][j];
            }
        }
        mat = copymat;
    }

    /**
     * @param k
     * @return
     */
    public final double[] stripFirstColumn(double[][] k) {
        double[] ret = new double[k.length];
        System.arraycopy(k[0], 0, ret, 0, k.length);
        return ret;
    }

    /**
     * @return
     */
    public final double[] stripFirstColumn() {

        double[] ret = new double[mat.length];
        if (mat != null) {
            System.arraycopy(mat[0], 0, ret, 0, mat.length);
        }
        return ret;
    }

    /**
     * @param data
     * @param f
     * @return
     */
    public static int[][] initValues(int[][] data, int f) {

        if (data != null) {

            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data[0].length; j++) {
                    data[i][j] = f;
                }
        }
        return data;
    }

    /**
     * @param data
     * @param f
     * @return
     */
    public static double[][] initValues(double[][] data, double f) {

        if (data != null) {

            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data[0].length; j++) {
                    data[i][j] = f;
                }
        }
        return data;

    }

    /**
     * @param lenx
     * @param leny
     * @param f
     * @return
     */
    public static double[][] initValues(int lenx, int leny, double f) {
        double[][] data = new double[lenx][leny];
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[0].length; j++)
                data[i][j] = f;
        return data;
    }

    /**
     * @param data
     * @param f
     * @return
     */
    public static int[] initValues(int[] data, int f) {
        if (data != null) {
            for (int i = 0; i < data.length; i++)
                data[i] = f;
        }
        return data;
    }

    /**
     * Checks the provided array with the provided value f.
     *
     * @param f
     * @param d
     * @return
     */
    public static boolean check(float f, float[] d) {

        int count = 0;
        int total = 0;
        boolean ret = false;
        if (d != null) {

            for (int i = 0; i < d.length; i++) {
                //System.out.println(f+"\t"+d[i]);
                if (d[i] == f)
                    count++;
                total++;
            }
        }
        if (total != 0 && count == total)
            ret = true;
        return ret;
    }

    /**
     * Initializes the provided array with the provided value f.
     */
    public static float[] init(float f, float[] d) {

        if (d != null) {

            for (int i = 0; i < d.length; i++) {
                d[i] = f;
            }
        }
        return d;
    }

    /**
     * Checks the provided array with the provided value f.
     */
    public static boolean check(double f, double[] d) {

        int count = 0;
        int total = 0;
        boolean ret = false;
        if (d != null) {

            for (int i = 0; i < d.length; i++) {
                if (d[i] == f)
                    count++;
                total++;
            }
        }
        if (total != 0 && count == total)
            ret = true;
        return ret;
    }

    /**
     * Initializes the provided array with the provided value f.
     */
    public static double[] init(double f, double[] d) {

        if (d != null) {

            for (int i = 0; i < d.length; i++) {
                d[i] = f;
            }
        }
        return d;
    }

    /**
     * Initializes the provided array with the provided value f.
     */
    public static double[] init(double f, int x) {
        double[] d = new double[x];
        for (int i = 0; i < d.length; i++) {
            d[i] = f;
        }
        return d;
    }

    /**
     * Initializes the provided array with the provided value f.
     */
    public static float[][] init(float f, float[][] d) {
        if (d != null) {
            for (int i = 0; i < d.length; i++)
                for (int j = 0; j < d[0].length; j++) {
                    d[i][j] = f;
                }
        }
        return d;
    }

    /**
     * Initializes the provided array with the provided value f.
     */
    public static float[][] initNoDiag(float f, float[][] d) {
        if (d != null) {
            for (int i = 0; i < d.length; i++)
                for (int j = 0; j < d[0].length; j++)
                    if (i != j) {
                        d[i][j] = f;
                    }
        }
        return d;
    }

    /**
     * Checks the provided array with the provided value f.
     */
    public static boolean check(float f, float[][] d) {

        int count = 0;
        int total = 0;
        boolean ret = false;
        if (d != null) {

            for (int i = 0; i < d.length; i++)
                for (int j = 0; j < d[0].length; j++) {
                    if (d[i][j] == f)
                        count++;
                    total++;
                }
        }
        if (count == total)
            ret = true;
        return ret;
    }

    /**
     * Initializes the provided array with the provided value f.
     */
    public static double[][] init(double f, double[][] d) {

        if (d != null) {

            for (int i = 0; i < d.length; i++)
                for (int j = 0; j < d[0].length; j++) {
                    d[i][j] = f;
                }
        }

        return d;
    }

    /**
     * @param f
     * @param x
     * @param y
     * @return
     */
    public static double[][] init(double f, int x, int y) {

        double[][] d = new double[x][y];

        for (int i = 0; i < d.length; i++)
            for (int j = 0; j < d[0].length; j++) {
                d[i][j] = f;
            }

        return d;
    }

    /**
     * Initializes the provided array with the provided value f.
     */
    public static double[][] initNoDiag(double f, double[][] d) {

        if (d != null) {

            for (int i = 0; i < d.length; i++)
                for (int j = 0; j < d[0].length; j++)
                    if (i != j) {
                        d[i][j] = f;
                    }
        }

        return d;
    }

    /**
     * Checks the provided array with the provided value f.
     */
    public static boolean check(double f, double[][] d) {

        int count = 0;
        int total = 0;
        boolean ret = false;
        if (d != null) {

            for (int i = 0; i < d.length; i++)
                for (int j = 0; j < d[0].length; j++) {
                    if (d[i][j] == f)
                        count++;
                    total++;
                }
        }
        if (count == total)
            ret = true;
        return ret;
    }


    public final void initValues(double f) {

        if (mat != null) {

            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++) {
                    mat[i][j] = f;
                }
        }
    }


    /**
     * Initializes the label variables.
     */
    public final void initLabels() {
        labels = new String[columns][rows];
        xlabels = new String[columns];
    }

    /**
     * Finds the maximum of this Matrix' mat double[][] array.
     */
    public final double findMax() {

        if (mat != null) {

            double max = mat[0][0];
            imax = 0;
            jmax = 0;

            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++)
                    if (mat[i][j] > max) {

                        max = mat[i][j];
                        imax = i;
                        jmax = j;
                    }

            return max;
        }
        return Double.NaN;
    }

    /**
     * Finds the maximum of this Matrix' mat double[][] array.
     * Ignores the diagonal.
     */
    public final double findMaxNonDiag() {

        if (mat != null) {

            double max = mat[0][0];
            imax = 0;
            jmax = 0;

            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++)
                    if (i != j)
                        if (mat[i][j] > max) {

                            max = mat[i][j];
                            imax = i;
                            jmax = j;
                        }

            return max;
        }
        return Double.NaN;
    }

    /**
     * Finds the maximum negative of this Matrix' mat double[][] array.
     */
    public final double findMaxNeg() {

        if (mat != null) {

            double max = mat[0][0];
            imax = 0;
            jmax = 0;

            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++)
                    if (mat[i][j] < max) {

                        max = mat[i][j];
                        imax = i;
                        jmax = j;
                    }
            return max;
        }
        return Double.NaN;
    }

    /**
     * Finds the minimum negative of this double[][] array.
     */
    public static double findMin(double[][] m) {
        double min = m[0][0];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++)
                if (m[i][j] <= min) {
                    min = m[i][j];
                }
        }
        return min;
    }

    /**
     * Finds the minimum of this int [][] array.
     */
    public static int findMin(int[][] m) {
        int min = m[0][0];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++)
                if (m[i][j] <= min) {
                    min = m[i][j];
                }
        }
        return min;
    }

    /**
     * Finds the minimum non zero value of this int [][] array.
     *
     * @param m
     * @return
     */
    public static double findMinNonZero(int[][] m) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++)
                if (m[i][j] < min && m[i][j] != 0) {
                    min = m[i][j];
                }
        }
        if (Double.isInfinite(min)) {
            min = Double.NaN;
        }

        return min;
    }

    /**
     * Finds the minimum value of array.
     */
    public static double findMin(double[] m) {
        double min = m[0];
        for (int i = 0; i < m.length; i++) {
            if (m[i] < min) {
                min = m[i];
            }
        }
        return min;
    }

    /**
     * Finds the maximum value of the array.
     */
    public static double findMax(double[][] array) {
        double max = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j] > max) {
                    max = array[i][j];
                }
            }
        }
        return max;
    }

    /**
     * Finds the maximum value of the array.
     */
    public static int findMax(int[][] array) {
        int max = array[0][0];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j] > max) {
                    max = array[i][j];
                }
            }
        }
        return max;
    }

    /**
     * Finds the maximum value of the array.
     */
    public static double findMax(double[] array) {
        double max = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }


    /**
     * Finds the minimum of this Matrix' mat double[][] array.
     */
    public double findMin() {
        if (mat != null) {
            double min = mat[0][0];
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[i].length; j++)
                    if (mat[i][j] <= min) {
                        min = mat[i][j];
                    }
            }
            return min;
        }
        return Double.NaN;
    }

    /**
     * Finds the minimum of this Matrix' mat double[][] array.
     * Ignores the diagonal.
     */
    public double findMinNonDiag() {
        if (mat != null) {
            double min = mat[0][0];
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[i].length; j++)
                    if (i != j)
                        if (mat[i][j] <= min) {
                            min = mat[i][j];
                        }
            }
            return min;
        }
        return Double.NaN;
    }

    /**
     * Finds the maximum of this Matrix' mat double[][] array and converts it to -1.
     */
    public final double findMaxandDel() {

        if (mat != null) {

            double max = mat[0][0];
            imax = 0;
            jmax = 0;

            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++)
                    if (mat[i][j] > max) {

                        max = mat[i][j];
                        imax = i;
                        jmax = j;
                    }

            if (imax != -1 && jmax != -1)
                mat[imax][jmax] = -1;
            return max;
        }
        return Double.NaN;
    }

    /**
     * Finds the maximum of this Matrix' mat double[][] array lower diagonal matrix and converts it to -1.
     */
    public final double findMaxandDelDiag() {

        if (mat != null) {

            double max = -1;
            imax = -1;
            jmax = -1;

            for (int i = 0; i < mat.length; i++)
                for (int j = i + 1; j < mat[i].length; j++)
                    if (mat[i][j] > max) {

                        max = mat[i][j];
                        imax = i;
                        jmax = j;
                    }

            if (imax != -1 && jmax != -1)
                mat[imax][jmax] = -1;
            return max;
        }
        return Double.NaN;
    }

    /**
     * @param array
     * @param threshold
     * @param replace
     * @return
     */
    public static double[][] filterBelowtoZero(double[][] array, double threshold, double replace) {
        double[][] ret = new double[array.length][array[0].length];
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[0].length; j++) {
                if (array[i][j] < threshold) {
                    ret[i][j] = replace;
                } else
                    ret[i][j] = array[i][j];
            }
        }
        return ret;
    }

    /**
     * @param array
     * @param threshold
     * @param replace
     * @return
     */
    public static double[][] filterAboveEqual(double[][] array, double threshold, double replace) {
        return filterAboveEqual(array, threshold, replace, false);
    }

    /**
     * @param array
     * @param threshold
     * @param replace
     * @param debug
     * @return
     */
    public static double[][] filterAboveEqual(double[][] array, double threshold, double replace, boolean debug) {
        double[][] ret = new double[array.length][array[0].length];
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[0].length; j++) {
                if (array[i][j] >= threshold) {
                    if (debug) {
                        System.out.println("filterAboveEqual replacing " + array[i][j] + "\twith\t" + replace);
                    }
                    ret[i][j] = replace;
                } else
                    ret[i][j] = array[i][j];
            }
        }
        return ret;
    }

    /**
     * @param array
     * @param threshold
     * @param replace
     * @return
     */
    public static double[] filterBelowtoZero(double[] array, double threshold, double replace) {
        double[] ret = new double[array.length];
        for (int i = 0; i < ret.length; i++) {
            if (array[i] < threshold) {
                ret[i] = replace;
            } else
                ret[i] = array[i];
        }
        return ret;
    }

    /**
     * @param array
     * @param threshold
     * @param replace
     * @return
     */
    public static int[][] filterBelowtoZero(int[][] array, int threshold, int replace) {
        int[][] ret = new int[array.length][array[0].length];
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[0].length; j++) {
                if (array[i][j] < threshold) {
                    ret[i][j] = replace;
                } else
                    ret[i][j] = array[i][j];
            }
        }
        return ret;
    }

    /**
     * Counts the number of nonzero entries in this Matrix' mat double[][] array.
     *
     * @param ar
     * @param a
     * @return
     */
    public static double countValue(int[] ar, int a) {
        int ret = -1;
        for (int i = 0; i < ar.length; i++)
            if (ar[i] == a)
                ret++;
        return ret;
    }

    /**
     * Counts the number of nonzero entries in this Matrix' mat double[][] array.
     *
     * @param ar
     * @param a
     * @return
     */
    public static int countValue(int[][] ar, int a) {
        int ret = -1;
        for (int i = 0; i < ar.length; i++)
            for (int j = i + 1; j < ar[0].length; j++) {
                if (ar[i][j] == a)
                    ret++;
            }
        return ret;
    }

    /**
     * Counts the number of entries equal to the specified double in a double[].
     *
     * @param ar
     * @param a
     * @return
     */
    public static double countValue(double[] ar, double a) {
        double ret = 0;
        for (int i = 0; i < ar.length; i++) {
            if (ar[i] == a) {
                ret++;
            }
        }
        return ret;
    }

    /**
     * Counts the number of entries equal to NaN.
     *
     * @param ar
     * @return
     */
    public static double countNaN(double[] ar) {
        double ret = 0;
        for (int i = 0; i < ar.length; i++) {
            //System.out.println(i + "\t" + ar[i] + "\t" + a);
            if (Double.isNaN(ar[i])) {
                //System.out.println("MATCHED "+i + "\t" + ar[i]);
                ret++;
            }
        }
        return ret;
    }

    /**
     * Counts the number of nonzero entries in this Matrix' mat double[][] array.
     *
     * @param ar
     * @param a
     * @return
     */
    public static double countValue(double[][] ar, double a) {
        double ret = -1;
        for (int i = 0; i < ar.length; i++)
            for (int j = 0; j < ar[0].length; j++) {
                if (ar[i][j] == a)
                    ret++;
            }
        return ret;
    }

    /**
     * @param ar
     * @param a
     * @return
     */
    public static double countValueLowerDiag(double[][] ar, double a) {
        double ret = -1;
        for (int i = 0; i < ar.length; i++)
            for (int j = i + 1; j < ar[0].length; j++) {
                if (ar[i][j] == a)
                    ret++;
            }
        return ret;
    }

    /**
     * Counts the number of nonzero entries in this Matrix' mat double[][] array.
     *
     * @param a
     * @return
     */
    public static double countNonZero(double[][] a) {
        double ret = -1;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] > 0)
                    ret++;
            }
        return ret;
    }

    /**
     * counts all non zero matrix elements
     *
     * @return
     */
    public final double countNonZero() {
        double ret = -1;
        if (mat != null) {
            for (int i = 0; i < columns; i++)
                for (int j = 0; j < rows; j++) {
                    if (mat[i][j] > 0)
                        ret++;
                }
        }
        return ret;
    }

    /**
     * counts all non zero lower triagnular matrix elements (excluding diagonal)
     *
     * @return
     */
    public final double countLowDiagNonZero() {
        double ret = -1;
        if (mat != null) {
            for (int i = 0; i < columns; i++)
                for (int j = i + 1; j < rows; j++) {
                    if (mat[i][j] > 0)
                        ret++;
                }
        }
        return ret;
    }

    /**
     * Counts all non zero lower triagnular matrix elements (excluding diagonal
     *
     * @param pass
     * @return
     */
    public static double countLowDiagNonZero(double[][] pass) {
        double ret = -1;
        if (pass != null) {
            for (int i = 0; i < pass.length; i++)
                for (int j = i + 1; j < pass.length; j++) {
                    if (pass[i][j] > 0)
                        ret++;
                }
        }
        return ret;
    }

    /**
     * Returns the number of valid data points per row
     *
     * @param pass
     * @return
     */
    public static int[] countCompleteRows(double[][] pass) {
        int collen = pass.length;
        int rowlen = pass[0].length;
        int[] ret = new int[collen];
        for (int i = 0; i < collen; i++) {
            for (int j = 0; j < rowlen; j++) {
                if (Double.isNaN(pass[i][j]))
                    ret[i]++;
            }
        }
        for (int i = 0; i < collen; i++) {
            ret[i] = rowlen - ret[i];
        }
        return ret;
    }

    /**
     * Sets negative values to zero in the 2d array.
     *
     * @param a
     * @return
     */
    public static double[][] setNegativetoZero(double[][] a) {
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] < 0)
                    a[i][j] = 0;
            }
        return a;
    }

    /**
     * Finds the top N correlations/predictions
     *
     * @param curmat
     * @param n
     * @return
     */
    public final double[][] topN(double[][] curmat, int n) {
        //System.out.println("mat size  "+mat.length+"   "+mat[0].length);
        double[][] top = new double[n][3];
        int ysize = curmat[0].length;
        if (curmat[0].length == 0)
            ysize = 1;
        double[][] ret = new double[curmat.length][ysize];

        int countall = 0;
        for (int i = 0; i < curmat.length; i++) {
            for (int j = i; j < ysize; j++) {
                int a = 0;
                if (curmat[i][j] > 0)
                    while (a < n) {
                        if (curmat[i][j] >= top[a][0]) {
                            countall++;
                            double[][] topcopy = top;
                            //System.out.println("adding to TOP  "+i+"  "+j+"  "+curmat[i][j]);
                            top = moveDown(topcopy, a, curmat[i][j], i, j);
                            a = n;
                        }
                    }
            }
        }

        for (int i = 0; i < n; i++) {
            ret[(int) top[i][1]][(int) top[i][2]] = top[i][0];
            //System.out.println(i+"  "+top[i][0]);
        }
        return ret;
    }


    /**
     * finds the top N correlations/predictions
     *
     * @param n
     * @return
     */
    public final double[][] topN(int n) {
        //System.out.println("mat size  "+mat.length+"   "+mat[0].length);
        double[][] top = new double[n][3];
        int ysize = mat[0].length;
        if (mat[0].length == 0)
            ysize = 1;
        double[][] ret = new double[mat.length][ysize];

        int countall = 0;
        for (int i = 0; i < mat.length; i++) {
            for (int j = i; j < ysize; j++) {
                int a = 0;
                if (mat[i][j] > 0)
                    while (a < n) {
                        if (mat[i][j] >= top[a][0]) {
                            countall++;
                            double[][] topcopy = top;
                            System.out.println("topN adding to TOP  " + i + "  " + j + "  " + mat[i][j]);
                            top = moveDown(topcopy, a, mat[i][j], i, j);
                            a = n;
                        }
                    }
            }
        }

        for (int i = 0; i < n; i++) {
            ret[(int) top[i][1]][(int) top[i][2]] = top[i][0];
            //System.out.println(i+"  "+top[i][0]);
        }
        return ret;
    }

    /**
     * adds new max to max list, shifts other members
     */
    public final double[][] moveDown(double[][] work, int cee, double a, int b, int c) {
        double[][] copy = new double[work.length][work[0].length];
        for (int i = 0; i < cee; i++) {
            copy[i][0] = work[i][0];
            copy[i][1] = work[i][1];
            copy[i][2] = work[i][2];
        }

        copy[cee][0] = a;
        copy[cee][1] = b;
        copy[cee][2] = c;

        for (int i = cee + 1; i < copy.length; i++) {
            copy[i][0] = work[i - 1][0];
            copy[i][1] = work[i - 1][1];
            copy[i][2] = work[i - 1][2];
        }
        return copy;
    }

    /**
     * Dot product of two matrices.
     */
    public static double[][] mult(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];

        if ((a.length != b.length) || (a[0].length != b[0].length))
            ret = null;

        else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a[i].length; j++)
                    ret[i][j] = a[i][j] * b[i][j];
        }

        return ret;
    }

    /**
     * Scalar multiplication of matrix.
     */
    public static double[][] mult(double[][] a, double b) {
        double[][] ret = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[i].length; j++)
                ret[i][j] = a[i][j] * b;

        return ret;
    }

    /**
     * Scalar multiplication of matrix.
     */
    public static double[][][][] mult(double[][][][] a, double b) {
        double[][][][] ret = new double[a.length][a[0].length][a[0][0].length][a[0][0][0].length];

        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[i].length; j++)
                for (int k = 0; k < a[0][0].length; k++)
                    for (int l = 0; l < a[0][0][0].length; l++)
                        ret[i][j][k][l] = a[i][j][k][l] * b;

        return ret;
    }

    /**
     * Scalar multiplication of matrix, except diagonal.
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][][][] multNoDiag(double[][][][] a, double b) {
        double[][][][] ret = new double[a.length][a[0].length][a[0][0].length][a[0][0][0].length];

        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[i].length; j++)
                if (i != j) {
                    for (int k = 0; k < a[0][0].length; k++)
                        for (int l = 0; l < a[0][0][0].length; l++)
                            ret[i][j][k][l] = a[i][j][k][l] * b;
                }

        return ret;
    }

    /**
     * Dot product of two matrices - only dots non-zero entries.
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] multNonZero(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];
        if ((a.length != b.length) || (a[0].length != b[0].length))
            ret = null;
        else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a[i].length; j++) {
                    if (a[i][j] != 0 && b[i][j] != 0)
                        ret[i][j] = a[i][j] * b[i][j];
                    else if (a[i][j] == 0 && b[i][j] != 0)
                        ret[i][j] = b[i][j];
                    else if (a[i][j] != 0 && b[i][j] == 0)
                        ret[i][j] = a[i][j];
                }
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public final double[][] rightConsJoin(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];
        if ((a.length != b.length) || (a[0].length != b[0].length))
            ret = null;
        else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a[i].length; j++) {
                    if (a[i][j] != 0 && b[i][j] != 0)
                        ret[i][j] = a[i][j] * b[i][j];
                    else if (a[i][j] == 0 && b[i][j] != 0)
                        ret[i][j] = b[i][j];
                }
        }
        return ret;
    }

    /**
     * multiplies double[][] b with internal Matrix double[][] mat
     * and reassigns mat to the multiplication result
     *
     * @param b
     */
    public final void mult(double[][] b) {
        if (b.length == mat.length) {
            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++)
                    mat[i][j] = mat[i][j] * b[i][j];
        }
    }

    /**
     *
     */
    /**
     * multiplies double b with internal Matrix double[][] mat
     * and reassigns mat to the multiplication result
     *
     * @param b
     */
    public final void multScale(double b) {

        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[i].length; j++)
                mat[i][j] = mat[i][j] * b;
    }

    /**
     * multiplies double b with internal Matrix double[][] mat
     * and reassigns mat to the multiplication result
     */
    public final double[][] mult(double b) {

        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[i].length; j++)
                mat[i][j] = mat[i][j] * b;
        return mat;
    }

    /**
     * multiplies double[][] b with internal Matrix double[][] mat
     * and reassigns mat to the multiplication result
     */
    public final void multNonZero(double[][] b) {
        if (b.length == mat.length) {
            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++) {
                    if (mat[i][j] != 0 && b[i][j] != 0)
                        mat[i][j] = mat[i][j] * b[i][j];
                    else if (mat[i][j] == 0 && b[i][j] != 0)
                        mat[i][j] = b[i][j];
                    else if (mat[i][j] != 0 && b[i][j] == 0)
                        mat[i][j] = mat[i][j];
                }
        }
    }

    /**
     * multiplies double[][] b with internal Matrix double[][] mat
     * and reassigns mat to the multiplication result
     */
    public final void rightConsJoin(double[][] b) {
        if (b.length == mat.length) {
            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++) {
                    if (mat[i][j] != 0 && b[i][j] != 0)
                        mat[i][j] = mat[i][j] * b[i][j];
                    else if (mat[i][j] == 0 && b[i][j] != 0)
                        mat[i][j] = b[i][j];
                }
        }
    }

    /**
     * divides double[][] b with internal Matrix double[][] mat
     * and reassigns mat to the division result
     *
     * @param b
     */
    public final void div(double b) {
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[i].length; j++)
                mat[i][j] = mat[i][j] / b;
    }

    /**
     * absolute difference of double[][] a and   double[][] b
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] AbsDiff(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];

        if ((a.length != b.length) || (a[0].length != b[0].length))
            ret = null;

        else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a.length; j++)
                    ret[i][j] = Math.abs(a[i][j] - b[i][j]);
        }
        return ret;
    }

    /**
     * abs
     *
     * @param a
     * @return
     */
    public static double[][] abs(double[][] a) {
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[0].length; j++)
                a[i][j] = Math.abs(a[i][j]);
        return a;
    }

    /**
     * Calculates the signed difference between a and b for all positive non-zero values.
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] PosDiff(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];
        System.out.println("AbsDiff " + a.length + "  " + b.length + "  " + a[0].length + "  " + b[0].length);
        if ((a.length != b.length) || (a[0].length != b[0].length))
            ret = null;

        else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a.length; j++) {
                    double test = a[i][j] - b[i][j];
                    if (test > 0)
                        ret[i][j] = test;
                }
        }
        return ret;
    }

    /**
     * Calculates the signed difference between a and b for all negative non-zero values.
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] negdiff(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];


        if ((a.length != b.length) || (a[0].length != b[0].length)) {
            System.out.println("negdiff attempting to operate on matrices of different dimensions  " + a.length + "   " + b.length);
            ret = null;
        } else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a[i].length; j++) {
                    double test = a[i][j] - b[i][j];
                    if (test < 0)
                        ret[i][j] = -test;
                }
        }
        return ret;
    }

    /**
     * DIRECTIONAL !! subtraction  double[][] a from  double[][] b
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] diff(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];

        if ((a.length != b.length) || (a[0].length != b[0].length)) {
            ret = null;
            System.out.println("diff MoreArray dimensions don't match " + a.length + "\t" + b.length + "\t" + a[0].length + "\t" + b[0].length);
        } else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a[i].length; j++) {
                    ret[i][j] = a[i][j] - b[i][j];
                    //System.out.println(i+"\t"+j+"\t"+ret[i][j]+"\t"+a[i][j]+"\t"+b[i][j]);
                }
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[][] multiply(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];
        if ((a.length != b.length) || (a[0].length != b[0].length))
            ret = null;
        else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a.length; j++)
                    ret[i][j] = a[i][j] * b[i][j];
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static double[][] ratioByDiagonalEntries(double[][] a) {
        double[][] ret = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++)
                ret[i][j] = a[i][j] / (a[i][i] * a[j][j]);
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static double[][] ratioByDiagonalEntriesMin(double[][] a) {
        double[][] ret = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++)
                ret[i][j] = a[i][j] / Math.min(a[i][i], a[j][j]);
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static double[][] ratioByDiagonalEntriesRoot(double[][] a) {
        double[][] ret = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++)
                ret[i][j] = a[i][j] / Math.sqrt(a[i][i] * a[j][j]);
        return ret;
    }

    /**
     * adds double[][] a with   double[][] b
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] add(double[][] a, double[][] b) {
        double[][] ret = new double[a.length][a[0].length];
        if ((a.length != b.length) || (a[0].length != b[0].length)) {
            System.out.println("Matrix.add size mistmatch y: " +
                    a.length + "\t" + b.length + "\tx: " + a[0].length + "\t" + b[0].length);
            ret = null;
        } else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a[0].length; j++)
                    ret[i][j] = a[i][j] + b[i][j];
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static int[][] add(int[][] a, int[][] b) {
        int[][] ret = new int[a.length][a[0].length];
        if ((a.length != b.length) || (a[0].length != b[0].length))
            ret = null;
        else {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a[i].length; j++)
                    ret[i][j] = a[i][j] + b[i][j];
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static int[] add(int[] a, int b) {
        for (int i = 0; i < a.length; i++)
            a[i] += b;
        return a;
    }

    /**
     * adds the first array to the second and returns the sum
     *
     * @param a
     * @param b
     * @return
     */
    public static int[] add(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++)
            a[i] += b[i];
        return a;
    }

    /**
     * adds double[][] b with internal Matrix double[][] mat
     * and reassigns mat to the addition result
     *
     * @param b
     */
    public final void add(double[][] b) {

        if ((mat.length == b.length) && (mat[0].length == b[0].length)) {
            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++)
                    mat[i][j] = mat[i][j] + b[i][j];
        }

    }

    /**
     * Substracts b from each cell in a
     *
     * @param a
     * @param b
     * @return
     */
    public static double[][] subtract(double[][] a, double b) {
        double[][] ret = a;//new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++)
                ret[i][j] -= b;
        return ret;
    }

    /**
     * Substracts b from each cell in a
     *
     * @param a
     * @param b
     * @return
     */
    public static double[] subtract(double[] a, double[] b) {
        double[] ret = a;//MoreArray.initArray(a.length, Double.NaN);
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(ret[i]) && !Double.isNaN(b[i])) {
                //System.out.println("subtract b/f "+a[i]+"\t"+ b[i]+"\tforw "+(a[i]-b[i]));
                ret[i] -= b[i];
                //System.out.println("subtract a/f "+ret[i]+"\t"+ b[i]+"\tback "+(ret[i]+b[i]));
            }
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] add(double[] a, double[] b) {
        double[] ret = a;//MoreArray.initArray(a.length, Double.NaN);
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(ret[i]) && !Double.isNaN(b[i])) {
                //System.out.println("subtract b/f "+a[i]+"\t"+ b[i]+"\tforw "+(a[i]-b[i]));
                ret[i] += b[i];
                //System.out.println("subtract a/f "+ret[i]+"\t"+ b[i]+"\tback "+(ret[i]+b[i]));
            }
        }
        return ret;
    }

    /**
     * Substracts b from each cell in a
     *
     * @param a
     * @param b
     * @return
     */
    public static double[] subtract(double[] a, double b) {
        double[] ret = a;//MoreArray.initArray(a.length, Double.NaN);
        if (!Double.isNaN(b))
            for (int i = 0; i < a.length; i++) {
                if (!Double.isNaN(a[i])) {
                    //System.out.println("subtract b/f "+a[i]+"\t"+ b[i]+"\tforw "+(a[i]-b[i]));
                    ret[i] -= b;
                    //System.out.println("subtract a/f "+ret[i]+"\t"+ b[i]+"\tback "+(ret[i]+b[i]));
                }
            }
        return ret;
    }

    /**
     * Substracts b from each cell in a
     *
     * @param a
     * @return
     */
    public static double[] setMinimum(double[] a, double min) {
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && a[i] < min)
                a[i] = min;
        }
        return a;
    }

    /**
     * Substracts b from each cell in a
     *
     * @param a
     * @return
     */
    public static double[] setFloor(double[] a, double floor) {

        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && a[i] < floor)
                a[i] = Double.NaN;
        }
        return a;
    }


    /**
     * adds Matrix pass with internal Matrix double[][] mat
     * and reassigns mat to the addition result
     */
    public final void add(Matrix pass) {

        if ((columns == pass.columns) && (rows == pass.rows)) {
            for (int i = 0; i < columns; i++)
                for (int j = 0; j < rows; j++)
                    mat[i][j] = mat[i][j] + pass.mat[i][j];
        }

    }

    /**
     * adds top triangular matrix of double[][] a with internal Matrix double[][] mat and reassigns mat to the addition result
     */
    public final void addTopDiag(double[][] a) {

        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a[i].length; j++) {
                if (a[i][j] != -1)
                    mat[i][j] = mat[i][j] + a[i][j];
                else
                    mat[i][j] = mat[i][j] + a[j][i];
            }
    }

    /**
     * @return
     */
    public final double sumEntries() {

        double ret = 0;
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[i].length; j++)
                ret += mat[i][j];
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static double sumEntries(int[][] at) {
        double ret = 0;
        for (int i = 0; i < at.length; i++)
            for (int j = 0; j < at[0].length; j++)
                ret += at[i][j];
        return ret;
    }


    /**
     * @param at
     * @return
     */
    public static double sumEntries(double[][] at) {
        double ret = 0;
        for (int i = 0; i < at.length; i++)
            for (int j = 0; j < at[0].length; j++)
                ret += at[i][j];
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static double sumEntries(double[] at) {
        double ret = 0;
        for (int i = 0; i < at.length; i++)
            ret += at[i];
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static int sumEntries(int[] at) {
        int ret = 0;
        for (int i = 0; i < at.length; i++)
            ret += at[i];
        return ret;
    }


    /**
     * @param at
     * @return
     */
    public static double[] sumOverRows(double[][] at) {
        int length = at[0].length;
        double[] ret = new double[length];
        for (int j = 0; j < length; j++)
            for (int i = 0; i < at.length; i++) {
                ret[j] += at[i][j];
            }
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static int[] sumOverRows(int[][] at) {
        int length = at[0].length;
        int[] ret = new int[length];
        for (int j = 0; j < length; j++)
            for (int i = 0; i < at.length; i++) {
                ret[j] += at[i][j];
            }
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static double avg(int[][] at) {
        //System.out.println("avgOverCols " + at.length + "\t" + at[0].length);
        double sum = 0;
        double count = 0;
        for (int i = 0; i < at.length; i++) {
            for (int j = 0; j < at[0].length; j++) {
                sum += at[i][j];
                count++;
            }
        }
        return sum / count;
    }

    /**
     * @param at
     * @return
     */
    public static double avg(double[][] at) {
        //System.out.println("avgOverCols " + at.length + "\t" + at[0].length);
        double sum = 0;
        double count = 0;
        for (int i = 0; i < at.length; i++) {
            for (int j = 0; j < at[0].length; j++) {
                if (!Double.isNaN(at[i][j])) {
                    sum += at[i][j];
                    count++;
                }
            }
        }
        return sum / count;
    }

    /**
     * @param a
     * @param mean
     * @return
     */
    public static double SD(double[][] a, double mean) {

        double sum = 0;
        int k = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (!Double.isNaN(a[i][j])) {
                    sum += Math.pow(a[i][j] - mean, 2);// * (x[i] - mean);
                    k++;
                }
            }
        }

        double s = Double.NaN;
        //System.out.println("SD k "+k+"\tsum "+sum);
        if (k > 1)
            s = Math.sqrt(sum / (double) (k - 1));
        return s;
    }

    /**
     * @param at
     * @return
     */
    public static double[] avgOverRows(double[][] at) {
        //System.out.println("avgOverRows " + at.length + "\t" + at[0].length);
        int length = at.length;
        double[] ret = new double[length];
        for (int j = 0; j < length; j++) {
            double[] cur = new double[at[0].length];
            for (int i = 0; i < at[0].length; i++) {
                cur[i] = at[j][i];
            }
            ret[j] += stat.avg(cur);
        }
        return ret;
    }

    /**
     * @param at
     * @return
     */
    /*   public static double[] avgOverSampRows(double[][] at) {
        //System.out.println("avgOverRows " + at.length + "\t" + at[0].length);
        int length = at.length;
        double[] ret = MoreArray.initArray(length, Double.NaN);//new double[length];
        for (int j = 0; j < length; j++) {
            double[] cur = new double[at[0].length];
            for (int i = 0; i < at[0].length; i++) {
                cur[i] = at[j][i];
            }
            double val = stat.avgOverSamp(cur, -1);
            if (!Double.isNaN(val)) {
                if (Double.isNaN(ret[j]))
                    ret[j] = 0;
                ret[j] += val;
            }
        }
        return ret;
    }*/

    /**
     * @param at
     * @return
     */
    public static double[] avgOverSampRows(double[][] at) {
        return avgOverSampRows(at, -1);
    }

    /**
     * @param at
     * @param sample
     * @return
     */
    public static double[] avgOverSampRows(double[][] at, int sample) {
        //System.out.println("avgOverRows " + at.length + "\t" + at[0].length);
        int length = at.length;
        double[] ret = MoreArray.initArray(length, Double.NaN);//new double[length];
        for (int j = 0; j < length; j++) {
            double[] cur = new double[at[0].length];
            for (int i = 0; i < at[0].length; i++) {
                cur[i] = at[j][i];
            }
            /*System.out.println("avgOverSampRows " + j);
            MoreArray.printArray(cur);*/

            double val = stat.avgOverSamp(cur, sample);
            //System.out.println("avgOverSampRows val " + val);
            if (!Double.isNaN(val)) {
                if (Double.isNaN(ret[j]))
                    ret[j] = 0;
                ret[j] += val;
            }
        }
        /* System.out.println("avgOverSampRows " + ret.length);
        MoreArray.printArray(ret);*/
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static double[] avgABSOverSampRows(double[][] at) {
        //System.out.println("avgOverRows " + at.length + "\t" + at[0].length);
        int length = at.length;
        double[] ret = MoreArray.initArray(length, Double.NaN);//new double[length];
        for (int j = 0; j < length; j++) {
            double[] cur = new double[at[0].length];
            for (int i = 0; i < at[0].length; i++) {
                cur[i] = at[j][i];
            }
            /*System.out.println("avgOverSampRows " + j);
            MoreArray.printArray(cur);*/

            //double val = stat.avgOverSamp(cur, sample);
            double val = stat.avgABSOverSamp(cur, -1);
            //System.out.println("avgOverSampRows val " + val);
            if (!Double.isNaN(val)) {
                if (Double.isNaN(ret[j]))
                    ret[j] = 0;
                ret[j] += val;
            }
        }
        /* System.out.println("avgOverSampRows " + ret.length);
        MoreArray.printArray(ret);*/
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static double[] sumOverCols(double[][] at) {
        int length = at.length;
        double[] ret = new double[length];
        for (int j = 0; j < length; j++)
            for (int i = 0; i < at[0].length; i++) {
                ret[j] += at[j][i];
            }
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static int[] sumOverCols(int[][] at) {
        int length = at.length;
        int[] ret = new int[length];
        for (int j = 0; j < length; j++)
            for (int i = 0; i < at[0].length; i++) {
                ret[j] += at[j][i];
            }
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static double[] avgOverCols(double[][] at) {
        //System.out.println("avgOverCols " + at.length + "\t" + at[0].length);
        int length = at[0].length;
        double[] ret = new double[length];
        for (int j = 0; j < length; j++) {
            double[] cur = new double[at.length];
            for (int i = 0; i < at.length; i++) {
                cur[i] = at[i][j];
            }
            ret[j] = stat.avg(cur);
        }
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static double[] avgOverCols(int[][] at) {
        //System.out.println("avgOverCols " + at.length + "\t" + at[0].length);
        int length = at[0].length;
        double[] ret = new double[length];
        for (int j = 0; j < length; j++) {
            int[] cur = new int[at.length];
            for (int i = 0; i < at.length; i++) {
                cur[i] = at[i][j];
            }
            ret[j] = stat.avg(cur);
        }
        return ret;
    }

    /**
     * @param at
     * @return
     */
    public static double[] SDOverCols(double[][] at, double[] mean) {
        //System.out.println("avgOverCols " + at.length + "\t" + at[0].length);
        int length = at[0].length;
        double[] ret = new double[length];
        for (int j = 0; j < length; j++) {
            double[] cur = new double[at.length];
            for (int i = 0; i < at.length; i++) {
                cur[i] = at[i][j];
            }
            ret[j] = stat.SD(cur, mean[j]);
        }
        return ret;
    }

    /**
     * Take the log of all entries. Assumes a square matrix of square matrices.
     *
     * @param r
     * @return
     */
    public static double[][][][] logNoDiag(double[][][][] r) {
        for (int j = 0; j < r.length; j++) {
            for (int k = 0; k < r[0].length; k++) {
                if (j != k) {
                    for (int a = 0; a < r[0][0].length; a++) {
                        for (int b = 0; b < r[0][0].length; b++) {
                            r[j][k][a][b] = Math.log(r[j][k][a][b]);
                        }
                    }
                }
            }
        }
        return r;
    }

    /**
     * Takes the log of all entries.
     *
     * @param r
     * @return
     */
    public static double[][] log(double[][] r) {
        for (int j = 0; j < r.length; j++) {
            for (int k = 0; k < r[0].length; k++) {
                if (!Double.isNaN(r[j][k]) && !Double.isInfinite(r[j][k])) {
                    if (r[j][k] == 0)
                        System.out.println("pre-log is 0 " + j + "\t" + k + "\t" + r[j][k] + "\t");
                    r[j][k] = Math.log(r[j][k]);
                    /*if (r[j][k] == 0)
                        System.out.println("post-log is 0 " + j + "\t" + k + "\t" + r[j][k] + "\t");*/
                    //System.out.println("log\t"+j+"\t"+k+"\t"+r[j][k]);
                }
            }
        }
        return r;
    }

    /**
     * @param r
     * @return
     */
    public static double[][] sqrt(double[][] r) {
        for (int j = 0; j < r.length; j++) {
            for (int k = 0; k < r[0].length; k++) {
                Double test = new Double(r[j][k]);
                if (!test.isNaN() && !test.isInfinite())
                    r[j][k] = Math.sqrt(r[j][k]);
            }
        }
        return r;
    }


    /**
     * Rotates the matrix counterclockwise - switching x with y axis and vice versa.
     */
    public final void switchRowsandColumns() {
        double[][] newmat = new double[mat[0].length][mat.length];
        for (int i = 0; i < mat[0].length; i++) {
            for (int j = 0; j < mat.length; j++) {
                newmat[i][j] = mat[j][i];
            }
        }
        mat = newmat;
        setDimensions();
    }

    /**
     * Normalizes the provided array with the provided double.
     *
     * @param n
     * @param a
     * @return
     */
    public static double[] norm(double n, double[] a) {
        double ret[] = new double[a.length];
        for (int i = 0; i < a.length; i++)
            ret[i] = a[i] / n;
        return ret;
    }

    /**
     * Normalizes the provided array with the provided double.
     *
     * @param n
     * @param a
     * @return
     */
    public static double[] add(double n, double[] a) {
        double ret[] = new double[a.length];
        for (int i = 0; i < a.length; i++)
            ret[i] = a[i] + n;
        return ret;
    }

    /**
     * Normalizes the provided array with the provided double.
     *
     * @param n
     * @param a
     * @return
     */
    public static ArrayList add(double n, ArrayList a) {
        ArrayList ret = new ArrayList();
        for (int i = 0; i < a.size(); i++) {
            double cur = ((Double) a.get(i)).doubleValue();
            ret.add(new Double(cur + n));
        }
        return ret;
    }

    /**
     * Normalizes the provided array with the provided double.
     *
     * @param n
     * @param a
     * @return
     */
    public static double[][] norm(double n, double[][] a) {
        double ret[][] = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[i].length; j++)
                ret[i][j] = a[i][j] / n;
        return ret;
    }

    /**
     * Normalizes the provided array with the provided double.
     *
     * @param n
     * @param a
     * @return
     */
    public static double[][] normCol(double[] n, double[][] a) {
        double ret[][] = new double[a.length][a[0].length];
        for (int i = 0; i < a[0].length; i++)
            for (int j = 0; j < a.length; j++)
                ret[i][j] = a[j][i] / n[i];
        return ret;
    }

    /**
     * Normalizes the class array with the provided double.
     *
     * @param n
     * @return
     */
    public final double[][] norm(double n) {
        double ret[][] = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[i].length; j++)
                ret[i][j] = mat[i][j] / n;
        return ret;
    }

    /**
     * Normalizes the current Matrix mat by n.
     *
     * @param n
     */
    public final void normalize(double n) {
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[i].length; j++)
                mat[i][j] = mat[i][j] / n;
    }

    /**
     * Outputs Matrix in FastME format.
     *
     * @param curmat
     * @param name
     */
    public final void writePhylipSquare(int[][] curmat, String name) {

        util.GiveDate give = new util.GiveDate();
        String dat = give.giveShortDate();
        String print = name + "." + dat + ".phylip_square";
        try {
            PrintWriter pif = new PrintWriter(new FileWriter(print), true);
            pif.println(curmat.length);
            for (int i = 0; i < curmat.length; i++) {
                pif.println(xlabels[i]);
                String data = "";
                for (int j = 0; j < curmat[0].length; j++) {
                    if (j != curmat[0].length - 1)
                        data += curmat[i][j] + "  ";
                    else
                        data += "" + curmat[i][j];
                }
                pif.println(data);
            }
            pif.close();
        } catch (IOException e) {
            System.out.println("Error with writing file.  " + print);
        }
    }

    /**
     * Outputs Matrix in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param name
     */
    public static void write(int[][] curmat, String name) {
        String print = name;
        if (name.indexOf(".mat") != name.length() - 4)
            print += ".mat";
        try {
            PrintWriter pif = new PrintWriter(new FileWriter(print), true);

            for (int i = 0; i < curmat.length; i++)
                for (int j = 0; j < curmat[0].length; j++) {
                    pif.println((i) + "  " + (j) + "  " + curmat[i][j]);
                }
            pif.close();
        } catch (IOException e) {
            System.out.println("Error writing file.  " + print);
        }
    }

    /**
     * Outputs Matrix in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param name
     */
    public static void write(double[][] curmat, String name) {
        String print = name;
        if (name.indexOf(".mat") != name.length() - 4)
            print += ".mat";
        try {
            PrintWriter pif = new PrintWriter(new FileWriter(print), true);
            for (int i = 0; i < curmat.length; i++)
                for (int j = 0; j < curmat[0].length; j++) {
                    pif.println((i) + "  " + (j) + "  " + curmat[i][j]);
                }
            pif.close();
        } catch (IOException e) {
            System.out.println("Error writing file.  " + print);
        }
    }

    /**
     * outputs Matrix in Matrix format to a file, if column and
     * row are -1 then writes all entries in order*
     *
     * @param curmat
     * @param name
     */
    public static void writeTab(double[][] curmat, String name) {
        if (name.indexOf(".txt") != name.length() - 5)
            name += ".txt";
        try {
            PrintWriter pif = new PrintWriter(new FileWriter(name), true);
            for (int i = 0; i < curmat.length; i++) {
                for (int j = 0; j < curmat[0].length - 1; j++) {
                    pif.print(curmat[i][j] + "\t");
                }
                pif.print(curmat[i][curmat[0].length - 1] + "\n");
            }
            pif.close();
        } catch (IOException e) {
            System.out.println("Error writing file.  " + name);
        }
    }

    /**
     * outputs array in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param xlabels
     * @param ylabels
     * @param name
     */
    public static void writeTab(int[][] curmat, String[] xlabels, String[] ylabels, String name) {

        String print = name;

        if (name.indexOf(".txt") != name.length() - 4)
            print += ".txt";

        //System.out.println("writeTab " + curmat.length + "\t" + curmat[0].length + "\t" + xlabels.length + "\t" + ylabels.length);
        if (curmat.length > 0 && curmat[0].length > 0)
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                pif.print("#\n");
                if (xlabels != null) {
                    pif.print("\t");
                    for (int i = 0; i < curmat[0].length - 1; i++) {
                        pif.print(xlabels[i] + "\t");
                    }
                    pif.print(xlabels[curmat[0].length - 1] + "\n");
                }

                for (int i = 0; i < curmat.length; i++) {
                    if (ylabels != null)
                        pif.print(ylabels[i] + "\t");
                    for (int j = 0; j < curmat[0].length - 1; j++) {
                        //System.out.println("writeTab " + i + "\t" + j + "\t" + curmat[i][j]);
                        pif.print(curmat[i][j] + "\t");
                    }
                    pif.print(curmat[i][curmat[0].length - 1] + "\n");
                }
                pif.close();
            } catch (IOException e) {
                System.out.println("Error writing file.  " + print);
            }
    }

    /**
     * outputs array in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param xlabels
     * @param ylabels
     * @param name
     * @param jcgheader
     */
    public static void writeTab(double[][] curmat, String[] xlabels, String[] ylabels, String name, boolean jcgheader) {
        String print = name;
        if (name.indexOf(".txt") != name.length() - 4)
            print += ".txt";
        //System.out.println("writeTab " + curmat.length + "\t" + curmat[0].length + "\t" + xlabels.length + "\t" + ylabels.length);
        if (curmat.length > 0 && curmat[0].length > 0)
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                if (jcgheader)
                    pif.print("#\n");
                if (xlabels != null) {
                    pif.print("\t");
                    for (int i = 0; i < curmat[0].length - 1; i++) {
                        pif.print(xlabels[i] + "\t");
                    }
                    pif.print(xlabels[curmat[0].length - 1] + "\n");
                }

                for (int i = 0; i < curmat.length; i++) {
                    if (ylabels != null)
                        pif.print(ylabels[i] + "\t");
                    for (int j = 0; j < curmat[0].length - 1; j++) {
                        //System.out.println("writeTab " + i + "\t" + j + "\t" + curmat[i][j]);
                        pif.print(curmat[i][j] + "\t");
                    }
                    pif.print(curmat[i][curmat[0].length - 1] + "\n");
                }
                pif.close();
            } catch (IOException e) {
                System.out.println("Error writing file.  " + print);
            }
    }

    /**
     * @param curmat
     * @param xlabels
     * @param ylabels
     * @param name
     */
    public static void writeTab(double[][] curmat, String[] xlabels, String[] ylabels, String name) {
        writeTab(curmat, xlabels, ylabels, name, true);
    }

    /**
     * @param curmat
     * @param xlabels
     * @param xlabelsStr
     * @param ylabels
     * @param outfile_name
     * @param header
     */
    public static void writeTab(double[][] curmat, String[] xlabels, String xlabelsStr, String[] ylabels, String outfile_name, String header) {
        String print = outfile_name;
        if (outfile_name.indexOf(".txt") != outfile_name.length() - 4)
            print += ".txt";
        //System.out.println("writeTab " + curmat.length + "\t" + curmat[0].length + "\t" + xlabels.length + "\t" + ylabels.length);
        if (curmat.length > 0 && curmat[0].length > 0)
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                pif.print(header + "\n");
                if (xlabels != null) {
                    pif.print("\t");
                    for (int i = 0; i < curmat[0].length - 1; i++) {
                        pif.print(xlabels[i] + "\t");
                    }
                    pif.print(xlabels[curmat[0].length - 1] + "\n");
                } else if (xlabelsStr != null) {
                    pif.println(xlabelsStr);
                }
                for (int i = 0; i < curmat.length; i++) {
                    if (ylabels != null)
                        pif.print(ylabels[i] + "\t");
                    for (int j = 0; j < curmat[0].length - 1; j++) {
                        //System.out.println("writeTab " + i + "\t" + j + "\t" + curmat[i][j]);
                        pif.print(curmat[i][j] + "\t");
                    }
                    pif.print(curmat[i][curmat[0].length - 1] + "\n");
                }
                pif.close();
            } catch (IOException e) {
                System.out.println("Error writing file.  " + print);
            }
    }

    /**
     * outputs array in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param xlabels
     * @param ylabels
     * @param outfile_name
     * @param header
     */
    public static void writeTab(double[][] curmat, String[] xlabels, String[] ylabels, String outfile_name, String header) {
        writeTab(curmat, xlabels, null, ylabels, outfile_name, header);
    }

    /**
     * outputs array in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param xlabels
     * @param ylabels
     * @param outfile_name
     * @param header
     */
    public static void writeTab(double[][] curmat, String xlabels, String[] ylabels, String outfile_name, String header) {
        writeTab(curmat, null, xlabels, ylabels, outfile_name, header);
    }

    /**
     * @param curmat
     * @param xlabels
     * @param xlabelsStr
     * @param ylabels
     * @param outfile_name
     * @param header
     */
    public static void writeTab(double[] curmat, String[] xlabels, String xlabelsStr, String[] ylabels, String outfile_name, String header) {
        String print = outfile_name;
        if (outfile_name.indexOf(".txt") != outfile_name.length() - 4)
            print += ".txt";
        //System.out.println("writeTab " + curmat.length + "\t" + curmat[0].length + "\t" + xlabels.length + "\t" + ylabels.length);
        if (curmat.length > 0)
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                pif.print(header + "\n");
                if (xlabels != null) {
                    pif.print("\t");
                    for (int i = 0; i < curmat.length - 1; i++) {
                        pif.print(xlabels[i] + "\t");
                    }
                    pif.print(xlabels[curmat.length - 1] + "\n");
                } else if (xlabelsStr != null) {
                    pif.println(xlabelsStr);
                }
                if (ylabels != null)
                    pif.print(ylabels[0] + "\t");
                for (int i = 0; i < curmat.length - 1; i++) {
                    pif.print(curmat[i] + "\t");
                }
                pif.print(curmat[curmat.length - 1] + "\n");
                pif.close();
            } catch (IOException e) {
                System.out.println("Error writing file.  " + print);
            }
    }

    /**
     * outputs array in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param xlabels
     * @param ylabels
     * @param outfile_name
     * @param header
     */
    public static void writeTab(double[] curmat, String[] xlabels, String[] ylabels, String outfile_name, String header) {
        writeTab(curmat, xlabels, null, ylabels, outfile_name, header);
    }

    /**
     * outputs array in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param xlabels
     * @param ylabels
     * @param outfile_name
     * @param header
     */
    public static void writeTab(double[] curmat, String xlabels, String[] ylabels, String outfile_name, String header) {
        writeTab(curmat, null, xlabels, ylabels, outfile_name, header);
    }

    /**
     * outputs array in Matrix format to a file, if column and
     * row are -1 then writes all entries in order
     *
     * @param curmat
     * @param xlabels
     * @param ylabels
     * @param name
     */
    public static void writeTab(double[] curmat, String[] xlabels, String[] ylabels, String name) {

        String print = name;

        if (name.indexOf(".txt") != name.length() - 4)
            print += ".txt";

        //System.out.println("writeTab " + curmat.length + "\t" + curmat[0].length + "\t" + xlabels.length + "\t" + ylabels.length);
        if (curmat.length > 0)
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);

                pif.print("\n");
                if (xlabels != null) {
                    for (int i = 0; i < curmat.length - 1; i++) {
                        pif.print(xlabels[i] + "\t");
                    }
                    pif.print(xlabels[curmat.length - 1] + "\n");
                }

                pif.print(ylabels[0] + "\t");
                for (int i = 0; i < curmat.length - 1; i++) {

                    //System.out.println("writeTab " + i + "\t" + j + "\t" + curmat[i][j]);
                    pif.print(curmat[i] + "\t");
                }
                pif.print(curmat[curmat.length - 1] + "\n");

                pif.close();
            } catch (IOException e) {
                System.out.println("Error writing file.  " + print);
            }
    }

    /**
     * Outputs Matrix in Matrix format to a file, if column and
     * row are -1 then writes all entries in order.
     *
     * @param curmat
     * @param name
     */
    public final void write(float[][] curmat, String name) {

        util.GiveDate give = new util.GiveDate();
        String dat = give.giveShortDate();
        String print = name + "." + dat + ".mat";

        try {

            PrintWriter pif = new PrintWriter(new FileWriter(print), true);

            for (int i = 0; i < curmat.length; i++)
                for (int j = 0; j < curmat[0].length; j++) {
                    if (labels != null)
                        pif.println((i) + "  " + (j) + "  " + curmat[i][j] + "   " + labels[i][j]);
                    else
                        pif.println((i) + "  " + (j) + "  " + curmat[i][j]);


                }
            pif.close();
        } catch (IOException e) {
            System.out.println("Error with writing file.  " + print);
        }
    }

    /**
     * Outputs Matrix in Matrix format to a file, if column and
     * row are -1 then writes all entries in order.
     *
     * @param name
     * @param column
     * @param row
     */
    public final void write(String name, int column, int row) {
        write(name, column, row, null);
    }

    /**
     * @param name
     * @param column
     * @param row
     * @param header
     */
    public final void write(String name, int column, int row, String header) {
        int a = column;
        int b = row;
        //util.GiveDate give = new util.GiveDate();
        //String dat = give.giveShortDate();
        String print = name;// + "." + dat + ".mat";
        if (column != -1) {
            print = print + ".col." + column;
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                if (header != null)
                    pif.println(header);
                for (int i = 0; i < mat[0].length; i++) {
                    pif.println((a) + "  " + (i) + "  " + mat[a][i]);
                }
                pif.close();
            } catch (IOException e) {
                System.out.println("Error with writing file.");
            }
        } else if (row != -1) {
            print = print + ".row." + row;
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                for (int i = 0; i < mat.length; i++) {
                    pif.println((i) + "  " + (b) + "  " + mat[i][b]);
                }
                pif.close();
            } catch (IOException e) {
                System.out.println("Error with writing file.");
            }
        } else if (column == -1 && row == -1) {
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                for (int i = 0; i < mat.length; i++)
                    for (int j = 0; j < mat[i].length; j++) {

                        if (labels != null)
                            pif.println((i) + "  " + (j) + "  " + mat[i][j] + "   " + labels[i][j]);
                        else
                            pif.println((i) + "  " + (j) + "  " + mat[i][j]);
                    }
                pif.close();
            } catch (IOException e) {
                System.out.println("Error with writing file.  " + print);
            }
        }
    }

    /**
     * @param outfile
     * @param header
     */
    public final void writeBlock(String outfile, String header) {
        try {
            TabFile.write(MoreArray.toString(mat, "", ""), outfile, xlabels, ylabels, "\t", header, null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR writeDistMatrix " + outfile);
        }
    }

    /**
     * @param outfile
     */
    public final void writeBlock(String outfile) {
        try {
            TabFile.write(MoreArray.toString(mat, "", ""), outfile, xlabels, ylabels, "\t", null, null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR writeDistMatrix " + outfile);
        }
    }

    /**
     * Outputs matrix in pure numerical format, ie no row/column indexing
     *
     * @param name
     * @param column
     * @param row
     */
    public final void writeNumbers(String name, int column, int row) {
        int a = column;
        int b = row;
        util.GiveDate give = new util.GiveDate();
        String dat = give.giveShortDate();
        String print = name + "." + dat + ".num";
        if (column != -1) { //System.out.println("WRITE column length   "+mat.length+"  referring to  "+column);
            print = print + ".col." + column;

            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                for (int i = 0; i < mat[0].length; i++) {
                    //System.out.println(i+"   "+mat[0].length+"   "+a+"   "+mat.length);
                    pif.println(mat[a][i]);
                }
                pif.close();
            } catch (IOException e) {
                System.out.println("Error with writing file.");
            }
        } else if (row != -1) { //System.out.println("WRITE row length   "+mat[0].length+"  referring to  "+row);
            print = print + ".row." + row;
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                for (int i = 0; i < mat.length; i++) {
                    //System.out.println(i+"   "+mat.length+"   "+b+"   "+mat[0].length);
                    pif.println(mat[i][b]);
                }
                pif.close();
            } catch (IOException e) {
                System.out.println("Error with writing file.");
            }
        } else if (column != -1 && row != -1) {
            try {
                PrintWriter pif = new PrintWriter(new FileWriter(print), true);
                for (int i = 0; i < mat.length; i++)
                    for (int j = 0; j < mat[i].length; j++)
                        pif.println(mat[i][j]);
                pif.close();
            } catch (IOException e) {
                System.out.println("Error with writing file.");
            }
        }
    }

    /**
     * Loads Matrix data from a file.
     * <p/>
     * 0  0  0.0
     * 0  1  1.9498773730673422
     * 0  2  2.127972591458828
     * 0  3  1.863213640461018
     * 0  4  1.868112946264224
     * 0  5  2.0342247220993066
     * 0  6  1.8674411583768844
     * 0  7  1.8814186774878152
     * 0  8  2.058700136008156
     * 0  9  1.9634144850234756
     * 0  10  1.873345088337971
     * 0  11  1.8719629777321987
     * 0  12  1.8867010123493335
     * 0  13  1.9761211045884814
     * 0  14  1.9346923372981037
     * 0  15  1.9485258171243203
     * 0  16  1.941229386239555
     * 0  17  1.9185116158105482
     * 0  18  1.9659355330223827
     * 0  19  2.087300277870915
     * 0  20  1.9168266092685586
     * 0  21  1.9159736871888402
     * 0  22  2.221905603305415
     * 0  23  2.22373830744537
     * 0  24  1.8532214006966357
     * 1  0  1.9498773730673422
     * etc.
     *
     * @param s
     */
    public final void load(String s) {


        mat = readMatrix(s);
        if (mat != null)
            setDimensions();

    }


    /**
     * @param str
     * @return
     */
    public static double[][] parseMatrix(String str) {
        int maxx = -1, maxy = -1;
        double[][] curmat;
        String data = null;
        StringTokenizer tokens = null;
        int tokType;
        Vector store = new Vector();
        int count = 0;
        int curind = str.indexOf("\n");
        boolean end = false;
        int linecount = 0;
        //System.out.println("parseMatrix " + curind + "\t" + str);
        while (!end) {
            //System.out.println(str);
            if (data != null) {
                data = util.StringUtil.nextLine(str, curind);
                curind = str.indexOf("\n", curind + 1);
            } else
                data = str.substring(0, curind);
            //System.out.println("parseMatrix " + linecount + "\t" + curind + "\tdata: " + data);
            if (data == null) {

                end = true;
                break;
            }
            linecount++;
            tokens = new StringTokenizer(data);
            if (tokens == null) {
                end = true;
                break;
            }
            int all = tokens.countTokens();
            int countok = 0;
            int i = 0;
            int j = 0;
            while (countok < all) {
                String curtok = tokens.nextToken();
                if (countok == 0) {
                    i = (int) (Integer.valueOf(curtok)).intValue();
                }
                if (countok == 1) {
                    j = (int) (Integer.valueOf(curtok)).intValue();
                }
                if (countok == 2) {
                    //System.out.println("i and j "+i+"\t"+j);
                    double now = Double.NaN;
                    int endex = curtok.indexOf("E");
                    if (endex == -1)
                        now = (Double.parseDouble(curtok));
                    else {
                        double val = (Double.parseDouble(curtok.substring(0, endex)));
                        int scale = 0;
                        int minendex = curtok.indexOf("E-");
                        if (minendex != -1)
                            scale = -Integer.parseInt(curtok.substring(minendex + 2, curtok.length()));
                        else
                            scale = Integer.parseInt(curtok.substring(endex + 1, curtok.length()));
                        //BigDecimal bd = new BigDecimal(val, scale);
                        now = val * Math.pow((double) 10.0, scale);
                    }

                    DTriple trip = new DTriple((double) i, (double) j, now);
                    if (i > maxx)
                        maxx = i;
                    if (j > maxy)
                        maxy = j;
                    //System.out.println("adding   "+i+"  "+j+"  "+now);
                    store.addElement(trip);
                }
                countok++;
            }
        }
        //System.out.println("new curmat "+maxx+"\t"+maxy);
        curmat = new double[maxx + 1][maxy + 1];
        for (int ab = 0; ab < store.size(); ab++) {
            DTriple pass = (DTriple) store.elementAt(ab);
            int ex = (int) pass.a;
            int yi = (int) pass.b;
            curmat[ex][yi] = pass.c;
        }
        if (store.size() == 0) {
            curmat = null;
            //System.out.println("setting matrix to null.");
        }
        return curmat;
    }

    /**
     * @param s
     * @return
     */
    public static double[][] readMatrix(String s) {
        double[][] curmat;
        String dataall = "";
        //System.out.println("loading matrix  "+s);
        try {
            BufferedReader in = new BufferedReader(new FileReader(s));
            String data = in.readLine();
            while (data.indexOf("#") == 0)
                data = in.readLine();
            //xlabels
            data = in.readLine();
            while (data != null) {
                if (data.indexOf("#") != 0)
                    dataall += data + "\n";
                data = in.readLine();
            }
        } catch (IOException e) {
        }
        curmat = parseMatrix(dataall);
        //	System.out.println("finished loading matrix.");
        return curmat;
    }

    /**
     * @param s
     * @return
     */
    public final double[][] readMatrixOld(String s) {

//System.out.println("readMatrix "+s);

        Vector store = new Vector();
        Vector veclabels = new Vector();
        try {
            BufferedReader in = new BufferedReader(new FileReader(s));

            String data = null;
            StringTokenizer tokens = null;
            int tokType;
            int count = 0;

            boolean end = false;
            int linecount = 0;
            try {

                while (end == false) {
                    data = in.readLine();
                    //System.out.println(linecount+"   "+data);
                    if (data == null) {
                        end = true;
                        break;
                    }

                    linecount++;

                    tokens = new StringTokenizer(data);

                    if (tokens == null) {
                        end = true;
                        break;
                    }

                    int all = tokens.countTokens();

                    int countok = 0;
                    int i = 0;
                    int j = 0;

                    while (countok < all) {

                        if (countok == 0) {
                            i = (int) (Double.valueOf(tokens.nextToken())).doubleValue();
                        }

                        if (countok == 1) {
                            j = (int) (Double.valueOf(tokens.nextToken())).doubleValue();
                        }

                        if (countok == 2) {
                            double now = (Double.valueOf(tokens.nextToken())).doubleValue();
                            DTriple trip = new DTriple((double) i, (double) j, now);
                            //System.out.println("reading   "+i+"  "+j+"  "+now);
                            //System.out.println("adding   "+trip.toString());
                            store.addElement(trip);
                        }
                        if (countok == 3) {
                            String maybe = tokens.nextToken();
                            //System.out.println(maybe);
                            if (maybe.length() != 0)
                                veclabels.addElement(maybe);
                        }
                        countok++;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error with file.");
            }
        } catch (IOException e) {
            System.out.println("No such file.");
        }

        int size = (int) Math.sqrt(store.size());

        double[][] ret = new double[size][size];

        if (veclabels.size() > 0) {
            labels = new String[size][size];
            xlabels = new String[size];
        }

        for (int a = 0; a < store.size(); a++) {

            DTriple pass = (DTriple) store.elementAt(a);
            int ex = (int) pass.a;
            int yi = (int) pass.b;
            ret[ex][yi] = pass.c;
            if (veclabels.size() > 0) {

                String safe = (String) veclabels.elementAt(a);
                labels[ex][yi] = safe;

                if (ex == yi) {

                    int star = safe.indexOf("*");
                    xlabels[ex] = safe.substring(star + 1, safe.length());
                }
            }
        }

        return ret;
    }


    /**
     * Finds Euclidean distance between two Coord objects. The first three floats are considered x,y,z.
     *
     * @param a
     * @param b
     * @return
     */
    public static double thrdist(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double abi = Math.pow((double) a[i] - b[i], (double) 2);
            sum += abi;
        }
        double ret = Math.sqrt(sum);
        return ret;
    }


    /**
     * Calculates a scaling factor for the data in relation of 255 color units.
     *
     * @param a
     */
    public void scaleDiag(double[][] a) {
        double min1 = a[0][0], max1 = a[0][0];
        double min2 = a[0][0], max2 = a[0][0];
        double scaleposdiag = 1;
        double scalenegdiag = 1;
        boolean neg = false;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (i == j)
                    if (a[i][j] < 0)
                        neg = true;
            }
        }

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++)
                if (i == j) {
                    if (max1 < a[i][j])
                        max1 = a[i][j];
                    if (min1 > a[i][j])
                        min1 = a[i][j];
                    if (neg == true) {
                        if (max2 < a[i][j])
                            max2 = a[i][j];
                        if (min2 > a[i][j])
                            min2 = a[i][j];
                    }
                }
        }
        scaleposdiag = ((double) (Math.abs(max1) - Math.abs(min1)) / (double) 255);

        if (neg == true)
            scalenegdiag = ((double) (Math.abs(max2) - Math.abs(min2)) / (double) 255);
    }

    /**
     * Calculates a scaling factor for the data in relation of 255 color units.
     * Ignores the diagonal.
     *
     * @param a
     */
    public void scaleNonDiag(double[][] a) {
        double min1 = a[0][0], max1 = a[0][0];
        double min2 = a[0][0], max2 = a[0][0];
        double scalepos = 1;
        double scaleneg = 1;
        boolean neg = false;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++)
                if (i != j) {
                    if (a[i][j] < 0)
                        neg = true;
                }
        }

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++)
                if (i != j) {
                    if (max1 < a[i][j])
                        max1 = a[i][j];
                    if (min1 > a[i][j])
                        min1 = a[i][j];


                    if (neg == true) {
                        if (max2 < a[i][j])
                            max2 = a[i][j];
                        if (min2 > a[i][j])
                            min2 = a[i][j];
                    }
                }
        }
        scalepos = ((double) (Math.abs(max1) - Math.abs(min1)) / (double) 255);

        if (neg)
            scaleneg = ((double) (Math.abs(max2) - Math.abs(min2)) / (double) 255);
    }

    /**
     * Finds the min/max of entries.
     *
     * @param smat
     * @return
     */
    public static double[] findMinMax(double[][] smat) {

        double[] ret = new double[2];
        ret[0] = findMin(smat);
        ret[1] = findMax(smat);

        return ret;
    }

    /**
     * Finds the min/max of > 0 entries. By convention >=0.
     *
     * @param smat
     * @return
     */
    public static double[] findMinMaxPos(double[][] smat) {

        double gmi = smat[0][0];
        double gma = smat[0][0];
        //System.out.println("findMinMaxPos start "+gmi+"\t"+gma);

        for (int j = 0; j < smat.length; j++) {

            for (int k = 0; k < smat[0].length; k++) {

                Double test = new Double(smat[j][k]);

                if (!test.isNaN() && !test.isInfinite())
                    if (smat[j][k] >= 0) {
                        if (smat[j][k] > gma)
                            gma = smat[j][k];
                        if (smat[j][k] < gmi)
                            gmi = smat[j][k];
                    }
            }
        }
        System.out.println("findMinMaxPos  " + gmi + "\t" + gma);
        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * @param smat
     * @return
     */
    public static double[] findMinMaxNonDiag(double[][] smat) {

        double gmi = smat[0][0];
        double gma = smat[0][0];

        for (int j = 0; j < smat.length; j++) {

            for (int k = 0; k < smat[0].length; k++) {

                if (j != k) {
                    if (smat[j][k] > gma)
                        gma = smat[j][k];
                    if (smat[j][k] < gmi)
                        gmi = smat[j][k];
                }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of > 0 entries. By convention >=0...
     */
    public static double[] findMinMaxPosNonDiag(double[][] smat) {

        double gmi = smat[0][0];
        double gma = smat[0][0];

        for (int j = 0; j < smat.length; j++) {

            for (int k = 0; k < smat[0].length; k++) {

                if (smat[j][k] >= 0 && j != k) {
                    if (smat[j][k] > gma)
                        gma = smat[j][k];
                    if (smat[j][k] < gmi)
                        gmi = smat[j][k];
                }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of > 0 entries.
     */
    public static double[] findMinMaxNeg(double[][] smat) {

        double gmi = smat[0][0];
        double gma = smat[0][0];

        for (int j = 0; j < smat.length; j++) {

            for (int k = 0; k < smat[0].length; k++) {

                if (smat[j][k] < 0) {
                    if (smat[j][k] > gma)
                        gma = smat[j][k];
                    if (smat[j][k] < gmi)
                        gmi = smat[j][k];
                }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of > 0, nondiagonal entries.
     */
    public static double[] findMinMaxNegNonDiag(double[][] smat) {

        double gmi = smat[0][0];
        double gma = smat[0][0];

        for (int j = 0; j < smat.length; j++) {

            for (int k = 0; k < smat[0].length; k++) {

                if (smat[j][k] < 0 && j != k) {
                    if (smat[j][k] > gma)
                        gma = smat[j][k];
                    if (smat[j][k] < gmi)
                        gmi = smat[j][k];
                }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }


    /**
     * All values are pushed positive such that the most negative is 0.
     */
    public static double[][] pushtoPos(double[][] n, double minim) {

        for (int i = 0; i < n.length; i++) {
            for (int j = 0; j < n[0].length; j++) {

                n[i][j] += minim;
            }
        }

        return n;
    }

    /**
     * Normalizes the array to an interval.
     */
    public static double[][] normtoInterval(double[][] n, double a, double b) {
        double min = findMin(n);

//n=pushtoPos(n,min);
//min=findMin(n);
        double max = findMax(n);

        double scale = Math.abs(b - a) / Math.abs(max - min);

        for (int i = 0; i < n.length; i++) {
            for (int j = 0; j < n[0].length; j++) {

                n[i][j] = n[i][j] * scale;

            }
        }
        return n;
    }

    /**
     * Normalizes the array to an interval.
     */
    public static double[] normtoInterval(double[] n, double a, double b) {

        double min = findMin(n);

        //n=pushtoPos(n,min);
        //min=findMin(n);
        double max = findMax(n);

        BigDecimal big_scale = new BigDecimal(Math.abs(b - a));
        big_scale = big_scale.divide(new BigDecimal(Math.abs(max - min)), 50, BigDecimal.ROUND_HALF_UP);

        //double scale = big_scale.doubleValue();
        double minless = Double.NaN;
        double maxmore = Double.NaN;

        int less = 0, more = 0;
        for (int i = 0; i < n.length; i++) {
            if (!Double.isNaN(n[i])) {
                BigDecimal mult1 = new BigDecimal(n[i]);
                mult1 = mult1.multiply(big_scale);
                n[i] = mult1.doubleValue();//n[i] * scale;
                //System.out.println("Matrix.normtoInterval " + i + "\t" + n[i]);
                if (n[i] < a) {
                    less++;
                    if (Double.isNaN(minless) || minless > n[i])
                        minless = n[i];
                } else if (n[i] > b) {
                    more++;
                    if (Double.isNaN(maxmore) || maxmore < n[i])
                        maxmore = n[i];
                }
            }
        }
        if (less > 0)
            System.out.println("ISSUE normtoInterval: " + less + " values are < the lower bound " + a + ", min = " + minless);
        if (more > 0)
            System.out.println("ISSUE normtoInterval: " + more + " values are > the upper bound " + b + ", max = " + maxmore);
        return n;
    }


    /**
     * Normalizes each array row by the row sums.
     *
     * @param n
     * @return
     */
    public static double[][] normbyRow(double[][] n) {
        for (int i = 0; i < n.length; i++) {
            double sum = stat.sumEntries(n[i]);
            if (sum != 0)
                n[i] = stat.norm(n[i], sum);
            else {
                for (int j = 0; j < n[0].length; j++) {
                    n[i][j] = 0;
                }
            }
        }
        return n;
    }

    /**
     * Normalizes each array column by the column sums.
     *
     * @param n
     * @return
     */
    public static double[][] normbyCol(double[][] n) {
        for (int i = 0; i < n[0].length; i++) {
            double[] col = extractColumn(n, i + 1);
            double sum = stat.sumEntries(col);
            col = stat.norm(col, sum);
            n = replaceColumn(n, col, i + 1);
        }
        return n;
    }


    /**
     * Normalizes each column per column in the array.
     *
     * @param n
     * @return
     */
    public static double[][] normperCol(double[][] n) {
        for (int i = 0; i < n[0].length; i++) {
            double[] curcol = getColumn(n, i);
            double total = mathy.stat.sumEntries(curcol);
            //System.out.println(" normperCol " + total);
            curcol = mathy.stat.norm(curcol, total);
            for (int j = 0; j < n.length; j++) {
                //System.out.println("normperCol "+total+"\t"+n[j][i]+"\t"+curcol[j]);
                n[j][i] = curcol[j];
            }
        }
        return n;
    }

    /**
     * @param n
     * @param index
     * @return
     */
    public static double[][] normperCol(double[][] n, int[] index) {
        for (int i = 0; i < n[0].length; i++) {
            double[] curcol = getColumn(n, i);
            double total = mathy.stat.sumEntries(curcol, index);
            //System.out.println(" normperCol " + total);
            curcol = mathy.stat.norm(curcol, total);
            for (int j = 0; j < n.length; j++) {
                //System.out.println("normperCol "+total+"\t"+n[j][i]+"\t"+curcol[j]);
                n[j][i] = curcol[j];
            }
        }
        return n;
    }

    /**
     * Normalizes each column per column in the array.
     */
    public static double[][] meanOneperCol(double[][] n) {
        for (int i = 0; i < n[0].length; i++) {
            double[] curcol = getColumn(n, i);
            double avg = mathy.stat.avg(curcol);
            //System.out.println(" normperCol " + total);
            curcol = subtract(curcol, avg);
            for (int j = 0; j < n.length; j++) {
                //System.out.println("normperCol "+total+"\t"+n[j][i]+"\t"+curcol[j]);
                n[j][i] = curcol[j];
            }
        }
        return n;
    }


    /**
     * Return a column from a 2D array.
     *
     * @param n
     * @param col
     * @return
     */
    public static double[] getColumn(double[][] n, int col) {
        double[] ret = new double[n.length];
        for (int i = 0; i < n.length; i++) {

            ret[i] = n[i][col];
        }
        return ret;
    }

    /**
     * @return
     */
    public String toString(int column, int row) {
        int a = column;
        int b = row;
        String ret = "";
        if (column != -1) {
            for (int i = 0; i < mat[0].length; i++) {
                ret += ((a) + "  " + (i) + "  " + mat[a][i]) + "\n";
            }
        } else if (row != -1) {
            for (int i = 0; i < mat.length; i++) {
                ret += ((i) + "  " + (b) + "  " + mat[i][b]) + "\n";
            }
        } else if (column == -1 && row == -1) {
            for (int i = 0; i < mat.length; i++)
                for (int j = 0; j < mat[i].length; j++) {
                    if (labels != null)
                        ret += ((i) + "  " + (j) + "  " + mat[i][j] + "   " + labels[i][j]) + "\n";
                    else
                        ret += ((i) + "  " + (j) + "  " + mat[i][j]) + "\n";
                }
        }
        return ret;
    }


    /**
     * Creates a new truncated array w/o row and columns having only values = 0.
     *
     * @param in
     * @return
     */
    public static ArrayList remZeroRowCol(double[][] in) {
        return remRowCol(in, 0);
    }

    /**
     * Creates a new truncated array w/o row and columns having only values = filter.
     *
     * @param in
     * @return
     */
    public static ArrayList remRowCol(double[][] in, double filter) {
        ArrayList ret = new ArrayList();
        int[] rem = new int[in.length];
        int[] index = new int[in.length];
        int count = 0;
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in.length; j++) {
                if (in[i][j] != filter) {
                    rem[i]++;
                }
            }
            if (rem[i] > 0) {
                index[i] = count;
                count++;
            }
        }
        int[] retindex = new int[count];
        count = 0;
        for (int i = 0; i < in.length; i++) {
            if (rem[i] > 0) {
                retindex[count] = i;
                count++;
            }
        }
        double[][] retarray = new double[count][count];
        for (int i = 0; i < in.length; i++) {
            if (rem[i] > 0)
                for (int j = 0; j < in.length; j++) {
                    if (rem[j] > 0)
                        retarray[index[i]][index[j]] = in[i][j];
                }
        }
        int[] ret2index = new int[count];
        for (int j = 0; j < in.length; j++) {
            if (rem[j] > 0)
                ret2index[index[j]] = j;
        }
        ret.add((double[][]) retarray);
        ret.add((int[]) retindex);
        ret.add((int[]) ret2index);
        return ret;
    }


    /**
     * Returns a one dimensional array from the two dimensional array.
     *
     * @param in
     * @return
     */
    public static double[] TwoDtoOneD(double[][] in) {
        int len = in.length;
        double[] ret = new double[len * len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                ret[i * len + j] = in[i][j];
                //if(ret[i * len + j] > 0)
                //  System.out.println("TwoDtoOneD "+ret[i * len + j]);
            }
        }
        return ret;
    }

    /**
     * Converts an int array to double.
     *
     * @param in
     * @return
     */
    public static double[][] convInttoDouble(int[][] in) {
        double[][] d = new double[in.length][in[0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                d[i][j] = in[i][j];
            }
        }
        return d;
    }


    /**
     * Returns a one dimensional array from the two dimensional array.
     *
     * @param in
     * @return
     */
    public static double[][] replace(double[][] in, double find, double replace) {
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                if (in[i][j] == find)
                    in[i][j] = replace;
            }
        }
        return in;
    }

    /**
     * @param in
     * @return
     */
    public static double[][] replaceLessThanAbs(double[][] in, double threshold, double replace) {
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                if (Math.abs(in[i][j]) < threshold)
                    in[i][j] = replace;
            }
        }
        return in;
    }


    /**
     * @param in
     * @return
     */
    public static double[][] replaceNaN(double[][] in, double replace) {
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                if (Double.isNaN(in[i][j]))
                    in[i][j] = replace;
            }
        }
        return in;
    }

    /**
     * Changes diagonal entries in a specified band to the provided value.
     *
     * @param in
     * @return array with manipulated diagonal band
     */
    public static double[][] cleanDiagonalSym(double[][] in, int separ, double to) {
        for (int i = 0; i < in.length; i++) {
            for (int j = i - separ; j < i + separ; j++) {
                if (j > 0 && j < in.length) {
                    in[i][j] = to;
                }
            }
        }
        return in;
    }

    /**
     * Computes the column sum.
     *
     * @param d
     * @return column sum
     */
    public static int[] columnSum(int[][] d) {
        int[] ret = new int[d[0].length];
        for (int j = 0; j < d[0].length; j++) {
            int cursum = 0;
            for (int i = 0; i < d.length; i++) {
                cursum += d[i][j];
            }
            ret[j] = cursum;
        }
        return ret;
    }

    /**
     * Computes the column sum.
     *
     * @param d
     * @return column sum
     */
    public static double[] columnSum(double[][] d) {
        double[] ret = new double[d[0].length];
        //for each column
        for (int j = 0; j < d[0].length; j++) {
            double cursum = 0;
            //for all rows
            for (int i = 0; i < d.length; i++) {
                if (!Double.isNaN(d[i][j]))
                    cursum += d[i][j];
            }
            ret[j] = cursum;
        }
        return ret;
    }

    /**
     * Computes the column max.
     *
     * @param d
     * @return column sum
     */
    public static double[] columnMax(double[][] d) {
        double[] ret = new double[d[0].length];
        //for each column
        for (int i = 0; i < d[0].length; i++) {
            ret[i] = mathy.stat.findMax(mathy.Matrix.extractColumn(d, i));
        }
        return ret;
    }

    /**
     * Computes the column max.
     *
     * @param d
     * @return column sum
     */
    public static double[] rowMax(double[][] d) {
        double[] ret = new double[d.length];
        //for each column
        for (int i = 0; i < d.length; i++) {
            ret[i] = mathy.stat.findMax((d[i]));
        }
        return ret;
    }

    /**
     * Computes the column sum.
     *
     * @param d
     * @return column sum
     */
    public static int[] rowSum(int[][] d) {
        int[] ret = new int[d.length];
        for (int j = 0; j < d.length; j++) {
            int rowsum = 0;
            for (int i = 0; i < d[0].length; i++) {
                rowsum += d[i][j];
            }
            ret[j] = rowsum;
        }
        return ret;
    }

    /**
     * @param d
     * @return
     */
    public static int rowSum(int[][] d, int k) {
        int rowsum = 0;
        for (int i = 0; i < d[0].length; i++) {
            rowsum += d[k][i];
        }
        return rowsum;
    }

    /**
     * @param d
     * @return
     */
    public static double rowSum(double[][] d, int k) {
        double rowsum = 0;
        for (int i = 0; i < d[0].length; i++) {
            rowsum += d[k][i];
        }
        return rowsum;
    }

    /**
     * Computes the maximum column sum.
     *
     * @param d
     * @return maximum column sum
     */
    public static Object[] maxColumnSum(double[][] d) {
        Object[] ret = new Object[3];
        double dr = Double.NaN;
        int pos = -1;
        double[] sum = new double[d[0].length];
        for (int j = 0; j < d[0].length; j++) {
            double cursum = 0;
            for (int i = 0; i < d.length; i++) {
                if (!Double.isNaN(d[i][j]))
                    cursum += d[i][j];
            }
            sum[j] = cursum;
            //System.out.println("maxColumnSum "+cursum+"\tcolumn "+i);
            if (cursum > dr || Double.isNaN(dr)) {
                dr = cursum;
                pos = j;
            }
        }
        ret[0] = new Double(dr);
        ret[1] = new Integer(pos);
        ret[2] = sum;
        return ret;
    }

    /**
     * Computes the maximum column sum.
     *
     * @param d
     * @return maximum column sum
     */
    public static Object[] maxRowSum(double[][] d) {
        Object[] ret = new Object[3];
        double dr = Double.NaN;
        int pos = -1;
        double[] sum = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            double cursum = 0;
            for (int j = 0; j < d[0].length; j++) {
                if (!Double.isNaN(d[i][j]))
                    cursum += d[i][j];
            }
            sum[i] = cursum;
            //System.out.println("maxColumnSum "+cursum+"\tcolumn "+i);
            if (cursum > dr || Double.isNaN(dr)) {
                dr = cursum;
                pos = i;
            }
        }
        ret[0] = new Double(dr);
        ret[1] = new Integer(pos);
        ret[2] = sum;
        return ret;
    }

    /**
     * Computes the maximum column sum.
     *
     * @param d
     * @return maximum column sum
     */
    public static Object[] maxColumnSum(double[][] d, double s) {
        Object[] ret = new Object[3];
        double dr = Double.NaN;
        int pos = -1;
        double[] sum = new double[d[0].length];
        for (int j = 0; j < d[0].length; j++) {
            double cursum = 0;
            for (int i = 0; i < d.length; i++) {
                if (!Double.isNaN(d[i][j])) {
                    double cur = d[i][j] * s;
                    BigDecimal bd = new BigDecimal(cur);
                    bd = bd.divide(new BigDecimal(s), 50, BigDecimal.ROUND_HALF_UP);
                    cursum += bd.doubleValue();
                }
            }
            cursum /= s;
            sum[j] = cursum;
            //System.out.println("maxColumnSum "+cursum+"\tcolumn "+i);
            if (cursum > dr || Double.isNaN(dr)) {
                dr = cursum;
                pos = j;
            }
        }
        ret[0] = new Double(dr);
        ret[1] = new Integer(pos);
        ret[2] = sum;
        return ret;
    }

    /**
     * Computes the maximum column sum.
     *
     * @param d
     * @return maximum column sum
     */
    public static Object[] maxRowSum(double[][] d, double s) {
        Object[] ret = new Object[3];
        double dr = Double.NaN;
        int pos = -1;
        double[] sum = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            double cursum = 0;
            for (int j = 0; j < d[0].length; j++) {
                if (!Double.isNaN(d[i][j])) {
                    double cur = d[i][j] * s;
                    BigDecimal bd = new BigDecimal(cur);
                    bd = bd.divide(new BigDecimal(s), 50, BigDecimal.ROUND_HALF_UP);
                    cursum += bd.doubleValue();
                }
            }
            cursum /= s;
            sum[i] = cursum;
            //System.out.println("maxColumnSum "+cursum+"\tcolumn "+i);
            if (cursum > dr || Double.isNaN(dr)) {
                dr = cursum;
                pos = i;
            }
        }
        ret[0] = new Double(dr);
        ret[1] = new Integer(pos);
        ret[2] = sum;
        return ret;
    }

    /**
     * Computes the maximum column sum.
     *
     * @param d
     * @return maximum column sum
     */
    public static Object[] maxColumnIntSum(double[][] d) {
        //double round = mathy.stat.roundUp(cgp.jcg.data[i][j], 0);
        Object[] ret = new Object[3];
        double dr = Double.NaN;
        int pos = -1;
        double[] sum = new double[d[0].length];
        for (int j = 0; j < d[0].length; j++) {
            double cursum = 0;
            for (int i = 0; i < d.length; i++) {
                if (!Double.isNaN(d[i][j])) {
                    double round = mathy.stat.roundUp(d[i][j], 0);
                    //System.out.println("maxColumnIntSum "+d[i][j]+"\trounded "+round);
                    cursum += d[i][j];
                }
            }
            sum[j] = cursum;
            //System.out.println("maxColumnSum "+cursum+"\tcolumn "+i);
            if (cursum > dr || Double.isNaN(dr)) {
                dr = cursum;
                pos = j;
            }
        }
        ret[0] = new Double(dr);
        ret[1] = new Integer(pos);
        ret[2] = sum;
        return ret;
    }

    /**
     * Computes the maximum column sum.
     *
     * @param d
     * @return maximum column sum
     */
    public static Object[] maxRowIntSum(double[][] d) {
        //double round = mathy.stat.roundUp(cgp.jcg.data[i][j], 0);
        Object[] ret = new Object[3];
        double dr = Double.NaN;
        int pos = -1;
        double[] sum = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            double cursum = 0;
            for (int j = 0; j < d[0].length; j++) {
                if (!Double.isNaN(d[i][j])) {
                    double round = mathy.stat.roundUp(d[i][j], 0);
                    //System.out.println("maxColumnIntSum "+d[i][j]+"\trounded "+round);
                    cursum += d[i][j];
                }
            }
            sum[i] = cursum;
            //System.out.println("maxColumnSum "+cursum+"\tcolumn "+i);
            if (cursum > dr || Double.isNaN(dr)) {
                dr = cursum;
                pos = i;
            }
        }
        ret[0] = new Double(dr);
        ret[1] = new Integer(pos);
        ret[2] = sum;
        return ret;
    }

    /**
     * @param log_mean_data
     */
    public static Matrix computeAndWriteCorrelations(double[][] log_mean_data, String[] labels, String outfile) {
        int size = log_mean_data.length;// * log_mean_data.length;
        double[][] correl = computeCorrelations(log_mean_data);
        //System.out.println("Finished correlating. Writing file ...");
        String[] pair_labels = makePairLabels(size, labels);
        Matrix.writeTab(correl, pair_labels, pair_labels, outfile);
        Matrix ret = new Matrix(correl, pair_labels, pair_labels);
        return ret;
    }


    /**
     * @param log_mean_data
     */
    public static double[][] computeCorrelations(double[][] log_mean_data) {
        int size = log_mean_data.length;// * log_mean_data.length;
        double[][] correl = new double[size][size];
        for (int i = 0; i < size; i++) {
            double[] rowi = log_mean_data[i];
            for (int j = i; j < size; j++) {
                if (i == j) {
                    correl[i][j] = 1;
                    correl[j][i] = correl[i][j];
                } else {
                    double[] rowj = log_mean_data[j];
                    //System.out.println(i + "\t" + j + "\ti " + rowi[0] + "\tj " + rowj[0]);
                    //System.out.println("correlating data pair " + i);
                    correl[i][j] = mathy.stat.correlationCoeff(rowi, rowj);
                    correl[j][i] = correl[i][j];
                }
            }
        }

        return correl;
    }

    /**
     * Makes labels for the experiment file channel pairs.
     *
     * @return pair labels
     */
    private static String[] makePairLabels(int size, String[] labels) {
        String[] ret = new String[size];
        int count = 0;
        for (int i = 0; i < labels.length; i++) {
            for (int j = 0; j < labels.length; j++) {
                ret[count] = labels[i] + "_" + labels[j];
            }
        }
        return ret;
    }

    /**
     * Extracts a column from the 2D array - 1 offset.
     *
     * @param sarray
     * @param col
     * @return
     */
    public static double[] extractColumn(double[][] sarray, int col) {
        col--;
        double[] ret = new double[sarray.length];
        if (col >= 0) {
            for (int i = 0; i < sarray.length; i++) {
                try {
                    ret[i] = sarray[i][col];
                } catch (Exception e) {
                    ret[i] = Double.NaN;
                    System.out.println("extractColumn not a double " + sarray[i][col]);
                }
            }
        } else {
            System.out.println("WARNING extractColumn index < 1 " + col);
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
    public static int[] extractColumn(int[][] sarray, int col) {
        col--;
        int[] ret = new int[sarray.length];
        if (col >= 0)
            for (int i = 0; i < sarray.length; i++) {
                try {
                    ret[i] = sarray[i][col];
                } catch (Exception e) {
                    ret[i] = 0;
                    System.out.println("extractColumn not an int " + sarray[i][col]);
                }
            }
        return ret;
    }

    /**
     * Extracts a column segment from the 2D array - column and row numbering start at 1.
     *
     * @param sarray
     * @param col
     * @param rows
     * @return
     */
    public static double[] extractColumnSegment(double[][] sarray, int col, int[] rows) {
        col--;
        double[] ret = new double[rows.length];
        if (col >= 0)
            for (int i = 0; i < rows.length; i++) {
                try {
                    ret[i] = sarray[rows[i] - 1][col];
                } catch (Exception e) {
                    ret[i] = Double.NaN;
                }
            }
        return ret;
    }

    /**
     * Extracts a rpw segment from the 2D array - column and row numbering start at 1.
     *
     * @param sarray
     * @param row
     * @param cols
     * @return
     */
    public static double[] extractRowSegment(double[][] sarray, int row, int[] cols) {
        row--;
        double[] ret = new double[cols.length];
        if (row >= 0)
            for (int i = 0; i < cols.length; i++) {
                try {
                    ret[i] = sarray[row][cols[i] - 1];
                } catch (Exception e) {
                    ret[i] = Double.NaN;
                }
            }
        return ret;
    }

    /**
     * col to be removed starts at 1
     *
     * @param sarray
     * @param coldata
     * @param col
     * @return
     */
    public static double[][] replaceColumn(double[][] sarray, double[] coldata, int col) {
        col--;
        if (col >= 0)
            for (int i = 0; i < sarray.length; i++) {
                sarray[i][col] = coldata[i];
            }
        return sarray;
    }

    /**
     * col to be removed starts at 1
     *
     * @param sarray
     * @param coldata
     * @param col
     * @return
     */
    public static int[][] replaceColumn(int[][] sarray, int[] coldata, int col) {
        col--;
        if (col >= 0)
            for (int i = 0; i < sarray.length; i++) {
                sarray[i][col] = coldata[i];
            }
        return sarray;
    }

    /**
     * Removed the columns specified as Integer in the ArrayList
     * Column index starts at 1.
     *
     * @param s
     * @param cols
     * @return
     */
    public static double[][] removeColumns(double[][] s, ArrayList cols) {
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
                if (not[index - 1] != 1) {
                    not[index - 1] = 1;
                    remove++;
                }
            }
        }
        ArrayList tmp = new ArrayList();
        int new_len = orig_cols - remove;
        //System.out.println("removeColumns new_len " + new_len + "\t" + orig_cols + "\t" + remove);

        for (int i = 0; i < rows; i++) {
            double[] add = new double[new_len];
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
        return MoreArray.convtodouble2DArray(tmp, new_len);
    }

    /**
     * Removed the columns specified as Integer in the ArrayList
     * Column index starts at 1.
     *
     * @param s
     * @param cols
     * @return
     */
    public static int[][] removeColumns(int[][] s, ArrayList cols) {
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
                if (not[index - 1] != 1) {
                    not[index - 1] = 1;
                    remove++;
                }
            }
        }
        ArrayList tmp = new ArrayList();
        int new_len = orig_cols - remove;
        //System.out.println("removeColumns new_len " + new_len + "\t" + orig_cols + "\t" + remove);

        for (int i = 0; i < rows; i++) {
            int[] add = new int[new_len];
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
        return MoreArray.convtoint2DArray(tmp, new_len);
    }

    /**
     * Removed the rows specified as Integer in the ArrayList
     * Row index starts at 1.
     *
     * @param s
     * @param rows
     * @return
     */
    public static double[][] removeRows(double[][] s, ArrayList rows) {
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
        return MoreArray.convtodouble2DArray(tmp, row_len);
    }

    /**
     * Removed the rows specified as Integer in the ArrayList
     * Row index starts at 1.
     *
     * @param s
     * @param rows
     * @return
     */
    public static int[][] removeRows(int[][] s, ArrayList rows) {
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
        return MoreArray.convtoint2DArray(tmp, row_len);
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
     * @param to
     * @return
     */
    public static double[][] copy(double[][] from, double[][] to) {
        to = new double[from.length][from[0].length];
        for (int i = 0; i < from.length; i++)
            for (int j = 0; j < from[0].length; j++)
                to[i][j] = from[i][j];
        return to;
    }


    /**
     * @return
     */
    public static double[] toPairs(double[][] data) {
        ArrayList pairs = new ArrayList();
        for (int i = 0; i < data.length; i++) {
            for (int j = i + 1; j < data[0].length; j++) {
                pairs.add(new Double(data[i][j]));
            }
        }
        return MoreArray.ArrayListtoDouble(pairs);
    }


    /**
     * @param a
     * @return
     */
    public static double Arrayavg(double[][] a) {
        double sum = 0;
        double k = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (!Double.isNaN(a[i][j])) {
                    sum += a[i][j];
                    k++;
                }
            }
        }
        double mea = sum / k;
        return mea;
    }

    /**
     * @param a
     * @return
     */
    public static float Arrayavg(float[][] a) {
        float sum = 0;
        float k = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (!Float.isNaN(a[i][j])) {
                    sum += a[i][j];
                    k++;
                }
            }
        }
        float mea = sum / k;
        return mea;
    }

    /**
     * @param a
     * @return
     */
    public final double ArrayavgPosNonZero(int[][] a) {
        double sum = 0;
        double k = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (a[i][j] > 0) {
                    sum += a[i][j];
                    k++;
                }
            }
        }
        double ret = Double.NaN;
        if (k > 0)
            ret = sum / k;
        return ret;
    }


    /**
     * @param a
     * @return
     */
    public static double[] ArrayavgbyRow(double[][] a) {

        double[] sum = new double[a.length];
        double[] k = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (!Double.isNaN(a[i][j])) {
                    sum[i] += a[i][j];
                    k[i]++;
                }
            }
        }
        double[] mea = stat.divide(sum, k);
        return mea;
    }

    /**
     * @param a
     * @param mean
     * @return
     */
    public static double[] ArraySDbyRow(double[][] a, double[] mean) {
        double[] sum = new double[a.length];
        double[] k = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (!Double.isNaN(a[i][j])) {
                    sum[i] += Math.pow((a[i][j] - mean[i]), 2);
                    k[i]++;
                }
            }
        }
        double[] sda = stat.divide(sum, k);
        sda = stat.sqrt(sda);
        return sda;
    }

    /**
     * @param a
     * @return
     */
    public static double symArrayavg(double[][] a) {
        double sum = 0;
        double k = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = i; j < a.length; j++) {
                if (!Double.isNaN(a[i][j])) {
                    sum += a[i][j];
                    k++;
                }
            }
        }
        double mea = sum / k;
        return mea;
    }

    /**
     * @param a
     * @return
     */
    public static double symArrayNonZeroavg(double[][] a) {
        double sum = 0;
        double k = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = i; j < a.length; j++) {
                if (!Double.isNaN(a[i][j])) {
                    if (a[i][j] > 0) {
                        sum += a[i][j];
                        k++;
                    }
                }
            }
        }
        double ret = Double.NaN;
        if (k > 0)
            ret = sum / k;
        return ret;

    }

    /**
     * @param val
     * @param d
     * @return
     */
    public static double[][] mergeArrayHorizontal(double[][] val, double[][] d) {
        double[][] join = new double[d.length][d[0].length + val[0].length];
        System.out.println("mergeArrayHorizontal " + val.length + "\t" + d.length);
        for (int i = 0; i < val.length; i++) {
            System.out.println("mergeArrayHorizontal v " + i + "\t" + val[i].length);
            System.out.println("mergeArrayHorizontal d " + i + "\t" + d[i].length);
            for (int j = 0; j < val[i].length; j++) {
                join[i][j] = val[i][j];
            }
            for (int j = 0; j < d[i].length; j++) {
                join[i][j + val[i].length] = d[i][j];
            }
        }
        return join;
    }

}