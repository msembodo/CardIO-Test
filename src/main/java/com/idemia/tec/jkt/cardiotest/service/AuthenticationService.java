package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired private RootLayoutController root;
    @Autowired private ApduService apduService;

    public StringBuilder generateMilenageDeltaTest(Authentication authentication) {
        StringBuilder deltaTestBuffer = new StringBuilder();
        deltaTestBuffer.append(
            ".SET_BUFFER L " + authentication.getSqn() + "\n\n"
            + ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".DEFINE %RAND " + authentication.getRand() + "\n\n"
            + ".POWER_ON\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            deltaTestBuffer.append("00 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n\n");
        deltaTestBuffer.append(".DEFINE %DELTA " + authentication.getDelta() + "\n\n");
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
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            deltaTestBuffer.append("\n00 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n\n");
        deltaTestBuffer.append(".POWER_OFF\n");
        return deltaTestBuffer;
    }

    public StringBuilder generateMilenageSqnMax(Authentication authentication) {
        StringBuilder sqnMaxBuffer = new StringBuilder();
        sqnMaxBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".DEFINE %RAND " + authentication.getRand() + "\n\n"
            + ".POWER_ON\n"
        );
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            sqnMaxBuffer.append("00 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n\n");
        if (authentication.isMilenage()) {
            sqnMaxBuffer.append(
                "; Milenage Algo\n"
                + ".LOAD dll\\Milenage_AKA.dll\n"
                + ".LOAD dll\\Calcul.dll\n"
                + ".DEFINE %SQN " + authentication.getSqnMax() + "\n"
                + ".DEFINE %AMF " + authentication.getAmf() + "\n"
                + ".POWER_ON\n"
                + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
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
                    + apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                    + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                    + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                    + ".POWER_ON\n"
                );
            }
            sqnMaxBuffer.append(
                "; authenticate in 3G mode with 2G context\n"
                + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
                + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
                + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
                + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
                + "; authenticate with 3G context\n"
                + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
                + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
                + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
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
        if (root.getRunSettings().getSecretCodes().isPin1disabled())
            sqnMaxBuffer.append("\n00 26 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; disable GPIN1\n\n");
        sqnMaxBuffer.append(".POWER_OFF\n");
        return sqnMaxBuffer;
    }

    private String runAlgo3gAgainCurrSqn(Authentication authentication) {
        StringBuilder routine = new StringBuilder();
        routine.append(
            "; running algo; check result DC\n"
            + ".POWER_ON\n"
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify GPIN1\n"
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
                + apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 81 22 10 %RAND 10 %AUTN (61XX) ; authenticate\n"
            + "00 C0 0000 W(2;1) [ DB 08 %RES 10 %CK 10 %IK 08 %Kc ] (9000) ; check result\n"
            + ".SET_BUFFER I R(3;14)\n"
            + ".GET_SQN_MAX\n"
            + ".DISPLAY O\n"
            + "; authenticate 3G context again with current SQN\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
                + apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
                + apduService.verifyPin1() + root.getRunSettings().getSecretCodes().getChv1() + " (9000) ; verify CHV1\n"
                + "A0 88 00 00 10 %RAND (9FXX) ; run GSM algo\n"
                + "A0 C0 00 00 W(2;1) [ %SRES %Kc ] (9000) ; check result\n"
                + ".POWER_ON\n"
            );
        }
        routine.append(
            "; authenticate in 3G mode with 2G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
            + "00 88 00 80 11 10 %RAND (61XX) ; authenticate\n"
            + "00 C0 0000 0E [ 04 %SRES 08 %Kc ] (9000) ; check result\n"
            + "; authenticate with 3G context\n"
            + "00 A4 04 0C <?> %USIM_AID (9000) ; select USIM AID\n"
            + apduService.verifyGpin() + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; verify global pin\n"
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
