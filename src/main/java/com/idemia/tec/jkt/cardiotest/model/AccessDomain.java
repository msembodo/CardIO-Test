package com.idemia.tec.jkt.cardiotest.model;

public class AccessDomain {
    private boolean useAlways;
    private boolean useIsc1;
    private boolean useIsc2;
    private boolean useIsc3;
    private boolean useIsc4;
    private boolean useGPin1;
    private boolean useLPin1;
    private boolean useBadCaseAlways;
    private boolean useBadCaseIsc1;
    private boolean useBadCaseIsc2;
    private boolean useBadCaseIsc3;
    private boolean useBadCaseIsc4;
    private boolean useBadCaseGPin1;
    private boolean useBadCaseLPin1;

    public AccessDomain(){}

    public AccessDomain(boolean useAlways, boolean useIsc1, boolean useIsc2,boolean useIsc3,boolean useIsc4,
                        boolean useGPin1, boolean useLPin1, boolean useBadCaseAlways, boolean useBadCaseIsc1, boolean useBadCaseIsc2,boolean useBadCaseIsc3,boolean useBadCaseIsc4,
                        boolean useBadCaseGPin1, boolean useBadCaseLPin1){
        this.useAlways = useAlways;
        this.useIsc1 = useIsc1;
        this.useIsc2 = useIsc2;
        this.useIsc3 = useIsc3;
        this.useIsc4 = useIsc4;
        this.useGPin1 = useGPin1;
        this.useLPin1 = useLPin1;
        this.useBadCaseAlways = useBadCaseAlways;
        this.useBadCaseIsc1 = useBadCaseIsc1;
        this.useBadCaseIsc2 = useBadCaseIsc2;
        this.useBadCaseIsc3 = useBadCaseIsc3;
        this.useBadCaseIsc4 = useBadCaseIsc4;
        this.useBadCaseGPin1 = useGPin1;
        this.useBadCaseLPin1 = useLPin1;
    }

    public boolean isUseAlways() {
        return useAlways;
    }

    public void setUseAlways(boolean useAlways) {
        this.useAlways = useAlways;
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

    public boolean isUseBadCaseAlways() {
        return useBadCaseAlways;
    }

    public void setUseBadCaseAlways(boolean useBadCaseAlways) {
        this.useBadCaseAlways = useBadCaseAlways;
    }

    public boolean isUseBadCaseIsc1() {
        return useBadCaseIsc1;
    }

    public void setUseBadCaseIsc1(boolean useBadCaseIsc1) {
        this.useBadCaseIsc1 = useBadCaseIsc1;
    }

    public boolean isUseBadCaseIsc2() {
        return useBadCaseIsc2;
    }

    public void setUseBadCaseIsc2(boolean useBadCaseIsc2) {
        this.useBadCaseIsc2 = useBadCaseIsc2;
    }

    public boolean isUseBadCaseIsc3() {
        return useBadCaseIsc3;
    }

    public void setUseBadCaseIsc3(boolean useBadCaseIsc3) {
        this.useBadCaseIsc3 = useBadCaseIsc3;
    }

    public boolean isUseBadCaseIsc4() {
        return useBadCaseIsc4;
    }

    public void setUseBadCaseIsc4(boolean useBadCaseIsc4) {
        this.useBadCaseIsc4 = useBadCaseIsc4;
    }

    public boolean isUseBadCaseGPin1() {
        return useBadCaseGPin1;
    }

    public void setUseBadCaseGPin1(boolean useBadCaseGPin1) {
        this.useBadCaseGPin1 = useBadCaseGPin1;
    }

    public boolean isUseBadCaseLPin1() {
        return useBadCaseLPin1;
    }

    public void setUseBadCaseLPin1(boolean useBadCaseLPin1) {
        this.useBadCaseLPin1 = useBadCaseLPin1;
    }
}
