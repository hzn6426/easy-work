package work;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.Work;

public class ParallelPrintMessageWork implements Work {

    private final String message;
    private final Integer delay;

    public ParallelPrintMessageWork(String message) {
        this.message = message;
        delay = null;
    }

    public ParallelPrintMessageWork(String message, int delay) {
        this.message = message;
        this.delay = delay;
    }

    @Override
    public String execute(WorkContext workContext) {
        System.out.println("executing " + message);
        if (Checker.BeNotNull(delay)) {
            try {
                Thread.sleep(delay * 1000L);
                return message;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return message;

    }
}
