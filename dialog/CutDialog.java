package dialog;

import util.StringUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CutDialog extends Dialog {

    String h = new String();
    public double cutval = -1;
    TextField ar = new TextField("99.0");
    Button b, c, d;
    Panel pan = new Panel();
    ActionEvent enterPress;
    public boolean changed = false;
    public String greater = "greater";

    public CutDialog(Frame q, String f) {

        super(q, f, true);
        pan.setLayout(new GridLayout(1, 4));

        b = new Button("Set greater than cutoff");
        b.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent e) {
                //System.out.println("button");
                h = ar.getText();
                //System.out.println(h+"  "+h);
                h = StringUtil.replace(h, " ", "");
                if (h.length() > 0) {
                    cutval = (double) Double.parseDouble(h);
                    //System.out.println(g+"  "+g);

                    greater = "greater";
                    changed = true;
                    hide();
                    dispose();
                }
            }
        });

        d = new Button("Set less than cutoff");
        d.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent e) {
                //System.out.println("button");
                h = ar.getText();
                //System.out.println(h+"  "+h);
                h = StringUtil.replace(h, " ", "");
                if (h.length() > 0) {
                    cutval = (double) Double.parseDouble(h);
                    //System.out.println(g+"  "+g);
                    greater = "less";
                    changed = true;
                    hide();
                    dispose();
                }
            }
        });

        c = new Button("Quit");
        c.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent e) {
                hide();
                dispose();
            }
        });

        pan.add(ar);
        pan.add(b);
        pan.add(d);
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
