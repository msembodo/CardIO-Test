package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.FileManagementController;
import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.FileManagement;
import com.idemia.tec.jkt.cardiotest.model.LinkFiles;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.idemia.tec.jkt.cardiotest.model.SecretCodes;

@Service
public class FileManagementService {

    @Autowired private RootLayoutController root;

    ObservableList <LinkFiles> allLinkFiles;


    public StringBuilder generateFilemanagementLinkFiles(FileManagement fileManagement) {
        StringBuilder linkFileTestBuffer = new StringBuilder();
        linkFileTestBuffer.append(
                ";=====================\n\n"
                        + ";Link File Test\n\n"
                        + ";=====================\n\n"
        );

        linkFileTestBuffer.append(
                        ".CALL Mapping.txt /LIST_OFF\n"
                        + ".CALL Options.txt /LIST_OFF\n\n"
                        + ".POWER_ON\n"
        );

        linkFileTestBuffer.append(
                "\n;\n"
                        + ";\n\n"
                        + ";\n"
        );

        linkFileTestBuffer.append(".POWER_OFF\n");
        return linkFileTestBuffer;
    }

    public StringBuilder generateFilemanagementRuwi(FileManagement fileManagement) {
        StringBuilder ruwiTestBuffer = new StringBuilder();
        ruwiTestBuffer.append(
                ";=====================\n"
                        + ";Readable and Updateable when Invalidated Test\n\n"
                        + ";=====================\n"
        );
        ruwiTestBuffer.append(
                ".CALL Mapping.txt /LIST_OFF\n"
                        + ".CALL Options.txt /LIST_OFF\n\n"
                        + ".POWER_ON\n"
        );

        ruwiTestBuffer.append(
                "\n;\n"
                        + ";\n\n"
                        + ";\n"
        );

        ruwiTestBuffer.append(".POWER_OFF\n");
        return ruwiTestBuffer;
    }

    public StringBuilder generateFilemanagementSfi(FileManagement fileManagement) {
        StringBuilder sfiTestBuffer = new StringBuilder();
        sfiTestBuffer.append(
                ";=====================\n"
                        + ";SFI Check Test\n"
                        + ";=====================\n\n"
        );

        sfiTestBuffer.append(
                        "; This is script to check SFI values according to ETSI Standard\n"
                        + "; A Short File Identifier (SFI) is coded as 5 bits valued in the range from 1 to 30.\n"
                        + "; No two files under the same parent shall have the same SFI\n"
                        + "; Note: DF Phonebook is not covered yet\n"
                        + "; How to decode SFI>check tag 88, SFI is coded from bits b8 to b4, see TS 102 221 for further reference\n"
                        + "; In case you found error like 6A82 (file not found), please check the profile in case SFI is set as 00.\n"
                        + "; Then pls compare to spec to know whether it's Optional or Mandatory \n\n"
        );

        sfiTestBuffer.append(
                ";APDU Mapping\n;=====================\n\n"
                        +".DEFINE %SELECT 00 A4 00 00 02 #2C\n"
                        + ".DEFINE %SELECT_AID 00 A4 04 00 #NC\n"
                        + ".DEFINE %READ_BINARY 00 B0 #3C\n"
                        + ".DEFINE %READ_RECORD 00 B2 #3C\n\n"
        );

        sfiTestBuffer.append(
                ".CALL Mapping.txt /LIST_OFF\n"
                        + ".CALL Options.txt /LIST_OFF\n\n"
                        + ".POWER_ON\n"
        );

        sfiTestBuffer.append(
                "\n"
                        + "00 20 00 0A 08 %" + root.getRunSettings().getSecretCodes().getIsc1() + " (9000)\n"
                        + "00 20 00 01 08 %" + root.getRunSettings().getSecretCodes().getChv1() + " (9000)\n");
                        //+ "00 20 00 02 08 %" + root.getRunSettings().getSecretCodes().getChv2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc2())
            sfiTestBuffer.append("00 20 00 0B 08 %" + root.getRunSettings().getSecretCodes().getIsc2() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc3())
            sfiTestBuffer.append("00 20 00 0C 08 %" + root.getRunSettings().getSecretCodes().getIsc3() + " (9000)\n");
        if (root.getRunSettings().getSecretCodes().isUseIsc4())
            sfiTestBuffer.append("00 20 00 0D 08 %" + root.getRunSettings().getSecretCodes().getIsc4() + " (9000)\n");


        sfiTestBuffer.append("\n******** MF SFI CHECKING ******** \n\n");
        sfiTestBuffer.append("; EF ICCID\n");
        if (fileManagement.isSFI_Iccid_2FE2_02_bool())
            sfiTestBuffer.append("%READ_BINARY $ 82 00 0A (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 82 00 0A (9000)\n\n");

        sfiTestBuffer.append("; EF ARR\n");
        if (fileManagement.isSFI_ARR_2F06_06_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 34 00\n%READ_RECORD $ 01 34 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 34 00\n;%READ_RECORD $ 01 34 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF PL \n");
        if (fileManagement.isSFI_PL_2F05_05_bool())
            sfiTestBuffer.append("%READ_BINARY $ 85 00 00\n%READ_BINARY $ 85 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 85 00 00\n;%READ_BINARY $ 85 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF DIR \n");
        if (fileManagement.isSFI_Dir_2F00_1E_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 F4 00\n%READ_RECORD $ 01 F4 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 F4 00\n;%READ_RECORD $ 01 F4 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("******* USIM SFI CHECKING ******* \n\n");
        sfiTestBuffer.append("%SELECT_AID $ <?> %USIM_AID (61xx) \n\n");

//EF_ECC

        sfiTestBuffer.append("; EF LI\n");
        if (fileManagement.isSFI_LI_6F05_02_bool())
            sfiTestBuffer.append("%READ_BINARY $ 82 00 00\n%READ_BINARY $ 82 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 82 00 00\n;%READ_BINARY $ 82 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF AD\n");
        if (fileManagement.isSFI_AD_6FAD_03_bool())
            sfiTestBuffer.append("%READ_BINARY $ 83 00 00\n%READ_BINARY $ 83 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 83 00 00\n;%READ_BINARY $ 83 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF UST\n");
        if (fileManagement.isSFI_UST_6F38_04_bool())
            sfiTestBuffer.append("%READ_BINARY $ 84 00 00\n%READ_BINARY $ 84 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 84 00 00\n;%READ_BINARY $ 84 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF EST\n");
        if (fileManagement.isSFI_EST_6F56_05_bool())
            sfiTestBuffer.append("%READ_BINARY $ 85 00 00\n%READ_BINARY $ 85 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 85 00 00\n;%READ_BINARY $ 85 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF ACC\n");
        if (fileManagement.isSFI_ACC_6F78_06_bool())
            sfiTestBuffer.append("%READ_BINARY $ 86 00 02 (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 86 00 02 (9000)\n\n");

        sfiTestBuffer.append("; EF IMSI\n");
        if (fileManagement.isSFI_IMSI_6F07_07_bool())
            sfiTestBuffer.append("%READ_BINARY $ 87 00 09 (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 87 00 09 (9000)\n\n");

        sfiTestBuffer.append("; EF KEYS\n");
        if (fileManagement.isSFI_KEYS_6F08_08_bool())
            sfiTestBuffer.append("%READ_BINARY $ 88 00 21 (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 88 00 21 (9000)\n\n");

        sfiTestBuffer.append("; EF KEYSPS\n");
        if (fileManagement.isSFI_KEYSPS_6F09_09_bool())
            sfiTestBuffer.append("%READ_BINARY $ 89 00 21 (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 89 00 21 (9000)\n\n");

        sfiTestBuffer.append("; EF PLMNwACT **\n");
        if (fileManagement.isSFI_PLMNwACT_6F60_0A_bool())
            sfiTestBuffer.append("%READ_BINARY $ 8A 00 00\n%READ_BINARY $ 8A 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 8A 00 00\n;%READ_BINARY $ 8A 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF LOCI\n");
        if (fileManagement.isSFI_LOCI_6F7E_0B_bool())
            sfiTestBuffer.append("%READ_BINARY $ 8B 00 0B (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 8B 00 0B (9000)\n\n");

        sfiTestBuffer.append("; EF PSLOCI\n");
        if (fileManagement.isSFI_PSLOCI_6F73_0C_bool())
            sfiTestBuffer.append("%READ_BINARY $ 8C 00 0E (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 8C 00 0E (9000)\n\n");

        sfiTestBuffer.append("; EF FPLMN\n");
        if (fileManagement.isSFI_FPLMN_6F7B_0D_bool())
            sfiTestBuffer.append("%READ_BINARY $ 8D 00 00\n%READ_BINARY $ 8D 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 8D 00 00\n;%READ_BINARY $ 8D 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF CBMID\n");
        if (fileManagement.isSFI_CBMID_6F48_0E_bool())
            sfiTestBuffer.append("%READ_BINARY $ 8E 00 00\n%READ_BINARY $ 8E 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 8E 00 00\n;%READ_BINARY $ 8E 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF START-HFN\n");
        if (fileManagement.isSFI_StartHFN_6F5B_0F_bool())
            sfiTestBuffer.append("%READ_BINARY $ 8F 00 06 (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 8F 00 06 (9000)\n\n");

        sfiTestBuffer.append("; EF THRESHOLD\n");
        if (fileManagement.isSFI_TRESHOLD_6F5C_10_bool())
            sfiTestBuffer.append("%READ_BINARY $ 90 00 03 (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 90 00 03 (9000)\n\n");

        sfiTestBuffer.append("; EF OPLMNwACT\n");
        if (fileManagement.isSFI_OPLMNwACT_6F61_11_bool())
            sfiTestBuffer.append("%READ_BINARY $ 91 00 00\n%READ_BINARY $ 91 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 91 00 00\n;%READ_BINARY $ 91 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF HPPLMN\n");
        if (fileManagement.isSFI_HPPLMN_6F31_12_bool())
            sfiTestBuffer.append("%READ_BINARY $ 92 00 01 (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 92 00 01 (9000)\n\n");

        sfiTestBuffer.append("; EF HPLMNwACT\n");
        if (fileManagement.isSFI_HPLMNwACT_6F62_13_bool())
            sfiTestBuffer.append("%READ_BINARY $ 92 00 01 (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 92 00 01 (9000)\n\n");

        sfiTestBuffer.append("; EF ICI\n");
        if (fileManagement.isSFI_ICI_6F80_14_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 A0 00\n%READ_RECORD $ 01 A0 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 A0 00\n;%READ_RECORD $ 01 A0 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF OCI\n");
        if (fileManagement.isSFI_OCI_6F81_15_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 A1 00\n%READ_RECORD $ 01 A1 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 A1 00\n;%READ_RECORD $ 01 A1 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF CCP2\n");
        if (fileManagement.isSFI_CCP2_6F4F_16_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 A2 00\n%READ_RECORD $ 01 A2 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 A2 00\n;%READ_RECORD $ 01 A2 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF ARR\n");
        if (fileManagement.isSFI_ARR_6F06_17_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 A3 00\n%READ_RECORD $ 01 A3 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 A3 00\n;%READ_RECORD $ 01 A3 W(2;1) (9000)\n\n");

//SFI_ePDGIdEm_6F65_18_bool

        sfiTestBuffer.append("; EF PNN\n");
        if (fileManagement.isSFI_PNN_6FC5_19_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 A4 00\n%READ_RECORD $ 01 A4 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 A4 00\n;%READ_RECORD $ 01 A4 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF OPL\n");
        if (fileManagement.isSFI_OPL_6FC6_1A_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 A5 00\n%READ_RECORD $ 01 A5 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 A5 00\n;%READ_RECORD $ 01 A5 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF SPDI\n");
        if (fileManagement.isSFI_SPDI_6FCD_1B_bool())
            sfiTestBuffer.append("%READ_BINARY $ 9B 00 00\n%READ_BINARY $ 9B 00 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_BINARY $ 9B 00 00\n;%READ_BINARY $ 9B 00 W(2;1) (9000)\n\n");

        sfiTestBuffer.append("; EF ACM\n");
        if (fileManagement.isSFI_ACM_6F39_1C_bool())
            sfiTestBuffer.append("%READ_RECORD $ 01 C0 00\n%READ_RECORD $ 01 C0 W(2;1) (9000)\n\n");
        else
            sfiTestBuffer.append(";%READ_RECORD $ 01 C0 00\n;%READ_RECORD $ 01 C0 W(2;1) (9000)\n\n");

//Kc
//KcGPRS

        sfiTestBuffer.append(".POWER_OFF\n");
        return sfiTestBuffer;
    }


}
