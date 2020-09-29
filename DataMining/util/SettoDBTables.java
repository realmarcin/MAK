package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.GiveDate;
import util.MoreArray;
import util.TabFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 2/11/13
 * Time: 10:17 PM
 */
public class SettoDBTables {

    Connection con;
    Statement stmt;

    int DatasetId = -1;
    int BiclusterId = -1;
    int GeneId = -1;

    String[] gene_labels;

    static String biclusterf = "Bicluster";
    ArrayList<String> biclusterFAr = new ArrayList<String>();

    /*
    +-----------------+--------------+------+-----+---------+----------------+
    | Field           | Type         | Null | Key | Default | Extra          |
    +-----------------+--------------+------+-----+---------+----------------+
    | biclusterId  | int(11)      | NO   | PRI | NULL    | auto_increment |
    | conditionIds | text         | YES  |     | NULL    |                |
    | expMean      | float        | YES  |     | NULL    |                |
    | exprMeanCrit | float        | YES  |     | NULL    |                |
    | exprRegCrit  | double       | YES  |     | NULL    |                |
    | fullCrit     | float        | YES  |     | NULL    |                |
    | moveType     | varchar(255) | YES  |     | NULL    |                |
    | name         | varchar(255) | YES  |     | NULL    |                |
    | preCriterion | float        | YES  |     | NULL    |                |
    | datasetId    | int(11)      | YES  |     | NULL    |                |
    */

    static String biclusterd = "Dataset";
    ArrayList<String> biclusterDAr = new ArrayList<String>();

    /*
    +-----------------+--------------+------+-----+---------+----------------+
    | Field           | Type         | Null | Key | Default | Extra          |
    +-----------------+--------------+------+-----+---------+----------------+
    | datasetId       | int(11)      | NO   | PRI | NULL    | auto_increment |
    | description     | varchar(255) | YES  |     | NULL    |                |
    | genomeKBaseId   | varchar(255) | YES  |     | NULL    |                |
    | kbaseId         | varchar(255) | YES  |     | NULL    |                |
    | name            | varchar(255) | YES  |     | NULL    |                |
    | networkType     | varchar(255) | YES  |     | NULL    |                |
    | sourceReference | varchar(255) | YES  |     | NULL    |                |
    */

    static String biclusterg = "Gene";
    ArrayList<String> biclusterGAr = new ArrayList<String>();

    /*
    +-------------+--------------+------+-----+---------+----------------+
    | Field       | Type         | Null | Key | Default | Extra          |
    +-------------+--------------+------+-----+---------+----------------+
    | geneId      | int(11)      | NO   | PRI | NULL    | auto_increment |
    | kbaseId     | varchar(255) | YES  | MUL | NULL    |                |
    | locusTag    | varchar(255) | YES  |     | NULL    |                |
    | moId        | int(11)      | YES  | MUL | NULL    |                |
    | name        | varchar(255) | YES  |     | NULL    |                |
    | biclusterId | int(11)      | YES  | MUL | NULL    |                |
    | datasetId   | int(11)      | YES  | MUL | NULL    |                |
    */

    /**
     * @param args
     */
    public SettoDBTables(String[] args) {

        GiveDate gd = new GiveDate();

        try {
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            //con = DriverManager.getConnection("jdbc:mysql://fern/marcin?user=marcin&password=tester");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/bicluster_dev?user=marcin&password=gimekbase");//
            System.out.println("created connection !!");
            stmt = con.createStatement();

            String s = new String("SELECT max(datasetId) FROM Dataset");
            ResultSet r = stmt.executeQuery(s);
            if (r.next()) {
                DatasetId = r.getInt(1) + 1;
                System.out.println("DatasetId " + DatasetId);
            }

            String s1 = new String("SELECT max(biclusterId) FROM Bicluster");
            ResultSet r1 = stmt.executeQuery(s1);
            if (r1.next()) {
                BiclusterId = r1.getInt(1) + 1;
                System.out.println("BiclusterId " + BiclusterId);
            }

            String s2 = new String("SELECT max(geneId) FROM Gene");
            ResultSet r2 = stmt.executeQuery(s2);
            if (r2.next()) {
                GeneId = r2.getInt(1) + 1;
                System.out.println("GeneId " + GeneId);
            }


            String[][] KBaseLookup = TabFile.readtoArray(args[2]);
            String[] kbasegeneids = MoreArray.extractColumnStr(KBaseLookup, 4);
            String[] moids = MoreArray.extractColumnStr(KBaseLookup, 3);

            try {
                String[][] sarray = TabFile.readtoArray(args[3]);
                System.out.println("setLabels g " + sarray.length + "\t" + sarray[0].length);
                int col = 2;
                String[] n = MoreArray.extractColumnStr(sarray, col);
                gene_labels = MoreArray.replaceAll(n, "\"", "");
                System.out.println("setLabels gene " + gene_labels.length + "\t" + gene_labels[0]);
            } catch (Exception e) {
                System.out.println("expecting 2 cols");
                e.printStackTrace();
            }

            try {
                ValueBlockList vbl = ValueBlockListMethods.readAny(args[0]);

                String description = "Shewanella gene co-expression modules";
                String genomeKBaseId = "kb|g.20848";
                String kbaseId = "kb|dataset.mak." + DatasetId;
                String networkType = "CO_EXPRESSION";
                String sourceReference = "MAK_BICLUSTER";
                String name = "MAKv1.0 Shewanella gene co-expression modules";
                biclusterDAr.add(description + "\t" + genomeKBaseId + "\t" + kbaseId + "\t" +
                        name + "\t" + networkType + "\t" + sourceReference);
                //CO_FITNESS
                TabFile.write(MoreArray.ArrayListtoString(biclusterDAr), biclusterd + "_" + gd.giveMilli());

                String load = null;
                try {
                    load = "INSERT INTO Dataset (datasetId, description, genomeKBaseId, kbaseId, name, networkType, sourceReference) " +
                            "VALUES (" + DatasetId + ",'" + description + "','" + genomeKBaseId +
                            "','" + kbaseId + "','" + name + "','" + networkType +
                            "','" + sourceReference + "')";

                    System.out.println("inserting " + load);

                    stmt.executeUpdate(load);
                    //System.out.println("loaded    "+loadData);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage() + "\n" + load);
                    e.printStackTrace();
                }


                int gcount = 0;
                for (int i = 0; i < vbl.size(); i++) {
                    ValueBlock vb = (ValueBlock) vbl.get(i);
                    int curBiclusterId = BiclusterId + i;
                    String blockid = vb.blockId();
                    int sla = blockid.indexOf("/");
                    String curkbaseid = "kb|bicluster." + curBiclusterId;
                    biclusterFAr.add(curBiclusterId + "\t" + blockid.substring(sla + 1) + "\t" + vb.exp_mean + "\t" + vb.all_criteria[1] + "\t" + vb.all_criteria[3] + "\t" +
                            vb.full_criterion + "\t" + MINER_STATIC.MOVE_LABELS[vb.move_type + 1] + "\t" + curkbaseid + "\t" + vb.pre_criterion + "\t" + DatasetId);

                    String loadB = null;
                    try {
                        loadB = "INSERT INTO Bicluster (biclusterId, conditionIds, expMean, exprMeanCrit, exprRegCrit, fullCrit, moveType,name,preCriterion,datasetId) " +
                                "VALUES (" + curBiclusterId + ",'" + blockid.substring(sla + 1) + "'," + vb.exp_mean +
                                "," + vb.all_criteria[1] + "," + vb.all_criteria[3] + "," + vb.full_criterion +
                                ",'" + MINER_STATIC.MOVE_LABELS[vb.move_type + 1] + "','" + "kb|bicluster." +
                                curBiclusterId + "'," + (!Double.isNaN(vb.pre_criterion) ? vb.pre_criterion : "NULL") + "," + DatasetId + ")";

                        System.out.println("inserting " + loadB);

                        stmt.executeUpdate(loadB);
                        //System.out.println("loaded    "+loadData);
                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage() + "\n" + loadB);
                        e.printStackTrace();
                    }


                    for (int j = 0; j < vb.genes.length; j++) {
                        String moId = gene_labels[vb.genes[j] - 1];

                        int kbind = MoreArray.getArrayInd(moids, moId);
                        if (kbind != -1) {
                            int curgeneid = GeneId + gcount;
                            biclusterGAr.add(curgeneid + "\t" + kbasegeneids[kbind] + "\tNULL\t" + moId + "\tkb|bicluster." + curBiclusterId + "\t" + curBiclusterId + "\t" + DatasetId);

                            String loadG = null;
                            try {
                                loadG = "INSERT INTO Gene (geneId, kbaseId, locusTag, moId, name, biclusterId, datasetId) " +
                                        "VALUES (" + curgeneid + ",'" + kbasegeneids[kbind] + "','" + "NULL" +
                                        "'," + moId + ",'" + "NULL" + "'," + curBiclusterId + "," + DatasetId + ")";

                                System.out.println("inserting " + loadG);

                                stmt.executeUpdate(loadG);
                                //System.out.println("loaded    "+loadData);
                            } catch (Exception e) {
                                System.out.println("Exception: " + e.getMessage() + "\n" + loadB);
                                e.printStackTrace();
                            }


                            gcount++;
                        } else
                            System.out.println(moId + "\t" + kbind);
                    }
                }

                TabFile.write(MoreArray.ArrayListtoString(biclusterFAr), biclusterf + "_" + gd.giveMilli());

                TabFile.write(MoreArray.ArrayListtoString(biclusterGAr), biclusterg + "_" + gd.giveMilli());

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }


    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 4) {
            SettoDBTables rm = new SettoDBTables(args);
        } else {
            System.out.println("syntax: java DataMining.util.SettoDBTables\n" +
                    "<input vbl file> <out dir> <KBaseLookup> <bicluster gene ids>"
            );
        }
    }
}
