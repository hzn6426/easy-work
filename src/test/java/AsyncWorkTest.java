/*
 * Copyright (c) 2025-2026, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.SequentialFlow;
import com.baomibing.work.listener.WorkExecuteListener;
import com.baomibing.work.report.DefaultWorkReport;
import work.LongWaitPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.AsyncWork.aNewAsyncWork;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class AsyncWorkTest {

    private static void testAsyncWork() {
        PrintMessageWork a = new PrintMessageWork("a");
        LongWaitPrintMessageWork b = new LongWaitPrintMessageWork("execute in 10 seconds,that a long work...");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        SequentialFlow flow = aNewSequentialFlow(a,  aNewAsyncWork(b).withAutoShutDown(true),c, d);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testAsyncWorkListener() {
        PrintMessageWork a = new PrintMessageWork("a");
        LongWaitPrintMessageWork b = new LongWaitPrintMessageWork("execute in 10 seconds,that a long work...");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");
        WorkExecuteListener listener = new WorkExecuteListener() {

            @Override
            public void onWorkExecute(DefaultWorkReport workReport, WorkContext workContext, Exception exception) {
                System.out.println("execute finished");
            }
        };
        SequentialFlow flow = aNewSequentialFlow(a,  aNamePointWork(aNewAsyncWork(b).withAutoShutDown(true)).addWorkExecuteListener(listener),c, d);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    public static void main(String[] args) {
//        testAsyncWork();
        testAsyncWorkListener();
    }
}
