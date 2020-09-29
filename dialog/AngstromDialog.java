package dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AngstromDialog extends Dialog {

    String value = new String();
    String key = null;
    public double cut;
    TextField ar = new TextField("5.0");
    Button b;
    Button c;
    Panel pan = new Panel();
    ActionEvent enterPress;
    public boolean changed = false;

    public AngstromDialog(Frame q, String f, String k) {

        super(q, f, true);
        key = new String(k);
        pan.setLayout(new BorderLayout());

        b = new Button("Set cutoff");
        b.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent e) {

                //System.out.println("button");
                value = ar.getText();
                //System.out.println(h+"  "+h);
                value = util.StringUtil.replace(value, " ", "");
                cut = Double.parseDouble(value);

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