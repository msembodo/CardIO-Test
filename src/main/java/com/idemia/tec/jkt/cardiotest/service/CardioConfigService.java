package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.model.RunSettings;

public interface CardioConfigService {

    RunSettings initConfig();
    void saveConfig(RunSettings runSettings);

}
