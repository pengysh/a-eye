package com.a.eye.bi.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.a.eye.bi.web.view.Application;
import com.a.eye.common.web.common.service.ServiceTypeRegistryService;
import com.a.eye.common.web.common.trace.ServiceType;

/**
 * @author emeroad
 */
@Component
public class DefaultApplicationFactory implements ApplicationFactory {

    @Autowired
    private ServiceTypeRegistryService registry;

    @Override
    public Application createApplication(String applicationName, short serviceTypeCode) {
        if (applicationName == null) {
            throw new NullPointerException("applicationName must not be null");
        }

        final ServiceType serviceType = registry.findServiceType(serviceTypeCode);
        return new Application(applicationName, serviceType);
    }

    @Override
    public Application createApplication(String applicationName, ServiceType serviceType) {
        return new Application(applicationName, serviceType);
    }

    @Override
    public Application createApplicationByTypeName(String applicationName, String serviceTypeName) {
        if (applicationName == null) {
            throw new NullPointerException("applicationName must not be null");
        }
        if (serviceTypeName == null) {
            throw new NullPointerException("serviceTypeName must not be null");
        }

        final ServiceType serviceType = registry.findServiceTypeByName(serviceTypeName);
        return new Application(applicationName, serviceType);
    }

}
