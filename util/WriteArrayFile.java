package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 */
public class WriteArrayFile {

    /**
     * @param d
     * @param outpath
     */
    public final static void write(int[] d, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            for (int i = 0; i < d.length; i++)
                out.println(i + "\t" + d[i]);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }

    }

    /**
     * @param d
     * @param outpath
     */
    public final static void write(long[] d, String outpath) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);
            for (int i = 0; i < d.length; i++)
                out.println(i + "\t" + d[i]);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * @param d
     * @param outpath
     */
    public final static void write(double[][] d, String outpath, String[] labels) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath));
            if (labels!=null) {
                for (int i = 0; i < labels.length; i++) {
                    out.print("\t" + labels[i]);
                }
                out.print("\n");
            }
            for (int i = 0; i < d.length; i++) {
                if (labels!=null) {
                    out.print(labels[i] + "\t");
                }
                for (int j = 0; j < d.length; j++) {
                    if (j < d.length - 1)
                        out.print(d[i][j] + "\t");
                    else
                        out.print(d[i][j] + "\n");
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
   }

    /**
     * @param al
     * @param outpath
     */
    public final static void writeListInt(ArrayList al, String outpath) {//, String[] label) {
        int[] first = (int[]) al.get(0);
        int all = first.length;
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath));
            /*
            for(int i=0;i<all;i++) {
                out.print(i+"\t"+label[i]);
            }
            */
            for (int i = 0; i < all; i++) {
                //out.print(i+"\t"+label[i]);
                for (int j = 0; j < al.size(); j++) {
                    int[] d = (int[]) al.get(j);
                    out.print(i + "\t" + d[i]);
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
      * @param al
      * @param outpath
      */
     public final static void writeList(ArrayList al, String outpath) {//, String[] label) {
         int all = al.size();
         try {
             PrintWriter out = new PrintWriter(new FileWriter(outpath));
                 for (int j = 0; j < al.size(); j++) {
                     out.print((String)al.get(j)+"\n");
                 }
             out.close();
         } catch (IOException e) {
             System.out.println("Error creating or writing file " + outpath);
             System.out.println("IOException: " + e.getMessage());
         }
    }

    /**
     * @param al
     * @param labels
     * @param outpath
     */
    public final static void writeList(ArrayList al, String[] labels, String outpath) {
        int all = labels.length;
        int maxlen = ((int[]) al.get(1)).length;
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath));
            for (int i = 0; i < all; i++) {
                if (i < all - 1)
                    out.print(labels[i] + "\t");
                else
                    out.print(labels[i] + "\n");
            }
            for (int i = 0; i < maxlen; i++) {
                for (int j = 0; j < al.size(); j++) {
                    int[] d = (int[]) al.get(j);
                    if (j < al.size() - 1)
                        out.print(d[i] + "\t");
                    else
                        out.print(d[i] + "\n");
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }

}
