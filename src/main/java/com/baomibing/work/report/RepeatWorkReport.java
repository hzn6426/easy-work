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
package com.baomibing.work.report;

import java.util.List;
/**
 * A default repeat flow execution result implementation
 *
 * @author zening (316279829@qq.com)
 */
public class RepeatWorkReport extends MultipleWorkReport {

    public RepeatWorkReport() {}

    public static RepeatWorkReport aNewRepeatWorkReport() {
        return new RepeatWorkReport();
    }



    public static RepeatWorkReport aNewRepeatWorkReport(MultipleWorkReport workReport) {
        RepeatWorkReport report =  new RepeatWorkReport();
        report.copy(workReport);
        return report;
    }

    public RepeatWorkReport addAllReports(List<WorkReport> workReports) {
        getReports().addAll(workReports);
        return this;
    }

    public RepeatWorkReport addReport(WorkReport workReport) {
        getReports().add(workReport);
        return this;
    }

    public RepeatWorkReport setWorkName(String workName) {
        this.workName = workName;
        return this;
    }
}
