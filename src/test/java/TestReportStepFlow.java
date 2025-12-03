import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.work.WorkReport;
import com.baomibing.work.work.WorkStatus;
import work.DelayPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ChooseFlow.aNewChooseFlow;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class TestReportStepFlow {

    private static void testExecuteStep() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        DelayPrintMessageWork g = new DelayPrintMessageWork("g", 3);
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork i = new PrintMessageWork("i");

        PrintMessageWork j = new PrintMessageWork("j");
        PrintMessageWork k = new PrintMessageWork("k");
        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        PrintMessageWork z = new PrintMessageWork("z");

        WorkFlow flow = aNewSequentialFlow(
            a,
            aNewChooseFlow(b)
                .chooseWhen(
                    WorkReportPredicate.COMPLETED,
                    aNewSequentialFlow(
                        c,
                        aNewParallelFlow(
                            aNewSequentialFlow(
                                aNewConditionalFlow(g).when(
                                    WorkReportPredicate.COMPLETED,
                                    aNewSequentialFlow(h, i),
                                    j
                                ),
                                k
                            ),
                            aNewSequentialFlow(l, m)
                        )
                    )
                )
                .otherWise(aNewSequentialFlow(d, e, f)),
            z
        );
        aNewSequentialFlow(a).execute()
            .thenExecute(b)
            .whenExecute(
                WorkReportPredicate.COMPLETED,
                aNewSequentialFlow(c).execute()
                    .parallelExecute(
                        aNewSequentialFlow(g).execute().whenExecute(
                            WorkReportPredicate.COMPLETED,
                            aNewSequentialFlow(h).execute().thenExecute(i),
                            j
                        ).thenExecute(k)
                    ).thenExecute(l).thenExecute(m),
                aNewSequentialFlow(d).execute().thenExecute(e).thenExecute(f)
            ).thenExecute(z);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testExecuteStep1() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        DelayPrintMessageWork g = new DelayPrintMessageWork("g", 3);
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork i = new PrintMessageWork("i");

        PrintMessageWork j = new PrintMessageWork("j");
        PrintMessageWork k = new PrintMessageWork("k");
        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        PrintMessageWork z = new PrintMessageWork("z");


        aNewSequentialFlow(a).execute()
            .thenExecute(b)
            .whenExecute(
                WorkReportPredicate.COMPLETED,
                aNewSequentialFlow(c).execute()
                    .parallelExecute(
                        aNewSequentialFlow(g).execute().whenExecute(
                            WorkReportPredicate.COMPLETED,
                            aNewSequentialFlow(h).execute().thenExecute(i),
                            j
                        ).thenExecute(k)
                    ).thenExecute(l).thenExecute(m),
                aNewSequentialFlow(d).execute().thenExecute(e).thenExecute(f)
            ).thenExecute(z).context(new WorkContext());
    }

    private static void testExecuteStep2() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        DelayPrintMessageWork g = new DelayPrintMessageWork("g", 3);
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork i = new PrintMessageWork("i");

        PrintMessageWork j = new PrintMessageWork("j");
        PrintMessageWork k = new PrintMessageWork("k");
        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        PrintMessageWork z = new PrintMessageWork("z");


        aNewSequentialFlow(a).execute()
            .thenExecute(b)
            .thenExecute(bReport -> {
                if (bReport.getStatus() == WorkStatus.COMPLETED) {
                    return aNewSequentialFlow(c).execute()
                        .parallelExecute(
                            aNewSequentialFlow(g).execute().thenExecute((gReport) -> {
                                if (gReport.getStatus() == WorkStatus.COMPLETED) {
                                    return aNewSequentialFlow(h).execute().thenExecute(i);
                                }
                                return j;
                            }).thenExecute(k)
                        ).thenExecute(l).thenExecute(m);
                }
                return aNewSequentialFlow(d).execute().thenExecute(e).thenExecute(f);
            }).thenExecute(z).context(new WorkContext());
    }

    private static void testExampleExecuteStep() {
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");
        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork z = new PrintMessageWork("z");

        WorkReport workReport = aNewSequentialFlow(aNewRepeatFlow(a).times(3))
            .execute()
            .thenExecute(aNewSequentialFlow(b,c,d))
            .thenExecute(aNewParallelFlow(e,f).withAutoShutDown(true))
            .thenExecute(report -> {
                if (report.getStatus() == WorkStatus.COMPLETED) {
                    return g;
                }
                return h;
            }).thenExecute(z);
    }



    public static void main(String[] args) {
        testExampleExecuteStep();
    }
}
