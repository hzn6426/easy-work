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
