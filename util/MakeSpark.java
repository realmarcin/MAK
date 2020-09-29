package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * User: marcin
 * Date: Jun 8, 2009
 * Time: 8:23:08 PM
 */
public class MakeSpark {

    String[][] data;
    String[] arg_options = {"-data", "-out", "-link"};
    String outfile;
    String link;

    /**
     *
     */
    public MakeSpark(String[] args) {
        initOptions(args);
        if (outfile != null)
            writeFile();
        else
            writeStdOut();
    }

    /**
     *
     */
    private void writeStdOut() {
        //pw.println("<html>");
        //pw.println("<head>");
        //pw.println("<title>MicrobesOnline: gene expression sparklines</title>");
        //pw.println("<link rev=\"made\" href=\"mailto:MJoachimiak%40lbl.gov\" />");
        //pw.println("<meta name=\"keywords\" content=\"microarray prokaryote bacteria genomic functional\" />");
        //pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
        //System.out.println("<script src=\"http://www.google.com/jsapi\" type=\"text/JAVASCRIPT\"> </script>");
        //System.out.println("<script type=\"text/javascript\">                                    ");
        System.out.println("google.load(\"visualization\", \"1\", {packages:[\"imagesparkline\"]});");
        System.out.println("google.setOnLoadCallback(drawChart);");

        System.out.println("function drawChart() {");
        System.out.println("var data = new google.visualization.DataTable();");
        for (int j = 1; j < data[0].length; j++) {
            //System.out.println("b/f " + data[0][j]);
            data[0][j] = StringUtil.replace(data[0][j], "\"", "");
            //System.out.println("a/f " + data[0][j]);
            System.out.println("data.addColumn(\"number\", \"" + data[0][j] + "\")");
        }
        System.out.println("data.addRows(" + (data.length - 1) + ");");

        for (int j = 1; j < data[0].length; j++) {
            for (int i = 1; i < data.length; i++) {
                if (!data[i][j].equals("NaN"))
                    System.out.println("data.setValue(" + (i - 1) + "," + (j - 1) + "," + data[i][j] + ");");
            }
        }

        String maxs = "5";
        String mins = "-5";
        for (int j = 1; j < data[0].length; j++) {
            maxs += ",5";
            mins += ",-5";
        }
        System.out.println("var chart = new google.visualization.ImageSparkLine(document.getElementById('chart_div'));");
        System.out.println("chart.draw(data, {max:(" + maxs + "), mins:(" + mins + "), width: 500, height: 150, showAxisLines: true, showValueLabels: false, labelPosition: 'left'});");
        System.out.println("}");

        //System.out.println("</script>");
    }

    /**
     */
    private void writeFile() {
        File out = new File(outfile);
        try {
            PrintWriter pw = new PrintWriter(out);

            pw.println("<html>");
            pw.println("<head>");
            pw.println("<title>MicrobesOnline: gene expression sparklines</title>");
            pw.println("<link rev=\"made\" href=\"mailto:MJoachimiak%40lbl.gov\" />");
            pw.println("<meta name=\"keywords\" content=\"microarray prokaryote bacteria genomic functional\" />");
            pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
            pw.println("<script src=\"http://www.google.com/jsapi\" type=\"text/JAVASCRIPT\"> </script>");
            pw.println("<script type=\"text/javascript\">                                    ");
            pw.println("google.load(\"visualization\", \"1\", {packages:[\"imagesparkline\"]});");
            pw.println("google.setOnLoadCallback(drawChart);");

            pw.println("function drawChart() {");
            pw.println("var data = new google.visualization.DataTable();");
            for (int j = 1; j < data[0].length; j++) {
                data[0][j] = StringUtil.replace(data[0][j], "\"", "");
                if (link == null)
                    pw.println("data.addColumn(\"number\", \"" + data[0][j] + "\")");
                else
                    pw.println("data.addColumn(\"number\", \"<a href=\"" + link + data[0][j] + ">" + data[0][j] + "</a>\")");
            }
            pw.println("data.addRows(" + (data.length - 1) + ");");

            for (int j = 1; j < data[0].length; j++) {
                for (int i = 1; i < data.length; i++) {
                    if (!data[i][j].equals("NaN"))
                        pw.println("data.setValue(" + (i - 1) + "," + (j - 1) + "," + data[i][j] + ");");
                }
            }
            String maxs = "5";
            String mins = "-5";
            for (int j = 1; j < data[0].length; j++) {
                maxs += ",5";
                mins += ",-5";
            }
            pw.println("var chart = new google.visualization.ImageSparkLine(document.getElementById('chart_div'));");
            pw.println("chart.draw(data, {max:(" + maxs + "), mins:(" + mins + "),width: 500, height: 150, showAxisLines: true,  showValueLabels: false, labelPosition: 'left'});");
            pw.println("}");

            pw.println("</script>");
            pw.println("</head>");

            pw.println("<body bgcolor=\"#ffffff\">");
            pw.println("<a href=\"http://www.microbesonline.org/\"><img src=\"/images/microbesOnline150.gif\" border=0></a>");
            pw.println("<hr>");
            pw.println("<div id=\"chart_div\"></div>");
            pw.println("</body>");
            pw.println("</html>");

            pw.close();
            System.out.println("wrote " + out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    private void initOptions(String[] args) {
        String[] options = MapArgOptions.compactArgs(args, arg_options);
        System.out.println("options");
        MoreArray.printArray(options);

        int[] indices = new int[arg_options.length];
        indices[0] = MoreArray.getArrayInd(arg_options, "-data");
        indices[1] = MoreArray.getArrayInd(arg_options, "-out");
        indices[2] = MoreArray.getArrayInd(arg_options, "-link");
        //indices[5] = MoreArray.getArrayInd(arg_options, "-linky");

        if (options[0] != null) {
            try {
                data = TabFile.readtoArray(options[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (options[1] != null)
            outfile = options[1];
        if (options[2] != null)
            link = options[2];
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2 || args.length == 4 || args.length == 6) {
            MakeSpark ce = new MakeSpark(args);
        } else {
            System.out.println(args.length);
            System.out.println("syntax: java util.MakeSpark\n" +
                    "-data\n" +
                    "-out (optional otherwise stdout)\n" +
                    "-link (optional string giving url)"
            );
        }
    }
}
