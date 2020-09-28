package com.idemia.tec.jkt.cardiotest.model.customapdu;

public class Verify3g {

    private Verify3gGlobalPin1 verify3gGlobalPin1;
    private Verify3gLocalPin1 verify3gLocalPin1;
    private Verify3gAdm1 verify3gAdm1;
    private Verify3gAdm2 verify3gAdm2;
    private Verify3gAdm3 verify3gAdm3;
    private Verify3gAdm4 verify3gAdm4;

    public Verify3g() {}

    public Verify3g(Verify3gGlobalPin1 verify3gGlobalPin1, Verify3gLocalPin1 verify3gLocalPin1,
                    Verify3gAdm1 verify3gAdm1, Verify3gAdm2 verify3gAdm2, Verify3gAdm3 verify3gAdm3,
                    Verify3gAdm4 verify3gAdm4) {
        this.verify3gGlobalPin1 = verify3gGlobalPin1;
        this.verify3gLocalPin1 = verify3gLocalPin1;
        this.verify3gAdm1 = verify3gAdm1;
        this.verify3gAdm2 = verify3gAdm2;
        this.verify3gAdm3 = verify3gAdm3;
        this.verify3gAdm4 = verify3gAdm4;
    }

    public Verify3gGlobalPin1 getVerify3gGlobalPin1() { return verify3gGlobalPin1; }
    public void setVerify3gGlobalPin1(Verify3gGlobalPin1 verify3gGlobalPin1) { this.verify3gGlobalPin1 = verify3gGlobalPin1; }
    public Verify3gLocalPin1 getVerify3gLocalPin1() { return verify3gLocalPin1; }
    public void setVerify3gLocalPin1(Verify3gLocalPin1 verify3gLocalPin1) { this.verify3gLocalPin1 = verify3gLocalPin1; }
    public Verify3gAdm1 getVerify3gAdm1() { return verify3gAdm1; }
    public void setVerify3gAdm1(Verify3gAdm1 verify3gAdm1) { this.verify3gAdm1 = verify3gAdm1; }
    public Verify3gAdm2 getVerify3gAdm2() { return verify3gAdm2; }
    public void setVerify3gAdm2(Verify3gAdm2 verify3gAdm2) { this.verify3gAdm2 = verify3gAdm2; }
    public Verify3gAdm3 getVerify3gAdm3() { return verify3gAdm3; }
    public void setVerify3gAdm3(Verify3gAdm3 verify3gAdm3) { this.verify3gAdm3 = verify3gAdm3; }
    public Verify3gAdm4 getVerify3gAdm4() { return verify3gAdm4; }
    public void setVerify3gAdm4(Verify3gAdm4 verify3gAdm4) { this.verify3gAdm4 = verify3gAdm4; }

}
