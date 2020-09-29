package util;

import gov.doe.kbase.MOTranslationService.MOTranslation;
import gov.doe.kbase.MOTranslationService.result;
import gov.doe.kbase.MOTranslationService.return_moLocusIds_to_fid_in_genome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TranslationTest {


    public static void main(String[] args) {
        try {
            // spin up the client
            MOTranslation client = new MOTranslation("http://140.221.92.71:7061");

           /* // whip up a list of fids.
            List<String> fids = new ArrayList<String>();
            fids.add("kb|g.0.peg.2173");
            fids.add("kb|g.0.peg.3016");
            fids.add("kb|g.0.peg.4288");

            // get the locus ids
            Map<String, List<Integer>> locusIdMap = client.fids_to_moLocusIds(fids);

            // print 'em out.
            for (String fid : locusIdMap.keySet()) {
                System.out.println(fid + ":");
                for (Integer locusId : locusIdMap.get(fid)) {
                    System.out.println("\t" + locusId);
                }
            }*/


            /*System.out.println("taxid " + 634452);
            List<String> kbgid = client.moTaxonomyId_to_genomes(new Integer(634452));
            System.out.println("kbgid " + kbgid != null ? kbgid.size() : Double.NaN);
            for (String s : kbgid) {
                System.out.println("kbgid " + s);
                //genome_map.put(g.getGenomeId(), s);
            }*/

            List<Integer> vimsss = new ArrayList<Integer>();
            vimsss.add(new Integer(39503));
            System.out.println("trying kbgids " + 39503);

            return_moLocusIds_to_fid_in_genome kbgids_return = client.moLocusIds_to_fid_in_genome(vimsss, "kb|g.2677");
            //Map<Integer, result> kbgids = translation.moLocusIds_to_fid_in_genome(list, genomeindex);
            Map<Integer, result> kbgids = kbgids_return.return_1;

            Object[] keys = (Object[]) (((Set) kbgids.keySet()).toArray());
            for (int k = 0; k < keys.length; k++) {
                result res = (result) kbgids.get(keys[k]);
                System.out.println("kbgids " + (Integer) keys[k] + "\t" + res.best_match+"\t"+res.status);
            }


        } catch (Exception e) {
            // This is a little generic, but I'm not sure what actual exceptions can get returned...
            System.out.println("Exception caught: " + e);
        }
    }


}