package util;

import java.util.ArrayList;

public class PreLoadList {


    public final static ArrayList load(int size, Object o, ArrayList a) {

        int c = 0;

        while (c < size) {

            a.add((Object) o);

            c++;
        }
        return a;
    }

}