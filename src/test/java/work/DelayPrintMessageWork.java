package work;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.work.DefaultWorkReport;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkReport;
import com.baomibing.work.work.WorkStatus;

public class DelayPrintMessageWork implements Work {

    private final String message;
    private final int delaySeconds;
    public DelayPrintMessageWork(String message, int delaySeconds) {
        this.message = message;
        this.delaySeconds = delaySeconds;
    }

    @Override
    public WorkReport execute(WorkContext workContext) {
        try {
            Thread.sleep(delaySeconds * 1000L);
            System.out.println(message);
            return new DefaultWorkReport().setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
