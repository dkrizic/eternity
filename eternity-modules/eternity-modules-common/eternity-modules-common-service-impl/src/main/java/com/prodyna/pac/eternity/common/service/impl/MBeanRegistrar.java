package com.prodyna.pac.eternity.common.service.impl;

import com.prodyna.pac.eternity.common.profiling.ProfilingMXBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Register MBeans at the MBeanServer
 */
@Startup
@Singleton
public class MBeanRegistrar {

    /**
     * the managed bean server
     */
    @Inject
    private MBeanServer mBeanServer;

    /**
     * the profiling bean
     */
    @Inject
    private ProfilingMXBean profilingMXBean;

    /**
     * the object name to use to register the ProfilingMXBean to the MBeanServer. The bean cannot store the name since
     * its already destroyed when this PreDestroy is called
     */
    private ObjectName profilingMXBeanObjectName;

    /**
     * registers this MBean to JMX
     */
    @PostConstruct
    public void registerInJMX() {

        try {
            this.profilingMXBeanObjectName =
                    new ObjectName("com.prodyna.pac.eternity.common.profiling.impl:type=ProfilingMXBeanImpl");
            this.mBeanServer.registerMBean(profilingMXBean, this.profilingMXBeanObjectName);

        } catch (final JMException e) {
            throw new IllegalStateException("Problem during registration of the profiling MBean", e);
        }

    }

    /**
     * unregisters this MBean from JMX
     */
    @PreDestroy
    public void unregisterFromJMX() {

        try {

            this.mBeanServer.unregisterMBean(this.profilingMXBeanObjectName);

        } catch (final JMException e) {
            throw new IllegalStateException("Problem during unregistration of the profiling MBean", e);
        }

    }

}
