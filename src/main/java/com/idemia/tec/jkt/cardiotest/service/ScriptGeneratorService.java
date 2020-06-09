package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.model.Authentication;

public interface ScriptGeneratorService {

    StringBuilder generateAtr();
    StringBuilder generateMapping();
    StringBuilder generateMilenageDeltaTest(Authentication authentication);
    StringBuilder generateMilenageSqnMax(Authentication authentication);

}
