package util;

/**
 * User: marcin
 * Date: Jun 16, 2006
 * Time: 7:30:01 PM
 */
public class CompactCounter {

    public int count = 0;
    int counter = 0;

    public CompactCounter() {
        count = 0;
        counter = 0;
    }

    public String print(int cur, int granule, String lastdef) {
        
        if (cur == granule) {
            counter++;
            for (int k = 0; k < lastdef.length(); k++)
                System.out.print("\b");
            lastdef = "" + count * counter;
            System.out.println("CompactCounter "+lastdef);
            count = 0;
        }
        return lastdef;
    }
}
