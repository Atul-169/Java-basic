import java.util.*;
import java.util.concurrent.locks.*;

public class P4_Voting {

    static int[] votes;       // votes[i] = vote count for candidate i
    static int M;             // number of candidates

    // ReadWriteLock: many readers OR one writer at a time
    static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    static final Lock readLock  = rwLock.readLock();
    static final Lock writeLock = rwLock.writeLock();

    // ── VOTER (Writer) ───────────────────────────────────────────────────────
    // Each voter has a sequence of votes; only the last one counts.
    // Changing vote = cancel previous + cast new (must be atomic → write lock).
    static class VoterTask implements Runnable {
        private final String name;
        private final List<Integer> voteSequence;

        VoterTask(String name, List<Integer> voteSequence) {
            this.name = name;
            this.voteSequence = voteSequence;
        }

        @Override
        public void run() {
            int currentVote = -1; // -1 means no vote cast yet
            for (int candidate : voteSequence) {
                writeLock.lock();
                try {
                    if (currentVote != -1) {
                        votes[currentVote]--;   // cancel previous vote
                    }
                    votes[candidate]++;         // cast new vote
                    currentVote = candidate;
                    System.out.println(name + " voted for Candidate-" + candidate);
                } finally {
                    writeLock.unlock();
                }
                try { Thread.sleep(new Random().nextInt(80) + 20); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    // ── MONITOR (Reader) ─────────────────────────────────────────────────────
    // Periodically reads and prints standings without blocking voters for long.
    static class MonitorTask implements Runnable {
        private final int intervals;
        private final int sleepMs;
        volatile boolean stop = false;

        MonitorTask(int intervals, int sleepMs) {
            this.intervals = intervals;
            this.sleepMs   = sleepMs;
        }

        @Override
        public void run() {
            for (int i = 0; i < intervals && !stop; i++) {
                try { Thread.sleep(sleepMs); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

                readLock.lock();    // multiple monitors can read simultaneously
                try {
                    System.out.print("[Monitor] Current standings: ");
                    for (int j = 0; j < M; j++) {
                        System.out.print("C" + j + ":" + votes[j] + " ");
                    }
                    System.out.println();
                } finally {
                    readLock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt(); // voters
        M     = sc.nextInt(); // candidates

        votes = new int[M];

        Thread[] voterThreads = new Thread[N];

        for (int i = 0; i < N; i++) {
            int k = sc.nextInt();
            List<Integer> seq = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                seq.add(sc.nextInt()); // candidate index
            }
            voterThreads[i] = new Thread(new VoterTask("Voter-" + (i + 1), seq));
        }

        // Monitor checks standings every 150ms, up to 10 times
        MonitorTask monitorTask = new MonitorTask(10, 150);
        Thread monitorThread = new Thread(monitorTask);

        monitorThread.start();
        for (Thread t : voterThreads) t.start();
        for (Thread t : voterThreads) t.join();

        monitorTask.stop = true;
        monitorThread.join();

        System.out.println("\n===== Final Vote Count =====");
        int winner = 0;
        for (int i = 0; i < M; i++) {
            System.out.println("Candidate-" + i + ": " + votes[i] + " votes");
            if (votes[i] > votes[winner]) winner = i;
        }
        System.out.println("Winner: Candidate-" + winner + " with " + votes[winner] + " votes!");
    }
}

/*
===== SAMPLE INPUT =====
4 3
3
0 1 0
3
1 2 1
2
2 0
3
0 1 2

===== SAMPLE OUTPUT (intermediate lines vary) =====
Voter-1 voted for Candidate-0
Voter-2 voted for Candidate-1
Voter-3 voted for Candidate-2
[Monitor] Current standings: C0:1 C1:1 C2:1
Voter-1 voted for Candidate-1
Voter-4 voted for Candidate-0
...
[Monitor] Current standings: C0:1 C1:2 C2:1

===== Final Vote Count =====
Candidate-0: 1 votes
Candidate-1: 2 votes
Candidate-2: 1 votes
Winner: Candidate-1 with 2 votes!
*/
