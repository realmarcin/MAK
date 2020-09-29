package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class with a method to read a text file into a String.
 */
public class ReadTabFile {

    public final static ArrayList read(String f) {

        ArrayList store = new ArrayList();

        try {

            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();

            while (line != null) {

//System.out.println(" ReadTabFile "+line);
                //StringTokenizer tok = new StringTokenizer(line, "\t");

                //int all = tok.countTokens();
                //System.out.println(all);

                ArrayList add = new ArrayList();
                ArrayList inds =  StringUtil.countOccurtoList(line, "\t");
                int prev =0;

                for(int i=0;i<inds.size();i++) {

                int cur = ((Integer)inds.get(i)).intValue();
                //System.out.println("doing ind "+cur);
                    if(i==0)
                        add.add((String) line.substring(0,cur));
                    else
                        add.add((String) line.substring(prev,cur));


                    //System.out.println("added "+(string)add.get(add.size()-1));
                    prev=cur;
                }

                add.add((String)line.substring(prev,line.length()));

                store.add((ArrayList) add);
                line = in.readLine();
            }

            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }


        store = removeTabs(store);
        return store;
    }

    /**
     *
     * @param a
     * @return
     */
    private final static ArrayList removeTabs(ArrayList a) {

       for(int i=0;i<a.size();i++)   {

        final ArrayList two = (ArrayList)a.get(i);

         for(int j=0;j<two.size();j++)   {

             String cur = (String)two.get(j);
             cur = StringUtil.replace(cur,"\t","");
             two.set(j,(String)cur);
             a.set(i,(ArrayList)two);                               
         }

         }

        return a;
    }

    /**
     *
     * @param f
     * @return
     */
    public final static String[][] readto2D(String f) {


        ArrayList a = read(f);

        ArrayList first = (ArrayList) a.get(0);

        String[][] store = new String[a.size()][first.size()];


        for (int i = 0; i < a.size(); i++) {

            ArrayList now = (ArrayList) a.get(i);

            for (int j = 0; j < now.size() - 1; j++) {

                store[i][j] = (String) now.get(j);
            }
        }
        return store;
    }

}
