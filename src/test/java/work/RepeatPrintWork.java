package work;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.report.DefaultWorkReport;
import com.baomibing.work.work.Work;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.work.WorkStatus;

import java.util.concurrent.atomic.AtomicInteger;

import static com.baomibing.work.report.DefaultWorkReport.aNewWorkReport;

public class RepeatPrintWork implements Work {

    private final String message;
    private final AtomicInteger counter = new AtomicInteger();

    public RepeatPrintWork(String message) {
        this.message = message;
    }

    @Override
    public WorkReport execute(WorkContext workContext) {
        int count = counter.incrementAndGet();
        System.out.println("the " + count + ":" + message);
        if (count < 10) {
            return aNewWorkReport();
        }
        return new DefaultWorkReport().setStatus(WorkStatus.FAILED).setWorkContext(workContext);
    }
}
