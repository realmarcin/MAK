package util;

/**
 * Rotates a tab delimited file.
 * <p/>
 * User: marcinjoachimiak
 * Date: May 20, 2007
 * Time: 2:04:54 PM
 */
public class RotateFile {

    String infile, outfile;
    int degrees = 90;

    public RotateFile(String[] args) {
        infile = args[0];
        outfile = args[1];
        String[][] read = TabFile.readtoArraytrimR(infile);
        if (args.length == 3) {
            degrees = Integer.parseInt(args[2]);
        }
        /*System.out.println("a/f readtoArray ");
        MoreArray.printArray(read[0]);
        MoreArray.printArray(read[10]);
        MoreArray.printArray(read[1000]);*/
        //TabFile.write(read, infile + "_testread.txt");
        String[][] read2 = MoreArray.rotateMatrix90MoveRowLabels(read);
        if (degrees >= 180)
            read2 = MoreArray.rotateMatrix90MoveRowLabels(read2);
        if (degrees == 270)
            read2 = MoreArray.rotateMatrix90MoveRowLabels(read2);
        TabFile.write(read2, outfile);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2 || args.length == 3) {
            RotateFile ce = new RotateFile(args);
        } else {
            System.out.println("usage: java util.RotateFile\n" +
                    "<infile>\n" +
                    "<outfile>\n" +
                    "<degrees (optional) 90,180,270>");
        }
    }
}


