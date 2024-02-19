/* Implement this class. */

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Math.min;

public class MyHost extends Host {
    private final Object lock = new Object();

    // compare tasks by priority and start time
    public class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task task1, Task task2) {
            int priorityComparison = Integer.compare(task2.getPriority(), task1.getPriority());

            if (priorityComparison != 0) {
                return priorityComparison;
            }

            return Long.compare(task1.getStart(), task2.getStart());
        }
    }
    PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<Task>(100, new TaskComparator());
    private volatile boolean isRunning = true;

    private Task runningTask; // task currently running on this host

    @Override
    public void run() {

        while (isRunning) {

            // if there are tasks in the queue
            if (!queue.isEmpty()) {

                // get the first task in the queue
                runningTask = queue.peek();
                boolean preempted = false;

                while (runningTask.getLeft() > 0 && !preempted) {

                    // check if the running task should be preempted
                    if(runningTask.isPreemptible()) {
                        if (queue.peek().getId() != runningTask.getId()) {
                            preempted = true;
                        }
                    }

                    if (!preempted) {

                        synchronized (lock) {
                            try {
                                long startTime = System.currentTimeMillis();
                                lock.wait(min(runningTask.getLeft(), 1000));
                                long endTime = System.currentTimeMillis();

                                // time waited
                                long elapsedTime = endTime - startTime;

                                // the task was preempted
                                if(elapsedTime < min(runningTask.getLeft(), 1000)) {
                                    preempted = true;
                                    runningTask.setLeft(runningTask.getLeft() - elapsedTime);
                                }
                                else {
                                    runningTask.setLeft(runningTask.getLeft() - 1000);
                                }

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                // the task is finished
                if(runningTask.getLeft() <= 0) {
                    runningTask.finish();
                    queue.remove(runningTask);
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        synchronized (lock) {
            queue.add(task);

            // notify the thread if the task has a higher priority than the running task
            if(runningTask != null)
                if(runningTask.isPreemptible() & runningTask.getPriority() < task.getPriority()) {
                    lock.notifyAll();
                }
        }
    }

    @Override
    public synchronized int getQueueSize() {
        return queue.size();
    }

    @Override
    public synchronized long getWorkLeft() {
        long leftSum = 0;

        for(Task task : queue) {
            leftSum += task.getLeft();
        }

        return leftSum;
    }

    @Override
    public synchronized void shutdown() {
        isRunning = !isRunning;
    }
}