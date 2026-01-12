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


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.work.WorkStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * A default execution result implementation
 *
 * @author zening (316279829@qq.com)
 */
public class DefaultWorkReport extends AbstractWorkReport {

    protected WorkContext workContext;
    protected WorkStatus status;
    protected Throwable error;
    protected Object result;
    protected String workName;
    @Getter
    protected  WorkStatus stoppedStatus;



    public DefaultWorkReport() {
        this.workContext = new WorkContext();
        this.status = WorkStatus.COMPLETED;
        this.error = null;
        this.result = null;
    }


    public static DefaultWorkReport aNewWorkReport() {
        return new DefaultWorkReport();
    }



    @Override
    public WorkStatus getStatus() {
        return status;
    }

    @Override
    public Throwable getError() {
        return error;
    }

    @Override
    public WorkContext getWorkContext() {
        return workContext;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public String getWorkName() {
        return workName;
    }

    public DefaultWorkReport setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public DefaultWorkReport setStatus(WorkStatus status) {
        this.status = status;
        return this;
    }

    public DefaultWorkReport setError(Throwable error) {
        this.error = error;
        return this;
    }

    public DefaultWorkReport setResult(Object result) {
        this.result = result;
        return this;
    }

    public DefaultWorkReport setWorkName(String workName) {
        this.workName = workName;
        return this;
    }

    public DefaultWorkReport setStoppedStatus(WorkStatus status) {
        this.stoppedStatus = status;
        return this;
    }

}
