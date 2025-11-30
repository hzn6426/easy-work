import com.baomibing.work.context.WorkContext;
import com.baomibing.work.enignee.WorkFlowEngineImpl;
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

public class FlowPolicyTest {

    private static void fastFailPolicy() {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();

        WorkContext workContext = new WorkContext();
        WorkFlow flow = aNewSequentialFlow(
                aNewRepeatFlow(work1).times(3).named("print foo 3 times"), exceptionPrintMessageWork,
                aNewConditionalFlow(aNewParallelFlow(work2,work3).named("print 'hello' and 'world' in parallel"))
                        .when(WorkReportPredicate.COMPLETED, work4, work5)
        ).policy(WorkExecutePolicy.FAST_FAIL);
        aNewWorkFlowEngine().run(flow, workContext);
    }

    private static void fastAllPolicy() {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();
        WorkContext workContext = new WorkContext();
        WorkFlow flow = aNewSequentialFlow(
                aNewRepeatFlow(work1).times(3).named("print foo 3 times"), exceptionPrintMessageWork,
                aNewConditionalFlow(aNewParallelFlow(work2,work3).named("print 'hello' and 'world' in parallel"))
                        .when(WorkReportPredicate.COMPLETED, work4, work5)
        ).policy(WorkExecutePolicy.FAST_ALL);
        aNewWorkFlowEngine().run(flow, workContext);
    }

    private static void fastSuccessPolicy() {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();
        WorkContext workContext = new WorkContext();
        WorkFlow flow = aNewSequentialFlow(
                aNewRepeatFlow(work1).times(3).named("print foo 3 times"), exceptionPrintMessageWork,
                aNewConditionalFlow(aNewParallelFlow(work2,work3).named("print 'hello' and 'world' in parallel"))
                        .when(WorkReportPredicate.COMPLETED, work4, work5)
        ).policy(WorkExecutePolicy.FAST_SUCCESS);
        aNewWorkFlowEngine().run(flow, workContext);
    }

    private static void fastExceptionPolicy() {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();
        WorkContext workContext = new WorkContext();
        WorkFlow flow = aNewSequentialFlow(
                aNewRepeatFlow(work1).times(3).named("print foo 3 times"), exceptionPrintMessageWork,
                aNewConditionalFlow(aNewParallelFlow(work2,work3).named("print 'hello' and 'world' in parallel"))
                        .when(WorkReportPredicate.COMPLETED, work4, work5)
        ).policy(WorkExecutePolicy.FAST_EXCEPTION);
        aNewWorkFlowEngine().run(flow, workContext);
    }

    public static void main(String[] args) {
//        fastSuccessPolicy();
        fastExceptionPolicy();
    }
}
