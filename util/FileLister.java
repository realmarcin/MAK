package util;

import javax.swing.*;
import java.awt.*;

/**
 * A class to display a text file in a simple window.  The text file is given to
 * this class in the form of a pre-processed String.
 * mj@98
 */
public class FileLister extends JFrame {


    final static String test = "H";
    /**
     * A panel to display the buttons used by this class.
     */
    Panel buttons;

    /**
     * The text area that the file is displayed in.
     */
    TextArea text;

    /**
     * The close button to hide and dispose of the window.
     */
    Button close;

    /**
     * Constructor to build a File Lister given a file to display and a name for the
     * file
     */
    public FileLister(String name, String file) {

        super();
        getContentPane().setLayout(new BorderLayout());

        Font fontplain = new Font("Courier", Font.PLAIN, 14);
        setFont(fontplain);
        int lines = StringUtil.countOccur(file, "\n");
        FontMetrics thisfm = getFontMetrics(fontplain);
        int advance = thisfm.stringWidth(test);
        int descent = thisfm.getMaxDescent();
        int lineHeight = thisfm.getHeight() - descent;
//System.out.println("LINES : "+lines);

        text = new TextArea(lines + 2, 85);
        text.setFont(fontplain);
        text.setEditable(false);
        buttons = new Panel();
        close = new Button("Close");
        buttons.add(close);
        getContentPane().add("Center", text);
        getContentPane().add("South", buttons);
        pack();
        setTitle(name);
    }

    /**
        * Constructor to build a File Lister given a file to display and a name for the
        * file
        */
       public FileLister(String name, String file, Image icon) {

           super();
           getContentPane().setLayout(new BorderLayout());

           Font fontplain = new Font("Courier", Font.PLAIN, 14);
           setFont(fontplain);
           int lines = StringUtil.countOccur(file, "\n");
           FontMetrics thisfm = getFontMetrics(fontplain);
           int advance = thisfm.stringWidth(test);
           int descent = thisfm.getMaxDescent();
           int lineHeight = thisfm.getHeight() - descent;
//System.out.println("LINES : "+lines);

           text = new TextArea(lines + 2, 85);
           text.setFont(fontplain);
           text.setEditable(false);
           buttons = new Panel();
           close = new Button("Close");
           buttons.add(close);
           getContentPane().add("Center", text);
           getContentPane().add("South", buttons);

        if(icon != null) {
            setIconImage(icon);
        }

           pack();
           setTitle(name);
       }

    /**
     * Constructor to build a File Lister given a file to display.
     */
    public FileLister(String file) {
        super();
        getContentPane().setLayout(new BorderLayout());

        Font fontplain = new Font("Courier", Font.PLAIN, 14);
        setFont(fontplain);
        int lines = StringUtil.countOccur(file, "\n");
        FontMetrics thisfm = getFontMetrics(fontplain);
        int advance = thisfm.stringWidth(test);
        int descent = thisfm.getMaxDescent();
        int lineHeight = thisfm.getHeight() - descent;
        //System.out.println("LINES : "+lines);

        text = new TextArea(lines + 4, 80);
        text.setFont(fontplain);
        text.setEditable(false);
        buttons = new Panel();
        close = new Button("Close");
        buttons.add(close);
        getContentPane().add("Center", text);
        getContentPane().add("South", buttons);
        pack();
        text.setText(file);

    }

    /**
     * Function to append the given String to the end of the current text in the
     * window.
     */
    public void append(String s) {
        text.append(s);
    }

    /**
     * Function to set the given String as the text of the current window.
     */
    public void setText(String s) {
        text.setText(s);
    }

    /**
     * Function to handle any actions directed at this window.
     */
    public boolean action(Event e, Object arg) {
        if (e.target == close) {
            hide();
            dispose();
        }
        return false;
    }
}
