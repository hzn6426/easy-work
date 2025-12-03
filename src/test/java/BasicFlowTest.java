import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class BasicFlowTest {

    public static void main(String[] args) {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        WorkContext workContext = new WorkContext();
        WorkFlow flow = aNewSequentialFlow(
                aNewRepeatFlow(work1).times(3),
                aNewConditionalFlow(
                    aNewParallelFlow(work2,work3)
                ).when(WorkReportPredicate.COMPLETED, work4, work5)
        );
        aNewWorkFlowEngine().run(flow, workContext);
    }
}
