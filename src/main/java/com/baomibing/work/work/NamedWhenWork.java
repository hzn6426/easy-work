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
import com.baomibing.work.predicate.WorkReportPredicate;
import lombok.Getter;

public class NamedWhenWork extends  NamedWork {

    @Getter
    private final WorkReportPredicate  predicate;
    @Getter
    private final Work work;

    private NamedWhenWork(WorkReportPredicate predicate, Work work) {
        this.predicate = predicate;
        this.work = work;
    }

    public static NamedWhenWork aNewNamedWhenWork(WorkReportPredicate predicate, Work work) {
        return new NamedWhenWork(predicate, work);
    }

    @Override
    public NamedWhenWork named(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Object execute(WorkContext context) {
        //ignore
        return null;
    }
}
