package advanced;

class A {
    synchronized void foo(B b) {
        synchronized (this) {
            
        }
        String name = Thread.currentThread().getName();
        System.out.println(name + " entered advanced.A.foo");
        /*
         * try {
         * Thread.sleep(1000);
         * } catch (InterruptedException e) {
         * e.printStackTrace();
         * }
         */
        System.out.println(name + " trying to call advanced.B.last");
        b.last();
    }

    synchronized void last() {
        System.out.println("Inside advanced.A.last");
    }

}

class B {
    synchronized void bar(A a) {
        String name = Thread.currentThread().getName();
        System.out.println(name + " entered advanced.B.bar");
        /*
         * try {
         * Thread.sleep(1000);
         * } catch (InterruptedException e) {
         * e.printStackTrace();
         * }
         */
        System.out.println(name + " trying to call advanced.A.last");
        a.last();
    }

    synchronized void last() {
        System.out.println("Inside advanced.B.last");
    }
}

public class Deadlock implements Runnable {
    A a;
    B b;
    Thread t;

    Deadlock() {
        a = new A();
        b = new B();
        Thread.currentThread().setName("advanced.Main Thread");
        t = new Thread(this, "Racing Thread");
    }

    void deadlockStart() {
        t.start();
        a.foo(b);
        System.out.println("Back in advanced.Main Thread");
    }

    public void run() {
        b.bar(a);
        System.out.println("Back in Racing Thread");
    }

    public static void main(String[] args) {
        Deadlock deadlock = new Deadlock();
        deadlock.deadlockStart();
    }

}
package advanced;

/*
 * DEADLOCK-FREE VERSION
 * 
 * Key idea:
 * Always acquire locks in the SAME ORDER (A → B)
 */

class A {

    // Removed method-level synchronized
    void foo(B b) {
        String name = Thread.currentThread().getName();

        // Step 1: Lock A first
        synchronized (this) {
            System.out.println(name + " locked A");

            // Small delay (just to simulate real scenario)
            try { Thread.sleep(100); } catch (Exception e) {}

            // Step 2: Then lock B (same order everywhere!)
            synchronized (b) {
                System.out.println(name + " locked B");

                System.out.println(name + " executing A.foo()");
                b.last(); // safe call (already holding B lock)
            }
        }
    }

    synchronized void last() {
        // This method is safe because lock is already acquired
        System.out.println("Inside A.last()");
    }
}

class B {

    // Removed method-level synchronized
    void bar(A a) {
        String name = Thread.currentThread().getName();

        /*
         * IMPORTANT:
         * We follow SAME LOCK ORDER as A.foo()
         * First lock A, then lock B
         */

        synchronized (a) {
            System.out.println(name + " locked A");

            try { Thread.sleep(100); } catch (Exception e) {}

            synchronized (this) {
                System.out.println(name + " locked B");

                System.out.println(name + " executing B.bar()");
                a.last(); // safe call
            }
        }
    }

    synchronized void last() {
        System.out.println("Inside B.last()");
    }
}

 class DeadlockFixed implements Runnable {

    A a = new A();
    B b = new B();

    Thread t;

    DeadlockFixed() {
        Thread.currentThread().setName("Main Thread");

        // Create second thread
        t = new Thread(this, "Racing Thread");
    }

    void startTest() {
        t.start();        // Start Racing Thread
        a.foo(b);         // Main Thread execution
        System.out.println("Back in Main Thread");
    }

    @Override
    public void run() {
        b.bar(a);         // Racing Thread execution
        System.out.println("Back in Racing Thread");
    }

    public static void main(String[] args) {
        new DeadlockFixed().startTest();
    }
}

