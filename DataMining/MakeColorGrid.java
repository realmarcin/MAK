package DataMining;

//import JColorGrid.JColorGridConfig;

import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.Program;

import java.io.File;
import java.util.HashMap;

/**
 * User: marcin
 * Date: Mar 31, 2008
 * Time: 6:33:56 PM
 */
public class MakeColorGrid extends Program {

    String[] valid_args = {"-inputdir", "-dataset", "-jcgparam"};
    MapArgOptions map;
    HashMap options;

    SimpleMatrix expr_matrix;

    /**
     *
     */
    public MakeColorGrid(String[] args) {
        //args = MapArgOptions.compactArgs(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        String inputdir = (String) options.get("-inputdir");
        String dataset = (String) options.get("-dataset");
        String jcgparam = (String) options.get("-jcgparam");

        /* String workingdir = System.getProperty("user.dir");
        File dirtest = new File(workingdir);
        String[] filestest = dirtest.list();
        System.out.println("dirtest " + dirtest);
        MoreArray.printArray(filestest);*/

        String indir_str = inputdir;//workingdir +this.sysRes.path_separator +
        System.out.println("inputdir " + indir_str);
        File dir = new File(indir_str);
        String[] files = dir.list();

        expr_matrix = new SimpleMatrix(dataset);
        System.out.println("jcgparam " + jcgparam);
        String infile = inputdir + sysRes.file_separator + files[0];
        System.out.println("infile " + infile);
        String[] strings = {infile, jcgparam};

        /*JColorGrid.JColorGridConfig jcgc = new JColorGridConfig();
        jcgc.read(jcgparam);
        jcgc.infile = infile;
        jcgc.outfile = inputdir + sysRes.file_separator + files[0] + ".eps";
        JColorGrid.JColorGrid jcg = new JColorGrid.JColorGrid();
        jcg.app = false;
        jcg.cgc = jcgc;
        for (int i = 0; i < files.length; i++) {
            String in = inputdir + sysRes.file_separator + files[i];
            String out = inputdir + sysRes.file_separator + files[i] + ".eps";            
            jcg.updateInOutFiles(in, out);
            System.out.println("doing " + i + "\t" + jcg.cgc.infile);
            jcg.run();
            jcg.cgp.writecanEPS(out);
        }*/

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6) {
            MakeColorGrid mcg = new MakeColorGrid(args);
        } else {
            System.out.println("syntax: java DataMining.MakeColorGrid\n" +
                    "<-inputdir dir JColorGrid input files'>\n" +
                    "<-dataset labeled expr. data set>\n" +
                    "<-jcgparam parameters file for JColorGrid>"
            );
            System.out.println("Note: accepts only absolute paths");
        }
    }
}
