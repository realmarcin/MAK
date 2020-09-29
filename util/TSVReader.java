package util;


import java.io.*;
import java.util.Scanner;
/**
 * User: marcin
 * Date: 7/6/20
 * Time: 5:39 PM
 */


/**
 * Reader for the tab separated values format (a basic table format without escapings or anything where the rows are separated by tabulators).*
 */
public class TSVReader implements Closeable {
    final Scanner in;
    String peekLine = null;

    public TSVReader(InputStream stream) throws FileNotFoundException {
        in = new Scanner(stream);
    }

    /**
     * Constructs a new TSVReader which produces values scanned from the specified input stream.
     */
    public TSVReader(File f) throws FileNotFoundException {
        in = new Scanner(f);
    }

    /**
     * @return
     */
    public boolean hasNextTokens() {
        if (peekLine != null) return true;
        if (!in.hasNextLine()) {
            return false;
        }
        String line = in.nextLine().trim();
        if (line.isEmpty()) {
            return hasNextTokens();
        }
        this.peekLine = line;
        return true;
    }

    /**
     * @return
     */
    public String[] nextTokens() {
        if (!hasNextTokens()) return null;
        String[] tokens = peekLine.split("[\\s\t]+");
//      System.out.println(Arrays.toString(tokens));
        peekLine = null;
        return tokens;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
