package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.response.TestSuiteResponse;

public interface RunService {

    TestSuiteResponse runAll();
    boolean runAtr();
    boolean runDeltaTest();
    boolean runSqnMax();
    boolean runRfmUsim();
    boolean runRfmUsimUpdateRecord();
    boolean runRfmUsimExpandedMode();
    boolean runRfmIsim();
    boolean runRfmIsimUpdateRecord();
    boolean runRfmIsimExpandedMode();
    boolean runSecretCodes3g();
    boolean runSecretCodes2g();

    //Custom RFM --------------------------------------
    boolean runRfmCustom();
    boolean runRfmCustomUpdateRecord();
    boolean runRfmCustomExpandedMode();
    // ------------------------------------------------


}
