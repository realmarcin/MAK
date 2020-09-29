package util;

import java.awt.*;

public class ProgressBar extends Frame {
    private int current;
    private int Max;
    private static final int FrameBottom = 24;

    public ProgressBar(String Title) {
        //super(Title);

        current = 0;

        setResizable(false);

        setLayout(null);
        //pack();
        //addNotify();
        //resize(insets().left + insets().right + 379, insets().top + insets().bottom + FrameBottom);
        setSize(400, 100);
        //System.out.println("bar ready to be shown!!");
    }

    public ProgressBar(String Title, int TotalItems) {
        super(Title);

        current = 0;
        Max = TotalItems;

        setResizable(false);

        setLayout(null);
        //pack();
        addNotify();
        //resize(400, 100);
        setSize(insets().left + insets().right + 379, insets().top + insets().bottom + FrameBottom);
        //System.out.println("bar ready to be shown!!");
    }

    public void setMaximum(int newmax) {
        Max = newmax;
    }

    public synchronized void show() {
        move(50, 50);
        super.show();
    }

    /**
     * Update the current and then update the progress indicator. If we have
     * updated the progress indicator once for each item, dispose of the
     * progress indicator.
     */
    public void setValue(int newcurrent) {
        System.out.println("current " + current + "\tswitching to " + newcurrent);
        current = newcurrent;

        /*if (current == Max)
            dispose();
        else*/
        if (current == Max)
            repaint();
    }


    /**
     * Paint the progress indicator.
     */
    public void paint(Graphics g) {
        System.out.println("painting");

        Dimension FrameDimension = getSize();
        double PercentComplete = (double) current * 100.0 / (double) Max;
        int BarPixelWidth = (int) ((double) (FrameDimension.width * current) / (double) Max);

        // Fill the bar with the appropriate percent full.
        g.setColor(Color.red);
        g.fillRect(0, 0, BarPixelWidth, FrameDimension.height);

// Build a String showing the % completed as a numeric value.
        String s = String.valueOf((int) PercentComplete) + "%";

// Set the color of the text.  If we don't, it appears in the same //color as the rectangle making the text effectively invisible.
        g.setColor(Color.black);

        // Calculate the width of the String in pixels.
        //We use this to center the String in the progress bar window.
        FontMetrics fm = g.getFontMetrics(g.getFont());
        int StringPixelWidth = fm.stringWidth(s);

        g.drawString(s, (int) ((double) (FrameDimension.width - StringPixelWidth) / 2.0), FrameBottom);
    }

    public boolean handleEvent(Event event) {
        if (event.id == Event.WINDOW_DESTROY) {
            //dispose();
            return true;
        }

        return super.handleEvent(event);
    }
}

