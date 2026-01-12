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
import com.baomibing.work.work.AsyncWork;
import com.baomibing.work.work.Work;
import lombok.SneakyThrows;

public class LongWaitPrintMessageWork implements Work {

    private final String message;

    public LongWaitPrintMessageWork(String message) {
        this.message = message;
    }

    @SneakyThrows
    @Override
    public String execute(WorkContext workContext) {
        System.out.println("long wait work start...");
        Thread.sleep(10_000);
        System.out.println(message);
        return message;
    }
}
