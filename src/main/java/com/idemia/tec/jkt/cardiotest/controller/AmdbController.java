package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmdbController {

    @FXML private TextField txtSdTar;
    @FXML private ComboBox<String> cmbCipherKeyset;
    @FXML private ComboBox<String> cmbAuthKeyset;
    @FXML private Label lblKic;
    @FXML private Label lblKid;
    @FXML private TextField txtMslByte;
    @FXML private ComboBox<String> cmbCipherAlgo;
    @FXML private CheckBox chkUseCipher;
    @FXML private ComboBox<String> cmbAuthVerif;
    @FXML private ComboBox<String> cmbSigningAlgo;
    @FXML private ComboBox<String> cmbCounterCheck;
    @FXML private ComboBox<String> cmbPorRequirement;
    @FXML private ComboBox<String> cmbPorSecurity;
    @FXML private CheckBox chkCipherPor;
    @FXML private CheckBox chkDestinationAddress;
    @FXML private CheckBox chkBufferSize;
    @FXML private CheckBox chkNetworkAccessName;
    @FXML private CheckBox chkTransportLevel;
    @FXML private TextField txtDestinationAddress;
    @FXML private TextField txtBufferSize;
    @FXML private TextField txtNetworkAccessName;
    @FXML private ComboBox<String> cmbTransportLevel;
    @FXML private TextField txtPort;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;

    public AmdbController() {}

    @FXML public void initialize() {
        // TODO
    }

    @FXML private void handleCipherKeysetContextMenu() {
        cmbCipherKeyset.setItems(cardiotest.getScp80KeysetLabels());
    }

    @FXML private void handleCipherKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbCipherKeyset.getSelectionModel().getSelectedItem())) {
                lblKic.setText("Kic (hex): " + keyset.getComputedKic());
                break;
            }
        }
    }

    @FXML private void handleAuthKeysetContextMenu() {
        cmbAuthKeyset.setItems(cardiotest.getScp80KeysetLabels());
    }

    @FXML private void handleAuthKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbAuthKeyset.getSelectionModel().getSelectedItem())) {
                lblKid.setText("Kid (hex): " + keyset.getComputedKid());
                break;
            }
        }
    }

    @FXML private void handleBtnSetMsl() {
        String mslHexStr = txtMslByte.getText();
        int mslInteger = Integer.parseInt(mslHexStr, 16);
        if (mslInteger > 31) {
            // MSL integer should not be higher than 31 (0x1F)
            Alert mslAlert = new Alert(Alert.AlertType.ERROR);
            mslAlert.initModality(Modality.APPLICATION_MODAL);
            mslAlert.initOwner(txtMslByte.getScene().getWindow());
            mslAlert.setTitle("Minimum Security Level");
            mslAlert.setHeaderText("Invalid MSL");
            mslAlert.setContentText("MSL value should not exceed '1F'");
            mslAlert.showAndWait();
        }
        else {
            // set components accordingly
            if (mslInteger == 0) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("No verification");
                cmbCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 1) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 2) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 3) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Digital Signature");
                cmbCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 4) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("No verification");
                cmbCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 5) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 6) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 7) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Digital Signature");
                cmbCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 8) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("No verification");
                cmbCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 9) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 10) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 11) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Digital Signature");
                cmbCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 12) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("No verification");
                cmbCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 13) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 14) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 15) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Digital Signature");
                cmbCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 16) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("No verification");
                cmbCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 17) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 18) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 19) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Digital Signature");
                cmbCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 20) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("No verification");
                cmbCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 21) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 22) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 23) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Digital Signature");
                cmbCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 24) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("No verification");
                cmbCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 25) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 26) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 27) {
                chkUseCipher.setSelected(false);
                cmbAuthVerif.getSelectionModel().select("Digital Signature");
                cmbCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 28) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("No verification");
                cmbCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 29) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 30) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 31) {
                chkUseCipher.setSelected(true);
                cmbAuthVerif.getSelectionModel().select("Digital Signature");
                cmbCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            // set back MSL text field as it may change due to race condition
            txtMslByte.setText(mslHexStr);
        }
    }

    @FXML private void handleUseCipherCheck() {
        root.getRunSettings().getAmmendmentB().getSdMsl().setUseCipher(chkUseCipher.isSelected());
        root.getRunSettings().getAmmendmentB().getSdMsl().computeMsl();
        txtMslByte.setText(root.getRunSettings().getAmmendmentB().getSdMsl().getComputedMsl());
    }

    @FXML private void handleAuthVerifSelection() {
        root.getRunSettings().getAmmendmentB().getSdMsl().setAuthVerification(cmbAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAmmendmentB().getSdMsl().computeMsl();
        txtMslByte.setText(root.getRunSettings().getAmmendmentB().getSdMsl().getComputedMsl());
    }

    @FXML private void handleCounterCheckSelection() {
        root.getRunSettings().getAmmendmentB().getSdMsl().setCounterChecking(cmbCounterCheck.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAmmendmentB().getSdMsl().computeMsl();
        txtMslByte.setText(root.getRunSettings().getAmmendmentB().getSdMsl().getComputedMsl());
    }

    public void saveControlState() {
        root.getRunSettings().getAmmendmentB().getSdMsl().setComputedMsl(txtMslByte.getText());
        root.getRunSettings().getAmmendmentB().getSdMsl().setUseCipher(chkUseCipher.isSelected());
        root.getRunSettings().getAmmendmentB().getSdMsl().setCipherAlgo(cmbCipherAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAmmendmentB().getSdMsl().setAuthVerification(cmbAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAmmendmentB().getSdMsl().setSigningAlgo(cmbSigningAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAmmendmentB().getSdMsl().setCounterChecking(cmbCounterCheck.getSelectionModel().getSelectedItem());

        root.getRunSettings().getAmmendmentB().getSdMsl().setPorRequirement(cmbPorRequirement.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAmmendmentB().getSdMsl().setCipherPor(chkCipherPor.isSelected());
        root.getRunSettings().getAmmendmentB().getSdMsl().setPorSecurity(cmbPorSecurity.getSelectionModel().getSelectedItem());

        root.getRunSettings().getAmmendmentB().setSdTar(txtSdTar.getText());

        SCP80Keyset scp80CipherKeyset = new SCP80Keyset();
        scp80CipherKeyset.setKeysetName(cmbCipherKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(scp80CipherKeyset.getKeysetName())) {
                scp80CipherKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                scp80CipherKeyset.setKeysetType(registeredKeyset.getKeysetType());
                scp80CipherKeyset.setKicValuation(registeredKeyset.getKicValuation());
                scp80CipherKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                scp80CipherKeyset.setKicMode(registeredKeyset.getKicMode());
                scp80CipherKeyset.setComputedKic(registeredKeyset.getComputedKic());
                scp80CipherKeyset.setKidValuation(registeredKeyset.getKidValuation());
                scp80CipherKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                scp80CipherKeyset.setKidMode(registeredKeyset.getKidMode());
                scp80CipherKeyset.setComputedKid(registeredKeyset.getComputedKid());
                scp80CipherKeyset.setCmacLength(registeredKeyset.getCmacLength());
                break;
            }
        }
        root.getRunSettings().getAmmendmentB().setScp80CipherKeyset(scp80CipherKeyset);

        SCP80Keyset scp80AuthKeyset = new SCP80Keyset();
        scp80AuthKeyset.setKeysetName(cmbAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : root.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(scp80AuthKeyset.getKeysetName())) {
                scp80AuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                scp80AuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                scp80AuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                scp80AuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                scp80AuthKeyset.setKicMode(registeredKeyset.getKicMode());
                scp80AuthKeyset.setComputedKic(registeredKeyset.getComputedKic());
                scp80AuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                scp80AuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                scp80AuthKeyset.setKidMode(registeredKeyset.getKidMode());
                scp80AuthKeyset.setComputedKid(registeredKeyset.getComputedKid());
                scp80AuthKeyset.setCmacLength(registeredKeyset.getCmacLength());
                break;
            }
        }
        root.getRunSettings().getAmmendmentB().setScp80AuthKeyset(scp80AuthKeyset);

        root.getRunSettings().getAmmendmentB().getConnectionParameters().setUseDestinationAddress(chkDestinationAddress.isSelected());
        root.getRunSettings().getAmmendmentB().getConnectionParameters().setUseBufferSize(chkBufferSize.isSelected());
        root.getRunSettings().getAmmendmentB().getConnectionParameters().setUseNetworkAccessName(chkNetworkAccessName.isSelected());
        root.getRunSettings().getAmmendmentB().getConnectionParameters().setUseTransportLevel(chkTransportLevel.isSelected());
        if (root.getRunSettings().getAmmendmentB().getConnectionParameters().useDestinationAddress())
            root.getRunSettings().getAmmendmentB().getConnectionParameters().setDestinationAddress(txtDestinationAddress.getText());
        if (root.getRunSettings().getAmmendmentB().getConnectionParameters().useBufferSize())
            root.getRunSettings().getAmmendmentB().getConnectionParameters().setBufferSize(Integer.parseInt(txtBufferSize.getText()));
        if (root.getRunSettings().getAmmendmentB().getConnectionParameters().useNetworkAccessName())
            root.getRunSettings().getAmmendmentB().getConnectionParameters().setNetworkAcessName(txtNetworkAccessName.getText());
        if (root.getRunSettings().getAmmendmentB().getConnectionParameters().useTransportLevel()) {
            
        }
        // TODO
    }

}
