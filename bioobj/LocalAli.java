package bioobj;


public class LocalAli {

    public LocalAli() {
        //System.out.println("new local alignment!!!");
    }

    private static int conv[] =
            {0, 11, 1, 2, 3, 4, 5, 6, 7, -1, 8, 9, 10, 11, -1, 12, 13, 14, 15, 16, -1, 17, 18, -1, 19, 13};

    private int d(char c1, char c2, int[][] mat) {
        int a = (int) 'A';
        int i1 = conv[(int) c1 - a];
        int i2 = conv[(int) c2 - a];
        if ((i1 < 0) || (i2 < 0))
            return (0);
        return (mat[i1][i2]);
    }


    public char[][] LocalAlignIt(char[] s1, char[] s2, int open, int extend, int[][] mat) {
        //System.out.println("started local alignment!!!");

        int l1 = s1.length;
        int l2 = s2.length;
        /*
    System.out.println("local array length   "+l1+"    "+l2);
    for(int u=0; u< l1; u++)
    System.out.println(u+"   "+s1[u]);
    for(int ua=0; ua< l2; ua++)
    System.out.println(ua+"   "+s2[ua]);
        */

        int[][] S = new int[l1 + 1][l2 + 1];
        int[][] E = new int[l1 + 1][l2 + 1];
        int[][] F = new int[l1 + 1][l2 + 1];
        int res;

        int i, ipos, jpos;

        ipos = jpos = 0;


        res = CreateLocalAlign(s1, s2, open, extend, S, E, F, mat, ipos, jpos);

        char[][] results = MakeLocalAlign(s1, s2, open, extend, S, E, F, mat, ipos, jpos);
        S = null;
        E = null;
        F = null;

        return (results);

    }

    private int CreateLocalAlign(char[] s1, char[] s2, int open, int extend, int[][] S, int[][] E, int[][] F, int[][] mat, int ipos, int jpos) {
        int i, j;
        int l1 = s1.length;
        int l2 = s2.length;
        int score = 0;
        int set = 0;
        int cur_max = 0;

        for (i = 0; i <= l1; i++)
            E[i][0] = S[i][0] = 0;

        for (i = 0; i <= l2; i++)
            F[0][i] = S[0][i] = 0;

        for (i = 1; i <= l1; i++) {
            for (j = 1; j <= l2; j++) {
                E[i][j] = Math.max(S[i][j - 1] - open, E[i][j - 1] - extend);
                F[i][j] = Math.max(S[i - 1][j] - open, F[i - 1][j] - extend);

                score = d(s1[i - 1], s2[j - 1], mat);
                S[i][j] = Math.max(0, Math.max(S[i - 1][j - 1] + score, Math.max(E[i][j], F[i][j])));
                if (set == 0 || (cur_max < S[i][j])) {
                    cur_max = S[i][j];
                    ipos = i;
                    jpos = j;
                    set = 1;
                }


            }
        }

        return (cur_max);
    }

    private char[][] MakeLocalAlign(char[] s1, char[] s2, int open, int extend, int[][] S, int[][] E, int[][] F, int[][] mat, int ipos, int jpos) {
        int len = 0;

        char[][] out = new char[2][s1.length + s2.length];
        while ((ipos > 0) && (jpos > 0)) {
            if (S[ipos][jpos] == 0)
                break;

            if (S[ipos][jpos] == F[ipos][jpos]) {
                do {
                    out[0][len] = s1[--ipos];
                    out[1][len++] = '-';

                } while ((ipos > 0) && (F[ipos + 1][jpos] == (F[ipos][jpos] - extend)));
            } else if (S[ipos][jpos] == E[ipos][jpos]) {
                do {
                    out[0][len] = '-';
                    out[1][len++] = s2[--jpos];


                } while ((jpos > 0) && (E[ipos][jpos + 1] == (E[ipos][jpos] - extend)));
            } else {
                out[0][len] = s1[--ipos];
                out[1][len++] = s2[--jpos];


            }
        }

        out[0][len] = out[1][len] = '\0';

        return out;
    }
}
