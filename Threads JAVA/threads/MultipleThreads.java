package threads;

class NewThread implements Runnable {
    String name; // name of thread
    Thread t;

    NewThread(String threadName) {
        name = threadName;
        t = new Thread(this, name);
        t.start(); // Start the thread
    }

    public void run() {
        System.out.println(name + " starting.");
        try {
            for (int i = 10; i > 0; i--) {
                System.out.println(name + ": " + i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println(name + "Interrupted");
        }
        System.out.println(name + " exiting.");
    }
}

public class MultipleThreads {
    public static void main(String[] args) {
        System.out.println("Main thread starting.");

        NewThread ob1=new NewThread("One");
        NewThread ob2=new NewThread("Two");
        NewThread ob3=new NewThread("Three");

        /*try {
            // wait for other threads to end
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }*/
            try {
                ob1.t.join();
                ob2.t.join();
                ob3.t.join();
            } catch (InterruptedException e) {
                System.out.println("Main thread Interrupted");
            }
        System.out.println("Main thread exiting.");
    }
}
