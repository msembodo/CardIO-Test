package com.idemia.tec.jkt.cardiotest.model;

public class AccessDomain {
    private boolean useIsc1;
    private boolean useIsc2;
    private boolean useIsc3;
    private boolean useIsc4;
    private boolean useGPin1;
    private boolean useLPin1;

    public AccessDomain(){}

    public AccessDomain(boolean useIsc1, boolean useIsc2,boolean useIsc3,boolean useIsc4,
                        boolean useGPin1, boolean useLPin1){
        this.useIsc1 = useIsc1;
        this.useIsc2 = useIsc2;
        this.useIsc3 = useIsc3;
        this.useIsc4 = useIsc4;
        this.useGPin1 = useGPin1;
        this.useLPin1 = useLPin1;
    }

    public boolean isUseIsc1() {
        return useIsc1;
    }

    public void setUseIsc1(boolean useIsc1) {
        this.useIsc1 = useIsc1;
    }

    public boolean isUseIsc2() {
        return useIsc2;
    }

    public void setUseIsc2(boolean useIsc2) {
        this.useIsc2 = useIsc2;
    }

    public boolean isUseIsc3() {
        return useIsc3;
    }

    public void setUseIsc3(boolean useIsc3) {
        this.useIsc3 = useIsc3;
    }

    public boolean isUseIsc4() {
        return useIsc4;
    }

    public void setUseIsc4(boolean useIsc4) {
        this.useIsc4 = useIsc4;
    }

    public boolean isUseGPin1() {
        return useGPin1;
    }

    public void setUseGPin1(boolean useGPin1) {
        this.useGPin1 = useGPin1;
    }

    public boolean isUseLPin1() {
        return useLPin1;
    }

    public void setUseLPin1(boolean useLPin1) {
        this.useLPin1 = useLPin1;
    }
}
