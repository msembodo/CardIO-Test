package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.Authentication;
import com.idemia.tec.jkt.cardiotest.model.SecretCodes;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScriptGeneratorServiceImpl implements ScriptGeneratorService {

    @Autowired
    private RootLayoutController root;

    @Override
    public StringBuilder generateAtr() {
        String composeAtrScript = ".CALL Mapping.txt\n"
            + ".CALL Options.txt\n\n"
            + ".POWER_ON /PROTOCOL_ON /NEGOTIATE_PROTOCOL [%ATR] (" + root.getRunSettings().getAtr().getStatus() + ")\n"
            + ".GET_PROTOCOL_PARAMETERS\n"
            + ".POWER_OFF";
        
        return new StringBuilder().append(composeAtrScript);
    }

    @Override
    public StringBuilder generateMapping() {
        StringBuilder mappings = new StringBuilder();
        mappings.append(".CALL ..\\variables.txt /LIST_OFF\n\n");
        for (VariableMapping mapping : root.getRunSettings().getVariableMappings()) {
            if (mapping.isFixed())
                mappings.append(String.format(".DEFINE %%%s  %s\n", mapping.getMappedVariable(), mapping.getValue()));
            else
                mappings.append(String.format(".DEFINE %%%s  %%%s\n", mapping.getMappedVariable(), mapping.getMccVariable()));
        }
        return mappings;
    }

    @Override
    public StringBuilder generateMilenageDeltaTest(Authentication authentication) {
        StringBuilder deltaTestBuffer = new StringBuilder();
        deltaTestBuffer.append(
            ".SET_BUFFER L " + authentication.getSqn() + "\n\n"
            + ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".DEFINE %RAND " + authentication.getRand() + "\n\n"
            + ".POWER_ON\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            deltaTestBuffer.append(
                "00 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n\n"
            );
        }
        deltaTestBuffer.append(
            ".DEFINE %DELTA " + authentication.getDelta() + "\n\n"
        );
        if (authentication.isIsimAuth()) {
            deltaTestBuffer.append(
                "; ISIM auth\n"
                + ".LOAD dll\\Milenage_AKA.dll\n"
                + ".LOAD dll\\Calcul.dll\n"
                + "; Case 1: AUTHENTICATE 3G CONTEXT AGAIN WITH CURRENT SQN (SEQ = 1, IND = 1)\n"
                + ".POWER_ON\n"
                + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
                + ".DEFINE %SQN L\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + runAlgo3gAgainCurrSqn(authentication)
                + "; Case 2: INCREASE SEQUENCE (SEQ = 2, IND = 1)\n"
                + ".POWER_ON\n"
                + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
                + ".DEFINE %SQN L\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + runAlgoSqnOkIsim(authentication)
                + "; Case 3: SYNC FAILED (SEQ = 3 + Delta, IND = 1)\n"
                + ".POWER_ON\n"
                + ".INCREASE_BUFFER L %DELTA\n"
                + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
                + ".DEFINE %SQN L\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + runAlgoSqnNokIsim(authentication)
                + "; Case 4: 3G ME in 3G context (Resync)\n"
                + ".POWER_ON\n"
                + ".DECREASE_BUFFER L %DELTA\n"
                + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
                + ".DEFINE %SQN L\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + runAlgoSqnOkIsim(authentication)
                + ".UNLOAD Milenage_AKA.dll\n"
                + ".UNLOAD Calcul.dll\n\n"
            );
        }
        deltaTestBuffer.append(
            "; Milenage Algo\n"
            + ".LOAD dll\\Milenage_AKA.dll\n"
            + ".LOAD dll\\Calcul.dll\n"
            + "; Case 1: AUTHENTICATE 3G CONTEXT AGAIN WITH CURRENT SQN (SEQ = 1, IND = 1)\n"
            + ".POWER_ON\n"
            + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
            + ".DEFINE %SQN L\n"
            + ".DEFINE %AMF " + authentication.getAmf() + "\n"
            + runAlgo3gAgainCurrSqn(authentication)
            + "; Case 2: INCREASE SEQUENCE (SEQ = 2, IND = 1)\n"
            + ".POWER_ON\n"
            + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
            + ".DEFINE %SQN L\n"
            + ".DEFINE %AMF " + authentication.getAmf() + "\n"
            + runAlgoSqnOk(authentication)
            + "; Case 3: SYNC FAILED (SEQ = 3 + Delta, IND = 1)\n"
            + ".POWER_ON\n"
            + ".INCREASE_BUFFER L %DELTA\n"
            + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
            + ".DEFINE %SQN L\n"
            + ".DEFINE %AMF " + authentication.getAmf() + "\n"
            + runAlgoSqnNok(authentication)
            + "; Case 4: 3G ME in 3G context (Resync)\n"
            + ".POWER_ON\n"
            + ".DECREASE_BUFFER L %DELTA\n"
            + ".INCREASE_BUFFER L 00 00 00 00 00 20\n"
            + ".DEFINE %SQN L\n"
            + ".DEFINE %AMF " + authentication.getAmf() + "\n"
            + runAlgoSqnOk(authentication)
            + ".UNLOAD Milenage_AKA.dll\n"
            + ".UNLOAD Calcul.dll\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            deltaTestBuffer.append(
                "\n00 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n\n"
            );
        }
        deltaTestBuffer.append(
            ".POWER_OFF\n"
        );
        return deltaTestBuffer;
    }

    @Override
    public StringBuilder generateMilenageSqnMax(Authentication authentication) {
        StringBuilder sqnMaxBuffer = new StringBuilder();
        sqnMaxBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".DEFINE %RAND " + authentication.getRand() + "\n\n"
            + ".POWER_ON\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            sqnMaxBuffer.append(
                "00 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n\n"
            );
        }
        if (authentication.isMilenage()) {
            sqnMaxBuffer.append(
                "; Milenage Algo\n"
                + ".LOAD dll\\Milenage_AKA.dll\n"
                + ".LOAD dll\\Calcul.dll\n"
                + ".DEFINE %SQN " + authentication.getSqnMax() + "\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + ".POWER_ON\n"
                + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
                + "; set variables for calculating AKA1 quintets\n"
                + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
                + ".SET_RI\n"
                + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
                authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
                authentication.getAkaC5())
                + ".SET_CI\n"
                + ".SET_BUFFER I %" + authentication.getKi() + "\n"
                + ".SET_K1\n"
                + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
                + ".SET_OPC\n"
                + ".SET_BUFFER I %RAND\n"
                + ".SET_RAND\n"
                + ".SET_BUFFER I %SQN\n"
                + ".SET_SQN\n"
                + ".SET_BUFFER I %AMF\n"
                + ".SET_AMF\n"
                + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
                + ".INIT_AKA1 ; calculate AKA1 quintets\n"
                + ".GET_MAC ; get quintets and triplets\n"
                + ".DISPLAY O\n"
                + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
                + ".DISPLAY O\n"
                + ".DEFINE %RES O\n"
                + ".GET_SRES\n"
                + ".DISPLAY O\n"
                + ".DEFINE %SRES O\n"
                + ".GET_CK\n"
                + ".DISPLAY O\n"
                + ".DEFINE %CK O\n"
                + ".GET_IK\n"
                + ".DISPLAY O\n"
                + ".DEFINE %IK O\n"
                + ".GET_AK\n"
                + ".DISPLAY O\n"
                + ".SET_KEY O(1;6) 0000\n"
                + ".GET_AUTN\n"
                + ".DISPLAY O\n"
                + ".DEFINE %AUTN O\n"
                + ".SET_DATA O(1;6) 0000\n"
                + ".XOR I 00 /P\n"
                + ".DISPLAY I (1;6)\n"
                + ".GET_KC ; 2G Kc\n"
                + ".DISPLAY O\n"
                + ".DEFINE %Kc O\n"
            );
            if (authentication.isGsmAlgo()) {
                sqnMaxBuffer.append(
                    "; run GSM algo (2G)\n"
                    + ".POWER_ON\n"
                    + "A0 A4 00 00 02 7F20 (9FXX) ; select DF GSM\n"
                    + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                    + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                    + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                    + ".POWER_ON\n"
                );
            }
            sqnMaxBuffer.append(
                "; authenticate in 3G mode with 2G context\n"
                + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
                + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
                + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
                + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
                + "; authenticate with 3G context\n"
                + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
                + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
                + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
                + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK 08 %Kc ] (9000) ; check result\n"
                + ".SET_BUFFER I R(3;14)\n"
                + ".GET_SQN_MAX\n"
                + ".DISPLAY O\n"
                + ".UNDEFINE %RES\n"
                + ".UNDEFINE %SRES\n"
                + ".UNDEFINE %CK\n"
                + ".UNDEFINE %IK\n"
                + ".UNDEFINE %AUTN\n"
                + ".UNDEFINE %Kc\n"
                + ".UNDEFINE %SQN\n"
                + ".UNDEFINE %AMF\n"
                + ".UNLOAD Milenage_AKA.dll\n"
                + ".UNLOAD Calcul.dll\n"
            );
        }
        if (authentication.isIsimAuth()) {
            sqnMaxBuffer.append(
                "; ISIM auth\n"
                + ".LOAD dll\\Milenage_AKA.dll\n"
                + ".LOAD dll\\Calcul.dll\n"
                + ".DEFINE %SQN " + authentication.getSqnMax() + "\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + ".POWER_ON\n"
                + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
                + "; set variables for calculating AKA1 quintets\n"
                + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
                + ".SET_RI\n"
                + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
                authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
                authentication.getAkaC5())
                + ".SET_CI\n"
                + ".SET_BUFFER I %" + authentication.getKi() + "\n"
                + ".SET_K1\n"
                + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
                + ".SET_OPC\n"
                + ".SET_BUFFER I %RAND\n"
                + ".SET_RAND\n"
                + ".SET_BUFFER I %SQN\n"
                + ".SET_SQN\n"
                + ".SET_BUFFER I %AMF\n"
                + ".SET_AMF\n"
                + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
                + ".INIT_AKA1 ; calculate AKA1 quintets\n"
                + ".GET_MAC ; get quintets and triplets\n"
                + ".DISPLAY O\n"
                + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
                + ".DISPLAY O\n"
                + ".DEFINE %RES O\n"
                + ".GET_SRES\n"
                + ".DISPLAY O\n"
                + ".DEFINE %SRES O\n"
                + ".GET_CK\n"
                + ".DISPLAY O\n"
                + ".DEFINE %CK O\n"
                + ".GET_IK\n"
                + ".DISPLAY O\n"
                + ".DEFINE %IK O\n"
                + ".GET_AK\n"
                + ".DISPLAY O\n"
                + ".SET_KEY O(1;6) 0000\n"
                + ".GET_AUTN\n"
                + ".DISPLAY O\n"
                + ".DEFINE %AUTN O\n"
                + ".SET_DATA O(1;6) 0000\n"
                + ".XOR I 00 /P\n"
                + ".DISPLAY I (1;6)\n"
                + "; authenticate with 3G context\n"
                + "00 A4 04 0C <?> %ISIM_AID (9000) ; select ISIM AID\n"
                + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
                + "00 C0 00 00 W(2;1) [DB 08 %RES 10 %CK 10 %IK] (9000) ; return result\n"
                + ".SET_BUFFER I R(3;14)\n"
                + ".GET_SQN_MAX\n"
                + ".DISPLAY O\n"
                + ".UNDEFINE %RES\n"
                + ".UNDEFINE %SRES\n"
                + ".UNDEFINE %CK\n"
                + ".UNDEFINE %IK\n"
                + ".UNDEFINE %AUTN\n"
                + ".UNDEFINE %Kc\n"
                + ".UNDEFINE %SQN\n"
                + ".UNDEFINE %AMF\n"
                + ".UNLOAD Milenage_AKA.dll\n"
                + ".UNLOAD Calcul.dll\n"
            );
        }
        if (root.getRunSettings().getSecretCodes().isPin1disabled()) {
            sqnMaxBuffer.append(
                "\n00 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n\n"
            );
        }
        sqnMaxBuffer.append(
            ".POWER_OFF\n"
        );
        return sqnMaxBuffer;
    }

    @Override
    public StringBuilder generateSecretCodes2g(SecretCodes secretCodes) {
        return new StringBuilder().append("; TODO");
    }

    @Override
    public StringBuilder generateSecretCodes3g(SecretCodes secretCodes) {
        StringBuilder codes3gBuffer = new StringBuilder();
        codes3gBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".POWER_ON\n"
        );
        if (secretCodes.isPin1disabled()) {
            codes3gBuffer.append(
                "00 28 00 01 08 %" + secretCodes.getGpin() + " (9000) ; enable GPIN1\n\n"
            );
        }
        if (secretCodes.isPin2disabled()) {
            codes3gBuffer.append(
                "; enable LPIN1\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 28 00 81 08 %" + secretCodes.getLpin() + " (9000)\n\n"
            );
        }
        codes3gBuffer.append(
            "; Global PIN\n"
            + "; check GPIN1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID (9000)\n"
            + "00 20 00 01 00 (63CX) ; remaining attempts for GPIN1\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "; disable GPIN1 and verify\n"
            + "00 26 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (6984) ; verification failed\n"
            + "; enable GPIN1 and verify\n"
            + "00 28 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "; change GPIN1 and verify new value\n"
            + "00 24 00 01 10 %" + secretCodes.getGpin() + " %" + secretCodes.getLpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getLpin() + " (9000)\n"
            + "; restore initial GPIN1 and verify\n"
            + "00 24 00 01 10 %" + secretCodes.getLpin() + " %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n\n"
            + "; Global PUK\n"
            + "; block GPIN1 and verify\n"
            + "; try verifying with false code " + secretCodes.getGpinRetries() + " times\n"
        );
        for (int i = 0; i < secretCodes.getGpinRetries(); i++)
            codes3gBuffer.append("00 20 00 01 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
        codes3gBuffer.append(
            "00 20 00 01 08 %" + secretCodes.getGpin() + " (6983) ; verification failed\n"
            + "; unblock GPIN1 using GPUK1 and verify\n"
            + "00 2C 00 01 10 %" + secretCodes.getGpuk() + " %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "; unblock GPIN1 using wrong GPUK1\n"
            + "00 2C 00 01 10 FFFFFFFFFFFFFFFF %" + secretCodes.getGpin() + " (63CX) ; verification failed\n"
            + "; check GPUK1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID\n"
            + "00 2C 00 01 00 (63CX) ; remaining attempts for GPUK1 because of wrong verification in previous command\n\n"
            + "; Local PIN\n"
            + "; verify LPIN1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID (9000)\n"
            + "00 20 00 81 00 (63CX) ; remaining attempts for LPIN1\n"
            + "00 20 00 81 08 %" + secretCodes.getLpin() + " (9000)\n"
            + "; change LPIN1 and verify new value\n"
            + "00 24 00 81 10 %" + secretCodes.getLpin() + " %" + secretCodes.getGpin() + " (9000)\n"
            + "00 20 00 81 08 %" + secretCodes.getGpin() + " (9000)\n"
            + "; restore initial LPIN1 and verify\n"
            + "00 24 00 81 10 %" + secretCodes.getGpin() + " %" + secretCodes.getLpin() + " (9000)\n"
            + "00 20 00 81 08 %" + secretCodes.getLpin() + " (9000)\n\n"
            + "; Local PUK\n"
            + "; block LPIN1 and verify\n"
            + "; try verifying with false code " + secretCodes.getLpinRetries() + " times\n"
        );
        for (int i = 0; i < secretCodes.getLpinRetries(); i++)
            codes3gBuffer.append("00 20 00 81 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
        codes3gBuffer.append(
            "00 20 00 81 08 %" + secretCodes.getLpin() + " (6983) ; verification failed\n"
            + "; unblock LPIN1 using LPUK1 and verify\n"
            + "00 2C 00 81 10 %" + secretCodes.getLpuk() + " %" + secretCodes.getLpin() + " (9000)\n"
            + "00 20 00 81 08 %" + secretCodes.getLpin() + " (9000)\n"
            + "; unblock LPIN1 using wrong LPUK1\n"
            + "00 2C 00 81 10 FFFFFFFFFFFFFFFF %" + secretCodes.getLpin() + " (63CX) ; verification failed\n"
            + "; check LPUK1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID\n"
            + "00 2C 00 81 00 (63CX) ; remaining attempts for LPUK1 because of wrong verification in previous command\n\n"
            + "; Issuer Security Code 1\n"
            + "; verify ADM1 remaining verifications\n"
            + "00 A4 04 0C <?> %USIM_AID (9000)\n"
            + "00 20 00 0A 00 (63CX) ; remaining attempts for ADM1\n"
            + "; verify ADM1\n"
            + "00 20 00 0A 08 %" + secretCodes.getIsc1() + " (9000)\n"
            + "; change ADM1 and verify new value\n"
            + "00 24 00 0A 10 %" + secretCodes.getIsc1() + " 0123456789ABCDEF (9000)\n"
            + "00 20 00 0A 08 0123456789ABCDEF (9000)\n"
            + "; restore initial ADM1 and verify\n"
            + "00 24 00 0A 10 0123456789ABCDEF %" + secretCodes.getIsc1() + " (9000)\n"
            + "00 20 00 0A 08 %" + secretCodes.getIsc1() + " (9000)\n\n"
        );
        if (secretCodes.isUseIsc2()) {
            codes3gBuffer.append(
                "; Issuer Security Code 2\n"
                + "; check ADM2 remaining verifications\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 20 00 0B 00 (63CX) ; remaining attempts for ADM2\n"
                + "; verify ADM2\n"
                + "00 20 00 0B 08 %" + secretCodes.getIsc2() + " (9000)\n"
                + "; block ADM2 and verify\n"
                + "; try verifying with false code " + secretCodes.getIsc2Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc2Retries(); i++)
                codes3gBuffer.append("00 20 00 0B 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                "00 20 00 0B 08 %" + secretCodes.getIsc2() + " (6983) ; verification failed\n"
                + "; unblock ADM2 using ADM1 and verify\n"
                + "00 2C 00 0B 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc2() + " (9000)\n"
                + "00 20 00 0B 08 %" + secretCodes.getIsc2() + " (9000)\n"
                + "; change ADM2 and verify new value\n"
                + "00 24 00 0B 10 %" + secretCodes.getIsc2() + " %" + secretCodes.getIsc1() + " (9000)\n"
                + "00 20 00 0B 08 %" + secretCodes.getIsc1() + " (9000)\n"
                + "; restore initial ADM2 and verify\n"
                + "00 24 00 0B 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc2() + " (9000)\n"
                + "00 20 00 0B 08 %" + secretCodes.getIsc2() + " (9000)\n\n"
            );
        }
        if (secretCodes.isUseIsc3()) {
            codes3gBuffer.append(
                "; Issuer Security Code 3\n"
                    + "; check ADM3 remaining verifications\n"
                    + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                    + "00 20 00 0C 00 (63CX) ; remaining attempts for ADM3\n"
                    + "; verify ADM3\n"
                    + "00 20 00 0C 08 %" + secretCodes.getIsc3() + " (9000)\n"
                    + "; block ADM3 and verify\n"
                    + "; try verifying with false code " + secretCodes.getIsc3Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc3Retries(); i++)
                codes3gBuffer.append("00 20 00 0C 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                    "00 20 00 0C 08 %" + secretCodes.getIsc3() + " (6983) ; verification failed\n"
                            + "; unblock ADM3 using ADM1 and verify\n"
                            + "00 2C 00 0C 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc3() + " (9000)\n"
                            + "00 20 00 0C 08 %" + secretCodes.getIsc3() + " (9000)\n"
                            + "; change ADM3 and verify new value\n"
                            + "00 24 00 0C 10 %" + secretCodes.getIsc3() + " %" + secretCodes.getIsc1() + " (9000)\n"
                            + "00 20 00 0C 08 %" + secretCodes.getIsc1() + " (9000)\n"
                            + "; restore initial ADM3 and verify\n"
                            + "00 24 00 0C 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc3() + " (9000)\n"
                            + "00 20 00 0C 08 %" + secretCodes.getIsc3() + " (9000)\n\n"
            );
        }
        if (secretCodes.isUseIsc4()) {
            codes3gBuffer.append(
                "; Issuer Security Code 4\n"
                + "; check ADM4 remaining verifications\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 20 00 0D 00 (63CX) ; remaining attempts for ADM4\n"
                + "; verify ADM4\n"
                + "00 20 00 0D 08 %" + secretCodes.getIsc4() + " (9000)\n"
                + "; block ADM4 and verify\n"
                + "; try verifying with false code " + secretCodes.getIsc4Retries() + " times\n"
            );
            for (int i = 0; i < secretCodes.getIsc4Retries(); i++)
                codes3gBuffer.append("00 20 00 0D 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                "00 20 00 0D 08 %" + secretCodes.getIsc4() + " (6983) ; verification failed\n"
                + "; unblock ADM4 using ADM1 and verify\n"
                + "00 2C 00 0D 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc4() + " (9000)\n"
                + "00 20 00 0D 08 %" + secretCodes.getIsc4() + " (9000)\n"
                + "; change ADM4 and verify new value\n"
                + "00 24 00 0D 10 %" + secretCodes.getIsc4() + " %" + secretCodes.getIsc1() + " (9000)\n"
                + "00 20 00 0D 08 %" + secretCodes.getIsc1() + " (9000)\n"
                + "; restore initial ADM4 and verify\n"
                + "00 24 00 0D 10 %" + secretCodes.getIsc1() + " %" + secretCodes.getIsc4() + " (9000)\n"
                + "00 20 00 0D 08 %" + secretCodes.getIsc4() + " (9000)\n\n"
            );
        }
        if (secretCodes.isBlockGpuk()) {
            codes3gBuffer.append(
                "; Block GPUK\n"
                + "; verify GPIN1\n"
                + "00 20 00 01 08 %" + secretCodes.getGpin() + " (9000)\n"
                + "; verify wrong GPIN1\n"
            );
            for (int i = 0; i < secretCodes.getGpinRetries(); i++)
                codes3gBuffer.append("00 20 00 01 08 FFFFFFFFFFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append("; unblock with wrong PUK code\n");
            for (int i = 0; i < secretCodes.getGpukRetries(); i++)
                codes3gBuffer.append("00 2C 00 01 10 3A3A3A3A3A3A3A3A39393939FFFFFFFF (63XX) ; try: " + (i + 1) + "\n");
            codes3gBuffer.append(
                "; unblock GPIN1 by GPUK1 fails as PUK1 blocked\n"
                + "00 2C 00 01 10 %" + secretCodes.getGpuk() + " %" + secretCodes.getGpin() + " (69XX)\n"
            );
        }
        if (secretCodes.isBlockLpuk()) {
            // TODO
        }

        if (secretCodes.isPin1disabled()) {
            codes3gBuffer.append(
                "00 26 00 01 08 %" + secretCodes.getGpin() + " (9000) ; disable GPIN1\n\n"
            );
        }
        if (secretCodes.isPin2disabled()) {
            codes3gBuffer.append(
                "; disable LPIN1\n"
                + "00 A4 04 0C <?> %USIM_AID (9000)\n"
                + "00 26 00 81 08 %" + secretCodes.getLpin() + " (9000)\n\n"
            );
        }
        codes3gBuffer.append(".POWER_OFF\n");
        return codes3gBuffer;
    }

    private String runAlgo3gAgainCurrSqn(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo; check result DC\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
        );
        if (authentication.isGsmAlgo()) {
            routine.append(
                "; run GSM algo (2G)\n"
                + ".POWER_ON\n"
                + "A0 A4 00 00 02 7F20 (9FXX) ; select DF GSM\n"
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK 08 %Kc ] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + "; authenticate 3G context again with current SQN\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 00 00 W(2;1) [DC] (9000) ; check result\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String runAlgoSqnOkIsim(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo, check result --> OK\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %ISIM_AID (9000) ; select ISIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK ] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String runAlgoSqnNokIsim(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo failed\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
            + ".POWER_ON\n"
            + "00 A4 04 0C <?> %ISIM_AID (9000) ; select ISIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 00 00 W(2;1) [DC] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String runAlgoSqnOk(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo, check result --> OK\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
        );
        if (authentication.isGsmAlgo()) {
            routine.append(
                "; run GSM algo (2G)\n"
                + ".POWER_ON\n"
                + "A0 A4 00 00 02 7F20 (9FXX) ; select DF GSM\n"
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK 08 %Kc ] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

    private String runAlgoSqnNok(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo failed\n"
            + ".POWER_ON\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "; set variables for calculating AKA1 quintets\n"
            + ".SET_BUFFER I %R1 %R2 %R3 %R4 %R5\n"
            + ".SET_RI\n"
            + String.format(".SET_BUFFER I %%%s %%%s %%%s %%%s %%%s\n", authentication.getAkaC1(),
            authentication.getAkaC2(), authentication.getAkaC3(), authentication.getAkaC4(),
            authentication.getAkaC5())
            + ".SET_CI\n"
            + ".SET_BUFFER I %" + authentication.getKi() + "\n"
            + ".SET_K1\n"
            + ".SET_BUFFER I %" + authentication.getOpc() + "\n"
            + ".SET_OPC\n"
            + ".SET_BUFFER I %RAND\n"
            + ".SET_RAND\n"
            + ".SET_BUFFER I %SQN\n"
            + ".SET_SQN\n"
            + ".SET_BUFFER I %AMF\n"
            + ".SET_AMF\n"
            + ".SET_RES_LENGTH " + authentication.getResLength() + " \n"
            + ".INIT_AKA1 ; calculate AKA1 quintets\n"
            + ".GET_MAC ; get quintets and triplets\n"
            + ".DISPLAY O\n"
            + ".GET_XRES ; same for 3G and 2G (XRES and SRES)\n"
            + ".DISPLAY O\n"
            + ".DEFINE %RES O\n"
            + ".GET_SRES\n"
            + ".DISPLAY O\n"
            + ".DEFINE %SRES O\n"
            + ".GET_CK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %CK O\n"
            + ".GET_IK\n"
            + ".DISPLAY O\n"
            + ".DEFINE %IK O\n"
            + ".GET_AK\n"
            + ".DISPLAY O\n"
            + ".SET_KEY O(1;6) 0000\n"
            + ".GET_AUTN\n"
            + ".DISPLAY O\n"
            + ".DEFINE %AUTN O\n"
            + ".SET_DATA O(1;6) 0000\n"
            + ".XOR I 00 /P\n"
            + ".DISPLAY I (1;6)\n"
            + ".GET_KC ; 2G Kc\n"
            + ".DISPLAY O\n"
            + ".DEFINE %Kc O\n"
        );
        if (authentication.isGsmAlgo()) {
            routine.append(
                "; run GSM algo (2G)\n"
                + ".POWER_ON\n"
                + "A0 A4 00 00 02 7F20 (9FXX) ; select DF GSM\n"
                + "A0 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [DC] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + ".UNDEFINE %RES\n"
            + ".UNDEFINE %SRES\n"
            + ".UNDEFINE %CK\n"
            + ".UNDEFINE %IK\n"
            + ".UNDEFINE %AUTN\n"
            + ".UNDEFINE %Kc\n"
            + ".UNDEFINE %SQN\n"
            + ".UNDEFINE %AMF\n"
        );
        return routine.toString();
    }

}
