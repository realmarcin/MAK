package dtype;

import java.io.*;
import java.util.Vector;

public class EvolDataList {
    public Vector famarray;
    public int listsize;

    /**
     * basic constructor
     */
    public EvolDataList() {
        famarray = new Vector();
        listsize = -1;
    }

    /**
     * function to fill the array of EvolData objects from a String
     * specifying a file
     */
    public void readList(String f) {
        EvolData evd = new EvolData();
        famarray = new Vector();
        try {
            Reader r = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            StreamTokenizer tokens = new StreamTokenizer(r);
            int tokType;
            int count = 0;
            int place = 0;
            boolean end = false;
            tokens.eolIsSignificant(true);
            tokens.slashSlashComments(true);

            try {
                do {
                    tokType = tokens.nextToken();
                    count++;

                    if (tokType == StreamTokenizer.TT_EOL) {
                        count = 0;
                    }
                    if (tokType == StreamTokenizer.TT_EOF) {
                        end = true;
                    } else {
                        if (count == 1) {
                            evd = new EvolData();
                            evd.updateTree(tokens.sval);
                            //list[place][0] = tokens.sval;
                        }
                        if (count == 2) {
                            evd.updateProtSeqs(tokens.sval);
                            //list[place][1] = tokens.sval;
                        }
                        if (count == 3) {
                            evd.updatePdb(tokens.sval);
                            //list[place][2] = tokens.sval;
                        }
                        if (count == 4) {
                            evd.updateMatchName(tokens.sval);
                            //list[place][3] = tokens.sval;
                            famarray.addElement(evd);
                            count = 0;
                            place++;
                            evd = new EvolData();
                        }
                    }
                } while (end == false);
            } catch (IOException e) {
                System.out.println("Error with file.");
            }
        } catch (IOException e) {
            System.out.println("ERROR: FILE NOT FOUND");
        }

        if (famarray.size() > 0)
            listsize = famarray.size();
    }
}