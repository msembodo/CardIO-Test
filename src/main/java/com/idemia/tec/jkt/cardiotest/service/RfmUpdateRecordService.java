package com.idemia.tec.jkt.cardiotest.service;

import org.springframework.stereotype.Service;

@Service
public class RfmUpdateRecordService {
//
//    @Autowired private RootLayoutController root;
//
//
//    public StringBuilder generateUpdateRecord(String type){
//        StringBuilder generateUpdateRecord = new StringBuilder();
//
////        StringBuilder updateIsim = updateISIM();
////        StringBuilder updateUsim = updateUSIM();
////        StringBuilder updateGSM = updateGSM();
//
//        switch (type){
//            case "isim" :
//                generateUpdateRecord.append(updateISIM());
//                break;
//            case "usim" :
//                generateUpdateRecord.append(updateUSIM());
//                break;
//            case "gsm" :
//                generateUpdateRecord.append(updateGSM());
//                break;
//        }
//
//        return generateUpdateRecord;
//
//    }
//
//    private StringBuilder headerScript(RfmIsim rfmIsim){
//        StringBuilder headerScript = new StringBuilder();
//        // call mappings and load DLLs
//        headerScript.append(
//                ".CALL Mapping.txt /LIST_OFF\n"
//                        + ".CALL Options.txt /LIST_OFF\n\n"
//                        + ".POWER_ON\n"
//                        + ".LOAD dll\\Calcul.dll\n"
//                        + ".LOAD dll\\OTA2.dll\n"
//                        + ".LOAD dll\\Var_Reader.dll\n"
//        );
//        // create counter and initialize for first-time run
//        File counterBin = new File(root.getRunSettings().getProjectPath() + "\\scripts\\COUNTER.bin");
//        if (!counterBin.exists()) {
//            headerScript.append(
//                    "\n; initialize counter\n"
//                            + ".SET_BUFFER L 00 00 00 00 00\n"
//                            + ".EXPORT_BUFFER L COUNTER.bin\n"
//            );
//        }
//        // load anti-replay counter
//        headerScript.append(
//                "\n; buffer L contains the anti-replay counter for OTA message\n"
//                        + ".SET_BUFFER L\n"
//                        + ".IMPORT_BUFFER L COUNTER.bin\n"
//                        + ".INCREASE_BUFFER L(04:05) 0001\n"
//                        + ".DISPLAY L\n"
//                        + "\n; setup TAR\n"
//                        + ".DEFINE %TAR " + rfmIsim.getTar() + "\n"
//        );
//        // enable pin if required
//        if (root.getRunSettings().getSecretCodes().isPin1disabled())
//            headerScript.append("\nA0 28 00 01 08 %" + root.getRunSettings().getSecretCodes().getGpin() + " (9000) ; enable GPIN1\n");
//
//        return headerScript;
//    }
//
//    private StringBuilder footerScript(RfmIsim rfmIsim){
//
//        StringBuilder footerScript = new StringBuilder();
//
//        return footerScript;
//
//
//    }
//
//
//
//    private StringBuilder updateISIM(){
//        StringBuilder rfmIsim =  new StringBuilder();
//        rfmIsim.append(headerScript(rfmIsim));
//        StringBuilder rfmIsimUpdateRecord = headerScript(rfmIsim);
//        rfmIsimUpdateRecord.append(
//                "TODO Body"
//        );
//        rfmIsimUpdateRecord.append(footerScript(rfmIsim));
//
//        return  rfmIsimUpdateRecord;
//
//    }
//
//    private StringBuilder updateUSIM(){
//        RfmUsim rfmUsim =  new RfmUsim();
//        StringBuilder rfmUsimUpdateRecord = headerScript(rfmUsim);
//
//        return rfmUsimUpdateRecord;
//    }
//
//    private StringBuilder updateGSM(){
//        RfmGsm rfmGsm =  new RfmGsm();
//        StringBuilder rfmGsmUpdateRecord = headerScript(rfmGsm);
//
//        return rfmGsmUpdateRecord;
//    }



}
