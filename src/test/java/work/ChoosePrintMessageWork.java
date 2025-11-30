package work;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.work.Work;

import java.util.Random;

public class ChoosePrintMessageWork implements Work {


    @Override
    public Integer execute(WorkContext workContext) {
        int minimum = 1, maximum = 10;
        Random rand = new Random();
        Integer random = minimum + rand.nextInt((maximum - minimum) + 1);
        System.out.println("random:" + random);
        return random;
    }
}
