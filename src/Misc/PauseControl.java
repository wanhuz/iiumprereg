package Misc;

public class PauseControl {
    private boolean needToPause;

    public synchronized void pausePoint() throws InterruptedException{
        while (needToPause) {
            wait();
        }
    }

    public synchronized void pause() {
        System.out.println("paused");
        needToPause = true;
    }

    public synchronized void unpause() {
        System.out.println("unpaused");
        needToPause = false;
        this.notifyAll();
    }
}