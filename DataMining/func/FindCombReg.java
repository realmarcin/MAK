package DataMining.func;

import DataMining.BlockMethods;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.StringUtil;
import util.TextFile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 8/15/13
 * Time: 2:30 PM
 */
public class FindCombReg {


    HashMap<String, String> funmap = new HashMap<String, String>();
    HashMap<String, String> TFmap = new HashMap<String, String>();
    HashMap<String, String> localmap = new HashMap<String, String>();
    HashMap<String, String> overmap = new HashMap<String, String>();

    double minoverlap = 0.2;
    ValueBlockList vbl;


    /**
     * @param args
     */
    public FindCombReg(String[] args) {

        init(args);

        Set s = overmap.keySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            double curval = Double.parseDouble((String) overmap.get(key));
            if (curval > minoverlap) {
                String[] split = key.split("_");
                //System.out.println(key + "\t" + split[0] + "\t" + split[1]);
                String TFone = TFmap.get(split[0]);
                String TFtwo = TFmap.get(split[1]);

                String Funcone = funmap.get(split[0]);
                try {
                    Funcone = StringUtil.replace(Funcone, "TIGRrole: ", "");
                    Funcone = StringUtil.replace(Funcone, "GO: ", "");
                    Funcone = StringUtil.replace(Funcone, "Path: ", "");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                String Functwo = funmap.get(split[1]);
                try {
                    Functwo = StringUtil.replace(Functwo, "TIGRrole: ", "");
                    Functwo = StringUtil.replace(Functwo, "GO: ", "");
                    Functwo = StringUtil.replace(Functwo, "Path: ", "");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                String Localone = localmap.get(split[0]);
                String Localtwo = localmap.get(split[1]);
                //System.out.println(TFone);
                //System.out.println(TFtwo);
                String[] TFsone = new String[0];
                try {
                    TFsone = TFone.split("_");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                String[] TFstwo = new String[0];
                try {
                    TFstwo = TFtwo.split("_");
                } catch (Exception e) {
                    //e.printStackTrace();
                }

                String[] Funcsone = new String[0];
                try {
                    Funcsone = Funcone.split(" x |_");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                String[] Funcstwo = new String[0];
                try {
                    Funcstwo = Functwo.split(" x |_");
                } catch (Exception e) {
                    //e.printStackTrace();
                }

                String[] Localsone = new String[0];
                try {
                    Localsone = Localone.split("_");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                String[] Localstwo = new String[0];
                try {
                    Localstwo = Localtwo.split("_");
                } catch (Exception e) {
                    //e.printStackTrace();
                }


                if (TFsone != null && TFstwo != null) {
                    int over = 0;
                    String common = "";
                    String diffone = "";
                    for (int i = 0; i < TFsone.length; i++) {
                        int index = StringUtil.getFirstEqualsIndex(TFstwo, TFsone[i]);
                        if (index != -1) {
                            over++;
                            common += TFsone[i] + ",";
                        } else {
                            diffone += TFsone[i] + ",";
                        }
                    }
                    String difftwo = "";
                    for (int i = 0; i < TFstwo.length; i++) {
                        int index = StringUtil.getFirstEqualsIndex(TFsone, TFstwo[i]);
                        if (index != -1) {
                        } else {
                            difftwo += TFstwo[i] + ",";
                        }
                    }

                    String Fcommon = "";
                    String Fdiffone = "";
                    for (int i = 0; i < Funcsone.length; i++) {
                        int index = StringUtil.getFirstEqualsIndex(Funcstwo, Funcsone[i]);
                        if (index != -1) {
                            Fcommon += Funcsone[i] + ",";
                        } else {
                            Fdiffone += Funcsone[i] + ",";
                        }
                    }
                    String Fdifftwo = "";
                    for (int i = 0; i < Funcstwo.length; i++) {
                        int index = StringUtil.getFirstEqualsIndex(Funcsone, Funcstwo[i]);
                        if (index != -1) {
                        } else {
                            Fdifftwo += Funcstwo[i] + ",";
                        }
                    }

                    String Lcommon = "";
                    String Ldiffone = "";
                    for (int i = 0; i < Localsone.length; i++) {
                        int index = StringUtil.getFirstEqualsIndex(Localstwo, Localsone[i]);
                        if (index != -1) {
                            Lcommon += Localsone[i] + ",";
                        } else {
                            Ldiffone += Localsone[i] + ",";
                        }
                    }
                    String Ldifftwo = "";
                    for (int i = 0; i < Localstwo.length; i++) {
                        int index = StringUtil.getFirstEqualsIndex(Localsone, Localstwo[i]);
                        if (index != -1) {
                        } else {
                            Ldifftwo += Localstwo[i] + ",";
                        }
                    }


                    if (diffone.length() > 0 && difftwo.length() > 0) {

                        System.out.println(key + "\t" + curval + "\tcommon " + over +
                                "\tleft one " + (TFsone.length - over) + "\tleft two " + (TFstwo.length - over) +
                                "\tTFone " + TFone + "\tTFtwo " + TFtwo);

                        ValueBlock one = (ValueBlock) vbl.get(Integer.parseInt(split[0]) - 1);
                        ValueBlock two = (ValueBlock) vbl.get(Integer.parseInt(split[1]) - 1);

                        System.out.println("one " + one.genes.length + "\t" + one.exps.length);
                        System.out.println("two " + two.genes.length + "\t" + two.exps.length);

                        double gover = BlockMethods.computeBlockOverlapGeneSum(one, two);
                        double goverNum = BlockMethods.computeBlockOverlapGeneNum(one, two);
                        double eover = BlockMethods.computeBlockOverlapExpSum(one, two);
                        double eoverNum = BlockMethods.computeBlockOverlapExpNum(one, two);
                        System.out.println("gover " + gover + "\teover " + eover + "\tgoverNum " + goverNum + "\teoverNum " + eoverNum);

                        System.out.println("common " + common + "\tdiff1 " + diffone + "\tdiff2 " + difftwo +
                                //"\tF1 " + Funcone + "\tF2 " + Functwo + "\tL1 " + Localone + "\tL2 " + Localtwo+
                                "\tF1 " + Fdiffone + "\tF2 " + Fdifftwo + "\tL1 " + Ldiffone + "\tL2 " + Ldifftwo);
                    }
                }
            }

        }
    }

    /**
     * @param args
     */
    private void init(String[] args) {

        String[] overdata = TextFile.readtoArray(args[0]);
        String[] TFdata = TextFile.readtoArray(args[1]);
        String[] fundata = TextFile.readtoArray(args[2]);
        String[] localdata = TextFile.readtoArray(args[3]);

        for (int i = 1; i < fundata.length; i++) {
            System.out.println(i + "\t" + fundata[i]);
            String[] now = fundata[i].split(" = ");
            if (now.length > 1 && !now[1].equals("none")) {
                funmap.put(now[0], now[1]);
            }
        }

        for (int i = 1; i < TFdata.length; i++) {
            String[] now = TFdata[i].split(" = ");
            if (!now[1].equals("none")) {
                TFmap.put(now[0], now[1]);
            }
        }

        for (int i = 1; i < localdata.length; i++) {
            System.out.println(i + "\t" + localdata[i]);
            String[] now = localdata[i].split(" = ");
            if (!now[1].equals("none")) {
                localmap.put(now[0], now[1]);
            }
        }

        for (int i = 1; i < overdata.length; i++) {
            String[] now = overdata[i].split(" = ");
            //overmap.put(StringUtil.replace(now[0], " ((pp)) ", "_"), now[1]);
            overmap.put(StringUtil.replace(now[0], " ((pp)) ", "_"), now[1]);
        }

        try {
            vbl = ValueBlockListMethods.readAny(args[4], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 5) {
            FindCombReg rm = new FindCombReg(args);
        } else {
            System.out.println("syntax: java DataMining.func.FindCombReg\n" +
                    "<overlap data>\n" +
                    "<TF path>\n" +
                    "<func path>\n" +
                    "<localization path>\n" +
                    "<vbl>"
            );
        }
    }
}
