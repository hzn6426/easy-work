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

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.*;

import static com.baomibing.work.flow.AbstractJsonWorkFlow.deserialize;

public class DeserializeTest {

    public static void deserializeSequentialFlowTest() {
        String json = ResourceReader.readJSON("json/sequential.json");
        SequentialFlow sequentialFlow = (SequentialFlow) deserialize(json);
        sequentialFlow.execute(new WorkContext());
    }

    public static void deserializeRepeatFlowTest() {
        String json = ResourceReader.readJSON("json/repeat.json");
        RepeatFlow repeatFlow = (RepeatFlow) deserialize(json);
        repeatFlow.execute(new WorkContext());
    }

    public static void deserializeRepeatFlowTest2() {
        String json = ResourceReader.readJSON("json/repeat2.json");
        RepeatFlow repeatFlow = (RepeatFlow) deserialize(json);
        repeatFlow.execute(new WorkContext());
    }

    public static void deserializeRepeatFlowNestedTest() {
        String json = ResourceReader.readJSON("json/repeat_nest.json");
        RepeatFlow repeatFlow = (RepeatFlow) deserialize(json);
        repeatFlow.execute(new WorkContext());
    }

    public static void testParallelFlow() {
        String json = ResourceReader.readJSON("json/parallel.json");
        ParallelFlow parallelFlow = (ParallelFlow) deserialize(json);
        parallelFlow.execute(new WorkContext());
    }

    public static void testLoopFlow() {
        String json = ResourceReader.readJSON("json/loop.json");
        LoopFlow loopFlow = (LoopFlow) deserialize(json);
        loopFlow.execute(new WorkContext());
    }

    public static void testConditionalFlow() {
        String json = ResourceReader.readJSON("json/conditional.json");
        ConditionalFlow conditionalFlow = (ConditionalFlow) deserialize(json);
        conditionalFlow.execute(new WorkContext());
    }

    public static void testChooseFlow() {
        String json = ResourceReader.readJSON("json/choose.json");
        ChooseFlow chooseFlow = (ChooseFlow) deserialize(json);
        chooseFlow.execute(new WorkContext());
    }

    public static void testChooseFlow2() {
        String json = ResourceReader.readJSON("json/choose2.json");
        ChooseFlow chooseFlow = (ChooseFlow) deserialize(json);
        chooseFlow.execute(new WorkContext());
    }

    public static void testChooseFlowAnd() {
        String json = ResourceReader.readJSON("json/choose_and.json");
        ChooseFlow chooseFlow = (ChooseFlow) deserialize(json);
        chooseFlow.execute(new WorkContext());
    }

    public static void testComplexFlow() {
        String json = ResourceReader.readJSON("json/complex.json");
        SequentialFlow sequentialFlow = (SequentialFlow) deserialize(json);
        sequentialFlow.execute(new WorkContext());
    }

    public static void main(String[] args) {
//        deserializeSequentialFlowTest();
//        deserializeRepeatFlowTest();
//        deserializeRepeatFlowTest2();
//        deserializeRepeatFlowNestedTest();
//        testParallelFlow();
//        testLoopFlow();
//        testConditionalFlow();
//        testChooseFlow();
//        testChooseFlow2();
//        testChooseFlowAnd();
        testComplexFlow();
    }
}
