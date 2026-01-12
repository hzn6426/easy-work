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
package com.baomibing.work.report;

import java.util.List;
/**
 * A default choose flow execution result implementation
 *
 * @author zening (316279829@qq.com)
 */
public class ChooseWorkReport extends MultipleWorkReport {
    public ChooseWorkReport() {

    }

    public static ChooseWorkReport aNewChooseWorkReport() {
        return new ChooseWorkReport();
    }


    public static ChooseWorkReport aNewChooseWorkReport(MultipleWorkReport workReport) {
        ChooseWorkReport report =  new ChooseWorkReport();
        report.copy(workReport);
        return report;
    }

    public ChooseWorkReport addAllReports(List<WorkReport> workReports) {
        getReports().addAll(workReports);
        return this;
    }

    public ChooseWorkReport addReport(WorkReport workReport) {
        getReports().add(workReport);
        return this;
    }

    public ChooseWorkReport setWorkName(String workName) {
        this.workName = workName;
        return this;
    }
}
