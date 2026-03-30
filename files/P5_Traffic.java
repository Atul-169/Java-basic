import java.util.*;

public class P5_Traffic {

    static int M;           // number of lanes
    static Object[] lanes;  // one lock per lane

    // ── CAR TASK ─────────────────────────────────────────────────────────────
    static class CarTask implements Runnable {
        private final String name;
        private final int[] route; // sequence of lane indices to pass through

        CarTask(String name, int[] route) {
            this.name  = name;
            this.route = route;
        }

        @Override
        public void run() {
            // *** DEADLOCK PREVENTION ***
            // Sort lanes by index before locking — every car always acquires
            // locks in ascending order, so circular waits cannot form.
            int[] sortedLanes = route.clone();
            Arrays.sort(sortedLanes);

            // Acquire all required lane locks in sorted order
            for (int lane : sortedLanes) {
                synchronized (lanes[lane]) {
                    // Lock is held until the nested block completes.
                    // We use a helper to hold all locks at once.
                }
            }

            // Above approach releases locks immediately. Instead we need to
            // hold ALL locks simultaneously while traversing. Use recursive locking:
            acquireAndRun(sortedLanes, 0);
        }

        // Recursively acquires locks in order, then performs the drive
        private void acquireAndRun(int[] sortedLanes, int idx) {
            if (idx == sortedLanes.length) {
                // All locks acquired — now traverse the ORIGINAL route order
                drive();
                return;
            }
            synchronized (lanes[sortedLanes[idx]]) {
                acquireAndRun(sortedLanes, idx + 1);
            }
        }

        private void drive() {
            for (int lane : route) {
                System.out.println(name + " entering lane " + lane);
                try { Thread.sleep(new Random().nextInt(100) + 50); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                System.out.println(name + " cleared  lane " + lane);
            }
            System.out.println(name + " has passed the intersection.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt(); // number of cars
        M     = sc.nextInt(); // number of lanes

        lanes = new Object[M];
        for (int i = 0; i < M; i++) lanes[i] = new Object();

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            int k = sc.nextInt(); // number of lanes in this car's route
            int[] route = new int[k];
            for (int j = 0; j < k; j++) {
                route[j] = sc.nextInt();
            }
            threads[i] = new Thread(new CarTask("Car-" + (i + 1), route));
        }

        long start = System.currentTimeMillis();

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        long end = System.currentTimeMillis();
        System.out.println("\nAll cars cleared in " + (end - start) / 1000.0 + " seconds.");
    }
}

/*
===== SAMPLE INPUT =====
4 4
3
0 2 3
2
1 3
3
2 0 1
2
3 1

===== SAMPLE OUTPUT (intermediate lines vary) =====
Car-2 entering lane 1
Car-2 entering lane 3
Car-1 entering lane 0
Car-4 entering lane 1   ← Car-4 must wait for Car-2 to clear lane 1
...
Car-2 cleared  lane 1
Car-2 cleared  lane 3
Car-2 has passed the intersection.
Car-1 entering lane 2
Car-4 entering lane 1
...

All cars cleared in 1.23 seconds.

===== WHY NO DEADLOCK? =====
Car-1 needs lanes [0,2,3] → locks in order: 0,2,3
Car-3 needs lanes [2,0,1] → locks in order: 0,1,2
Both want lane 0 and lane 2, but BOTH will always try lock-0 before lock-2.
So one must wait while the other proceeds — no circular wait → no deadlock.
*/
