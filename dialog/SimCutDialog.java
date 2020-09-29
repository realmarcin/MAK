package dialog;

import util.StringUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimCutDialog extends Dialog {

    String key = null;
    public double[] cut = new double[3];

    TextField ar1 = new TextField("3.0");
    TextField ar2 = new TextField("1.5");
    TextField ar3 = new TextField("0.5");
    Button b;
    Button c;
    Panel pan = new Panel();
    ActionEvent enterPress;
    public boolean changed = false;

    public SimCutDialog(Frame q, String f, String k) {

        super(q, f, true);
        key = k;
        pan.setLayout(new GridLayout(1, 4));

        b = new Button("Set cutoffs");
        b.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent e) {

                //System.out.println("button");
                String val1 = ar1.getText();
                String val2 = ar2.getText();
                String val3 = ar3.getText();
                //System.out.println(h+"  "+h);
                val1 = StringUtil.replace(val1, " ", "");
                val2 = StringUtil.replace(val2, " ", "");
                val3 = StringUtil.replace(val3, " ", "");

                cut[0] = Double.parseDouble(val1);
                cut[1] = Double.parseDouble(val2);
                cut[2] = Double.parseDouble(val3);

                changed = true;
                hide();
                dispose();
            }
        });

        c = new Button("Quit");
        c.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent e) {

                cut = null;
                hide();
                dispose();
            }
        });

        pan.add(ar1);
        pan.add(ar2);
        pan.add(ar3);
        pan.add(b);
        pan.add(c);
        add(pan);
        pack();
        show();
    }

    /**
     * Old skool java AWT.
     */
    public final boolean handleEvent(Event ev) {

        if (ev.id == Event.WINDOW_DESTROY) {

            hide();
            dispose();
            return true;
        }
        return false;
    }

}
