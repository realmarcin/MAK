package util;

public class ProgressThread extends Thread {

    private ProgressBar showBar = null;
    private int max = 0;
    public boolean shouldStop = false;

    public ProgressThread(ProgressBar progBar, int newmax) {
        super();
        showBar = progBar;
        max = newmax;
        showBar.setMaximum(max);
    }

    public void run() {
        while (!shouldStop)
            for (int idx = 1; idx <= max && !shouldStop; idx++) {
// Do your copying here or simply wait 100 msecs in "dummymode".
                try {
                    Thread.sleep(100);
                } catch (Exception interrupted) {
                }
                showBar.setValue(idx);
            }
    }

}
