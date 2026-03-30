import java.util.*;

public class P2_Bank {

    static class Transaction {
        char type;  // 'd' = deposit, 'w' = withdraw, 't' = transfer
        int accA;
        int accB;   // used only for transfer
        int amount;
        Transaction(char type, int accA, int accB, int amount) {
            this.type = type;
            this.accA = accA;
            this.accB = accB;
            this.amount = amount;
        }
    }

    // Each account has its own lock object
    static int[] balance;
    static Object[] locks;

    static class PersonTask implements Runnable {
        private final String name;
        private final List<Transaction> transactions;

        PersonTask(String name, List<Transaction> transactions) {
            this.name = name;
            this.transactions = transactions;
        }

        @Override
        public void run() {
            for (Transaction tx : transactions) {
                if (tx.type == 'd') {
                    synchronized (locks[tx.accA]) {
                        balance[tx.accA] += tx.amount;
                    }
                    System.out.println(name + " deposited " + tx.amount + " into account " + tx.accA);

                } else if (tx.type == 'w') {
                    boolean success;
                    synchronized (locks[tx.accA]) {
                        if (balance[tx.accA] >= tx.amount) {
                            balance[tx.accA] -= tx.amount;
                            success = true;
                        } else {
                            success = false;
                        }
                    }
                    if (success)
                        System.out.println(name + " withdrew " + tx.amount + " from account " + tx.accA);
                    else
                        System.out.println(name + " FAILED withdrawal of " + tx.amount + " from account " + tx.accA + " (insufficient funds)");

                } else { // transfer
                    // *** DEADLOCK AVOIDANCE: always lock lower index account first ***
                    int first  = Math.min(tx.accA, tx.accB);
                    int second = Math.max(tx.accA, tx.accB);
                    boolean success;
                    synchronized (locks[first]) {
                        synchronized (locks[second]) {
                            if (balance[tx.accA] >= tx.amount) {
                                balance[tx.accA] -= tx.amount;
                                balance[tx.accB] += tx.amount;
                                success = true;
                            } else {
                                success = false;
                            }
                        }
                    }
                    if (success)
                        System.out.println(name + " transferred " + tx.amount
                                + " from account " + tx.accA + " to account " + tx.accB);
                    else
                        System.out.println(name + " FAILED transfer of " + tx.amount
                                + " from account " + tx.accA + " (insufficient funds)");
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt(); // people
        int M = sc.nextInt(); // accounts

        balance = new int[M];
        locks   = new Object[M];
        for (int i = 0; i < M; i++) {
            balance[i] = sc.nextInt(); // initial balance
            locks[i]   = new Object();
        }

        System.out.print("Initial balances: ");
        for (int b : balance) System.out.print(b + " ");
        System.out.println();

        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++) {
            int k = sc.nextInt();
            List<Transaction> txList = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                char type = sc.next().charAt(0);
                if (type == 't') {
                    int a = sc.nextInt(), b = sc.nextInt(), amt = sc.nextInt();
                    txList.add(new Transaction('t', a, b, amt));
                } else {
                    int a = sc.nextInt(), amt = sc.nextInt();
                    txList.add(new Transaction(type, a, -1, amt));
                }
            }
            threads[i] = new Thread(new PersonTask("Person-" + (i + 1), txList));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("\nFinal balances:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < M; i++) {
            if (i > 0) sb.append(" ");
            sb.append(balance[i]);
        }
        System.out.println(sb);
    }
}

/*
===== SAMPLE INPUT =====
3 3
500 300 200
3
d 0 100
w 1 50
t 0 2 200
3
w 2 100
d 1 200
t 1 0 100
2
d 2 50
w 0 999

===== SAMPLE OUTPUT =====
Initial balances: 500 300 200
Person-1 deposited 100 into account 0
Person-2 withdrew 100 from account 2
Person-3 deposited 50 into account 2
Person-1 withdrew 50 from account 1
Person-2 deposited 200 into account 1
Person-3 FAILED withdrawal of 999 from account 0 (insufficient funds)
Person-1 transferred 200 from account 0 to account 2
Person-2 transferred 100 from account 1 to account 0

Final balances:
500 450 200
(exact order of intermediate lines may vary)
*/
