package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import com.idemia.tec.jkt.cardiotest.service.AmdbService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    @FXML private Label lblDestinationAddress;
    @FXML private Label lblBufferSize;
    @FXML private Label lblNetworkAccessName;
    @FXML private Label lblTransportLevel;
    @FXML private Label lblPort;
    @FXML private TextField txtDestinationAddress;
    @FXML private TextField txtBufferSize;
    @FXML private TextField txtNetworkAccessName;
    @FXML private ComboBox<String> cmbTransportLevel;
    @FXML private TextField txtPort;

    @Autowired private RootLayoutController root;
    @Autowired private CardiotestController cardiotest;
    @Autowired private AmdbService amdbService;

    private boolean checkOpenChannelOk;

    public AmdbController() {}

    @FXML public void initialize() {
        txtMslByte.setText(root.getRunSettings().getAmmendmentB().getSdMsl().getComputedMsl());

        chkUseCipher.setSelected(root.getRunSettings().getAmmendmentB().getSdMsl().isUseCipher());

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
        if (!(cmbCipherAlgo.getItems().size() > 0)) cmbCipherAlgo.getItems().addAll(cipherAlgos);
        cmbCipherAlgo.setValue(root.getRunSettings().getAmmendmentB().getSdMsl().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        if (!(cmbAuthVerif.getItems().size() > 0)) cmbAuthVerif.getItems().addAll(authVerifs);
        cmbAuthVerif.setValue(root.getRunSettings().getAmmendmentB().getSdMsl().getAuthVerification());

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
        if (!(cmbSigningAlgo.getItems().size() > 0)) cmbSigningAlgo.getItems().addAll(signingAlgos);
        cmbSigningAlgo.setValue(root.getRunSettings().getAmmendmentB().getSdMsl().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        if (!(cmbCounterCheck.getItems().size() > 0)) cmbCounterCheck.getItems().addAll(counterCheckings);
        cmbCounterCheck.setValue(root.getRunSettings().getAmmendmentB().getSdMsl().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        if (!(cmbPorRequirement.getItems().size() > 0)) cmbPorRequirement.getItems().addAll(porRequirements);
        cmbPorRequirement.setValue(root.getRunSettings().getAmmendmentB().getSdMsl().getPorRequirement());

        chkCipherPor.setSelected(root.getRunSettings().getAmmendmentB().getSdMsl().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        if (!(cmbPorSecurity.getItems().size() > 0)) cmbPorSecurity.getItems().addAll(porSecurities);
        cmbPorSecurity.setValue(root.getRunSettings().getAmmendmentB().getSdMsl().getPorSecurity());

        txtSdTar.setText(root.getRunSettings().getAmmendmentB().getSdTar());

        // initialize list of available keysets for RFM USIM
        if (root.getRunSettings().getAmmendmentB().getScp80CipherKeyset() != null) {
            cmbCipherKeyset.setValue(root.getRunSettings().getAmmendmentB().getScp80CipherKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getAmmendmentB().getScp80CipherKeyset().getKeysetName())) {
                    lblKic.setText("Kic (hex): " + keyset.getComputedKic());
                    break;
                }
            }
        }
        if (root.getRunSettings().getAmmendmentB().getScp80AuthKeyset() != null) {
            cmbAuthKeyset.setValue(root.getRunSettings().getAmmendmentB().getScp80AuthKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getAmmendmentB().getScp80AuthKeyset().getKeysetName())) {
                    lblKid.setText("Kid (hex): " + keyset.getComputedKid());
                    break;
                }
            }
        }

        txtDestinationAddress.setText(root.getRunSettings().getAmmendmentB().getConnectionParameters().getDestinationAddress());
        txtBufferSize.setText(Integer.toString(root.getRunSettings().getAmmendmentB().getConnectionParameters().getBufferSize()));
        txtNetworkAccessName.setText(root.getRunSettings().getAmmendmentB().getConnectionParameters().getNetworkAcessName());

        // initialize list of transport protocol
        List<String> transportProtocols = new ArrayList<>();
        transportProtocols.add("UDP");
        transportProtocols.add("TCP");
        if (!(cmbTransportLevel.getItems().size() > 0)) cmbTransportLevel.getItems().addAll(transportProtocols);
        cmbTransportLevel.setValue(decodeTransportProtocol(root.getRunSettings().getAmmendmentB().getConnectionParameters().getTransportLevel()));

        txtPort.setText(root.getRunSettings().getAmmendmentB().getConnectionParameters().getPort());

        chkDestinationAddress.setSelected(root.getRunSettings().getAmmendmentB().getConnectionParameters().isUseDestinationAddress());
        handleUseDestinationAddressCheck();

        chkBufferSize.setSelected(root.getRunSettings().getAmmendmentB().getConnectionParameters().isUseBufferSize());
        handleUseBufferSizeCheck();

        chkNetworkAccessName.setSelected(root.getRunSettings().getAmmendmentB().getConnectionParameters().isUseNetworkAccessName());
        handleUseNetworkAccessNameCheck();

        chkTransportLevel.setSelected(root.getRunSettings().getAmmendmentB().getConnectionParameters().isUseTransportLevel());
        handleUseTransportLevelCheck();
    }

    @FXML private void handleCipherKeysetContextMenu() { cmbCipherKeyset.setItems(cardiotest.getScp80KeysetLabels()); }

    @FXML private void handleCipherKeysetSelection() {
        for (SCP80Keyset keyset : root.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbCipherKeyset.getSelectionModel().getSelectedItem())) {
                lblKic.setText("Kic (hex): " + keyset.getComputedKic());
                break;
            }
        }
    }

    @FXML private void handleAuthKeysetContextMenu() { cmbAuthKeyset.setItems(cardiotest.getScp80KeysetLabels()); }

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

    @FXML private void handleUseDestinationAddressCheck() {
        lblDestinationAddress.setDisable(!chkDestinationAddress.isSelected());
        txtDestinationAddress.setDisable(!chkDestinationAddress.isSelected());
    }

    @FXML private void handleUseBufferSizeCheck() {
        lblBufferSize.setDisable(!chkBufferSize.isSelected());
        txtBufferSize.setDisable(!chkBufferSize.isSelected());
    }

    @FXML private void handleUseNetworkAccessNameCheck() {
        lblNetworkAccessName.setDisable(!chkNetworkAccessName.isSelected());
        txtNetworkAccessName.setDisable(!chkNetworkAccessName.isSelected());
    }

    @FXML private void handleUseTransportLevelCheck() {
        lblTransportLevel.setDisable(!chkTransportLevel.isSelected());
        cmbTransportLevel.setDisable(!chkTransportLevel.isSelected());
        lblPort.setDisable(!chkTransportLevel.isSelected());
        txtPort.setDisable(!chkTransportLevel.isSelected());
    }

    private String decodeTransportProtocol(int level) {
        if (level == 1) return "UDP";
        if (level == 2) return "TCP";
        return null;
    }

    private int encodeTransportProtocol(String type) {
        if (type.equals("UDP")) return 1;
        if (type.equals("TCP")) return 2;
        return 0;
    }

    @FXML private void handleCheckOpenChannel() {
        checkOpenChannelOk = amdbService.checkOpenChannel();
        // TODO
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

        if (cmbCipherKeyset.getSelectionModel().getSelectedItem() != null) {
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
        }

        if (cmbAuthKeyset.getSelectionModel().getSelectedItem() != null) {
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
        }

        root.getRunSettings().getAmmendmentB().getConnectionParameters().setUseDestinationAddress(chkDestinationAddress.isSelected());
        root.getRunSettings().getAmmendmentB().getConnectionParameters().setUseBufferSize(chkBufferSize.isSelected());
        root.getRunSettings().getAmmendmentB().getConnectionParameters().setUseNetworkAccessName(chkNetworkAccessName.isSelected());
        root.getRunSettings().getAmmendmentB().getConnectionParameters().setUseTransportLevel(chkTransportLevel.isSelected());
        if (root.getRunSettings().getAmmendmentB().getConnectionParameters().isUseDestinationAddress())
            root.getRunSettings().getAmmendmentB().getConnectionParameters().setDestinationAddress(txtDestinationAddress.getText());
        if (root.getRunSettings().getAmmendmentB().getConnectionParameters().isUseBufferSize())
            root.getRunSettings().getAmmendmentB().getConnectionParameters().setBufferSize(Integer.parseInt(txtBufferSize.getText()));
        if (root.getRunSettings().getAmmendmentB().getConnectionParameters().isUseNetworkAccessName())
            root.getRunSettings().getAmmendmentB().getConnectionParameters().setNetworkAcessName(txtNetworkAccessName.getText());
        if (root.getRunSettings().getAmmendmentB().getConnectionParameters().isUseTransportLevel()) {
            root.getRunSettings().getAmmendmentB().getConnectionParameters().setTransportLevel(encodeTransportProtocol(cmbTransportLevel.getSelectionModel().getSelectedItem()));
            root.getRunSettings().getAmmendmentB().getConnectionParameters().setPort(txtPort.getText());
        }
    }

}
