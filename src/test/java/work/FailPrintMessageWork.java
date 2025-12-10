package work;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.report.DefaultWorkReport;
import com.baomibing.work.work.Work;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.work.WorkStatus;

public class FailPrintMessageWork implements Work {

    private final String message;

    public FailPrintMessageWork(String message) {
        this.message = message;
    }

    @Override
    public WorkReport execute(WorkContext workContext) {
        System.out.println(message);
        return new DefaultWorkReport().setStatus(WorkStatus.FAILED).setWorkContext(workContext);
    }
}
