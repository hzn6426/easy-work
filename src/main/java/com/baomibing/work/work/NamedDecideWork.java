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
import lombok.Getter;

public class NamedDecideWork extends NamedWork {
    @Getter
    private final Work decideWork;

    private NamedDecideWork(Work work) {
        this.decideWork = work;
    }

    public static NamedDecideWork aNewNamedDecideWork(Work work) {
        return new NamedDecideWork(work);
    }

    @Override
    public NamedDecideWork named(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Object execute(WorkContext context) {
        //ignore
        return null;
    }
}
