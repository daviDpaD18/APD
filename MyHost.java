/* Implement this class. */

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MyHost extends Host {
    private BlockingQueue<Task> queue = new PriorityBlockingQueue<Task>(20,
     (t1, t2) -> 
     {
        if (t1.getPriority() == t2.getPriority())
            return t1.getId() - t2.getId();
        else 
            return t2.getPriority() - t1.getPriority();
    });

    private boolean ok = true;

    private Set<Task> runningTasks = new HashSet<Task>();

    @Override
    public void run() {
        while (ok) {
            if (!queue.isEmpty()) {
                Task task = null;
                try {
                    task = queue.take();
                } catch (InterruptedException e) {
                    
                    e.printStackTrace();
                }
                runningTasks.add(task);

                for (Task t : queue) {
                    if (!t.isPreemptible() && t.getLeft() > 0 && t.getLeft() < t.getDuration()) {
                        try {
                            queue.take();
                        } catch (InterruptedException e) {
                           
                            e.printStackTrace();
                        }
                        try {
                            queue.put(task);
                        } catch (InterruptedException e) {
                            
                            e.printStackTrace();
                        }
                        task = t;
                        runningTasks.remove(task);
                        break;
                    }
                }

                if (queue.peek() != null && task.getPriority() < queue.peek().getPriority() && task.getLeft() > 0 && task.isPreemptible()) {
                    Task aux = null;
                    try {
                        aux = queue.take();
                    } catch (InterruptedException e) {
                       
                        e.printStackTrace();
                    }
                    try {
                        queue.put(task);
                    } catch (InterruptedException e) {
                        
                        e.printStackTrace();
                    }
                    task = aux;
                    runningTasks.remove(aux);
                }

                if (task.getLeft() > 0) {
                    try {
                        Thread.sleep(1000);
                        task.setLeft(task.getLeft() - (long)1000);

                        if (task.getLeft() > 0) {
                            queue.put(task);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } 
                
                if (task.getLeft() <= 0) {
                    task.finish();
                    System.out.println("Task " + task.getId() + " finished at " + task.getFinish());
                    runningTasks.remove(task);
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        try {
            this.queue.put(task);
        } catch (InterruptedException e) {
        
            e.printStackTrace();
        }
    }


    @Override
    public int getQueueSize() {
        return (queue.size() + runningTasks.size());    
    }

    @Override
    public long getWorkLeft() {
        Long workLeft = Long.valueOf(0);
        for(Task task : queue) {
            workLeft += task.getLeft();
        }

        for(Task task : runningTasks) {
            workLeft += task.getLeft();
        }

        return workLeft;
    }

    @Override
    public void shutdown() {
        interrupt();
        ok = false;
    }
}
