/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {
    int nextHost = -1;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public synchronized void addTask(Task task) {
        int task_id = task.getId();
        int task_start = task.getStart();
        long task_duration = task.getDuration();
        TaskType task_type = task.getType();
        int task_priority = task.getPriority();
        boolean task_isPreemptible = task.isPreemptible();
        long task_left = task.getLeft();
        double task_finish = task.getFinish();

        switch (algorithm) {
            case ROUND_ROBIN:
                nextHost = (nextHost + 1) % hosts.size();
                hosts.get(nextHost).addTask(task);
                break;

            case SHORTEST_QUEUE:
                int minQueueSize = Integer.MAX_VALUE;

                for(int i = 0; i < hosts.size(); i++) {
                    int size = hosts.get(i).getQueueSize();
                    if(size < minQueueSize) {
                        minQueueSize = size;
                        nextHost = i;
                    }
                    else if(size == minQueueSize) {
                        if(hosts.get(i).getId() < hosts.get(nextHost).getId()) {
                            nextHost = i;
                        }
                    }
                }

                hosts.get(nextHost).addTask(task);
                break;

            case SIZE_INTERVAL_TASK_ASSIGNMENT:

                switch (task_type){
                    case SHORT: {
                        hosts.get(0).addTask(task);
                        break;
                    }
                    case MEDIUM: {
                        hosts.get(1).addTask(task);
                        break;
                    }
                    case LONG: {
                        hosts.get(2).addTask(task);
                        break;
                    }
                }
                break;

            case LEAST_WORK_LEFT:
                long minWorkLeft = Integer.MAX_VALUE;

                for(int i = 0; i < hosts.size(); i++) {
                    long workLeft = hosts.get(i).getWorkLeft();

                    if(workLeft < minWorkLeft) {
                        minWorkLeft = workLeft;
                        nextHost = i;
                    }
                    else if(workLeft == minWorkLeft) {
                        if(hosts.get(i).getId() < hosts.get(nextHost).getId()) {
                            nextHost = i;
                        }
                    }
                }

                hosts.get(nextHost).addTask(task);
                break;
        }
    }
}
