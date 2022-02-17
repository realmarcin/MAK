package util;

import mathy.stat;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Version 1.0, 10/98 - original version
 *
 * @author Marcin
 * @version 1.0, 10/98
 */
public class StringUtil {


    /**
     * Finds the last index of String x in String s before index l.
     *
     * @param s
     * @param x
     * @param l
     * @return last index
     */
    public final static int lastIndexBefore(String s, String x, int l) {
        //System.out.println("s: "+s+"\tx: "+x);
        int ret = s.indexOf(x);
        int last = -1;

        if (ret < l)
            while (ret < l && ret != -1 && ret != last) {
                //System.out.println("lastIndexBefore: "+ret+"\tmax: "+l);
                last = ret;
                if (ret + 1 < s.length())
                    ret = s.indexOf(x, ret + 1);
            }
        ret = last;

        if (ret > l || ret >= s.length())
            ret = -1;

        //System.out.println("Returning "+ret);
        return last;
    }

    /**
     * Returns a 'null' label if the argument is null.
     */
    public final static String testifNull(String s) {

        if (s == null)
            return "null";
        else
            return s;
    }

    /**
     * Returns a 'null' label if the argument is null.
     */
    public final static StringBuffer testifNull(StringBuffer s) {

        if (s == null)
            return new StringBuffer("null");
        else
            return s;
    }

    /**
     * Method that returns the index of the next nonwhite space character from the String.
     */
    public final static int nextNonWhite(String s, int a) {

        int ret = -1;
        for (int i = a; i < s.length() - 1; i++) {

            //System.out.println(i+"\t"+s.substring(i, i+1));

            if (!s.substring(i, i + 1).equals(" ")) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    /**
     * replace all occurences of String 'a' in master with
     * String 'b', returning a new String.
     *
     * @see String
     * @see String#replace
     */
    public final static String replace(String master, String a, String b) {
        int al = a.length();
        //System.out.println("replace " + master + "\ta " + a + "\tb " + b);
        char[] tc = master.toCharArray();
        StringBuffer rsb = new StringBuffer(tc.length);
        int start = 0;
        int end;
        while (start < tc.length) {
            end = master.indexOf(a, start);
            //System.out.println("replace end " + end+"\t"+master);
            if (end == -1)
                end = tc.length;
            rsb.append(tc, start, end - start);
            if (end < tc.length)
                rsb.append(b);
            start = end + al;
        }
        //System.out.println("replace " + rsb.toString());
        return rsb.toString();
    }

    /**
     * replace all occurences of String 'a' in master with
     * String 'b', returning a new String.
     *
     * @see String
     * @see String#replace
     */
    public final static String[] replace(String[] master, String a, String b) {
        for (int i = 0; i < master.length; i++)
            master[i] = replace(master[i], a, b);
        return master;
    }

    /**
     * replace all occurences of String 'a' in master with
     * String 'b', returning a new String.
     *
     * @see String
     * @see String#replace
     */
    public final static StringBuffer replace(StringBuffer master, String a, String b) {
        int al = a.length();
///System.out.println("master "+master+"\ta "+a+"\tb "+b);
        char[] tc = (new String(master)).toCharArray();
        StringBuffer rsb = new StringBuffer(tc.length);
        int start = 0;
        int end;
        while (start < tc.length) {
            end = master.indexOf(a, start);
            if (end == -1) end = tc.length;
            rsb.append(tc, start, end - start);
            if (end < tc.length) rsb.append(b);
            start = end + al;
        }
        return rsb;
    }

    /**
     * replace all occurences of String 'a' in a master with
     * String 'b', returning a new String.
     *
     * @see String
     * @see String#replace
     */
    public final static String replace(String master, String[] a, String b) {
        for (int i = 0; i < a.length; i++) {
            master = replace(master, a[i], b);
        }
        return master;
    }


    /**
     * @param master
     * @return
     */
    public final static String[] camelCase(String[] master) {
        for (int i = 0; i < master.length; i++)
            master[i] = camelCase(master[i]);
        return master;
    }


    /**
     * @param master
     * @return
     */
    public final static String camelCase(String master) {
        StringBuilder sb = new StringBuilder();
        for (String oneString : master.split(" ")) {
            if (oneString.length() > 0) {
                //System.out.println("^" + oneString + "^");
                sb.append(oneString.substring(0, 1).toUpperCase());
                sb.append(oneString.substring(1).toLowerCase());
                //System.out.println(sb);
            }
        }
        return sb.toString();
    }

    /**
     * @param master
     * @return
     */
    public final static String[] stringify(String[] master) {
        for (int i = 0; i < master.length; i++)
            master[i] = stringify(master[i]);
        return master;
    }


    /**
     * @param master
     * @return
     */
    public final static String stringify(String master) {
        StringBuilder sb = new StringBuilder();
        for (String oneString : master.split(" ")) {
            if (oneString.length() > 0) {
                //System.out.println("^" + oneString + "^");
                sb.append(oneString.substring(0, 1).toLowerCase());
                sb.append(oneString.substring(1).toLowerCase());
                //System.out.println(sb);
            }
        }
        return sb.toString();
    }

    /**
     * @param master
     * @return
     */
    public final static String[] lowerCase(String[] master) {
        for (int i = 0; i < master.length; i++)
            master[i] = master[i].toLowerCase();
        return master;
    }


    /**
     * Trims a String from the right.
     */

    public final static String trimR(String k) {
        int spa = k.lastIndexOf(" ");
        //System.out.println("ProSet spa "+spa+"\t"+thisSeqName.length());
        while (spa == k.length() - 1) {
            k = k.substring(0, k.length() - 1);
            spa = k.lastIndexOf(" ");
        }
        return k;
    }

    /**
     * Trims a String from the left.
     */
    public final static String trimL(String k) {
        int spa = k.indexOf(" ");
        //System.out.println("ProSet spa "+spa+"\t"+thisSeqName.length());
        while (spa == 0) {
            k = k.substring(1);
            spa = k.indexOf(" ");
        }
        return k;
    }

    public final static String trim(String t) {

        t = trimR(t);
        t = trimL(t);
        return t;
    }

    /**
     * Returns the length of the longest String in the Vector.
     */
    public final static int maxString(Vector v) {
        int ret = -1;
        for (int i = 0; i < v.size(); i++) {
            int m = ((String) v.elementAt(i)).length();
            if (ret < m) {
                ret = m;
            }
        }
        return ret;
    }

    /**
     * Returns the length of the longest String in the ArrayList.
     */
    public final static int maxStringfromList(ArrayList al) {
        int ret = -1;
        for (int i = 0; i < al.size(); i++) {
            int m = ((String) al.get(i)).length();
            if (ret < m) {
                ret = m;
            }
        }
        return ret;
    }

    /**
     * Returns the first index of the element of the String array which contains the String.
     *
     * @param in
     * @param id
     * @return first index
     */
    public final static int getFirstIndexOf(String id, String[] in, String left, String right) {
        for (int i = 0; i < in.length; i++) {
            in[i] = left + in[i] + right;
        }
        for (int i = 0; i < in.length; i++) {
            if (id.indexOf(in[i]) != -1)
                return i;
        }
        return -1;
    }

    /**
     * Returns the first index of the element of the String array which contains the String.
     *
     * @param in
     * @param id
     * @return first index
     */
    public final static int getFirstIndexOf(String in, String[] id) {
        for (int i = 0; i < id.length; i++) {
            if (id[i] != null)
                if (in.indexOf(id[i]) != -1)
                    return i;
        }
        return -1;
    }

    /**
     * Returns the first index of the element of the String array which contains the String.
     *
     * @param in
     * @param id
     * @param suffix
     * @return
     */
    public final static int getFirstIndexOf(String in, String[] id, String suffix) {
        for (int i = 0; i < id.length; i++) {
            if (id[i] != null)
                if (in.indexOf(id[i]) != -1 && id[i].endsWith(suffix))
                    return i;
        }
        return -1;
    }

    /**
     * @param in
     * @param id
     * @return
     */
    public final static int getFirstEqualsIndex(String[] in, String id) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] != null)
                if (in[i].equals(id))
                    return i;
        }
        return -1;
    }

    /**
     * @param in
     * @param id
     * @return
     */
    public final static int getFirstEqualsIndexIgnoreCase(String[] in, String id) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] != null)
                if (in[i].equalsIgnoreCase(id))
                    return i;
        }
        return -1;
    }

    /**
     * @param in
     * @param id
     * @return
     */
    public final static int getFirstEqualsIgnoreCaseIndex(String[] in, String id) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] != null)
                if (in[i].equalsIgnoreCase(id))
                    return i;
        }
        return -1;
    }

    /**
     * Returns the first index of the element of the StringBuffer array which contains the String.
     *
     * @param in
     * @param id
     * @return first index
     */
    public final static StringBuffer getFirstIndexOf(StringBuffer[] in, String id) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] != null)
                if (in[i].indexOf(id) != -1)
                    return in[i];
        }
        return null;
    }

    /**
     * Returns the first index of the element of the String array which starts with the String.
     *
     * @param in
     * @param id
     * @return
     */
    public final static int getFirstStartsWith(String in, String[] id) {
        for (int i = 0; i < id.length; i++) {
            if (id[i] != null)
                if (in.startsWith(id[i]))
                    return i;
        }
        return -1;
    }

    /**
     * Returns the first index of the element of the String array which starts with the String.
     *
     * @param in
     * @param id
     * @return
     */
    public final static String getFirstStartsWith(String[] in, String id) {
        for (int i = 0; i < in.length; i++) {
            if (in[i].startsWith(id))
                return in[i];
        }
        return null;
    }

    /**
     * Returns the first index of the element of the String array which starts with the String.
     *
     * @param in
     * @param id
     * @return
     */
    public final static int getFirstStartsWithRetInt(String[] in, String id) {
        for (int i = 0; i < in.length; i++) {
            if (in[i].startsWith(id))
                return i;
        }
        return -1;
    }

    /**
     * Removes the specified character from the String array.
     *
     * @param in
     * @param rem
     * @return
     */
    public final static String[] removeChar(String[] in, String rem) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] != null && in[i].indexOf(rem) != -1)
                in[i] = StringUtil.replace(in[i], rem, "");
        }
        return in;
    }

    /**
     * Removes the specified character from the StringBuffer array.
     *
     * @param in
     * @param rem
     * @return
     */
    public final static StringBuffer[] removeChar(StringBuffer[] in, String rem) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] != null && in[i].indexOf(rem) != -1)
                in[i] = StringUtil.replace(in[i], rem, "");
        }
        return in;
    }


    /**
     * @param data
     * @param query
     * @param delim
     * @return
     */
    public final static String parse(String data, String query, String delim) {
        int start = data.indexOf(query) + query.length();
        //int end = data.length();
        //if(delim!=null && delim!="")
        int end = data.indexOf(delim, start + query.length());
        if (end == -1)
            end = data.length();
        if (start != -1 && end != -1)
            return data.substring(start, end);
        else
            return null;
    }

    /**
     * @param data
     * @param query
     * @param delim
     * @return
     */
    public final static StringBuffer parse(StringBuffer data, String query, String delim) {
        int q = data.indexOf(query);
        int d = data.indexOf(delim, q + query.length());
        if (q != -1 && d != -1)
            return new StringBuffer((data.toString()).substring(q, d));
        else
            return null;
    }

    /**
     * @param data
     * @param query
     * @param delim
     * @return
     */
    public final static String parse(String data, String query, String delim, int start) {
        System.out.println("WARNING: not guaranteed to return all strings if strings not unique");
        int q = data.indexOf(query, start);
        int d = data.indexOf(delim, q + query.length() + 1);
        if (q != -1 && d != -1 && q + query.length() < d)
            return data.substring(q + query.length(), d);
        else
            return null;
    }

    /**
     * @param data
     * @param query
     * @param delim
     * @return
     */
    public final static StringBuffer parse(StringBuffer data, String query, String delim, int start) {
        int q = data.indexOf(query, start);
        int d = data.indexOf(delim, q + query.length());
        if (q != -1 && d != -1)
            return new StringBuffer(data.substring(q + query.length(), d));
        else if (q != -1) {
            //System.out.println("LAST parse data " + data);
            //System.out.println("LAST parse " + data.substring(q + query.length(), data.length()));
            return new StringBuffer(data.substring(q + query.length(), data.length()));
        } else
            return null;
    }

    /**
     * @param data
     * @param query
     * @return
     */
    public final static StringBuffer[] parsetoStringBuffer(StringBuffer data, String query) {
        int all = countOccur(data.toString(), query);
        //System.out.println("query :" + query + ":\tcountOccur " + all);
        ArrayList store = new ArrayList();
        if (all > 1) {
            int count = 1;

            int limit = data.indexOf(query);
            StringBuffer first = new StringBuffer(data.substring(0, limit));
            int lastlimit = limit;
            while (first.length() <= 3) {

                limit = data.indexOf(query, limit + 1);
                first = new StringBuffer(data.substring(lastlimit, limit));
                lastlimit = limit;
            }

            store.add(first);
            //ret[0] = new StringBuffer(data.substring(0, limit));
            //System.out.println(count + "\t" + ret[0]);
            StringBuffer cur = parse(data, query, query, limit);
            //limit = data.indexOf(query, limit + 1);

            while (cur != null) {

                //SystemMethods.
                //ret[count] = cur;
                store.add(cur);
                cur = parse(data, query, query, limit + cur.length());
                //System.out.println("parsed :"+cur+":");
                limit = data.indexOf(query, limit + 1);
                count++;
                //if (cur != null)
                //    System.out.println("member "+count + "\t" + cur + "\t" + data.indexOf(cur) + cur.length());
            }
        }
        StringBuffer[] ret = util.MoreArray.convtoStringBuffer(store);
        //System.out.println("LAST "+data.substring(data.lastIndexOf(" "), data.length())+"\tlast parsed "+ret[ret.length-1]);
        //new StringBuffer[store.size()];
        return ret;
    }

    /**
     * Parses a string based on the query and returns a string array with query matches removed.
     *
     * @param data
     * @param query
     * @return
     */
    public final static String[] parsetoArray(String data, String query) {
        int[] index = occurIndex(data, query);
        /* System.out.println("parsetoArray index");
        MoreArray.printArray(index);*/
        ArrayList store = new ArrayList();
        if (index.length > 0) {
            store.add(data.substring(0, index[0]));
            for (int i = 0; i < index.length - 1; i++) {
                store.add(data.substring(index[i] + query.length(), index[i + 1]));
            }
            store.add(data.substring(index[index.length - 1] + 1, data.length()));
        } else
            store.add(data);
        return MoreArray.ArrayListtoString(store);
    }


    /**
     * Parses a string based on CSV format and returns a string array with query matches removed.
     *
     * @param data
     * @return
     */
    public final static String[] parsetoArrayCSV(String data) {
        /* System.out.println("parsetoArray index");
        MoreArray.printArray(index);*/
        ArrayList store = new ArrayList();
        boolean quote = true;
        int i = 0;
        int indcomma = data.indexOf(",");
        int indnextcomma = data.indexOf(",", indcomma + 1);
        int indquotestart = data.indexOf(",\"");
        int indquoteend = data.indexOf("\",", indquotestart + 2);
        //just commas
        if (indquoteend == -1) {
            return parsetoArray(data, ",");
        }
        //no parsing
        else if (indcomma == -1) {
            String[] ret = new String[1];
            ret[0] = data;
            return ret;
        }
        //csv
        else {
            boolean first = true;
            while (data.length() > 0) {
                //System.out.println(data);
                if (first) {
                    String s = data.substring(0, indcomma);
                    //System.out.println("parsetoArrayCSV adding first " + s);
                    store.add(s);
                    data = data.substring(indcomma);
                    first = false;
                } else {
                    if (indquotestart == 0) {
                        if (indquoteend != -1) {
                            String s = data.substring(2, indquoteend);
                            store.add(s);
                            String s1 = data.substring(indquoteend + 1);
                            //System.out.println("parsetoArrayCSVadding indquotestart indquoteend " + s1);
                            data = s1;
                        } else {
                            String s = data.substring(1);
                            //System.out.println("parsetoArrayCSVadding indquotestart " + s);
                            store.add(s);
                            data = "";
                            break;
                        }
                    } else if (indcomma != -1) {
                        if (indnextcomma != -1) {
                            String s1 = data.substring(1, indnextcomma);
                            data = data.substring(indnextcomma);
                            //System.out.println("parsetoArrayCSV adding indcomma indnextcomma " + s1);
                            store.add(s1);
                        } else {
                            String s = data.substring(1);
                            //System.out.println("parsetoArrayCSV adding indcomma " + s);
                            store.add(s);
                            data = "";
                            break;
                        }
                    }
                }
                if (data.length() > 0) {
                    indcomma = data.indexOf(",");
                    indnextcomma = data.indexOf(",", indcomma + 1);
                    indquotestart = data.indexOf(",\"");
                    indquoteend = data.indexOf("\",");
                } else {
                    break;
                }
            }
        }

        return MoreArray.ArrayListtoString(store);
    }


    /**
     * @param locs
     * @return
     */

    public final static ArrayList translateLocateOccurtoList(int[] locs) {
        ArrayList a = new ArrayList();
        for (int i = 0; i < locs.length; i++) {
            if (locs[i] == 1) {
                a.add(new Integer(i));
            }
        }
        return a;
    }


    /**
     * Returns the location of occurences in array form.
     *
     * @param data
     * @param query
     * @return
     */
    public final static int[] locateOccur(String[] data, String query) {
        int datalen = data.length;
        int[] ret = MoreArray.initArray(datalen, -1);
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (data[i].equals(query)) {
                //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                ret[i] = 10;
            }
        }
        return ret;
    }

    /**
     * Returns the location of indexOf in array form.
     *
     * @param data
     * @param query
     * @return
     */
    public final static int[] locateIndexOfIn(String[] query, String data) {
        int datalen = query.length;
        int[] ret = MoreArray.initArray(datalen, -1);
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (data.indexOf(query[i]) != -1) {
                //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                ret[i] = 10;
            }
        }
        return ret;
    }

    /**
     * Returns the location of indexOf in array form.
     *
     * @param data
     * @param query
     * @return
     */
    public final static int[] locateIndexOf(String[] data, String query) {
        int datalen = data.length;
        int[] ret = MoreArray.initArray(datalen, -1);
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (data[i] != null)
                if (data[i].indexOf(query) != -1) {
                    //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                    ret[i] = i;
                }
        }
        return ret;
    }

    /**
     * Returns the location of indexOf in array form.
     *
     * @param data
     * @param query
     * @return
     */
    public final static int[] locateEquals(String[] data, String query) {
        int datalen = data.length;
        int[] ret = MoreArray.initArray(datalen, -1);
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (data[i] != null)
                if (data[i].equals(query)) {
                    //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                    ret[i] = i;
                }
        }
        return ret;
    }

    /**
     * Returns the location of indexOf in array form.
     *
     * @param data
     * @param query
     * @return
     */
    public final static int[] locateIndexOf(String[] data, String query, int defaultval) {
        int datalen = data.length;
        int[] ret = MoreArray.initArray(datalen, defaultval);
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (data[i].indexOf(query) != -1) {
                //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                ret[i] = i;
            }
        }
        return ret;
    }

    /**
     * Returns the location of "indexOf A but not indexOf B" in array form.
     *
     * @param data
     * @param query
     * @return
     */
    public final static int[] locateIndexOfButNot(String[] data, String query, String[] not) {
        int datalen = data.length;
        int[] ret = MoreArray.initArray(datalen, -1);
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (data[i].indexOf(query) != -1 && stat.sumEntries(StringUtil.locateIndexOfIn(not, data[i])) < 10) {
                //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                ret[i] = 10;
            }
        }
        return ret;
    }

    /**
     * @param data
     * @param query
     * @return
     */
    public final static int[] occurIndex(String data, String query) {
        int datalen = data.length();
        int query_len = query.length();
        ArrayList a = new ArrayList();
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (i + query_len < datalen)
                if (data.substring(i, i + query_len).equals(query)) {
                    //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                    a.add(new Integer(i));
                }
        }
        return MoreArray.ArrayListtoInt(a);
    }

    /**
     * @param data
     * @param query
     * @return
     */
    public final static int[] occurIndex(String[] data, String query) {
        int datalen = data.length;
        ArrayList a = new ArrayList();
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (data[i].indexOf(query) != -1) {
                //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                a.add(new Integer(i));
            }
        }
        return MoreArray.ArrayListtoInt(a);
    }

    /**
     * @param data
     * @param query
     * @return
     */
    public final static int[] occurIndexIgnoreCase(String[] data, String query) {
        int datalen = data.length;
        ArrayList a = new ArrayList();
        //System.out.println("occurIndexIgnoreCase " + datalen);
        for (int i = 0; i < datalen; i++) {
            //System.out.println("occurIndexIgnoreCase " + data[i] + "\n" + query);
            if (data[i].toLowerCase().indexOf(query.toLowerCase()) != -1) {
                //System.out.println(i + "\tlocateOccur match " + query + "\t"+ data[i]);
                a.add(new Integer(i));
            }
        }
        return MoreArray.ArrayListtoInt(a);
    }

    /**
     * @param data
     * @param query
     * @return
     */
    public final static int occurIndexIgnoreCase(String data, String query) {
        return data.toLowerCase().indexOf(query.toLowerCase());
    }

    /**
     * @param data
     * @param query
     * @return
     */
    public final static int[] equalsIndex(String[] data, String query) {
        int datalen = data.length;
        ArrayList a = new ArrayList();
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");
            if (data[i].equals(query)) {
                //System.out.println(i + "\tlocateOccur match\n" + query + "\n" + data[i] + "\n");
                a.add(new Integer(i));
            }
        }
        return MoreArray.ArrayListtoInt(a);
    }

    /**
     * @param data
     * @param query
     * @param not
     * @return
     */
    public final static int[] occurIndexButNot(String[] data, String query, String[] not) {
        return StringUtil.occurIndexButNot(data, query, not, false);
    }

    /**
     * @param data
     * @param query
     * @param not
     * @return
     */
    public final static int[] occurIndexButNot(String[] data, String query, String[] not, boolean debug) {
        int datalen = data.length;
        if (debug)
            System.out.println("occurIndexButNot total " + datalen);
        ArrayList a = new ArrayList();
        for (int i = 0; i < datalen; i++) {
            if (debug)
                System.out.println(data[i] + "\n" + query + "\n\n");

            int[] ar = StringUtil.locateIndexOfIn(not, data[i]);
            double v = stat.sumEntries(ar);
            if (debug) {
                System.out.println("occurIndexButNot " + data[i] + "\t" + query + "\t" +
                        MoreArray.toString(not, " ") + "\t" + v + "\t" + MoreArray.toString(ar, " "));
            }
            if (data[i].indexOf(query) != -1 && v < 0) {
                if (debug)
                    System.out.println(i + "\toccurIndexButNot match\tq " + query + "\tm " + data[i]);
                a.add(new Integer(i));
                if (debug)
                    System.out.println("occurIndexButNot " + i);
            }
        }
        return MoreArray.ArrayListtoInt(a);
    }

    public final static int[] occurIndexButNot1(String[] data, String query, String[] not) {

        int datalen = data.length;
        //System.out.println("occurIndexButNot total "+datalen);
        ArrayList a = new ArrayList();
        for (int i = 0; i < datalen; i++) {
            //System.out.println(data[i]+"\n"+query+"\n\n");

            int[] ar = StringUtil.locateIndexOfIn(not, data[i]);
            double v = stat.sumEntries(ar);
            //System.out.println("occurIndexButNot " + data[i] + "\t" + query + "\t" +
            //        MoreArray.toString(not, " ") + "\t" + v+"\t"+MoreArray.toString(ar," "));
            if (data[i].indexOf(query) != -1 && v < 0) {
                //System.out.println(i + "\toccurIndexButNot match\tq " + query + "\tm " + data[i]);
                a.add(new Integer(i));
                //System.out.println("occurIndexButNot "+i);
            }
        }
        return MoreArray.ArrayListtoInt(a);
    }

    /**
     * Returns the location of occurences in array form.
     *
     * @param data
     * @param query
     * @return
     */
    public final static int[] locateOccur(String data, String query) {
        int datalen = data.length();
        int[] ret = new int[datalen];
        StringBuffer sb = new StringBuffer(query);
        StringBuffer da = new StringBuffer(data);
        int qlen = sb.length();
        if (da != null && sb != null) {
            for (int i = 0; i + qlen < datalen; i++) {
                //System.out.println("locateOccur " + i);
                int qindex = (da.substring(i)).indexOf(query) + i;
                if (qindex == -1) {
                    //System.out.println("not found " + query);
                    break;
                } else {
                    i = qindex;
                    //System.out.println("comparing piece: query:" + query + ":\tcount:" + ret[i] + ":");
                    ret[i] = 1;
                    //System.out.println("found " + i + "\t" + query);
                    i++;
                }
            }
        }
        return ret;
    }

    /**
     * Returns the location of occurences in array form.
     *
     * @param cdata
     * @param cquery
     * @param start
     * @param end
     * @return
     */
    public final static int[] locateOccurFast(char[] cdata, char[] cquery, int start, int end) {
        int datalen = cdata.length;
        if (end > datalen)
            end = datalen;
        //char[] cdata = data.toCharArray();
        int qlen = cquery.length;
        //char[] cquery = query.toCharArray();

        int[] ret = new int[datalen];

        int first_ind = MoreArray.getNextInstance(cdata, cquery, start, end);
        //System.out.println("locateOccurFast "+first_ind+"\t "+MoreArray.chartoString(cquery));
        while (first_ind != -1 && first_ind + qlen < end) {
            //System.out.println("locateOccur " + i);
            char[] cur_from_cdata = MoreArray.getSub(cdata, first_ind, first_ind + 7);
            //System.out.println("locateOccurFast "+first_ind+"\t"+MoreArray.chartoString(cur_from_cdata)+"\t"+MoreArray.chartoString(cquery));
            ret[first_ind] = 1;
            first_ind = MoreArray.getNextInstance(cdata, cquery, first_ind + 1, end);
        }

        /*int datalen = data.length();
    char[] cdata = new char[datalen];
    int qlen = query.length();
    char[] cquery = new char[qlen];

    int[] ret = new int[datalen];

    if (cdata != null && cquery != null) {

        int first_ind = data.indexOf(query);
        if(first_ind != -1)
        for (int i = first_ind; i + qlen < datalen; i++) {

            //System.out.println("locateOccur " + i);
            char[] cur_from_cdata = new char[qlen];

            cur_from_cdata = MoreArray.getSub(cdata, i, i+7);

            if (cur_from_cdata == cquery){
                System.out.println("query "+query+"\n");
                ret[i] = 1;
            }
        }*/

        return ret;
    }

    /**
     * Returns the location of occurences in list form.
     */
    public final static ArrayList locateOccurtoList(String data, String query) {
        ArrayList ret = new ArrayList();
        StringBuffer sb = new StringBuffer(query);
        StringBuffer da = new StringBuffer(data);
        int qlen = sb.length();
        int totlen = da.length();
        if (da != null && sb != null) {
            for (int i = 0; i + qlen < totlen; i++) {
                String c = da.substring(i, i + qlen);
                //System.out.println("comparing piece:"+c+"\tquery:"+query+":\tcount:"+ret+":");
                if (da.substring(i).indexOf(query) == -1)
                    break;
                else if (query.equals(c)) {
                    ret.add(new Integer(i));
                }
            }
        }
        return ret;
    }

    /**
     * Counts the occurence of a String in a String.
     */
    public final static int countOccur(String data, String query) {
        StringBuffer sb = new StringBuffer(query);
        StringBuffer da = new StringBuffer(data);
        int qlen = sb.length();
        int totlen = da.length();
        int ret = 0;
        if (da != null && sb != null) {
            int start = data.indexOf(query);
            if (start > -1) {
                for (int i = start; i + qlen <= totlen; i++) {
                    String c = da.substring(i, i + qlen);
                    //System.out.println("comparing piece:"+c+"\tquery:"+query+":\tcount:"+ret+":");
                    if (query.equals(c))
                        ret++;
                }
            }
        }
        return ret;
    }

    /**
     * @param data
     * @param query
     * @return
     */
    public final static int countOccur(String[] data, String query) {
        int ret = 0;
        for (int i = 0; i < data.length; i++) {
            ret += countOccur(data[i], query);
        }
        return ret;
    }


    /**
     * Counts the occurence of a String in a String.
     */
    public final static int countOccur(String data, char query) {
        char[] da = data.toCharArray();
        int totlen = da.length;
        int ret = 0;
        if (da != null) {
            int start = data.indexOf("" + query);
            if (start >= 0) {
                for (int i = start; i < totlen; i++) {
                    if (query == da[i])
                        ret++;
                }
            }
        }
        return ret;
    }


    /**
     * @param data
     * @param query
     * @return
     */
    public final static int countIndexOf(String[] data, String query) {
        int ret = 0;
        for (int i = 0; i < data.length; i++) {
            ret += countIndexOf(data[i], query);
        }
        return ret;
    }


    /**
     * Counts the occurence of a String in a String.
     */
    public final static int countIndexOf(String data, String query) {
        StringBuffer sb = new StringBuffer(query);
        StringBuffer da = new StringBuffer(data);
        int ret = 0;
        if (da != null && sb != null) {
            int start = data.indexOf(query);
            if (start > -1) {
                ret = 1;
            }
        }
        return ret;
    }

    /**
     * Counts the occurence of a String in a String.
     *
     * @param data
     * @param query
     * @return
     */
    public final static ArrayList countOccurtoList(String data, String query) {
        ArrayList ret = new ArrayList();
        if (data != null && query != null) {
            int start = data.indexOf(query);
            if (start > -1) {
                for (int i = start; i + query.length() <= data.length(); i++) {
                    String c = data.substring(i, i + query.length());
                    if (query.equals(c)) {
                        ret.add(new Integer(i));
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Trims a specified character from beg and end.
     *
     * @param s
     * @param t
     * @return
     */
    public final static String trim(String s, String t) {
        while (s.indexOf(t) == 0) s = s.substring(1);
        while (s.length() > 0 && s.indexOf(t) == s.length() - 1) s = s.substring(0, s.length() - 1);
        return s;
    }

    /**
     * @param s
     * @param l
     * @return
     */
    public final static String nextLine(String s, int l) {
        int newl = s.indexOf("\n", l + 1);
        if (newl != -1)
            return s.substring(l, newl);
        else
            return null;
    }

    /**
     * Replaces String a with String b starting at 'at'
     *
     * @param a
     * @param b
     * @param at
     * @return
     */
    public final static String replaceAtString(String a, String b, int at) {
        int len = b.length();

        String a1 = a.substring(0, at);
        int test = len + a1.length();
        String a2 = "";
        if (test < a.length())
            a2 = a.substring(test);
//System.out.println("chopping "+a+" at "+at);
//System.out.println("inserting "+len+" between "+a1.length()+"  "+a2.length());
        a = a1 + b + a2;
        return a;
    }

    /**
     * Analyzed an aligned sequence and reports index of first non-gap character.
     *
     * @param b
     * @return
     */
    public final static int indexOfBinA(String b) {
        int ret = -1;
        for (int i = 0; i < b.length(); i++) {
            if (b.charAt(i) != '~' && b.charAt(i) != '-')
                ret = i;
        }
        return ret;
    }

    /**
     * Returns the Nth occurence of String query in db
     *
     * @return
     */
    public final static int NthIndex(String db, String query, int start) {
        int ret = -1;
        //int count = 0;
        int ind = db.indexOf(query, start);
        while (ind != -1) {
            //count++;
            ind = db.indexOf(query, ind + 1);
        }
        return ret;
    }

    /**
     * @return
     */
    public final static ArrayList stringListtoArray(String[] s) {
        ArrayList a = new ArrayList();
        for (int i = 0; i < s.length; i++) {
            a.add((String) s[i]);
        }

        return a;
    }

    /**
     * Subtract two Strings based on gap and equality rule.
     *
     * @param child
     * @param parent
     * @return
     */
    public final static String subtractString(StringBuffer child, StringBuffer parent) {
        String c = new String(child);
        char[] one = c.toCharArray();
        String p = new String(child);
        char[] two = p.toCharArray();
        return new String(CharTool.subtract(one, two));
    }

    /**
     * @param s
     * @param p
     * @return
     */
    public final static String padLeft(String s, int p) {
        while (p > 0) {
            s = " " + s;
            p--;
        }
        return s;
    }

    /**
     * @param s
     * @param p
     * @return
     */
    public final static String padRight(String s, int p) {
        while (p > 0) {
            s += " ";
            p--;
        }
        return s;
    }

    /**
     *
     */
    public final static String replaceforMySQLString(String s) {
        s = util.StringUtil.replace(s, "'", " ");
        s = util.StringUtil.replace(s, "~", " ");
        s = util.StringUtil.replace(s, "[", " ");
        s = util.StringUtil.replace(s, "]", " ");
        s = util.StringUtil.replace(s, "\\", " ");
        s = util.StringUtil.replace(s, ":", " ");
        return s;
    }

    /**
     * Returns the shorter of two Strings.
     *
     * @param a
     * @param b
     * @return
     */
    public final static String min(String a, String b) {
        if (a.length() > b.length())
            return b;
        else
            return a;
    }

    /**
     * Rotates a String array by 'degrees' degrees clockwise.
     *
     * @param in 90,180,270 allowed
     * @return
     */
    public final static String[][] rotateArray(String[][] in, int degrees) {
        String[][] ret = null;
        int xlen = in[0].length;
        int ylen = in.length;

        if ((Integer.valueOf("" + (degrees / 90.0))) != null) {
            if (degrees == 90) {
                ret = new String[xlen][ylen];

                for (int i = ylen; i > -1; i--) {
                    for (int j = 0; j < xlen; j++) {

                        ret[j][i] = in[i][j];
                    }
                }
            }

            if (degrees == 180) {
                ret = new String[xlen][ylen];

                for (int i = ylen; i > -1; i--) {
                    for (int j = 0; j < xlen; j++) {

                        ret[i][j] = in[i][j];
                    }
                }
            }

            if (degrees == 270) {
                ret = new String[xlen][ylen];

                for (int i = 0; i < ylen; i++) {
                    for (int j = 0; j < xlen; j++) {

                        ret[j][i] = in[i][j];
                    }
                }
            }
        }

        return ret;
    }


    /**
     * @param query
     * @param flank
     */
    public final static String trimFlanking(String query, String flank) {

        if (query.indexOf(flank) == 0)
            query = query.substring(1);
        if (query.lastIndexOf(flank) == query.length() - 1)
            query = query.substring(0, query.length() - 1);
        return query;
    }


    /**
     * @param t
     * @param list
     * @return
     */
    public final static int nexctCharNot(String t, String list) {

        for (int i = 0; i < t.length(); i++) {

            if (list.indexOf(t.charAt(i)) == -1) {
                //System.out.println(i + "\t" + t.charAt(i));
                return i;
            }
        }

        return -1;
    }

    /**
     * @param t
     * @param list
     * @return
     */
    public final static int nexctChar(String t, String list) {

        for (int i = 0; i < t.length(); i++) {

            if (list.indexOf(t.charAt(i)) != -1) {
                //System.out.println(i + "\t" + t.charAt(i));
                return i;
            }
        }

        return -1;
    }


    /**
     * @param s
     * @param width
     * @param pad
     * @return
     */
    public final static String padLeft(String s, int width, String pad) {
        while (s.length() < width) {
            s = pad + s;
        }
        return s;
    }

    /**
     * Returns a String encased in single quotes, if not null.
     *
     * @param s
     * @return
     */
    public final static String singleQuotes(String s) {

        if (s != null)
            if (s.equals(""))
                s = null;

        if (s != null)
            s = "'" + s + "'";

        return s;
    }

    /**
     * Makes a String array containing unique Strings from the input array.
     *
     * @param in
     * @return
     */
    public final static String[] makeUnique(String[] in) {
        ArrayList copy = new ArrayList();
        for (int i = 0; i < in.length; i++) {
            copy.add(in[i]);
        }
        for (int i = 0; i < copy.size(); i++) {
            String curi = (String) copy.get(i);
            for (int j = i + 1; j < copy.size(); j++) {
                String curj = (String) copy.get(j);
                if (curi.equals(curj)) {
                    //System.out.println("makeUnique remove "+(String)copy.get(j));
                    copy.remove(j);
                    j--;
                }
            }
        }
        return MoreArray.ArrayListtoString(copy);
    }

    /**
     * Makes a String array containing unique Strings from the input array.
     *
     * @param in
     * @return
     */
    public final static String[] makeUnique(ArrayList in) {
        ArrayList copy = new ArrayList();
        for (int i = 0; i < in.size(); i++) {

            copy.add(in.get(i));
        }
        for (int i = 0; i < copy.size(); i++) {
            String curi = (String) copy.get(i);
            if (curi != null) {
                for (int j = i + 1; j < copy.size(); j++) {
                    String curj = (String) copy.get(j);
                    if (curj != null) {
                        if (curi.equals(curj)) {
                            copy.remove(j);
                            j--;
                        }
                    } else
                        copy.remove(j);
                }
            } else {
                copy.remove(i);
                i--;
            }

        }
        return MoreArray.ArrayListtoString(copy);
    }

    /**
     * Makes a String array containing unique Strings from the input array.
     *
     * @param in
     * @return
     */
    public final static ArrayList makeUniquetoArray(ArrayList in) {

        ArrayList copy = new ArrayList();
        for (int i = 0; i < in.size(); i++) {

            copy.add(in.get(i));
        }

        for (int i = 0; i < copy.size(); i++) {

            String curi = (String) copy.get(i);
            for (int j = i + 1; j < copy.size(); j++) {

                String curj = (String) copy.get(j);
                if (curi.equals(curj)) {
                    copy.remove(j);
                    j--;
                }
            }
        }

        return copy;
    }


    /**
     * Indexes one String array to another.
     *
     * @param from
     * @param to
     */
    public final static int[] crossIndex(String[] from, String[] to) {
        int[] ret = MoreArray.initArray(from.length, -1);//new int[from.length];
        for (int i = 0; i < from.length; i++) {
            for (int j = 0; j < to.length; j++) {
                //System.out.println("crossIndex "+from[i]+"\t"+to[j]);
                if (from[i].equals(to[j])) {
                    ret[i] = j;
                    //System.out.println("crossIndex " + i+"\t"+ from[i] + "\tsetId " + j+"\t"+to[j]);
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Indexes one String array to another.
     *
     * @param from
     * @param to
     */
    public final static int[] crossIndex(String[] from, String[] to, String[] second_index) {

        int[] ret = new int[from.length];
        for (int i = 0; i < from.length; i++) {
            for (int j = 0; j < to.length; j++) {
                //System.out.println("crossIndex "+from[i]+"\t"+to[j]);
                //System.out.println("crossIndex "+from[i]+"\t"+to[j]+"\t"+j+"\t"+second_index.length);
                if (from[i].equals(to[j])) {
                    //System.out.println("crossIndex equals");
                    ret[i] = Integer.parseInt(second_index[j]);
                    //System.out.println("crossIndex " + i+"\t"+ from[i] + "\tsetId " + ret[i]+"\t"+to[j]);
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * @param delim
     * @param a
     * @return
     */
    public final static String join(String delim, ArrayList a) {
        String ret = null;
        if (a != null && a.size() > 0) {
            for (int i = 0; i < a.size(); i++) {
                Object o = a.get(i);
                if (o != null) {
                    if (ret == null)
                        ret = "";
                    ret += ((String) o) + delim;
                }
            }
            if (ret.indexOf(",") == ret.length() - 1)
                ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    /**
     * @param delim
     * @param a
     * @return
     */
    public final static String join(String delim, String[] a) {

        String ret = null;
        if (a != null && a.length > 0) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != null) {
                    if (ret == null)
                        ret = "";
                    ret += a[i] + delim;
                }
            }
            if (ret.indexOf(",") == ret.length() - 1)
                ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public final static String[] join(String[] a, String[] b) {
        String[] ret = new String[a.length + b.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            ret[i + a.length] = b[i];
        }
        return ret;
    }

    /**
     * @param str
     * @return
     */
    public final static String reverse(String str) {
        return (new StringBuffer(str)).reverse().toString();
    }

    /**
     * @param ar
     * @param pre
     * @return
     */
    public final static String[] prepend(String[] ar, String pre) {
        for (int i = 0; i < ar.length; i++) {
            ar[i] = pre + ar[i];
        }
        return ar;
    }

    /**
     * @param ar
     * @param append
     * @return
     */
    public final static String[] append(String[] ar, String append) {
        for (int i = 0; i < ar.length; i++) {
            ar[i] = ar[i] + append;
        }
        return ar;
    }

    /**
     * @param ar
     * @param prepend
     * @param append
     * @return
     */
    public final static String[] prependAppend(String[] ar, String prepend, String append) {
        ar = prepend(ar, prepend);
        return append(ar, append);
    }

    /**
     * @param s
     * @param num
     * @return
     */
    public static String[] replicate(String s, int num) {
        String[] ret = new String[num];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = s;
        }
        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public static String[] splice(String[] a, String[] b) {
        String[] ret = new String[a.length + b.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        for (int i = a.length; i < ret.length; i++) {
            ret[i] = b[i - a.length];
        }
        return ret;
    }

    /**
     * @param s
     * @param c
     * @return
     */
    public static String[] truncate(String[] s, int c) {
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i].substring(0, Math.min(s[i].length(), c));
        }
        return s;
    }

    /**
     * @param s
     * @return
     */
    public static String[] toUpper(String[] s) {
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i].toUpperCase();
        }
        return s;
    }


    /**
     * @param s
     * @return
     */
    public static boolean isTrueorYes(String s) {
        if (s.equalsIgnoreCase("T") || s.equalsIgnoreCase("TRUE") ||
                s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("YES"))
            return true;
        return false;
    }

    /**
     * @param s
     * @return
     */
    public static boolean isTrueorYes(int s) {
        if (s == 1)
            return true;
        return false;
    }


    /**
     * @param o
     * @return
     */
    public static String[] removeEmpty(String[] o) {
        ArrayList d = MoreArray.convtoArrayList(o);
        for (int i = 0; i < d.size(); i++) {
            String get = (String) d.get(i);
            if (get.length() == 0 || get == null || d.get(i) == null) {
                System.out.println("removeEmpty " + (i + 1) + "\t:" + get + ":");
                d.remove(i);
                i--;
            }
        }
        if (d.size() > 0)
            return MoreArray.ArrayListtoString(d);
        else
            return null;
    }
}
