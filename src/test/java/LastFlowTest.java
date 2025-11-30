import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.work.WorkExecutePolicy;
import work.ExceptionPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class LastFlowTest {

    public static void main(String[] args) {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");
        PrintMessageWork work5 = new PrintMessageWork("work5");
        PrintMessageWork finalWork = new PrintMessageWork("final");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();
        WorkContext workContext = new WorkContext();
        WorkFlow flow = aNewSequentialFlow(
            exceptionPrintMessageWork,
            aNewRepeatFlow(work1).times(3),
            aNewConditionalFlow(
                aNewParallelFlow(work2,work3)
            ).when(WorkReportPredicate.COMPLETED, work4, work5)
        ).policy(WorkExecutePolicy.FAST_EXCEPTION)
         .lastly(finalWork);
        aNewWorkFlowEngine().run(flow, workContext);
    }
}
