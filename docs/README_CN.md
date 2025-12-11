***

<div align="center">
    <b><em>Easy Work</em></b><br>
    基于Java&trade;的简单、易用、傻瓜式的工作流引擎
</div>

<div align="center">

</div>

***

📖 [English](../README.md) | 📖 中文

## Easy Work 是什么?

Easy Work 是一个用于 Java 的工作流引擎。它提供了简洁的 API 和 构建模块，便于创建和运行可组合的工作流。

在Easy Work中，工作单元由`Work`接口表示，工作流则由`WorkFlow`接口表示。Easy  Work 提供了 `WorkFlow` 接口的6种实现方式：

<p align="center">
    <img src="../img/workflow.png" width="70%">
</p>

这些是使用Easy Work创建工作流时所需了解的唯一基本流程。

你无需学习复杂的符号或概念，只需掌握几个易于理解的自然API即可。

## 如何使用 ？
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

这不是一个非常有用的工作流，只是为了让你了解如何使用Easy Work而编写工作流。

你可以在 `test/java` 中 查看更多的测试用例。

更详细的信息，请参考[wiki](WIKI_CN.md)

<b>注意：此项目中 API 的部分命名方式 参考 <a href="https://github.com/j-easy/easy-flows">easy-flow</a></b>，一个非常简单易用的流程引擎。

## 许可证

Easy Work 基于 Apache License Version 2.0