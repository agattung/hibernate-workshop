package com.lps.system;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lps.constants.LpsConstants;

public class EnvironmentDetectionSystem {

    private static Logger LOGGER = LoggerFactory.getLogger(EnvironmentDetectionSystem.class);

    private static String[] LOCAL_IP_RANGE = new String[]{"0.", "127.","172", "192","169.","10."};
    
    private String envDetectIpAddress;
    private String hostname;
    private String appProfile;

    private static EnvironmentDetectionSystem INSTANCE;

    static {
        INSTANCE = new EnvironmentDetectionSystem();
    }

    public static EnvironmentDetectionSystem getInstance() {
        return INSTANCE;
    }

    private EnvironmentDetectionSystem() {

        registerShutdownHook();

        // just in case set Timezone to UTC here
        System.setProperty("user.timezone","GMT");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        DateTimeZone.setDefault(DateTimeZone.UTC);

        try {
            envDetectIpAddress = retrieveInternetIpAddress();
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            String msg = "Could not detect environment because of exception. Application startup will not be initiated.";
            LOGGER.error(msg, e);
            throw new RuntimeException(e);
        }
        LOGGER.info("Env detection IP Address: " + envDetectIpAddress);
        LOGGER.info("Env detection Hostname: " + hostname);
    }

    public void detectEnvironment() {
        try {
            // 1. app_profile already set?
            appProfile = System.getProperty(LpsConstants.APPLICATION_PROFILE_KEY);
            if (appProfile == null) {
                appProfile = System.getenv(LpsConstants.APPLICATION_PROFILE_KEY);
            }

            String msg;

            if (appProfile == null) {
                appProfile = determineProfileByEnvironment();
                msg = LpsConstants.APPLICATION_PROFILE_KEY + " was not explicitly set. Environment detection resolved to profile '" + appProfile + "'.";
            } else {
                msg = LpsConstants.APPLICATION_PROFILE_KEY + " already explicitly set to '" + appProfile + "'. No environment detection needed.";
            }

            setProfile(appProfile);

            LOGGER.warn("*****" + msg + "*****");
        } finally {
            //GtixRequestContext.removeCurrent();
            // removeCurrent will be performed at the end of the boot-process
            // at the moment this is in GtixCookieFilter
        }
    }

    public String getAppProfile() {
        return appProfile;
    }

    public String getDetectedIpAddress() {
        return envDetectIpAddress;
    }

    public String getHostname() {
        return hostname;
    }

    private String retrieveInternetIpAddress() throws SocketException {

        String tempAddressString = null;

        for (Enumeration<NetworkInterface> ifaces =
                     NetworkInterface.getNetworkInterfaces();
             ifaces.hasMoreElements(); )
        {
            NetworkInterface iface = ifaces.nextElement();
            if (iface.isLoopback() || iface.isVirtual()) {
                continue;
            }

            for (Enumeration<InetAddress> addresses =
                         iface.getInetAddresses();
                 addresses.hasMoreElements(); )
            {
                InetAddress address = addresses.nextElement();
                // currently (only) IP4 is supported in this class
                if ( address instanceof Inet6Address) {
                    continue;
                }
                String currentAddressString = address.getHostAddress();
                String msg = "Found ip address " + currentAddressString + " for network interface " + iface.getDisplayName();

                if (tempAddressString == null || (isIpInRange(tempAddressString, LOCAL_IP_RANGE) && !isIpInRange(currentAddressString, LOCAL_IP_RANGE))) {
                    msg += ". Use it as env detect ip address.";
                    tempAddressString = currentAddressString;
                } else {
                    msg += ". Keep current env detect ip address " + tempAddressString + ".";
                }
                LOGGER.debug(msg);
            }
        }
        LOGGER.info("Use " + tempAddressString + " as ip address for environment detection.");

        return tempAddressString;
    }

    private void setProfile(String appProfile) {
        // do we have already some profiles set?
        String existingSpringProfiles = getExistingSpringProfiles();
        if (!StringUtils.isBlank(existingSpringProfiles)) {
            existingSpringProfiles += ", ";
        } else {
            existingSpringProfiles = "";
        }

        String[] profileParts = appProfile.split("-");
        String springProfile= profileParts[0];
        existingSpringProfiles += springProfile;
        LOGGER.info("Mapped appProfile '" + appProfile + "' to Spring profile '" + springProfile + "'. Cumulated Spring Profiles: " + existingSpringProfiles);
        System.setProperty(LpsConstants.APPLICATION_PROFILE_KEY, appProfile);
        System.setProperty("spring.profiles.active", existingSpringProfiles);
        System.setProperty("SPRING_PROFILES_ACTIVE", existingSpringProfiles);
    }

    private String getExistingSpringProfiles() {
        String profile = System.getProperty("spring.profiles.active");
        if ( profile == null) {
            profile = System.getProperty("SPRING_PROFILES_ACTIVE");
        }
        if ( profile == null) {
            profile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        return profile;
    }

    private String determineProfileByEnvironment() {
        dumpProperties();

        String profile;

        String loweredHostName = hostname.toLowerCase();
        		
        if (loweredHostName.startsWith("lpws") || loweredHostName.startsWith("lpnb")) {
        	profile="oracle";
        } else if (loweredHostName.startsWith("agws") || loweredHostName.startsWith("agnb")) {
        	profile="mysql";
        } else {
            dumpProperties();
            throw new IllegalStateException("Can't determine environment using IP-address checks et al. Please check.");
        }
        return profile;
    }

    /**
     *
     * @param ipAddress
     * @param ipPrefixes
     * @return <code>true</code> if ipAddress starts with one of the string given in ipPrefixes
     */
    private boolean isIpInRange(String ipAddress, String[] ipPrefixes) {
        for (String prefix : ipPrefixes) {
            if (ipAddress.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void dumpProperties() {
        if (LOGGER.isTraceEnabled()) {
            Map<String, String> envProperties = System.getenv();
            String msg = "Environment: " + LpsConstants.LINE_SEPARATOR;
            for (Map.Entry<String, String> entry : envProperties.entrySet()) {
                msg += entry.getKey() + "=" + entry.getValue() + LpsConstants.LINE_SEPARATOR;
            }
            LOGGER.trace(msg);

            Properties sysProps = System.getProperties();
            msg = "System properties: " + LpsConstants.LINE_SEPARATOR;
            for (Map.Entry<Object, Object> entry : sysProps.entrySet()) {
                msg += entry.getKey() + "=" + entry.getValue() + LpsConstants.LINE_SEPARATOR;
            }
            LOGGER.trace(msg);
        }
    }

    private void registerShutdownHook() {
        Runtime runtime = Runtime.getRuntime();

        synchronized (runtime) {
            Thread shutdownThread = new Thread() {
                public void run() {
                	EnvironmentDetectionSystem eds = EnvironmentDetectionSystem.getInstance();
                    LOGGER.info("Starting shutdown (hook).");
                }
            };
            runtime.addShutdownHook(shutdownThread);
        }
    }
}

