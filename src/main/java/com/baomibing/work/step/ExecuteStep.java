/**
 * Copyright (c) 2025-2025, zening (316279828@qq.com).
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
 */
package com.baomibing.work.step;


import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkReport;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * A single step execution interface that allows the workflow to execute each step based on specific states
 *
 * @author zening (316279829@qq.com)
 */
public interface ExecuteStep extends WorkFlow {

    WorkReport loopExecute(Work...works);

    WorkReport loopExecute(WorkReportPredicate breakPredicate, WorkReportPredicate continuePredicate, Work...works);

    WorkReport parallelExecute(ExecutorService service, Work... works);

    WorkReport parallelExecute(Work... works);

    WorkReport repatUtilExecute(WorkReportPredicate predicate, Work work);

    WorkReport thenExecute(Work... works);

    WorkReport thenExecute(Function<WorkReport, Work> fn);

    WorkReport whenExecute(WorkReportPredicate predicate, Work work);

    WorkReport whenExecute(WorkReportPredicate predicate, Work trueWork, Work falseWork);



}
