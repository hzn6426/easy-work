
📖 English | 📖 [中文](WIKI_CN.md)

# Home Page
Easy Work is a workflow engine for Java. It provides concise APIs and building blocks for creating and running composable workflows.

In Easy Work, work units are represented by the `Work` interface, while workflows are represented by the `WorkFlow` interface.

Easy Work provides six implementation methods for the `WorkFlow` interface:

<p align="center">
    <img src="../img/workflow.png" width="70%">
</p>

Those are the only basic flows you need to know to start creating workflows with Easy Work.

You don't need to learn a complex notation or concepts, just a few natural APIs that are easy to think about.

# Quick Start

## Prerequisite
Easy Work requires a Java 1.8+ runtime。

## Building from source
To build Easy Work from sources, you need to have [git](htts://www.git-scm.com) and [maven](https://maven.apache.org/) installed and set up.

Please follow these instructions :
```
$ git clone https://github.com/hzn6426/easy-work.git
$ cd easy-work
$ mvn install
```
## Use with maven

If you use maven, add the following dependency to your pom.xml :
```
<dependency>
    <groupId>com.baomibing</groupId>
    <artifactId>easy-work</artifactId>
    <version>latestVersion</version>
</dependency>

```

The JAR package has been uploaded to the central repository and can be searched through the central repository.

# Define a unit of Work

## The Work interface
A unit of work in Easy Work is represented by the `Work` interface:

```java
@FunctionalInterface
public interface  Work  {
    Object execute(WorkContext context);
}
```
You can define `Work` to return any desired result. Here is an example of a Work definition:

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
Or define it through functional interfaces:

```java
Work doSomeWork = context -> {
    //do something
    return "OK";
};
```

## The NamedPointWork 
A class that decorates the `work` interface, which can <b>name</b> the `work` interface, allowing differentiation in the process results via `workName`.

This is crucial as it serves as a shortcut for quickly retrieving results within the process chain. 
At the same time, this class supports `break point` mode, which can pause the workflow through breakpoints


An example of defining a NamedPointWork is:

```java
PrintMessageWork work4 = new PrintMessageWork("ok");
aNamePointWork(work4).named("work4").point("WORK_4");
```
Note: Your custom `Work` will be decorated with `NamedPointWork` in the process and automatically generate a name to support the subsequent `trace` function.

If `Work` has already been customized with `NamedPointWork` decoration, it will no longer be processed



## The WorkReport interface
After the process is executed, the result will be encapsulated into the `WorkReport` interface. The following is the `WorkReport` interface:

```java
public interface WorkReport extends ExecuteStep {

    WorkStatus getStatus();
    Throwable getError();
    WorkContext getWorkContext();
    Object getResult();
    String getWorkName();

}
```
The `WorkReport` contains the following information:
1. If the work execute success, the state is `COMPLETED`，otherwise is `FAILED`.
2. `getError()`can retrieve the exception information.
3. The return result of `Work` is obtained through `getResult()` method.
4. `getWorkName()` can retrieve the corresponding `Work` to be executed.

## Work Policy
Failure or exception may occur during the execution of the flows, `Work Policy` used to define the handling plan for `special` situations that occur during the execution flows.

Corresponding policies can be set in the process definition through the `policy(WorkExecutePolicy)` method.

There are several types of `WorkExecutePolicy` available:
- `FAST_FAIL` When a work unit fails to execute (with a status of `FAILED`), stop the execution  and return the failure result, return all result if not have any FAIL
- `FAST_FAIL_EXCEPTION` When a work unit fails to execute and the `error` field information is not `NULL`, stop the execution  and return the failure result, return all result if not have any FAIL `default` policy
- `FAST_SUCCESS` When a work unit is successfully executed (with the status of `Completed`), stop the execution  and return the result of the success.
- `FAST_ALL` Execute all work units, regardless of any `exception` information
- `FAST_ALL_SUCCESS` Execute all work units and return all successful results. 
- `FAST_EXCEPTION` When a work unit executes an `exception`, stop the execution  and throw the `exception`.

# Define a WorkFlow

## The WorkFlow interface
A flow is represented by the `WorkFlow` interface in Easy Work:

```java
public interface WorkFlow extends Work {

    WorkReport execute(WorkContext context);

    WorkFlow context(WorkContext workContext);
}
```

A WorkFlow is also a Work, which allows workflows to be combined with each other.

The `WorkFlow` contains two methods to be implemented:

1. Method - Execute , pass in `context`, execute the workflow and return result of `WorkReport` 
2. Method - Context, inject custom `context` information, return itself for further iteration

## Built-in workflow
Easy Work provides six implementation methods for the `WorkFlow` interface:
<p align="center">
    <img src="../img/workflow.png" width="70%">
</p>

## ConditionalFlow
A `ConditionalFlow` is a unit of work that is selected and executed based on the correctness of the conditions, which  is defined by 4 artifacts:
1. Firstly, execute the current workflow, and multiple workflows are executed in sequence
2. A `WorkReportPredicate` for the conditional logic
3. The unit of work to execute if the predicate is satisfied
4. The unit of work to execute if the predicate is not satisfied (optional)

To create a `ConditionalFlow`, you can refer to the following example (`test/java/TestConditionalFlow`):
```java
WorkFlow flow = aNewConditionalFlow(successWork).when(WorkReportPredicate.COMPLETED, work1, work2);
```
## RepeatFlow
A `RepeatFlow` is a flow of looping a given unit of work until the condition is `false` or a fixed number of loops are reached. Conditional expressions are constructed by `WorkReportPredicte`.

You can build conditional logic that meets the requirements by customizing the `WorkReportPredicte` interface.

To create a `RepeatFlow`, you can refer to the following example(`test/java/TestRepeatFlow`)：
```java
WorkFlow flow = aNewRepeatFlow(repeatWork).times(3);
// 或者
WorkFlow flow = aNewRepeatFlow(repeatWork).until(WorkReportPredicate.FAILED);
```

## SequentialFlow
A `SequentialFlow` As described, the work units are executed in sequence, and each work unit waits for the completion of the previous one (COMPLETED or FAILED) before executing, returning only one result, which depending on the policy.
To create a `SequentialFlow`, you can refer to the following example(`test/java/TestConditionalFlow`):
```java
WorkFlow flow = aNewSequentialFlow(work1, work2, work3);
```
You can dynamically add the corresponding Work through the provided methods in addition to the constructor:

1. `addWork(Work work)` Method to add Work to the end
2. `addWork(int index, Work work)` Method to add Work to the corresponding location

Note: The Work added by the above methods is always before the Work corresponding to the `then` method

## ParallelFlow
A `ParallelFlow` is executed in `parallel` on its work units. It is considered complete only when all work units have been executed. The flow returns a `ParallelWorkReport` containing the results of concurrent execution, which is determined by the policy.

In `ParallelFlow`, corresponding key parameter values can be set, mainly including the following points:
1. `timeoutInSeconds` Indicates the number of seconds to wait, set through the method `withTimeoutInSeconds`, default is `60 seconds`, and returns a `FAILED` state if the waiting time is exceeded.
2. `executor` Indicate the thread pool used, set a custom thread pool through the method `withExecutor`, otherwise use the default thread pool to submit tasks.
3. `autoShutdown` Indicates whether the thread pool will automatically shut down after the task is completed, set this parameter through `withAutoShutDown`.

To create a `ParallelFlow`, you can refer to the following example(`test/java/TestParallelFlow`):
```java
WorkFlow flow = aNewParallelFlow(work1, work2, exceptionWork, work3);
```
You can dynamically add the corresponding Work through the provided methods in addition to the constructor:

1. `addWork(Work work)` Method to add Work to the end

Note: The Work added by the above methods is always before the Work corresponding to the `then` method

## ChooseFlow
A `ChooseFlow` selects and executes the corresponding work unit through multiple conditional branches. If none of the branch conditions are met,
Then execute the work unit in 'otherWise' (optional), similar to the structure of  `if..else if..else`, and only execute the first work unit that meets the condition.

`ChooseFlow` sets the `shortLogic` parameter through the method `witShorLogic`. When set to `false`,
 execute each work unit with a condition of `true`, and if all conditions are `false`, execute the `otherWise` work unit.

To create a `ChooseFlow`, you can refer to the following example(`test/java/TestChooseFlow`):
```java
WorkFlow flow = aNewChooseFlow(work)
    .chooseWhen((report) -> report.getResult().equals(1), work1)
    .chooseWhen((report) -> report.getResult().equals(2), work2)
    .chooseWhen((report) -> report.getResult().equals(3), work3)
    .otherWise(work4);
```
## LoopFlow
A `LoopFlow` is a sequential <b>infinite</b> loop execution of its work units,Until the interruption condition is met, which can be applied through a set `policy` to interrupt the loop, or through corresponding custom logic judgment to interrupt the loop, as follows:

1. Set the interrupt condition through the method `withBreakPredicte`, interrupt the loop when the condition is met, and return the result of the last execution.
2. Set to skip a certain work unit through the method `withContinuePredicate`

Easy Work has defined `LoopIndexPredicate` and `LoopLengthPredicate` to assist in index and loop length based interrupt conditions

To create a `LoopFlow`，you can refer to the following example(`test/java/TestLoopFlow`):
```java
WorkFlow flow = aNewLoopFlow(work1, work2, work3, work4).withBreakPredicate(LoopIndexPredicate.indexPredicate(2));
```
You can dynamically add the corresponding Work through the provided methods in addition to the constructor:

1. `addWork(Work work)` Method to add Work to the end
2. `addWork(int index, Work work)` Method to add Work to the corresponding location

Note: The Work added by the above methods is always before the Work corresponding to the `then` method

# Build workflow

## Composable Build
Easy Work provides the API  for constructing composite workflows, allowing for the orchestration and construction of various complex flows through built-in workflows.

First let's write some work:

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
This unit of work prints a given message to the standard output. Now let's suppose we want to create the following workflow:
1. print `a` three times
2. then print `b` `c` `d` in sequence
3. then print `e` `f` in parallel
4. then if both `e` and `f` have been successfully printed to the console, print `g`, otherwise print `h`
5. finally print `z`

This workflow can be illustrated as follows:
<p align="center">
    <img src="../img/example.png" width="70%">
</p>

* `flow1` is a `RepeatFlow` of work which print `a` three times
* `flow2` is a `SequentialFlow` of work which print `b` `c` `d` in sequence order
* `flow3` is a `ParallelFlow` of work which respectively print `e` and `f` in parallel
* `flow4` is a `ConditionalFlow` based on conditional judgment. It first executes `flow3` then if the result is successful (in the state of 'Complete') execute `g`, otherwise, execute `h`
* `flow5` is a `SequentialFlow`, which ensures the sequential execution of `Flow1`, `Flow2`, `Flow4`, and finally executes `z`

With Easy Work，this workflow can be implemented with the following snippet:
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

This is not a very useful workflow, but just to give you an idea about how to write workflows with Easy Work.

You can view more test cases in 'test/java'.

## Step build

For the above example, you can also use a `single step build` method, which is different from composite construction. `Single step build` can be customized based on the `results` of each process, and each step returns a `WorkReport`, for example:

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
<b>Note: This method of construction will be executed immediately and return the `WorkReport` .</b>

For more examples, please refer to `test/java/TestReportStepFlow`

`WortReport` inherits the `ExecuteStep` interface and provides a default implementation in the `DefaultWorkReport`:

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
These APIs enable the construction of one-step results based on `WorkReport`.


## ThenStep 
Similar to the single step construction process above, the `ThenStep` interface provides result based construction for composite flows. The interface is as follows:

```java
public interface ThenStep extends WorkFlow {

    WorkFlow then(Function<WorkReport, Work> fun);

    WorkFlow then(Work work);

}
```
The work unit that is wrapped by `then` is always executed unless the `policy` is `FAST_EXCEPTION`

An example of `ThenStep` is (For more examples, please refer to `test/java/ThenFlowTest`):
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
The result of this example is:
```
work2
work3
after parallel
work4
after conditional
```
You can use an infinite number of `then` methods to build workflows (not recommended), which will be executed sequentially like `SequentialWorkFlow`, and the return result policy still meets the corresponding workflow's `policy` policy.

## LastStep
`LastStep` is similar to `finally` in that it always executes its units of work, regardless of whether an exception occurs. The interface definition is as follows:

```java
public interface LastStep {

    WorkFlow lastly(Work... work);
}
```
An example of `LastStep` is
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
The result of this example is:
```
final
```

# Running a workflow

## WorkContext
The execution workflow can set corresponding contextual information, and the context in Easy Work is represented by `WorkContext`. 
This object contains a `Map` type property to store the parameter information required by the process.

The `WorkFlow.context()` method can be used to input contextual information into the workflow.

To create a `WorkContext`,You can refer to the following example:

```java
new WorkContext().put("param_a", "a").put("param_b", "b");
```
## WorkFlowEngine 
The `WorkFlowEngine` interface represents a workflow engine:

```java
public interface WorkFlowEngine {
    
    WorkReport run(WorkFlow workFlow, WorkContext workContext);
}
```

Easy Work provides a default implementation class for this interface, which you can build using the following methods:

```java
WorkFlowEngine workFlowEngine = aNewWorkFlowEngine();
```

You can then execute a `WorkFlow` by invoking the `run` method :

```java
WorkFlow workFlow = ... // create work flow
WorkReport workReport = aNewWorkFlowEngine().run(workFlow, new WorkContext());
```

## WorkFlow.execute
You can also simplify this operation by using the `WorkFlow.execute` method to execute the workflow:

```java
WorkFlow workFlow = ... // create work flow
WorkReport workReport = workFlow.execute(new WorkContext());
```

## AbstractWorkFlow.execute
You can also execute the workflow through the `AbstractWorkFlow.execute` method:

```java
WorkReport workReport = aNewSequentialFlow(work1, work2, work3).execute();
```

At this point, custom context information can be passed through the `context()` method.

## Pause Workflow
You can set a `break point` to pause the workflow, which can be decorated with `NamedPointWork` and set `break point`. Use the `execute (String point)` method to execute to the corresponding `break point`.

Ignore breakpoint execution through the ` Execute() ` method, and execute from the pause if the workflow has been paused.

A reference breakpoint example is (for more examples, please refer to `test/java/**Point`):

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
You can set breakpoints at `any` position in the 6 built-in processes of Easy Work, and you can set any number of breakpoints. 

Please note the following points:

1. The breakpoint only supports decorating the `Work` interface and does not support decorating the `WorkFlow` interface
2. Setting breakpoints for `Work` in `ParallelWorkFlow` will be ignored to ensure concurrent execution of `Work`
3. The `then` method also supports breakpoint execution
4. The `last` method does not support breakpoint execution because it always executes

# Get the workflow results

## DefaultWorkReport
When a `Work` is executed, it returns a `DefaultWorkReport` object that implements the `WorkReport` interface and inherits the `AbstractWorkReport` class, giving it the ability to step build.

Obtain the execution result of the corresponding `Work` through the `getResult()` method, and obtain the name of the corresponding `Work` through the `getWorkName()` method

## MultipleWorkReport

When the built-in `WorkFlow` returns a `WorkReport` , the `WorkReport` <b>inherits</b> `MultipleWorkReport`. A `MultipleWorkReport` may have multiple results, all of which are included in the `reports` property. 

It provides a simple method to retrieve the corresponding results based on the index and convert them to target class:
```java
public <T> T getResult(int index, Class<T> clazz) {
    return (T) getResult().get(index);
}


public <T> Collection<T> getResultCollection(int index, Class<T> clazz) {
    return (Collection<T>) getResult(index, clazz);
}
```
An example of `MultipleWorkReport` is:
```java
// the ParallelWorkReport inherits the MultipleWorkReport
ParallelWorkReport report = aNewParallelFlow(countWork, dataWork).execute();
Integer count = report.getResult(0, Integer.class);
List<Order> applies = Lists.newArrayList(report.getResultCollection(1, Order.class));
```
The status of `MultipleWorkReport` follows the following rules:
1. If any result is `FAILED`, its status is' `FAILED`
2. If all results are `Completed`, then the status is `Completed`
3. If `reports` is empty, return `Completed`


## Trace result
`Trace` 'is a support for tracing the results of `workFlow`, which can be opened through the `trace (true)` method. 

The execution results of each `Work` in the workflow will be stored according to the mapping from `name` to `WorkReport` , and the corresponding result mapping will be obtained through the `getExecutedRReportMap()` method.

An example of a trace is (for more examples, please refer to 'test/Java/TraceTest'):
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

## Listener
You can configure the addition of listeners through the `aNamePointWork` class. As long as the `Work` is executed (or may fail to execute due to policies), the listener will always be called back.

An example of adding a listener is as follows:
```java
WorkExecuteListener listener = (DefaultWorkReport report, WorkContext workContext, Exception ex) -> {
    System.out.println(report.getStatus() == WorkStatus.COMPLETED ? "YES, SUCCESS" : "NO, FAILURE");
};
SequentialFlow flow = aNewSequentialFlow(a, aNamePointWork(b).addWorkExecuteListener(listener), c);
```
The listener provides three parameters:

1. The result returned by the `Work` can be used to obtain the execution status, and the success can be determined based on the status
2. `Context` information during the execution of the `Work`
3. The `exception` information of Work execution, exception only has a value when the execution fails

