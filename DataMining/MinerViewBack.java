package DataMining;

import mathy.Matrix;
import org.w3c.dom.Node;
import util.MapArgOptions;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * User: marcin
 * Date: Feb 23, 2010
 * Time: 6:56:18 PM
 */
public class MinerViewBack {


    boolean debug = true;

    HashMap options;
    String[] valid_args = {"-param", "-dataexpr", "-datappi", "-traj", "-help", "-animate", "-pixel", "-range"};

    String dataexprpath, datappipath, trajpath, parameter_path;
    public double dataexpr_max, dataexpr_min;
    public double dataexpr_max_cut, dataexpr_min_cut;
    public double datappi_max, datappi_min;
    double[][] dataexpr, datappi;
    public RunMinerBack rmb;
    /* boolean fullCriteria = false;
    boolean truncData = false;
    boolean random = false;*/
    Parameters prm;

    public MinerViewCanvas backmvc;

    ValueBlockList trajectory_vbl;

    public int data_rows, data_cols, datappi_rows, datappi_cols;
    public int block_rows, block_cols;

    String[] xlabels, ylabels, ppixlabels, ppiylabels;
    int canvdimx = 1050, canvdimy = 800;
    int totalCanvdimx = -1;
    int totalCanvdimy = -1;
    ValueBlockList trajectory;
    int pixel = -1;
    double[] range = {-1.0, 1.0};

    public boolean normalized_crit = false;
    public boolean animate = false;
    public String animate_path;
    public String traj_path;
    public boolean traj_path_is_dir;

    int mode;
    static int RUN = 1, PLAY = 2;

    /**
     * @param args
     */
    public MinerViewBack(String[] args, boolean frame, MinerViewCanvas mvc) {
        if (args.length > 0) {
            options = MapArgOptions.maptoMap(args, valid_args);
        }

        init();

        backmvc = mvc;

        if (frame) {
            if (mode == RUN)
                rmb.run();
            else if (mode == PLAY) {
                if (animate) {
                    createStaticAndDynamic();
                }
            }
        } else {
            createStaticAndDynamic();
        }

    }

    /**
     *
     */
    private void createStaticAndDynamic() {
        createStatic();
        try {
            animate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws Exception
     */
    public void animate() throws Exception {
        int delaytime = 20;
        File dir = new File(animate_path);
        String[] list = dir.list();
        BufferedImage[] frames = new BufferedImage[list.length];
        for (int i = 0; i < list.length; i++) {
            String s = animate_path + "/" + list[i];
            System.out.println("animate reading " + s);
            frames[i] = ImageIO.read(new File(s));
        }

        ImageWriter iw = ImageIO.getImageWritersByFormatName("gif").next();

        String traj = null;
        try {
            traj = traj_path.substring(traj_path.lastIndexOf("/"));
        } catch (Exception e) {
            traj = traj_path;
            //e.printStackTrace();
        }
        String output = animate_path + "/" + traj + ".gif";
        output = StringUtil.replace(output, "//", "/");
        output = StringUtil.replace(output, "//", "/");
        output = StringUtil.replace(output, ".txt", "");
        System.out.println("animate output " + output);
        File outf = new File(output);
        ImageOutputStream ios = ImageIO.createImageOutputStream(outf);
        iw.setOutput(ios);
        iw.prepareWriteSequence(null);

        for (int i = 0; i < frames.length; i++) {
            BufferedImage src = frames[i];
            ImageWriteParam iwp = iw.getDefaultWriteParam();
            IIOMetadata metadata = iw.getDefaultImageMetadata(
                    new ImageTypeSpecifier(src), iwp);

            configure(metadata,
                    "" + delaytime, i);

            IIOImage ii = new IIOImage(src, null, metadata);
            iw.writeToSequence(ii, null);
        }

        iw.endWriteSequence();
        ios.close();
        System.out.println("animate output done");
    }

    /**
     * @param meta
     * @param delayTime
     * @param imageIndex
     */
    public static void configure(IIOMetadata meta, String delayTime, int imageIndex) {

        String metaFormat = meta.getNativeMetadataFormatName();

        if (!"javax_imageio_gif_image_1.0".equals(metaFormat)) {
            throw new IllegalArgumentException(
                    "Unfamiliar gif metadata format: " + metaFormat);
        }

        Node root = meta.getAsTree(metaFormat);

        //find the GraphicControlExtension node
        Node child = root.getFirstChild();
        while (child != null) {
            if ("GraphicControlExtension".equals(child.getNodeName())) {
                break;
            }
            child = child.getNextSibling();
        }

        IIOMetadataNode gce = (IIOMetadataNode) child;
        gce.setAttribute("userDelay", "FALSE");
        gce.setAttribute("delayTime", delayTime);

        //only the first node needs the ApplicationExtensions node
        if (imageIndex == 0) {
            IIOMetadataNode aes =
                    new IIOMetadataNode("ApplicationExtensions");
            IIOMetadataNode ae =
                    new IIOMetadataNode("ApplicationExtension");
            ae.setAttribute("applicationID", "NETSCAPE");
            ae.setAttribute("authenticationCode", "2.0");
            byte[] uo = new byte[]{
                    //last two bytes is an unsigned short (little endian) that
                    //indicates the the number of times to loop.
                    //0 means loop forever.
                    0x1, 0x0, 0x0
            };
            ae.setUserObject(uo);
            aes.appendChild(ae);
            root.appendChild(aes);
        }

        try {
            meta.setFromTree(metaFormat, root);
        } catch (IIOInvalidTreeException e) {
            //shouldn't happen
            throw new Error(e);
        }
    }

    /**
     * Creates images for a trajectory.
     */
    public void createStatic() {

        for (int i = 0; i < trajectory.size(); i++) {
            backmvc.curTrajectoryPosition = i;
            //backmvc.drawTrajectory(backmvc.ig);
            String prefix = "" + i;
            while (prefix.length() < 3)
                prefix = "" + 0 + prefix;

            String out = animate_path + "/" + prefix + "_traj.jpeg";
            writecanJPEG(out);
            //writecanGIF(prefix + "_traj.gif");
        }
        backmvc.curTrajectoryPosition = trajectory.size() - 1;
        backmvc.drawStuff();
    }


    /**
     * @param file
     */
    public void writecanJPEG(String file) {
        //java.awt.image.BufferedImage bi = cgcan.screenShot();
        int height = this.canvdimy;
        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(backmvc.sizecanvx, height,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        try {
            File ft = new File(file);
            boolean test = ft.createNewFile();
            System.out.println("writecanJPEG " + test + "\t" + ft.exists());
            FileOutputStream f = new FileOutputStream(ft);
            Graphics2D g = bi.createGraphics();
            backmvc.sizey = height;
            backmvc.drawStuff();
            backmvc.paint(g);
            g.dispose();

// Get the canvas size
            //Dimension size = canvas.getSize();

// create a buffered image the correct size
            //BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

// paint the canvas to the image's graphics context
            //Graphics2D graphics = image.createGraphics();
            //canvas.paintAll(graphics);

// tidy up
            //graphics.dispose();

// save the buffered image to a file
            //FileOutputStream fos = null;
            try {
                //fos = new FileOutputStream("image.jpg");
                ImageIO.write(bi, "jpg", f);
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(f);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
            param.setQuality((float) 100, true);
            param.setXDensity(backmvc.sizecanvx);
            param.setYDensity(height);
            encoder.encode(bi, param);
            g.dispose();
            f.close();*/
        } catch (Exception e) {
            System.out.println("writecanJPEG " + file);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * @param file
     */
    /*public void writecanGIF(String file) {
        try {
            BufferedImage image = new BufferedImage(backmvc.sizecanvx, backmvc.sizecanvy, BufferedImage.TYPE_INT_ARGB);
            backmvc.drawStuff(true);
            backmvc.paint(image.getGraphics());
            ImageIO.write(image, "GIF", new File(file));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    */

    /**
     * @return
     */
    public int sumExps() {
        int sum = 0;
        for (int i = 0; i < trajectory.size(); i++) {
            ValueBlock v = (ValueBlock) trajectory.get(i);
            if (v != null) {
                sum += v.exps.length;
            } else
                System.out.println("WARNING block " + i + " is null");
        }
        return sum;
    }

    /**
     * @return
     */
    public int maxGenes() {
        int max = 0;
        for (int i = 0; i < trajectory.size(); i++) {
            ValueBlock v = (ValueBlock) trajectory.get(i);
            if (v != null) {
                if (v.genes.length > max)
                    max = v.genes.length;
            } else
                System.out.println("WARNING block " + i + " is null");
        }
        return max;
    }

    /**
     *
     */
    /*public void checkIsPreCrit() {
        double crit_max = 0;
        double precrit_max = 0;

        for (int i = 0; i < trajectory.size(); i++) {
            ValueBlock v = (ValueBlock) trajectory.get(i);
            if (v.pre_criterion > precrit_max)
                precrit_max = v.pre_criterion;

            if (v.full_criterion > 1) {
                isPreCrit = false;
                if (v.full_criterion > 3) {
                    if (v.full_criterion > crit_max)
                        crit_max = v.full_criterion;
                }
            }
        }

        if (crit_max != 0) {
            normalized_crit = true;
            for (int i = 0; i < trajectory.size(); i++) {
                ValueBlock v = (ValueBlock) trajectory.get(i);
                v.full_criterion /= crit_max;
                trajectory.set(i, v);
            }
        }

        if (precrit_max > 1) {
            normalized_crit = true;
            for (int i = 0; i < trajectory.size(); i++) {
                ValueBlock v = (ValueBlock) trajectory.get(i);
                v.pre_criterion /= precrit_max;
                trajectory.set(i, v);
            }
        }

    }*/

    /**
     * Runs the trajectory animation.
     */
    public void updateBlocks() {
        System.out.println("MinerView updateBlocks");
        if (rmb != null) {
            //System.out.println("MinerView updateBlocks rmb");
            if (rmb.mi != null) {
                //System.out.println("MinerView updateBlocks rmb.mi");
                if (rmb.mi.trajectory != null) {
                    //System.out.println("MinerView updateBlocks rmb.mi.trajectory");
                    if (rmb.mi.trajectory.size() > 0) {
                        //System.out.println("MinerView updateBlocks drawStuff");
                        backmvc.drawStuff(true);
                        //backmvc.updateSize();
                    } else
                        System.out.println("MinerView updateBlocks rmb.mi.trajectory is empty");
                } else
                    System.out.println("MinerView updateBlocks rmb.mi.trajectory is null");
            } else
                System.out.println("MinerView updateBlocks rmb.mi is null");
        } else
            System.out.println("MinerView updateBlocks rmb is null");
    }

    /**
     *
     */
    private void init() {
        prm = new Parameters();
        parameter_path = null;
        if (parameter_path != null) {
            mode = RUN;
        } else if (trajpath != null) {
            mode = PLAY;
        }
        if (options != null) {
            parameter_path = (String) options.get("-param");
            //System.out.println("MinerView parameter_path " + parameter_path);
            trajpath = (String) options.get("-traj");
            //System.out.println("MinerView trajectory " + trajectory);
            dataexprpath = (String) options.get("-dataexpr");
            datappipath = (String) options.get("-datappi");
            System.out.println("MinerViewBack dataexprpath " + dataexprpath);
            Object o = options.get("-pixel");
            if (o != null) {
                pixel = Integer.parseInt(((String) o));
                System.out.println("MinerViewBack pixel " + pixel);
            }
        }

        int moves = -1;
        if (parameter_path != null) {
            prm.read(parameter_path);
            moves = prm.MAXMOVES[0];
            if (moves == -1)
                moves = 50;
            else if (moves < 10)
                moves = 10;
        } else if (trajpath != null) {
            File test = new File(trajpath);
            if (!test.exists()) {
                System.out.println("Trajectory file does not exist " + trajpath);
                System.exit(1);
            }

            try {
                trajectory = ValueBlockListMethods.readAny(trajpath, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (trajectory != null) {
                System.out.println("MinerView trajectory " + trajectory.size());
                //checkIsPreCrit();
                int size = trajectory.size();
                System.out.println("MinerView read " + size);
                moves = size;
            }
        }

        int totalexps = trajectory != null ? sumExps() : 100;
        int maxgenes = maxGenes();
        int nmoves = trajectory != null ? trajectory.size() : 100;
        System.out.println("totalexps " + totalexps + "\tmaxgenes " + maxgenes);

        int thispixel = pixel != -1 ? pixel : MinerViewCanvas.default_xunit;
        int realtotalCanvdimx = MinerViewCanvas.xOffset + 2 * thispixel * (totalexps + moves);
        //+MinerViewCanvas.xunit * nmoves;//(prm.JMAX + 1) *
        totalCanvdimx = 1000000;//realtotalCanvdimx + 500;//

        int realtotalCanvdimy = MinerViewCanvas.yOffset + thispixel * maxgenes;
        System.out.println("total " + totalexps + "\tnmoves " + nmoves + "\t" + totalCanvdimx + "\t" + realtotalCanvdimx
                + "\t" + totalCanvdimy + "\t" + realtotalCanvdimy);
        totalCanvdimy = 2000;//realtotalCanvdimy;//1000;
        //modargs = origargs;

        //prm.read((String) options.get("-param"));
        if (dataexprpath == null && prm != null && prm.EXPR_DATA_PATH != null) {
            dataexprpath = prm.EXPR_DATA_PATH;
            System.out.println("MinerView reading dataexprpath " + dataexprpath);
        }
        if (datappipath == null && prm != null && prm.INTERACT_DATA_PATH != null) {
            datappipath = prm.INTERACT_DATA_PATH;
            System.out.println("MinerView reading datappipath " + datappipath);
        }

        if (dataexprpath != null) {
            String[][] all = TabFile.readtoArray(dataexprpath);
            xlabels = all[0];
            ylabels = MoreArray.extractColumnStr(all, 1);
            all = MoreArray.removeColumn(all, 1);
            all = MoreArray.removeRow(all, 1);
            /*System.out.println("MinerView expr data first column");
            MoreArray.printArray(MoreArray.extractColumnStr(all, 1));*/
            dataexpr = MoreArray.convfromString(all);
            data_rows = dataexpr.length - 1;
            data_cols = dataexpr[0].length - 1;
            System.out.println("MinerView data_rows " + data_rows + "\tdata_cols " + data_cols);
            //MoreArray.printArray(dataexpr[1]);
            dataexpr_min = Matrix.findMin(dataexpr);
            dataexpr_max = Matrix.findMax(dataexpr);
            System.out.println("MinerView dataexpr range " + dataexpr_min + "\t" + dataexpr_max + "\t" +
                    (dataexpr_max - dataexpr_min));

            if (options != null) {
                Object o2 = options.get("-range");
                if (o2 != null) {
                    if (((String) o2).equals("min,max")) {
                        range[0] = dataexpr_min;
                        range[1] = dataexpr_max;
                    } else {
                        String[] range_data = (((String) o2)).split(",");
                        range = new double[2];
                        range[0] = Double.parseDouble(range_data[0]);
                        range[1] = Double.parseDouble(range_data[1]);
                    }
                    System.out.println("MinerViewBack range " + o2 + "\t\t" + range[0] + "\t" + range[1]);
                }
            }

            if (range != null) {
                dataexpr_min_cut = range[0];
                dataexpr_max_cut = range[1];
                System.out.println("MinerView dataexpr range cutoffs " + dataexpr_min_cut + "\t" + dataexpr_max_cut + "\t" +
                        (dataexpr_max_cut - dataexpr_min_cut));
            }
            System.out.println("MinerView reordering by mean");
            ValueBlockListMethods.reorderByMean(dataexpr, trajectory);
            System.out.println("MinerView reordering done");
        }

        if (datappipath != null) {
            String[][] all = TabFile.readtoArray(datappipath);
            ppixlabels = all[0];
            ppiylabels = MoreArray.extractColumnStr(all, 1);
            all = MoreArray.removeColumn(all, 1);
            all = MoreArray.removeRow(all, 1);
            datappi = MoreArray.convfromString(all);
            datappi_rows = datappi.length - 1;
            datappi_cols = datappi[0].length - 1;
            System.out.println("MinerView data_rows " + datappi_rows + "\tdata_cols " + datappi_cols);
            //MoreArray.printArray(dataexpr[1]);
            datappi_min = Matrix.findMin(datappi);
            datappi_max = Matrix.findMax(datappi);
            System.out.println("MinerView datappi range " + datappi_min + "\t" + datappi_max + "\t" + (datappi_max - datappi_min));
        }

        if (mode == RUN) {
            rmb = new RunMinerBack(this);
            dataexpr = rmb.expr_matrix.data;
            dataexpr_min = Matrix.findMin(rmb.expr_matrix.data);
            dataexpr_max = Matrix.findMax(rmb.expr_matrix.data);
            if (range != null) {
                dataexpr_min = range[0];
                dataexpr_max = range[1];
            }
        }

        if (options != null) {
            traj_path = (String) options.get("-traj");
            File testtraj = new File(traj_path);
            if (testtraj.exists()) {
                if (testtraj.isDirectory()) {
                    traj_path_is_dir = true;
                }
            }
            String s1 = (String) options.get("-animate");
            if (s1 != null) {
                animate = true;
                animate_path = s1;
                //int i1 = traj_path.indexOf("/");
                //int i2 = traj_path.indexOf("/", i1 + 1);
                int i1 = traj_path.lastIndexOf("/");
                String traj_path_prefix = traj_path.substring(i1 + 1);
                traj_path_prefix = StringUtil.replace(traj_path_prefix, ".txt", "");
                System.out.println("MinerView traj_path_prefix " + traj_path_prefix);
                animate_path += "/" + traj_path_prefix;
                System.out.println("MinerView animate_path " + animate_path);
                File make = new File(animate_path);
                if (!make.exists())
                    make.mkdir();
                else {
                    int c = 0;
                    File newdir = new File(animate_path + "_" + c);
                    while (newdir.exists()) {
                        c++;
                        newdir = new File(animate_path + "_" + c);
                    }
                    System.out.println("Renaming from: " + animate_path);
                    System.out.println("Renaming to  : " + newdir.toString());
                    make.renameTo(newdir);
                    make.mkdir();
                    /*String[] list = make.list();
                    for (int i = 0; i < list.length; i++) {
                        File nowf = new File(animate_path + "/" + list[i]);
                        nowf.delete();*/
                }
            }
        }
    }

}
