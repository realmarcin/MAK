package mathy;

import util.ParsePath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Version 1.0 1/1/04
 * Author mj
 * <p/>
 * Class representin the SuperMatrix object (matrix of matrices) and family of methods.
 */
public class SuperMatrix {

    public double[][][][] supmat;
    int total;
    public int maxdim;
    public int celldim;

    double[] minmaxpos, minmaxneg, minmaxposnondiag, minmaxnegnondiag, minmaxnondiag, minmaxdiag;


    /**
     *
     */
    public SuperMatrix() {

    }

    /**
     * @param max
     * @param cell
     */
    public SuperMatrix(int max, int cell) {

        maxdim = max;
        celldim = cell;
        supmat = new double[maxdim][maxdim][celldim][celldim];

    }

    /**
     * @param s
     * @return
     */
    private final static String[] testDir(String s) {

        boolean test = false;

        File testdirone = new File(s);

        if (!testdirone.isDirectory()) {
            System.out.println("The specified path\n" + s + "\nis not a directory.");
        } else
            return (testdirone.list());

        return null;
    }

    /**
     * Assumes a square SuperMatrix currently.
     */
    public final static int[] getMatSizefromDir(String d) {

        File dr = new File(d);

        String[] fn = dr.list();
        int count = 0;
        for (int i = 0; i < fn.length; i++) {

            ParsePath p = new ParsePath(d + "/" + fn[i]);
            if (p.getExt().equals("mat"))
                count++;
        }

        int[] ret = {(int) Math.sqrt((double) count), (int) Math.sqrt((double) count)};

        System.out.println("Mat size " + ret[0]);
        return ret;
    }

    /**
     * @param s
     * @param max
     * @param min
     */
    public void loadLowerDiag(String s, int max, int min) {

        if (supmat == null) {

            maxdim = max;
            celldim = min;
            supmat = new double[maxdim][maxdim][celldim][celldim];
        }

        String[] filesone = testDir(s);

        for (int i = 0; i < max; i++) {
            for (int j = i; j < max; j++) {
                for (int a = 0; a < filesone.length; a++) {

//System.out.println(i+"\t"+j+"\t"+a);
                    ParsePath pp = new ParsePath(filesone[a]);
                    String testb = i + "_" + j + "_";

                    if (filesone[a].indexOf(testb) == 0 && pp.getExt().equals("mat")) {

                        Matrix matr = new Matrix();
                        //System.out.println("matrix load "+pathone+"/"+filesone[a]);

                        matr.load(s + "/" + filesone[a]);
                        setIJ(i, j, matr);
                    }
                }
            }
        }

    }

    /**
     * @param s
     * @param max
     * @param min
     */
    public void loadUpperDiag(String s, int max, int min) {

        if (supmat == null) {

            maxdim = max;
            celldim = min;
            supmat = new double[maxdim][maxdim][celldim][celldim];
        }

        testDir(s);
        String[] filesone = testDir(s);

        for (int i = 0; i < max; i++) {
            for (int j = 0; j < i; j++) {
                for (int a = 0; a < filesone.length; a++) {

                    ParsePath pp = new ParsePath(filesone[a]);
                    String testb = i + "_" + j + "_";

                    if (filesone[a].indexOf(testb) == 0 && pp.getExt().equals("mat")) {

                        Matrix matr = new Matrix();
                        //System.out.println("matrix load "+s+"/"+filesone[a]);
                        matr.load(s + "/" + filesone[a]);
                        setIJ(i, j, matr);
                    }
                }
            }
        }

    }

    /**
     * @param d
     */
    public void load(double[][][][] d) {

        supmat = d;
        maxdim = d.length;
        celldim = d[0][0].length;
    }


    /**
     * Loads from a directory of indexed submatrix files.
     *
     * @param s
     * @param max
     * @param min
     */
    public void load(String s, int max, int min) {

        if (supmat == null) {

            maxdim = max;
            celldim = min;
            supmat = new double[maxdim][maxdim][celldim][celldim];
        }

        testDir(s);
        String[] filesone = testDir(s);

        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                for (int a = 0; a < filesone.length; a++) {

//System.out.println(i+"\t"+j+"\t"+a);
                    ParsePath pp = new ParsePath(filesone[a]);
                    String testb = i + "_" + j + "_";

                    if (filesone[a].indexOf(testb) == 0 && pp.getExt().equals("mat")) {

                        Matrix matr = new Matrix();
                        //System.out.println("matrix load "+s+"/"+filesone[a]);

                        matr.load(s + "/" + filesone[a]);
                        setIJ(i, j, matr);
                    }
                }
            }
        }

    }

    /**
     * @param s
     * @param dim
     * @param cells
     */
    public void readFast(String s, int dim, int cells) {

        //System.out.println(s+"\t"+dim+"\t"+cells);

        System.out.println("readFast " + dim + "\t" + cells);

        if (supmat == null) {

            maxdim = dim;
            celldim = cells;
            supmat = new double[maxdim][maxdim][celldim][celldim];
        }


        try {

            BufferedReader in = new BufferedReader(new FileReader(s));

            String line = in.readLine();
            int counteri = 0;
            int counterj = 0;

            while (line != null) {

                String matstr = "";

                //System.out.println("readFast line "+line+"\n"+matstr);

                if (line.indexOf("#") == 0)
                    while (line.indexOf("#") == 0)
                        line = in.readLine();

                while (line.length() > 0 && line.indexOf("#") != 0) {

                    matstr += line + "\n";
                    line = in.readLine();

                    if (line == null)
                        break;
                }

                /*
                if (line.indexOf("#") != 0)
                    while (line.indexOf("#") != 0)
                        line = in.readLine();
                */

                if (matstr.length() == 0)
                    break;

                //System.out.println("matstr "+matstr);
                Matrix curmat = new Matrix(matstr, true);

                //System.out.println("setting "+counteri +"\t"+ counterj );
                setIJ(counteri, counterj, curmat);

                //System.out.println("reading "+counteri +"\t"+ counterj );
                //Matrix.print(getIJ(counteri,counterj).retDoubleMat());

                counteri++;
                if (counteri == dim) {
                    counterj++;
                    counteri = 0;
                }

                if (line == null)
                    break;
                line = in.readLine();
                if (line == null)
                    break;
            }
        } catch (IOException e) {
        }

    }


    /**
     * @param s
     */
    public void readSlow(String s) {

        supmat = null;

        int bigx = 0;
        int smallx = 0;

        try {

            BufferedReader in = new BufferedReader(new FileReader(s));

            String line = in.readLine();

            int first = line.indexOf("big dim ");
            bigx = Integer.parseInt(line.substring(first + 8, line.indexOf("^", first + 1)));
            int second = line.indexOf("small dim ");
            smallx = Integer.parseInt(line.substring(second + 10, line.indexOf("^", second + 1)));

        } catch (IOException e) {
        }


        readFast(s, (int) bigx, smallx);

    }

    /**
     * Writes a SuperMatrix object to a single file.
     *
     * @param s
     */
    public void writeSM(String s) {

        ParsePath pp = new ParsePath(s);
        if (!pp.getExt().equals("supmat"))
            s += ".supmat";

        if (supmat != null) {

            //testDir(s);
            String data = "";

            data += "#SuperMatrix big dim " + supmat.length + "^2\tsmall dim " + supmat[0][0][0].length + "^2\n";

            for (int i = 0; i < supmat.length; i++) {
                for (int j = 0; j < supmat.length; j++) {

                    Matrix matr = new Matrix(supmat[i][j]);
                    data += "#SuperMatrix\t" + i + "\t" + j + "\n";
                    data += matr.toString(-1, -1) + "\n";

                }
            }
            util.TextFile.write(data, s);
        }

    }


    /**
     * Set the i,j Matrix in the SuperMatrix.
     */
    public void setIJ(int i, int j, Matrix k) {


        supmat[i][j] = (double[][]) k.mat;

        //System.out.println("setIJ ");
        //Matrix.print(supmat[i][j]);
    }

    /**
     * Return the i,j Matrix in the SuperMatrix.
     */
    public Matrix getIJ(int i, int j) {

        double[][] temp = supmat[i][j];
        //System.out.println("getIJ [0][0] "+temp[0][0]);
        Matrix ret = new Matrix(temp);
        return ret;
    }


    /**
     * Returns the default SuperMatrix 4 dimensional double array.
     */
    public double[][][][] retDoubleSupMat() {

        return supmat;
    }

    /**
     * Returns the SuperMatrix in 2 dimensional form.
     */
    public double[][] retDoubleMat() {

//System.out.println("SM maxdim "+maxdim+"\tcelldim "+celldim);

        double[][] mat = new double[maxdim * celldim][maxdim * celldim];

        int adim = 0;
        int bdim = 0;

        for (int i = 0; i < maxdim; i++) {
            for (int j = 0; j < maxdim; j++) {

                double[][] cur = supmat[i][j];

                for (int ia = 0; ia < celldim; ia++) {
                    for (int ja = 0; ja < celldim; ja++) {

                        mat[i * celldim + ia][j * celldim + ja] = cur[ia][ja];
                    }
                }
            }
        }

        return mat;
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public double[] findMinMaxPos(double[][][][] m) {

        load(m);
        return (findMinMaxPos());
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public double[] findMinMaxNeg(double[][][][] m) {

        load(m);
        return (findMinMaxNeg());
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public double[] findMinMaxPosNonDiag(double[][][][] m) {

        load(m);
        findMinMaxPosNonDiag();

        return (findMinMaxPosNonDiag());
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public double[] findMinMaxNegNonDiag(double[][][][] m) {

        load(m);

        return (findMinMaxNegNonDiag());
    }

    /**
     * Finds the min/max of the nondiagonal entries.
     */
    public double[] findMinMaxNonDiag(double[][][][] m) {

        load(m);

        return (findMinMaxNonDiag());
    }

    /**
     * Finds the min/max of the diagonal matrices.
     */
    public double[] findMinMaxDiag(double[][][][] m) {

        load(m);
        findMinMaxDiag();

        return (findMinMaxDiag());
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public final static double[] findMinMaxPosS(double[][][][] m) {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < m.length; j++) {
            for (int k = 0; k < m[0].length; k++) {
                for (int a = 0; a < m[0][0].length; a++) {
                    for (int b = 0; b < m[0][0][0].length; b++) {
                        if (m[j][k][a][b] >= 0) {
                            if (m[j][k][a][b] > gma)
                                gma = m[j][k][a][b];
                            if (m[j][k][a][b] < gmi)
                                gmi = m[j][k][a][b];
                        }
                    }
                }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public final static double[] findMinMaxNegS(double[][][][] m) {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < m.length; j++) {
            for (int k = 0; k < m[0].length; k++) {
                for (int a = 0; a < m[0][0].length; a++) {
                    for (int b = 0; b < m[0][0][0].length; b++) {

                        if (m[j][k][a][b] < 0) {

                            if (m[j][k][a][b] > gma)
                                gma = m[j][k][a][b];
                            if (m[j][k][a][b] < gmi)
                                gmi = m[j][k][a][b];
                        }
                    }
                }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public final static double[] findMinMaxPosNonDiagS(double[][][][] m) {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < m.length; j++) {
            for (int k = 0; k < m[0].length; k++) {

                if (j != k)
                    for (int a = 0; a < m[0][0].length; a++) {
                        for (int b = 0; b < m[0][0][0].length; b++) {

                            if (m[j][k][a][b] >= 0) {

                                if (m[j][k][a][b] > gma)
                                    gma = m[j][k][a][b];
                                if (m[j][k][a][b] < gmi)
                                    gmi = m[j][k][a][b];
                            }
                        }
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the < 0 entries.
     */
    public final static double[] findMinMaxNegNonDiagS(double[][][][] m) {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < m.length; j++) {

            for (int k = 0; k < m.length; k++) {

                if (j != k)
                    for (int a = 0; a < m[0][0].length; a++) {

                        for (int b = 0; b < m[0][0].length; b++) {

                            if (m[j][k][a][b] < 0) {
                                if (m[j][k][a][b] > gma)
                                    gma = m[j][k][a][b];
                                if (m[j][k][a][b] < gmi)
                                    gmi = m[j][k][a][b];
                            }
                        }
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the nondiagonal entries.
     */
    public final static double[] findMinMaxNonDiagLowerS(double[][][][] m) {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < m.length; j++) {

            for (int k = j + 1; k < m.length; k++) {

                if (j != k)
                    for (int a = 0; a < m[0][0].length; a++) {

                        for (int b = 0; b < m[0][0].length; b++) {

                            if (m[j][k][a][b] > gma)
                                gma = m[j][k][a][b];
                            if (m[j][k][a][b] < gmi)
                                gmi = m[j][k][a][b];

                        }
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the nondiagonal entries.
     */
    public final static double[] findMinMaxNonDiagUpperS(double[][][][] m) {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < m.length; j++) {

            for (int k = 0; k < j; k++) {

                if (j != k)
                    for (int a = 0; a < m[0][0].length; a++) {

                        for (int b = 0; b < m[0][0].length; b++) {

                            if (m[j][k][a][b] > gma)
                                gma = m[j][k][a][b];
                            if (m[j][k][a][b] < gmi)
                                gmi = m[j][k][a][b];
                        }
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the nondiagonal entries.
     */
    public final static double[] findMinMaxNonDiagS(double[][][][] m) {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < m.length; j++) {
            for (int k = 0; k < m[0].length; k++) {

                if (j != k)
                    for (int a = 0; a < m[0][0].length; a++) {
                        for (int b = 0; b < m[0][0].length; b++) {

                            if (m[j][k][a][b] > gma)
                                gma = m[j][k][a][b];
                            if (m[j][k][a][b] < gmi)
                                gmi = m[j][k][a][b];
                        }
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the diagonal matrices.
     */
    public final static double[] findMinMaxDiagS(double[][][][] m) {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < m.length; j++) {
            for (int k = 0; k < m[0].length; k++) {
                if (j == k)
                    for (int a = 0; a < m[0][0].length; a++) {
                        if (m[j][k][a][a] > gma)
                            gma = m[j][k][a][a];
                        if (m[j][k][a][a] < gmi)
                            gmi = m[j][k][a][a];
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /****/

    /**
     * Finds the min/max of the > 0 entries.
     */
    public double[] findMinMaxPos() {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < supmat.length; j++) {
            for (int k = 0; k < supmat[0].length; k++) {
                for (int a = 0; a < supmat[0][0].length; a++) {
                    for (int b = 0; b < supmat[0][0][0].length; b++) {

                        if (supmat[j][k][a][b] >= 0) {
                            if (supmat[j][k][a][b] > gma)
                                gma = supmat[j][k][a][b];
                            if (supmat[j][k][a][b] < gmi)
                                gmi = supmat[j][k][a][b];
                        }
                    }
                }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the < 0 entries.
     */
    public double[] findMinMaxNeg() {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;


        for (int j = 0; j < supmat.length; j++) {
            for (int k = 0; k < supmat[0].length; k++) {
                for (int a = 0; a < supmat[0][0].length; a++) {
                    for (int b = 0; b < supmat[0][0][0].length; b++) {

                        if (supmat[j][k][a][b] < 0) {

                            if (supmat[j][k][a][b] > gma)
                                gma = supmat[j][k][a][b];
                            if (supmat[j][k][a][b] < gmi)
                                gmi = supmat[j][k][a][b];
                        }
                    }
                }
            }
        }
        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public double[] findMinMaxPosNonDiag() {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;


        for (int j = 0; j < supmat.length; j++) {
            for (int k = 0; k < supmat[0].length; k++) {
                if (j != k)
                    for (int a = 0; a < supmat[0][0].length; a++) {
                        for (int b = 0; b < supmat[0][0][0].length; b++) {

                            if (supmat[j][k][a][b] >= 0) {
                                if (supmat[j][k][a][b] > gma)
                                    gma = supmat[j][k][a][b];
                                if (supmat[j][k][a][b] < gmi)
                                    gmi = supmat[j][k][a][b];
                            }
                        }
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Scalar multiplication of matrix, except diagonal.
     */
    public final static double[][][][] multNoDiag(double[][][][] a, double b) {

        double[][][][] ret = new double[a.length][a[0].length][a[0][0].length][a[0][0][0].length];

        for (int j = 0; j < a.length; j++)
            for (int k = 0; k < a[0].length; k++) {

                if (j != k) {
                    for (int m = 0; m < a[0][0].length; m++)
                        for (int n = 0; n < a[0][0][0].length; n++)
                            ret[j][k][m][n] = a[j][k][m][n] * b;
                } else if (j == k) {
                    for (int m = 0; m < a[0][0].length; m++)
                        for (int n = 0; n < a[0][0][0].length; n++)
                            ret[j][k][m][n] = a[j][k][m][n];
                }
            }

        return ret;
    }

    /**
     * Take the log base e of all entries. Assumes a square matrix of square matrices.
     */
    public final static double[][][][] lnNoDiag(double[][][][] a) {

        double[][][][] ret = new double[a.length][a[0].length][a[0][0].length][a[0][0][0].length];

        for (int j = 0; j < a.length; j++) {
            for (int k = 0; k < a[0].length; k++) {
                if (j != k) {
                    for (int m = 0; m < a[0][0].length; m++)
                        for (int n = 0; n < a[0][0][0].length; n++)
                            ret[j][k][m][n] = Math.log(a[j][k][m][n]);

                } else if (j == k) {
                    for (int m = 0; m < a[0][0].length; m++)
                        for (int n = 0; n < a[0][0][0].length; n++)
                            ret[j][k][m][n] = a[j][k][m][n];
                }
            }
        }
        return ret;
    }

    /**
     * Take the log base e of all entries. Assumes a square matrix of square matrices.
     */
    public final static double[][][][] logNNoDiag(double[][][][] a, double base) {

        double[][][][] ret = new double[a.length][a[0].length][a[0][0].length][a[0][0][0].length];

        for (int j = 0; j < a.length; j++) {
            for (int k = 0; k < a[0].length; k++) {
                if (j != k) {
                    for (int m = 0; m < a[0][0].length; m++)
                        for (int n = 0; n < a[0][0][0].length; n++)
                            ret[j][k][m][n] = Math.log(a[j][k][m][n]) / Math.log(base);

                } else if (j == k) {
                    for (int m = 0; m < a[0][0].length; m++)
                        for (int n = 0; n < a[0][0][0].length; n++)
                            ret[j][k][m][n] = a[j][k][m][n];
                }
            }
        }
        return ret;
    }

    /**
     * Finds the min/max of the > 0 entries.
     */
    public double[] findMinMaxNegNonDiag() {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < supmat.length; j++) {
            for (int k = 0; k < supmat[0].length; k++) {
                if (j != k)
                    for (int a = 0; a < supmat[0][0].length; a++) {
                        for (int b = 0; b < supmat[0][0][0].length; b++) {

                            if (supmat[j][k][a][b] < 0) {

                                if (supmat[j][k][a][b] > gma)
                                    gma = supmat[j][k][a][b];
                                if (supmat[j][k][a][b] < gmi)
                                    gmi = supmat[j][k][a][b];
                            }
                        }
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the nondiagonal entries.
     */
    public double[] findMinMaxNonDiag() {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < supmat.length; j++) {
            for (int k = 0; k < supmat[0].length; k++) {
                if (j != k)
                    for (int a = 0; a < supmat[0][0].length; a++) {
                        for (int b = 0; b < supmat[0][0][0].length; b++) {

                            if (supmat[j][k][a][b] > gma)
                                gma = supmat[j][k][a][b];
                            if (supmat[j][k][a][b] < gmi)
                                gmi = supmat[j][k][a][b];
                        }
                    }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Finds the min/max of the diagonal matrices. This method look only at the diagonsl of
     * diagonal supermatrix cells.
     */
    public double[] findMinMaxDiag() {

        double gmi = Double.POSITIVE_INFINITY;
        double gma = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < supmat.length; j++) {
            for (int k = 0; k < supmat[0].length; k++) {
                if (j == k) {
                    for (int a = 0; a < supmat[0][0].length; a++) {

                        if (supmat[j][k][a][a] > gma)
                            gma = supmat[j][k][a][a];
                        if (supmat[j][k][a][a] < gmi)
                            gmi = supmat[j][k][a][a];
                        /*
                        for(int b=0;b<supmat[0][0][0].length;b++) {

                            if(supmat[j][k][a][b] > gma)
                                gma = supmat[j][k][a][b];
                            if(supmat[j][k][a][b] < gmi)
                                gmi = supmat[j][k][a][b];
                        }
                        */
                    }
                }
            }
        }

        double[] ret = new double[2];
        ret[0] = gmi;
        ret[1] = gma;

        return ret;
    }

    /**
     * Multiples the diagonal of the diagonal matrices by a scalar.
     *
     * @param scalar
     */
    public void multDiagDiag(double scalar) {

        for (int j = 0; j < supmat.length; j++) {
            for (int k = 0; k < supmat[0].length; k++) {
                if (j == k) {
                    for (int a = 0; a < supmat[0][0].length; a++) {

                        supmat[j][k][a][a] *= scalar;
                    }
                }
            }
        }
    }

    /**
     * Takes the log base 2 of the diagonal of the diagonal matrices.
     */

    public void logoddsDiagDiag() {
        for (int j = 0; j < supmat.length; j++) {
            for (int k = 0; k < supmat[0].length; k++) {
                if (j == k) {
                    for (int a = 0; a < supmat[0][0].length; a++) {
                        supmat[j][k][a][a] = Math.log(supmat[j][k][a][a]);
                        supmat[j][k][a][a] *= 1.0 / Math.log(2);
                    }
                }
            }
        }
    }

    /**
     * Loads a lower and upper diagonal matrix, adding an additional data-null diagonal in order to represent diagonals from low and up.
     */
    public void setUpLowExtrDiag(SuperMatrix low, SuperMatrix up) {

        maxdim = low.maxdim + 1;
        supmat = new double[maxdim][maxdim][celldim][celldim];

        for (int i = 0; i < low.maxdim; i++) {
            for (int j = i; j < low.maxdim; j++) {

                Matrix cur = up.getIJ(i, j);
                setIJ(i, j + 1, cur);
            }
        }

        for (int i = 0; i < low.maxdim; i++) {
            for (int j = 0; j <= i; j++) {

                Matrix cur = low.getIJ(i, j);
                setIJ(i + 1, j, cur);
            }
        }
    }


    /**
     * @param a
     * @return
     */
    public final static ArrayList calcStats(ArrayList a) {

        ArrayList ret = new ArrayList();

        double[][] first = ((SuperMatrix) a.get(0)).retDoubleMat();
        int len = first.length;
        double[][] avg = new double[len][len];
        double[][] sd = new double[len][len];

        for (int i = 0; i < a.size(); i++) {

            double[][] add = ((SuperMatrix) a.get(i)).retDoubleMat();
            avg = Matrix.add(avg, add);
        }

        avg = Matrix.norm((double) a.size(), avg);
        ret.add((double[][]) avg);


        for (int i = 0; i < a.size(); i++) {

            double[][] cur = ((SuperMatrix) a.get(i)).retDoubleMat();
            double[][] add = Matrix.diff(cur, avg);
            sd = Matrix.add(sd, Matrix.multiply(add, add));
        }

        sd = Matrix.sqrt(sd);
        ret.add((double[][]) sd);

        return ret;

    }

}