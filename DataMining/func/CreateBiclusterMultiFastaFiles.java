package DataMining.func;

import util.MapArgOptions;
import util.MoreArray;

import java.io.*;
import java.util.HashMap;

/**
 * Created by rauf on 5/7/15.
 */
public class CreateBiclusterMultiFastaFiles {

    boolean debug = false;
    boolean only_upstream = false;

    String[] valid_args = {
            "-microbes_online_tab", "-microbes_online_genome_fasta", "-gene_ids", "-vbl", "-output_directory", "-output_prefix", "-upstream_length", "-only_upstream", "-debug"//"-sum",
    };

    HashMap options;

    String outdir = "";
    String outprefix = "";
    String tab_file = null;
    String genome = null;
    String vbl_file = null;
    String gene_file = null;

    int upstream_length = 0;

    HashMap fasta_dict = new HashMap<String, String>();
    HashMap sequence_dict = new HashMap<String, String>();
    HashMap loci_dict = new HashMap<String, String>();

    public CreateBiclusterMultiFastaFiles(String[] args) {
        try {
            init(args);
            readGenomeFasta();
            createSequenceDict();
            mapGeneIndicesToLociId();
            createBiclusterMultiFasta();

            System.out.println("Done!");

        } catch (Exception
                e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    private void createBiclusterMultiFasta() throws IOException {
        FileInputStream fstream = new FileInputStream(vbl_file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line;
        int count = 0;
        while ((line = br.readLine()) != null) {
            if (count != 0) {
                line = line.replaceAll("\"", "");
                String[] ls = line.split("\t");
                String suffix = "_bicluster_" + ls[0] + ".fasta";
                String[] gene_ids = ls[2].split("/")[0].split(",");
                PrintWriter writer = new PrintWriter(outdir + outprefix + suffix, "UTF-8");
                for (int i = 0; i < gene_ids.length; i++) {
                    String gene_id = gene_ids[i];
                    String loci_id = (String) loci_dict.get(gene_id);
                    String gene_seq = (String) sequence_dict.get(loci_id);
                    writer.println(">" + loci_id);
                    writer.println(gene_seq);
                }
                writer.close();
            }
            count++;
        }
        fstream.close();
    }

    private void mapGeneIndicesToLociId() throws IOException {
        FileInputStream fstream = new FileInputStream(gene_file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line;
        int count = 0;
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("\"", "");
            String[] ls = line.split("\\s+");
            loci_dict.put(ls[0], ls[1]);
        }
        fstream.close();
    }

    private void readGenomeFasta() throws IOException {
        FileInputStream fstream = new FileInputStream(genome);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line;
        String seq = "";
        String header = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith(">")) {
                seq += line;
            } else {
                if (header.compareTo("") != 0) {
                    fasta_dict.put(header, seq);
                }
                header = line.substring(1);
                header = header.split("\\s+")[0];
                seq = "";
            }
        }
        fstream.close();
        if (header.compareTo("") != 0) {
            fasta_dict.put(header, seq);
        }
    }

    private void createSequenceDict() throws IOException {
        PrintWriter writer1 = new PrintWriter(outdir + outprefix + "_all_seqs.fasta", "UTF-8");
        FileInputStream fstream = new FileInputStream(tab_file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String line;
        int count = 0;
        while ((line = br.readLine()) != null) {
            if (count != 0) {
                String[] ls = line.split("\t");
                String lociId = ls[7];
                String scaffoldId = ls[3];
                int start = Integer.parseInt(ls[4]);
                int stop = Integer.parseInt(ls[5]);
                String strand = ls[6];

                String seq = (String) fasta_dict.get(scaffoldId);
                String gene_seq = "";
                if (strand.compareTo("-") == 0) {
                    int gene_start = start;
                    start = start + upstream_length;

                    String tmp = "";
                    if (only_upstream) {
                        if (gene_start < start) {
                            if ((start-1) >= seq.length()) {
                                tmp = seq.substring(gene_start+9);
                            } else {
                                tmp = seq.substring(gene_start+9, start-1);
                            }
                        }
                    } else if (start >= seq.length()) {
                        tmp = seq.substring(stop - 1);
                    } else {
                        tmp = seq.substring(stop - 1, start);
                    }
                    tmp = new StringBuilder(tmp).reverse().toString().toUpperCase();
                    HashMap myMap = new  HashMap<String, String>();
                    myMap.put('A', 'T');
                    myMap.put('C', 'G');
                    myMap.put('G', 'C');
                    myMap.put('T', 'A');
                    myMap.put('N', 'N');
                    for (int i = 0; i < tmp.length(); i++) {
                        gene_seq += myMap.get(tmp.charAt(i));
                    }
                } else if (strand.compareTo("+") == 0) {
                    int gene_start = start;
                    start = start - upstream_length;
                    if (start < 1) {
                        start = 1;
                    }
                    String tmp = "";
                    if (only_upstream) {
                        if (start < gene_start) {
                            tmp = seq.substring(start, gene_start-10);
                        }
                    } else if (stop == seq.length()) {
                        tmp = seq.substring(start - 1);
                    } else {
                        tmp = seq.substring(start - 1, stop);
                    }
                    gene_seq = tmp;
                }
                sequence_dict.put(lociId, gene_seq);
                writer1.println(">" + lociId);
                writer1.println(gene_seq);
            }
            count++;
        }
        fstream.close();
        writer1.close();
    }

    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-output_directory") != null) {
            outdir = (String) options.get("-output_directory");
        }
        if (options.get("-output_prefix") != null) {
            outprefix = (String) options.get("-output_prefix");
        }
        if (options.get("-microbes_online_tab") != null) {
            tab_file = (String) options.get("-microbes_online_tab");
        }
        if (options.get("-vbl") != null) {
            vbl_file = (String) options.get("-vbl");
        }
        if (options.get("-microbes_online_genome_fasta") != null) {
            genome = (String) options.get("-microbes_online_genome_fasta");
        }
        if (options.get("-gene_ids") != null) {
            gene_file = (String) options.get("-gene_ids");
        }
        if (options.get("-upstream_length") != null) {
            upstream_length = Integer.parseInt((String) options.get("-upstream_length"));
        }
        if (options.get("-only_upstream") != null) {
            String only_upstream_flag = (String) options.get("-only_upstream");
            only_upstream = false;
            if (only_upstream_flag.compareTo("y") == 0) {
                only_upstream = true;
            }
        }
        if (options.get("-debug") != null) {
            String debug_flag = (String) options.get("-debug");
            debug = false;
            if (debug_flag.compareTo("y") == 0) {
                debug = true;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(args);
        if (args.length >= 5) {
            CreateBiclusterMultiFastaFiles rm = new CreateBiclusterMultiFastaFiles(args);
        } else {
            System.out.println("syntax: java DataMining.func.CreateBiclusterMultiFastaFiles\n" +
                            "<-microbes_online_tab>\n" +
                            "<-microbes_online_genome_fasta>\n" +
                            "<-vbl\n" + //change to vbl
                            "<-gene_ids>\n" +
                            "<-ouput_prefix>\n" +
                            "<-upstream_length>\n" +
                            "<-only_upstream>\n" +
                            "<-output_directory>\n" +
                            "<-debug y/n OPTIONAL>"
            );
        }
    }
}