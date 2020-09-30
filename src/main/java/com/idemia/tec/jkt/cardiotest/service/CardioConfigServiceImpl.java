package com.idemia.tec.jkt.cardiotest.service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idemia.tec.jkt.cardiotest.controller.CustomAPDUController;
import com.idemia.tec.jkt.cardiotest.model.*;
import com.idemia.tec.jkt.cardiotest.model.customapdu.CustomApdu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class CardioConfigServiceImpl implements CardioConfigService {

    private File runSettingsFile;

    @Autowired private CustomAPDUController customAPDUController;

    @Override public RunSettings initConfig() {
        runSettingsFile = new File("run-settings.json");
        if (runSettingsFile.exists()) {
            // read saved settings
            ObjectMapper mapper = new ObjectMapper();
            try { return mapper.readValue(runSettingsFile, RunSettings.class); }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
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

            CustomApdu defaultCustomApdu = customAPDUController.getDefaultApduParams();
            defaultSettings.setCustomApdu(defaultCustomApdu);

            defaultSettings.setAtr(new ATR(true, "", "", ""));
            defaultSettings.setSecretCodes(new SecretCodes(true, true, true, false, false, false, false, false));
            defaultSettings.getSecretCodes().setUseIsc2(false);
            defaultSettings.getSecretCodes().setUseIsc3(false);
            defaultSettings.getSecretCodes().setUseIsc4(false);

            CardParameters cardParameters = new CardParameters();
            cardParameters.setCardManagerAid("A000000151000000");
            cardParameters.setUsimAid("A0000000871002FFFFFFFF89");
            cardParameters.setDfUsim("7FF0");
            cardParameters.setDfGsm("7F20");
            cardParameters.setDfGsmAccess("5F3B");
            cardParameters.setDfTelecom("7F10");
            cardParameters.setIsimAid("A0000000871004FFFFFFFF89");
            cardParameters.setDfIsim("7FF2");
            cardParameters.setCsimAid("A0000000871006FFFFFFFF89");
            cardParameters.setDfCsim("7FF3");
            cardParameters.setIccid("ICCID");
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
            authentication.setSqnMax("00 02 00 00 00 00");
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

            defaultSettings.setScp80Keysets(new ArrayList<>());

            defaultSettings.setAppletParams(new ArrayList<>());

            SmsUpdate smsUpdate = new SmsUpdate("44", "07913366003000F0", "41", false, "05811250F3", "PoR as SMS-SUBMIT");
            defaultSettings.setSmsUpdate(smsUpdate);

            MinimumSecurityLevel rfmUsimMsl = new MinimumSecurityLevel(true, "Cryptographic Checksum", "Counter must be higher");
            rfmUsimMsl.setCipherAlgo("3DES - CBC 2 keys");
            rfmUsimMsl.setSigningAlgo("3DES - CBC 2 keys");
            rfmUsimMsl.setPorRequirement("PoR required");
            rfmUsimMsl.setPorSecurity("response with no security");

            AccessDomain rfmUsimAccessDomain = new AccessDomain();
            rfmUsimAccessDomain.setUseAlways(false);
            rfmUsimAccessDomain.setUseIsc1(false);
            rfmUsimAccessDomain.setUseIsc2(false);
            rfmUsimAccessDomain.setUseIsc3(false);
            rfmUsimAccessDomain.setUseIsc4(false);
            rfmUsimAccessDomain.setUseGPin1(false);
            rfmUsimAccessDomain.setUseLPin1(false);

            AccessDomain rfmUsimBadCaseAccessDomain = new AccessDomain();
            rfmUsimBadCaseAccessDomain.setUseBadCaseAlways(false);
            rfmUsimBadCaseAccessDomain.setUseBadCaseIsc1(false);
            rfmUsimBadCaseAccessDomain.setUseBadCaseIsc2(false);
            rfmUsimBadCaseAccessDomain.setUseBadCaseIsc3(false);
            rfmUsimBadCaseAccessDomain.setUseBadCaseIsc4(false);
            rfmUsimBadCaseAccessDomain.setUseBadCaseGPin1(false);
            rfmUsimBadCaseAccessDomain.setUseBadCaseLPin1(false);

            RfmUsim rfmUsim = new RfmUsim();
            rfmUsim.setIncludeRfmUsim(true);
            rfmUsim.setTar("B00001");
            rfmUsim.setTargetEf("6F7B");
            //rfmUsim.setTargetEfBadCase("6F05");
            rfmUsim.setFullAccess(true);
            rfmUsim.setMinimumSecurityLevel(rfmUsimMsl);

            rfmUsim.setRfmUsimAccessDomain(rfmUsimAccessDomain);
            rfmUsim.setRfmUsimBadCaseAccessDomain(rfmUsimBadCaseAccessDomain);
            defaultSettings.setRfmUsim(rfmUsim);

            MinimumSecurityLevel rfmGsmMsl = new MinimumSecurityLevel(true, "Cryptographic Checksum", "Counter must be higher");
            rfmGsmMsl.setCipherAlgo("3DES - CBC 2 keys");
            rfmGsmMsl.setSigningAlgo("3DES - CBC 2 keys");
            rfmGsmMsl.setPorRequirement("PoR required");
            rfmGsmMsl.setPorSecurity("response with no security");

            AccessDomain rfmGsmAccessDomain = new AccessDomain();
            rfmGsmAccessDomain.setUseAlways(false);
            rfmGsmAccessDomain.setUseIsc1(false);
            rfmGsmAccessDomain.setUseIsc2(false);
            rfmGsmAccessDomain.setUseIsc3(false);
            rfmGsmAccessDomain.setUseIsc4(false);
            rfmGsmAccessDomain.setUseGPin1(false);
            rfmGsmAccessDomain.setUseLPin1(false);

            AccessDomain rfmGsmBadCaseAccessDomain = new AccessDomain();
            rfmGsmBadCaseAccessDomain.setUseBadCaseAlways(false);
            rfmGsmBadCaseAccessDomain.setUseBadCaseIsc1(false);
            rfmGsmBadCaseAccessDomain.setUseBadCaseIsc2(false);
            rfmGsmBadCaseAccessDomain.setUseBadCaseIsc3(false);
            rfmGsmBadCaseAccessDomain.setUseBadCaseIsc4(false);
            rfmGsmBadCaseAccessDomain.setUseBadCaseGPin1(false);
            rfmGsmBadCaseAccessDomain.setUseBadCaseLPin1(false);

            RfmGsm rfmGsm = new RfmGsm();
            rfmGsm.setIncludeRfmGsm(true);
            rfmGsm.setTar("B00001");
            rfmGsm.setTargetEf("6F7B");
            //rfmGsm.setTargetEfBadCase("6F05");
            rfmGsm.setFullAccess(true);
            rfmGsm.setMinimumSecurityLevel(rfmGsmMsl);

            rfmGsm.setRfmGsmAccessDomain(rfmGsmAccessDomain);
            rfmGsm.setRfmGsmBadCaseAccessDomain(rfmGsmBadCaseAccessDomain);
            defaultSettings.setRfmGsm(rfmGsm);

            MinimumSecurityLevel rfmIsimMsl = new MinimumSecurityLevel(true, "Cryptographic Checksum", "Counter must be higher");
            rfmIsimMsl.setCipherAlgo("3DES - CBC 2 keys");rfmIsimMsl.setSigningAlgo("3DES - CBC 2 keys");
            rfmIsimMsl.setPorRequirement("PoR required");
            rfmIsimMsl.setPorSecurity("response with no security");

            AccessDomain rfmIsimAccessDomain = new AccessDomain();
            rfmIsimAccessDomain.setUseAlways(false);
            rfmIsimAccessDomain.setUseIsc1(false);
            rfmIsimAccessDomain.setUseIsc2(false);
            rfmIsimAccessDomain.setUseIsc3(false);
            rfmIsimAccessDomain.setUseIsc4(false);
            rfmIsimAccessDomain.setUseGPin1(false);
            rfmIsimAccessDomain.setUseLPin1(false);

            AccessDomain rfmIsimBadCaseAccessDomain = new AccessDomain();
            rfmIsimBadCaseAccessDomain.setUseBadCaseAlways(false);
            rfmIsimBadCaseAccessDomain.setUseBadCaseIsc1(false);
            rfmIsimBadCaseAccessDomain.setUseBadCaseIsc2(false);
            rfmIsimBadCaseAccessDomain.setUseBadCaseIsc3(false);
            rfmIsimBadCaseAccessDomain.setUseBadCaseIsc4(false);
            rfmIsimBadCaseAccessDomain.setUseBadCaseGPin1(false);
            rfmIsimBadCaseAccessDomain.setUseBadCaseLPin1(false);

            RfmIsim rfmIsim = new RfmIsim();
            rfmIsim.setIncludeRfmIsim(true);
            rfmIsim.setTar("B00025");
            rfmIsim.setTargetEf("6FAD");
            //rfmIsim.setTargetEfBadCase("6F02");
            rfmIsim.setFullAccess(true);
            rfmIsim.setMinimumSecurityLevel(rfmIsimMsl);

            rfmIsim.setRfmIsimAccessDomain(rfmIsimAccessDomain);
            rfmIsim.setRfmIsimBadCaseAccessDomain(rfmIsimBadCaseAccessDomain);
            defaultSettings.setRfmIsim(rfmIsim);

            MinimumSecurityLevel ramMsl = new MinimumSecurityLevel(true, "Cryptographic Checksum", "Counter must be higher");
            ramMsl.setCipherAlgo("3DES - CBC 2 keys");ramMsl.setSigningAlgo("3DES - CBC 2 keys");
            ramMsl.setPorRequirement("PoR required");
            ramMsl.setPorSecurity("response with no security");

            Isd isd = new Isd();
            isd.setMethodForGpCommand("with Card Manager Keyset");
            isd.setScLevel("00");
            isd.setScpMode("0255");

            Ram ram = new Ram();
            ram.setIncludeRam(true);
            ram.setTar("000000");
            ram.setMinimumSecurityLevel(ramMsl);

            ram.setIsd(isd);
            defaultSettings.setRam(ram);

            defaultSettings.setCustomScriptsSection1(new ArrayList<>());
            defaultSettings.setCustomScriptsSection2(new ArrayList<>());
            defaultSettings.setCustomScriptsSection3(new ArrayList<>());

            MinimumSecurityLevel rfmCustomMsl = new MinimumSecurityLevel(true, "Cryptographic Checksum", "Counter must be higher");
            rfmCustomMsl.setCipherAlgo("3DES - CBC 2 keys");
            rfmCustomMsl.setSigningAlgo("3DES - CBC 2 keys");
            rfmCustomMsl.setPorRequirement("PoR required");
            rfmCustomMsl.setPorSecurity("response with no security");

            AccessDomain rfmCustomAccessDomain = new AccessDomain();
            rfmCustomAccessDomain.setUseAlways(false);
            rfmCustomAccessDomain.setUseIsc1(false);
            rfmCustomAccessDomain.setUseIsc2(false);
            rfmCustomAccessDomain.setUseIsc3(false);
            rfmCustomAccessDomain.setUseIsc4(false);
            rfmCustomAccessDomain.setUseGPin1(false);
            rfmCustomAccessDomain.setUseLPin1(false);

            AccessDomain rfmCustomBadCaseAccessDomain = new AccessDomain();
            rfmCustomBadCaseAccessDomain.setUseBadCaseAlways(false);
            rfmCustomBadCaseAccessDomain.setUseBadCaseIsc1(false);
            rfmCustomBadCaseAccessDomain.setUseBadCaseIsc2(false);
            rfmCustomBadCaseAccessDomain.setUseBadCaseIsc3(false);
            rfmCustomBadCaseAccessDomain.setUseBadCaseIsc4(false);
            rfmCustomBadCaseAccessDomain.setUseBadCaseGPin1(false);
            rfmCustomBadCaseAccessDomain.setUseBadCaseLPin1(false);

            RfmCustom rfmCustom = new RfmCustom();
            rfmCustom.setIncludeRfmCustom(true);
            rfmCustom.setTar("494D45");
            rfmCustom.setTargetDf("7FF0");
            rfmCustom.setTargetEf("6F7B");
            rfmCustom.setFullAccess(true);
            rfmCustom.setMinimumSecurityLevel(rfmCustomMsl);
            rfmCustom.setRfmCustomAccessDomain(rfmCustomAccessDomain);
            rfmCustom.setRfmCustomBadCaseAccessDomain(rfmCustomBadCaseAccessDomain);
            defaultSettings.setRfmCustom(rfmCustom);


            FileManagement fileManagement = new FileManagement(true, false, null ,true, false, null,true, false, null,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,0,0,false);
            defaultSettings.setFileManagement(fileManagement);

            MinimumSecurityLevel sdMsl = new MinimumSecurityLevel(true, "Cryptographic Checksum", "Counter must be higher");
            sdMsl.setCipherAlgo("3DES - CBC 2 keys");
            sdMsl.setSigningAlgo("3DES - CBC 2 keys");
            sdMsl.setPorRequirement("PoR required");
            sdMsl.setPorSecurity("response with no security");

            ConnectionParameters connParams = new ConnectionParameters();
            connParams.setUseDestinationAddress(true);
            connParams.setDestinationAddress("127.0.0.1");
            connParams.setBufferSize(1316);
            connParams.setNetworkAcessName("ggpic6.tmn.pt");
            connParams.setUseTransportLevel(true);
            connParams.setTransportLevel(2);
            connParams.setPort("8543");

            AmmendmentB ammendmentB = new AmmendmentB("000000", null, null, sdMsl, connParams);
            defaultSettings.setAmmendmentB(ammendmentB);

            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(runSettingsFile, defaultSettings);
                return defaultSettings;
            } catch (JsonGenerationException e) {
                e.printStackTrace();
                return null;
            } catch (JsonMappingException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override public void saveConfig(RunSettings runSettings) {
        ObjectMapper mapper = new ObjectMapper();
        try { mapper.writerWithDefaultPrettyPrinter().writeValue(runSettingsFile, runSettings); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
