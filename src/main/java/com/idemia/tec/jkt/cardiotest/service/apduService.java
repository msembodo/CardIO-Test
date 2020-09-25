package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class apduService {

    @Autowired private RootLayoutController root;

    public String verifyGpin() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gGlobalPin1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gGlobalPin1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gGlobalPin1().getP3() + " %";
    }

    public String verifyLpin() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gLocalPin1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gLocalPin1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gLocalPin1().getP3() + " %";
    }

    public String verify3gAdm1() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm1().getP3() + " %";
    }

    public String verify3gAdm2() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm2().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm2().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm2().getP3() + " %";
    }

    public String verify3gAdm3() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm3().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm3().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm3().getP3() + " %";
    }

    public String verify3gAdm4() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm4().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm4().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm4().getP3() + " %";
    }

    public String verifyPin1() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv1().getP3() + " %";
    }

    public String verifyPin2() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv2().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv2().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv2().getP3() + " %";
    }

    public String verify2gAdm1() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm1().getP3() + " %";
    }

    public String verify2gAdm2() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm2().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm2().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm2().getP3() + " %";
    }

    public String verify2gAdm3() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm3().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm3().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm3().getP3() + " %";
    }

    public String verify2gAdm4() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm4().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm4().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm4().getP3() + " %";
    }

}
