/* Implement this class. */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {
    AtomicInteger counter = new AtomicInteger();

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        if(this.algorithm == SchedulingAlgorithm.ROUND_ROBIN) { 
            (this.hosts.get(counter.getAndIncrement() % this.hosts.size())).addTask(task);
        }
            
        if (this.algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            Host host = hosts.get(0);

            for (Host h : hosts) {
                if (h.getQueueSize() < host.getQueueSize()) {
                    host = h;
                }
            }
            host.addTask(task);
        }
        if (this.algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            switch (task.getType()) {
                case SHORT:
                    hosts.get(0).addTask(task);
                    break;
                case MEDIUM:
                    hosts.get(1).addTask(task);
                    break;
                case LONG:
                    hosts.get(2).addTask(task);
                    break;
            }

           
            
        }
        if (this.algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
             Host host = hosts.get(0);

            for (Host h : hosts) {
                if (h.getWorkLeft() < host.getWorkLeft()) {
                    host = h;
                }
            }
            host.addTask(task);
        }
    }
}