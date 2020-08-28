package com.idemia.tec.jkt.cardiotest.model;

public class ConnectionParameters {

    private boolean useDestinationAddress;
    private String destinationAddress;
    private boolean useBufferSize;
    private int bufferSize;
    private boolean useNetworkAccessName;
    private String networkAcessName;
    private boolean useTransportLevel;
    private int transportLevel;
    private String port;

    public ConnectionParameters() {}

    public ConnectionParameters(boolean useDestinationAddress, String destinationAddress, boolean useBufferSize, int bufferSize, boolean useNetworkAccessName, String networkAcessName, boolean useTransportLevel, int transportLevel, String port) {
        this.useDestinationAddress = useDestinationAddress;
        this.destinationAddress = destinationAddress;
        this.useBufferSize = useBufferSize;
        this.bufferSize = bufferSize;
        this.useNetworkAccessName = useNetworkAccessName;
        this.networkAcessName = networkAcessName;
        this.useTransportLevel = useTransportLevel;
        this.transportLevel = transportLevel;
        this.port = port;
    }

    public boolean useDestinationAddress() {
        return useDestinationAddress;
    }

    public void setUseDestinationAddress(boolean useDestinationAddress) {
        this.useDestinationAddress = useDestinationAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public boolean useBufferSize() {
        return useBufferSize;
    }

    public void setUseBufferSize(boolean useBufferSize) {
        this.useBufferSize = useBufferSize;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean useNetworkAccessName() {
        return useNetworkAccessName;
    }

    public void setUseNetworkAccessName(boolean useNetworkAccessName) {
        this.useNetworkAccessName = useNetworkAccessName;
    }

    public String getNetworkAcessName() {
        return networkAcessName;
    }

    public void setNetworkAcessName(String networkAcessName) {
        this.networkAcessName = networkAcessName;
    }

    public boolean useTransportLevel() {
        return useTransportLevel;
    }

    public void setUseTransportLevel(boolean useTransportLevel) {
        this.useTransportLevel = useTransportLevel;
    }

    public int getTransportLevel() {
        return transportLevel;
    }

    public void setTransportLevel(int transportLevel) {
        this.transportLevel = transportLevel;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
