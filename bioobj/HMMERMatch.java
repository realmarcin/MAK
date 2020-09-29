package bioobj;

public class HMMERMatch {


    public String def;
    public int start;
    public int stop;
    public int match;
    public double bits;
    public double evalue;
    public String seq;

    public HMMERMatch() {

        def = null;
        start = -1;
        stop = 1;
        match = -1;
        bits = Double.NaN;
        evalue = Double.NaN;
    }


    public void parseDef(String s) {

        if (s != null && s.length()>0 )
        {
            int col = s.indexOf(":");
            def = s.substring(0, col);

            int dom = s.indexOf(": domain ")+": domain ".length();
            int ofint = s.indexOf(" of ", dom);
            match = Integer.parseInt(s.substring(dom,ofint));

            int fromind = s.indexOf(" from ")+" from ".length();
            int toind = s.indexOf(" to ");
            start = Integer.parseInt(s.substring(fromind, toind));

            toind +=" to ".length();
            int scoreind = s.indexOf(": score ");
            stop = Integer.parseInt(s.substring(toind, scoreind));

            scoreind +=": score ".length();
            int evalind = s.indexOf(", E = ");
            bits = Double.parseDouble(s.substring(scoreind, evalind));

            evalind +=", E = ".length();
            evalue = Double.parseDouble(s.substring(evalind, s.length()));
        }
    }


    public String toString() {

        //return("def"+"\t"+"match"+"\t"+"start"+"\t"+"stop"+"\t"+"bits"+"\t"+"evalue"+"\t"+"seq"+"\n");
        return(def+"\t"+match+"\t"+start+"\t"+stop+"\t"+bits+"\t"+evalue+"\t"+seq) ;

    }


}