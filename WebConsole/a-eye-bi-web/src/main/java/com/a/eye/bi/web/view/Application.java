package com.a.eye.bi.web.view;

import com.a.eye.common.web.common.trace.ServiceType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author pengysh
 */
@JsonSerialize(using = ApplicationSerializer.class)
public final class Application {
    private final String name;
    private final ServiceType serviceType;
    private final short code;

    public Application(String name, ServiceType serviceType) {
        if (name == null) {
            throw new NullPointerException("name must not be null. serviceType=" + serviceType);
        }
        if (serviceType == null) {
            throw new NullPointerException("serviceType must not be null. name=" + name);
        }
        this.name = name;
        this.serviceType = serviceType;
        this.code = serviceType.getCode();
    }


    public String getName() {
        return name;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public short getServiceTypeCode() {
        return serviceType.getCode();
    }

    public short getCode() {
        return code;
    }

    public boolean equals(String thatName, ServiceType thatServiceType) {
        if (thatName == null) {
            throw new NullPointerException("thatName must not be null");
        }
        if (thatServiceType == null) {
            throw new NullPointerException("thatServiceType must not be null");
        }
        if (serviceType != thatServiceType) return false;
        if (!name.equals(thatName)) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Application that = (Application) o;

        if (serviceType != that.serviceType) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + serviceType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name + "(" + serviceType + ":" + code + ")";
    }
}
