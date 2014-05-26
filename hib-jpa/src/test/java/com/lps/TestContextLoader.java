package com.lps;


import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.DelegatingSmartContextLoader;

import com.lps.system.EnvironmentDetectionSystem;

/**
 * ContextLoader to be used in tests.
 * Sets
 * 1) UTC timezone
 * 2) correct environment to be used by Spring.
 *
 */
public class TestContextLoader extends DelegatingSmartContextLoader {

    public static ApplicationContext STATIC_APPLICATION_CONTEXT;

    @Override
    public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {

        System.setProperty("user.timezone","GMT");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        DateTimeZone.setDefault(DateTimeZone.UTC);

        EnvironmentDetectionSystem envDetectService = EnvironmentDetectionSystem.getInstance();
        envDetectService.detectEnvironment();

        STATIC_APPLICATION_CONTEXT = super.loadContext(mergedConfig);
        return STATIC_APPLICATION_CONTEXT;
    }

}
