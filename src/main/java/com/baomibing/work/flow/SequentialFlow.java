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
package com.baomibing.work.flow;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomibing.work.context.WorkContext;
import com.baomibing.work.exception.ExceptionEnum;
import com.baomibing.work.report.MultipleWorkReport;
import com.baomibing.work.report.SequentialWorkReport;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.EndWork;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkExecutePolicy;
import com.baomibing.work.work.WorkStatus;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.List;

import static com.baomibing.work.report.SequentialWorkReport.aNewSequentialWorkReport;
import static com.baomibing.work.util.WorkUtil.assertNotEmpty;
import static com.baomibing.work.util.WorkUtil.assertNotNull;

/**
 * A sequential flow executes a set of work units in sequence.
 *
 * @author zening (316279829@qq.com)
 */
public class SequentialFlow extends AbstractWorkFlow {

    private SequentialFlow(List<Work> works) {
        works.forEach(work -> workList.add(wrapNamedPointWork(work)));
        this.workList.add(new EndWork());
    }

    public static SequentialFlow deserialize(JSONObject flow) {
        assertNotNull(flow, ExceptionEnum.FLOW_JSON_NOT_BE_NULL);
        JSONArray workArray = flow.getJSONArray(Strings.WORKS);
        assertNotEmpty(workArray, ExceptionEnum.WORKS_NOT_BE_NULL_OR_EMPTY);
        SequentialFlow sequentialFlow =  new SequentialFlow(deserializeWorks(workArray));
        sequentialFlow.named(flow.getString(Strings.NAME));
        sequentialFlow.policy(EnumUtils.getEnum(WorkExecutePolicy.class, flow.getString(Strings.POLICY)));
        sequentialFlow.context(deserializeContext(flow.getJSONObject(Strings.CONTEXT)));
        Boolean trace = flow.getBoolean(Strings.TRACE);
        sequentialFlow.trace(Boolean.TRUE.equals(trace));
        //parse then work and lastly work
        List<Work> thenWorks = deserializeThenWork(flow);
        if (Checker.BeNotEmpty(thenWorks)) {
            for (Work thenWork : thenWorks) {
                sequentialFlow.then(thenWork);
            }
        }
        sequentialFlow.lastly(deserializeLastWork(flow));
        return sequentialFlow;
    }

    public JSONObject serialize() {
        JSONObject json = serializeBase();
        json.put(Strings.TYPE, Strings.SEQUENTIAL);
        List<Work> works = Lists.newArrayList();
        for (Work work : this.workList) {
            if (!(work instanceof EndWork)) {
                works.add(work);
            }
        }
        json.put(Strings.WORKS, serializeWorks(works));

        if (Checker.BeNotEmpty(thenWorks)) {
            json.put(Strings.THEN, serializeWorks(thenWorks));
        }
        if (Checker.BeNotNull(lastWork)) {
            json.put(Strings.LASTLY, serializeWork(lastWork));
        }
        return json;
    }

    @Override
    public SequentialWorkReport execute() {
        return execute(Strings.EMPTY);
    }

    public SequentialWorkReport execute(String point) {
        return aNewSequentialWorkReport(executeInternal(point));
    }

    @Override
    public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
        return executeThenInternal(workReport, point);
    }

    @Override
    public void doExecute(String point) {
        if (beStopped()) {
            return;
        }

        WorkContext workContext = getDefaultWorkContext();
        Work work = queue.poll();

        if (work instanceof EndWork) {
            return;
        }

        //cache the result
        WorkReport report = doSingleWork(work, workContext, point);
        multipleWorkReport.addReport(report);

        if (beStopped()) {
            if (work instanceof WorkFlow) {
                queue.offerFirst(work);
            }
            pointWork = queue.peek();
            return;
        }

        if (beBreak(report)) {
            return;
        }

        //execute to next
        if (report.getStatus() != WorkStatus.STOPPED) {
            doExecute(point);
        }

    }

    @Override
    public void locate2CurrentWork() {
        locate2CurrentWorkInternal();
    }


    @Override
    public SequentialFlow then(Work work) {
        if (Checker.BeNotNull(work)) {
            thenWorks.add(work);
        }
        return this;
    }


    public static SequentialFlow aNewSequentialFlow(Work... works) {
        return new SequentialFlow(Arrays.asList(works));
    }

    //dynamic add work
    public SequentialFlow addWork(Work work) {
        int index = Iterables.indexOf(workList, w -> w instanceof EndWork);
        workList.add(index, work);
        return this;
    }

    //dynamic add work at index
    public SequentialFlow addWork(int index, Work work) {
        int endWorkIndex = Iterables.indexOf(workList, w -> w instanceof EndWork);
        if (index < 0) {
            index = 0;
        }
        if (index > endWorkIndex) {
            index = endWorkIndex;
        }
        workList.add(index, work);
        return this;
    }

    public SequentialFlow named(String name) {
        if (Checker.BeNotEmpty(name)) {
            this.name = name;
        }
        return this;
    }

    public SequentialFlow policy(WorkExecutePolicy workExecutePolicy) {
        if (Checker.BeNotNull(workExecutePolicy)) {
            this.workExecutePolicy = workExecutePolicy;
        }
        return this;
    }

    @Override
    public SequentialFlow context(WorkContext workContext) {
        if (Checker.BeNotNull(workContext)) {
            this.workContext = workContext;
        }
        return this;
    }

    public SequentialFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }
}
