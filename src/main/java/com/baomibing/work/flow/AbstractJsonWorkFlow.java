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
import com.baomibing.work.exception.WorkFlowException;
import com.baomibing.work.predicate.WorkReportJsonPredicate;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.PredicationUtil;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.AsyncWork;
import com.baomibing.work.work.NamedPointWork;
import com.baomibing.work.work.Work;
import com.google.common.collect.Lists;

import java.util.List;

import static com.baomibing.work.util.WorkUtil.*;
import static com.baomibing.work.work.AsyncWork.aNewAsyncWork;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public abstract class AbstractJsonWorkFlow {

    public static  WorkFlow deserialize(String flow) {
        JSONObject flowJson =  JSONObject.parseObject(flow);
        return deserializeWorkFlow(flowJson, true);
    }

    protected static WorkFlow deserializeWorkFlow(JSONObject flowJson, boolean beStrict) {
        assertNotNull(flowJson, ExceptionEnum.FLOW_JSON_NOT_BE_NULL);
        WorkFlow workFlow = null;
        String workClassName = flowJson.getString(Strings.TYPE);
        if (Strings.SEQUENTIAL.equals(workClassName)) {
            workFlow = SequentialFlow.deserialize(flowJson);
        }  else if (Strings.REPEAT.equals(workClassName)) {
            workFlow = RepeatFlow.deserialize(flowJson);
        } else if (Strings.PARALLEL.equals(workClassName)) {
            workFlow = ParallelFlow.deserialize(flowJson);
        } else if (Strings.LOOP.equals(workClassName)) {
            workFlow = LoopFlow.deserialize(flowJson);
        } else if (Strings.CONDITIONAL.equals(workClassName)) {
            workFlow = ConditionalFlow.deserialize(flowJson);
        } else if (Strings.CHOOSE.equals(workClassName)) {
            workFlow = ChooseFlow.deserialize(flowJson);
        } else {
            if (beStrict) {
                throw new WorkFlowException(ExceptionEnum.TYPE_IS_NOT_AN_VALID_WORK_FLOW_TYPE, workClassName);
            }
        }
        return workFlow;
    }

    protected static JSONObject serializeWorkFlow(Work work) {
        JSONObject workJson = null;
        if (work instanceof SequentialFlow) {
            SequentialFlow sequentialFlow = (SequentialFlow) work;
            workJson = sequentialFlow.serialize();
        } else if (work instanceof RepeatFlow) {
            RepeatFlow repeatFlow = (RepeatFlow) work;
            workJson = repeatFlow.serialize();
        } else if (work instanceof ParallelFlow) {
            ParallelFlow parallelFlow = (ParallelFlow) work;
            workJson = parallelFlow.serialize();
        } else if (work instanceof LoopFlow) {
            LoopFlow loopFlow = (LoopFlow) work;
            workJson = loopFlow.serialize();
        }  else if (work instanceof ConditionalFlow) {
            ConditionalFlow conditionalFlow = (ConditionalFlow) work;
            workJson = conditionalFlow.serialize();
        }  else if (work instanceof ChooseFlow) {
            ChooseFlow chooseFlow = (ChooseFlow) work;
            workJson = chooseFlow.serialize();
        }
        return workJson;
    }

    protected static List<Work> deserializeThenWork(JSONObject flowJson) {
        JSONArray works = flowJson.getJSONArray(Strings.THEN);
        if (Checker.BeNotEmpty(works)) {
            return deserializeWorks(works);
        }
        return Lists.newArrayList();
    }

    protected static Work deserializeLastWork(JSONObject flowJson) {
        JSONObject work = flowJson.getJSONObject(Strings.LASTLY);
        if (Checker.BeNotNull(work)) {
            return deserializeWork(work);
        }
        return null;
    }

    protected static Work deserializeWork(JSONObject workJson) {
        assertNotNull(workJson, ExceptionEnum.WORK_JSON_NOT_BE_NULL);
        Work work;
        String workClassName = workJson.getString(Strings.TYPE);
        work = deserializeWorkFlow(workJson, false);
        if (Checker.BeNotNull(work)) {
            return work;
        }

        if (NamedPointWork.class.getSimpleName().equals(workClassName)) {
            JSONObject innerWork = workJson.getJSONObject(Strings.WORK);
            work = aNamePointWork(deserializeWork(innerWork)).point(workJson.getString(Strings.POINT)).named(workJson.getString(Strings.NAME));
        } else if (AsyncWork.class.getSimpleName().equals(workClassName)) {
            JSONObject innerWork = workJson.getJSONObject(Strings.WORK);
            work = aNewAsyncWork(deserializeWork(innerWork)).withAutoShutDown(workJson.getBoolean(Strings.AUTO_SHUTDOWN));
        } else {
            Class<?> clazz = loadClass(workClassName);
            assertSubWork(clazz);
            work = (Work) workJson.toJavaObject(clazz);
        }
        if (Checker.BeNull(work)) {
            throw new WorkFlowException(ExceptionEnum.TYPE_IS_NOT_AN_VALID_WORK_TYPE, workClassName);
        }
        return work;
    }

    public static JSONObject serializeWork(Work work) {
        assertNotNull(work, ExceptionEnum.WORK_JSON_NOT_BE_NULL);
        JSONObject json = serializeWorkFlow(work);
        if (Checker.BeNotNull(json)) {
            return json;
        }
        json = new JSONObject();
        if (work instanceof NamedPointWork) {
            NamedPointWork namedPointWork = (NamedPointWork) work;
            Work innerWork = namedPointWork.getWork();
            json = JSONObject.parseObject(JSONObject.toJSONString(innerWork));
            json.put(Strings.TYPE, innerWork.getClass().getName());
            json.put(Strings.NAME, namedPointWork.getName());
            if (Checker.BeNotEmpty(namedPointWork.getPoint())) {
                json.put(Strings.POINT, namedPointWork.getPoint());
            }
        } else if (work instanceof AsyncWork) {
            AsyncWork asyncWork = (AsyncWork) work;
            Work innerWork = asyncWork.getWork();
            json.put(Strings.AUTO_SHUTDOWN, asyncWork.isAutoShutdown());
            json.put(Strings.TYPE, asyncWork.getClass().getName());
            JSONObject innerJson = JSONObject.parseObject(JSONObject.toJSONString(innerWork));
            innerJson.put(Strings.TYPE, innerWork.getClass().getName());
            json.put(Strings.WORK, innerJson);
        } else {
            json = JSONObject.parseObject(JSONObject.toJSONString(work));
            json.put(Strings.TYPE, work.getClass().getName());
        }
        return json;
    }

    protected static List<Work> deserializeWorks(JSONArray jsonArray) {
        List<Work> works = Lists.newArrayList();
        assertNotEmpty(jsonArray, ExceptionEnum.WORKS_NOT_BE_NULL_OR_EMPTY);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject workJson = jsonArray.getJSONObject(i);
            works.add(deserializeWork(workJson));
        }
        return works;
    }

    protected static JSONArray serializeWorks(List<Work> works) {
        assertNotEmpty(works, ExceptionEnum.WORKS_NOT_BE_NULL_OR_EMPTY);
        JSONArray jsonArray = new JSONArray();
        for (Work work : works) {
            jsonArray.add(serializeWork(work));
        }
        return jsonArray;
    }

    protected static WorkContext deserializeContext(JSONObject jsonObject) {
        if (Checker.BeEmpty(jsonObject)) {
            return null;
        }
        return jsonObject.toJavaObject(WorkContext.class);
    }

    protected static JSONObject serializeContext(WorkContext workContext) {
        if (Checker.BeNull(workContext)) {
            return new JSONObject();
        }
        return JSONObject.parseObject(JSONObject.toJSONString(workContext));
    }

    public WorkReportJsonPredicate toJsonPredicate(WorkReportPredicate predicate) {
        return PredicationUtil.toJsonPredicate(predicate);
    }
}
