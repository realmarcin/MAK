package DataMining;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import util.MapArgOptions;
import util.StringUtil;

import java.util.HashMap;

/**
 * DEPRECATED
 * <p/>
 * - skip random numbers until start
 * -
 * <p/>
 * User: marcin
 * Date: May 19, 2008
 * Time: 3:58:26 PM
 */
public class NullDistrib {

    String[] valid_args = {"-seed", "-start", "-boot", "-data", "-imputed", "-workdir", "-outfile", "-methods"};
    HashMap options;

    int seed = -1;
    int start = -1;
    int samples = 10;
    int nboot = 1;
    java.lang.String datapath, imputedpath, methodspath, workdir, outfile;

    private Rengine re;
    REXP Rexpr;

    int Imin = 5;
    int Jmin = 5;
    int Imax = 95;
    int Jmax = 95;

    double missing_data_threshold = 0.2;

    boolean use_features = false;
    boolean debug = false;

    /**
     * @param args
     */
    public NullDistrib(String[] args) {

        init(args);

        initVar();

        initNulls();

        runLoop();

        System.exit(0);
    }

    /**
     *
     */
    private void output() {
        System.out.println("output");
        System.out.println(Rexpr = re.eval("i"));

        System.out.println(Rexpr = re.eval("Jtot"));
        System.out.println(Rexpr = re.eval("ExpCMSEc.null[1,,]"));
        String s0 = "write((ExpCMSEc.null[1,,]),file=paste(\"ExpCMSEc.null\",i,sep=\"_\"),ncolumns=Jtot)";
        if (debug)
            System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        System.out.println(Rexpr = re.eval("write((ExpCMSEr.null[1,,]),file=paste(\"ExpCMSEr.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSE.null[1,,]),file=paste(\"ExpCMSE.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEc1.null[1,,]),file=paste(\"ExpCMSEc1.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEr1.null[1,,]),file=paste(\"ExpCMSEr1.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSE1.null[1,,]),file=paste(\"ExpCMSE1.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEc2.null[1,,]),file=paste(\"ExpCMSEc2.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEr2.null[1,,]),file=paste(\"ExpCMSEr2.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSE2.null[1,,]),file=paste(\"ExpCMSE2.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEc3.null[1,,]),file=paste(\"ExpCMSEc3.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEr3.null[1,,]),file=paste(\"ExpCMSEr3.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSE3.null[1,,]),file=paste(\"ExpCMSE3.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEc4.null[1,,]),file=paste(\"ExpCMSEc4.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEr4.null[1,,]),file=paste(\"ExpCMSEr4.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSE4.null[1,,]),file=paste(\"ExpCMSE4.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEc5.null[1,,]),file=paste(\"ExpCMSEc5.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSEr5.null[1,,]),file=paste(\"ExpCMSEr5.null\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((ExpCMSE5.null[1,,]),file=paste(\"ExpCMSE5.null\",i,sep=\"_\"),ncolumns=Jtot)"));

        System.out.println(Rexpr = re.eval("write((Kend.nullc[1,,]),file=paste(\"Kend.nullc\",i,sep=\"_\"),ncolumns=Jtot)"));
        System.out.println(Rexpr = re.eval("write((Kend.nullt[1,,]),file=paste(\"Kend.nullt\",i,sep=\"_\"),ncolumns=Jtot)"));

        //####Write out the matrices for gene dependent nulls at the end into one file
        System.out.println(Rexpr = re.eval("write((Inter.nullm[,1,]),file=paste(\"Inter.nullm\",i,sep=\"_\"),append=TRUE,ncolumns=Itot1)"));
        System.out.println(Rexpr = re.eval("write((Inter.nulls[,1,]),file=paste(\"Inter.nulls\",i,sep=\"_\"),append=TRUE,ncolumns=Itot1)"));
        if (use_features)
            System.out.println(Rexpr = re.eval("write((Feat.null[,1,]),file=paste(\"Feat.null\",i,sep=\"_\"),append=TRUE,ncolumns=Itot1)"));
    }

    /**
     *
     */
    private void runLoop() {
        System.out.println("runLoop " + samples);
        for (int i = 1; i < samples + 1; i++) {
            String s0 = "i=" + i;
            if (debug)
                System.out.println("R: " + s0);
            //System.out.println(Rexpr = re.eval(s0));
            Rexpr = re.eval(s0);
            //System.out.println("loop " + i);
            for (int j = 1; j < samples + 1; j++) {
                System.out.println("loop " + i + "\t" + j);
                s0 = "j=" + j;
                if (debug)
                    System.out.println("R: " + s0);
                //System.out.println(Rexpr = re.eval(s0));
                Rexpr = re.eval(s0);
                int k = 1;
                while (k < nboot + 1) {
                    //System.out.println("loop " + i + "\t" + j + "\t" + k);
                    s0 = "k=" + k;
                    if (debug)
                        System.out.println("R: " + s0);
                    //System.out.println(Rexpr = re.eval(s0));                      q1`
                    Rexpr = re.eval(s0);
                    s0 = "isamp=(i-1)*10+" + Imin;
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    s0 = "jsamp=(j-1)*10+" + Jmin;
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    s0 = "Is1=sample(I,isamp)";
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    s0 = "Js1=sample(J,jsamp)";
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    s0 = "bl1=expr_data[Is1,Js1]";
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    s0 = "Nbl1=expr_data_copy[Is1,Js1]";
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    s0 = "Wbl=(sum(is.na(Nbl1))/(isamp*jsamp))<0.2";
                    //s0 = "whole_NA=(sum(is.na(Nbl1))/(isamp*jsamp))<" + missing_data_threshold;
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    double whole_NA = Rexpr.asDouble();
                    s0 = "rNA=apply(Nbl1,1,function(x) sum(is.na(x))/jsamp)";
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    s0 = "cNA=apply(Nbl1,2,function(x) sum(is.na(x))/isamp)";
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    s0 = "Rbl=sum(rNA<" + missing_data_threshold + ")==isamp";
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    double row_NA = Rexpr.asDouble();
                    s0 = "Cbl=sum(cNA<" + missing_data_threshold + ")==jsamp";
                    if (debug)
                        System.out.println("R: " + s0);
                    Rexpr = re.eval(s0);
                    double column_NA = Rexpr.asDouble();
                    if (debug)
                        System.out.println("whole_NA&&row_NA&&column_NA " + whole_NA + "\t" + row_NA + "\t" + column_NA);

                    if (!Double.isNaN(whole_NA) && !Double.isNaN(row_NA) && !Double.isNaN(column_NA)) {
                        makeOneNull();
                        k++;
//        if (k % 1000 == 0) {
//            System.out.println("makeOneNull bootstrap " + k);
//        }
                    }
                }
            }
            output();
        }
    }

    /**
     * @return
     */
    private void makeOneNull() {
        /*System.out.println(Rexpr = re.eval("i"));
        System.out.println(Rexpr = re.eval("j"));
        System.out.println(Rexpr = re.eval("k"));*/

        String s1 = "outs=ExpCritMnSMn.block(expr_data,Is1,Js1)";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "outs1=ExpCritMdSMn.block(expr_data,Is1,Js1)";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "outs2=ExpCritMnSMd.block(expr_data,Is1,Js1)";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "outs3=ExpCritMdSMd.block(expr_data,Is1,Js1)";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "outs4=ExpCritMnAMd.block(expr_data,Is1,Js1)";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "outs5=ExpCritMdAMd.block(expr_data,Is1,Js1)";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = "outsK=Kend.PRECRITERIA(expr_data,Is1,Js1)";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "outsI=InterCrit.block(interact_data,Is1)";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = " ExpCMSE.null[1,j,k]=outs[1]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEr.null[1,j,k]=outs[2]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEc.null[1,j,k]=outs[3]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = "ExpCMSE1.null[1,j,k]=outs1[1]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEr1.null[1,j,k]=outs1[2]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEc1.null[1,j,k]=outs1[3]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = "ExpCMSE2.null[1,j,k]=outs2[1]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEr2.null[1,j,k]=outs2[2]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEc2.null[1,j,k]=outs2[3]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = "ExpCMSE3.null[1,j,k]=outs3[1]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEr3.null[1,j,k]=outs3[2]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEc3.null[1,j,k]=outs3[3]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = "ExpCMSE4.null[1,j,k]=outs4[1]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEr4.null[1,j,k]=outs4[2]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEc4.null[1,j,k]=outs4[3]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = "ExpCMSE5.null[1,j,k]=outs5[1]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEr5.null[1,j,k]=outs5[2]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "ExpCMSEc5.null[1,j,k]=outs5[3]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = " Kend.nullc[1,j,k]=outsK[1]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "Kend.nullt[1,j,k]=outsK[2]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        s1 = "Inter.nullm[i,1,k]=outsI[1]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);
        s1 = "Inter.nulls[i,1,k]=outsI[2]";
        if (debug)
            System.out.println("R: " + s1);
        Rexpr = re.eval(s1);

        if (use_features) {
            s1 = "Feat.null[i,1,k]=FeatureWblock(Is1,Js1,GFeat,Ifact,I,J)";
            if (debug)
                System.out.println("R: " + s1);
            Rexpr = re.eval(s1);
        }
    }

    /**
     *
     */
    private void initNulls() {

        String s0 = "dim=c(Itot,Jtot,nboot)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        s0 = "ExpCMSE.null=array(NA,dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEr.null=array(NA,dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEc.null=array(NA,dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        s0 = "ExpCMSE1.null=array(NA,dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEr1.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEc1.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        s0 = "ExpCMSE2.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEr2.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEc2.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        s0 = "ExpCMSE3.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEr3.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEc3.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        s0 = "ExpCMSE4.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEr4.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEc4.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        s0 = "ExpCMSE5.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEr5.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "ExpCMSEc5.null=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        s0 = "Kend.nullc=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "Kend.nullt=array(NA, dim)";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        s0 = "Inter.nullm=array(NA,dim=c(Itot1,1,nboot))";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));
        s0 = "Inter.nulls=array(NA,dim=c(Itot1,1,nboot))";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        if (use_features) {
            s0 = "Feat.null=array(NA,dim=c(Itot,1,nboot))";
            System.out.println("initNulls " + s0);
            System.out.println(Rexpr = re.eval(s0));
        }
    }


    /**
     *
     */
    private void combineNulls() {

/*###The function to create an array from the multiple files
createArray=function(nfiles,ncols,nboot,fileprefix){
    outarray=array(NA,dim=c(nfiles,ncols,nboot))
    for(i in 1:num){
        outarray[i,,]=read.table(file=paste(fileprefix,i,sep="_"),sep = " ")
    }
    outarray
}


###example function call where I=gene set size, J=expr set size, K=number of bootstrap samples, creating ExpCMSEc.null array and ###then creating the mean and sd matrices
#If null is gene by experiment then,
array1=createArray(I,J,K,"ExpCMSEc.null")
Idim=length(array1[,1,1])
Jdim=length(array1[1,,1])
sdMat=meanMat=matrix(NA, ncol=Jdim, nrow=Idim)

for(b in 1:Idim){
    for(l in 1:Jdim){
        meanMat[b,l]=mean(ExpCMSE.null[b,l,],na.rm=TRUE)
        sdMat[b,l]=sd(ExpCMSE.null[b,l,],na.rm=TRUE)
    }
}

#If array2 gene by gene then,
array2=createArray(I,I,K,"Inter.nullm")
Idim=length(array2[,1,1])
sdMatIc=meanMatIc=sdMatI=meanMatI=matrix(NA, ncol=1, nrow=Idim)
for(b in 1:Idim){
    meanMatI[b,1]=mean(array2[b,1,],na.rm=TRUE)
    sdMatI[b,1]=sd(array2[b,1,],na.rm=TRUE)

}*/
    }

    /**
     *
     */
    private void initVar() {

        String s00 = "set.seed(" + seed + ")";
        System.out.println("R: " + s00);
        System.out.println(Rexpr = re.eval(s00));

        String s01 = "load(\"" + datapath + "\")";
        System.out.println("R: " + s01);
        System.out.println(Rexpr = re.eval(s01));

        String s6 = "expr_data_copy=expr_data";
        System.out.println("R: " + s6);
        System.out.println(Rexpr = re.eval(s6));

        String s02 = "load(\"" + imputedpath + "\")";
        System.out.println("R: " + s02);
        System.out.println(Rexpr = re.eval(s02));

        String s0 = "IMIN=" + Imin;
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        String s1 = "JMIN=" + Jmin;
        System.out.println("R: " + s1);
        System.out.println(Rexpr = re.eval(s1));

        String s2 = "IMAX=" + Imax;
        System.out.println("R: " + s2);
        System.out.println(Rexpr = re.eval(s2));

        String s3 = "JMAX=" + Jmax;
        System.out.println("R: " + s3);
        System.out.println(Rexpr = re.eval(s3));

        String s8 = "GFeat=rAllfeat[,-1]";
        System.out.println("R: " + s8);
        System.out.println(Rexpr = re.eval(s8));

        String s9 = "GFeat=unlist(sapply(GFeat,as.numeric))";
        System.out.println("R: " + s9);
        System.out.println(Rexpr = re.eval(s9));

        String s10 = "Ifact=Ifactor";
        System.out.println("R: " + s10);
        System.out.println(Rexpr = re.eval(s10));

        String s11 = "I=dim(expr_data)[1]";
        System.out.println("R: " + s11);
        System.out.println(Rexpr = re.eval(s11));

        String s12 = "J=dim(expr_data)[2]";
        System.out.println("R: " + s12);
        System.out.println(Rexpr = re.eval(s12));

        String s13 = "nboot=" + nboot;
        System.out.println("R: " + s13);
        System.out.println(Rexpr = re.eval(s13));

        String s14a = "Itot=1";
        System.out.println("R: " + s14a);
        System.out.println(Rexpr = re.eval(s14a));

        String s14 = "Itot1=(IMAX-IMIN)/10+1";
        System.out.println("R: " + s14);
        System.out.println(Rexpr = re.eval(s14));

        String s15 = "Jtot=(JMAX-JMIN)/10+1";
        System.out.println("R: " + s15);
        System.out.println(Rexpr = re.eval(s15));
    }

    /**
     *
     */
    private void init(String[] args) {

        options = MapArgOptions.maptoMap(args, valid_args);
        System.out.println("init " + options);
        seed = Integer.parseInt((String) options.get("-seed"));
        try {
            start = Integer.parseInt((String) options.get("-start"));
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        }
        nboot = Integer.parseInt((String) options.get("-boot"));
        datapath = (String) options.get("-data");
        workdir = (String) options.get("-workdir");
        workdir = StringUtil.replace(workdir, "\\", "/");
        imputedpath = (String) options.get("-imputed");
        outfile = (String) options.get("-outfile");
        methodspath = (String) options.get("-methods");

        String[] R_args = {"--no-save"};
        System.out.println("init: starting Rengine");
        re = new Rengine(R_args, false, new TextConsole());
        System.out.println("init: Rengine created, waiting for R");
        if (!re.waitForR()) {
            System.out.println("init: Cannot load R");
            System.exit(1);
        } else {
            System.out.println("init: R started");
        }

        String s0 = "rm(list=ls())";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = re.eval(s0));

        String s00 = "setwd(\"" + workdir + "\")";
        System.out.println("R: " + s00);
        System.out.println(Rexpr = re.eval(s00));

        String s01 = "library(polspline)";
        System.out.println("R: " + s01);
        System.out.println(Rexpr = re.eval(s01));

        String s02 = "library(irr)";
        System.out.println("R: " + s02);
        System.out.println(Rexpr = re.eval(s02));

        String s03 = "library(spatstat)";
        System.out.println("R: " + s03);
        System.out.println(Rexpr = re.eval(s03));

        String s04 = "source(\"" + methodspath + "\")";
        System.out.println("R: " + s04);
        System.out.println(Rexpr = re.eval(s04));
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 14 || args.length == 16) {
            NullDistrib rm = new NullDistrib(args);
        } else {
            System.out.println("syntax: java DataMining.NullDistrib\n" +
                    "<-seed 'random seed integer'>\n" +
                    "<-start 'OPTIONAL start position'>\n" +
                    "<-boot 'number of bootstrap samples'>\n" +
                    "<-data 'rda data file'>\n" +
                    "<-imputed 'rda imputed data file'>\n" +
                    "<-workdir 'working directory'>\n" +
                    "<-outfile 'output file'>\n" +
                    "<-methods 'path to R code'>"
            );
        }
    }
}
