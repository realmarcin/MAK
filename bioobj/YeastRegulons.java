package bioobj;

import util.MoreArray;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: marcin
 * Date: Mar 29, 2008
 * Time: 3:48:27 PM
 */
public class YeastRegulons {

    //assigned gene	spot name	orf	MIPS Description	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio	p-value	ratio
    //				ABF1		ACE2		ADR1		ARG80		ARG81		ARO80		ASH1		AZF1		BAS1		CAD1		CBF1		CHA4		CIN5		CRZ1		CUP9		DAL81		DAL82		DIG1		DOT6		ECM22		FHL1		FKH1		FKH2		FZF1		GAL4		GAT1		GAT3		GCN4		GCR1		GCR2		GLN3		GRF10(Pho2)		GTS1		HAA1		HAL9		HAP2		HAP3		HAP4		HAP5		HIR1		HIR2		HMS1		HSF1		IME4		INO2		INO4		IXR1		LEU3		MAC1		MAL13		MAL33		MATa1		MBP1		MCM1		MET31		MET4		MIG1		MOT3		MSN1		MSN2		MSN4		MSS11		MTH1		NDD1		NRG1		PDR1		PHD1		PHO4		PUT3		RAP1		RCS1		REB1		RFX1		RGM1		RGT1		RIM101		RLM1		RME1		ROX1		RPH1		RTG1		RTG3		RTS2		SFL1		SFP1		SIG1		SIP4		SKN7		SKO1		SMP1		SOK2		SRD1		STB1		STE12		STP1		STP2		SUM1		SWI4		SWI5		SWI6		THI2		UGA3		USV1		YAP1		YAP3		YAP5		YAP6		YAP7		YBR267W		YFL044C		YJL206C		ZAP1		ZMS1

    public String[] factors;
    public int data_offset = 4;
    public ArrayList bygene;

    public java.util.ArrayList[] significant, significant_int, significant_unique, significant_unique_int;
    public int[] significant_sizes, significant_unique_sizes;

    public String[] types = {"MacIsaac", "Lee"};
    /*type index based on types array*/
    public int type = 0;

    /**
     *
     */
    public YeastRegulons() {
        bygene = new ArrayList();
    }

    /**
     * @param f
     */
    public YeastRegulons(String f, String s) {
        type = MoreArray.getArrayInd(types, s);
        bygene = new ArrayList();
        if (type == 0) {
            readMacIsaac(f);
            setSizeUnique();
        } else if (type == 1) {
            readLee(f);
            extractStrIds();
        }
        /* for (int j = 0; j < significant.length; j++) {
            System.out.println("YeastRegulons significant " + j + "\t" + factors[j] + "\t" + significant[j].size());
        }*/
        printStats();
    }

    /**
     * @param f
     */
    public YeastRegulons(String f, String[] gene_labels, String s) {
        type = MoreArray.getArrayInd(types, s);
        bygene = new ArrayList();
        if (type == 0) {
            readMacIsaac(f);
            translatetoIntIds(gene_labels);
            setUniquetoRegulon();
            setSizeUnique();
        } else if (type == 1) {
            readLee(f);
            extractStrIds();
            translatetoIntIds(gene_labels);
            setSize();
        }

        printStats();
    }

    /**
     *
     */
    private void printStats() {
        System.out.println("YeastRegulons:  significant: " + significant.length +
                "\tsignificant " + significant_int.length +
                "\tbygene " + bygene.size() + "\tsignificant_sizes " + significant_sizes.length);
    }

    /**
     *
     */
    private void translatetoIntIds(String[] theselabels) {
        significant_int = MoreArray.initArrayListArray(factors.length);
        significant_unique_int = MoreArray.initArrayListArray(factors.length);
        for (int i = 0; i < factors.length; i++) {
            for (int j = 0; j < significant[i].size(); j++) {
                String cur = (String) significant[i].get(j);
                //System.out.println("GAL4 " + i + "\t" + cur);
                int add = MoreArray.getArrayInd(theselabels, cur);
                //System.out.println("translatetoIntIds " + cur + "\t" + add);
                if (add != -1) {
                    significant_int[i].add(new Integer(add + 1));
                }
            }
        }

        for (int i = 0; i < significant_int.length; i++) {
            for (int j = 0; j < significant_int[i].size(); j++) {
                Integer cur = (Integer) significant_int[i].get(j);
                //System.out.println("GAL4 " + i + "\t" + cur);
                if(!checkIntegers(i, cur)) {
                    significant_unique_int[i].add(cur);
                }
            }
        }
    }

    /**
     *
     * @param i
     * @param cur
     * @return
     */
    private boolean checkIntegers(int i, Integer cur) {
        for (int a = i + 1; a < significant_int.length; a++) {
            for (int b = 0; b < significant_int[a].size(); b++) {
                Integer curtwo = (Integer) significant_int[a].get(b);
                if(cur.equals(curtwo))
                return true;
            }
        }

        return false;
    }

    /**
     *
     */
    private void extractStrIds() {
        significant = MoreArray.initArrayListArray(factors.length);
        for (int j = 0; j < bygene.size(); j++) {
            MacIsaacRegulonEntry re = (MacIsaacRegulonEntry) bygene.get(j);
            for (int i = 0; i < factors.length; i++) {
                if (significant[i] == null)
                    significant[i] = new ArrayList();
                if (re.pval_ratio[i][0] <= 0.001)
                    significant[i].add(re.gene_name);
            }
        }
        setSize();
    }

    /**
     *
     */
    private void setSize() {
        significant_sizes = new int[significant.length];
        for (int n = 0; n < significant.length; n++) {
            significant_sizes[n] = significant[n].size();
            //System.out.println(factors[n] + "\t" + significant_sizes[n]);
        }
    }

    /**
     *
     */
    private void setSizeUnique() {
        significant_unique_sizes = new int[significant_unique.length];
        for (int n = 0; n < significant_unique.length; n++) {
            significant_unique_sizes[n] = significant_unique[n].size();
            //System.out.println(factors[n] + "\t" + significant_sizes[n]);
        }
    }


    /**
     * @param f
     */
    public void readLee(String f) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = in.readLine();
            String[] labels = data.split("\t");
            data = in.readLine();
            factors = MoreArray.removeNullAndEmpty(data.split("\t"));
            /*System.out.println("YeastRegulons factors");
            MoreArray.printArray(factors);*/
            data = in.readLine();
            while (data != null) {
                //System.out.println("data " + data);
                String[] ar = data.split("\t");
                MacIsaacRegulonEntry re = null;
                try {
                    re = new MacIsaacRegulonEntry(ar, factors, data_offset);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("factors.length " + factors.length + "\t" + ar.length);
                }
                bygene.add(re);
                data = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param f
     */
    public void readMacIsaac(String f) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = in.readLine();
            ArrayList tmpfact = new ArrayList();

            data = in.readLine();
            while (data != null) {
                //System.out.println("data " + data);
                String[] ar = data.split("\t");
                tmpfact.add(ar[0]);
                ArrayList add = MoreArray.convtoArrayList(ar);
                add.remove(0);
                bygene.add(add);
                data = in.readLine();
            }
            in.close();

            factors = MoreArray.ArrayListtoString(tmpfact);

            /*System.out.println("YeastRegulons factors");
            MoreArray.printArray(factors);*/

            significant = MoreArray.initArrayListArray(factors.length);
            for (int j = 0; j < bygene.size(); j++) {
                ArrayList ar = (ArrayList) bygene.get(j);
                for (int i = 0; i < ar.size(); i++) {
                    if (significant[j] == null)
                        significant[j] = new ArrayList();
                    significant[j].add((String) ar.get(i));
                }
            }
            setSize();

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void setUniquetoRegulon() {
        significant_unique = MoreArray.initArrayListArray(factors.length);
        for (int j = 0; j < significant.length; j++) {
            for (int k = 0; k < significant[j].size(); k++) {
                String cur = (String) significant[j].get(k);
                if (!checkOtherRegulons(j, cur))
                    significant_unique[j].add(cur);
            }
        }
    }

    /**
     * @param j
     * @param cur
     * @return
     */
    private boolean checkOtherRegulons(int j, String cur) {
        for (int a = j + 1; a < significant.length; a++) {
            for (int b = 0; b < significant[a].size(); b++) {
                String curtwo = (String) significant[a].get(b);
                if (cur.equals(curtwo))
                    return true;
            }
        }
        return false;
    }

}

