import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiExecutor {
    private List<Thread> taskThreads;

    public MultiExecutor(List<Runnable> tasks) {
        taskThreads = new ArrayList<Thread>();
        this.taskThreads = tasks.stream().map(Thread::new).collect(Collectors.toList());
    }

    /**
     * Starts and executes all the tasks concurrently
     */
    public void executeAll() {
        this.taskThreads.forEach(Thread::start);
    }
}