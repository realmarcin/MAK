package mathy;

import dtype.Data1;
import dtype.IntDouble;
import util.MoreArray;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Collection of math and stat methods.
 */
public class stat {

    public double trueplus = 0, trueminus = 0, falseplus = 0, falseminus = 0;
    public double[] dat;
    public double sn = 0;
    public double mathew = 0;

    /**
     * Takes quadratic equation ax^2+bx+c and solves for roots
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public final static double[] solveQuadratic(double a, double b, double c) {
        double[] ret = new double[2];
        double B2 = Math.pow(b, 2);
        ret[0] = (-b + Math.sqrt(B2 - 4.0 * a * c)) / (2.0 * a);
        ret[1] = (-b - Math.sqrt(B2 - 4.0 * a * c)) / (2.0 * a);
        return ret;
    }


    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public final static double solveLinearSlope(double x1, double y1, double x2, double y2) {
        double ret = Double.NaN;
        double a = (y2 - y1) / (x2 - x1);
        return ret;
    }

    /**
     * @param sd
     * @param n
     * @return
     */
    public final double standardError(double sd, double n) {
        return Math.sqrt(n) * sd;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public static double vSDData1(ArrayList x, double mea) {
        double sum = 0;
        double el = 0;
        int i = 0;
        while (i < x.size()) {
            el = ((Data1) x.get(i)).x;
            sum += Math.pow((el - mea), 2);
            i++;
        }
        double sd = Math.sqrt(sum / (double) x.size());
        return sd;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public static double vSD(ArrayList x, double mea) {
        double sum = 0;
        double el = 0;
        int i = 0;
        while (i < x.size()) {
            el = ((Double) x.get(i)).doubleValue();
            sum += Math.pow((el - mea), 2);
            i++;
        }
        double sd = Double.NaN;
        if (x.size() > 1)
            sd = (float) Math.sqrt(sum / (float) (x.size() - 1.0));
        return sd;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public final float vSD(ArrayList x, float mea) {
        float sum = 0;
        float el = 0;
        int i = 0;
        while (i < x.size()) {
            el = (float) ((Data1) x.get(i)).x;
            sum += Math.pow((el - mea), 2);
            i++;
        }
        float sd = Float.NaN;
        if (x.size() > 1)
            sd = (float) Math.sqrt(sum / (float) (x.size() - 1.0));
        return sd;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public final float ArraySD(float[][] x, float mea) {
        float sum = 0;
        float count = 0;
        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < x[0].length; j++) {
                sum += Math.pow((x[i][j] - mea), 2);
                count++;
            }
        float sda = Float.NaN;
        if (count > 1)
            sda = (float) Math.sqrt(sum / (float) (count - 1.0));
        return sda;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public final static double ArraySD(double[][] x, double mea) {
        double sum = 0;
        double count = 0;
        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < x[0].length; j++) {
                sum += Math.pow((x[i][j] - mea), 2);
                count++;
            }
        double ret = Double.NaN;
        if (count > 1)
            ret = Math.sqrt(sum / (count - 1.0));
        return ret;
    }

    /**
     * @param x
     * @param mean
     * @return
     */
    public final double ArraySD(int[][] x, double mean) {
        double sum = 0;
        double count = 0;
        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < x[0].length; j++) {
                sum += Math.pow(((double) x[i][j] - mean), 2);
                count++;
            }
        double ret = Double.NaN;
        if (count > 1)
            ret = Math.sqrt(sum / (count - 1.0));
        return ret;
    }

    /**
     * @param x
     * @param mean
     * @return
     */
    public final double symArraySD(double[][] x, double mean) {
        double sum = 0;
        double count = 0;
        for (int i = 0; i < x.length; i++)
            for (int j = i; j < x[0].length; j++) {
                sum += Math.pow((x[i][j] - mean), 2);
                count++;
            }
        double ret = Double.NaN;
        if (count > 1)
            ret = Math.sqrt(sum / (count - 1.0));
        return ret;
    }

    /**
     * Calculates the average matrix value for a symmetrix matrix.
     *
     * @param a    min range
     * @param b    max range
     * @param data
     * @return average value
     */
    public final double avgSymMatrixValforRange(int a, int b, double[][] data) {
        //	System.out.println(a+"\t"+b+"\tdata.lengths "+data.length+"\t"+data[0].length);
        double ret = 0;
        double count = 0;
        for (int i = a; i < b + 1; i++) {
            for (int j = a + 1; j < b + 1; j++) {
                //System.out.println(ret+"\tadded to "+data[i][j]);
                ret += 100.0 * data[i][j];
                count++;
            }
        }
        ret = ret / count;
//System.out.println("Element count "+count+".\tAverage "+ret);
        return ret;
    }

    /**
     * @param x
     * @param mean
     * @return
     */
    public final double symArrayNonZeroSD(double[][] x, double mean) {
        double sum = 0;
        double count = 0;
        for (int i = 0; i < x.length; i++)
            for (int j = i; j < x[0].length; j++) {
                if (x[i][j] > 0) {
                    sum += Math.pow((x[i][j] - mean), 2);
                    count++;
                }
            }
        double sda = Double.NaN;
        if (count > 1)
            sda = Math.sqrt(sum / (count - 1.0));
        return sda;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public static double SD(double[] x, double mea) {
        return SDOverSample(x, mea, -1);
    }


    /**
     * @param x
     * @param mean
     * @return
     */
    public static double SDOverSample(double[] x, double mean, int sample) {
        double sum = 0;
        int k = sample;
        if (sample <= 0)
            k = 0;
        for (int i = 0; i < x.length; i++) {
            if (!Double.isNaN(x[i])) {
                sum += Math.pow(x[i] - mean, 2);// * (x[i] - mean);
                if (sample <= 0)
                    k++;
            }
        }
        double s = Double.NaN;
        //System.out.println("SDOverSample "+sum+"\t"+k);
        if (k > 1)
            s = Math.sqrt(sum / (double) (k - 1));
        return s;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public static float SD(float[] x, float mea) {
        return SDoverSample(x, mea);
    }


    /**
     * @param x
     * @param mea
     * @return
     */
    public static float SDoverSample(float[] x, float mea) {
        float sum = 0;
        float sample = 0;
        for (int i = 0; i < x.length; i++) {
            if (!Double.isNaN(x[i])) {
                sum += Math.pow((x[i] - mea), 2);
                sample++;
            }
        }
        float s = Float.NaN;
        if (sample > 1)
            s = (float) Math.sqrt(sum / (sample - 1));
        return s;
    }


    /**
     * @param x
     * @param mean
     * @return
     */
    public final double nonMinusOneSD(double[] x, double mean) {
        double count = 0;
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] != -1) {
                sum += Math.pow((x[i] - mean), 2);
                count++;
            }
        }
        double sda = Double.NaN;
        if (count > 1)
            sda = Math.sqrt(sum / (count - 1.0));
        return sda;
    }

    /**
     * @param x
     * @param mean
     * @return
     */
    public static float nonMinusOneSD(float[] x, float mean) {
        float count = 0;
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] != -1) {
                sum += Math.pow((x[i] - mean), 2);
                count++;
            }
        }
        float sd = Float.NaN;
        if (count > 1)
            sd = (float) Math.sqrt(sum / (count - 1.0));
        return sd;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public static double nonZeroSD(double[] x, double mea) {
        int count = 0;
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] != 0) {
                sum += Math.pow((x[i] - mea), 2);
                count++;
            }
        }
        double sda = Double.NaN;
        if (count > 1)
            sda = Math.sqrt(sum / (double) (count - 1.0));
        return sda;
    }

    /**
     * @param x
     * @param mea
     * @return
     */
    public static double sSD(double x, double mea) {
        return Math.sqrt(x / mea);
    }

    /**
     * @param a
     * @return
     */
    public static double vavgData1(ArrayList a) {
        double sum = 0;
        int i = 0;
        for (i = 0; i < a.size(); i++) {
            sum += ((Data1) a.get(i)).x;
            i++;
        }
        return (sum / (double) a.size());
    }

    /**
     * @param a
     * @return
     */
    public static double vavg(ArrayList a) {
        double sum = 0;
        int i = 0;
        for (i = 0; i < a.size(); i++) {
            sum += ((Double) a.get(i)).doubleValue();
            i++;
        }
        return (sum / (double) a.size());
    }

    /**
     * @param a
     * @return
     */
    public static float vavgfloat(ArrayList a) {
        float sum = 0;
        int i = 0;
        for (i = 0; i < a.size(); i++) {
            sum += (float) ((Data1) a.get(i)).x;
            i++;
        }
        return (sum / (float) a.size());
    }

    /**
     * @param a
     * @return
     */
    public static double avg(double[] a) {
        return avgOverSamp(a, -1);
    }

    /**
     * @param a
     * @return
     */
    public static double[][] histOverSamp(double[] a) {
        ArrayList tmp = new ArrayList();
        for (int i = 0; i < a.length; i++) {
            Double newd = new Double(a[i]);
            if (tmp.indexOf(newd) == -1) {
                tmp.add(newd);
            }
        }
        double[] vals = MoreArray.ArrayListtoDouble(tmp);
        Arrays.sort(vals);
        double[][] ret = new double[tmp.size()][2];
        for (int i = 0; i < tmp.size(); i++) {
            double cur = vals[i];
            ret[i][0] = cur;
            ret[i][1] = stat.countOccurence(a, cur);

        }
        return ret;
    }


    /**
     * @param a
     * @return
     */
    public static double avgOverSamp(double[] a, int sample) {
        double sum = 0;
        int k = sample;
        if (sample == -1)
            k = 0;
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i])) {
                sum += a[i];
                //if (sample != -1)/*Bug that would artificially inflate the sample count*/
                if (sample == -1)
                    k++;
            }
        }
        //System.out.println("stat.avg " + a.length + "\t" + k);
        double val = Double.NaN;
        if (k > 0)
            val = sum / (double) k;
        return val;
    }

    /**
     * @param a
     * @return
     */
    public static double avgABSOverSamp(double[] a, int sample) {
        double sum = 0;
        int k = sample;
        if (sample == -1)
            k = 0;
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i])) {
                sum += Math.abs(a[i]);
                //if (sample != -1)/*Bug that would artificially inflate the sample count*/
                if (sample == -1)
                    k++;
            }
        }
        //System.out.println("stat.avgABSOverSamp raw " + a.length + "\t" + k+"\t"+sample+"\t"+sum);
        double val = Double.NaN;
        if (k > 0)
            val = sum / (double) k;
        //System.out.println("stat.avgABSOverSamp avg " + a.length + "\t" + k+"\t"+sample+"\t"+val);
        return val;
    }

    /**
     * @param a
     * @param index
     * @return
     */
    public static double avgOverSamp(double[] a, int[] index) {
        double sum = 0;
        int k = 0;
        for (int i = 0; i < a.length; i++) {
            if (index[i] != 0)
                if (!Double.isNaN(a[i])) {
                    sum += a[i];
                    k++;
                }
        }
        //System.out.println("stat.avg " + a.length + "\t" + k);
        double val = Double.NaN;
        if (k > 0)
            val = sum / (double) k;
        return val;
    }

    /**
     * @param a
     * @return
     */
    public static float avg(float[] a) {
        float sum = 0;
        float k = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != Float.NaN) {
                sum += a[i];
                k++;
            }
        }
        float ret = Float.NaN;
        if (k > 0)
            ret = sum / k;
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static double nonMinusOneavg(double[] a) {
        double sum = 0;
        double k = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != -1) {
                sum += a[i];
                k++;
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
    public static float nonMinusOneavg(float[] a) {
        float sum = 0;
        float k = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != -1) {
                sum += a[i];
                k++;
            }
        }
        float ret = Float.NaN;
        if (k > 0)
            ret = sum / k;
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static double nonZeroavg(double[] a) {
        double sum = 0;
        double k = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0) {
                sum += a[i];
                k++;
            }
        }
        double ret = Double.NaN;
        if (k > 0)
            ret = sum / (double) k;
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static double avg(int[] a) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += (double) a[i];
        }
        double mean = (sum / (double) a.length);
        return mean;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double avg(double a, double b) {
        double mea = (a + b) / 2.0;
        return mea;
    }


    /**
     * Calculates the average of a sample.
     *
     * @param a
     * @return
     */
    public static double doubleSampAvg(ArrayList a) {
        double m = 0;
        for (int i = 0; i < a.size(); i++)
            m += ((Double) a.get(i)).doubleValue();
        m /= (double) a.size();
        return m;
    }

    /**
     * Calculates the average of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] arraySampAvg(ArrayList a) {
        double[] first = (double[]) a.get(0);
        double[] sampsize = new double[first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    m[j] += cur[j];
                    sampsize[j]++;
                }
            }
        }
        m = norm(m, sampsize);
        return m;
    }

    /**
     * Calculates the average of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] arraySampMax(ArrayList a) {
        double[] first = (double[]) a.get(0);
        double[] sampsize = new double[first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            m[i] = stat.findMax(cur);
        }
        return m;
    }


    /**
     * Calculates the median of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @param count
     * @return
     */
    public static int[] arraySampCount(ArrayList a, int count) {
        double[] first = (double[]) a.get(0);
        int[] m = new int[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (cur[j] == count) {
                    m[j]++;
                }
            }
        }
        return m;
    }

    /**
     * Calculates the median of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] arraySampMedian(ArrayList a) {
        double[] first = (double[]) a.get(0);
        double[][] data = new double[a.size()][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            //System.out.println("median ");
            //MoreArray.printArray(data);
            m[i] = median(d);
            //System.out.println("median " + m[i]);
        }
        return m;
    }

    /**
     * Calculates the median of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] arraySampMean(ArrayList a) {
        double[] first = (double[]) a.get(0);
        double[][] data = new double[a.size()][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            //System.out.println("median ");
            //MoreArray.printArray(data);
            m[i] = avg(d);
            //System.out.println("median " + m[i]);
        }
        return m;
    }

    /**
     * Calculates the median of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] arraySampFreq(ArrayList a) {
        double[] first = (double[]) a.get(0);
        double size = a.size();
        double[][] data = new double[(int) size][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < size; i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            //System.out.println("median ");
            //MoreArray.printArray(data);
            m[i] = countGreaterThan(d, 0);
            m[i] /= size;
            //System.out.println("median " + m[i]);
        }
        return m;
    }

    /**
     * Calculates the median of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] findColumnMedian(ArrayList a) {
        double[] first = (double[]) a.get(0);
        double[][] data = new double[a.size()][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            //System.out.println("median ");
            //MoreArray.printArray(data);
            m[i] = median(d);
            //System.out.println("median " + m[i]);
        }
        return m;
    }

    /**
     * Calculates the median of a sample for an ArrayList storing rows of data.
     *
     * @param data
     * @return
     */
    public static double[] findColumnMedian(double[][] data) {
        double[] m = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            //System.out.println("median ");
            //MoreArray.printArray(data);
            m[i] = median(d);
            //System.out.println("median " + m[i]);
        }
        return m;
    }

    /**
     * Calculates the sd of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] findColumnSD(ArrayList a) {
        double[] first = (double[]) a.get(0);
        double[][] data = new double[a.size()][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            double mean = avg(d);
            m[i] = SD(d, mean);
        }
        return m;
    }

    /**
     * Calculates the sd of a sample for an ArrayList storing rows of data.
     *
     * @param data
     * @return
     */
    public static double[] findColumnSD(double[][] data) {
        double[] m = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            double mean = avg(d);
            m[i] = SD(d, mean);
        }
        return m;
    }

    /**
     * Calculates the count above a threshold for an ArrayList storing rows of data.
     *
     * @param a
     * @param cut
     * @return
     */
    public static double[] findColumnCountGreaterThan(ArrayList a, double cut) {//, String[] labels, String label,
        //double[] agree, double[] median, double[] sd) {
        double[] first = (double[]) a.get(0);
        double[][] data = new double[a.size()][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            m[i] = countGreaterThan(d, cut);
            /*if (m[i] == 1) {
                System.out.println("findColumnCountGreaterThan " + i + "\tDVU" + label + "\t" + labels[i]
                        + "\t" + agree[i] + "\t" + median[i] + "\t" + sd[i]);
                MoreArray.printArray(d);
            }*/
        }
        return m;
    }

    /**
     * @param data
     * @param cut
     * @return
     */
    public static double[] findColumnCountGreaterThan(double[][] data, double cut) {//, String[] labels, String label,
        //double[] agree, double[] median, double[] sd) {
        double[] m = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            m[i] = countGreaterThan(d, cut);
            /*if (m[i] == 1) {
                System.out.println("findColumnCountGreaterThan " + i + "\tDVU" + label + "\t" + labels[i]
                        + "\t" + agree[i] + "\t" + median[i] + "\t" + sd[i]);
                MoreArray.printArray(d);
            }*/
        }
        return m;
    }

    /**
     * Calculates the max of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] findColumnMax(ArrayList a) {
        double[] first = (double[]) a.get(0);
        double[][] data = new double[a.size()][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            //System.out.println("median ");
            //MoreArray.printArray(data);
            m[i] = findMax(d);
            //System.out.println("median " + m[i]);
        }
        return m;
    }

    /**
     * Calculates the agreement of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] findColumnAgree(ArrayList a, double cutoff) {
        double[] first = (double[]) a.get(0);
        double[][] data = new double[a.size()][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            int nonz = countGreaterThan(d, cutoff);
            m[i] = (double) nonz / (double) d.length;
            //System.out.println("median " + m[i]);
            /*System.out.println("findColumnAgree "+nonz+"\t"+d.length+"\t"+m[i] );
            MoreArray.printArray(d);*/
        }
        return m;
    }

    /**
     * Calculates the agreement of a sample for an ArrayList storing rows of data.
     *
     * @param data
     * @param cutoff
     * @return
     */
    public static double[] findColumnAgree(double[][] data, double cutoff) {
        double[] m = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            int nonz = countGreaterThan(d, cutoff);
            m[i] = (double) nonz / (double) d.length;
            //System.out.println("median " + m[i]);
            /*System.out.println("findColumnAgree "+nonz+"\t"+d.length+"\t"+m[i] );
            MoreArray.printArray(d);*/
        }
        return m;
    }

    /**
     * Calculates the median of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static double[] arraySampMedian(ArrayList a, boolean[] exclude) {
        double[] first = (double[]) a.get(0);
        int excludecount = 0;
        for (int i = 0; i < exclude.length; i++) {
            if (exclude[i]) {
                excludecount++;
            }
        }
        ArrayList store = new ArrayList();
        //double[][] data = new double[a.size() - excludecount][first.length];
        double[] m = new double[first.length];
        for (int i = 0; i < a.size(); i++) {
            if (!exclude[i]) {
                double[] cur = (double[]) a.get(i);
                double[] data = new double[first.length];
                store.add(cur);
                /*for (int j = 0; j < cur.length; j++) {
                    if (!exclude[j]) {
                        if (!Double.isNaN(cur[j])) {
                            data[i][j] = cur[j];
                        }
                    }
                }*/
            }
        }
        double[][] data = MoreArray.ArrayListto2DDouble(store);
        for (int i = 0; i < first.length; i++) {
            double[] d = Matrix.extractColumn(data, i + 1);
            //System.out.println("median ");
            //MoreArray.printArray(data);
            m[i] = median(d);
            //System.out.println("median " + m[i]);
        }
        return m;
    }

    /**
     * @param array
     * @param threshold
     * @param replace
     * @return
     */
    public static double[] filterAboveEqual(double[] array, double threshold, double replace) {
        return filterAboveEqual(array, threshold, replace, false);
    }

    /**
     * @param array
     * @param threshold
     * @param replace
     * @param debug
     * @return
     */
    public static double[] filterAboveEqual(double[] array, double threshold, double replace, boolean debug) {
        double[] ret = new double[array.length];
        for (int i = 0; i < ret.length; i++) {
            if (array[i] >= threshold) {
                if (debug) {
                    System.out.println("filterAboveEqual replacing " + array[i] + "\twith\t" + replace);
                }
                ret[i] = replace;
            } else
                ret[i] = array[i];
        }
        return ret;
    }

    /**
     * Calculates the mean pairwise agreement of a sample for an ArrayList storing rows of data.
     *
     * @param a
     * @return
     */
    public static ArrayList arrayPairAgree(ArrayList a, double cutoff, ArrayList files) {
        double[] first = (double[]) a.get(0);
        double[][] data = new double[a.size()][first.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                if (!Double.isNaN(cur[j])) {
                    data[i][j] = cur[j];
                }
            }
        }
        boolean[] dontuse = new boolean[a.size()];
        dontuse = countPairDisagree(a, cutoff, data, files, dontuse);
        int include = 0;
        for (int i = 0; i < a.size(); i++) {
            if (!dontuse[i]) {
                include++;
                String file = (String) files.get(i);
                //System.out.println("arrayPairAgree including " + file);
            }
        }
        //System.out.println("arrayPairAgree total included " + include);

        ArrayList vals = new ArrayList();
        for (int i = 0; i < a.size(); i++) {
            if (!dontuse[i])
                for (int j = i + 1; j < a.size(); j++) {
                    if (!dontuse[j]) {
                        double d = pairAgree(data[i], data[j]);
                        //if (d >= cutoff)
                        vals.add(new Double(d));
                    }
                }
        }
        double[] doubles = MoreArray.ArrayListtoDouble(vals);
        //"arrayPairAgree " + 
        //MoreArray.printArray(doubles);
        double d = avg(doubles);
        ArrayList ret = new ArrayList();
        ret.add(new Double(d));
        ret.add(dontuse);
        return ret;
    }

    /**
     * @param a
     * @param cutoff
     * @param data
     * @param dontuse
     */
    private static boolean[] countPairDisagree(ArrayList a, double cutoff, double[][] data, ArrayList files, boolean[] dontuse) {
        while (true) {
            int[] count = new int[a.size()];
            for (int i = 0; i < a.size(); i++) {
                if (!dontuse[i])
                    for (int j = 0; j < a.size(); j++) {
                        if (!dontuse[j])
                            if (i != j) {
                                double d = pairAgree(data[i], data[j]);
                                if (d < cutoff)
                                    count[i]++;
                            }
                    }
            }
            int max = stat.findMax(count);
            int index = -1;
            try {
                index = MoreArray.getArrayInd(count, max);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (max > 0) {
                dontuse[index] = true;
                String file = (String) files.get(index);
                System.out.println("countPairDisagree discarding " + file);
            } else break;
        }

        return dontuse;
    }

    /**
     *
     */
    public static double pairAgree(double[] a, double[] b) {
        double ret = Double.NaN;
        if (a.length == b.length) {
            double agree = 0;
            double denom = 0;
            for (int i = 0; i < a.length; i++) {
                if (a[i] != 0 && b[i] != 0) {
                    agree++;
                    denom++;
                } else if (a[i] != 0 && b[i] == 0) {
                    denom++;
                } else if (a[i] == 0 && b[i] != 0) {
                    denom++;
                }
                ret = agree / denom;
            }
        } else
            System.out.println("Arrays of different length " + a.length + "\t" + b.length);
        return ret;
    }

    /**
     * @param d
     * @return
     */
    public static double median(double[] d) {
        Arrays.sort(d);
        //MoreArray.printArray(d);
        if (d.length % 2 == 0) {
            return (d[(int) ((double) d.length / 2.0) - 1] + d[(int) ((double) d.length / 2.0)]) / 2.0;
        }
        return d[(int) stat.roundUp((double) (d.length / 2.0) - 1, 0)];
    }

    /**
     * Computes the mean of Double objects stored in the array.
     *
     * @param a
     * @return
     */
    public static double listAvg(ArrayList a) {
        double sum = 0;
        double count = 0;
        for (int i = 0; i < a.size(); i++) {
            try {
                double cur = ((Double) a.get(i)).doubleValue();
                sum += cur;
                count++;
            } catch (Exception e) {
                //System.out.println("not a double "+(string)a.get(i));
                e.printStackTrace();
            }
        }
        //System.out.println("listAvg "+a.size()+"\t"+count);
        double ret = Double.NaN;
        if (count > 0)
            ret = sum / count;
        return ret;
    }

    /**
     * Calculates the average of a sample.
     *
     * @param a
     * @return
     */
    public static double[][] array2DSampAvg(ArrayList a) {
        double[][] first = (double[][]) a.get(0);
        double[][] store = Matrix.initValues(first.length, first[0].length, 0);
        double sampsize = a.size();
        for (int i = 0; i < sampsize; i++)
            store = mathy.Matrix.add((double[][]) a.get(i), store);
        return norm(store, sampsize);
    }

    /**
     * Calculates the average of a sample.
     *
     * @param a
     * @return
     */
    public static double doubleSampSD(ArrayList a, double mean) {
        double sd = 0;
        for (int i = 0; i < a.size(); i++) {
            double cur = ((Double) a.get(i)).doubleValue();
            sd += Math.pow((cur - mean), 2);
        }
        sd = Math.sqrt(sd / (double) (a.size() - 1.0));
        return sd;
    }

    /**
     * Calculates the average of a sample.
     *
     * @param a
     * @return
     */
    public static double[][] array2DSampSD(ArrayList a, double[][] mean) {
        double[][] first = (double[][]) a.get(0);
        double[][] store = Matrix.initValues(first.length, first[0].length, 0);
        double[] m = new double[first.length];
        for (int i = 0; i < first.length; i++) {
            for (int j = 0; j < first[0].length; j++) {
                double[][] cur = (double[][]) a.get(i);
                store = mathy.Matrix.add(cur, store);
            }
        }
        double[][] sd = new double[mean.length][mean[0].length];
        for (int i = 0; i < a.size(); i++) {
            double[][] cur = (double[][]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                for (int k = 0; k < cur[0].length; k++) {
                    sd[j][k] = sd[j][k] + Math.pow((cur[j][k] - mean[j][k]), 2);
                    i++;
                }
            }
        }
        for (int j = 0; j < mean.length; j++) {
            for (int k = 0; k < mean[0].length; k++) {
                sd[j][k] = Math.sqrt(sd[j][k] / (a.size() - 1.0));
            }
        }
        return sd;
    }

    /**
     * Calculates the standard deviation of a sample.
     *
     * @param a
     * @return
     */
    public static double[] arraySampSD(ArrayList a, double[] mean) {
        double[] sd = new double[mean.length];
        for (int i = 0; i < a.size(); i++) {
            double[] cur = (double[]) a.get(i);
            for (int j = 0; j < cur.length; j++) {
                sd[j] = sd[j] + Math.pow(cur[j] - mean[j], 2);
                i++;
            }
        }
        for (int j = 0; j < mean.length; j++) {
            sd[j] = Math.sqrt(sd[j] / (a.size() - 1.0));
        }
        return sd;
    }

    /**
     * @param x
     * @param mea
     * @param sda
     * @return
     */
    public static float[] norm(float[] x, float mea, float sda) {
        float[] ret = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            ret[i] = ((x[i] - mea) / sda);
        }
        return ret;
    }

    /**
     * @param x
     * @param mea
     * @param sda
     * @return
     */
    public final double[] norm(double[] x, double mea, double sda) {
        double[] ret = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            ret[i] = (x[i] - mea) / sda;
        }
        return ret;
    }

    /**
     * @param x
     * @return
     */
    public final double[] norm(double[] x) {
        double mea = avg(x);
        double sda = SD(x, mea);
        double[] ret = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            ret[i] = (x[i] - mea) / sda;
        }
        return ret;
    }

    /**
     * @param x
     * @param mea
     * @param sda
     * @return
     */
    public final double[][] Arraynorm(double[][] x, double mea, double sda) {
        double[][] ret = new double[x.length][x[0].length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                ret[i][j] = ((x[i][j] - mea) / sda);
            }
        }
        return ret;
    }

    /**
     * @param x
     * @param mean
     * @param sd
     * @return
     */
    public static double[][] symArraynorm(double[][] x, double mean, double sd) {
        double[][] ret = new double[x.length][x[0].length];
        for (int i = 0; i < x.length; i++) {
            for (int j = i; j < x[0].length; j++) {
                ret[i][j] = (x[i][j] - mean) / sd;
            }
        }
        return ret;
    }

    /**
     * @param x
     * @param mean
     * @param sd
     * @return
     */
    public static ArrayList vnormData1(ArrayList x, double mean, double sd) {
        ArrayList ret = new ArrayList();
        double calc = 0;
        for (int i = 0; i < x.size(); i++) {
            dtype.Data1 cur = (dtype.Data1) x.get(i);
            calc = (cur.x - mean) / sd;
            dtype.Data1 dat = new dtype.Data1(calc);
            ret.add(dat);
        }
        return ret;
    }

    /**
     * @param x
     * @param mean
     * @param sd
     * @return
     */
    public static ArrayList vnorm(ArrayList x, double mean, double sd) {
        ArrayList ret = new ArrayList();
        double calc = 0;
        for (int i = 0; i < x.size(); i++) {
            Double cur = (Double) x.get(i);
            calc = (cur.doubleValue() - mean) / sd;
            ret.add(new Double(calc));
        }
        return ret;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public final float rcov(float[] x, float[] y) {
        float cov = 0;
        int i = 0;
        for (i = 0; i < x.length; i++) {
            cov += (x[i] * y[i]);
        }
        cov = cov / (float) x.length;
        return cov;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public final double rcov(double[] x, double[] y) {
        double cov = 0;
        for (int i = 0; i < x.length; i++) {
            cov += (x[i] * y[i]);
        }
        cov = cov / (double) x.length;
        return cov;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public final double Arrayrcov(double[][] x, double[][] y) {
        double cov = 0;
        double ia = 0;
        //x = computeArrayNorm(x);
        //y = computeArrayNorm(y);
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                cov += (x[i][j] * y[i][j]);
                ia++;
            }
        }
        cov = cov / ia;
        return cov;
    }


    /**
     * @param x
     * @param y
     * @return
     */
    public static double symArrayrcov(double[][] x, double[][] y) {
        double cov = 0;
        int i = 0;
        for (i = 0; i < x.length; i++) {
            for (int j = i; j < x[0].length; j++) {
                cov += (x[i][j] * y[i][j]);
                i++;
            }
        }
        cov = cov / (double) i;
        return cov;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public static double vrcov(ArrayList x, ArrayList y) {
        double cov = 0;
        double a = 0;
        double b = 0;
        double counter = 0;
        for (int i = 0; i < x.size(); i++) {
            Data1 ba = ((Data1) x.get(i));
            a = ba.x;
            Data1 ab = ((Data1) y.get(i));
            b = ab.x;
            cov += (a * b);
            counter++;
        }
        cov = cov / counter;
        return cov;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public static float vrcovfloat(ArrayList x, ArrayList y) {
        float cov = 0;
        float a = 0;
        float b = 0;
        float counter = 0;
        for (int i = 0; i < x.size(); i++) {
            Data1 ba = ((Data1) x.get(i));
            a = (float) ba.x;
            Data1 ab = ((Data1) y.get(i));
            b = (float) ab.x;
            cov += (a * b);
            counter++;
        }
        cov = cov / counter;
        return cov;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public final double rcov1(double x, double y) {
        double cov = 0;
        cov = (x * y) / (double) 2;
        return cov;
    }

    /**
     * @param x
     * @return
     */
    public final double[] computeNorm(double[] x) {
        double a = avg(x);
        double s = SD(x, a);
        x = norm(x, a, s);
        return x;
    }

    /**
     * @param x
     * @return
     */
    public final float[] computeNorm(float[] x) {
        float a = avg(x);
        float s = SD(x, a);
        float[] ret = norm(x, a, s);
        return ret;
    }

    /**
     * @param x
     * @return
     */
    public final double[][] computeArrayNorm(double[][] x) {
        double a = Matrix.Arrayavg(x);
        double s = ArraySD(x, a);
        x = Arraynorm(x, a, s);
        return x;
    }


    /**
     * @param n
     * @param a
     * @param b
     * @return
     */
    public static double[] normtoInterval(ArrayList n, double a, double b) {
        int si = n.size();
        double test = ((Double) n.get(0)).doubleValue();
        double min = test, max = -test;
        int count = 0;
        for (int i = 0; i < si; i++) {
            if (n.get(i) != null) {
                double ni = ((Double) n.get(i)).doubleValue();
                if (ni < min)
                    min = ni;
                if (ni > max)
                    max = ni;
            }
        }
        double[] ret = new double[n.size()];
        //System.out.println("stat norm min "+min+"\tmax "+max);
        double scale = Math.abs(b - a) / (double) Math.abs(max - min);
        for (int i = 0; i < si; i++) {
            if (n.get(i) != null) {
                double ni = ((Double) n.get(i)).doubleValue();
                double reti = ni * scale;
                if (reti > b)
                    reti = b;
                else if (reti < a)
                    reti = a;
                //System.out.println("stat norm b/f " + ni + "\ta/f " + reti);
                ret[i] = reti;
            }
        }
        return ret;
    }

    /**
     * @param n
     * @param a
     * @param b
     * @return
     */
    public static double[] normtoInterval(double[] n, double a, double b) {
        double[] ret = new double[n.length];
        double min = 1, max = 0;
        for (int i = 0; i < ret.length; i++) {
            if (n[i] < min)
                min = n[i];
            if (n[i] > max)
                max = n[i];
        }
        double scale = Math.abs(b - a) / (double) Math.abs(max - min);
        for (int i = 0; i < ret.length; i++) {
            ret[i] = n[i] / scale;
            //System.out.println("norm orig "+n[i]+"\t"+ret[i]);
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static ArrayList normZerotoOne(ArrayList a) {
        ArrayList ret = new ArrayList();
        int minpos = findMin(a);
        double mintot = ((Double) a.get(minpos)).doubleValue();
        if (mintot < 0)
            a = Matrix.add(Math.abs(mintot), a);
        double cur = ((Double) a.get(0)).doubleValue();
        double min = cur, max = cur;
        for (int i = 1; i < a.size(); i++) {
            cur = ((Double) a.get(i)).doubleValue();

            if (cur < min)
                min = cur;
            if (cur > max)
                max = cur;
        }
        double scale = Math.abs(max - min);
        if (scale != 0) {
            for (int i = 0; i < a.size(); i++) {
                cur = ((Double) a.get(i)).doubleValue();
                cur /= scale;
                ret.add(new Double(cur));
            }
        } else {
            System.out.println("Maximum is equal to minimum. Setting all values to 0 corresponding to mean of normal distribution.");
            for (int i = 0; i < a.size(); i++) {
                ret.add(new Double(0));
            }
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static ArrayList setMeantoZero(ArrayList a) {
        ArrayList ret = new ArrayList();
        double mean = vavg(a);
        for (int i = 0; i < a.size(); i++) {
            double cur = ((Double) a.get(i)).doubleValue();
            ret.add(new Double(cur - mean));
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static ArrayList normPlusMinOne(ArrayList a) {
        ArrayList ret = new ArrayList();
        int minpos = findMin(a);
        double minval = ((Double) a.get(minpos)).doubleValue();
        int maxpos = findMax(a);
        double maxval = ((Double) a.get(maxpos)).doubleValue();
        double cur = ((Double) a.get(0)).doubleValue();
        double min = cur, max = cur;
        for (int i = 1; i < a.size(); i++) {
            cur = ((Double) a.get(i)).doubleValue();
            if (cur < min)
                min = cur;
            if (cur > max)
                max = cur;
        }
        double scale = Math.abs(max - min);
        if (scale != 0) {
            for (int i = 0; i < a.size(); i++) {
                cur = ((Double) a.get(i)).doubleValue();
                cur /= scale;
                ret.add(new Double(cur));
            }
        } else {
            System.out.println("Maximum is equal to minimum. Setting all values to 0 corresponding to mean of normal distribution.");
            for (int i = 0; i < a.size(); i++) {
                ret.add(new Double(0));
            }
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static ArrayList normZeroOne(ArrayList a) {

        ArrayList ret = new ArrayList();

        int minpos = findMin(a);
        double minval = ((Double) a.get(minpos)).doubleValue();

        if (minval < 0)
            a = Matrix.add(Math.abs(minval), a);

        minpos = findMin(a);
        minval = ((Double) a.get(minpos)).doubleValue();
        int maxpos = findMax(a);
        double maxval = ((Double) a.get(maxpos)).doubleValue();

        System.out.println(minval + "\t" + maxval);

        double cur = ((Double) a.get(0)).doubleValue();
        double min = cur, max = cur;

        for (int i = 1; i < a.size(); i++) {

            cur = ((Double) a.get(i)).doubleValue();

            if (cur < min)
                min = cur;
            if (cur > max)
                max = cur;
        }

        double scale = Math.abs(max - min);

        if (scale != 0) {
            for (int i = 0; i < a.size(); i++) {
                cur = ((Double) a.get(i)).doubleValue();
                cur /= scale;
                ret.add(new Double(cur));
            }
        } else {
            System.out.println("Maximum is equal to minimum. Setting all values to 0 corresponding to mean of normal distribution.");
            for (int i = 0; i < a.size(); i++) {
                ret.add(new Double(0));
            }
        }

        return ret;
    }

    /**
     * @param n
     * @return
     */
    public static double[] normZerotoOne(double[] n) {

        double mintot = findMin(n);
        if (mintot < 0)
            n = Matrix.add(Math.abs(mintot), n);

        double[] ret = new double[n.length];
        double min = n[0], max = n[0];
        for (int i = 0; i < ret.length; i++) {
            if (n[i] < min)
                min = n[i];
            if (n[i] > max)
                max = n[i];
        }
        double scale = Math.abs(max - min);
        for (int i = 0; i < ret.length; i++) {
            ret[i] = n[i] / scale;
        }
        return ret;
    }

    /**
     * @param n
     * @return
     */
    public static float[] normZerotoOne(float[] n) {
        float[] ret = new float[n.length];
        float min = 1, max = 0;
        for (int i = 0; i < ret.length; i++) {
            if (n[i] < min)
                min = n[i];
            if (n[i] > max)
                max = n[i];
        }
        float scale = Math.abs(max - min);
        for (int i = 0; i < ret.length; i++) {
            ret[i] = n[i] / scale;
        }
        return ret;

    }

    /**
     * Normalizes the integer aray to an interval.
     *
     * @param n
     * @param factor
     * @param add
     * @param cut
     * @return
     */
    public static int[] normtoIntervalInteger(int[] n, int factor, int add, int cut) {

        int[] ret = new int[n.length];
        int min = findMin(n);
        int max = findMax(n);

        double scale = Math.abs(max - min);
        if (cut != -1)
            scale = Math.abs(max - cut);

        for (int i = 0; i < ret.length; i++) {
            if (n[i] >= cut)
                ret[i] = (int) ((double) factor * ((double) n[i] / (double) scale)) + add;
//System.out.println("normalizing  "+n[i]+"  ,ret "+ret[i]);
        }

        return ret;

    }


    /**
     * Normalizes the array to percent of total.
     *
     * @param n
     * @return
     */
    public static double[] normtoPercent(double[] n) {
        double[] ret = new double[n.length];
        double sum = 0;
        for (int i = 0; i < ret.length; i++) {
            sum += n[i];
        }
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (n[i] / sum) * (double) 100;
        }
        return ret;
    }

    /**
     * @param n
     * @return
     */
    public static double[] normtoFraction(double[] n) {
        double[] ret = new double[n.length];
        double sum = 0;
        for (int i = 0; i < ret.length; i++) {
            sum += n[i];
        }
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (n[i] / sum);
        }
        return ret;
    }

    /**
     * Normalizes the array to percent of total.
     *
     * @param n
     * @return
     */
    public static double[][] normtoPercent(double[][] n) {
        double[][] ret = new double[n.length][n.length];
        double sum = 0;
        for (int i = 0; i < ret.length; i++)
            for (int j = 0; j < ret.length; j++) {
                sum += n[i][j];
            }

        for (int i = 0; i < ret.length; i++)
            for (int j = 0; j < ret.length; j++) {
                ret[i][j] = n[i][j] / (n[i][j] / sum) * (double) 100.0;
            }
        return ret;

    }

    /**
     * Mean absolute difference between two vectors.
     *
     * @param a
     * @param b
     * @return
     */
    public static double arrayMeanDiff(double[] a, double[] b) {

        double diff = 0;
        for (int i = 0; i < a.length; i++)
            diff = diff + Math.abs(a[i] - b[i]);
        diff = diff / (double) a.length;
        return diff;
    }

    /**
     * Mean absolute difference between two vectors.
     *
     * @param a
     * @param b
     * @return
     */
    public static int arrayMeanDiff(int[] a, int[] b) {
        double diff = 0;
        for (int i = 0; i < a.length; i++)
            diff = diff + Math.abs(a[i] - b[i]);
        diff = diff / (double) a.length;
        return (int) diff;
    }

    /**
     * Mean absolute difference between two vectors.
     *
     * @param a
     * @param b
     * @return
     */
    public static double arraySDDiff(double[] a, double[] b, double meandiff) {
        double sd = 0;
        for (int i = 0; i < a.length; i++) {
            sd += Math.pow((meandiff - Math.abs(a[i] - b[i])), 2);
        }
        sd = Math.sqrt(sd / (double) (a.length - 1));
        return sd;
    }

    /**
     * Mean directed difference between two vectors.
     *
     * @param a
     * @param b
     * @return
     */
    public static double arrayDirectedMeanDiff(double[] a, double[] b) {
        double diff = 0;
        for (int i = 0; i < a.length; i++)
            diff = diff + (a[i] - b[i]);
        diff = diff / (double) a.length;
        return diff;
    }

    /**
     * Mean absolute difference between two matrices.
     *
     * @param a
     * @param b
     * @return
     */
    public final double matrixNegMeanDiff(double[][] a, double[][] b) {
        double diff = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++) {
                if (a[i][j] - b[i][j] < 0)
                    diff = diff + Math.abs(a[i][j] - b[i][j]);
            }

        diff = diff / Math.pow(a.length, 2);
        return diff;
    }

    /**
     * Mean absolute difference of > 0 values between two matrices.
     *
     * @param a
     * @param b
     * @return
     */
    public static double matrixPlusMeanDiff(double[][] a, double[][] b) {
        double diff = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++) {
                if (a[i][j] - b[i][j] > 0)
                    diff = diff + Math.abs(a[i][j] - b[i][j]);
            }

        diff = diff / Math.pow(a.length, 2);
        return diff;
    }

    /**
     * Mean directed difference between two matrices.
     *
     * @param a
     * @param b
     * @return
     */
    public final double matrixDirectedMeanDiff(double[][] a, double[][] b) {
        double diff = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++)
                diff = diff + (a[i][j] - b[i][j]);

        diff = diff / Math.pow(a.length, 2);
        return diff;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public final double matrixMeanDiff(double[][] a, double[][] b) {
        double diff = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++)
                diff = diff + Math.abs(a[i][j] - b[i][j]);

        diff = diff / Math.pow(a.length, 2);
        return diff;
    }

    /**
     * Mathew's coefficient between of matrices.
     *
     * @param tru
     * @param pred
     * @return
     */
    public final double mathewCoeffMat(double[][] tru, double[][] pred) {
        trueplus = 0;
        trueminus = 0;
        falseplus = 0;
        falseminus = 0;
        for (int i = 0; i < tru.length; i++)
            for (int j = 0; j < tru[0].length; j++) {
                if (tru[i][j] > 0) {
                    if (pred[i][j] > 0)
                        trueplus++;
                    if (pred[i][j] == 0)
                        trueminus++;
                }
                if (tru[i][j] == 0) {
                    if (pred[i][j] > 0)
                        falseminus++;
                    if (pred[i][j] == 0)
                        falseplus++;
                }
            }
        double ret = matCoeff(trueplus, trueminus, falseplus, falseminus);
        return ret;
    }

    /**
     * Mathew's coefficient of two arrays.
     *
     * @param tru
     * @param pre
     * @return
     */
    public final double mathewCoeffArr(double[] tru, double[] pre) {
        trueplus = 0;
        trueminus = 0;
        falseplus = 0;
        falseminus = 0;
        for (int i = 0; i < tru.length; i++) {
            if (tru[i] > 0) {
                if (pre[i] > 0) {
                    System.out.println("true TRUE  in mathew  " + i + "   " + tru[i] + "  " + pre[i]);
                    trueplus++;
                }
                if (pre[i] == 0) {
                    trueminus++;
                    //System.out.println("false TRUE in mathew "+i+"   "+tru[i]+"  "+pre[i]);
                }
            }
            if (tru[i] == 0) {
                if (pre[i] > 0) {
                    falseminus++;
                    System.out.println("false FALSE  in mathew  " + i + "   " + tru[i] + "  " + pre[i]);
                }
                if (pre[i] == 0) {
                    falseplus++;
                    //System.out.println("true FALSE  in mathew  "+i+"   "+tru[i]+"  "+pre[i]);
                }
            }
        }
        sn = (trueplus / (trueplus + trueminus + falseminus + falseplus));
//System.out.println("S/N  "+sn);
//	System.out.println("trueplus  "+trueplus+"  trueminus  "+trueminus+"  falseplus  "+falseplus+"  falseminus  "+falseminus);
        double ret = matCoeff(trueplus, trueminus, falseplus, falseminus);
        return ret;
    }
    /**matCoeff(trueplus, trueminus, falseplus, falseminus);
     */

    /**
     * Mathew's coefficient given counts of all cases..
     *
     * @param c1 true positive
     * @param c2 false positive
     * @param c4 true negative
     * @param c5 false negative
     * @return
     */
    public final double matCoeff(double c1, double c2, double c4, double c5) {
        double d1 = c1, d2 = c2, d4 = c4, d5 = c5;
        mathew = (d1 * d4 - d2 * d5) / Math.sqrt((d4 + d2) * (d4 + d5) * (d1 + d2) * (d1 + d5));
        //System.out.println("mathew in STAT   "+mathew);
        return mathew;
    }

    /**
     * Counts number of non -1 values in array.
     *
     * @param data
     * @return
     */
    public static int countNotMinusOne(double[] data) {
        int min = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i] != -1)
                min++;
        return min;
    }

    /**
     * Counts the number of -1 values in array.
     *
     * @param data
     * @return
     */
    public static int countMinusOne(double[] data) {
        int min = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i] == -1)
                min++;
        return min;
    }

    /**
     * Counts the number of -1 values in array. Uses only the [0] row of the array.
     *
     * @param data
     * @return
     */
    public static int countMinusOne(double[][] data) {
        int min = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i][0] == -1)
                min++;
        return min;
    }

    /**
     * Counts the number of 'a' values in array. Uses only the [0] row of the array.
     *
     * @param data
     * @param a
     * @return
     */
    public static int countOccurence(int[][] data, int a) {
        int count = 0;
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data.length; j++)
                if (data[i][j] == a)
                    count++;
        return count;
    }

    /**
     * @param data
     * @param a
     * @return
     */
    public static int countOccurence(int[] data, int a) {
        int count = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i] == a)
                count++;
        return count;
    }

    /**
     * @param dataA
     * @param dataB
     * @param a
     * @return
     */
    public static int countCooccurence(double[] dataA, double[] dataB, double a) {
        int count = -1;
        boolean isnan = false;
        if (Double.isNaN(a))
            isnan = true;
        if (dataA.length == dataB.length) {
            count = 0;
            for (int i = 0; i < dataA.length; i++)
                if ((isnan && Double.isNaN(dataA[i]) && Double.isNaN(dataB[i]))
                        || (dataA[i] == a && dataB[i] == a))
                    count++;
        }
        return count;
    }

    /**
     * @param data
     * @param a
     * @return
     */
    public static int countOccurence(double[] data, double a) {
        int count = 0;
        boolean isnan = false;
        if (Double.isNaN(a))
            isnan = true;
        for (int i = 0; i < data.length; i++) {
            if ((isnan && Double.isNaN(data[i]))
                    || (data[i] == a))
                count++;
        }
        return count;
    }

    /**
     * Counts the number of -1 values in array. Uses only the [0] row of the array.
     *
     * @param data
     * @return
     */
    public static int countMinusOne(int[][] data) {
        int min = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i][0] == -1)
                min++;
        return min;
    }

    /**
     * @param data
     * @return
     */
    public static int countLessThan(double[] data, double cutoff) {
        int ret = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i] < cutoff)
                ret++;
        return ret;
    }

    /**
     * @param data
     * @param cutoff
     * @return
     */
    public static int countGreaterThan(double[] data, double cutoff) {
        int ret = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i] > cutoff)
                ret++;
        return ret;
    }

    /**
     * @param data
     * @param cutoff
     * @return
     */
    public static int countGreaterThan(int[] data, int cutoff) {
        int ret = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i] > cutoff)
                ret++;
        return ret;
    }

    /**
     * @param data
     * @param cutoff
     * @return
     */
    public static int[] countGreaterThanByRow(double[][] data, double cutoff) {
        int[] ret = new int[data.length];
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[0].length; j++)
                if (data[i][j] > cutoff)
                    ret[i]++;
        return ret;
    }

    /**
     * @param data
     * @param val
     * @return
     */
    public static int[] countNotEqualByRow(double[][] data, double val) {
        int[] ret = new int[data.length];
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[0].length; j++)
                if (data[i][j] != val)
                    ret[i]++;
        return ret;
    }

    /**
     * Counts the number of entries greater than cuttoff for each row.
     *
     * @param data
     * @param cutoff
     * @return
     */
    public static int[] countGreaterThanByCol(double[][] data, double cutoff) {
        int[] ret = new int[data[0].length];
        for (int j = 0; j < data[0].length; j++)
            for (int i = 0; i < data.length; i++)
                if (data[i][j] > cutoff)
                    ret[j]++;
        return ret;
    }

    /**
     * Counts the number of entries greater than cuttoff for each row.
     *
     * @param data
     * @param val
     * @return
     */
    public static int[] countNotEqualByCol(double[][] data, double val) {
        int[] ret = new int[data[0].length];
        for (int j = 0; j < data[0].length; j++)
            for (int i = 0; i < data.length; i++)
                if (data[i][j] != val)
                    ret[j]++;
        return ret;
    }


    /**
     * Finds the min value of the array.
     *
     * @param data
     * @return
     */
    public static int findMinPos(double[] data) {
        double minval = findMin(data);
        int ret = -1;
        for (int i = 0; i < data.length; i++)
            if (data[i] == minval) {
                ret = i;
                break;
            }
        return ret;
    }

    /**
     * @param data
     * @param d
     * @return
     */
    public static int findMinPosGreaterEqualThanReverse(double[] data, double d) {
        for (int i = data.length - 1; i > -1; i--)
            if (!Double.isNaN(data[i]) && d <= data[i]) {
                if (i < data.length - 1)
                    return i + 1;
                else
                    return -1;
            }
        return 0;
    }

    /**
     * @param data
     * @param d
     * @return
     */
    public static int findMinPosGreaterThanReverse(double[] data, double d) {
        for (int i = data.length - 1; i > -1; i--)
            if (!Double.isNaN(data[i]) && d < data[i]) {
                if (i < data.length - 1)
                    return i + 1;
                else
                    return -1;
            }
        return 0;
    }

    /**
     * @param data
     * @param d
     * @return
     */
    public static int findMinPosGreaterThanEqualReverse(double[] data, double d) {
        for (int i = data.length - 1; i > -1; i--)
            if (!Double.isNaN(data[i]) && d <= data[i]) {
                if (i < data.length - 1)
                    return i + 1;
                else
                    return -1;
            }
        return 0;
    }

    /**
     * Find the left most value of -1 in array.
     *
     * @param data
     * @return
     */
    public static int findMinLeftPosMinusOne(double[] data) {
        int ret = -1;
        for (int i = 0; i < data.length; i++)
            if (data[i] == -1) {
                ret = i;
                break;
            }
        return ret;
    }

    /**
     * Find the left most value of -1 in double matrix.
     * Only considers the [0] row in the matrix.
     *
     * @param data
     * @return
     */
    public static int findMinLeftPosMinusOne(double[][] data) {
        int ret = -1;
        for (int i = 0; i < data.length; i++)
            if (data[i][0] == -1) {
                ret = i;
                break;
            }
        return ret;
    }

    /**
     * Find the left most value of -1 in int matrix.
     * Only considers the [0] row in the matrix.
     *
     * @param data
     * @return
     */

    public static int findMinLeftPosMinusOne(int[][] data) {
        int ret = -1;
        for (int i = 0; i < data.length; i++)
            if (data[i][0] == -1) {
                ret = i;
                break;
            }
        return ret;
    }

    /**
     * Find the left most value of -1 in double matrix past start index.
     * Only considers the [0] row in the matrix.
     *
     * @param data
     * @param start
     * @return
     */
    public static int findMinLeftPosMinusOneStart(double[][] data, int start) {
        int ret = -1;
        for (int i = start; i < data.length; i++)
            if (data[i][0] == -1) {
                ret = i;
                break;
            }
        return ret;
    }

    /**
     * Find the left most value of -1 in int matrix past start index.
     * Only considers the [0] row in the matrix.
     *
     * @param data
     * @param start
     * @return
     */
    public static int findMinLeftPosMinusOneStart(int[][] data, int start) {

        if (start < 0)
            start = 0;

        int ret = -1;
        for (int i = start; i < data.length; i++)
            if (data[i][0] == -1) {
                ret = i;
                break;
            }
        return ret;
    }

    /**
     * Finds first index of minimum not -1 value in double array.
     *
     * @param data
     * @return
     */
    public static int findMinPosNotMinusOne(double[] data) {
        double minval = findMinNotMinusOne(data);
        int ret = -1;
        for (int i = 0; i < data.length; i++)
            if (data[i] == minval) {
                ret = i;
                break;
            }
        return ret;
    }

    /**
     * Finds min not -1 value in double array.
     *
     * @param data
     * @return
     */
    public static double findMinNotMinusOne(double[] data) {
        double min = data[0];
        for (int i = 0; i < data.length; i++)
            if (data[i] < min && data[i] != -1) {
                min = data[i];
            }
        return min;
    }

    /**
     * Finds first index of minimum not -1 value in double array.
     *
     * @param data
     * @return
     */
    public static int findMinPosNot(double[] data, double not) {
        int ret = -1;
        double minval = Double.POSITIVE_INFINITY;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != not && data[i] < minval) {
                minval = data[i];
                ret = i;
            }
        }
        return ret;
    }

    /**
     * Finds first index of value != to 'not'.
     *
     * @param data
     * @param not
     * @return
     */
    public static int findFirstPosNot(double[] data, double not) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != not) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the min value in double array.
     *
     * @param data
     * @return
     */
    public static int findMin(ArrayList data) {
        double min = ((Double) data.get(0)).doubleValue();
        int minpos = 0;
        for (int i = 1; i < data.size(); i++) {
            double cur = ((Double) data.get(i)).doubleValue();
            if (cur < min) {
                min = cur;
                minpos = i;
            }
        }
        return minpos;
    }

    /**
     * Finds the min value in double array.
     *
     * @param data
     * @return
     */
    public static int findMax(ArrayList data) {
        double max = ((Double) data.get(0)).doubleValue();
        int maxpos = 0;
        for (int i = 1; i < data.size(); i++) {
            double cur = ((Double) data.get(i)).doubleValue();
            if (cur > max) {
                max = cur;
                maxpos = i;
            }
        }
        return maxpos;
    }

    /**
     * Finds the min value in double array.
     *
     * @param data
     * @return minimum value
     */
    public static double findMin(double[] data) {
        double min = data[0];
        for (int i = 0; i < data.length; i++)
            if (data[i] < min)
                min = data[i];
        return min;
    }

    /**
     * Finds the min value in float array.
     *
     * @param data
     * @return
     */
    public static float findMin(float[] data) {
        float min = data[0];
        for (int i = 0; i < data.length; i++)
            if (data[i] < min)
                min = data[i];
        return min;
    }

    /**
     * Finds the min value in int array.
     *
     * @param data
     * @return
     */
    public static int findMin(int[] data) {
        int min = data[0];
        for (int i = 0; i < data.length; i++)
            if (data[i] < min)
                min = data[i];
        return min;
    }

    /**
     * @param data
     * @param k
     * @return
     */
    public static int findIndex(int[] data, int k) {
        for (int i = 0; i < data.length; i++)
            if (data[i] == k)
                return i;
        return -1;
    }

    /**
     * @param data
     * @param k
     * @return
     */
    public static int findIndex(Double[] data, double k) {
        for (int i = 0; i < data.length; i++)
            if (data[i] == k)
                return i;
        return -1;
    }

    /**
     * @param data
     * @param k
     * @return
     */
    public static int findIndex(double[] data, double k) {
        for (int i = 0; i < data.length; i++)
            if (data[i] == k)
                return i;
        return -1;
    }


    /**
     * Finds the maximum value of array.
     *
     * @param data
     * @return
     */
    public static double findMax(double[] data) {
        double max = data[0];
        for (int i = 0; i < data.length; i++)
            if (data[i] > max)
                max = data[i];
        return max;
    }

    /**
     * Finds the maximum value of array.
     *
     * @param data
     * @return
     */
    public static double findMax(Double[] data) {
        double max = data[0];
        for (int i = 0; i < data.length; i++)
            if (data[i] > max)
                max = data[i];
        return max;
    }

    /**
     * Finds the maximum value of array.
     *
     * @param data
     * @return
     */
    public static int findMaxIndex(double[] data) {
        double max = data[0];
        int maxint = -1;
        for (int i = 0; i < data.length; i++)
            if (data[i] > max) {
                max = data[i];
                maxint = i;
            }
        return maxint;
    }

    /**
     * Finds the maximum value of array.
     *
     * @param data
     * @return
     */
    public static int findMinIndex(double[] data) {
        double min = data[0];
        int minint = 0;
        for (int i = 1; i < data.length; i++)
            if (data[i] < min) {
                System.out.println("findMinIndex " + i + "\t" + min + "\t" + minint);
                min = data[i];
                minint = i;
            }
        return minint;
    }

    /**
     * Finds the maximum value of array.
     *
     * @param data
     * @return
     */
    public static float findMax(float[] data) {
        float max = data[0];
        for (int i = 0; i < data.length; i++)
            if (data[i] > max)
                max = data[i];
        return max;
    }

    /**
     * Finds the maximum value of array.
     *
     * @param data
     * @return
     */
    public static int findMax(int[] data) {
        int max = data[0];
        for (int i = 0; i < data.length; i++)
            if (data[i] > max)
                max = data[i];
        return max;
    }

    /**
     * Returns the sum of all values in the double array.
     *
     * @param d
     * @return
     */
    public static double sumEntries(double[] d) {
        double sum = 0;
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]))
                sum += d[i];
        }
        return sum;
    }

    /**
     * @param d
     * @return
     */
    public static double sumEntries(double[] d, int[] index) {
        double sum = 0;
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]) && index[i] > 0)
                sum += d[i];
        }
        return sum;
    }

    /**
     * @param d
     * @return
     */
    public static BigDecimal sumEntriesBigD(double[] d, int[] index) {
        BigDecimal sum = new BigDecimal(0.0f);
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]) && index[i] > 0) {
                BigDecimal val = new BigDecimal(d[i]);
                sum = sum.add(val);
            }
        }
        return sum;
    }

    /**
     * Returns the sum of squares of all values in the double array.
     *
     * @param d
     * @return
     */
    public static double sumEntriesSquared(double[] d) {
        double sum = 0;
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]))// != Double.NaN)
                sum += Math.pow(d[i], 2);
        }
        return sum;
    }

    /**
     * @param d
     * @return
     */
    public static double sumEntriesDiffMeanSquared(double[] d, double mean) {
        double sum = 0;
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]))// != Double.NaN)
                sum += Math.pow(d[i] - mean, 2);
        }
        return sum;
    }

    /**
     * @param d
     * @param mean
     * @param index
     * @return
     */
    public static double sumEntriesDiffMeanSquared(double[] d, double mean, int[] index) {
        double sum = 0;
        for (int i = 0; i < d.length; i++) {
            if (index[i] == 1 && !Double.isNaN(d[i]))
                sum += Math.pow(d[i] - mean, 2);
        }
        return sum;
    }


    /**
     * @param d
     * @param index
     * @return
     */
    public static double sumEntriesSquared(double[] d, int[] index) {
        double sum = 0;
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]) && index[i] > 0)
                sum += Math.pow(d[i], 2);
        }
        return sum;
    }

    /**
     *
     */
    public static BigDecimal sumEntriesSquaredBigD(double[] d, int[] index) {
        BigDecimal sum = new BigDecimal(0.0f);
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]) && index[i] > 0) {
                BigDecimal val = new BigDecimal(d[i]);
                val = val.multiply(val);
                sum = sum.add(val);
            }
        }
        return sum;
    }

    /**
     * Returns the sum of all values in the int array.
     *
     * @param d
     * @return
     */
    public static double sumEntries(int[] d) {
        double sum = 0;
        for (int i = 0; i < d.length; i++)
            sum += d[i];
        return sum;
    }

    /**
     * @param a
     * @param b
     * @param index
     * @return
     */
    public static double sumProduct(double[] a, double[] b, int[] index) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && !Double.isNaN(b[i]) && index[i] > 0)
                sum += a[i] * b[i];
        }
        return sum;
    }

    /**
     * @param a
     * @param b
     * @param index
     * @return
     */
    public static BigDecimal sumProductBigD(double[] a, double[] b, int[] index) {
        BigDecimal sum = new BigDecimal(0.0f);
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && !Double.isNaN(b[i]) && index[i] > 0) {
                BigDecimal vala = new BigDecimal(a[i]);
                BigDecimal valb = new BigDecimal(a[i]);
                sum = sum.add(vala.multiply(valb));
            }
        }
        return sum;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double sumProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && !Double.isNaN(b[i]))
                sum += a[i] * b[i];
        }
        return sum;
    }

    /**
     * @param d
     * @return
     */
    public static double[] invert(double[] d) {
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]) && d[i] != 0)
                d[i] = 1.0 / d[i];
            else
                d[i] = Double.NaN;
        }
        return d;

    }

    /**
     * Finds the norm value of the double array.
     *
     * @param d
     * @param n
     * @return
     */
    public static double[] norm(double[] d, double n) {
        double[] newd = MoreArray.copy(d);
        if (n != 0)
            for (int i = 0; i < d.length; i++) {
                if (!Double.isNaN(d[i]))
                    newd[i] /= n;
            }
        else
            for (int i = 0; i < d.length; i++)
                newd[i] = Double.NaN;
        return newd;

    }

    /**
     * @param d
     * @param n
     * @return
     */
    public static double[] norm(double[] d, double[] n) {
        double[] newd = MoreArray.copy(d);
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]) && n[i] != 0)
                newd[i] /= n[i];
            else
                newd[i] = Double.NaN;
        }
        return newd;

    }

    /**
     * Finds the norm value of the double array.
     *
     * @param d
     * @param n
     * @return
     */
    public static double[][] norm(double[][] d, double n) {
        double[][] newd = MoreArray.copy(d);
        for (int i = 0; i < d.length; i++)
            for (int j = 0; j < d[0].length; j++)
                newd[i][j] /= n;
        return newd;
    }


    /**
     * @param r
     * @param N
     * @return
     */
    public static double tStatistic(double r, double N) {
        return (r * Math.sqrt(N - 2.0)) / Math.sqrt(1.0 - r * r);
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public static double correlationCoeffPopulation(double[] a, double[] b) {
        double coeff = Double.NaN;
        if (a.length == b.length) {
            ArrayList get = determineCommonSample(a, b);
            int[] index = (int[]) get.get(0);
            /*double sample = ((Integer) get.get(1)).intValue();
            double meana = avg(a);//avgOverSamp(a);//, index);//avg(a)
            double meanb = avg(b);//avgOverSamp(b);//, index);*/
            //System.out.println("correlationCoeff means " + meana + "\t" + meanb);
            double numerator = Double.NaN;
            double sumxy = 0;
            double sumx = sumEntries(a);//, index);
            double sumy = sumEntries(b);//, index);
            for (int i = 0; i < a.length; i++) {
                if (index[i] == 1 && !Double.isNaN(a[i]) && !Double.isNaN(b[i])) {
                    sumxy += (a[i]) * (b[i]);
                }
            }
            if (!Double.isNaN(sumx) && !Double.isInfinite(sumx) && !Double.isNaN(sumy) && !Double.isInfinite(sumy)) {
                numerator = ((double) a.length) * sumxy - (sumx * sumy);// / (double) a.length;//
            }

            //double num = calcCorCoeffNumPopulation(a, meana, b, meanb, index, (int) sample);
            //double denominator = calcCorCoeffDenomPopulation(a, b, index, (int) sample);
            //System.out.println("correlationCoeff num/denominator " + num + "\t" + denominator);

            double denominator = Double.NaN;
            //double sumx = sumEntries(a);//, index);
            double sumx2 = sumEntriesSquared(a);//, index);
            //double sumy = sumEntries(b);//, index);
            double sumy2 = sumEntriesSquared(b);//, index);//
            if (!Double.isNaN(sumx) && !Double.isInfinite(sumx) && !Double.isNaN(sumy) && !Double.isInfinite(sumy)) {
                double left = ((double) a.length) * sumx2 - Math.pow(sumx, 2);// / (double) a.length;// / (double) sample;//((double) a.length) *
                double right = ((double) a.length) * sumy2 - Math.pow(sumy, 2);// / (double) a.length;// / (double) sample;//((double) a.length) * 
                denominator = Math.sqrt(left) * Math.sqrt(right);
            }
            if (!Double.isNaN(numerator) && !Double.isInfinite(numerator) && !Double.isNaN(denominator) && !Double.isInfinite(denominator))
                coeff = numerator / denominator;

        } else {
            System.out.println("correlationCoeff lengths do not match " + a.length + "\t" + b.length);
        }
        return coeff;
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public static double correlationCoeffPopulationStDev(double[] a, double[] b) {
        double coeff = Double.NaN;
        int alen = a.length;
        if (alen == b.length) {
            ArrayList get = determineCommonSample(a, b);
            int[] index = (int[]) get.get(0);
            double sample = ((Integer) get.get(1)).intValue();
            double meana = avgOverSamp(a, index);//avgOverSamp(a, -1);// alen);//, index);//avg(a)
            double meanb = avgOverSamp(b, index);// avgOverSamp(b, -1);//alen);//, index);//avg(b)
            //double sda = SDOverSample(a, meana, alen);//SD(a, meana);
            //double sdb = SDOverSample(b, meanb, alen);//SD(b, meanb);
            double suma_min_amean = sumEntriesDiffMeanSquared(a, meana, index);
            double sumb_min_bmean = sumEntriesDiffMeanSquared(b, meanb, index);

            //System.out.println("correlationCoeff means " + meana + "\t" + meanb + "\t" + suma_min_amean + "\t" + sumb_min_bmean);
            double numerator = 0;

            for (int i = 0; i < alen; i++) {
                if (!Double.isNaN(a[i]) && !Double.isNaN(b[i])) {
                    numerator += (a[i] - meana) * (b[i] - meanb);
                }
            }

            //double num = calcCorCoeffNumPopulation(a, meana, b, meanb, index, (int) sample);
            //double denominator = calcCorCoeffDenomPopulation(a, b, index, (int) sample);
            //System.out.println("correlationCoeff numerator " + numerator);

            double denominator = Math.sqrt(suma_min_amean) * Math.sqrt(sumb_min_bmean);//((double) alen) * sda * sdb;
            //System.out.println("correlationCoeff denominator " + denominator + "\t" + alen + "\t" + suma_min_amean + "\t" + sumb_min_bmean);

            if (!Double.isNaN(numerator) && !Double.isInfinite(numerator) && !Double.isNaN(denominator) && !Double.isInfinite(denominator))
                coeff = numerator / denominator;
            System.out.println("correlationCoeff numerator " + numerator + "\tdenominator " + denominator + "\tcoeff " + coeff);
        } else {
            System.out.println("correlationCoeff lengths do not match " + alen + "\t" + b.length);
        }
        return coeff;
    }

    /**
     * Computes the numerator of the population correlation coefficient.
     *
     * @param a
     * @param b
     * @return
     */
    public static double calcCorCoeffNumPopulation(double[] a, double ma, double[] b, double mb, int[] index, int sample) {
        double ret = Double.NaN;
        double sumxy = 0;
        double sumx = sumEntries(a);//, index);
        double sumy = sumEntries(b);//, index);
        for (int i = 0; i < a.length; i++) {
            if (index[i] == 1 && !Double.isNaN(a[i]) && !Double.isNaN(b[i])) {
                sumxy += (a[i]) * (b[i]);
            }
        }
        if (!Double.isNaN(sumx) && !Double.isInfinite(sumx) && !Double.isNaN(sumy) && !Double.isInfinite(sumy)) {
            ret = a.length * sumxy - (sumx * sumy) / (double) a.length;
        }
        return ret;
    }

    /**
     * Computes the denominator of the correlation coefficient.
     *
     * @param a
     * @param b
     * @return
     */
    public static double calcCorCoeffDenomPopulation(double[] a, double[] b, int[] index, int sample) {
        double ret = Double.NaN;
        double sumx = sumEntries(a);//, index);
        double sumx2 = sumEntriesSquared(a);//, index);
        double sumy = sumEntries(b);//, index);
        double sumy2 = sumEntriesSquared(b);//, index);//
        if (!Double.isNaN(sumx) && !Double.isInfinite(sumx) && !Double.isNaN(sumy) && !Double.isInfinite(sumy)) {
            double left = ((double) a.length) * sumx2 - Math.pow(sumx, 2) / (double) a.length;// / (double) sample;
            double right = ((double) a.length) * sumy2 - Math.pow(sumy, 2) / (double) a.length;// / (double) sample;
            ret = Math.sqrt(left) * Math.sqrt(right);
        }
        return ret;
    }


    /**
     * Computes the numerator of the sample correlation coefficient.
     *
     * @param a
     * @param ma
     * @param b
     * @param mb
     * @return
     */
    public static double[] calcCorCoeffNumSample(double[] a, double ma, double[] b, double mb) {
        double[] ret = MoreArray.initArray(2, Double.NaN);
        //System.out.println("calcCorCoeffNum sums " + sumx + "\t" + sumy);
        double sample = 0;
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && !Double.isNaN(b[i])) {
                if (Double.isNaN(ret[0]))
                    ret[0] = 0;
                ret[0] += ((a[i] - ma) * (b[i] - mb));
                sample++;
            }
        }
        ret[1] = sample;
        return ret;
    }

    /**
     * if (!Double.isNaN(sumx) && !Double.isInfinite(sumx) && !Double.isNaN(sumy) && !Double.isInfinite(sumy))
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal[] calcCorCoeffNumSampleBigD(double[] a, double[] b, double sample, int[] index) {
        BigDecimal[] ret = new BigDecimal[3];
        BigDecimal retsum = sumEntriesBigD(a, index);
        ret[1] = retsum;
        ret[2] = sumEntriesBigD(b, index);
        BigDecimal prodxy = sumProductBigD(a, b, index);
        BigDecimal sampleB = new BigDecimal(sample);
        sampleB = sampleB.multiply(prodxy);
        BigDecimal subt = retsum.multiply(ret[2]);
        ret[0] = sampleB.subtract(subt);
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] calcCorCoeffNumSample(double[] a, double[] b, double sample, int[] index) {
        double[] ret = MoreArray.initArray(3, Double.NaN);
        double sumx = sumEntries(a, index);
        ret[1] = sumx;
        double sumy = sumEntries(b, index);
        double prodxy = sumProduct(a, b, index);
        ret[2] = sumy;
        //System.out.println("calcCorCoeffNumSample sums " + sumx + "\t" + sumy);
        if (!Double.isNaN(sumx) && !Double.isInfinite(sumx) &&
                !Double.isNaN(sumy) && !Double.isInfinite(sumy) &&
                !Double.isNaN(prodxy) && !Double.isInfinite(prodxy)) {
            ret[0] = sample * prodxy - sumx * sumy;
        }
        //System.out.println("calcCorCoeffNumSample "+ret[0]+"\t"+sample+"\t"+prodxy+"\t"+sumx+"\t"+sumy);
        return ret;
    }

    /**
     * if (!Double.isNaN(sumx) && !Double.isInfinite(sumx) && !Double.isNaN(sumy) && !Double.isInfinite(sumy))
     *
     * @param a
     * @param b
     * @param sample
     * @return
     */
    public static BigDecimal calcCorCoeffDenomSampleBigD(double[] a, double[] b, double sample,
                                                         BigDecimal sumx, BigDecimal sumy, int[] index) {
        BigDecimal sumx2 = sumEntriesSquaredBigD(a, index);
        BigDecimal sumy2 = sumEntriesSquaredBigD(b, index);
        BigDecimal leftB = new BigDecimal(sample);
        leftB = leftB.multiply(sumx2);
        sumx = sumx.multiply(sumx);//sumx.pow(2);
        leftB = leftB.subtract(sumx);
        BigDecimal rightB = new BigDecimal(sample);
        rightB = rightB.multiply(sumy2);
        sumy = sumx.multiply(sumx);//= sumy.pow(2);
        rightB = rightB.subtract(sumy);
        BigDecimal returnB = new BigDecimal(Math.sqrt(leftB.doubleValue()));
        BigDecimal returnBmult = new BigDecimal(Math.sqrt(rightB.doubleValue()));
        return returnB.multiply(returnBmult);
    }

    /**
     * @param a
     * @param b
     * @param sample
     * @return
     */
    public static double calcCorCoeffDenomSample(double[] a, double[] b, double sample,
                                                 double suma, double sumb, int[] index) {
        double ret = Double.NaN;
        double sumx = suma;
        double sumx2 = sumEntriesSquared(a, index);
        double sumy = sumb;
        double sumy2 = sumEntriesSquared(b, index);
        //System.out.println("calcCorCoeffDenomSample " + sumx + "\t" + sumx2 + "\t" + sumy + "\t" + sumy2);
        if (!Double.isNaN(sumx) && !Double.isInfinite(sumx) && !Double.isNaN(sumy) && !Double.isInfinite(sumy)) {
            //System.out.println("calcCorCoeffDenomSample sumx sumy " + sumx + "\t" + sumy);
            double left = sample * sumx2 - Math.pow(sumx, 2);
            double right = sample * sumy2 - Math.pow(sumy, 2);
            ret = Math.sqrt(left) * Math.sqrt(right);
        }
        return ret;
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public static double correlationCoeff(double[] a, double[] b) {
        //return correlationCoeffPopulationStDev(a, b);
        //return correlationCoeffPopulation(a, b);
        return correlationCoeffSample(a, b);
    }

    /**
     * Computes the correlation coefficient between two double arrays.
     *
     * @param a
     * @param b
     * @return
     */
    public static double correlationCoeffSampleBigD(double[] a, double[] b) {
        return correlationCoeffSampleBigD(a, b, 20);
    }

    /**
     * Computes the correlation coefficient between two double arrays.
     *
     * @param a
     * @param b
     * @return
     */
    public static double correlationCoeffSampleBigD(double[] a, double[] b, int scale) {
        boolean identical = MoreArray.arrayEqualsIgnoreNaN(a, b);
        double coeff = Double.NaN;
        //System.out.println("correlationCoeff means " + meana + "\t" + meanb);
        ArrayList get = determineCommonSample(a, b);
        int[] index = (int[]) get.get(0);
        /*System.out.println("correlationCoefSample common sample");
        MoreArray.printArray(index);*/
        double sample = ((Integer) get.get(1)).intValue();
        //System.out.println("correlationCoefSample common sample "+sample);
        /*System.out.println("correlationCoefSample " + sample);
        System.out.println("correlationCoefSample A");
        MoreArray.printArray(a);
        System.out.println("correlationCoefSample B");
        MoreArray.printArray(b);*/
        if (sample > 0) {
            BigDecimal[] num = calcCorCoeffNumSampleBigD(a, b, sample, index);
            BigDecimal denom = calcCorCoeffDenomSampleBigD(a, b, sample, num[1], num[2], index);
            //System.out.println("correlationCoeffSample num/denom " + num[0] + "\t" + denom);
            double denom_val = denom.doubleValue();
            if (num[0].doubleValue() == 0.0 && denom_val == 0.0) {
                System.out.println("correlationCoeffSample failing due to both numerator and denominator = 0. " +
                        "This occurs when the vectors are identical and have all identical entries, setting to 1.0");
                coeff = 1.0;
            } else if (denom_val != 0.0f) {
                num[0] = num[0].divide(denom, scale, BigDecimal.ROUND_HALF_UP);
                coeff = num[0].doubleValue();
                //System.out.println("correlationCoeffSample identical " + identical + "\t" + coeff);
            } else {
                System.out.println("correlationCoeffSample failing due to division by zero; setting coeff to NaN");
                coeff = Double.NaN;
            }
            if (identical && coeff != 1.0) {
                System.out.println("correlationCoeffSample failing for identical case, setting to 1.0; coeff " + coeff + "\tnumerator " + num[0] + "\tdenom " + denom);
                coeff = 1.0;
            }
        }
        return coeff;
    }

    /**
     * Computes the correlation coefficient between two double arrays.
     *
     * @param a
     * @param b
     * @return
     */
    public static double correlationCoeffSample(double[] a, double[] b) {
        double coeff = Double.NaN;
        //System.out.println("correlationCoeff means " + meana + "\t" + meanb);
        ArrayList get = determineCommonSample(a, b);
        int[] index = (int[]) get.get(0);
        //System.out.println("correlationCoefSample common sample");
        //MoreArray.printArray(index);
        double sample = ((Integer) get.get(1)).intValue();
        //System.out.println("correlationCoefSample common sample "+sample);
        /*System.out.println("correlationCoefSample " + sample);
        System.out.println("correlationCoefSample A");
        MoreArray.printArray(a);
        System.out.println("correlationCoefSample B");
        MoreArray.printArray(b);*/
        if (sample > 0) {
            double[] num = calcCorCoeffNumSample(a, b, sample, index);
            double denom = calcCorCoeffDenomSample(a, b, sample, num[1], num[2], index);
            //System.out.println("correlationCoeffSample num/denom " + num[0] + "\t" + denom);
            double meana = avg(a);
            double meanb = avg(b);
            double sda = SD(a, meana);
            double sdb = SD(b, meanb);

            if (num[0] == 0.0 && denom == 0.0) {
                //System.out.println("correlationCoeffSample failing due to both numerator and denominator ("+denom+") = 0.");
                /*System.out.println("a");
                MoreArray.printArray(a);
                System.out.println("b");
                MoreArray.printArray(b);*/
                coeff = Double.NaN;
            } else if (sda == 0.0 || sdb == 0.0) {
                coeff = 1.0;
            } else if (denom == 0.0f) {
                coeff = Double.NaN;
            } else
                coeff = num[0] / denom;

            if (coeff > 1.0) {
                //System.out.println("correlationCoeffSample coeff > 1; setting to 1 " + coeff);
                coeff = 1.0;
            } else if (coeff < -1.0) {
                //System.out.println("correlationCoeffSample coeff < -1; setting to -1 " + coeff);
                coeff = -1.0;
            }
        }
        //System.out.println("coeff "+coeff);
        return coeff;
    }


    /**
     * Computes the correlation coefficient between two series of double stored in an ArrayList as DDouble objects.
     *
     * @param ar
     * @return
     */
    public static double correlationCoeff(ArrayList ar) {
        double[] a = new double[ar.size()];
        double[] b = new double[ar.size()];
        for (int i = 0; i < ar.size(); i++) {
            dtype.DDouble d = (dtype.DDouble) ar.get(i);
            a[i] = d.x;
            b[i] = d.y;
        }

        return correlationCoeffSample(a, b);
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static ArrayList determineCommonSample(double[] a, double[] b) {
        int[] index = null;
        int samp = 0;
        int len = a.length;
        if (len == b.length) {
            index = new int[len];
            for (int i = 0; i < len; i++) {
                if (!Double.isNaN(a[i]) && !Double.isNaN(b[i])) {
                    index[i] = 1;
                    samp++;
                }
            }
        }
        ArrayList ret = new ArrayList();
        ret.add(index);
        ret.add(new Integer(samp));
        return ret;
    }

    /**
     * Adds the specified value to each value in the double array.
     *
     * @param data
     * @param d
     * @return
     */
    public static double[] addVal(double[] data, double d) {
        for (int i = 0; i < data.length; i++)
            data[i] += d;
        return data;
    }


    /**
     * Rounds the double to the specified number of decimal places. Broken when rounding up to an integer (i.e., no decimal places).
     *
     * @param a
     * @param number_of_decimal_places
     * @return
     */
    /*public static double roundUp(double a, int number_of_decimal_places) {
        double deka = 10.0;
        double c = Math.pow(deka, number_of_decimal_places);
        double d = a * c;
        double result1 = java.lang.Math.round(d);
        double the_result;
        the_result = result1 / c;
        //System.out.println("roundUp : input a "+a+"\tnumber_of_decimal_places "+number_of_decimal_places+"\t deka "+deka+"\tc "+c+"\t"+d+"\tresult1 "+result1+"\tthe_result "+the_result);
        return the_result;
    }*/

    /**
     * @param d
     * @param number_of_decimal_places
     * @return
     */
    public static double roundUp(double d, int number_of_decimal_places) {
        String s = "";
        for (int i = 0; i < number_of_decimal_places; i++) {
            s += "#";
        }

        DecimalFormat twoDForm = new DecimalFormat("#." + s);
        return Double.valueOf(twoDForm.format(d));
    }

    /**
     * @param d
     * @param number_of_decimal_places
     * @return
     */
    public static double[] roundUp(double d[], int number_of_decimal_places) {
        String s = "";
        if (number_of_decimal_places > 0)
            s += ".";
        for (int i = 0; i < number_of_decimal_places; i++) {
            s += "#";
        }
        double[] ret = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            DecimalFormat twoDForm = new DecimalFormat("#" + s);
            ret[i] = Double.valueOf(twoDForm.format(d[i]));
        }
        return ret;
    }

    /**
     * Bins the double array values by value.
     *
     * @param d
     * @return
     */
    public static ArrayList countBins(double[] d) {
        ArrayList store = new ArrayList();
        for (int i = 0; i < d.length; i++) {
            int there = checkArray(store, d[i]);
            if (there == -1) {
                IntDouble id = new IntDouble(1, d[i]);
                store.add((IntDouble) id);
            } else {
                IntDouble incr = (IntDouble) store.get(there);
                incr.x++;
                store.set(there, incr);
            }
        }
        return store;
    }

    /**
     * Checks an entry in a double array, used by countBins.
     *
     * @param store
     * @param d
     * @return
     */
    public static int checkArray(ArrayList store, double d) {
        for (int i = 0; i < store.size(); i++) {
            IntDouble cur = (IntDouble) store.get(i);
            if (cur.y == d)
                return i;
        }
        return -1;
    }

    /**
     * Finds the position of the min value of the distribution.
     *
     * @param v
     * @return
     */
    public static int findMinPosDistrib(Vector v) {
        double min = Double.POSITIVE_INFINITY;
        int minpos = -1;
        for (int i = 0; i < v.size(); i++) {
            Distrib d = (Distrib) v.get(i);
            if (d.val < min) {
                min = d.val;
                minpos = i;
            }
        }
        //System.out.println("findMinPosDistrib "+min+"\t"+minpos);
        return minpos;

    }

    /**
     * Finds the position of the min value of the distribution.
     *
     * @param v
     * @return
     */
    public static int findMaxPosDistrib(Vector v) {
        double max = Double.NEGATIVE_INFINITY;
        int maxpos = -1;
        for (int i = 0; i < v.size(); i++) {
            Distrib d = (Distrib) v.get(i);
            if (d.val > max) {
                max = d.val;
                maxpos = i;
            }
        }
        //System.out.println("findMinPosDistrib "+max+"\t"+maxpos);
        return maxpos;

    }

    /**
     * Totals an int[] and normalizes to percent, returning a percent double.
     *
     * @param now  data array
     * @param norm value
     * @return percent of norm value
     */
    public static double totalNormPerc(int[] now, double norm) {
        double ret = 0;
        for (int i = 0; i < now.length; i++) {
            ret += (double) now[i];
        }
        ret = (ret / norm) * (double) 100;
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] divide(double[] a, double[] b) {
        double[] ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i] / b[i];
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] mult(double[] a, double b) {
        double[] ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i] * b;
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @param default_value
     * @return
     */
    public static double[] divideDefaultValue(double[] a, double[] b, double default_value) {
        double[] ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i] / b[i];
            if (Double.isNaN(ret[i]))
                ret[i] = default_value;
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static double[] log(double[] a) {
        double[] ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = Math.log(a[i]);
        }
        return ret;
    }

    /**
     * @param a
     * @param base
     * @return
     */
    public static double[] log(double[] a, double base) {
        double[] ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = Math.log(a[i]) / Math.log(base);
        }
        return ret;
    }

    /**
     * @param a
     * @param base
     * @return
     */
    public static double log(double a, double base) {
        return Math.log(a) / Math.log(base);
    }

    /**
     * @param a
     * @return
     */
    public static double[] sqrt(double[] a) {
        double[] ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = Math.sqrt(a[i]);
        }
        return ret;
    }

    /**
     * @param a
     * @return
     */
    public static double listAVG(ArrayList a) {
        double[] cur = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            cur[i] = ((Double) a.get(i)).doubleValue();
        }
        return avg(cur);
    }

    /**
     * @param a
     * @return
     */
    public static double listSD(ArrayList a, double mean) {
        double[] cur = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            cur[i] = ((Double) a.get(i)).doubleValue();
        }
        return SD(cur, mean);
    }

    /**
     * @param a
     * @return
     */
    public static double[] listArrayAvg(ArrayList[] a) {
        double[] d = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            if (a[i] != null)
                d[i] = listAVG(a[i]);
            else
                d[i] = Double.NaN;
        }
        return d;
    }

    /**
     * @param a
     * @return
     */
    public static double[] listArrayAvg(ArrayList a) {
        double[] d = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            ArrayList list = (ArrayList) a.get(i);
            //double[] test = MoreArray.ArrayListtoDouble(list);
            /*System.out.println("listArrayAvg " + i);
            MoreArray.printArray(test);*/
            if (list != null) {
                d[i] = listAVG(list);
            } else
                d[i] = Double.NaN;
        }
        return d;
    }

    /**
     * @param a
     * @return
     */
    public static double[] listArraySD(ArrayList a, double[] mean) {
        double[] d = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            ArrayList list = (ArrayList) a.get(i);
            if (list != null) {
                d[i] = listSD(list, mean[i]);
            } else
                d[i] = Double.NaN;
        }
        return d;
    }

    /**
     * Calculates the log base 2 odds score.
     *
     * @param data
     * @param bg
     * @return
     */
    public static double arrayLogOddsBits(double[] data, double[] bg) {

        int countbad = 0;
        double d = Double.NaN;
        if (data.length == bg.length) {
            d = 0;
            for (int i = 0; i < data.length; i++) {
                double ratio = data[i] / bg[i];
                double bits = logOddsBits(ratio, bg[i]);

                //System.out.println("arrayLogOddsBits ratio & bits " + i + "\t" + ratio + "\t" + bits);
                if (!Double.isNaN(bits))
                    d += bits;
                else countbad++;
            }
        }

        if (countbad >= data.length / 2.0)
            return Double.NaN;
        else
            return d;
    }

    /**
     * @param is_plus
     * @param data
     * @param bg_plus
     * @param bg_minus
     * @return
     */
    public static double arrayLogOddsBits(boolean[] is_plus, double[] data, double[] bg_plus, double[] bg_minus) {

        int countbad = 0;
        double d = Double.NaN;
        if (data.length == bg_plus.length) {
            d = 0;
            for (int i = 0; i < data.length; i++) {

                double ratio = Double.NaN, bits = Double.NaN;
                if (is_plus[i]) {
                    ratio = data[i] / bg_plus[i];
                    bits = logOddsBits(ratio, bg_plus[i]);
                } else {
                    ratio = data[i] / bg_minus[i];
                    bits = logOddsBits(ratio, bg_minus[i]);
                }

                //System.out.println("arrayLogOddsBits ratio & bits " + i + "\t" + ratio + "\t" + bits);
                if (!Double.isNaN(bits))
                    d += bits;
                else countbad++;
            }
        }

        if (countbad >= data.length / 2.0)
            return Double.NaN;
        else
            return d;
    }

    /**
     * @param data
     * @param bg
     * @return
     */
    public static double logOddsBits(double data, double bg) {
        double ratio = data / bg;
        double bits = (1 / Math.log(2)) * Math.log(ratio);
        return bits;
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public static double euclidean(double[] a, double[] b) {
        double ret = Double.NaN;
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (!Double.isNaN(a[i]) && !Double.isNaN(a[i])) {
                    if (Double.isNaN(ret))
                        ret = 0;
                    double v = (a[i] - b[i]);
                    //System.out.println(a[i]+"\t"+b[i]+"\t"+v);
                    ret += Math.pow(v, 2);//v * v;
                }
            }
            return Math.sqrt(ret);
        }

        return ret;
    }

    /**
     * Returns the number of valid comparisons between the two arrays.
     *
     * @param a
     * @param b
     * @return
     */
    public static double comparisons(double[] a, double[] b) {
        double ret = 0;
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (!Double.isNaN(a[i]) && !Double.isNaN(a[i])) {
                    ret++;
                }
            }
        } else {
            System.out.println("stat.comparisons lengths do not match " + a.length + "\t" + b.length);
        }
        return ret;
    }

    /**
     * @return
     */
    public static boolean testInteger(String s) {
        boolean ret = false;
        try {
            int test = Integer.parseInt(s);
            ret = true;
        } catch (NumberFormatException e) {
        }
        return ret;
    }


    /**
     * Returns an array of natural numbers ordered from start to end (inclusive) and missing the values specified.
     * Array indices start at 0, thus start=0 is the first element fof the array.
     *
     * @param start
     * @param end
     * @param remove
     * @return
     */
    public static int[] createNaturalNumbersRemoved(int start, int end, int[] remove) {
        //int remove_len = remove.length;
        //System.out.println("createNaturalNumbersRemoved Removing " + remove_len);
        //int size = end - start + 1 - remove_len;
        //System.out.println("createNaturalNumbersRemoved size " + size);
        ArrayList store = new ArrayList();
        for (int i = start; i < end; i++) {
            if (!testRemove(remove, i))
                store.add(new Integer(i));
            /*else
              System.out.println("createNaturalNumbersRemoved ommitting " + i);*/
        }
        return MoreArray.ArrayListtoInt(store);
    }

    /**
     * Returns an array of natural numbers ordered from start to end (inclusive)
     * Array indices start at 0, thus start=0 is the first element fof the array.
     *
     * @param start
     * @param end
     * @return
     */
    public static int[] createNaturalNumbers(int start, int end) {
        //int remove_len = remove.length;
        //System.out.println("createNaturalNumbersRemoved Removing " + remove_len);
        //int size = end - start + 1 - remove_len;
        //System.out.println("createNaturalNumbersRemoved size " + size);
        ArrayList store = new ArrayList();
        for (int i = start; i < end; i++) {
            store.add(new Integer(i));
            /*else
              System.out.println("createNaturalNumbersRemoved ommitting " + i);*/
        }
        return MoreArray.ArrayListtoInt(store);
    }

    /**
     * @param ar
     * @param val
     * @return
     */
    public static boolean testRemove(int[] ar, int val) {
        for (int i = 0; i < ar.length; i++) {
            if (ar[i] == val)
                return true;
        }
        return false;
    }

    /**
     * @param d
     * @return
     */
    public static double[] fillRandom(double[] d) {
        Random r = new Random();
        for (int i = 0; i < d.length; i++)
            d[i] = r.nextDouble();
        return d;
    }

    /**
     * @param size
     * @return
     */
    public static double[] fillRandom(int size) {
        double[] ret = new double[size];
        return fillRandom(ret);
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] subtract(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++)
            a[i] -= b[i];
        return a;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static int[] subtract(int[] a, int b) {
        int[] cp = new int[a.length];
        for (int i = 0; i < a.length; i++)
            cp[i] = a[i] - b;
        return cp;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] subtract(double[] a, double b) {
        for (int i = 0; i < a.length; i++)
            a[i] -= b;
        return a;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] subtract(double b, double[] a) {
        for (int i = 0; i < a.length; i++)
            a[i] = b - a[i];
        return a;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] add(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++)
            a[i] += b[i];
        return a;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static double[] add(double[] a, double b) {
        for (int i = 0; i < a.length; i++)
            a[i] += b;
        return a;
    }

    /**
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
     * @param a
     * @param b
     * @return
     */
    public static int[] add(int[] a, int b) {
        int[] cp = new int[a.length];
        for (int i = 0; i < cp.length; i++)
            cp[i] = a[i] + b;
        return cp;
    }

    /**
     *
     */
    public static void testCorrelation() {
        double[] test1 = MoreArray.initArray(10, 1.0);
        double[] test2 = MoreArray.initArray(10, 1.0);
        double correl = correlationCoeff(test1, test2);
        System.out.println("Perfect correlation test for identical series " + correl);
        double[] test3 = MoreArray.initArraySeries(10, 1.0, 1.0);
        double[] test4 = MoreArray.initArraySeries(10, 1.0, 1.0);
        double correl2 = correlationCoeff(test3, test4);
        System.out.println("Perfect correlation test for increasing series " + correl2);
    }

    /**
     * @param e
     * @param o
     * @return
     */
    public static double massSpecPPMError(double e, double o) {
        return (Math.abs(e - o) / e) * 1000000.0;
    }


    /**
     * @param a
     * @param n
     * @return
     */
    public static ArrayList truncateSamplesToTopN(ArrayList a, int n) {
        for (int i = 0; i < a.size(); i++) {
            ArrayList cur = (ArrayList) a.get(i);
            //default sort is asc do need to reverse Collectons.sort result
            Collections.sort(cur);
            cur = MoreArray.reverse(cur);
            //System.out.println("truncateSamplesToTopN " + i + "\t" + a.get(0));
            while (cur.size() > n) {
                cur.remove(cur.size() - 1);
            }
            a.set(i, cur);
        }
        return a;
    }

    /**
     * @param d
     * @param t
     * @return
     */
    public static double[] applyThreshold(double[] d, double t, int modulo, int remainder) {
        for (int i = 0; i < d.length; i++) {
            if (i % modulo == remainder)
                if (d[i] != 0 && d[i] <= t) {
                    //System.out.println("applyThreshold " + i + "\t" + t + "\t" + d[i]);
                    d[i] = 0;
                }
        }
        return d;
    }

    /**
     * @param d
     * @param t
     * @return
     */
    public static double[] applyThreshold(double[] d, double t) {
        for (int i = 0; i < d.length; i++) {
            if (d[i] != 0 && d[i] <= t) {
                //System.out.println("applyThreshold " + i + "\t" + t + "\t" + d[i]);
                d[i] = 0;
            }
        }
        return d;
    }

    /**
     * @param d
     * @param t
     * @return
     */
    public static double[] applyThresholdGreat(double[] d, double t) {
        for (int i = 0; i < d.length; i++) {
            if (d[i] != 0 && d[i] > t) {
                //System.out.println("applyThreshold " + i + "\t" + t + "\t" + d[i]);
                d[i] = 0;
            }
        }
        return d;
    }


    /**
     * @param min
     * @param max
     * @param val
     * @return
     */
    public static double floorAndCeiling(double min, double max, double val) {
        if (val < min)
            val = min;
        else if (val > max)
            val = max;
        return val;
    }


    /**
     * @param d
     * @return
     */
    public static int[] ranksAsc(double[] d) {
        double[] orig = MoreArray.copy(d);
        Arrays.sort(d);
        //MoreArray.printArray(d);
        int[] ranks = new int[d.length];
        for (int i = 0; i < d.length; i++) {
            ranks[i] = MoreArray.getArrayInd(d, orig[i]);
        }
        return ranks;
    }

    /**
     * @param d
     * @return
     */
    public static int[] ranksDesc(double[] d, boolean debug) {
        double[] sorted = MoreArray.copy(d);
        Integer integer1 = new Integer(1);
        Arrays.sort(sorted);
        ArrayList ar = MoreArray.convtoArrayList(sorted);
        Collections.reverse(ar);
        sorted = MoreArray.ArrayListtoDouble(ar);
        //System.out.println("ranksDesc");
        //MoreArray.printArray(d);
        int[] ranks = new int[sorted.length];
        HashMap h = new HashMap();
        for (int i = 0; i < sorted.length; i++) {
            int i1 = MoreArray.getArrayInd(sorted, d[i]);
            //System.out.println("ranksDesc "+i+"\trank "+i1+"\tval "+d[i]);
            ranks[i] = i1;
            Integer integer = new Integer(i1);
            if (h.get(integer) == null) {
                h.put(integer, integer1);
            } else if (debug)
                System.out.println("ranksDesc ties pos " + i + "\trank " + i1 + "\tval " + d[i]);
        }
        return ranks;
    }

    /**
     * @param d
     * @return
     */
    public static int[] ranksDescUnique(double[] d) {
        Integer integer1 = new Integer(1);

        double[] sorted = MoreArray.copy(d);
        //replacing NaN with v low value to sort
        sorted = stat.replace(sorted, Double.NaN, -1000000000000000000000.0);
        Arrays.sort(sorted);
        ArrayList ar = MoreArray.convtoArrayList(sorted);
        Collections.reverse(ar);
        sorted = MoreArray.ArrayListtoDouble(ar);
        //System.out.println("ranksDesc");
        //MoreArray.printArray(d);
        //System.out.println("ranksDesc sorted");
        //MoreArray.printArray(sorted);

        sorted = stat.replace(sorted, -1000000000000000000000.0, Double.NaN);

        int[] ranks = new int[sorted.length];
        HashMap h = new HashMap();
        int[] done = new int[sorted.length];

        for (int i = 0; i < sorted.length; i++) {
            //if (!Double.isNaN(d[i])) {
            int i1 = MoreArray.getArrayIndUnique(sorted, d[i], done);
            if (i1 == -1) {
                //System.out.println("ranksDesc == -1 " + i + "\t" + i1 + "\td[i] " + d[i]);
                //MoreArray.printArray(sorted);
            } else {
                done[i1] = 1;
                ranks[i] = i1;
                Integer integer = new Integer(i1);
                if (h.get(integer) == null) {
                    h.put(integer, integer1);
                } else {
                    //System.out.println("ranksDesc ties pos " + i + "\trank " + i1 + "\tval " + d[i]);
                    boolean placed = false;
                    int i2 = i1 + 1;
                    while (!placed) {
                        Integer teg = new Integer(i2);
                        if (h.get(teg) == null) {
                            done[i2] = 1;
                            ranks[i] = i2;
                            break;
                        } else
                            i2 = i2 + 1;
                    }
                }
            }
            /*} else {
                System.out.println("ranksDesc ignoring " + i + "\td[i] " + d[i]);
                ranks[i] = i;
                done[i] = 1;
                if (h.get(i) == null) {
                    h.put(i, i);
                }
            }*/
        }
        return ranks;
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public static int Hamming(int[] a, int[] b) {
        int ret = 0;
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i])
                    ret++;
            }
        } else {
            System.out.println("Hamming array lengths do not match " + a.length + "\t" + b.length);
        }
        //System.out.println("Hamming " + a.length + "\t" + b.length + "\t" + ret);
        return ret;
    }

    /**
     * @param data
     */
    public static double meanPairHamming(int[][] data) {
        ArrayList vals = new ArrayList();
        for (int i = 0; i < data.length; i++) {
            for (int j = i + 1; j < data.length; j++) {
                vals.add(Hamming(data[i], data[j]));
            }
        }
        int[] dists = MoreArray.ArrayListtoInt(vals);
        //System.out.println("meanPairHamming " + dists.length + "\t" + data.length);
        //System.out.println("meanPairHamming " + MoreArray.toString(dists, ","));
        double v = avg(dists);
        //System.out.println("meanPairHamming " + v + "\t" + dists[0] + "\t" + dists[1]);
        return v;
    }


    /**
     * @param a
     * @param find
     * @param replace
     * @return
     */
    public static double[] replace(double[] a, double find, double replace) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == find || (Double.isNaN(find) && Double.isNaN(a[i])))
                a[i] = replace;
        }
        return a;
    }

    /**
     * @param a
     * @return
     */
    public static double IQR_BROKEN(double[] a) {
        Arrays.sort(a);
        double[] quantiles = getQuantile_BROKEN(a);
        MoreArray.printArray(quantiles);

        double q1val = getQuantileVal_BROKEN(a, 0.25);
        double q3val = getQuantileVal_BROKEN(a, 0.75);

        double v = q3val - q1val;
        System.out.println("stat.IQR q1val " + q1val);
        System.out.println("stat.IQR q3val " + q3val);
        System.out.println("stat.IQR " + v);
        return v;
    }


    /**
     * UNTESTED
     *
     * @param d
     * @return
     */
    private static double[] getQuantile_BROKEN(double[] d) {
        double n = d.length;
        double[] q = new double[(int) n];
        for (int i = 0; i < d.length; i++) {
            q[i] = (i - 1.0) / (n - 1.0);
        }

        return q;
    }

    /**
     * @param a
     * @param quantile
     * @return
     */
    private static double getQuantileVal_BROKEN(double[] a, double quantile) {
        double q1pos = quantile * (double) a.length;
        double q1rem = q1pos - (int) q1pos;
        double q1val = Double.NaN;

        //if exactly the same take the average of two adjacent values
        if (q1rem == 0.0) {
            int i = (int) q1pos;
            //System.out.println("getQuantileVal " +q1rem+"\t"+a[i]+"\t"+a[i+1]);
            System.out.println("getQuantileVal no remainder quantile " + quantile);
            System.out.println("getQuantileVal no remainder q1pos " + q1pos);
            System.out.println("getQuantileVal no remainder q1rem " + q1rem);
            System.out.println("getQuantileVal no remainder i, a.length " + i + "\t" + a.length);
            q1val = stat.avg(a[i], a[i + 1]);
            System.out.println("getQuantileVal no remainder q1val " + q1val);
        } else {
            int i = (int) Math.ceil(q1pos);
            System.out.println("getQuantileVal remainder quantile " + quantile);
            System.out.println("getQuantileVal remainder q1pos " + q1pos);
            System.out.println("getQuantileVal remainder q1rem " + q1rem);
            System.out.println("getQuantileVal remainder i, a.length " + i + "\t" + a.length);
            q1val = a[i];
            System.out.println("getQuantileVal remainder q1val " + q1val);
        }
        return q1val;
    }


    /**
     * Computes genometric mean for N=2
     *
     * @param a
     * @param b
     * @return
     */
    public static double geometricMeanN2(double a, double b) {
        return 2 * (a * b) / (a + b);
    }


    /**
     * Computes the Fischer exact test.
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    public static double FischerExact(int a, int b, int c, int d) {
        int sum = a + b + c + d;
        System.out.println("fisher " + (a + b) + "\t" + factorial(a + b));
        System.out.println("fisher " + (c + d) + "\t" + factorial(c + d));
        System.out.println("fisher " + (a + c) + "\t" + factorial(a + c));
        System.out.println("fisher " + (b + d) + "\t" + factorial(b + d));
        double numerator = factorial(a + b) * factorial(c + d) * factorial(a + c) * factorial(b + d);
        double denom = factorial(a) * factorial(b) * factorial(c) * factorial(d) * factorial(sum);
        System.out.println("fisher " + numerator + "\t" + denom);
        return numerator / denom;
    }

    /**
     * @param n
     * @return
     */
    public static int factorial(int n) {
        int ret = 1;
        if (n > 1)
            ret = n * factorial(n - 1);
        System.out.println("factorial " + n + "\t" + ret);
        return ret;
    }


    /**
     * @param d
     * @param cut
     * @param replace
     * @return
     */
    public static double[] applyCut(double[] d, double cut, double replace) {
        for (int i = 0; i < d.length; i++) {
            if (d[i] >= 0.01)
                d[i] = replace;
        }
        return d;
    }


    /**
     * Returns the abs value of the double array.
     *
     * @param d
     * @return
     */
    public static double[] abs(double[] d) {
        for (int i = 0; i < d.length; i++) {
            if (!Double.isNaN(d[i]))
                d[i] = Math.abs(d[i]);
        }
        return d;
    }
}
