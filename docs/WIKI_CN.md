📖 [English](WIKI.md) | 📖 中文

# 首页
Easy Work 是一个用于 Java 的工作流引擎。它提供了简洁的 API 和 构建模块，便于创建和运行可组合的工作流。
在Easy Work中，工作单元由`Work`接口表示，工作流则由`WorkFlow`接口表示。Easy  Work 提供了 `WorkFlow` 接口的6种实现方式：

<p align="center">
    <img src="../img/workflow.png" width="70%">
</p>

这些是使用Easy Work创建工作流时所需了解的唯一基本流程。你无需学习复杂的符号或概念，只需掌握几个易于理解的自然API即可。

# 快速开始

## 先决条件
Easy Work 需要 jdk 1.8+ 的运行环境。

## 源代码构建
从源代码构建 Easy Work 你需要安装 [git](htts://www.git-scm.com) 和 [maven](https://maven.apache.org/)
请遵循以下说明：
```
$ git clone https://github.com/hzn6426/easy-work.git
$ cd easy-work
$ mvn install
```
## maven 构建

使用 maven，请将以下依赖添加到你的 pom.xml中
```
<dependency>
    <groupId>com.baomibing</groupId>
    <artifactId>easy-work</artifactId>
    <version>latestVersion</version>
</dependency>

```

jar 包已上传中央仓库，可通过中央仓库进行搜索

# 定义一个 Work

## Work 接口
Easy Work中的流程单元由 Work 接口表示，以下是 `Work` 接口的定义:

```java
@FunctionalInterface
public interface  Work  {
    Object execute(WorkContext context);
}
```

你可以定义 Work 返回任何你希望的结果，以下是一个 Work 定义的例子：

```java
public class PrintMessageWork implements Work {

    private final String message;

    public PrintMessageWork(String message) {
        this.message = message;
    }

    @Override
    public String execute(WorkContext workContext) {
        System.out.println(message);
        return message;
    }
}
```
或者通过函数式接口的方式定义:

```java
Work doSomeWork = context -> {
    //do something
    return "OK";
};
```

## NamedPointWork 类
一个修饰`work`接口的类，可<b>命名</b> `work` 接口，在流程结果中通过 `workName` 来进行区分，这很重要，是在流程链中获快速取结果的一种快捷方式，同时该类支持断点模式，通过断点可暂停工作流

定义一个 NamedPointWork例子为:
```java
PrintMessageWork work4 = new PrintMessageWork("ok");
aNamePointWork(work4).named("work4").point("WORK_4");
```
你还可以通过 `NamedPointWork` 来添加监听器，监听器会在 `Work` 执行后进行回调，具体列子为：
```java
aNamePointWork(b).addWorkExecuteListener((DefaultWorkReport report, WorkContext workContext, Exception ex) -> {
    System.out.println(report.getStatus() == WorkStatus.COMPLETED ? "YES, SUCCESS" : "NO, FAILURE");
})
```
注意：你自定义的 `Work` 在流程中都会被 `NamedPointWork` 装饰，并自动生成一个名称，这是为后续 `trace` 功能提供支持，如果 `Work` 已经被自定义 `NamedPointWork` 装饰，不会再对此进行处理。

## AsyncWork 类
一个修饰`Work`接口的类，用该类包裹的 `Work` 接口将异步执行并忽略等待，直接将结果设置为 COMPLETE，因此该类不会都塞流程的执行。
该类主要用来定义长时间执行的任务，可通过 `NamedPointWork` 来添加监听器，监听执行的结果，或者通过 `isBeDone()` 方法来判断是否执行结束。

定义一个AsyncWork的例子为(更多例子参考test/java/AsyncWorkTest):
```java
PrintMessageWork a = new PrintMessageWork("a");
LongWaitPrintMessageWork b = new LongWaitPrintMessageWork("execute in 10 seconds,that a long work...");
PrintMessageWork c = new PrintMessageWork("c");
PrintMessageWork d = new PrintMessageWork("d");
WorkExecuteListener listener = new WorkExecuteListener() {
    @Override
    public void onWorkExecute(DefaultWorkReport workReport, WorkContext workContext, Exception exception) {
        System.out.println("execute finished");
    }
};
SequentialFlow flow = aNewSequentialFlow(a,  aNamePointWork(aNewAsyncWork(b).withAutoShutDown(true)).addWorkExecuteListener(listener),c, d);
aNewWorkFlowEngine().run(flow, new WorkContext());
```

## 流程结果
流程执行后结果会被封装到 `WorkReport` 接口中，以下是`WorkReport` 接口:
```java
public interface WorkReport extends ExecuteStep {

    WorkStatus getStatus();
    Throwable getError();
    WorkContext getWorkContext();
    Object getResult();
    String getWorkName();

}
```
WorkReport具有以下信息：
1. 如果运行成功 state 为 `COMPLETED`，失败为 `FAILED`
2. `getError()`可获取对应的异常信息
3. Work 的返回结果通过`getResult()`获取
4. `getWorkName()` 可获取对应执行的 Work

## 流程执行策略
多个流程执行过程中会出现失败或者异常，流程策略用来定义执行过程中出现`特殊`情况的处理方案。
在流程定义中可以通过 `policy` 方法来进行过设置对应的策略。
策略`WorkExecutePolicy`有如下几种:
- `FAST_FAIL` 有一个工作单元执行`失败`(状态为 `FAILED`)时，停止执行流程并返回失败结果，没有错误时返回所有结果
- `FAST_FAIL_EXCEPTION` 有一个工作单元执行`失败`时并且`异常`(`error`字段) 信息不为 `NULL` 时，停止执行流程并返回失败结果，没有错误时返回所有结果，`默认方案`
- `FAST_SUCCESS` 有一个工作单元执行成功(状态为`COMPLETED`)时，停止执行流程并返回该成功的结果
- `FAST_ALL` 执行所有工作单元，不管是否有异常信息
- `FAST_ALL_SUCCESS` 执行所有工作单元，返回所有成功的结果
- `FAST_EXCEPTION` 有一个工作单元执行`异常`时，停止执行流程并抛出异常信息

# 定义一个流程

## WorkFlow 接口
一个流程在 Easy Work 中用 `WorkFlow`接口表示:
```java
public interface WorkFlow extends Work {

    WorkReport execute(WorkContext context);

    WorkFlow context(WorkContext workContext);
}
```
一个 WorkFlow 也是一个 Work，这就使工作流可互相组合。

工作流`WorkFlow` 包含两个待实现的方法：
1. 方法-执行流程，传入上下文，执行工作流，返回`WorkReport` 结果
2. 方法-上下文，注入自定义的上下文信息，返回本身以便继续迭代

## 内置工作流
Easy Work 提供了 6 种 内置的  `WorkFlow` 工作流实现:
<p align="center">
    <img src="../img/workflow.png" width="70%">
</p>

## ConditionalFlow
一个`ConditionalFlow` 是根据条件的正确与否选择执行对应的工作单元，其由 4 个组成部分：
1. 首先执行当前的工作流,多个工作流以顺序执行
2. 根据条件逻辑 `WorkReportPredicate` 进行判断
3. 如果逻辑判断为 `true` 执行第一个工作单元
4. 如果逻辑判断为 `false` 执行第二个工作单元(可选)

要创建一个`ConditionalFlow`，可以参考以下的例子(`test/java/TestConditionalFlow`):
```java
WorkFlow flow = aNewConditionalFlow(successWork).when(WorkReportPredicate.COMPLETED, work1, work2);
```
## RepeatFlow
一个`RepeatFlow`是对于给定的工作单元进行循环，直到条件为`false`或者循环固定的次数。条件表达式通过`WorkReportPredicate`来进行构建。
你可以通过自定义`WorkReportPredicate`接口来构建满足要求的条件逻辑。
要构建一个`RepeatFlow`，可以参考以下的例子(`test/java/TestRepeatFlow`)：
```java
WorkFlow flow = aNewRepeatFlow(repeatWork).times(3);
// 或者
WorkFlow flow = aNewRepeatFlow(repeatWork).until(WorkReportPredicate.FAILED);
```

## SequentialFlow
`SequentialFlow` 就像描述的那样，按照顺序执行其中的工作单元，每一个工作单元都会等待前一个工作单元执行完成后(成功或失败)再执行，只返回其中的一个结果，具体由设置的策略决定。
要构建一个`SequentialFlow`，可以参考以下的例子(`test/java/TestConditionalFlow`):
```java
WorkFlow flow = aNewSequentialFlow(work1, work2, work3);
```
你可以在除了构造函数之外通过提供的方法来动态添加对应的 Work:
1. `addWork(Work work)` 方法将 Work 添加到最后
2. `addWork(int index, Work work)` 方法将 Work 添加到对应的位置

注意：以上方法添加的 Work 总是在 `then` 方法对应的 Work 之前

## ParallelFlow
一个`ParallelFlow`是对其中的工作单元`并行`执行，当所有的工作单元执行完成后才算完成，流程返回一个 `ParallelWorkReport`，包含并发执行的结果，具体由设置的策略决定。
在`ParallelFlow` 可以设置对应的关键参数值，主要有以下几点：
1. `timeoutInSeconds` 表示等待的秒数，通过方法`withTimeoutInSeconds`来设置，默认为`60秒`，超过等待时间则返回失败
2. `executor` 表示使用的线程池，通过`withExecutor`来设置自定义的线程池，否则使用默认线程池来提交任务
3. `autoShutdown` 表示任务完成后，线程池是否自动关闭，通过`withAutoShutDown`来设置该参数

要构建一个`ParallelFlow`，可以参考以下的例子(`test/java/TestParallelFlow`):
```java
WorkFlow flow = aNewParallelFlow(work1, work2, exceptionWork, work3);
```
你可以在除了构造函数之外通过提供的方法来动态添加对应的 Work:
1. `addWork(Work work)` 方法将 Work 添加到最后

注意：以上方法添加的 Work 总是在 `then` 方法对应的 Work 之前

## ChooseFlow
一个`ChooseFlow` 是通过多个条件分支 来选择执行对应的工作单元，如果分支条件都不满足，
则执行`otherWise`中的工作单元(可选)，类似 `if..else if..else`结构，只会执行<b>第一个</b>满足条件的工作单元。

`ChooseFlow`通过方法`witShortLogic` 来设置 `shortLogic` 参数，当设置为 `false` 时，
分支判断变为: 执行每个分支条件为 `true` 的工作单元，如果都为 `false`，执行 `otherWise` 工作单元。

要构建一个`ChooseFlow`，可参考以下的例子(`test/java/TestChooseFlow`):
```java
WorkFlow flow = aNewChooseFlow(work)
    .chooseWhen((report) -> report.getResult().equals(1), work1)
    .chooseWhen((report) -> report.getResult().equals(2), work2)
    .chooseWhen((report) -> report.getResult().equals(3), work3)
    .otherWise(work4);
```
## LoopFlow
一个`LoopFlow` 是对其中的工作单元<b>无限</b>`顺序循环`执行，直到满足中断条件，可通过设置的`策略`来应用循环的中断的方式，也可以通过对应的自定义逻辑判断来中断循环，具体如下：
1. 通过方法`withBreakPredicate`来设置中断条件，满足条件后中断循环，返回最后一次执行的结果
2. 通过方法`withContinuePredicate`来设置跳过某个工作单元

系统定义了`LoopIndexPredicate` 和 `LoopLengthPredicate`辅助进行基于索引和循环长度的中断条件

要构建一个`LoopFlow`，可以参考以下的例子(`test/java/TestLoopFlow`):
```java
WorkFlow flow = aNewLoopFlow(work1, work2, work3, work4).withBreakPredicate(LoopIndexPredicate.indexPredicate(2));
```
你可以在除了构造函数之外通过提供的方法来动态添加对应的 Work:
1. `addWork(Work work)` 方法将 Work 添加到最后
2. `addWork(int index, Work work)` 方法将 Work 添加到对应的位置

注意：以上方法添加的 Work 总是在 `then` 方法对应的 Work 之前

# 构建工作流

## 组合式构建
Easy Work 的 API 提供了组合式工作流构建的方法，通过内置的工作流可以编排构建各种复杂流程。
首先，让我们创建一个 Work:

```java
public class PrintMessageWork implements Work {

    private final String message;

    public PrintMessageWork(String message) {
        this.message = message;
    }

    @Override
    public String execute(WorkContext workContext) {
        System.out.println(message);
        return message;
    }
}
```
此 Work 将指定消息打印至标准输出。现在假设我们想要创建如下工作流程：
1. 打印 a 三次
2. 顺序打印 b c d
3. 并行执行 e f
4. 如果并行执行的结果成功，执行 g, 否则执行 h
5. 最后执行 z

此工作流程说明如下：
<p align="center">
    <img src="../img/example.png" width="70%">
</p>

* `flow1` 是一个 打印 a 的 `RepeatFlow`，连续执行三次
* `flow2` 是一个 依次打印 b c d 的 `SequentialFlow`， 按照顺序依次执行
* `flow3` 是一个 并行打印 e f 的 `ParallelFlow`， 同时执行
* `flow4` 是一个 基于条件判断的 `ConditionalFlow`，首先执行`flow3`，如果执行成功（状态为 COMPLETE）则执行 g，否则执行 h
* `flow5` 是一个 顺序执行流程 `SequentialFlow`，保证 顺序执行 `flow1` `flow2` `flow4`，最后执行 z

使用Easy Work，此工作流可以通过以下代码段实现：
```java
PrintMessageWork a = new PrintMessageWork("a");
PrintMessageWork b = new PrintMessageWork("b");
PrintMessageWork c = new PrintMessageWork("c");
PrintMessageWork d = new PrintMessageWork("d");
PrintMessageWork e = new PrintMessageWork("e");
PrintMessageWork f = new PrintMessageWork("f");
PrintMessageWork g = new PrintMessageWork("g");
PrintMessageWork h = new PrintMessageWork("h");
PrintMessageWork z = new PrintMessageWork("z");

WorkFlow flow = aNewSequentialFlow(
    aNewRepeatFlow(a).times(3),
    aNewSequentialFlow(b,c,d),
    aNewConditionalFlow(
        aNewParallelFlow(e,f).withAutoShutDown(true)
    ).when(
        WorkReportPredicate.COMPLETED,
        g,
        h
    ),
    z
);
aNewWorkFlowEngine().run(flow, new WorkContext());
```

这不是一个非常有用的工作流，只是为了让你了解如何使用`Easy Work`而编写工作流。

你可以在 `test/java` 中 查看更多的测试用例。

## 单步式构建
对于上面的例子，你还可以采用单步式构建的方式，区别于组合式构建，单步式可以根据每个流程的`结果`进行自定义构建，每一步都返回一个 `WorkReport`，例如:
```java
WorkReport workReport = aNewSequentialFlow(aNewRepeatFlow(a).times(3))
    .execute()
    .thenExecute(aNewSequentialFlow(b,c,d))
    .thenExecute(aNewParallelFlow(e,f).withAutoShutDown(true))
    .thenExecute(report -> {
        if (report.getStatus() == WorkStatus.COMPLETED) {
            return g;
        }
        return h;
    }).thenExecute(z);
```
<b>注意：此种方式构建的会立即执行，并返回 `WorkReport` 结果。</b>

更多的例子请参考 `test/java/TestReportStepFlow`

`WortReport` 继承了 `ExecuteStep` 接口，在 `DefaultWorkReport` 中提供了默认的实现:
```java
public interface ExecuteStep extends WorkFlow {

    WorkReport loopExecute(Work...works);

    WorkReport loopExecute(WorkReportPredicate breakPredicate, WorkReportPredicate continuePredicate, Work...works);

    WorkReport parallelExecute(ExecutorService service, Work... works);

    WorkReport parallelExecute(Work... works);

    WorkReport repatUtilExecute(WorkReportPredicate predicate, Work work);

    WorkReport thenExecute(Work... works);

    WorkReport thenExecute(Function<WorkReport, Work> fn);

    WorkReport whenExecute(WorkReportPredicate predicate, Work work);

    WorkReport whenExecute(WorkReportPredicate predicate, Work trueWork, Work falseWork);
    
}
```
这些API能够使基于 `WorkReport` 的单步式结果构建成为可能。

## ThenStep 
类似上面的单步式构建流程， ThenStep 接口为组合式流程提供了基于结果的构建，接口如下:
```java
public interface ThenStep extends WorkFlow {

    WorkFlow then(Function<WorkReport, Work> fun);

    WorkFlow then(Work work);

}
```
被  `then` 包裹的工作单元总是执行，除非策略为 `FAST_EXCEPTION`。

一个 `ThenStep` 的例子为(更多例子请参考 `test/java/ThenFlowTest`):
```java
PrintMessageWork work1 = new PrintMessageWork("work1");
PrintMessageWork work2 = new PrintMessageWork("work2");
PrintMessageWork work3 = new PrintMessageWork("work3");
PrintMessageWork work4 = new PrintMessageWork("work4");
PrintMessageWork work5 = new PrintMessageWork("work5");
PrintMessageWork work6 = new PrintMessageWork("after parallel");
PrintMessageWork work7 = new PrintMessageWork("after conditional");
ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();

WorkContext workContext = new WorkContext();
WorkFlow flow = aNewSequentialFlow(
    exceptionPrintMessageWork,
    aNewRepeatFlow(work1).times(3)
).then(workReport ->
    aNewConditionalFlow(
        aNewParallelFlow(
            work2,
            work3
        ).withAutoShutDown(true).then(report -> work6)
    ).when(
        WorkReportPredicate.COMPLETED,
        work4,
        work5
    ).then(r -> work7)
);
aNewWorkFlowEngine().run(flow, workContext);
```
该例子运行的结果为：
```
work2
work3
after parallel
work4
after conditional
```
你可以通过无限多个 `then` 方法来进行构建工作流（不建议），这些工作流会像 `SequentialWorkFlow` 那样顺序执行，返回结果策略仍满足对应工作流的 `policy` 策略

## LastStep
`LastStep` 与 `finally` 类似，总会执行其中的工作单元，不管是否发生异常，接口定义如下:
```java
public interface LastStep {

    WorkFlow lastly(Work... work);
}
```
一个`LastStep` 的例子为：

```java
PrintMessageWork work1 = new PrintMessageWork("work1");
PrintMessageWork work2 = new PrintMessageWork("work2");
PrintMessageWork work3 = new PrintMessageWork("work3");
PrintMessageWork work4 = new PrintMessageWork("work4");
PrintMessageWork work5 = new PrintMessageWork("work5");
PrintMessageWork finalWork = new PrintMessageWork("final");

ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();
WorkContext workContext = new WorkContext();
WorkFlow flow = aNewSequentialFlow(
    exceptionPrintMessageWork,
    aNewRepeatFlow(work1).times(3),
    aNewConditionalFlow(
        aNewParallelFlow(work2,work3)
    ).when(WorkReportPredicate.COMPLETED, work4, work5)
).policy(WorkExecutePolicy.FAST_EXCEPTION)
 .lastly(finalWork);
aNewWorkFlowEngine().run(flow, workContext);
```
该例子运行的结果为：
```
final
```


# 执行工作流

## 流程上下文
执行工作流可设置对应的上下文信息，Easy Work 中的上下文通过 `WorkContext`来表示，该对象中包含一个`Map`类型的属性用来存储流程所需的参数信息。

可通过 `WorkFlow.context()`方法来为工作流传入上下文信息。

要构建一个上下文可参考如下的例子：

```java
new WorkContext().put("param_a", "a").put("param_b", "b");
```
## WorkFlowEngine 
 `WorkFlowEngine`接口表示一个工作流引擎:
```java
public interface WorkFlowEngine {
    
    WorkReport run(WorkFlow workFlow, WorkContext workContext);
}
```
Easy Work提供了一个该接口的默认实现类，你可以通过以下方式进行构建:
```java
WorkFlowEngine workFlowEngine = aNewWorkFlowEngine();
```
你可以通过调用`run`方法来执行 `WorkFlow`工作流:
```java
WorkFlow workFlow = ... // 创建工作流
WorkReport workReport = aNewWorkFlowEngine().run(workFlow, new WorkContext());
```

## WorkFlow.execute
你还可以通过 `WorkFlow.execute`方法来简化该操作，来执行工作流:
```java
WorkFlow workFlow = ... // 创建工作流
WorkReport workReport = workFlow.execute(new WorkContext());
```

## AbstractWorkFlow.execute
你还可以通过 `AbstractWorkFlow.execute`方法来执行工作流:
```java
WorkReport workReport = aNewSequentialFlow(work1, work2, work3).execute();
```
此时可以通过 `context()`方法来传递自定义的上下文信息

## 暂停工作流
你可以设置`断点`来进行暂停工作流，这可以通过使用 `NamedPointWork` 来装饰 Work，并设置断点，通过 `execute(String point)`方法来执行到对应的`断点`，
通过 `execute()`方法来忽略断点执行，如果工作流已暂停，则从暂停处执行。


一个可供参考的断点例子为（更多例子参考`test/java/**Point`)：
```java
PrintMessageWork a = new PrintMessageWork("a");
PrintMessageWork b = new PrintMessageWork("b");
PrintMessageWork c = new PrintMessageWork("c");
PrintMessageWork d = new PrintMessageWork("d");
PrintMessageWork e = new PrintMessageWork("e");
PrintMessageWork f = new PrintMessageWork("f");
PrintMessageWork g = new PrintMessageWork("g");
PrintMessageWork h = new PrintMessageWork("h");

SequentialFlow flow =  aNewSequentialFlow(
    a,
    b,
    aNewSequentialFlow(aNamePointWork(c).named("THE_C").point("CC"),d),
    e
    ).then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);
flow.execute("CC");
System.out.println("execute to CC..");
flow.execute("GG");
System.out.println("execute to GG..");
flow.execute("");
```
你可以在 Easy Work 内置的 6 种流程的任何位置设置`断点`，可以设置任意多个`断点`，注意以下几点：
1. 断点只支持装饰 `Work` 接口，不支持修饰 `WorkFlow` 接口
2. 对于 `ParallelWorkFlow` 中的 `Work` 设置断点会被忽略，这是为了保证其中的 `Work` 并发执行
3. 对于 `then` 方法同样支持断点执行
4. `last`方法不支持断点执行，因为它总是执行

# 获取工作流结果

## DefaultWorkReport
当一个 Work 执行时，将返回一个 `DefaultWorkReport`对象，该对象实现了 `WorkReport` 接口， 并继承了 `AbstractWorkReport`类，这样有了单步执行的能力，
通过 `getResult()` 方法获取对应 `Work` 的执行结果，通过 `getWorkName()` 方法获取对应 `Work` 的名称

## MultipleWorkReport
当内置的 `WorkFlow` 返回 `WorkReport` 时，该 `WorkReport` <b>继承</b> `MultipleWorkReport`，一个`MultipleWorkReport` 会有多个结果，都包含在 `reports` 属性中，其提供了简单的方法来根据索引获取对应结果并进行转化：
```java
public <T> T getResult(int index, Class<T> clazz) {
    return (T) getResult().get(index);
}


public <T> Collection<T> getResultCollection(int index, Class<T> clazz) {
    return (Collection<T>) getResult(index, clazz);
}
```
一个可供参考的例子为:
```java
ParallelWorkReport report = aNewParallelFlow(countWork, dataWork).execute();
Integer count = report.getResult(0, Integer.class);
List<Order> applies = Lists.newArrayList(report.getResultCollection(1, Order.class));
```
MultipleWorkReport 的状态遵循以下规则:
1. 有一个结果为 `FAILED`，其状态为 `FAILED`
2. 所有结果都为 `COMPLETED`，则状态为 `COMPLETED`
3. 如果 `reports` 为空，返回 `COMPLETED`

## trace 支持
 `trace` 是对 `workFlow` 结果追踪的一种支持，可通过`trace(true)` 方法打开结果跟踪，工作流中的每个 `Work` 的执行结果会根据 `name -> WorkReport`的映射存储，通过 `getExecutedReportMap()` 方法获取对应的结果映射。
 
一个 trace的例子为(更多例子请参考 `test/java/TraceTest`):
```java
PrintMessageWork work1 = new PrintMessageWork("foo");
PrintMessageWork work2 = new PrintMessageWork("hello");
PrintMessageWork work3 = new PrintMessageWork("world");
PrintMessageWork work4 = new PrintMessageWork("ok");
PrintMessageWork work5 = new PrintMessageWork("nok");

ConditionalFlow conditionalFlow = aNewConditionalFlow(
    aNewParallelFlow(work2,work3).withAutoShutDown(true)
).when(WorkReportPredicate.COMPLETED, aNamePointWork(work4).named("work4"), work5).trace(true);

WorkContext workContext = new WorkContext();
SequentialFlow flow = aNewSequentialFlow(
    aNewRepeatFlow(work1).times(3),
    conditionalFlow
).named("sequential").trace(true);
aNewWorkFlowEngine().run(flow, workContext);
Map<String, WorkReport> map =  flow.getExecutedReportMap();
//you can get result of work4
WorkReport work4Report = map.get("work4");
for (Map.Entry<String, WorkReport> entry : map.entrySet()) {
    System.out.println(entry.getKey());
    WorkReport report = entry.getValue();
    System.out.println(report.getClass().getName());
}
//you can get the result of conditionalFlow
Map<String, WorkReport> map2 =  conditionalFlow.getExecutedReportMap();
for (Map.Entry<String, WorkReport> entry : map2.entrySet()) {
    System.out.println(entry.getKey());
    WorkReport report = entry.getValue();
    System.out.println(report.getClass().getName());
}
```

## 监听器
你可以通过 `aNamePointWork`类添加监听器的配置，只要 Work执行（可能因为策略导致无法执行），监听器总会回调，添加监听器的例子为：
```java
WorkExecuteListener listener = (DefaultWorkReport report, WorkContext workContext, Exception ex) -> {
    System.out.println(report.getStatus() == WorkStatus.COMPLETED ? "YES, SUCCESS" : "NO, FAILURE");
};
SequentialFlow flow = aNewSequentialFlow(a, aNamePointWork(b).addWorkExecuteListener(listener), c);
```
监听器提供了 3 个参数：
1. 该 Work 返回的结果，可以从结果中获取执行的状态，根据状态来判断是否成功
2. 执行该 Work 时的 `context` 上下文信息
3. Work 执行异常时的异常信息，只有在执行失败时才有值

# Predicate 条件
Easy Work 中的 `Predicate` 都是 `WorkReportPredicate` 接口类型的实现, 主要用于在 `ConditionalFlow` 和 `ChooseFlow` 中进行条件决策。
从 1.0.7版本开始添加了多种类型，辅助进行条件决策，更多例子请参考（test/java/RoutePredicateTest)。

## AllPredicate 类
对流程执行的结果（`MultipleWorkReport`类型）进行匹配，其内所有的 `WorkReport` 都满足条件，则为 `true`，否则为 `false`

## AnyPredicate 类
对流程结果进行匹配，其内所有的`WorkReport`有一个满足条件，则为`true`，否则为 `false`

## NonePredicate 类
对流程结果进行匹配，其内所有的`WorkReport`没有一个满足条件，则为`true`，否则为 `false`

## AndPredicate 类
对流程结果进行匹配，其内的`WorkReport`满足所有设置的组合条件时，则为`true`，否则为 `false`

## OrPredicate 类
对流程结果进行匹配，其内的`WorkReport`满足任何设置的组合条件时，则为`true`，否则为 `false`