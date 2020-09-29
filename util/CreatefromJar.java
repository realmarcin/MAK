package util;

import java.io.*;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Creates example data from the JAR.
 */
public class CreatefromJar {


    /**
     * Loads the demo data from the available jevtrace.jar file.
     */
    public final static InputStream createStream(String path, ArrayList files, PrintWriter pw) {

        InputStream str = null;

        try {

            JarFile jf = null;

            try {

                jf = new JarFile(path);
            } catch (Exception e) {
                //System.out.println("Failed to access " + path);
                pw.println("Failed to access " + path);
                pw.println("Cur dir " + (new File("")).getAbsolutePath());
                pw.println(e.getMessage());
            }

            if (jf != null) {
                ClassLoader cl = ClassLoader.getSystemClassLoader();
                str = cl.getResourceAsStream(path);
            }

        } catch (Exception e) {
        }
        return str;
    }

    /**
     * Loads the demo data from the available jevtrace.jar file.
     */
    public final static void createFile(String path, ArrayList files, PrintWriter pw) {

        try {

            JarFile jf = null;

            try {

                jf = new JarFile(path);
            } catch (Exception e) {
                //System.out.println("Failed to access " + path);
                pw.println("Failed to access " + path);
                pw.println("Cur dir " + (new File("")).getAbsolutePath());
                pw.println(e.getMessage());
            }

            if (jf != null) {

                if (pw != null)
                    pw.println("Accessed JAR file.");
                String[] load = new String[files.size()];

                for (int i = 0; i < files.size(); i++) {

                    load[i] = (String) files.get(i);
                }


                for (int i = 0; i < files.size(); i++) {

                    File testaln = new File(load[i]);

                    if (!testaln.exists()) {

                        String jarentryfilea = load[i];
                        //System.out.println("looking for jar entry " + jarentryfilea);
                        if (pw != null)
                            pw.println("looking for jar entry " + jarentryfilea);

                        if (pw != null)
                            pw.println("Chop " + load[i].lastIndexOf("/") + "\t" + load[i].length());
                        String outf = load[i].substring(load[i].lastIndexOf("/") + 1, load[i].length());

                        JarEntry entrya = jf.getJarEntry(jarentryfilea);

                        if (entrya == null) {
                            pw.println("jar entry is null");
                        } else {

                            if (pw != null) {
                                pw.println("compressed size " + entrya.getCompressedSize());
                                pw.println("size " + entrya.getSize());
                            }

                            try {

                                BufferedInputStream isa = new BufferedInputStream(jf.getInputStream(entrya));

                                BufferedReader ina = new BufferedReader(new InputStreamReader(isa));

                                try {

                                    PrintWriter out = new PrintWriter(new FileWriter(outf), true);
                                    String r = ina.readLine();
                                    if (pw != null)
                                        pw.println("first line " + r);
                                    while (r != null) {

                                        if (pw != null)
                                            pw.println("writing " + r);
                                        out.println(r);
                                        r = ina.readLine();
                                    }
                                    out.close();
                                } catch (IOException e) {

                                    if (pw != null) {
                                        pw.println("error creating or writing file " + outf);
                                        pw.println("IOException: " + e.getMessage());
                                    }
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }

                            if (testaln.exists()) {
                                if (pw != null)
                                    pw.println("Succesfully wrote " + outf);
                            } else {
                                if (pw != null)
                                    pw.println("Failed to write " + outf);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
