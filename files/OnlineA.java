import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KitchenInventory {

    static class Action {
        char type;
        int item;
        Action(char type, int item) {
            this.type = type;
            this.item = item;
        }
    }

    static class FriendTask implements Runnable {
        private final String name;
        private final List<Action> actions;

        FriendTask(String name, List<Action> actions) {
            this.name = name;
            this.actions = actions;
        }

        @Override
        public void run() {
            for (Action a : actions) {
                if (a.type == 'a') {
                    synchronized (stock) {
                        stock[a.item]++;
                    }
                    System.out.println(name + " added item " + a.item);
                } else {
                    synchronized (stock) {
                        stock[a.item]--;
                    }
                    System.out.println(name + " removed item " + a.item);
                }
            }
        }
    }

    static int[] stock;

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        int M = sc.nextInt();

        stock = new int[M];

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            int k = sc.nextInt();
            List<Action> acts = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                char type = sc.next().charAt(0);
                int item = sc.nextInt();
                acts.add(new Action(type, item));
            }
            String name = "Friend-" + (i + 1);
            threads[i] = new Thread(new FriendTask(name, acts));
        }

        // Start all threads
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            t.join();
        }

        // Print final inventory
        System.out.println("\nFinal inventory status:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < M; i++) {
            if (i > 0) sb.append(" ");
            sb.append(stock[i]);
        }
        System.out.println(sb);
    }
}
