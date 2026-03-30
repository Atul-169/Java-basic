import java.util.*;

public class P3_Restaurant {

    // Shared order queue
    static final Queue<String> orderQueue = new LinkedList<>();
    static boolean allOrdersPlaced = false; // signal chefs to stop when done

    // ── WAITER (Producer) ────────────────────────────────────────────────────
    static class WaiterTask implements Runnable {
        private final String name;
        private final List<String> orders; // each order: "ItemName TableNo"

        WaiterTask(String name, List<String> orders) {
            this.name = name;
            this.orders = orders;
        }

        @Override
        public void run() {
            for (String order : orders) {
                synchronized (orderQueue) {
                    orderQueue.add(order);
                    System.out.println(name + " placed order: [" + order + "]"
                            + "  (queue size: " + orderQueue.size() + ")");
                    orderQueue.notifyAll(); // wake up waiting chefs
                }
                try { Thread.sleep(new Random().nextInt(100) + 50); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    // ── CHEF (Consumer) ──────────────────────────────────────────────────────
    static class ChefTask implements Runnable {
        private final String name;
        private int cookedCount = 0;

        ChefTask(String name) { this.name = name; }

        @Override
        public void run() {
            while (true) {
                String order;
                synchronized (orderQueue) {
                    // Wait while queue is empty AND waiters are still placing orders
                    while (orderQueue.isEmpty() && !allOrdersPlaced) {
                        try { orderQueue.wait(); }
                        catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                    }
                    // If queue empty AND all orders placed → chef is done
                    if (orderQueue.isEmpty()) return;
                    order = orderQueue.poll();
                }
                // Cook outside the lock so other chefs can grab orders simultaneously
                System.out.println(name + " is cooking: [" + order + "]");
                try { Thread.sleep(new Random().nextInt(300) + 100); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                System.out.println(name + " finished: [" + order + "]");
                cookedCount++;
            }
        }

        int getCookedCount() { return cookedCount; }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int W = sc.nextInt(); // number of waiters
        int C = sc.nextInt(); // number of chefs

        Thread[] waiterThreads = new Thread[W];
        Thread[] chefThreads   = new Thread[C];
        ChefTask[] chefTasks   = new ChefTask[C];

        // Read waiter orders
        for (int i = 0; i < W; i++) {
            int k = sc.nextInt();
            List<String> orders = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                String item  = sc.next();
                int table    = sc.nextInt();
                orders.add(item + " Table-" + table);
            }
            waiterThreads[i] = new Thread(new WaiterTask("Waiter-" + (i + 1), orders));
        }

        // Create chef threads
        for (int i = 0; i < C; i++) {
            chefTasks[i]   = new ChefTask("Chef-" + (i + 1));
            chefThreads[i] = new Thread(chefTasks[i]);
        }

        // Start chefs first so they are ready to consume
        for (Thread t : chefThreads) t.start();
        for (Thread t : waiterThreads) t.start();

        // Wait for all waiters to finish placing orders
        for (Thread t : waiterThreads) t.join();

        // Signal chefs that no more orders will come
        synchronized (orderQueue) {
            allOrdersPlaced = true;
            orderQueue.notifyAll();
        }

        // Wait for all chefs to finish cooking
        for (Thread t : chefThreads) t.join();

        System.out.println("\n===== Kitchen Summary =====");
        int total = 0;
        for (int i = 0; i < C; i++) {
            System.out.println("Chef-" + (i + 1) + " cooked " + chefTasks[i].getCookedCount() + " orders.");
            total += chefTasks[i].getCookedCount();
        }
        System.out.println("Total orders cooked: " + total);
    }
}

/*
===== SAMPLE INPUT =====
2 3
3
Pasta 1
Pizza 2
Burger 3
3
Sushi 4
Salad 5
Soup 6

===== SAMPLE OUTPUT (order of intermediate lines varies) =====
Waiter-1 placed order: [Pasta Table-1]  (queue size: 1)
Chef-1 is cooking: [Pasta Table-1]
Waiter-1 placed order: [Pizza Table-2]  (queue size: 1)
Chef-2 is cooking: [Pizza Table-2]
Waiter-2 placed order: [Sushi Table-4]  (queue size: 1)
Chef-3 is cooking: [Sushi Table-4]
...
Chef-2 finished: [Pizza Table-2]
Chef-1 finished: [Pasta Table-1]
...

===== Kitchen Summary =====
Chef-1 cooked 2 orders.
Chef-2 cooked 2 orders.
Chef-3 cooked 2 orders.
Total orders cooked: 6
*/
