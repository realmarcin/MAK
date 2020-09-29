package dialog;

import util.StringUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GapDialog extends Dialog {

    String h = new String();
    public double cut;
    TextField ar = new TextField("30.0");
    Button b;
    Button c;
    Panel pan = new Panel();
    public boolean changed = false;
    ActionEvent enterPress;

    public GapDialog(Frame q, String f) {

        super(q, f, true);

        pan.setLayout(new BorderLayout());

        b = new Button("Set cutoff");
        b.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent e) {

                //System.out.println("button");
                h = ar.getText();
                //System.out.println(h+"  "+h);
                h = StringUtil.replace(h, " ", "");
                cut = (Double.parseDouble(h));
//System.out.println("gapdialog "+cut);
                changed = true;
                hide();
                dispose();
            }
        });

        c = new Button("Quit");
        c.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent e) {
                hide();
                dispose();
            }
        });

        pan.add("North", ar);
        pan.add("Center", b);
        pan.add("South", c);
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
