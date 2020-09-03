package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.model.*;

public interface ScriptGeneratorService {

    StringBuilder generateAtr();
    StringBuilder generateMapping();
    StringBuilder generateMilenageDeltaTest(Authentication authentication);
    StringBuilder generateMilenageSqnMax(Authentication authentication);
    StringBuilder generateRfmUsim(RfmUsim rfmUsim);
    StringBuilder generateRfmUsimUpdateRecord(RfmUsim rfmUsim);
    StringBuilder generateRfmUsimExpandedMode(RfmUsim rfmUsim);
    StringBuilder generateRfmGsm(RfmGsm rfmGsm);
    StringBuilder generateRfmGsmUpdateRecord(RfmGsm rfmGsm);
    StringBuilder generateRfmGsmExpandedMode(RfmGsm rfmGsm);
    StringBuilder generateRfmIsim(RfmIsim rfmIsim);
    StringBuilder generateRfmIsimUpdateRecord(RfmIsim rfmIsim);
    StringBuilder generateRfmIsimExpandedMode(RfmIsim rfmIsim);
    StringBuilder generateSecretCodes2g(SecretCodes secretCodes);
    StringBuilder generateSecretCodes3g(SecretCodes secretCodes);
    StringBuilder generateRfmCustom(RfmCustom rfmCustom);
    StringBuilder generateRfmCustomUpdateRecord(RfmCustom rfmCustom);
    StringBuilder generateRfmCustomExpandedMode(RfmCustom rfmCustom);


    // ------------------------------------------------
    StringBuilder generateFilemanagementLinkFiles(FileManagement fileManagement);
    StringBuilder generateFilemanagementRuwi(FileManagement fileManagement);
    StringBuilder generateFilemanagementSfi(FileManagement fileManagement);
    // ------------------------------------------------

}
