package DataMining;

import mathy.FactorialPoorMans;

/**
 * User: marcin
 * Date: Mar 2, 2010
 * Time: 2:13:41 PM
 */
public class CountBlocks {

    int genes, exps, mingene, maxgene, minexp, maxexp;
    double fraction;

    /**
     * @param args
     */
    public CountBlocks(String[] args) {

        genes = Integer.parseInt(args[0]);
        exps = Integer.parseInt(args[1]);
        mingene = Integer.parseInt(args[2]);
        maxgene = Integer.parseInt(args[3]);
        minexp = Integer.parseInt(args[4]);
        maxexp = Integer.parseInt(args[5]);
        fraction = Double.parseDouble(args[6]);

        double sum = 0;
        FactorialPoorMans fpm = new FactorialPoorMans();
        long maxgenefact = fpm.factorial(maxgene);
        long maxexpfact = fpm.factorial(maxexp);
        for (int i = mingene; i <= maxgene; i++) {
            for (int j = minexp; j <= maxexp; j++) {
                /*double genes = choose((long) maxgene, (long) i);
                double exps = choose((long) maxexp, (long) j);*/
                /*double genes = factorial((long) maxgene) / (factorial((long) i) * factorial((long) (maxgene - i)));
                double exps = factorial((long) maxexp) / (factorial((long) j) * factorial((long) (maxexp - j)));*/

                double genes = maxgenefact / (fpm.factorial(i) * fpm.factorial(maxgene - i));
                double exps = maxexpfact / (fpm.factorial(j) * fpm.factorial(maxexp - j));
                double product = genes * exps;
                sum += product;
                /*System.out.println("genes " + (int) maxgene + "choose" + i + " =" + genes +
                        "\texps " + (int) maxexp + "choose" + j + " =" + exps + "\ttotal " + product);*/
                System.out.print(".");
            }
        }
        System.out.println("");
        System.out.println("Total genes " + genes);
        System.out.println("Total exps " + exps);
        System.out.println("mingene " + mingene);
        System.out.println("maxgene " + maxgene);
        System.out.println("minexp " + minexp);
        System.out.println("maxexp " + maxexp);
        System.out.println("Total possible blocks " + sum +
                " and fraction " + fraction + " is " + (sum * fraction));
    }

    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 7) {
            CountBlocks rm = new CountBlocks(args);
        } else {
            System.out.println("syntax: java DataMining.CountBlocks\n" +
                    "<genes> <exps> <min gene> <max gene> <min exp> <max exp> <fraction>"
            );
        }
    }
}
