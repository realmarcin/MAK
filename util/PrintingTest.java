package util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrintingTest extends Frame implements ActionListener {

    PrintCanvas canvas;

    public PrintingTest() {
        super("Printing Test");
        canvas = new PrintCanvas();
        add("Center", canvas);

        Button b = new Button("Print");
        b.setActionCommand("print");
        b.addActionListener(this);
        add("South", b);

        pack();
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("print")) {
            PrintJob pjob = getToolkit().getPrintJob(this,
                    "Printing Test", null);

            if (pjob != null) {
                Graphics pg = pjob.getGraphics();

                if (pg != null) {
                    canvas.printAll(pg);
                    pg.dispose(); // flush page
                }
                pjob.end();

            }
        }
    }

    public static void main(String args[]) {
        PrintingTest test = new PrintingTest();
        test.show();
    }
}

class PrintCanvas extends Canvas {

    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }

    public void paint(Graphics g) {
        Rectangle r = getBounds();

        g.setColor(Color.yellow);
        g.fillRect(0, 0, r.width, r.height);

        g.setColor(Color.blue);
        g.drawLine(0, 0, r.width, r.height);

        g.setColor(Color.red);
        g.drawLine(0, r.height, r.width, 0);
    }
}
