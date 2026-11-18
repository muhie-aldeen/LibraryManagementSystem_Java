package library;

import java.util.PriorityQueue;
import java.util.Comparator;

public class WaitingQueue {

    PriorityQueue<User> queue;

    public WaitingQueue() {
        queue = new PriorityQueue<>(new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {

                // 1) المتخرجين لهم أولوية أعلى
                if (u1.isGraduate && !u2.isGraduate)
                    return -1; // u1 أولوية أعلى
                if (!u1.isGraduate && u2.isGraduate)
                    return 1;  // u2 أولوية أعلى

                // 2) إذا الاتنين نفس الشي → الأقل كتب أولوية أعلى
                return Integer.compare(u1.borrowedCount, u2.borrowedCount);
            }
        });
    }

    public void addUser(User user) {
        queue.add(user);
    }

    public User getNextUser() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
