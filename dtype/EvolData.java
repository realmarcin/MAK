package dtype;

import java.io.File;

public class EvolData {
    public File prottree;
    public File protseqs;
    public File dnaseqs;
    public File pdb;
    public File[] pdbs;
    public String matchseqtopdb;
    public String[] matchseqstopdbs;

    public EvolData() {

        prottree = null;

        protseqs = null;
        ;

        dnaseqs = null;

        pdb = null;
        matchseqtopdb = null;
        matchseqstopdbs = null;
    }

    public EvolData(String ptree, String pseqs, String pdna, String[] ppdb) {
        if (ptree != null)
            prottree = new File(ptree);
        if (pseqs != null)
            protseqs = new File(pseqs);
        if (pdna != null)
            dnaseqs = new File(pdna);
        if (ppdb != null)
            for (int i = 0; i < ppdb.length; i++)
                pdbs[i] = new File(ppdb[i]);
    }

    public EvolData(String ptree, String pseqs, String pdna, String ppdb) {
        if (ptree != null)
            prottree = new File(ptree);
        if (pseqs != null)
            protseqs = new File(pseqs);
        if (pdna != null)
            dnaseqs = new File(pdna);
        if (ppdb != null)
            pdb = new File(ppdb);
    }

    public void updateTree(String ptree) {
        if (ptree != null)
            prottree = new File(ptree);
    }

    public void updateProtSeqs(String pseqs) {
        if (pseqs != null)
            protseqs = new File(pseqs);
    }

    public void updateDnaSeqs(String pdna) {
        if (pdna != null)
            dnaseqs = new File(pdna);
    }

    public void updatePdb(String ppdb) {
        if (ppdb != null)
            pdb = new File(ppdb);
    }

    public void updateMatchName(String pname) {
        if (pname != null)
            matchseqtopdb = new String(pname);
    }

    public void updatePdbs(String[] ppdb) {
        if (ppdb != null) {
            pdbs = new File[ppdb.length];
            for (int i = 0; i < ppdb.length; i++)
                pdbs[i] = new File(ppdb[i]);
        }
    }

    public void updateMatchNames(String[] pnames) {
        if (pnames != null) {
            matchseqstopdbs = new String[pnames.length];
            for (int i = 0; i < pnames.length; i++)
                matchseqstopdbs[i] = new String(pnames[i]);
        }
    }

}