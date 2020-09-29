package DataMining.func;

import util.TabFile;
import util.TextFile;

import java.sql.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jan 19, 2012
 * Time: 6:57:59 PM
 */
public class ValidGenomes {


    String db = "genomics_test";
    public final static String localhost_str = "jdbc:mysql://localhost/";
    String server = localhost_str;

    /**
     * @param args
     */
    public ValidGenomes(String[] args) {
        String[] data = TextFile.readtoArray(args[0]);

        String[] out = new String[data.length];
        String q1a = "select isPartial,isActive,isCircular,comment,length from Scaffold where taxonomyId=";
        //String q1b = "";

        try {

            Connection con = createConnection(server, db, "test", "test");
            Statement stmt = createStatement(con);
            for (int i = 0; i < data.length; i++) {
                String sel = q1a + data[i];// + " " + q1b;
                System.out.println("query " + i + "\t" + sel);
                ResultSet rs = stmt.executeQuery(sel);
                int countpart = 0;
                int countact = 0;
                int countcirc = 0;
                int countplas = 0;
                String com = "";
                while (rs.next()) {
                    int cur = rs.getInt(1);
                    if (cur == 1)
                        countpart++;
                    int cur2 = rs.getInt(2);
                    if (cur2 == 1)
                        countact++;
                    int cur3 = rs.getInt(3);
                    if (cur3 == 1)
                        countcirc++;

                    String s = rs.getString(4);
                    int length = rs.getInt(5);
                    if (s.indexOf("plasmid") != -1 || s.indexOf("Plasmid") != -1) {
                        countplas++;
                        if (com.length() == 0)
                            com = s;
                        else
                            com = ", " + s;
                    }
                }

                out[i] = data[i] + "\t" + countpart + "\t" + countact + "\t" + countcirc + "\t" + countplas + "\t" + com;
                System.out.println("result " + out[i]);
                /*if (countpart > 0)
                    out[i] = "partial";
                else
                    out[i] = "complete";*/

            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TabFile.write(out, args[1]);
    }

    /**
     * @param custom_db_url
     * @return
     * @throws SQLException
     */
    public static Connection createConnection(String custom_db_url, String custom_db, String user, String pwd) throws SQLException {
        try {
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } /*catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        String url = custom_db_url + custom_db + "?user=" + user + "&password=" + pwd;
        System.out.println("createConnection SQL createStatement " + url);
        Connection con = DriverManager.getConnection(url);
        return con;
    }

    /**
     * @return
     * @throws SQLException
     */
    public static Statement createStatement(Connection con) throws SQLException {
        return con.createStatement();
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 2) {
            ValidGenomes rm = new ValidGenomes(args);
        } else {
            System.out.println("syntax: java DataMining.func.ValidGenomes\n" +
                    "<file with data item per row>\n" +
                    "<outfile>"
            );
        }
    }
}
