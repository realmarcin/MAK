package DataMining;

import org.rosuda.JRI.Rengine;
import util.MoreArray;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: Jun 8, 2007
 * Time: 11:34:19 AM
 */
public class MoveParams {

    boolean debug = false;

    /*"g-",
      "g+",
      "e-",
      "e+"*/

    /*Single string form of possible MOVE_TYPES*/
    ArrayList[] possible_move_list;
    /*List of string representations for possible MOVE_TYPES*/
    ArrayList pMoveStrings;
    /*List of string representations of old MOVE_TYPES*/
    //ArrayList OldMovesStrBlockId;
    /* probability of addition move*/
    double pA;
    /* probability of gene move*/
    double pG;
    /* probability of batch move*/
    double pBatch;
    /* keeps track of which MOVE_TYPES get tried on this particular prev_block try */
    int[] tried_moves;
    /* index that is tested if it is NA or not */
    //double[] IpC;
    /* 0 = Delete, 1 = ADD */
    int Delete_or_Add;
    int Single_or_Batch;
    /* 0 = Experiment, 1 = Gene */
    int Experiment_or_Gene;

    String move_type;

    /*"g-",
     "g+",
     "e-",
     "e+"*/
    int[] possible_move_types;
    int move_count = 0;

    boolean stop = false;

    String current_move_class;

    //boolean inPlateau;
    //boolean donePlateau;
    boolean exp_shaving;
    boolean gene_shaving;
    boolean exp_growing;
    boolean gene_growing;

    int moveIndex = -1;

    String unit_moveSeq;


    /**
     *
     */
    public MoveParams() {
        init();
    }

    /**
     * @param prm
     */
    public MoveParams(Parameters prm, boolean d) {
        init();
        //PA = prm.PA;
        pA = prm.INITPA;
        //PG = prm.PG;
        pG = prm.INITPG;
        pBatch = prm.PBATCH;
        //System.out.println("MoveParams MoveParams "+PA+"\t"+PG);
        current_move_class = prm.move_class;

        debug = d;
    }


    /**
     *
     */
    public void init() {
        possible_move_list = MoreArray.initArrayListArray(2);
        //OldMovesStrBlockId = new ArrayList();
        pMoveStrings = new ArrayList();
        pA = MINER_STATIC.DEFAULT_pA;
        pG = MINER_STATIC.DEFAULT_pG;
        pBatch = 0.5;
        if (debug)
            System.out.println("MoveParams init " + pA + "\t" + pG + "\t" + pBatch);
        tried_moves = new int[4];
        stop = false;
        move_count = 0;
        move_type = null;
        initMoveDirections();
        possible_move_types = null;
        current_move_class = null;
        //inPlateau = false;
        //donePlateau = false;
        gene_shaving = false;
        exp_shaving = false;
        gene_growing = false;
        exp_growing = false;

        moveIndex = -1;

        unit_moveSeq = "";
    }

    /**
     *
     */
    public void initMoveDirections() {
        Delete_or_Add = -1;
        Single_or_Batch = -1;
        Experiment_or_Gene = -1;
    }

    /**
     * @param prm
     */
    public void nonInitpApG(Parameters prm) {
        pA = prm.PA;
        pG = prm.PG;
        if (debug)
            System.out.println("MoveParams nonInitpApG " + pA + "\t" + pG);
    }

    /**
     * @return
     */
    public String toString() {
        String ret = "";
        ret += "possible_move_list\n";
        if (possible_move_list != null) {
            if (possible_move_list[0] != null)
                ret += possible_move_list[0].size() + "\n";
            if (possible_move_list[1] != null)
                ret += possible_move_list[1].size() + "\n";
        } else
            ret += "possible_move_list is null\n";

        /*  ret += "OldMovesStrBlockId\n";
if (OldMovesStrBlockId != null) {
    ret += OldMovesStrBlockId.size() + "\n";
} else
    ret += "OldMovesStrBlockId is null\n";*/

        ret += "PA\n";
        ret += "" + pA + "\n";
        ret += "PG\n";
        ret += "" + pG + "\n";
        ret += "PBATCH\n";
        ret += "" + pBatch + "\n";
        ret += "tried_moves\n";
        ret += MoreArray.toString(tried_moves) + "\n";
        ret += "Move directions\n";
        ret += Single_or_Batch + "\t" + Experiment_or_Gene + "\n";
        ret += "current_move_class " + current_move_class + "\n";
        //ret += "inPlateau " + inPlateau + "\n";
        //ret += "donePlateau " + donePlateau + "\n";
        ret += "exp_shaving " + exp_shaving + "\n";
        return ret;
    }

    /**
     *
     */
    public void printOut() {
        System.out.println("MoveParams " + toString());
    }


    public boolean isExpAdd() {
        return possible_move_types[3] != 4;
    }


    public boolean canDelGenes() {
        return possible_move_types[0] == 1 & tried_moves[0] == 0 || gene_shaving;
    }

    public boolean canAddGenes() {
        return possible_move_types[1] == 2 && tried_moves[1] == 0;
    }

    public boolean canDelExps() {
        return (possible_move_types[2] == 3 && tried_moves[2] == 0) || exp_shaving;
    }

    public boolean canAddExps() {
        return possible_move_types[3] == 4 && tried_moves[3] == 0;
    }


    /**
     * Tests if there are any move types left to try.
     *
     * @return
     */
    public boolean moreMoves() {
        if (debug) {
            System.out.println("moreMoves b/f");
            MoreArray.printArray(tried_moves);
        }
        for (int i = 0; i < tried_moves.length; i++) {
            if (possible_move_types[i] == -1) {
                tried_moves[i] = 1;
                if (debug)
                    System.out.println("moreMoves move not possible " + i + "\t" + possible_move_types[i]);
            }
        }
        if (debug) {
            System.out.println("moreMoves a/f");
            MoreArray.printArray(tried_moves);
        }

        for (int i = 0; i < tried_moves.length; i++) {
            if (tried_moves[i] == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param prmpA
     * @param prmpG
     */
    public void resetpApG(double prmpA, double prmpG) {
        pA = !Double.isNaN(prmpA) ? prmpA : MINER_STATIC.DEFAULT_pA;
        pG = !Double.isNaN(prmpG) ? prmpG : MINER_STATIC.DEFAULT_pG;
    }

    /**
     * @param prmpA
     */
    public void resetpA(double prmpA) {
        pA = !Double.isNaN(prmpA) ? prmpA : MINER_STATIC.DEFAULT_pA;
    }

    /**
     * @param prmpG
     */
    public void resetpG(double prmpG) {
        pG = !Double.isNaN(prmpG) ? prmpG : MINER_STATIC.DEFAULT_pG;
    }

    /**
     * @param Reng
     */
    public void makepG(Rengine Reng) {
        if (debug)
            System.out.println("makepG");
        String s2 = "rbinom(1, 1, " + pG + ")";
        if (debug)
            System.out.println("R: " + s2);
        final double pGval = (Reng.eval(s2)).asDouble();
        if (debug)
            System.out.println("pGval " + pGval);
        Experiment_or_Gene = (int) pGval;
    }

    /**
     * @param Reng
     */
    public void makepA(Rengine Reng) {
        if (debug)
            System.out.println("makepA");
        String s = "rbinom(1, 1, " + pA + ")";
        if (debug)
            System.out.println("R: " + s);
        final double pAval = (Reng.eval(s)).asDouble();
        if (debug)
            System.out.println("pGval " + pAval);
        Delete_or_Add = (int) pAval;
    }
}

