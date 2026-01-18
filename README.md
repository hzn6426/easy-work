***

<div align="center">
    <b><em>Easy Work</em></b><br>
    The simple, easy-used, stupid workflow engine for Java™
</div>

<div align="center">

</div>

***

📖 English | 📖 [中文](docs/README_CN.md)

## What is Easy Work?

Easy Work is a workflow engine for Java. It provides concise APIs and building blocks for creating and running composable workflows. 

In Easy Work, work units are represented by the `Work` interface, while workflows are represented by the `WorkFlow` interface. 

Easy Work provides six implementation methods for the WorkFlow interface:

<p align="center">
    <img src="./img/workflow.png" width="70%">
</p>

Those are the only basic flows you need to know to start creating workflows with Easy Work.

You don't need to learn a complex notation or concepts, just a few natural APIs that are easy to think about.

## How does it work ?
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
    <img src="./img/example.png" width="70%">
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
## Pause Workflow
Now(from v1.0.5) the workflow support the `break point`, which can pause the workflow, do something 
then recovery the workflow. You can use it in `Any` position of the workflow.

For example, you can break the point at `c` work, and then recovery to execute.

This workflow can be illustrated as follows:
<p align="center">
    <img src="./img/point.png" width="70%">
</p>

This workflow can be implemented with the following snippet:
```java
SequentialFlow flow = aNewSequentialFlow(
    aNewRepeatFlow(a).times(3),
    aNewSequentialFlow(b,aNamePointWork(c).point("C_BREAK_POINT"),d),
    aNewConditionalFlow(
        aNewParallelFlow(e,f).withAutoShutDown(true)
    ).when(
        WorkReportPredicate.COMPLETED,
        g,
        h
    ),
    z
);
//execute to the break point
flow.execute("C_BREAK_POINT");
System.out.println("execute to the break point `C_BREAK_POINT`");
//recovery execute from the `C_BREAK_POINT` to the end.
flow.execute();
```
## Build workflow from json
Now (from version V1.0.8) EasyWork support JSON-based construction, allowing you to build workflows of arbitrary complexity using JSON.

Based on the example shown in the diagram above, the fragment for constructing a workflow using JSON is (example. json):
```json
{
  "type": "sequential",
  "works": [{
    "type": "repeat",
    "times": 3,
    "work": {
      "type": "work.PrintMessageWork",
      "message": "a"
    }
  },{
    "type": "sequential",
    "works": [{
      "type": "work.PrintMessageWork",
      "message": "b"
    },{
      "type": "work.PrintMessageWork",
      "message": "c"
    },{
      "type": "work.PrintMessageWork",
      "message": "d"
    }]
  },{
      "type": "conditional",
      "decide": {
        "type": "parallel",
        "autoShutdown": true,
        "works": [{
          "type": "work.PrintMessageWork",
          "message": "e"
        },{
          "type": "work.PrintMessageWork",
          "message": "f"
        }]
      },
      "predicate": {
        "left" : "$status",
        "operator": "eq",
        "right": "COMPLETED"
      },
      "trueWork": {
        "type": "work.PrintMessageWork",
        "message": "g"
      },
      "falseWork": {
        "type": "work.PrintMessageWork",
        "message": "h"
      }
  }],
  "then": {
    "type": "work.PrintMessageWork",
    "message": "z"
  }
}
```
You can deserialize the workflow using the following code and execute it (for more examples, please refer to test/Java/ReservializeTest)
```java
String json = ResourceReader.readJSON("json/example.json");
SequentialFlow sequentialFlow = (SequentialFlow) deserialize(json);
sequentialFlow.execute(new WorkContext());
```
This is not a very useful workflow, but just to give you an idea about how to write workflows with Easy Work.

You can view more test cases in `test/java`.

You can find more details about all of this in the [wiki](docs/WIKI.md)

<b>Note: Some of the naming conventions for the APIs in this project refer to <a href="https://github.com/j-easy/easy-flows"> Easy flow</a></b>, a very simple and easy-to-use process engine.</b>

## License

Easy Work is released under the Apache License Version 2.0