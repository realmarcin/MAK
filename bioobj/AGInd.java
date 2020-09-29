package bioobj;


public class AGInd {

    public long cstart = -1, cstop = -1;
    public String refseq, contig, strand;

    public int start = -1, stop = -1, agind = -1;


    public AGInd(String curexon) {

        String sstart = null, sstop = null, sagind = null;

        int und2 = curexon.indexOf("_");
        und2 = curexon.indexOf("_", und2 + 1);

        int ntind = curexon.indexOf("NT_");
        int undind = curexon.indexOf("_", ntind + 3);
        int startind = curexon.indexOf("_", undind + 1);
        int endind = curexon.indexOf("_", startind + 1);
        int finalind = curexon.indexOf("_", endind + 1);
        int stopind = curexon.indexOf("_", finalind + 1);
        int lastund = curexon.lastIndexOf("_");

        if (lastund == stopind)
            lastund = -1;

        int find = curexon.indexOf(" ", stopind + 1);
        int end = curexon.indexOf(" ", find + 1);

        if (end == -1)
            end = curexon.length();

        if (find == -1)
            find = curexon.length();

        //System.out.println("AGInd " + curexon);
        //System.out.println("AGInd " + finalind + "\t" + stopind + "\t" + curexon.length());
        //System.out.println("AGInd "+endind+"\t"+finalind+"\t"+stopind+"\t"+find+"\t"+lastund);

        refseq = curexon.substring(0, und2);
        contig = curexon.substring(ntind, undind);

        //System.out.println(curexon.substring(startind+1,endind));

        try {
            strand = curexon.substring(undind + 1, startind);
            cstart = Long.parseLong(curexon.substring(startind + 1, endind));
            cstop = Long.parseLong(curexon.substring(endind + 1, finalind));

            if (stopind != -1)
                sstart = curexon.substring(finalind + 1, stopind);
            else
                sstart = curexon.substring(finalind + 1, curexon.length());
            //System.out.println("AGInd start "+start);

            sstop = curexon.substring(stopind + 1, lastund);
            sagind = curexon.substring(lastund + 1, end);
            agind = Integer.parseInt(util.StringUtil.replace(sagind, " ", ""));

        } catch (Exception e) {

            /*
            cstart=Long.parseLong(curexon.substring(endind+1,finalind));
            cstop=Long.parseLong(curexon.substring(finalind+1,stopind));
            sstart=curexon.substring(stopind+1,lastund);
            sstop = curexon.substring(lastund+1, end);
            */
        }


        if (sstart != null)
            start = Integer.parseInt(util.StringUtil.replace(sstart, " ", ""));
        if (sstop != null)
            stop = Integer.parseInt(util.StringUtil.replace(sstop, " ", ""));

//System.out.println("AGInd "+refseq+"\t"+contig+"\t"+cstart+"\t"+cstop+"\t"+sstart+"\t"+sstop+"\t"+sagind);

    }


    public String toString() {

        return ("AGInd " + refseq + "\t" + contig + "\t" + cstart + "\t" + cstop + "\t" + start + "\t" + stop + "\t" + agind);
    }

}
