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
