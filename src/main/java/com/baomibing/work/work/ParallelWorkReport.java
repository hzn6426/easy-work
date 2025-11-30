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
package com.baomibing.work.work;

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * A default parallel execution result implementation
 *
 * @author zening (316279829@qq.com)
 */
public class ParallelWorkReport extends AbstractWorkReport {

    @Getter
    private final List<WorkReport> reports;
    private WorkFlow work;
    protected WorkContext workContext;
    protected WorkStatus status;
    protected Throwable error;
    protected Object result;

    @Setter
    private WorkFlow previous;



    public ParallelWorkReport() {
        this(new ArrayList<>());
    }



    public ParallelWorkReport(List<WorkReport> reports) {
        this.reports = reports;
    }



    public void addAll(List<WorkReport> workReports) {
        reports.addAll(workReports);
    }


    @Override
    public WorkStatus getStatus() {
        for (WorkReport report : reports) {
            if (report.getStatus().equals(WorkStatus.FAILED)) {
                return WorkStatus.FAILED;
            }
        }
        return WorkStatus.COMPLETED;
    }


    @Override
    public Throwable getError() {
        for (WorkReport report : reports) {
            Throwable error = report.getError();
            if (error != null) {
                return error;
            }
        }
        return null;
    }


    @Override
    public WorkContext getWorkContext() {
        WorkContext workContext = new WorkContext();
        for (WorkReport report : reports) {
            WorkContext partialContext = report.getWorkContext();
            for (Map.Entry<String, Object> entry : partialContext.getContextMap().entrySet()) {
                workContext.put(entry.getKey(), entry.getValue());
            }
        }
        return workContext;
    }

    @Override
    public List<Object> getResult() {
        return getReports().stream().map(WorkReport::getResult).collect(Collectors.toList());
    }


    public ParallelWorkReport setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public ParallelWorkReport setStatus(WorkStatus status) {
        this.status = status;
        return this;
    }

    public ParallelWorkReport setError(Throwable error) {
        this.error = error;
        return this;
    }
}
