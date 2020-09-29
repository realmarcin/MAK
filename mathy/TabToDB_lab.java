package mysql;

import util.GiveDate;
import util.ParsePath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;

public class TabToDB_lab {

    String[] names;
    String db = null;
    int numcol;

    Vector one = null;
    String fileplusdir;
    String[] files;

    String delim = "\t";
    String allnames;
    String dbcreate;
    String[] args;
    String user = "marcin";
    String origpath;


    public TabToDB_lab() {

    }

    public TabToDB_lab(String[] a) {
        args = a;

//System.out.println(args.length);
        db = new String(args[0]);
        fileplusdir = new String(args[1]);
        origpath = fileplusdir;

        File test = new File(fileplusdir);

        if (test.isDirectory()) {
            File getdirlist = new File(fileplusdir);
            files = getdirlist.list();

            for (int i = 0; i < files.length; i++) {

                ParsePath pp = new ParsePath(files[i]);
                fileplusdir = origpath + "/" + files[i];
                String curname = db + pp.getName();
                run(curname);
            }
        } else {

            run(db);
        }

    }


    public void run() {

        if (db != null) {

            numcol = args.length - 2;
            names = new String[args.length - 2];
            allnames = new String("");
            dbcreate = new String("");
            System.out.println("name len " + names.length);

            for (int ab = 0; ab < names.length; ab++)
                names[ab] = args[ab + 2];

            int ab = 0;

            for (ab = 0; ab < names.length - 1; ab++) {

//System.out.println(ab+"a "+allnames);
//System.out.println(ab+"c "+dbcreate);
                args[ab + 2] = util.StringUtil.replace(args[ab + 2], " ", "_");
                args[ab + 2] = util.StringUtil.replace(args[ab + 2], "-", "_");
                args[ab + 2] = util.StringUtil.replace(args[ab + 2], ".", "_");

                allnames += args[ab + 2] + ",";
                dbcreate += args[ab + 2] + " TEXT,";
            }
            args[ab + 2] = util.StringUtil.replace(args[ab + 2], " ", "_");
            args[ab + 2] = util.StringUtil.replace(args[ab + 2], "-", "_");
            args[ab + 2] = util.StringUtil.replace(args[ab + 2], ".", "_");

            allnames += args[args.length - 1];
            dbcreate += args[args.length - 1] + " TEXT";
//System.out.println("last a "+allnames);
//System.out.println("last c "+dbcreate);
            GiveDate gd = new GiveDate();
            String dat = gd.giveShortDate();

            try {
                Class.forName("org.gjt.mm.mysql.Driver").newInstance();
                Connection con = DriverManager.getConnection("jdbc:mysql://fern/" + user + "?user=" + user + "&password=tester");

                System.out.println("created connection !!");
                Statement stmt = con.createStatement();

/*
	    if(db.indexOf(".") != -1)
		{
		    String testdb = db.substring(0, db.indexOf("."));
		    String seltest = "show databases like '"+testdb+"'";
		    ResultSet testr = stmt.executeQuery(seltest);
		    if(!testr.next())
			{
			    System.out.println("SPECIFIED DATABASE DOES NOT EXIST !!");
			    System.exit(0);
			}
		}
*/


                String drop = "DROP TABLE IF EXISTS " + db;
                stmt.executeUpdate(drop);
                System.out.println("finished dropping table if it existed !!");
                String create = "CREATE TABLE IF NOT EXISTS " + db + " (id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, " + dbcreate + ")";
                System.out.println("creating  " + create);

                try {

                    PrintWriter out = new PrintWriter(new FileWriter(db + ".sql"), true);
                    out.println(drop + ";\n");
                    out.println("create table {\n");
                    out.println(dbcreate + "\n");
                    out.println("}\n");
                    out.close();
                } catch (IOException e) {
                    System.out.println("problem with writing " + db + ".sql");
                }

                stmt.executeUpdate(create);

//if(files == null)
                readWrite(con, fileplusdir, db);
/*
else
{
for(int i =0; i < files.length; i++)
{
readWrite(con, fileplusdir+files[i]);
}
}
*/
                stmt.close();
                con.close();
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    /**
     * @param newdbname
     */
    public void run(String newdbname) {

        db = newdbname;
        run();
    }

    private void readWrite(Connection c, String file, String newdbname) {
        try {
            Statement stmt = c.createStatement();

            one = new Vector();

            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String data = null;
                StringTokenizer tokens = null;

                data = in.readLine();

                while (data != null) {

                    while (data.indexOf("#") == 0)
                        data = in.readLine();

                    tokens = new StringTokenizer(data, "\t");
                    int all = tokens.countTokens();

                    System.out.println(data + "   has total tokens  " + all);
                    //System.exit(0);
                    String[] store = new String[numcol];
                    int count = 0;

                    //boolean prevtab = false;
                    while (tokens.hasMoreTokens() && count < numcol) {
                        String tmp = tokens.nextToken(delim);

                        //if(!tmp.equals("\t"))
                        //{
                        store[count] = tmp;
                        //count++;
                        //prevtab = false;
                        //    }
                        //else if(prevtab)
                        //    {
                        //store[count] = new String("null");
                        //	count++;
                        //prevtab = true;
                        //	    }
                        count++;
                    }

                    for (int i = 0; i < store.length; i++) {
                        if (store[i] != null) {
                            store[i] = util.StringUtil.replace(store[i], "'", " ");
                            store[i] = util.StringUtil.replace(store[i], "/", "_");
                            store[i] = util.StringUtil.replace(store[i], "\\", "_");
                        } else
                            store[i] = "null";
//System.out.println("FINAL : "+i+"  :  "+store[i]);
                    }

                    int ak = 0;
                    String insertdata = new String("");

                    insertdata += "'" + store[ak];
                    if (numcol > 2) {
                        for (ak = 1; ak < numcol - 1; ak++) {
                            insertdata += "','" + store[ak];
                        }
                        insertdata += "','" + store[ak] + "'";
                        //System.out.println(ak+"  "+insertdata);
                    } else {
                        if (numcol == 1)
                            insertdata += "'";
                        if (numcol == 2) {
                            ak++;
                            insertdata += "','" + store[ak] + "'";
                        }
                    }
                    String insertall = "INSERT INTO " + newdbname + " (" + allnames + ") VALUES (" + insertdata + ")";
                    System.out.println(insertall);
                    stmt.executeUpdate(insertall);

                    data = in.readLine();
                }
            } catch (IOException e) {
                System.out.println("Error with 'translate names' file.");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param argv
     */
    public static void main(String[] argv) {
        if (argv.length >= 3) {
            TabToDB_lab mad = new TabToDB_lab(argv);
        } else {

            System.out.println("syntax:  java gpipe.mysql.TabToDB_lab <dbname> <data file or dir of files> <db column designations separated by white space>\n");
        }
    }
}
