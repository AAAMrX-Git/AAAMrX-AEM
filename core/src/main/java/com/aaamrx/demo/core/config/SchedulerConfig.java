package com.aaamrx.demo.core.config;

import org.osgi.service.metatype.annotations.*;

@ObjectClassDefinition(
    name = "AAAMrX Scheduler Configuration"
)
public @interface SchedulerConfig {

    @AttributeDefinition(
        name = "Scheduler Name",
        description = "Name of the scheduler",
        type = AttributeType.STRING
    )
    public String schedulerName() default "Custom Sling Scheduler";

    @AttributeDefinition(
        name = "Cron Expression",
        description = "Cron Expression for scheduler",
        type = AttributeType.STRING
    )
    public String cronExpression() default "0/30 * * * * ?";

    @AttributeDefinition(
        name = "Enabled",
        description = "Check to activate the scheduler execution"
    )
    public boolean enabled() default false;
}
