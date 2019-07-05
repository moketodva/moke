package cn.moke.generator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author moke
 */
@Configuration
public class ScheduledConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        AtomicInteger mCount = new AtomicInteger(1);
        scheduledTaskRegistrar.setScheduler(new ScheduledThreadPoolExecutor(3, (Runnable r) -> new Thread(r,"Scheduled #" + mCount.getAndIncrement())));
    }
}
