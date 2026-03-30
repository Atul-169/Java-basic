import java.util.*;

public class P1_Library {

    static class Action {
        char type; // 'c' = checkout, 'r' = return
        int book;
        Action(char type, int book) {
            this.type = type;
            this.book = book;
        }
    }

    static class StudentTask implements Runnable {
        private final String name;
        private final List<Action> actions;

        StudentTask(String name, List<Action> actions) {
            this.name = name;
            this.actions = actions;
        }

        @Override
        public void run() {
            for (Action a : actions) {
                if (a.type == 'c') {
                    boolean success;
                    // Check AND modify must be atomic together
                    synchronized (stock) {
                        if (stock[a.book] > 0) {
                            stock[a.book]--;
                            success = true;
                        } else {
                            success = false;
                        }
                    }
                    if (success)
                        System.out.println(name + " checked out book " + a.book);
                    else
                        System.out.println(name + " FAILED to checkout book " + a.book + " (no copies left)");
                } else {
                    synchronized (stock) {
                        stock[a.book]++;
                    }
                    System.out.println(name + " returned book " + a.book);
                }
            }
        }
    }

    static int[] stock;

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt(); // number of students
        int M = sc.nextInt(); // number of book types

        stock = new int[M];

        // Initialize stock with some copies per book
        System.out.println("Initial stock:");
        for (int i = 0; i < M; i++) {
            stock[i] = sc.nextInt();
            System.out.print(stock[i] + " ");
        }
        System.out.println();

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            int k = sc.nextInt();
            List<Action> acts = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                char type = sc.next().charAt(0);
                int book = sc.nextInt();
                acts.add(new Action(type, book));
            }
            String name = "Student-" + (i + 1);
            threads[i] = new Thread(new StudentTask(name, acts));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("\nFinal stock:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < M; i++) {
            if (i > 0) sb.append(" ");
            sb.append(stock[i]);
        }
        System.out.println(sb);
    }
}

/*
===== SAMPLE INPUT =====
3 3
2 1 3
3
c 0
c 1
r 0
3
c 0
c 1
c 2
2
r 2
c 0

===== SAMPLE OUTPUT =====
Initial stock:
2 1 3
Student-1 checked out book 0
Student-2 checked out book 0
Student-3 returned book 2
Student-1 checked out book 1
Student-2 FAILED to checkout book 1 (no copies left)
Student-1 returned book 0
Student-2 checked out book 2
Student-3 checked out book 0

Final stock:
1 0 2
*/
