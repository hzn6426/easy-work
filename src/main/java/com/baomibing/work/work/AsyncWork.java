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
package com.baomibing.work.work;

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.listener.WorkExecuteListener;
import com.baomibing.work.report.DefaultWorkReport;
import com.baomibing.work.util.Checker;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;

import static com.baomibing.work.report.DefaultWorkReport.aNewWorkReport;

/**
 * An Async Work which execute async and ignore the result.
 *
 * @author zening (316279829@qq.com)
 */
public class AsyncWork implements Work {

    //thread pool
    private static ExecutorService executor = null;

    @Getter
    private boolean beDone = false;

    private boolean autoShutdown = false;

    @Setter
    private WorkExecuteListener  workExecuteListener;

    @Setter
    private  String workName;
    @Getter
    private Work work;

    private AsyncWork(Work work) {
        this.work = work;

        if (executor == null) {
            executor = initExecutor();
        }
    }

    public static AsyncWork aNewAsyncWork(Work work) {
        return new AsyncWork(work);
    }

    @Override
    public DefaultWorkReport execute(WorkContext context) {

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            DefaultWorkReport report = new DefaultWorkReport();
            try {
                Object result = work.execute(context);
                if (workExecuteListener != null) {
                    report.setResult(result).setWorkContext(context).setWorkName(workName);
                    workExecuteListener.onWorkExecute(report, context, null);
                }
            } catch (Exception e) {
                if (workExecuteListener != null) {
                    report.setResult(null).setError(e).setStatus(WorkStatus.FAILED).setWorkContext(context).setWorkName(workName);
                    workExecuteListener.onWorkExecute(report, context, e);
                }
            } finally {
                this.beDone = true;
                if (autoShutdown) {
                    executor.shutdown();
                }
            }
        }, executor);

        return aNewWorkReport();
    }

    private ThreadPoolExecutor initExecutor() {
        final int corePoolSize = 10;
        final int maxPoolSize = 20;
        final int queueCapacity = 50;
        final int keepAliveTime = 30;
        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity, true);
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
            queue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public AsyncWork withExecutor(ExecutorService theExecutor) {
        if (Checker.BeNotNull(theExecutor)) {
            executor = theExecutor;
        }
        return this;
    }

    public AsyncWork withAutoShutDown(boolean beAutoShutdown) {
        this.autoShutdown = beAutoShutdown;
        return this;
    }
}
