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
package com.baomibing.work.step;


import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.work.Work;

/**
 * A follow-up execution interface that always executes the follow-up interface, unless the {@link com.baomibing.work.work.WorkExecutePolicy} is FAST_EXCEPTION
 *
 *  @author zening (316279829@qq.com)
 */
public interface ThenStep extends WorkFlow {

//    WorkFlow then(Function<WorkReport, Work> fun);

    WorkFlow then(Work work);

}
