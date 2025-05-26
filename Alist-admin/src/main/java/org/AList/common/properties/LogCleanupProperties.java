package org.AList.common.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogCleanupProperties {

    @Autowired
    private Environment environment;

    public boolean isEnabled() {
        return environment.getProperty("admin.log.cleanup.enabled", Boolean.class, false);
    }

    public int getRetentionDays() {
        return environment.getProperty("admin.log.cleanup.retention-days", Integer.class, 30);
    }

    public int getBatchSize() {
        return environment.getProperty("admin.log.cleanup.batch-size", Integer.class, 1000);
    }

    public String getSchedule() {
        return environment.getProperty("admin.log.cleanup.schedule", "0 0 2 * * *");
    }

    public boolean isDryRun() {
        return environment.getProperty("admin.log.cleanup.dry-run", Boolean.class, false);
    }
}