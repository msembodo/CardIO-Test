package com.idemia.tec.jkt.cardiotest.model.customapdu;

public class Verify2g {

    private Verify2gChv1 verify2gChv1;
    private Verify2gChv2 verify2gChv2;
    private Verify2gAdm1 verify2gAdm1;
    private Verify2gAdm2 verify2gAdm2;
    private Verify2gAdm3 verify2gAdm3;
    private Verify2gAdm4 verify2gAdm4;

    public Verify2g() {}

    public Verify2g(Verify2gChv1 verify2gChv1, Verify2gChv2 verify2gChv2, Verify2gAdm1 verify2gAdm1,
                    Verify2gAdm2 verify2gAdm2, Verify2gAdm3 verify2gAdm3, Verify2gAdm4 verify2gAdm4) {
        this.verify2gChv1 = verify2gChv1;
        this.verify2gChv2 = verify2gChv2;
        this.verify2gAdm1 = verify2gAdm1;
        this.verify2gAdm2 = verify2gAdm2;
        this.verify2gAdm3 = verify2gAdm3;
        this.verify2gAdm4 = verify2gAdm4;
    }

    public Verify2gChv1 getVerify2gChv1() { return verify2gChv1; }
    public void setVerify2gChv1(Verify2gChv1 verify2gChv1) { this.verify2gChv1 = verify2gChv1; }
    public Verify2gChv2 getVerify2gChv2() { return verify2gChv2; }
    public void setVerify2gChv2(Verify2gChv2 verify2gChv2) { this.verify2gChv2 = verify2gChv2; }
    public Verify2gAdm1 getVerify2gAdm1() { return verify2gAdm1; }
    public void setVerify2gAdm1(Verify2gAdm1 verify2gAdm1) { this.verify2gAdm1 = verify2gAdm1; }
    public Verify2gAdm2 getVerify2gAdm2() { return verify2gAdm2; }
    public void setVerify2gAdm2(Verify2gAdm2 verify2gAdm2) { this.verify2gAdm2 = verify2gAdm2; }
    public Verify2gAdm3 getVerify2gAdm3() { return verify2gAdm3; }
    public void setVerify2gAdm3(Verify2gAdm3 verify2gAdm3) { this.verify2gAdm3 = verify2gAdm3; }
    public Verify2gAdm4 getVerify2gAdm4() { return verify2gAdm4; }
    public void setVerify2gAdm4(Verify2gAdm4 verify2gAdm4) { this.verify2gAdm4 = verify2gAdm4; }

}
