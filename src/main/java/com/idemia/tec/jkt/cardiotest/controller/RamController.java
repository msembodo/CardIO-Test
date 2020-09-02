package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import com.idemia.tec.jkt.cardiotest.model.Isd;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RamController {

    @FXML private CheckBox chkIncludeRam;
    @FXML private CheckBox chkIncludeRamUpdateRecord;
    @FXML private CheckBox chkIncludeRamExpandedMode;
    @FXML private TextField txtRamMslByte;
    @FXML private ComboBox<String> cmbRamCipherAlgo;
    @FXML private CheckBox chkRamUseCipher;
    @FXML private ComboBox<String> cmbRamAuthVerif;
    @FXML private ComboBox<String> cmbRamSigningAlgo;
    @FXML private ComboBox<String> cmbRamPorRequirement;
    @FXML private ComboBox<String> cmbRamPorSecurity;
    @FXML private CheckBox chkRamCipherPor;
    @FXML private ComboBox<String> cmbRamCounterCheck;
    @FXML private TextField txtRamTar;
    @FXML private ComboBox<String> cmbRamMethodForGpCommand;
    @FXML private TextField txtRamScpMode;
    @FXML private ComboBox<String> cmbRamScLevel;
    @FXML private CheckBox chkRamSecured;
    @FXML private ComboBox<String> cmbIsdEnc;
    @FXML private ComboBox<String> cmbIsdMac;
    @FXML private ComboBox<String> cmbIsdKey;
    @FXML private ComboBox<String> cmbIsdPin;
    @FXML private CheckBox chkUseSpecificKeyset;
    @FXML private Label lblRamCipheringKeyset;
    @FXML private ComboBox<String> cmbRamCipheringKeyset;
    @FXML private Label lblRamKic;
    @FXML private CheckBox chkRamCustomKic;
    @FXML private TextField txtRamCustomKic;
    @FXML private Label lblRamAuthKeyset;
    @FXML private ComboBox<String> cmbRamAuthKeyset;
    @FXML private Label lblRamKid;
    @FXML private CheckBox chkRamCustomKid;
    @FXML private TextField txtRamCustomKid;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public RamController() {}

    @FXML private void initialize() {
        chkIncludeRam.setSelected(root.getRunSettings().getRam().isIncludeRam());
        handleIncludeRamCheck();

        chkIncludeRamUpdateRecord.setSelected(root.getRunSettings().getRam().isIncludeRamUpdateRecord());
        handleIncludeRamUpdateRecordCheck();

        chkIncludeRamExpandedMode.setSelected(root.getRunSettings().getRam().isIncludeRamExpandedMode());
        handleIncludeRamExpandedModeCheck();

        // RAM MSL

        txtRamMslByte.setText(root.getRunSettings().getRam().getMinimumSecurityLevel().getComputedMsl());

        chkRamUseCipher.setSelected(root.getRunSettings().getRam().getMinimumSecurityLevel().isUseCipher());

        // initialize list of cipher algorithm
        List<String> cipherAlgos = new ArrayList<>();
        cipherAlgos.add("as defined in keyset");
        cipherAlgos.add("no cipher");
        cipherAlgos.add("DES - CBC");
        cipherAlgos.add("AES - CBC");
        cipherAlgos.add("XOR");
        cipherAlgos.add("3DES - CBC 2 keys");
        cipherAlgos.add("3DES - CBC 3 keys");
        cipherAlgos.add("DES - ECB");
        cmbRamCipherAlgo.getItems().addAll(cipherAlgos);
        cmbRamCipherAlgo.setValue(root.getRunSettings().getRam().getMinimumSecurityLevel().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        cmbRamAuthVerif.getItems().addAll(authVerifs);
        cmbRamAuthVerif.setValue(root.getRunSettings().getRam().getMinimumSecurityLevel().getAuthVerification());

        // initialize list of signing algorithm
        List<String> signingAlgos = new ArrayList<>();
        signingAlgos.add("as defined in keyset");
        signingAlgos.add("no algorithm");
        signingAlgos.add("DES - CBC");
        signingAlgos.add("AES - CMAC");
        signingAlgos.add("XOR");
        signingAlgos.add("3DES - CBC 2 keys");
        signingAlgos.add("3DES - CBC 3 keys");
        signingAlgos.add("DES - ECB");
        signingAlgos.add("CRC32 (may be X5h)");
        signingAlgos.add("CRC32 (may be X0h)");
        signingAlgos.add("ISO9797 Algo 3 (auth value 8 byte)");
        signingAlgos.add("ISO9797 Algo 3 (auth value 4 byte)");
        signingAlgos.add("ISO9797 Algo 4 (auth value 4 byte)");
        signingAlgos.add("ISO9797 Algo 4 (auth value 8 byte)");
        signingAlgos.add("CRC16");
        cmbRamSigningAlgo.getItems().addAll(signingAlgos);
        cmbRamSigningAlgo.setValue(root.getRunSettings().getRam().getMinimumSecurityLevel().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        cmbRamCounterCheck.getItems().addAll(counterCheckings);
        cmbRamCounterCheck.setValue(root.getRunSettings().getRam().getMinimumSecurityLevel().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        cmbRamPorRequirement.getItems().addAll(porRequirements);
        cmbRamPorRequirement.setValue(root.getRunSettings().getRam().getMinimumSecurityLevel().getPorRequirement());

        chkRamCipherPor.setSelected(root.getRunSettings().getRam().getMinimumSecurityLevel().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        cmbRamPorSecurity.getItems().addAll(porSecurities);
        cmbRamPorSecurity.setValue(root.getRunSettings().getRam().getMinimumSecurityLevel().getPorSecurity());

        // initialize Method for GP Command
        List<String> methodForGpCommand = new ArrayList<>();
        methodForGpCommand.add("with Card Manager Keyset");
        methodForGpCommand.add("no Card Manager Keyset");
        methodForGpCommand.add("SIMBiOs");
        cmbRamMethodForGpCommand.getItems().addAll(methodForGpCommand);
        cmbRamMethodForGpCommand.setValue(root.getRunSettings().getRam().getIsd().getMethodForGpCommand());

        // initialize SC Level
        List<String> scLevel = new ArrayList<>();
        scLevel.add("00");
        scLevel.add("01");
        scLevel.add("03");
        cmbRamScLevel.getItems().addAll(scLevel);
        cmbRamScLevel.setValue(root.getRunSettings().getRam().getIsd().getScLevel());

        // ISD secured state
        chkRamSecured.setSelected(root.getRunSettings().getRam().getIsd().isSecuredState());

        // RAM parameters

        txtRamTar.setText(root.getRunSettings().getRam().getTar());
        txtRamScpMode.setText(root.getRunSettings().getRam().getIsd().getScpMode());
        cmbIsdMac.setValue(root.getRunSettings().getRam().getIsd().getCardManagerMac());
        cmbIsdEnc.setValue(root.getRunSettings().getRam().getIsd().getCardManagerEnc());
        cmbIsdKey.setValue(root.getRunSettings().getRam().getIsd().getCardManagerKey());


        // initialize list of available keysets for RAM

        if (root.getRunSettings().getRam().getCipheringKeyset() != null) {
            cmbRamCipheringKeyset.setValue(root.getRunSettings().getRam().getCipheringKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRam().getCipheringKeyset().getKeysetName())) {
                    lblRamKic.setText("Kic (hex): " + keyset.getComputedKic());
                    break;
                }
            }
            chkRamCustomKic.setSelected(root.getRunSettings().getRam().getCipheringKeyset().isCustomKic());
            handleRamCustomKicCheck();
            if (chkRamCustomKic.isSelected())
                txtRamCustomKic.setText(root.getRunSettings().getRam().getCipheringKeyset().getComputedKic());
            else
                txtRamCustomKic.setText("");
        }

        if (root.getRunSettings().getRam().getAuthKeyset() != null) {
            cmbRamAuthKeyset.setValue(root.getRunSettings().getRam().getAuthKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRam().getAuthKeyset().getKeysetName())) {
                    lblRamKid.setText("Kid (hex): " + keyset.getComputedKid());
                    break;
                }
            }
            chkRamCustomKid.setSelected(root.getRunSettings().getRam().getAuthKeyset().isCustomKid());
            handleRamCustomKidCheck();
            if (chkRamCustomKid.isSelected())
                txtRamCustomKid.setText(root.getRunSettings().getRam().getAuthKeyset().getComputedKid());
            else
                txtRamCustomKid.setText("");
        }

        chkUseSpecificKeyset.setSelected(root.getRunSettings().getRam().isUseSpecificKeyset());
        handleUseSpecificKeysetCheck();

        cmbRamMethodForGpCommand.setValue(root.getRunSettings().getRam().getIsd().getMethodForGpCommand());
        handleRamMethodForGpCommand();
    }

    @FXML private void handleCipherKeysetContextMenu() { cmbRamCipheringKeyset.setItems(cardiotest.getScp80KeysetLabels()); }
    @FXML private void handleAuthKeysetContextMenu() { cmbRamAuthKeyset.setItems(cardiotest.getScp80KeysetLabels()); }
    @FXML private void handleIsdEncContextMenu() { cmbIsdEnc.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleIsdMacContextMenu() { cmbIsdMac.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleIsdKeyContextMenu() { cmbIsdKey.setItems(cardiotest.getMappedVariables()); }
    @FXML private void handleIsdPinContextMenu() { cmbIsdPin.setItems(cardiotest.getMappedVariables()); }

    @FXML private void handleIncludeRamCheck() {
        if (chkIncludeRam.isSelected())
            root.getMenuRam().setDisable(false);
        else
            root.getMenuRam().setDisable(true);
    }

    @FXML private void handleIncludeRamUpdateRecordCheck() {
        if (chkIncludeRamUpdateRecord.isSelected())
            root.getMenuRamUpdateRecord().setDisable(false);
        else
            root.getMenuRamUpdateRecord().setDisable(true);
    }

    @FXML private void handleIncludeRamExpandedModeCheck() {
        if (chkIncludeRamExpandedMode.isSelected())
            root.getMenuRamExpandedMode().setDisable(false);
        else
            root.getMenuRamExpandedMode().setDisable(true);
    }

    @FXML private void handleUseSpecificKeysetCheck() {
        if (chkUseSpecificKeyset.isSelected()) {
            lblRamCipheringKeyset.setDisable(false);
            cmbRamCipheringKeyset.setDisable(false);
            lblRamKic.setDisable(false);
            chkRamCustomKic.setDisable(false);
            txtRamCustomKic.setDisable(false);
            lblRamAuthKeyset.setDisable(false);
            cmbRamAuthKeyset.setDisable(false);
            lblRamKid.setDisable(false);
            chkRamCustomKid.setDisable(false);
            txtRamCustomKid.setDisable(false);
        }
        else {
            lblRamCipheringKeyset.setDisable(true);
            cmbRamCipheringKeyset.setDisable(true);
            lblRamKic.setDisable(true);
            chkRamCustomKic.setDisable(true);
            txtRamCustomKic.setDisable(true);
            lblRamAuthKeyset.setDisable(true);
            cmbRamAuthKeyset.setDisable(true);
            lblRamKid.setDisable(true);
            chkRamCustomKid.setDisable(true);
            txtRamCustomKid.setDisable(true);
        }
    }

    @FXML private void handleRamCipheringKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRamCipheringKeyset.getSelectionModel().getSelectedItem())) {
                lblRamKic.setText("Kic (hex): " + keyset.getComputedKic());
                break;
            }
        }
    }

    @FXML private void handleRamAuthKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRamAuthKeyset.getSelectionModel().getSelectedItem())) {
                lblRamKid.setText("Kid (hex): " + keyset.getComputedKid());
                break;
            }
        }
    }

    @FXML private void handleRamCustomKicCheck() {
        if (chkRamCustomKic.isSelected())
            txtRamCustomKic.setDisable(false);
        else
            txtRamCustomKic.setDisable(true);
    }

    @FXML private void handleRamCustomKidCheck() {
        if (chkRamCustomKid.isSelected())
            txtRamCustomKid.setDisable(false);
        else
            txtRamCustomKid.setDisable(true);
    }

    @FXML private void handleButtonSetRamMsl() {
        String mslHexStr = txtRamMslByte.getText();
        int mslInteger = Integer.parseInt(mslHexStr, 16);
//        logger.info("MSL integer: " + mslInteger);
        if (mslInteger > 31) {
            // MSL integer shoould not be higher than 31 (0x1F)
            Alert mslAlert = new Alert(Alert.AlertType.ERROR);
            mslAlert.initModality(Modality.APPLICATION_MODAL);
            mslAlert.initOwner(txtRamMslByte.getScene().getWindow());
            mslAlert.setTitle("Minimum Security Level");
            mslAlert.setHeaderText("Invalid MSL");
            mslAlert.setContentText("MSL value should not exceed '1F'");
            mslAlert.showAndWait();
        } else {
            // set components accordingly
            if (mslInteger == 0) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("No verification");
                cmbRamCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 1) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRamCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 2) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRamCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 3) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRamCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 4) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("No verification");
                cmbRamCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 5) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRamCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 6) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRamCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 7) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRamCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 8) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("No verification");
                cmbRamCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 9) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRamCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 10) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRamCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 11) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRamCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 12) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("No verification");
                cmbRamCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 13) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRamCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 14) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRamCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 15) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRamCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 16) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("No verification");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 17) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 18) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 19) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 20) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("No verification");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 21) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 22) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 23) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 24) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("No verification");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 25) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 26) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 27) {
                chkRamUseCipher.setSelected(false);
                cmbRamAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 28) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("No verification");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 29) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 30) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 31) {
                chkRamUseCipher.setSelected(true);
                cmbRamAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRamCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            // set back MSL text field as it may change due to race condition
            txtRamMslByte.setText(mslHexStr);
        }
    }

    @FXML private void handleRamUseCipherCheck() {
        if (chkRamUseCipher.isSelected())
            root.getRunSettings().getRam().getMinimumSecurityLevel().setUseCipher(true);
        else
            root.getRunSettings().getRam().getMinimumSecurityLevel().setUseCipher(false);
        root.getRunSettings().getRam().getMinimumSecurityLevel().computeMsl();
        txtRamMslByte.setText(root.getRunSettings().getRam().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRamAuthVerifSelection() {
        root.getRunSettings().getRam().getMinimumSecurityLevel().setAuthVerification(cmbRamAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRam().getMinimumSecurityLevel().computeMsl();
        txtRamMslByte.setText(root.getRunSettings().getRam().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRamCounterCheckingSelection() {
        root.getRunSettings().getRam().getMinimumSecurityLevel().setCounterChecking(cmbRamCounterCheck.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRam().getMinimumSecurityLevel().computeMsl();
        txtRamMslByte.setText(root.getRunSettings().getRam().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML private void handleRamMethodForGpCommand() {
        if(cmbRamMethodForGpCommand.getValue().equals("with Card Manager Keyset")) {
            txtRamScpMode.setDisable(false);
            cmbRamScLevel.setDisable(false);
            chkRamSecured.setDisable(false);
            cmbIsdEnc.setDisable(false);
            cmbIsdMac.setDisable(false);
            cmbIsdKey.setDisable(false);
            cmbIsdPin.setDisable(true);
        }
        else if(cmbRamMethodForGpCommand.getValue().equals("no Card Manager Keyset")) {
            txtRamScpMode.setDisable(true);
            cmbRamScLevel.setDisable(true);
            chkRamSecured.setDisable(true);
            cmbIsdEnc.setDisable(true);
            cmbIsdMac.setDisable(true);
            cmbIsdKey.setDisable(true);
            cmbIsdPin.setDisable(true);
        }
        else if(cmbRamMethodForGpCommand.getValue().equals("SIMBiOs")) {
            txtRamScpMode.setDisable(true);
            cmbRamScLevel.setDisable(true);
            chkRamSecured.setDisable(true);
            cmbIsdEnc.setDisable(true);
            cmbIsdMac.setDisable(true);
            cmbIsdKey.setDisable(true);
            cmbIsdPin.setDisable(false);
        }
    }


    public void saveControlState() {
        root.getRunSettings().getRam().setIncludeRam(chkIncludeRam.isSelected());
        root.getRunSettings().getRam().setIncludeRamUpdateRecord(chkIncludeRamUpdateRecord.isSelected());
        root.getRunSettings().getRam().setIncludeRamExpandedMode(chkIncludeRamExpandedMode.isSelected());

        root.getRunSettings().getRam().getMinimumSecurityLevel().setComputedMsl(txtRamMslByte.getText());
        root.getRunSettings().getRam().getMinimumSecurityLevel().setUseCipher(chkRamUseCipher.isSelected());
        root.getRunSettings().getRam().getMinimumSecurityLevel().setCipherAlgo(cmbRamCipherAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRam().getMinimumSecurityLevel().setAuthVerification(cmbRamAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRam().getMinimumSecurityLevel().setSigningAlgo(cmbRamSigningAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRam().getMinimumSecurityLevel().setCounterChecking(cmbRamCounterCheck.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRam().getMinimumSecurityLevel().setPorRequirement(cmbRamPorRequirement.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRam().getMinimumSecurityLevel().setCipherPor(chkRamCipherPor.isSelected());
        root.getRunSettings().getRam().getMinimumSecurityLevel().setPorSecurity(cmbRamPorSecurity.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRam().setTar(txtRamTar.getText());

        root.getRunSettings().getRam().setUseSpecificKeyset(chkUseSpecificKeyset.isSelected());
        SCP80Keyset RamCipheringKeyset = new SCP80Keyset();
        RamCipheringKeyset.setKeysetName(cmbRamCipheringKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(RamCipheringKeyset.getKeysetName())) {
                RamCipheringKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                RamCipheringKeyset.setKeysetType(registeredKeyset.getKeysetType());
                RamCipheringKeyset.setKicValuation(registeredKeyset.getKicValuation());
                RamCipheringKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                RamCipheringKeyset.setKicMode(registeredKeyset.getKicMode());
                RamCipheringKeyset.setKidValuation(registeredKeyset.getKidValuation());
                RamCipheringKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                RamCipheringKeyset.setKidMode(registeredKeyset.getKidMode());
                RamCipheringKeyset.setCmacLength(registeredKeyset.getCmacLength());

                RamCipheringKeyset.setCustomKic(chkRamCustomKic.isSelected());
                if (RamCipheringKeyset.isCustomKic())
                    RamCipheringKeyset.setComputedKic(txtRamCustomKic.getText());
                else
                    RamCipheringKeyset.setComputedKic(registeredKeyset.getComputedKic());

                RamCipheringKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRam().setCipheringKeyset(RamCipheringKeyset);

        SCP80Keyset RamAuthKeyset = new SCP80Keyset();
        RamAuthKeyset.setKeysetName(cmbRamAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(RamAuthKeyset.getKeysetName())) {
                RamAuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                RamAuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                RamAuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                RamAuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                RamAuthKeyset.setKicMode(registeredKeyset.getKicMode());
                RamAuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                RamAuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                RamAuthKeyset.setKidMode(registeredKeyset.getKidMode());
                RamAuthKeyset.setCmacLength(registeredKeyset.getCmacLength());

                RamAuthKeyset.setComputedKic(registeredKeyset.getComputedKic());

                RamAuthKeyset.setCustomKid(chkRamCustomKid.isSelected());
                if (RamAuthKeyset.isCustomKid())
                    RamAuthKeyset.setComputedKid(txtRamCustomKid.getText());
                else
                    RamAuthKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRam().setAuthKeyset(RamAuthKeyset);

        Isd isd = new Isd();
        isd.setMethodForGpCommand(cmbRamMethodForGpCommand.getSelectionModel().getSelectedItem());
        isd.setScpMode(txtRamScpMode.getText());
        isd.setScLevel(cmbRamScLevel.getSelectionModel().getSelectedItem());
        isd.setSecuredState(chkRamSecured.isSelected());
        isd.setCardManagerEnc(cmbIsdEnc.getSelectionModel().getSelectedItem());
        isd.setCardManagerMac(cmbIsdMac.getSelectionModel().getSelectedItem());
        isd.setCardManagerKey(cmbIsdKey.getSelectionModel().getSelectedItem());
        isd.setCardManagerPin(cmbIsdPin.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRam().setIsd(isd);
    }

}
