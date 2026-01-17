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
package com.baomibing.work.work;

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.listener.WorkExecuteListener;
import com.baomibing.work.util.Checker;
import lombok.Getter;

/**
 * A Work class that can name, set breakpoints, and add execution listeners.
 *
 * @author zening (316279829@qq.com)
 */
public class NamedPointWork extends NamedWork {

    @Getter
    private final Work work;
    @Getter
    private WorkExecuteListener workExecuteListener;
    @Getter
    private String point;

    @Getter
    private boolean beExecuted = false;

    public static NamedPointWork aNamePointWork(Work work) {
        return new NamedPointWork(work);
    }

    public static NamedPointWork aNewNamePointWork(Work work) {
        return new NamedPointWork(work);
    }

    public NamedPointWork(Work work) {
        if (work instanceof AsyncWork) {
            AsyncWork asyncWork = (AsyncWork) work;
            asyncWork.setWorkName(name);
        }
        this.work = work;
    }

    public NamedPointWork point(String point) {
        if (Checker.BeNotEmpty(point)) {
            this.point = point;
        }
        return this;
    }

    @Override
    public NamedPointWork named(String name) {
        this.name = name;
        if (work instanceof AsyncWork) {
            AsyncWork asyncWork = (AsyncWork) work;
            asyncWork.setWorkName(name);
        }
        return this;
    }

    @Override
    public Object execute(WorkContext context) {
        this.beExecuted = true;
        return work.execute(context);
    }

    public NamedPointWork addWorkExecuteListener(WorkExecuteListener workExecuteListener) {
        this.workExecuteListener = workExecuteListener;
        if (work instanceof AsyncWork) {
            AsyncWork asyncWork = (AsyncWork) work;
            asyncWork.setWorkExecuteListener(workExecuteListener);
        }
        return this;
    }

}
