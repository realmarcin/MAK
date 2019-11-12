package DataMining;

import mathy.Matrix;
import util.TabFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

/**
 * Viewer application for block search trajectories.
 * <p/>
 * User: marcin
 * Date: Oct 11, 2007
 * Time: 5:07:30 PM
 */
public class TrajectoryView extends JFrame {

    double version = 0.01;
    String title = "Block search trajectory viewer v" + version;
    public JMenuBar menu_bar = new JMenuBar();
    public JMenu file_menu = new JMenu("File");
    public JMenuItem load_conf_item = new JMenuItem("");
    public JMenuItem save_item = new JMenuItem("Save");
    public JMenuItem quit_item = new JMenuItem("Quit");

    private JButton runBut = new JButton("Run trajectory");

    final static String[] MODES = {"random_test", "runTrajectory"};
    String mode;

    String datafile;

    public double data_max, data_min;
    double[][] data;
    TrajectoryViewCanvas mvc;

    ArrayList trajectory;

    FontMetrics fm;
    int medium_advance;
    final static int smallfont_size = 7;
    public final static Font fontsmall = new Font("MonoSpaced", Font.PLAIN, smallfont_size);
    final static int mediumfont_size = 12;
    public final static Font fontmedium = new Font("MonoSpaced", Font.BOLD, mediumfont_size);
    FontRenderContext frc;

    public int data_rows, data_cols;

    int dimx, dimy;
    int canvdimx, canvddimy;

    /**
     * @param args
     */
    public TrajectoryView(String[] args) {
        super();

        init(args);

        //ArrayList trajectory = TabFile.readtoList(args[1]);
        System.out.println("Creating random trajectory");
        trajectory = randomTrajectory(5, 6, 10);
        mvc = new TrajectoryViewCanvas(this);

        canvdimx = mvc.im.getWidth();//(data_rows + 1) * backmvc.xunit;
        canvddimy = mvc.im.getHeight();//(data_cols + 1) * backmvc.yunit;
        System.out.println("TrajectoryView canvdimx " + canvdimx + "\tcanvdimy " + canvddimy);

        setMethods();

        buildGUI();

        pack();

        dimx = this.getWidth() + canvdimx;
        dimy = this.getHeight() + canvddimy;
        System.out.println("TrajectoryView dimx " + dimx + "\tdimy " + dimy);

        setSize(dimx, dimy);
        setResizable(true);
        show();

        runTrajectory();

    }

    /**
     *
     */
    private void setMethods() {
        save_item.addActionListener(new SaveActionListener());
        load_conf_item.addActionListener(new LoadActionListener());
        quit_item.addActionListener(new QuitActionListener());

        runBut.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getSource().equals(runBut)) {
                    runTrajectory();
                }
            }
        });
    }

    /**
     * Runs the trajectory animation.
     */
    public void runTrajectory() {
        try {
            Thread t = new Thread();
            for (int i = 0; i < trajectory.size(); i++) {
                String iDform = (String) trajectory.get(i);
                mvc.current_block = BlockMethods.ijIDtoIcJctoList(iDform);
                mvc.current_block_index = i;
                System.out.println("runTrajectory " + i + "\t" + iDform);
                mvc.drawStuff();
                //repaint();
                t.sleep(mvc.millis);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mvc.current_block = null;
        mvc.current_block_index = -1;
    }

    /**
     *
     */
    private void buildGUI() {
        getContentPane().setLayout(new BorderLayout());
        file_menu.add(quit_item);
        menu_bar.add(file_menu);
        this.getContentPane().add(menu_bar, BorderLayout.NORTH);

        JPanel panel1 = new JPanel();
        panel1.setSize(mvc.getWidth(), mvc.getHeight());
        panel1.setLayout(new BorderLayout());
        panel1.add(mvc, "Center");
        this.getContentPane().add(panel1, BorderLayout.CENTER);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.add(runBut, "Center");
        this.getContentPane().add(panel2, BorderLayout.SOUTH);
    }

    /**
     * @param args
     */
    private void init(String[] args) {

        datafile = args[0];

        setTitle(title);
        AffineTransform af = new AffineTransform();
        frc = new FontRenderContext(af, false, false);
        Frame j = new Frame();
        fm = j.getFontMetrics(fontmedium);
        medium_advance = fm.stringWidth("A");
        data = TabFile.readtoDoubleArray(datafile);
        data_rows = data.length;
        data_cols = data[0].length;
        System.out.println("TrajectoryView data_rows " + data_rows + "\tdata_cols " + data_cols);
        data_min = Matrix.findMin(data);
        data_max = Matrix.findMax(data);
    }

    /**
     * @return
     */
    private ArrayList randomTrajectory(int a, int b, int num) {
        ArrayList ret = new ArrayList();
        Random r = new Random();
        int total = 0;
        String add = "";
        while (total < num) {
            int countx = 0, county = 0;
            while (countx < a) {
                int x = r.nextInt(data_rows);
                if (add.indexOf("," + x + ",") != -1 || add.indexOf("" + x + ",") != 0) {
                    add += "" + x + ",";
                    countx++;
                }
            }
            add = add.substring(0, add.length() - 1);
            add += "/";
            while (county < b) {
                int y = r.nextInt(data_cols);
                if (add.indexOf("," + y + ",") != -1 || add.indexOf("" + y + ",") != 0) {
                    add += "" + y + ",";
                    county++;
                }
            }
            add = add.substring(0, add.length() - 1);
            ret.add(add);
            add = "";
            total++;
        }
        return ret;
    }

    class LoadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {

        }
    }

    class SaveActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {

        }
    }

    final static class QuitActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length > 1 && args.length < 8) {
            TrajectoryView rm = new TrajectoryView(args);
        } else {
            System.out.println("syntax: java DataMining.TrajectoryView\n" +
                    "<mode>\n" +
                    "<... mode dependent list or arguments, see below ...>");
            System.out.println("usage: mode = random_test, runTrajectory");
            System.out.println("usage: if mode = random:");
            System.out.println("usage: <gene expression data file>");
            System.out.println("usage: <trajectory file>");
            System.out.println("usage: if mode = runTrajectory:");
            System.out.println("<null distribution>\n" +
                    "<gene expression data file>\n" +
                    "<rdata file>\n" +
                    "<prev_block file>\n" +
                    "<parameter file>" + "\n" +
                    "<R methods>\n" +
                    "<optional R_HOME path>");
        }
    }
}


