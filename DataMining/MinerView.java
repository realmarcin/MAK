package DataMining;

import util.MoreArray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;

/**
 * Class to run and view results of associative biclustering.
 * <p/>
 * Bugs:
 * - x-axis bicluster numbering is off when scrolled
 * <p/>
 * FEATURES:
 * .
 * <p/>
 * User: marcin
 * Date: Oct 14, 2007
 * Time: 3:37:16 PM
 */
public class MinerView extends JFrame {
    public boolean debug = true;
    double version = 0.0167;
    String progname = "Blockada v";
    String title = progname + version;

    public JMenuBar menu_bar = new JMenuBar();
    public JMenu file_menu = new JMenu("File");
    public JMenu option_menu = new JMenu("Options");
    public JMenuItem load_datat_traj_item = new JMenuItem("Load expression data+trajectory");
    public JMenuItem load_traj_item = new JMenuItem("Load trajectory");
    public JMenuItem save_item = new JMenuItem("Save");
    public JMenuItem quit_item = new JMenuItem("Quit");
    public JCheckBoxMenuItem full_item = new JCheckBoxMenuItem("Full criteria", false);
    public JCheckBoxMenuItem trunc_item = new JCheckBoxMenuItem("Truncate data to (1,100)x(1,100)", false);
    public JScrollBar jscrollX;
    public JScrollBar jscrollY;
    private JScrollPane scrollPane;
    /*JButton startBut = new JButton("Start");
    JButton randomBut = new JButton("Start RANDOMIZE_BLOCKS");
    JButton stopBut = new JButton("Stop");*/

    public MinerViewCanvas mvc;

    MinerViewBack mvb;


    /**
     * @param args
     */
    public MinerView(String[] args) {
        super();
        mvb = new MinerViewBack(args, true, mvc);

        init();

        mvc = new MinerViewCanvas(this, mvb.totalCanvdimx);
        System.out.println("MinerView canvdimx " + mvc.sizecanvx + "\tcanvdimy " + mvc.sizecanvy +
                "\tsizex " + mvc.sizex + "\tsizey " + mvc.sizey);

        buildGUI();
        setSize(mvc.sizecanvx, mvc.sizecanvy);
        mvb.backmvc = mvc;
        if (mvb.pixel != -1) {
            mvc.xunit = mvb.pixel;
            mvc.yunit = mvb.pixel;
        }

        setTitle(title);
        setResizable(false);
        setVisible(true);
        pack();
        if (mvb.mode == mvb.RUN)
            mvb.rmb.run();
        /*else if (mode == PLAY) {

        }*/
        if (mvb.animate) {
            mvb.createStatic();
            try {
                mvb.animate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private void buildGUI() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        load_datat_traj_item.addActionListener(new LoadData_TrajActionListener());
        load_traj_item.addActionListener(new LoadTrajActionListener());
        save_item.addActionListener(new SaveImageActionListener());
        quit_item.addActionListener(new QuitActionListener());

        file_menu.add(load_traj_item);
        file_menu.add(load_datat_traj_item);
        file_menu.add(save_item);
        file_menu.add(quit_item);
        menu_bar.add(file_menu);

        full_item.addActionListener(new FullActionListener());
        trunc_item.addActionListener(new TruncActionListener());
        option_menu.add(full_item);
        option_menu.add(trunc_item);
        menu_bar.add(option_menu);
        JPanel jpl = new JPanel();
        jpl.setLayout(new BorderLayout());

        cp.add(menu_bar, BorderLayout.NORTH);
        scrollPane = new JScrollPane(mvc, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        Ruler columnView = new Ruler(Ruler.HORIZONTAL, true);
        scrollPane.setColumnHeaderView(columnView);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(mvb.canvdimx, mvb.canvdimy));
        panel.add(mvc, BorderLayout.CENTER);
        jpl.add(panel, BorderLayout.CENTER);

        System.out.println("jscrollX totalCanvdimx " + mvb.totalCanvdimx);
        jscrollX = new JScrollBar(JScrollBar.HORIZONTAL, 0, mvb.canvdimx, 0, mvb.totalCanvdimx);
        jscrollX.addAdjustmentListener(new XAdjustmentListener());
        jpl.add(jscrollX, BorderLayout.SOUTH);

        System.out.println("jscrollX totalCanvdimy " + mvb.totalCanvdimy + "\t" + mvb.canvdimy + "\t" + mvb.totalCanvdimy);
        jscrollY = new JScrollBar(JScrollBar.VERTICAL, 0, mvb.canvdimy, 0, mvb.totalCanvdimy);
        jscrollY.addAdjustmentListener(new YAdjustmentListener());
        jpl.add(jscrollY, BorderLayout.EAST);

        cp.add(jpl, BorderLayout.CENTER);

        /*
        final MinerView pass = this;
        startBut.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getSource().equals(startBut)) {
                    *//*rmb = new RunMinerBack(modargs[1], modargs[2], modargs[3], modargs[4],
                            modargs[5], modargs[6], modargs[7], modargs[8], pass);
                    rmb.run(FULL_CRITERIA);*//*
                }
            }
        });

        randomBut.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getSource().equals(randomBut)) {
                    *//*rmb = new RunMinerBack(modargs[1], modargs[2], modargs[3], modargs[4],
                            modargs[5], modargs[6], modargs[7], modargs[8], pass);
                    rmb.run(FULL_CRITERIA);*//*
                }
            }
        });

        stopBut.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getSource().equals(stopBut)) {
                    rmb.stop();
                    rmb = null;
                }
            }
        });

        JPanel bjpl = new JPanel();
        bjpl.setLayout(new FlowLayout());
        bjpl.add(startBut);
        bjpl.add(randomBut);
        bjpl.add(stopBut);
        cp.add(bjpl, BorderLayout.SOUTH);*/
    }

    /**
     * -trajectory
     * C:\projects\integr8_genom\Miner\miner_results\results_CIN5_optexp_crittest\MADR_12971501297150_-1_sesi_toplist.txt
     * -data
     * C:\projects\integr8_genom\Miner\rda\rdataNN1_impute2_ortho_Data.txt
     */
    final class LoadData_TrajActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String ctrajpath = null;
            String cdatapath = null;

            Frame f = new Frame();
            FileDialog fd = new FileDialog(f, "Select expression data file.", FileDialog.LOAD);
            fd.setVisible(true);
            //if (dataexprpath == null)
            if (fd.getDirectory() != null && fd.getFile() != null) {
                cdatapath = fd.getDirectory() + fd.getFile();
                setVisible(false);
                dispose();
            }
            f = new Frame();
            fd = new FileDialog(f, "Select trajectory trajectory file.", FileDialog.LOAD);
            fd.setVisible(true);
            if (fd.getDirectory() != null && fd.getFile() != null) {
                ctrajpath = fd.getDirectory() + fd.getFile();
                setVisible(false);
                dispose();
            }
            cdatapath = cdatapath != null ? cdatapath : (String) mvb.options.get("-dataexpr");
            ArrayList argsAr = new ArrayList();
            if (ctrajpath != null) {
                argsAr.add("-traj");
                argsAr.add(ctrajpath);
            }
            argsAr.add("-dataexpr");
            argsAr.add(cdatapath);
            if (mvb.pixel != MinerViewCanvas.default_xunit) {
                argsAr.add("-pixel");
                argsAr.add(mvb.pixel);
            }

            String[] args = MoreArray.ArrayListtoString(argsAr);
            System.out.println("new MinerView");
            MoreArray.printArray(args);
            if (args != null) {
                MinerView mv = new MinerView(args);
            }
        }
    }

    final class LoadTrajActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String ctrajpath = null;
            String cdatapath = null;

            Frame f = new Frame();
            FileDialog fd = new FileDialog(f, "Select trajectory trajectory file.", FileDialog.LOAD);
            fd.setVisible(true);
            if (fd.getDirectory() != null && fd.getFile() != null) {
                ctrajpath = fd.getDirectory() + fd.getFile();
            }
            cdatapath = cdatapath != null ? cdatapath : (String) mvb.options.get("-dataexpr");
            if (ctrajpath != null) {
                ArrayList argsAr = new ArrayList();
                if (ctrajpath != null) {
                    argsAr.add("-traj");
                    argsAr.add(ctrajpath);
                }
                argsAr.add("-dataexpr");
                argsAr.add(cdatapath);
                if (mvb.pixel != MinerViewCanvas.default_xunit) {
                    argsAr.add("-pixel");
                    argsAr.add(mvb.pixel);
                }
                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockListMethods.readAny(ctrajpath, debug);
                } catch (Exception e) {
                    e.printStackTrace();
                    /*try {
                        vbl = ValueBlockListMethods.readSimple(ctrajpath);
                    } catch (Exception e1) {
                        try {
                            vbl = ValueBlockListMethods.readBIC(ctrajpath, false);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }*/
                }

                if (vbl != null && vbl.size() > 0) {
                    setVisible(false);
                    dispose();
                    String[] args = MoreArray.ArrayListtoString(argsAr);
                    System.out.println("new MinerView");
                    MinerView mv = new MinerView(args);
                } else {
                    Frame fr = new Frame();
                    dialog.ErrorDialog erd = new dialog.ErrorDialog(fr, "Trajectory is empty or otherwise corrupt");
                    erd.setVisible(true);
                }
            }
        }
    }

    /**
     *
     */
    final class SaveImageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {

        }
    }

    /**
     *
     */
    final static class QuitActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }


    final class FullActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            boolean state = full_item.getState();
            if (!state)
                full_item.setState(true);
            else
                full_item.setState(false);
        }
    }

    /**
     *
     */
    final class TruncActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            boolean state = trunc_item.getState();
            if (!state)
                trunc_item.setState(true);
            else
                trunc_item.setState(false);
        }
    }

    /**
     *
     */
    final class XAdjustmentListener implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent e) {
            //System.out.println("MinerView Scrollbar event " + e.getValue());
            mvc.setXOffset(e.getValue());
        }
    }

    /**
     *
     */
    final class YAdjustmentListener implements AdjustmentListener {
        public void adjustmentValueChanged(AdjustmentEvent e) {
            //System.out.println("MinerView Scrollbar event " + e.getValue());
            mvc.setYOffset(e.getValue());
        }
    }

    /**
     * @param event
     */
    public void adjustmentValueChanged(AdjustmentEvent event) {
        // The event comes from JScrollBar jscrollX
        if (event.getSource() == jscrollX) {
            int val = jscrollX.getValue();
            //System.out.println("MinerView Scrollbar event changed " + val);
            mvc.drawStuff(true);
        }
    }


    /**
     *
     */
    public void init() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);
        setBackground(Color.lightGray);

        int lastslash = mvb.traj_path.lastIndexOf("/");
        title = progname + version + " " + mvb.traj_path.substring(lastslash + 1);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 4 || args.length == 6 ||
                args.length == 8 || args.length == 10 || args.length == 12 || args.length == 14) {
            MinerView rm = new MinerView(args);
        } else if (args.length == 0) {
            System.out.println("syntax: java DataMining.MinerView\n" +
                    "<-param 'parameter file' OPTIONAL>\n" +
                    "<-dataexpr 'expr data file'>\n" +
                    "<-datappi 'interaction data file' OPTIONAL>\n" +
                    "<-traj 'trajectory file'>\n" +
                    "<-animate 'out dir' OPTIONAL>\n" +
                    "<-pixel integer OPTIONAL>\n" +
                    "<-range float,float OPTIONAL>\n"
            );
            System.out.println("for syntax: java DataMining.MinerView -help\n");
        }
    }

}


