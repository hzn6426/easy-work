package com.baomibing.work.work;

import com.baomibing.work.context.WorkContext;
import lombok.Getter;

import java.util.UUID;

public class NamedPointWork implements Work {

    private final Work work;
    @Getter
    private String point;
    @Getter
    private String name = UUID.randomUUID().toString();
    @Getter
    private boolean beExecuted = true;

    public static NamedPointWork aNamePointWork(Work work) {
        return new NamedPointWork(work);
    }

    public NamedPointWork(Work work) {
        this.work = work;
    }

    public NamedPointWork point(String point) {
        this.point = point;
        return this;
    }

    public NamedPointWork named(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Object execute(WorkContext context) {
        this.beExecuted = true;
        return work.execute(context);
    }
}
