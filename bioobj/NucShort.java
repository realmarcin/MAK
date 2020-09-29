package bioobj;

/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 12:59:38 PM
 * To change this template use File | Settings | File Templates.
 */
public final class NucShort {

/*
G	G	Guanine
A	A	Adenine
T	T	Thymine
C	C	Cytosine
R	AG  A or G	puRine
Y	CT  C or T	pYrimidine
M	AC  A or C	aMino
W	AT  A or T	Weak interaction (2 H bonds)
S	CG  C or G	Strong interaction (3 H bonds)
K	GT  G or T	Keto
V	ACG A or C or G	not-T (not-U), V follows U
D	AGT A or G or T	not-C, D follows C
H	ACT A or C or T	not-G, H follows G in the alphabet
B	CGT C or G or T	not-A, B follows A
N or X	A or C or G or T	any
*/

    final static String code = "RYMWSKVDHBXN";
    final static String complement = "YRKSWMBHVDNN";
    final static String[] dnamap = {"AG", "CT", "AC", "AT", "CG", "GT", "AGC", "AGT", "ACT", "CGT", "ACGT", "ACGT"};
    final static String[] rnamap = {"AG", "CU", "AC", "AU", "CG", "GU", "AGC", "AGU", "ACU", "CGU", "ACGU", "ACGU"};
}
