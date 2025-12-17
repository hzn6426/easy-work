package work;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.work.Work;

import java.util.Random;

public class ChooseMinPrintMessageWork implements Work {

    private int min;
    private int max;

    public ChooseMinPrintMessageWork(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer execute(WorkContext workContext) {
        int minimum = min, maximum = max;
        Random rand = new Random();
        Integer random = minimum + rand.nextInt((maximum - minimum) + 1);
        System.out.println("random:" + random);
        return random;
    }
}
