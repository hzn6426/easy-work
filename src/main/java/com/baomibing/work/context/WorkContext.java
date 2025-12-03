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
package com.baomibing.work.context;

import com.baomibing.work.util.Checker;
import com.baomibing.work.work.WorkReport;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A context for work flow
 *
 * @author zening (316279829@qq.com)
 */
public class WorkContext {
    @Getter
    private final Map<String, Object> contextMap = new ConcurrentHashMap<>();

    public WorkContext put(String key, Object value) {
        contextMap.put(key, value);
        return this;
    }

    public Object get(String key) {
        return contextMap.get(key);
    }

    public WorkContext remove(String key) {
        contextMap.remove(key);
        return this;
    }

    public WorkContext clear() {
        contextMap.clear();
        return this;
    }

    public void copy(WorkContext context) {
        if (Checker.BeEmpty(context.getContextMap())) {
            this.contextMap.putAll(context.contextMap);
        }
    }
}
