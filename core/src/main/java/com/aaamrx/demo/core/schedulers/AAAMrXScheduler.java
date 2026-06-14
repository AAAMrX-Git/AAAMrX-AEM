package com.aaamrx.demo.core.schedulers;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaamrx.demo.core.config.SchedulerConfig;

@Component(
    service = Runnable.class,
    immediate = true
)
@Designate(ocd = SchedulerConfig.class)
public class AAAMrXScheduler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(AAAMrXScheduler.class);

    private int schedulerId;

    @Reference
    private Scheduler scheduler;

    @Activate
    @Modified
    protected void activate(SchedulerConfig config){
        schedulerId = config.schedulerName().hashCode();
        removeScheduler();

        if(config.enabled()){
            addScheduler(config);
            log.info("\n================ Scheduler Activated and Registered =============\n");
        }
        else{
            log.info("\n================ Scheduler Activated but Disabled via Configurations =============\n");
        }
    }

    @Deactivate
    protected void deactivate(SchedulerConfig config){
        removeScheduler();
        log.info("\n================ Scheduler Deactivated and Unregistered =============\n");
    }

    protected void removeScheduler(){
        scheduler.unschedule(String.valueOf(schedulerId));
    }

    protected void addScheduler(SchedulerConfig config){
        ScheduleOptions options = scheduler.EXPR(config.cronExpression());
        options.name(String.valueOf(schedulerId));
        options.canRunConcurrently(false);
        scheduler.schedule(this, options);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        log.info("\n================ Scheduler Run Started =============\n");
    }
    
}
