package bioobj;

import dtype.Coord;

import java.util.ArrayList;

/**
 * General class to represent and operate on Protein sequences.
 */
public class Protein extends Seq {


    public String leader = null;
    public int leaderstart = -1;
    public int leaderstop = -1;
    public int leaderhph = -1;

    public ArrayList pdbcoord;
    public int pdboff = 0;


    /**
     *
     */
    public Protein() {

        super();

        pdbcoord = new ArrayList();
    }

    /**
     * @param s
     */
    public Protein(String s) {

        super(s);

    }

    /**
     * @param s
     */
    public Protein(String s, String n) {

        super(s, n);

    }

    /**
     * @param s
     */
    public Protein(Seq s) {

        seq = s.seq;
        name = s.name;
        len = s.len;
    }

    /**
     * @param p
     */
    public Protein(Protein p) {

        seq = p.seq;
        name = p.name;
        len = p.len;
    }

    /**
     * @param i
     * @return
     */
    public final char AA(int i) {

        return seq.charAt(i);
    }


    /**
     *
     */
    private void stripBackbone() {

        stripAtomsByName("CA");
        stripAtomsByName("C");
        stripAtomsByName("N");
        stripAtomsByName("O");
    }

    /**
     *
     */

    public final void setPDBOff() {

        if (pdbcoord != null && pdbcoord.size() > 0) {

            pdboff = ((Coord) pdbcoord.get(0)).resnum;
        }
    }

    /**
     * @param strip
     */
    private final void stripAtomsByName(String strip) {

        for (int i = 0; i < pdbcoord.size(); i++) {

            Coord co = (Coord) pdbcoord.get(i);
            if (co.atom.equals(strip)) {
                pdbcoord.remove(i);
                i++;
            }
        }
    }


    /**
     * Returns a ArrayList object filled with Coord objects for residue number res.
     *
     * @param res
     * @return
     */
    public final ArrayList getAtoms(int res) {

//System.out.println(res+"\tgetAtoms.");
        ArrayList ret = new ArrayList();
        if (pdbcoord != null) {
            for (int i = 0; i < pdbcoord.size(); i++) {

                Coord co = (Coord) pdbcoord.get(i);
                 //System.out.println(co.resnum+"\t"+res);
                if (co.resnum == res + pdboff) {
                    //System.out.println("getAtoms "+co.resnum+"\tmatch! "+res+"\t"+pdboff);
                    ret.add((Coord) co);
                }
            }
        }
        return ret;
    }


    /**
     * Searches the atom records of the Protein pdbcoord object. Using the +pdboff adjustment to match residue numbers.
     *
     * @param res
     * @param search
     * @return
     */
    public final Coord atomSearch(int res, String search) {

//System.out.println("Protein.atomSearch "+res+"\t"+search+"\tpdbcoord.size() "+pdbcoord.size());
        System.out.println("pdboff " + pdboff);
        Coord ret = null;

        for (int i = 0; i < pdbcoord.size(); i++) {

            Coord co = (Coord) pdbcoord.get(i);
            if (co.resnum == res + pdboff) {
                //System.out.println(co.atom+"\t"+search);
                if (co.atom.equalsIgnoreCase(search)) {
                    //System.out.println("Found: "+co.fulltoString());
                    ret = new Coord(co);
                    break;
                }
            }
        }
        return ret;
    }


    /**
     * Renumbers the coordinates of protein residues to agree with the extracted seq.
     */
    public final void renumCoord() {

        int count = 1;
        String curres = "";
        int curnum = -1;
        //System.out.println("Protein length "+length());
        for (int i = 0; i < pdbcoord.size(); i++) {

            Coord curcoord = (Coord) pdbcoord.get(i);

            if (curnum == -1) {
                curres = curcoord.res;
                curnum = curcoord.resnum;
            } else if (curres.equals(curcoord.res) && curnum < curcoord.resnum) {
                curres = curcoord.res;
                curnum = curcoord.resnum;
                count++;
            }

            //System.out.println("readPDB renum "+curcoord.resnum+"\t"+count);
            curcoord.resnum = count;
            pdbcoord.set(i, (Coord) curcoord);
        }

        pdboff = 0;

    }


    /**
     * Translate a protein to DNA using the human codon usage table
     */
    public final static String ProttoDNA(String prot) {

        String transdna = "";
        for (int h = 0; h < prot.length(); h++) {

            String test = prot.substring(h, h + 1);
            for (int a = 0; a < SeqConst.trans.length; a++) {

                if (SeqConst.trans[a][1].equals(test))
                    transdna += SeqConst.trans[a][0];
            }

        }
        return transdna;
    }


}

