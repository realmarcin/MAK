package mathy;

import util.MoreArray;
import util.TabFile;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jan 25, 2012
 * Time: 5:28:38 PM
 */
public class SimpleMatrixInt {


    public int[][] data;
    public String[] xlabels, ylabels;
    public boolean vertical = false;

    /**
     *
     */
    public SimpleMatrixInt() {
        data = null;
        xlabels = null;
        ylabels = null;
    }

    /**
     * @param f
     */
    public SimpleMatrixInt(String f) {
        read(f);
    }

    /**
     * @param f
     */
    private void read(String f) {
        String[][] read_data = TabFile.readtoArray(f);
//        System.out.println("SimpleMatrix read_data " + read_data.length + "\t" + read_data[0].length+
//                "\ttop left corner " + read_data[0][0] +
//                "\tfirst gene " + read_data[1][0] + "\tfirst exp " + read_data[0][1]);
        /*System.out.println("SimpleMatrix read_data first row");
        MoreArray.printArray(read_data[0]);*/
        //System.out.println("SimpleMatrix read_data second row");
        //MoreArray.printArray(read_data[1]);
        xlabels = read_data[0];
        ArrayList tmp = MoreArray.convtoArrayList(xlabels);
        //remove the column label row
        tmp.remove(0);
        xlabels = MoreArray.ArrayListtoString(tmp);

        read_data = MoreArray.removeRow(read_data, 1);
//        System.out.println("SimpleMatrix removeRow " + read_data.length + "\t" + read_data[0].length +
//                "\ttop left corner " + read_data[0][0] +
//                "\tfirst gene " + read_data[1][0] + "\tfirst exp " + read_data[0][1]);
        ylabels = MoreArray.extractColumnStr(read_data, 1);
        /*System.out.println("SimpleMatrix xlabels");
        MoreArray.printArray(xlabels);
        System.out.println("SimpleMatrix ylabels");
        MoreArray.printArray(ylabels);*/
        read_data = MoreArray.removeColumn(read_data, 1);
//        System.out.println("SimpleMatrix removeColumn " + read_data.length + "\t" + read_data[0].length +
//                "\ttop left corner " + read_data[0][0] +
//                "\tfirst gene " + read_data[1][0] + "\tfirst exp " + read_data[0][1]);
        /*  System.out.println("SimpleMatrix read_data first row");
        MoreArray.printArray(read_data[0]);
        System.out.println("SimpleMatrix read_data second row");
        MoreArray.printArray(read_data[1]);*/
        data = MoreArray.convfromStringtoInt(read_data);
        System.out.println("SimpleMatrix read top left corner " + data[0][0] +
                "\tfirst gene " + ylabels[0] + "\tfirst exp " + xlabels[0]);
        System.out.println("SimpleMatrix " + data.length + "\t" + data[0].length + "\t" + ylabels.length + "\t" + xlabels.length);
        /*System.out.println("SimpleMatrix first row");
        MoreArray.printArray(data[0]);
        System.out.println("SimpleMatrix second row");
        MoreArray.printArray(data[1]);*/
        /*System.out.println("SimpleMatrix data");
        for (int i = 0; i < data.length; i++) {
            System.out.println(i + "\t" + data[i][0] + "\t" + data[i][1] + "\t" + data[i][2] + "\t" + data[i][3]);
        }*/

        detectExpAxis();
    }


    /**
     * @param exps
     */
    public void maskExperiments(String[] exps) {
        ArrayList cols = new ArrayList();
        /*System.out.println("maskExperiments vertical " + vertical);
        System.out.println("maskExperiments xlabels ");
        MoreArray.printArray(xlabels);
        System.out.println("maskExperiments exps ");
        MoreArray.printArray(exps);*/
        /*
        System.out.println("maskExperiments ylabels ");
        MoreArray.printArray(ylabels);
        */
        //x-PRECRITERIA_AXIS is experiments
        if (vertical) {
            for (int i = 0; i < xlabels.length; i++) {
                //System.out.println(i + "\t" + xlabels[i]);
                int dotind = xlabels[i].indexOf(".");
                //System.out.println("maskExperiments dotind xlabels " + xlabels[i] + "\t" + dotind);
                int index = MoreArray.getArrayInd(exps, xlabels[i].substring(0, dotind));
                if (index == -1) {
                    //System.out.println("maskExperiments xlabels cols "+(i+1)+"\t"+xlabels[i]);
                    cols.add(new Integer(i + 1));
                } else {
                    //System.out.println("maskExperiments no index " + xlabels[i]);
                }
            }
            xlabels = exps;
        }
        //y-PRECRITERIA_AXIS is experiments
        else {
            for (int i = 0; i < ylabels.length; i++) {
                //System.out.println(i + "\t" + ylabels[i]);
                int dotind = ylabels[i].indexOf(".");
                //System.out.println("maskExperiments dotind ylabels " + ylabels[i] + "\t" + dotind);
                int index = MoreArray.getArrayInd(exps, ylabels[i].substring(0, dotind));
                if (index == -1) {
                    //System.out.println("maskExperiments ylabels cols "+(i+1));
                    cols.add(new Integer(i + 1));
                } else {
                    //System.out.println("maskExperiments no index " + ylabels[i]);
                }
            }
            ylabels = exps;
        }
        //System.out.println("SimpleMatrix data 1 "+data.length+"\t"+data[0].length+"\t"+cols.size());
        String[][] tmpdata = MoreArray.removeColumns(MoreArray.toString(data), cols);
        data = MoreArray.convfromStringtoInt(tmpdata);

        /*System.out.println("SimpleMatrix data 2 "+data.length+"\t"+data[0].length);
        for (int i = 0; i < data.length; i++) {
            System.out.println(i + "\t" + data[i][0] + "\t" + data[i][1] + "\t" + data[i][2] + "\t" + data[i][3]);
        }*/
    }

    /**
     *
     */
    public void detectExpAxis() {
        int count = 0;
        for (int i = 0; i < xlabels.length; i++) {
            if (xlabels[i].indexOf(".") != -1)
                count++;
        }
        if (count > 0) {
            vertical = true;
        } else
            vertical = false;
        //System.out.println("SimpleMatrix vertical? " + vertical);
    }


}
