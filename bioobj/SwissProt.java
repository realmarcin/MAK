package bioobj;

import java.util.StringTokenizer;

public class SwissProt {
    public static final String adress = "http://expasy.cbr.nrc.ca/cgi-bin/get-sprot-raw.pl?";

    public String id = null;
    public String ac = null;
    public String dt = null;
    public String de = null;
    public String gn = null;
    public String os = null;
    public String oc = null;
    public String ox = null;
    public String rn = null;
    public String rp = null;
    public String rc = null;
    public String rx = null;
    public String ra = null;
    public String rt = null;
    public String rl = null;
    public String cc = null;
    public String dr = null;
    public String kw = null;
    public String ec = null;
    public String ft = null;
    public String sq = null;

    public void Swissprot() {
        id = null;
        ac = null;
        dt = null;
        de = null;
        gn = null;
        os = null;
        oc = null;
        ox = null;
        rn = null;
        rp = null;
        rc = null;
        rx = null;
        ra = null;
        rt = null;
        rl = null;
        cc = null;
        dr = null;
        kw = null;
        ec = null;
        ft = null;
        sq = null;
    }

    public void parseSW(String pass) {
        StringTokenizer tokens = new StringTokenizer(pass);

        int all = tokens.countTokens();

        if (all > 0) {
            String data = tokens.nextToken();
            int count = 1;

            while (count < all) {
                //System.out.println(data+"   "+count);
                if (data.equals("ID") && id == null) {
                    id = tokens.nextToken();
                    count++;
                    data = tokens.nextToken();
                    count++;
                    //System.out.println("ID   "+swid+"   "+count);
                }
                if (data.equals("AC") && ac == null && id != null) {
                    ac = tokens.nextToken();
                    count++;

                    if (ac.indexOf(";") != -1)
                        ac = ac.substring(0, ac.indexOf(";"));
                    data = tokens.nextToken();
                    count++;
                }
                if (data.equals("DT") && dt == null && ac != null) {
                    dt = new String("");

                    int i = 0;
                    while (i < 3) {
                        data = tokens.nextToken();
                        count++;
                        dt += new String(data);

                        while (!data.equals("DT") && i < 2) {
                            data = tokens.nextToken();
                            count++;
                        }
                        i++;
                    }

                    data = tokens.nextToken();
                    count++;

                    /*
           for(i=0; i < 3; i++)
           {
           int ind = dt[i].indexOf("-");

           while(ind != -1)
            {
           //System.out.println("ind  "+ind);
            if(ind != 0)
            dt[i] = new String(dt[i].substring(0, ind)+dt[i].substring(ind+1));
            else
            dt[i] = new String(dt[i].substring(1));
            ind = dt[i].indexOf("-");
            }
           }
           */
/*
		System.out.println("dt   "+dt[0]+"   "+count);
		System.out.println("dt   "+dt[1]+"   "+count);
		System.out.println("dt   "+dt[2]+"   "+count);
*/
                }
                if (data.equals("DE") && de == null && ac != null) {
                    data = tokens.nextToken();
                    count++;
                    de = new String();
                    while (!data.equals("GN") && !data.equals("OS")) {
                        if (!data.equals("DE")) {
                            if (de != null)
                                de = new String(de + " " + data);
                            else
                                de = new String(data);
                        }
                        data = tokens.nextToken();
                        count++;
                    }
                    //	System.out.println("DE   "+de+"   "+count);
                }
                if (data.equals("GN") && de != null && os == null) {
//System.out.println("GN  "+data);
                    data = tokens.nextToken();
                    count++;
                    while (!data.equals("OS")) {
                        if (!data.equals("GN")) {
                            if (gn != null)
                                gn = new String(gn + " " + data);
                            else
                                gn = new String(data);
                        }
                        data = tokens.nextToken();
                        count++;
                    }
//System.out.println("GN   "+data+"   "+count);
                }
                if (data.equals("OS") && os == null && de != null) {
                    data = tokens.nextToken();
                    count++;
                    os = new String(" ");
                    while (data.indexOf(".") == -1) {
                        if (!data.equals("OS"))
                            os = new String(os + " " + data);
                        data = tokens.nextToken();
                        count++;
                    }

                    os = os + " " + data;
                    data = tokens.nextToken();
                    count++;
                    //System.out.println("OS   "+os+"   "+count);
                }

                if (data.indexOf("RL") != -1 && data.length() != 2 && os != null) {
                    data = tokens.nextToken();
                    rl = new String("");
                    count++;
                    while (data.indexOf(".") == -1) {
                        rl = rl + " " + data;
                        data = tokens.nextToken();
                        count++;
                    }

                    rl = rl + " " + data;
                    data = tokens.nextToken();
                    count++;
                }
                if (data.equals("KW") && os != null) {
                    data = tokens.nextToken();
                    kw = new String("");
                    count++;
                    while (data.indexOf(".") == -1) {
                        if (!data.equals("KW"))
                            kw = new String(kw + " " + data);
                        data = tokens.nextToken();
                        count++;
                    }

                    kw = kw + " " + data;
                    data = tokens.nextToken();
                    count++;
//System.out.println("KW   "+kw);

//System.out.println("\n "+data);
                }
                if (data.equals("SQ") && sq == null) {
                    //System.out.println("SQ   "+data+"   "+count);
                    data = tokens.nextToken();
                    count++;
                    data = tokens.nextToken();
                    count++;

                    while ((data.indexOf("CRC64;") == -1 && data.indexOf("CRC32;") == -1) && count < all) {
                        data = tokens.nextToken();
                        count++;
                    }
                    if (count == all)
                        break;

                    data = tokens.nextToken();
                    count++;
                    sq = new String(data);

                    while (count < all) {
                        data = tokens.nextToken();
                        count++;
                        sq = new String(sq + data);
                    }
//System.out.println("sq  "+sq);
                    count = all;

                    int ind = sq.indexOf(" ");
                    while (ind != -1) {
                        sq = new String(sq.substring(0, ind) + sq.substring(ind + 1));
                        ind = sq.indexOf(" ");
                    }

                    ind = sq.indexOf("\n");

                    while (ind != -1) {
                        //System.out.println(ind+"  "+sq.length()+"  "+sq+"\n");
                        if (ind + 1 < sq.length())
                            sq = new String(sq.substring(0, ind) + sq.substring(ind + 1));
                        else
                            sq = new String(sq.substring(0, ind));
                        ind = sq.indexOf("\n");
                    }

                    ind = sq.indexOf("/");
                    while (ind != -1) {	//System.out.println(ind+"  "+sq.length()+"  "+sq+"\n");
                        if (ind + 1 < sq.length())
                            sq = new String(sq.substring(0, ind) + sq.substring(ind + 1));
                        else
                            sq = new String(sq.substring(0, ind));
                        ind = sq.indexOf("/");
                    }

                }
                if (tokens.countTokens() > 0)
                    data = tokens.nextToken();
                count++;
            }


            parseEC();

            System.out.println("id " + id + "\n ac  " + ac + "\n de  " + de + "\n os  " + os + " \n gn  " + gn + "  \n  ec   " + ec);


        }

    }


    private void parseEC() {
        ec = new String("");

        int ind = de.indexOf("(EC ");
        if (ind != -1) {
            ind = ind + 3;
            int next = de.indexOf(")", ind);
            if (next > ind)
                ec = de.substring(ind, next);

            System.out.println("parsed EC   " + ec);
        }
    }

}
