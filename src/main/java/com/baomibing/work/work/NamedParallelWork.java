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
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class NamedParallelWork extends NamedWork {

    @Getter
    private List<Work> supplierWorks  = new ArrayList<>();

    private NamedParallelWork(List<Work> works) {
        this.supplierWorks = works;
    }

    public static NamedParallelWork aNewParallelWork(List<Work> works) {
        return new NamedParallelWork(works);
    }

    @Override
    public NamedParallelWork named(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Object execute(WorkContext context) {
        //ignore
        return null;
    }

    public void addWork(Work work) {
        supplierWorks.add(work);
    }
}
