package work;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.work.Work;

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
