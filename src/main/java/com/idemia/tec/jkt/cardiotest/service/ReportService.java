package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.model.RunSettings;

public interface ReportService {

    void createReportFromSettings(RunSettings runSettings);

}
