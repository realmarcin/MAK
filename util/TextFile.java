package util;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Marcin
 * Date: Aug 26, 2005
 * Time: 3:36:15 PM
 */
public class TextFile {


    public final static String read(String f) {
        String ret = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            while (line != null) {
                ret += line;
                line = in.readLine();
            }
            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return ret;
    }

    /**
     * Reads the file into an ArrayList, one String object per line.
     *
     * @param f
     * @return ArrayList with rows from text files as entries
     */
    public final static ArrayList readtoList(String f) {
        ArrayList ret = new ArrayList();
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            while (line != null) {
                ret.add((String) line);
//System.out.println(c+"\t"+line);
                line = in.readLine();
            }
            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return ret;
    }

    /**
     * @param f
     * @param parse
     * @return
     */
    public final static ArrayList readtoList(String f, String parse) {
        ArrayList ret = new ArrayList();
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            while (line != null) {
                ret.add(line.split(parse));
//System.out.println(c+"\t"+line);
                line = in.readLine();
            }
            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return ret;
    }

    /**
     * Reads the URL into an ArrayList, one String object per line.
     *
     * @param readurl
     * @return ArrayList with rows from given URL as entries
     */
    public static ArrayList readtoListfromURL(URL readurl) throws Exception {
        ArrayList ret = new ArrayList();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        readurl.openStream()));
        String line = in.readLine();
        while (line != null) {
            ret.add((String) line);
//System.out.println(c+"\t"+line);
            line = in.readLine();
        }
        in.close();
        return ret;
    }

    /**
     * @param f
     * @return String[] array rows from text file as elements
     */
    public final static String[] readtoArray(String f) {
        ArrayList data = new ArrayList();
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            while (line != null) {
                //System.out.println("loaded "+line);
                data.add((String) line);
                line = in.readLine();
            }
            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return MoreArray.convtoString(data);
    }

    /**
     * @param data
     * @param outpath
     */
    public final static void write(String data, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath));
            out.print(data);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }


    /**
     * Writes the text values of an ArrayList, one text value per Array element, to a text file.
     *
     * @param data
     * @param outpath
     */
    public final static void write(ArrayList data, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            for (int j = 0; j < data.size(); j++) {
                try {
                    //System.out.println((String) data.get(j));
                    out.println((String) data.get(j));
                } catch (Exception e) {
                    try {
                        out.println(MoreArray.toString((String[]) data.get(j)));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Writes the text values of an ArrayList, one text value per Array element, to a text file.
     *
     * @param data
     * @param outpath
     */
    public final static void write(ArrayList data, String outpath, String delim) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            for (int j = 0; j < data.size(); j++) {
                try {
                    out.println((String) data.get(j));
                } catch (Exception e) {
                    try {
                        out.println(MoreArray.toString((String[]) data.get(j), delim));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Writes the text values of an ArrayList, one text value per Array element, to a text file.
     *
     * @param data
     * @param outpath
     */
    public final static void write(ArrayList data, int index, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            for (int j = 0; j < data.size(); j++) {
                out.println(((String[]) data.get(j))[index]);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param data
     * @param outpath
     */
    public final static void writeObject(ArrayList data, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            for (int j = 0; j < data.size(); j++) {
                out.println(((Object) data.get(j)).toString());
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param data
     * @param outpath
     */
    public final static void writeObject(ArrayList data, String[] xlabels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            out.println(MoreArray.toString(xlabels, "\t"));
            for (int j = 0; j < data.size(); j++) {
                out.println(((Object) data.get(j)).toString());
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Writes the text values of an ArrayList, one text value per Array element, along with the corresponding text label to a text file.
     *
     * @param data
     * @param labels
     * @param outpath
     */
    public final static void write(ArrayList data, String[] labels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);

            String start = "";
            int j = 0;

            for (j = 0; j < labels.length - 1; j++) {

                start += (labels[j] + "\t");
            }
            start += labels[j] + "\n";

            out.println(start);

            int max = findMaxLenTwo(data);
            for (int i = 0; i < max; i++) {
                for (int k = 0; k < data.size(); k++) {

                    String[][] curdata = (String[][]) (data.get(k));

                    try {
                        if (k < data.size() - 1)
                            out.print(curdata[i][0] + "\t" + curdata[i][1] + "\t" + curdata[i][2] + "\t");
                        else
                            out.print(curdata[i][0] + "\t" + curdata[i][1] + "\t" + curdata[i][2] + "\n");
                    } catch (Exception e) {

                        if (k < data.size() - 1)
                            out.print("" + "\t" + "" + "\t" + "" + "\t");
                        else
                            out.print("" + "\t" + "" + "\t" + "" + "\n");

                    }
                }
            }

            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Writes the text values of an ArrayList, one text value per Array element, along with the corresponding text label to a text file.
     *
     * @param data
     * @param xlabels
     * @param ylabels
     * @param outpath
     */
    public final static void writeintArray(ArrayList data, String[] xlabels, String[] ylabels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            if (xlabels != null)
                for (j = 0; j < xlabels.length - 1; j++) {
                    start += (xlabels[j] + "\t");
                }
            start += xlabels[j] + "\n";
            out.print(start);
            for (int k = 0; k < data.size(); k++) {
                if (ylabels != null)
                    out.print(ylabels[k] + "\t");
                int[] curdata = (int[]) (data.get(k));
                String curstr = MoreArray.toString(curdata);
                out.println(curstr);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param data
     * @param xlabels
     * @param ylabels
     * @param outpath
     */
    public final static void writedoubleArray(ArrayList data, String[] xlabels, String[] ylabels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            if (xlabels != null) {
                start += "\t";
                for (j = 0; j < xlabels.length - 1; j++) {
                    start += (xlabels[j] + "\t");
                }
            }
            start += xlabels[j] + "\n";
            out.print(start);
            for (int k = 0; k < data.size(); k++) {
                if (ylabels != null)
                    out.print(ylabels[k] + "\t");
                double[] curdata = (double[]) (data.get(k));
                String curstr = MoreArray.toString(curdata);
                out.println(curstr);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param data
     * @param xlabels
     * @param outpath
     */
    public final static void writeStringArray(ArrayList data, String[] xlabels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            if (xlabels != null) {
                for (j = 0; j < xlabels.length - 1; j++) {
                    start += (xlabels[j] + "\t");
                }
                start += xlabels[j] + "\n";
                out.print(start);
            }

            for (int k = 0; k < data.size(); k++) {
                String[] curdata = (String[]) (data.get(k));

                for (int l = 0; l < curdata.length; l++) {
                    if (l < curdata.length - 1)
                        out.print(curdata[l] + "\t");
                    else
                        out.print(curdata[l] + "\n");
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param data
     * @param xlabels
     * @param ylabels
     * @param outpath
     */
    public final static void writeStringArray(ArrayList data, String[] xlabels, String[] ylabels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            if (xlabels != null)
                for (j = 0; j < xlabels.length - 1; j++) {
                    start += (xlabels[j] + "\t");
                }
            start += xlabels[j] + "\n";
            out.print(start);
            for (int k = 0; k < data.size(); k++) {
                String[] curdata = (String[]) (data.get(k));
                if (ylabels != null)
                    out.print(ylabels[k] + "\t");
                for (int l = 0; l < curdata.length; l++) {
                    if (l < curdata.length - 1)
                        out.print(curdata[l] + "\t");
                    else
                        out.print(curdata[l] + "\n");
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Writes the text values of an ArrayList, one text value per Array element, along with the corresponding text label to a text file.
     *
     * @param data
     * @param labels
     * @param outpath
     */
    public final static void writeSingleStringArray(ArrayList data, String[] labels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            for (j = 0; j < labels.length - 1; j++) {
                start += (labels[j] + "\t");
            }
            start += labels[j] + "\n";
            out.print(start);
            for (int k = 0; k < data.size(); k++) {
                String curdata = (String) (data.get(k));
                out.print(curdata + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Writes the text values of an ArrayList, one text value per Array element, along with the corresponding text label to a text file.
     *
     * @param data
     * @param xlabels
     * @param ylabels
     * @param outpath
     */
    public final static void writeSingleStringArray(ArrayList data, String[] xlabels, String[] ylabels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            if (xlabels != null) {
                for (j = 0; j < xlabels.length - 1; j++) {
                    start += (xlabels[j] + "\t");
                }
                start += xlabels[j] + "\n";
            }
            out.print(start);
            for (int k = 0; k < data.size(); k++) {
                String curdata = (String) (data.get(k));
                out.print(ylabels[k] + "\t" + curdata + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Writes the double values of double[] array elements in an ArrayList along with the corresponding text label to a text file.
     *
     * @param data
     * @param labels
     * @param outpath
     */
    public final static void writedoubleList(ArrayList data, String[] labels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            for (j = 0; j < labels.length - 1; j++) {
                start += (labels[j] + "\t");
            }
            start += labels[j] + "\n";
            out.println(start);
            int max = findMaxLenThree(data);
            for (int i = 0; i < max; i++) {
                for (int k = 0; k < data.size(); k++) {
                    double[] curdata = (double[]) (data.get(k));
                    try {
                        if (k < data.size() - 1)
                            out.print(curdata[i] + "\t");
                        else
                            out.print(curdata[i] + "\n");
                    } catch (Exception e) {
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * ?
     *
     * @param n
     * @return not sure
     */
    public final static int findMaxLenThree(ArrayList n) {
        int ret = -1;
        for (int k = 0; k < n.size(); k++) {
            double[] cur = (double[]) (n.get(k));
            if (cur.length > ret)
                ret = cur.length;
        }
        return ret;
    }

    /**
     * ?
     *
     * @param n
     * @return not sure
     */
    public final static int findMaxLenTwo(ArrayList n) {
        int ret = -1;
        for (int k = 0; k < n.size(); k++) {
            String[][] cur = (String[][]) (n.get(k));
            if (cur.length > ret)
                ret = cur.length;
        }
        return ret;
    }

    /**
     * @param data
     * @param outpath
     */
    public final static void write(ArrayList[] data, String[] labels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            for (j = 0; j < labels.length - 1; j++) {

                start += (labels[j] + "\t");
            }
            start += labels[j];

            out.println(start);

            int max = findMaxlen(data);

            for (int i = 0; i < max; i++) {
                System.out.println("TextFile write " + i);
                int k = 0;
                for (k = 0; k < data.length; k++) {

                    String curdata = null;
                    System.out.println("TextFile write " + i + "\t" + k);

                    try {
                        String[] cur = (String[]) (data[j].get(0));
                        curdata = cur[max];
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    if (curdata == null)
                        curdata = "";
                    if (k == data.length - 1)
                        out.print(curdata + "\n");
                    else
                        out.print(curdata + "\t");
                }
                out.print("\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }


    /**
     * @param data
     * @return maximum length row
     */
    public final static int findMaxlen(ArrayList[] data) {
        int max = findMaxlen(data);
        for (int i = 0; i < data.length; i++) {
            String cur = ((String[]) data[i].get(0))[0];
            int size = cur.length();
            if (size > max)
                max = size;
        }
        return max;
    }

    /**
     * @param data
     * @param outpath
     */
    public final static void write(String[] data, String outpath) {
        write(data, null, outpath);
    }

    /**
     * @param data
     * @param header
     * @param outpath
     */
    public final static void write(String[] data, String header, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            if (header != null)
                out.println(header);
            for (int i = 0; i < data.length; i++) {
                out.println(data[i]);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param data
     * @param outpath
     */
    public final static void write(String[][] data, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            for (int i = 0; i < data.length; i++) {
                int j = 0;
                String now = "";
                /*System.out.println("TextFile write " + i);
                MoreArray.printArray(data[i]);*/
                for (j = 0; j < data[i].length; j++) {
                    if (j < data[0].length - 1)
                        now += (data[i][j] + "\t");
                    else
                        now += (data[i][j]);
                }
                //System.out.println("TextFile write " + i + ":" + now + ":");
                out.println(now);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param data
     * @param labels
     * @param outpath
     */
    public final static void write(String[][] data, String[] labels, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            String start = "";
            int j = 0;
            for (j = 0; j < labels.length - 1; j++) {
                start += (labels[j] + " ");
            }
            start += labels[j];
            out.println(start);
            for (int i = 0; i < data.length; i++) {
                int k = 0;
                String now = "";
                for (k = 0; k < data[0].length - 1; k++) {
                    now += (data[i][k] + " ");
                }
                now += (data[i][k]);
                out.println(now);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }


    /**
     * @param data
     * @param xlabels
     * @param ylabels
     * @param outpath
     */
    public final static void write(String[][] data, String[] xlabels, String[] ylabels, String outpath) {
        final String tab = "\t";
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            StringBuffer start = new StringBuffer("\t");
            int j = 0;
            for (j = 0; j < data[0].length - 1; j++) {
                if (xlabels != null) {
                    start.append(xlabels[j]);
                    start.append(tab);
                } else
                    start.append(tab);
            }
            if (xlabels != null)
                start.append(xlabels[j]);

            out.println(start);
            for (int i = 0; i < data.length; i++) {
                int k = 0;
                StringBuffer now = new StringBuffer("");
                if (ylabels != null) {
                    now.append(ylabels[i]);
                    now.append(tab);
                } else
                    now.append(tab);
                for (k = 0; k < data[0].length - 1; k++) {
                    now.append(data[i][k]);
                    now.append(tab);
                }
                now.append(data[i][k]);
                out.println(now);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param data
     * @param outpath
     * @param a
     * @param b
     */
    public final static void write(String[][] data, String outpath, String[] a, String[] b) {
        String[] labels = makeLabels(a, b);
        write(data, labels, outpath);
    }


    /**
     * @param models
     * @param serieslabels
     * @return
     */
    private static String[] makeLabels(String[] models, String[] serieslabels) {
        String[] labels = new String[1 + models.length * serieslabels.length];
        for (int i = 0; i < models.length; i++) {
            for (int j = 0; j < serieslabels.length; j++) {
                labels[i * serieslabels.length + j] = models[i] + "_" + serieslabels[j];
            }
        }
        return labels;
    }
}
