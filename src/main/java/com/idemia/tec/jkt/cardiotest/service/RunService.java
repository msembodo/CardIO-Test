package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.response.TestSuiteResponse;

public interface RunService {

    TestSuiteResponse runAll();
    boolean runAtr();
    boolean runDeltaTest();
    boolean runSqnMax();
    boolean runSecretCodes3g();
    boolean runSecretCodes2g();

}
