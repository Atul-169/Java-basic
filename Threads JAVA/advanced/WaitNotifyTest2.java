package advanced;

public class WaitNotifyTest2 {
    private static final Object lock = new Object();

    public static void main(String[] args) {
        // Thread that waits
        Runnable waitRunnable = new Runnable() {
            public void run() {
                synchronized (lock) {
                    try {
                        System.out.println("Thread 1: Going to wait...");
                        lock.wait(); // Wait until notified
                        System.out.println("Thread 1: Got notified, continuing...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread [] waitingThreads = new Thread[5];
        for (int i = 0; i< waitingThreads.length; i++) {
            waitingThreads[i] = new Thread(waitRunnable);
        }

        // Thread that notifies
        Runnable notifyRunnable = new Runnable() {
            public void run() {
                synchronized (lock) {
                    System.out.println("Thread 2: About to notify...");
                    lock.notifyAll(); // Wake up the waiting thread
                    System.out.println("Thread 2: Notified!");
                }
            }
        };
        Thread notifyingThread = new Thread(notifyRunnable);
       

        // Start threads
        for (int i = 0; i< waitingThreads.length; i++) {
            waitingThreads[i].start();
        }

        try {
            Thread.sleep(2000); // Let first thread wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        notifyingThread.start();       
    }

}
