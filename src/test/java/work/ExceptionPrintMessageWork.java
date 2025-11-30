package work;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.work.Work;

public class ExceptionPrintMessageWork implements Work {

    @Override
    public Object execute(WorkContext context) {
        throw new RuntimeException("The Exception Work");
    }
}
