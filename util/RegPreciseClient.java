package util;

import com.lbl.regprecise.xml.reference.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import gov.doe.kbase.MOTranslationService.MOTranslation;
import gov.doe.kbase.MOTranslationService.result;
import gov.doe.kbase.MOTranslationService.return_moLocusIds_to_fid_in_genome;
import gov.doe.kbase.MOTranslationService.return_moLocusIds_to_fid_in_genome_fast;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 12/11/12
 * Time: 5:56 PM
 */
public class RegPreciseClient {

    private MOTranslation translation;

    HashMap genome_map;

    ArrayList[][] output_array;
    String[] genome_list;

    int istart, jstart, kstart;

    /**
     * @param args
     */
    public RegPreciseClient(String[] args) {

        //connectLut();

        try {
            init();

            ClientConfig clientConfig = new DefaultClientConfig();
            Client client = Client.create(clientConfig);

            translateGenomes(client);

            //testClient(client);

        /*REGULOG COLLECTIONS*/
            WebResource resource = client.resource("http://regprecise.lbl.gov/Services/rest/regulogCollections?collectionType=taxGroup");
            ClientResponse response = resource.accept("application/json").get(ClientResponse.class);
            int status = response.getStatus();
            String text = response.getEntity(String.class);

            System.out.println("REGULOG COLLECTIONS " + status + "\n" + text);
            ObjectMapper mapper = new ObjectMapper();

            StringReader sr = new StringReader(text);

            RegulogCollectionContainer bl = null;
            try {
                bl = mapper.readValue(sr, new
                        TypeReference<RegulogCollectionContainer>() {
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*REGULOG COLLECTIONS*/
            int size = bl.regulogCollection.size();
            for (int i = 0; i < size; i++) {
                System.out.println("doing regulogCollection " + i + "\t" + ((double) i / (double) size));
                RegulogCollection cura = (RegulogCollection) bl.regulogCollection.get(i);
                System.out.println(cura.getCollectionId() + "\t" + cura.getName() + "\t" + cura.getCollectionType() + "\t" + cura.getClassName());

                WebResource resource2 = client.resource("http://regprecise.lbl.gov/Services/rest/regulogs?collectionType=taxGroup&collectionId=" + cura.getCollectionId());
                ClientResponse response2 = resource2.accept("application/json").get(ClientResponse.class);
                int status2 = response2.getStatus();
                String text2 = response2.getEntity(String.class);

                System.out.println("REGULOGS " + status2 + "\n" + text2);
                ObjectMapper mapper2 = new ObjectMapper();

                StringReader sr2 = new StringReader(text2);

                RegulogContainer bl2 = null;
                try {
                    bl2 = mapper2.readValue(sr2, new
                            TypeReference<RegulogContainer>() {
                            });

            /*REGULONS*/
                    int size1 = bl2.regulog.size();
                    for (int j = 0; j < size1; j++) {
                        System.out.println("doing regulogCollection " + i + " * " + j + "\t" + ((double) j / (double) size1));
                        Regulog curb = (Regulog) bl2.regulog.get(j);
                        String taxname = curb.getTaxonName();
                        System.out.println("regulog " + curb.getEffector() + "\t" + curb.getPathway() + "\t" + curb.getRegulationType() + "\t" +
                                curb.getRegulatorFamily() + "\t" + curb.getRegulatorName() + "\t" + taxname);

                        WebResource resource3 = client.resource("http://regprecise.lbl.gov/Services/rest/regulons?regulogId=" + curb.getRegulogId());
                        ClientResponse response3 = resource3.accept("application/json").get(ClientResponse.class);
                        int status3 = response3.getStatus();
                        String text3 = response3.getEntity(String.class);

                        System.out.println("REGULONS " + status3 + "\n" + text3);
                        ObjectMapper mapper3 = new ObjectMapper();

                        StringReader sr3 = new StringReader(text3);

                        RegulonContainer bl3 = null;
                        try {
                            bl3 = mapper3.readValue(sr3, new
                                    TypeReference<RegulonContainer>() {
                                    });

                           /*REGULATORS, SITES, GENES*/
                            int size2 = bl3.regulon.size();
                            for (int k = 0; k < size2; k++) {
                                System.out.println("doing regulon " + i + " * " + j + " * " + k + "\t" + ((double) k / (double) size2));
                                Regulon curc = (Regulon) bl3.regulon.get(k);
                                System.out.println("Regulon " + curc.getEffector() + "\t" + curc.getPathway() + "\t" +
                                        curc.getRegulationType() + "\t" + curc.getRegulatorFamily() + "\t" + curc.getGenomeId());

                                String mapgenomeId = (String) genome_map.get(curc.getGenomeId());

                                System.out.println("mapgenomeId " + mapgenomeId + "\t" + curc.getGenomeId());

                                if (mapgenomeId != null) {
                                    int genomeindex = StringUtil.getFirstIndexOf(mapgenomeId, genome_list);
                                    int genI = getGenes(client, curc, genomeindex);

                                    if (!curb.getRegulationType().equals("RNA")) {
                                        int regI = getRegulators(client, curc, genomeindex);
                                    } else
                                        System.out.println("skipping RNA regulator(s)");

                                    int sitI = getSites(client, curc, genomeindex);

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            response.close();

    /*regulons.<genome_id>.tab, trancription_factors.<genome_id>.tab and binding_sites.<genome_id>.tab*/
            for (int i = 0; i < genome_list.length; i++) {
                System.out.println("outputing " + genome_list[i]);
                if (output_array[i][0] != null)
                    TextFile.write(output_array[i][0], "regulons." + genome_list[i] + ".tab");
                if (output_array[i][1] != null)
                    TextFile.write(output_array[i][1], "transcription_factors." + genome_list[i] + ".tab");
                if (output_array[i][2] != null)
                    TextFile.write(output_array[i][2], "binding_sites." + genome_list[i] + ".tab");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param client
     */
    private void translateGenomes(Client client) {
        genome_map = new HashMap();

        ArrayList out = new ArrayList();
        ArrayList bad = new ArrayList();

        WebResource resource6 = client.resource("http://regprecise.lbl.gov/Services/rest/genomes");
        ClientResponse response6 = resource6.accept("application/json").get(ClientResponse.class);
        int status6 = response6.getStatus();
        String text6 = response6.getEntity(String.class);

        System.out.println("REGULONS " + status6 + "\n" + text6);
        ObjectMapper mapper6 = new ObjectMapper();

        StringReader sr6 = new StringReader(text6);

        int notmapped = 0;
        GenomesContainer bl6 = null;
        try {
            bl6 = mapper6.readValue(sr6, new
                    TypeReference<GenomesContainer>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (Genome g : bl6.genome) {
                Integer taxId = g.getTaxonomyId();
                System.out.println("trying taxId " + taxId);
                List<String> kbgid = translation.moTaxonomyId_to_genomes(taxId);
                if (kbgid != null && kbgid.size() > 0) {
                    for (String s : kbgid) {
                        System.out.println("kbgid adding " + s);
                        genome_map.put(g.getGenomeId(), s);

                        out.add(g.getGenomeId() + "\t" + s);
                    }
                } else {
                    bad.add(taxId);
                    notmapped++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collection<String> getkeys = genome_map.values();
        genome_list = getkeys.toArray(new String[0]);

        output_array = new ArrayList[genome_map.size()][3];

        System.out.println("not mapped " + notmapped);

        TextFile.write(out, "genome_map.txt");

        System.out.println("not mapped genomes");
        MoreArray.printArray(MoreArray.ArrayListtoString(bad));
    }


    /**
     * @param client
     * @param curc
     * @throws IOException
     */
    private int getGenes(Client client, Regulon curc, int genomeindex) throws IOException {

        String s1 = "http://regprecise.lbl.gov/Services/rest/genes?regulonId=" + curc.getRegulonId();
        WebResource resource5 = client.resource(s1);
        ClientResponse response5 = resource5.accept("application/json").get(ClientResponse.class);
        int status5 = response5.getStatus();
        String text5 = response5.getEntity(String.class);

        System.out.println("GENES " + status5 + "\n" + text5);
        ObjectMapper mapper5 = new ObjectMapper();

        StringReader sr5 = new StringReader(text5);

        GenesContainer bl5 = null;
        GeneContainer bl5s = null;
        try {
            bl5 = mapper5.readValue(sr5, new
                    TypeReference<GenesContainer>() {
                    });
            System.out.println("bl5.gene.size() " + bl5.gene.size());
                /*GENES*/
            List<Integer> list = new ArrayList<Integer>();

            for (int l = 0; l < bl5.gene.size(); l++) {
                Gene cure = (Gene) bl5.gene.get(l);
                Integer vimss = cure.getVimssId();
                list.add(vimss);
                System.out.println("getGenes " + genome_list[genomeindex] + "\t" + vimss);
            }

            List<String> best_match = getKBGeneId(genomeindex, list);
            if (best_match != null && best_match.size() > 0) {
                for (int l = 0; l < best_match.size(); l++) {
                    String s = curc.getRegulonId() + "\t" + best_match.get(l);
                    System.out.println("gene " + s + "\t" + genome_list[genomeindex]);

                    if (output_array[genomeindex][0] == null)
                        output_array[genomeindex][0] = new ArrayList();

                    output_array[genomeindex][0].add(s);
                }
            }

        } catch (IOException e) {
            sr5 = new StringReader(text5);
            bl5s = mapper5.readValue(sr5, new
                    TypeReference<GeneContainer>() {
                    });
            try {
                System.out.println("bl5s " + 1);
                /*GENE*/
                Integer vimss = bl5s.gene.getVimssId();

                System.out.println("getGenes " + genome_list[genomeindex] + "\t" + vimss);
                List<Integer> list = new ArrayList<Integer>();
                list.add(vimss);

                List<String> best_match = getKBGeneId(genomeindex, list);

                if (best_match != null && best_match.size() > 0) {
                    String s = bl5s.gene.getRegulonId() + "\t" + best_match.get(0);

                    System.out.println("gene " + s);

                    if (output_array[genomeindex][0] == null)
                        output_array[genomeindex][0] = new ArrayList();

                    output_array[genomeindex][0].add(s);
                }
            } catch (Exception ee) {
                ee.printStackTrace();
                return 0;
            }
        }

        return 1;
    }

    /**
     * @param client
     * @param curc
     * @throws IOException
     */
    private int getSites(Client client, Regulon curc, int genomeindex) throws IOException {
        String s1 = "http://regprecise.lbl.gov/Services/rest/sites?regulonId=" + curc.getRegulonId();
        WebResource resource5 = client.resource(s1);
        ClientResponse response5 = resource5.accept("application/json").get(ClientResponse.class);
        int status5 = response5.getStatus();
        String text5 = response5.getEntity(String.class);

        System.out.println("BINDING SITES " + status5 + "\n" + text5);
        ObjectMapper mapper5 = new ObjectMapper();

        StringReader sr5 = new StringReader(text5);

        SitesContainer bl5 = null;
        SiteContainer bl5s = null;
        try {
            bl5 = mapper5.readValue(sr5, new
                    TypeReference<SitesContainer>() {
                    });
            System.out.println("bl5.site.size() " + bl5.site.size());
                /*BINDING SITES*/
            for (int l = 0; l < bl5.site.size(); l++) {
                Site cure = (Site) bl5.site.get(l);
                List<Integer> list = new ArrayList<Integer>();
                list.add(cure.getGeneVIMSSId());
                List<String> best_match = getKBGeneId(genomeindex, list);
                if (best_match != null && best_match.size() > 0) {
                    String s = cure.getRegulonId() + "\t" + best_match.get(0) + "\t" + cure.getPosition();
                    System.out.println(s);

                    if (output_array[genomeindex][2] == null)
                        output_array[genomeindex][2] = new ArrayList();
                    output_array[genomeindex][2].add(s);
                }
            }


        } catch (IOException e) {
            sr5 = new StringReader(text5);
            bl5s = mapper5.readValue(sr5, new
                    TypeReference<SiteContainer>() {
                    });
            try {
                System.out.println("bl5s " + 1);
                List<Integer> list = new ArrayList<Integer>();
                list.add(bl5s.site.getGeneVIMSSId());
                List<String> best_match = getKBGeneId(genomeindex, list);
                if (best_match != null && best_match.size() > 0) {
                /*BINDING SITE*/
                    String s = bl5s.site.getRegulonId() + "\t" + best_match.get(0) + "\t" + bl5s.site.getPosition();
                    System.out.println(s);

                    if (output_array[genomeindex][2] == null)
                        output_array[genomeindex][2] = new ArrayList();
                    output_array[genomeindex][2].add(s);
                }
            } catch (Exception ee) {
                ee.printStackTrace();
                return 0;
            }
        }

        return 1;
    }

    /**
     * @param client
     * @param curc
     * @throws IOException
     */
    private int getRegulators(Client client, Regulon curc, int genomeindex) throws IOException {
        String s = "http://regprecise.lbl.gov/Services/rest/regulators?regulonId=" + curc.getRegulonId();
        System.out.println(s);
        WebResource resource4 = client.resource(s);
        ClientResponse response4 = resource4.accept("application/json").get(ClientResponse.class);
        int status4 = response4.getStatus();
        String text4 = response4.getEntity(String.class);

        System.out.println("REGULATORS " + status4 + "\n" + text4);
        ObjectMapper mapper4 = new ObjectMapper();

        StringReader sr4 = new StringReader(text4);

        if (text4 != null) {
            RegulatorsContainer bl4 = null;
            RegulatorContainer bl4s = null;
            try {
                bl4 = mapper4.readValue(sr4, new
                        TypeReference<RegulatorsContainer>() {
                        });

                if (bl4 != null && bl4.regulator != null) {

           /*REGULATORS*/
                    try {
                        System.out.println("bl4.regulator.size() " + bl4.regulator.size());
                        for (int l = 0; l < bl4.regulator.size(); l++) {
                            Regulator curd = (Regulator) bl4.regulator.get(l);
                            if (curd != null) {
                                String s1 = curd.getRegulonId() + "\t" + curd.getVimssId() + "\t" + curc.getRegulationType();

                                List<Integer> list = new ArrayList<Integer>();
                                Integer vimss = curd.getVimssId();
                                list.add(vimss);

                                System.out.println("getRegulators " + genome_list[genomeindex] + "\t" + vimss);
                                List<String> best_match = getKBGeneId(genomeindex, list);
                                if (best_match != null && best_match.size() > 0) {
                                    s1 = curd.getRegulonId() + "\t" + best_match.get(0) + "\t" + curc.getRegulationType();

                                    System.out.println(s1);

                                    if (output_array[genomeindex][1] == null)
                                        output_array[genomeindex][1] = new ArrayList();
                                    output_array[genomeindex][1].add(s1);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //return 1;
                    }
                }
            } catch (IOException e) {
                sr4 = new StringReader(text4);
                bl4s = mapper4.readValue(sr4, new
                        TypeReference<RegulatorContainer>() {
                        });
                try {
                    System.out.println("bl4s " + 1);
                /*REGULATORS*/
                    if (bl4s.regulator != null) {
                        List<Integer> list = new ArrayList<Integer>();
                        Integer vimss = bl4s.regulator.getVimssId();
                        list.add(vimss);

                        System.out.println("getRegulators " + genome_list[genomeindex] + "\t" + vimss);

                        List<String> best_match = getKBGeneId(genomeindex, list);
                        if (best_match != null && best_match.size() > 0) {
                            String s1 = bl4s.regulator.getRegulonId() + "\t" + best_match.get(0) + "\t" + curc.getRegulationType();
                            System.out.println(s1);

                            if (output_array[genomeindex][1] == null)
                                output_array[genomeindex][1] = new ArrayList();
                            output_array[genomeindex][1].add(s1);
                        }
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    return 0;
                }
            }
        }
        return 1;
    }

    /**
     * @param genomeindex
     * @param list
     * @return
     */
    private List<String> getKBGeneId(int genomeindex, List<Integer> list) {
        List<String> retlist = new ArrayList<String>();
        try {
            return_moLocusIds_to_fid_in_genome_fast kbgids_return = null;

            while (kbgids_return == null) {
                try {
                    //kbgids_return = translation.moLocusIds_to_fid_in_genome(list, genome_list[genomeindex]);
                    kbgids_return = translation.moLocusIds_to_fid_in_genome_fast(list, genome_list[genomeindex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(500);
            }
            Map<Integer, result> kbgids = kbgids_return.return_1;

            Object[] keys = ((Set) kbgids.keySet()).toArray();
            if (keys.length > 1) {
                for (int k = 0; k < keys.length; k++) { //keys.length; k++) {
                    result res = (result) kbgids.get(keys[k]);
                    System.out.println("kbgids " + (Integer) keys[k] + "\t" + res.best_match);
                    retlist.add(res.best_match);
                }

                return retlist;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return null;
    }

    /**
     * @param client
     */
    private void testClient(Client client) {
        WebResource resource = client.resource("http://regprecise.lbl.gov/Services/rest/regulogCollections?collectionType=taxGroup");
        ClientResponse response = resource.accept("application/json").get(ClientResponse.class);//String.class);
        int status = response.getStatus();
        String text = response.getEntity(String.class);
        System.out.println("testClient " + status + "\t" + response.getType() + "\n" + text);
    }

    /**
     * @throws Exception
     */
    protected void init() throws Exception {
        try {
            translation = new MOTranslation("http://140.221.92.71:7061");
            System.out.println("connected http://140.221.92.71:7061");
            if (translation == null)
                System.out.println("translation == null");
        } catch (MalformedURLException e) {
            throw new Exception("Unable to initialize translation", e);
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            RegPreciseClient rm = new RegPreciseClient(args);
        } else {
            System.out.println("syntax: java util.RegPreciseClient\n"
            );
        }
    }
}

class RegulogCollectionContainer {
    @JsonProperty("regulogCollection")
    public List<RegulogCollection> regulogCollection;
}

class RegulogContainer {
    @JsonProperty("regulog")
    public List<Regulog> regulog;
}

class RegulonContainer {
    @JsonProperty("regulon")
    public List<Regulon> regulon;
}

class RegulatorsContainer {
    @JsonProperty("regulator")
    public List<Regulator> regulator;
}

class SitesContainer {
    @JsonProperty("site")
    public List<Site> site;
}

class GenesContainer {
    @JsonProperty("gene")
    public List<Gene> gene;
}

class RegulatorContainer {
    @JsonProperty("regulator")
    public Regulator regulator;
}

class SiteContainer {
    @JsonProperty("site")
    public Site site;
}

class GeneContainer {
    @JsonProperty("gene")
    public Gene gene;
}

class GenomesContainer {
    @JsonProperty("genome")
    public List<Genome> genome;
}