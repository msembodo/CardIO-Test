package com.idemia.tec.jkt.cardiotest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idemia.tec.jkt.cardiotest.model.ATR;
import com.idemia.tec.jkt.cardiotest.model.RunSettings;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class CardioConfigServiceImpl implements CardioConfigService {

    private File runSettingsFile;

    @Override
    public RunSettings initConfig() {
        runSettingsFile = new File("run-settings.json");
        if (runSettingsFile.exists()) {
            // read saved settings
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(runSettingsFile, RunSettings.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // constants for default values
            String PROJECT_PATH_DEFAULT = "C:\\";
            String ADV_SAVE_VAR_DEFAULT = "variables.txt";
            int READER_NUMBER_DEFAULT = 0;
            String REQUEST_ID = "EP-7746";
            String REQUEST_NAME = "Mobitel (VEON/RU): eSIM pilot";
            String PROFILE_NAME = "20200124 eSIM Mobitel";
            int PROFILE_VERSION = 1;
            String CARD_IMAGE_ITEM_ID = "ICI-183-2020-0914";
            String CUSTOMER = "VEON/MOBITEL GEORGIA";
            String DEVELOPER_NAME = "";
            String TESTER_NAME = "";

            // initialize default values and save settings
            RunSettings defaultSettings = new RunSettings();
            defaultSettings.setProjectPath(PROJECT_PATH_DEFAULT);
            defaultSettings.setAdvSaveVariablesPath(ADV_SAVE_VAR_DEFAULT);
            defaultSettings.setReaderNumber(READER_NUMBER_DEFAULT);
            defaultSettings.setStopOnError(false);
            defaultSettings.setRequestId(REQUEST_ID);
            defaultSettings.setRequestName(REQUEST_NAME);
            defaultSettings.setProfileName(PROFILE_NAME);
            defaultSettings.setProfileVersion(PROFILE_VERSION);
            defaultSettings.setCardImageItemId(CARD_IMAGE_ITEM_ID);
            defaultSettings.setCustomer(CUSTOMER);
            defaultSettings.setDeveloperName(DEVELOPER_NAME);
            defaultSettings.setTesterName(TESTER_NAME);
            defaultSettings.setVariableMappings(new ArrayList<>());
            defaultSettings.setAtr(new ATR(true, "", "", ""));

            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(runSettingsFile, defaultSettings);
                return defaultSettings;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void saveConfig(RunSettings runSettings) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(runSettingsFile, runSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
