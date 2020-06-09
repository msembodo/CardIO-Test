package com.idemia.tec.jkt.cardiotest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idemia.tec.jkt.cardiotest.model.*;
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
            defaultSettings.setSecretCodes(new SecretCodes(true, true, true, false));

            CardParameters cardParameters = new CardParameters();
            cardParameters.setCardManagerAid("A000000151000000");
            cardParameters.setUsimAid("A0000000871002FFFFFFFF89");
            cardParameters.setDfUsim("7FF0");
            cardParameters.setDfGsmAccess("5F3B");
            cardParameters.setDfTelecom("7F10");
            cardParameters.setIsimAid("A0000000871004FFFFFFFF89");
            cardParameters.setDfIsim("7FF2");
            cardParameters.setCsimAid("A0000000871006FFFFFFFF89");
            cardParameters.setDfCsim("7FF3");
            defaultSettings.setCardParameters(cardParameters);

            Authentication authentication = new Authentication();
            authentication.setIncludeDeltaTest(true);
            authentication.setIncludeSqnMax(true);
            authentication.setResLength("08");
            authentication.setAkaC1("C1");
            authentication.setAkaC2("C2");
            authentication.setAkaC3("C3");
            authentication.setAkaC4("C4");
            authentication.setAkaC5("C5");
            authentication.setAkaRi("RI");
            authentication.setRand("12345678 9ABCDEF0 12345678 9ABCDEF0");
            authentication.setSqn("00 00 00 00 00 01");
            authentication.setDelta("FF FF FF FF FF E0");
            authentication.setAmf("0000");
            authentication.setKi("K1");
            authentication.setOpc("OPC");
            authentication.setComp1282(false);
            authentication.setComp1283(false);
            authentication.setMilenage(true);
            authentication.setIsimAuth(false);
            authentication.setGsmAlgo(false);
            defaultSettings.setAuthentication(authentication);

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
