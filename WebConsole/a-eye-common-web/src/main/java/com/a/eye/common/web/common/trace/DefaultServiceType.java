package com.a.eye.common.web.common.trace;

/**
 * @author emeroad
 * @author netspider
 * @author Jongho Moon
 */
class DefaultServiceType implements ServiceType {
    private final short code;
    private final String name;
    private final String desc;
    private final boolean terminal;

    // FIXME record statistics of only rpc call currently. so is it all right to chane into isRecordRpc()
    private final boolean recordStatistics;

    // whether or not print out api including destinationId
    private final boolean includeDestinationId;
    private final ServiceTypeCategory category;



    DefaultServiceType(int code, String name, String desc, ServiceTypeProperty... properties) {
        // code must be a short value but constructors accept int to make declaring ServiceType values more cleaner by removing casting to short.
        if (code > Short.MAX_VALUE || code < Short.MIN_VALUE) {
            throw new IllegalArgumentException("code must be a short value");
        }

        this.code = (short)code;
        this.name = name;
        this.desc = desc;

        this.category = ServiceTypeCategory.findCategory((short)code);

        boolean terminal = false;
        boolean recordStatistics = false;
        boolean includeDestinationId = false;
        
        for (ServiceTypeProperty property : properties) {
            switch (property) {
            case TERMINAL:
                terminal = true;
                break;
                
            case RECORD_STATISTICS:
                recordStatistics = true;
                break;
                
            case INCLUDE_DESTINATION_ID:
                includeDestinationId = true;
                break;
            default:
                throw new IllegalStateException("Unknown ServiceTypeProperty:" + property);
            }
        }
        
        this.terminal = terminal;
        this.recordStatistics = recordStatistics;
        this.includeDestinationId = includeDestinationId;
    }


    @Override
    public boolean isInternalMethod() {
        return this == INTERNAL_METHOD;
    }

    @Override
    public boolean isRpcClient() {
        return ServiceTypeCategory.RPC.contains(code);
    }

    // FIXME record statistics of only rpc call currently. so is it all right to chane into isRecordRpc()
    @Override
    public boolean isRecordStatistics() {
        return recordStatistics;
    }

    @Override
    public boolean isUnknown() {
        return this == ServiceType.UNKNOWN; // || this == ServiceType.UNKNOWN_CLOUD;
    }

    // return true when the service type is USER or can not be identified
    @Override
    public boolean isUser() {
        return this == ServiceType.USER;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public boolean isTerminal() {
        return terminal;
    }

    @Override
    public boolean isIncludeDestinationId() {
        return includeDestinationId;
    }

    @Override
    public ServiceTypeCategory getCategory() {
        return category;
    }

    @Override
    public HistogramSchema getHistogramSchema() {
        return category.getHistogramSchema();
    }

    @Override
    public boolean isWas() {
        return this.category == ServiceTypeCategory.SERVER;
    }
    
    @Override
    public String toString() {
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultServiceType that = (DefaultServiceType) o;

        return code == that.code;

    }

    @Override
    public int hashCode() {
        return (int) code;
    }

    public static boolean isWas(final short code) {
        return ServiceTypeCategory.SERVER.contains(code);
    }


}
