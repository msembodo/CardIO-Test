package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import com.idemia.tec.jkt.cardiotest.model.AdvSaveVariable;
import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class CardiotestController {

    @FXML
    private TableView<AdvSaveVariable> tblAdvSave;
    @FXML
    private TableColumn<AdvSaveVariable, String> clmnDefined;
    @FXML
    private TableColumn<AdvSaveVariable, String> clmnValue;
    @FXML
    private TableView<VariableMapping> tblMapping;
    @FXML
    private TableColumn<VariableMapping, String> clmnMappedTo;
    @FXML
    private TableColumn<VariableMapping, String> clmnMccVar;
    @FXML
    private TextField txtMappedTo;
    @FXML
    private ComboBox<String> cmbMccVar;
    @FXML
    private StackPane stackPane;

    private MaskerPane maskerPane;

    @FXML
    private CheckBox chkFixedVal;
    @FXML
    private TextField txtMccVar;

    // project details tab
    @FXML
    private TextField txtProjectFolder;
    @FXML
    private TextField txtRequestId;
    @FXML
    private TextField txtRequestName;
    @FXML
    private TextField txtProfileName;
    @FXML
    private TextField txtProfileVersion;
    @FXML
    private TextField txtCardImageItemId;
    @FXML
    private TextField txtCustomer;
    @FXML
    private TextField txtDeveloperName;
    @FXML
    private TextField txtTesterName;

    private List<ComboBox<String>> comboPool;
    private ObservableList<String> mappedVariables;

    // ATR box
    @FXML
    private TextField txtAtr;
    @FXML
    private CheckBox chkIncludeAtr;
    @FXML
    private Label lblProtocol;

    // card parameters
    @FXML
    private TextField txtCardManagerAid;
    @FXML
    private TextField txtUsimAid;
    @FXML
    private TextField txtDfUsim;
    @FXML
    private TextField txtDfGsmAccess;
    @FXML
    private TextField txtDfTelecom;
    @FXML
    private TextField txtIsimAid;
    @FXML
    private TextField txtDfIsim;
    @FXML
    private TextField txtCsimAid;
    @FXML
    private TextField txtDfCsim;

    // secret code tab
    @FXML
    private CheckBox chkPin1Disabled;
    @FXML
    private CheckBox chkPin2Disabled;
    @FXML
    private CheckBox chkInclude3gScript;
    @FXML
    private CheckBox chkInclude2gScript;
    @FXML
    private ComboBox<String> cmbGpin;
    @FXML
    private ComboBox<String> cmbLpin;
    @FXML
    private ComboBox<String> cmbGpuk;
    @FXML
    private ComboBox<String> cmbLpuk;
    @FXML
    private TextField txtGpinRetries;
    @FXML
    private TextField txtLpinRetries;
    @FXML
    private TextField txtGpukRetries;
    @FXML
    private TextField txtLpukRetries;
    @FXML
    private CheckBox chkBlockGpuk;
    @FXML
    private CheckBox chkBlockLpuk;
    @FXML
    private ComboBox<String> cmbChv1;
    @FXML
    private ComboBox<String> cmbChv2;
    @FXML
    private ComboBox<String> cmbPuk1;
    @FXML
    private ComboBox<String> cmbPuk2;
    @FXML
    private TextField txtChv1Retries;
    @FXML
    private TextField txtChv2Retries;
    @FXML
    private TextField txtPuk1Retries;
    @FXML
    private TextField txtPuk2Retries;
    @FXML
    private CheckBox chkBlockPuk1;
    @FXML
    private CheckBox chkBlockPuk2;
    @FXML
    private Label lblIsc2;
    @FXML
    private Label lblIsc3;
    @FXML
    private Label lblIsc4;
    @FXML
    private ComboBox<String> cmbIsc1;
    @FXML
    private ComboBox<String> cmbIsc2;
    @FXML
    private ComboBox<String> cmbIsc3;
    @FXML
    private ComboBox<String> cmbIsc4;
    @FXML
    private Label lblIsc2Retries;
    @FXML
    private Label lblIsc3Retries;
    @FXML
    private Label lblIsc4Retries;
    @FXML
    private TextField txtIsc1Retries;
    @FXML
    private TextField txtIsc2Retries;
    @FXML
    private TextField txtIsc3Retries;
    @FXML
    private TextField txtIsc4Retries;
    @FXML
    private CheckBox chkUseIsc2;
    @FXML
    private CheckBox chkUseIsc3;
    @FXML
    private CheckBox chkUseIsc4;

    // authentication tab
    @FXML
    private CheckBox chkIncludeDeltaTest;
    @FXML
    private CheckBox chkIncludeSqnMax;
    @FXML
    private TextField txtResLength;
    @FXML
    private ComboBox<String> cmbAkaC1;
    @FXML
    private ComboBox<String> cmbAkaC2;
    @FXML
    private ComboBox<String> cmbAkaC3;
    @FXML
    private ComboBox<String> cmbAkaC4;
    @FXML
    private ComboBox<String> cmbAkaC5;
    @FXML
    private ComboBox<String> cmbAkaRi;
    @FXML
    private TextField txtRand;
    @FXML
    private TextField txtSqn;
    @FXML
    private TextField txtSqnMax;
    @FXML
    private TextField txtDelta;
    @FXML
    private TextField txtAmf;
    @FXML
    private ComboBox<String> cmbKi;
    @FXML
    private ComboBox<String> cmbOpc;
    @FXML
    private CheckBox chkComp1282;
    @FXML
    private CheckBox chkComp1283;
    @FXML
    private CheckBox chkMilenage;
    @FXML
    private CheckBox chkIsimAuth;
    @FXML
    private CheckBox chkGsmAlgo;

    // OTA settings tab
    @FXML
    private TableView<SCP80Keyset> tblScp80Keyset;
    @FXML
    private TableColumn<SCP80Keyset, String> clmnKeysetName;
    @FXML
    private TextField txtKeysetName;
    @FXML
    private ComboBox<String> cmbKeysetVersion;
    @FXML
    private ComboBox<String> cmbKeysetType;
    @FXML
    private ComboBox<String> cmbKicValuation;
    @FXML
    private ComboBox<String> cmbKicLength;
    @FXML
    private ComboBox<String> cmbKicMode;
    @FXML
    private ComboBox<String> cmbKidValuation;
    @FXML
    private ComboBox<String> cmbKidLength;
    @FXML
    private ComboBox<String> cmbKidMode;
    @FXML
    private Label lblCmacLength;
    @FXML
    private ComboBox<String> cmbCmacLength;
    @FXML
    private Label lblAddKeysetErrMsg;
    @FXML
    private TextField txtUdhiFirstByte;
    @FXML
    private TextField txtScAddress;
    @FXML
    private TextField txtTpPid;
    @FXML
    private CheckBox chkTpOa;
    @FXML
    private Label lblTpOa;
    @FXML
    private TextField txtTpOa;
    @FXML
    private ComboBox<String> cmbPorFormat;

    private ObservableList<String> scp80KeysetLabels;

    // RFM USIM tab
    @FXML
    private CheckBox chkIncludeRfmUsim;
    @FXML
    private CheckBox chkIncludeRfmUsimUpdateRecord;
    @FXML
    private CheckBox chkIncludeRfmUsimExpandedMode;
    @FXML
    private TextField txtRfmUsimMslByte;
    @FXML
    private ComboBox<String> cmbRfmUsimCipherAlgo;
    @FXML
    private CheckBox chkRfmUsimUseCipher;
    @FXML
    private ComboBox<String> cmbRfmUsimAuthVerif;
    @FXML
    private ComboBox<String> cmbRfmUsimSigningAlgo;
    @FXML
    private ComboBox<String> cmbRfmUsimPorRequirement;
    @FXML
    private ComboBox<String> cmbRfmUsimPorSecurity;
    @FXML
    private CheckBox chkRfmUsimCipherPor;
    @FXML
    private ComboBox<String> cmbRfmUsimCounterCheck;
    @FXML
    private TextField txtRfmUsimTar;
    @FXML
    private TextField txtRfmUsimTargetEf;
    @FXML
    private TextField txtRfmUsimTargetEfBadCase;
    @FXML
    private CheckBox chkRfmUsimFullAccess;
    @FXML
    private Label lblRfmUsimCustomTarget;
    @FXML
    private ComboBox<String> cmbRfmUsimCustomTargetAcc;
    @FXML
    private TextField txtRfmUsimCustomTargetEf;
    @FXML
    private Label lblRfmUsimCustomTargetBadCase;
    @FXML
    private ComboBox<String> cmbRfmUsimCustomTargetAccBadCase;
    @FXML
    private TextField txtRfmUsimCustomTargetEfBadCase;
    @FXML
    private CheckBox chkUseSpecificKeyset;
    @FXML
    private Label lblRfmUsimCipheringKeyset;
    @FXML
    private ComboBox<String> cmbRfmUsimCipheringKeyset;
    @FXML
    private Label lblRfmUsimKic;
    @FXML
    private CheckBox chkRfmUsimCustomKic;
    @FXML
    private TextField txtRfmUsimCustomKic;
    @FXML
    private Label lblRfmUsimAuthKeyset;
    @FXML
    private ComboBox<String> cmbRfmUsimAuthKeyset;
    @FXML
    private Label lblRfmUsimKid;
    @FXML
    private CheckBox chkRfmUsimCustomKid;
    @FXML
    private TextField txtRfmUsimCustomKid;

    // bottom tab pane
    @FXML
    private TabPane tabBottom;
    @FXML
    private TextFlow txtInterpretedLog;
    @FXML
    private TextArea txtCommandResponse;

    static Logger logger = Logger.getLogger(CardiotestController.class);

    private CardiotestApplication application;

    @Autowired
    private RootLayoutController root;

    public CardiotestController() {}

    public void setMainApp(CardiotestApplication application) {
        this.application = application;
    }

    public void setObservableList() {
        // add observable list data to adv save & mapping tables
        tblAdvSave.setItems(application.getAdvSaveVariables());
        tblMapping.setItems(application.getMappings());

        // load variables from saved settings
        File loadedVarFile = new File(root.getRunSettings().getAdvSaveVariablesPath());
        if (loadedVarFile != null) {
            try {
                Scanner scanner = new Scanner(loadedVarFile);
                List<String> definedVariables = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith(".DEFINE"))
                        definedVariables.add(line);
                }
                root.getRunSettings().setAdvSaveVariablesPath(loadedVarFile.getAbsolutePath());
                logger.info(String.format("Variable file selected: %s", loadedVarFile.getAbsolutePath()));
                root.getAppStatusBar().setText("Variables loaded.");
                for (String line : definedVariables) {
                    String[] components = line.split("\\s+");
                    application.getAdvSaveVariables().add(
                            new AdvSaveVariable(components[1].substring(1), components[2])
                    );
                    cmbMccVar.getItems().add(components[1].substring(1));
                }
            } catch (FileNotFoundException e) {
                logger.warn("Can't find variable file. Please select from MCC Advance Save.");
                root.getAppStatusBar().setText("Can't find variable file. Please select from MCC Advance Save.");
//                e.printStackTrace();
            }
        }

        // load mappings from saved settings
        for (VariableMapping mapping : root.getRunSettings().getVariableMappings())
            application.getMappings().add(mapping);

        // SCP80 keyset table
        tblScp80Keyset.setItems(application.getScp80Keysets());
        // load keysets from saved settings
        for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets())
            application.getScp80Keysets().add(keyset);
        // select first row initially
        tblScp80Keyset.getSelectionModel().select(0);

        // add authentication RiCi default values
        if (application.getMappings().size() == 0) {
            application.getMappings().add(new VariableMapping("C1", null, "00000000000000000000000000000000", true));
            application.getMappings().add(new VariableMapping("C2", null, "00000000000000000000000000000001", true));
            application.getMappings().add(new VariableMapping("C3", null, "00000000000000000000000000000002", true));
            application.getMappings().add(new VariableMapping("C4", null, "00000000000000000000000000000004", true));
            application.getMappings().add(new VariableMapping("C5", null, "00000000000000000000000000000008", true));
            application.getMappings().add(new VariableMapping("RI", null, "4000204060", true));
            application.getMappings().add(new VariableMapping("K1", null, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", true));
            application.getMappings().add(new VariableMapping("OPC", null, "BAEBC618A55C351F25CEDF37BF70F390", true));
            for (VariableMapping mapping : application.getMappings())
                mappedVariables.add(mapping.getMappedVariable());
        }
    }

    @FXML
    private void initialize() {
        // initialize variable table
        clmnDefined.setCellValueFactory(celldata -> celldata.getValue().definedVariable());
        clmnValue.setCellValueFactory(celldata -> celldata.getValue().value());

        // initialize mapping table
        clmnMappedTo.setCellValueFactory(celldata -> celldata.getValue().mappedVariable());
        clmnMccVar.setCellValueFactory(celldata -> celldata.getValue().mccVariable());

        // clear mapping display
        showMappings(null);

        // listen for selection changes and show mapping detail when changed
        tblMapping.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> showMappings(newValue))
        );
        chkFixedVal.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (chkFixedVal.isSelected()) {
                cmbMccVar.setDisable(true);
                txtMccVar.setDisable(false);
            } else {
                cmbMccVar.setDisable(false);
                txtMccVar.setDisable(true);
            }
        });

        mappedVariables = FXCollections.observableArrayList();
        for (VariableMapping mapping : root.getRunSettings().getVariableMappings())
            mappedVariables.add(mapping.getMappedVariable());

        comboPool = new ArrayList<>();

        // for masking main window while verification is executing
        maskerPane = new MaskerPane();
        maskerPane.setVisible(false);
        stackPane.getChildren().add(maskerPane);

        tabBottom.getSelectionModel().select(0);

        Font fixedWidthFont;
        if (Font.getFamilies().contains("Consolas"))
            fixedWidthFont = Font.font("Consolas");
        else
            fixedWidthFont = Font.font("Monospaced");
        txtCommandResponse.setFont(fixedWidthFont);
        txtCommandResponse.setEditable(false);
        txtCommandResponse.setDisable(true);

        // restore values of controls

        // project details
        txtProjectFolder.setText(root.getRunSettings().getProjectPath());
        txtRequestId.setText(root.getRunSettings().getRequestId());
        txtRequestName.setText(root.getRunSettings().getRequestName());
        txtProfileName.setText(root.getRunSettings().getProfileName());
        txtProfileVersion.setText(Integer.toString(root.getRunSettings().getProfileVersion()));
        txtCardImageItemId.setText(root.getRunSettings().getCardImageItemId());
        txtCustomer.setText(root.getRunSettings().getCustomer());
        txtDeveloperName.setText(root.getRunSettings().getDeveloperName());
        txtTesterName.setText(root.getRunSettings().getTesterName());

        // ATR box

        chkIncludeAtr.setSelected(root.getRunSettings().getAtr().isIncludeAtr());
        handleIncludeAtrCheck();

        txtAtr.setText(root.getRunSettings().getAtr().getAtrString());

        // card parameters

        txtCardManagerAid.setText(root.getRunSettings().getCardParameters().getCardManagerAid());
        txtUsimAid.setText(root.getRunSettings().getCardParameters().getUsimAid());
        txtDfUsim.setText(root.getRunSettings().getCardParameters().getDfUsim());
        txtDfGsmAccess.setText(root.getRunSettings().getCardParameters().getDfGsmAccess());
        txtDfTelecom.setText(root.getRunSettings().getCardParameters().getDfTelecom());
        txtIsimAid.setText(root.getRunSettings().getCardParameters().getIsimAid());
        txtDfIsim.setText(root.getRunSettings().getCardParameters().getDfIsim());
        txtCsimAid.setText(root.getRunSettings().getCardParameters().getCsimAid());
        txtDfCsim.setText(root.getRunSettings().getCardParameters().getDfCsim());

        // secret codes

        chkPin1Disabled.setSelected(root.getRunSettings().getSecretCodes().isPin1disabled());
        chkPin2Disabled.setSelected(root.getRunSettings().getSecretCodes().isPin2disabled());

        chkInclude3gScript.setSelected(root.getRunSettings().getSecretCodes().isInclude3gScript());
        handleInclude3gScriptCheck();

        chkInclude2gScript.setSelected(root.getRunSettings().getSecretCodes().isInclude2gScript());
        handleInclude2gScriptCheck();

        cmbGpin.setItems(mappedVariables);
        registerForComboUpdate(cmbGpin);
        if (root.getRunSettings().getSecretCodes().getGpin() != null)
            cmbGpin.getSelectionModel().select(root.getRunSettings().getSecretCodes().getGpin());
        if (root.getRunSettings().getSecretCodes().getGpinRetries() != 0)
            txtGpinRetries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getGpinRetries()));

        cmbLpin.setItems(mappedVariables);
        registerForComboUpdate(cmbLpin);
        if (root.getRunSettings().getSecretCodes().getLpin() != null)
            cmbLpin.getSelectionModel().select(root.getRunSettings().getSecretCodes().getLpin());
        if (root.getRunSettings().getSecretCodes().getLpinRetries() != 0)
            txtLpinRetries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getLpinRetries()));

        cmbGpuk.setItems(mappedVariables);
        registerForComboUpdate(cmbGpuk);
        if (root.getRunSettings().getSecretCodes().getGpuk() != null)
            cmbGpuk.getSelectionModel().select(root.getRunSettings().getSecretCodes().getGpuk());
        if (root.getRunSettings().getSecretCodes().getGpukRetries() != 0)
            txtGpukRetries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getGpukRetries()));

        cmbLpuk.setItems(mappedVariables);
        registerForComboUpdate(cmbLpuk);
        if (root.getRunSettings().getSecretCodes().getLpuk() != null)
            cmbLpuk.getSelectionModel().select(root.getRunSettings().getSecretCodes().getLpuk());
        if (root.getRunSettings().getSecretCodes().getLpukRetries() != 0)
            txtLpukRetries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getLpukRetries()));

        cmbChv1.setItems(mappedVariables);
        registerForComboUpdate(cmbChv1);
        if (root.getRunSettings().getSecretCodes().getChv1() != null)
            cmbChv1.getSelectionModel().select(root.getRunSettings().getSecretCodes().getChv1());
        if (root.getRunSettings().getSecretCodes().getChv1Retries() != 0)
            txtChv1Retries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getChv1Retries()));

        cmbChv2.setItems(mappedVariables);
        registerForComboUpdate(cmbChv2);
        if (root.getRunSettings().getSecretCodes().getChv2() != null)
            cmbChv2.getSelectionModel().select(root.getRunSettings().getSecretCodes().getChv2());
        if (root.getRunSettings().getSecretCodes().getChv2Retries() != 0)
            txtChv2Retries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getChv2Retries()));

        cmbPuk1.setItems(mappedVariables);
        registerForComboUpdate(cmbPuk1);
        if (root.getRunSettings().getSecretCodes().getPuk1() != null)
            cmbPuk1.getSelectionModel().select(root.getRunSettings().getSecretCodes().getPuk1());
        if (root.getRunSettings().getSecretCodes().getPuk1Retries() != 0)
            txtPuk1Retries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getPuk1Retries()));

        cmbPuk2.setItems(mappedVariables);
        registerForComboUpdate(cmbPuk2);
        if (root.getRunSettings().getSecretCodes().getPuk2() != null)
            cmbPuk2.getSelectionModel().select(root.getRunSettings().getSecretCodes().getPuk2());
        if (root.getRunSettings().getSecretCodes().getPuk2Retries() != 0)
            txtPuk2Retries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getPuk2Retries()));

        chkBlockGpuk.setSelected(root.getRunSettings().getSecretCodes().isBlockGpuk());
        chkBlockLpuk.setSelected(root.getRunSettings().getSecretCodes().isBlockLpuk());
        chkBlockPuk1.setSelected(root.getRunSettings().getSecretCodes().isBlockPuk1());
        chkBlockPuk2.setSelected(root.getRunSettings().getSecretCodes().isBlockPuk2());

        cmbIsc1.setItems(mappedVariables);
        registerForComboUpdate(cmbIsc1);
        if (root.getRunSettings().getSecretCodes().getIsc1() != null)
            cmbIsc1.getSelectionModel().select(root.getRunSettings().getSecretCodes().getIsc1());
        if (root.getRunSettings().getSecretCodes().getIsc1Retries() != 0)
            txtIsc1Retries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getIsc1Retries()));

        cmbIsc2.setItems(mappedVariables);
        registerForComboUpdate(cmbIsc2);
        if (root.getRunSettings().getSecretCodes().getIsc2() != null)
            cmbIsc2.getSelectionModel().select(root.getRunSettings().getSecretCodes().getIsc2());
        if (root.getRunSettings().getSecretCodes().getIsc2Retries() != 0)
            txtIsc2Retries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getIsc2Retries()));
        chkUseIsc2.setSelected(root.getRunSettings().getSecretCodes().isUseIsc2());
        handleUseIsc2Check();

        cmbIsc3.setItems(mappedVariables);
        registerForComboUpdate(cmbIsc3);
        if (root.getRunSettings().getSecretCodes().getIsc3() != null)
            cmbIsc3.getSelectionModel().select(root.getRunSettings().getSecretCodes().getIsc3());
        if (root.getRunSettings().getSecretCodes().getIsc3Retries() != 0)
            txtIsc3Retries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getIsc3Retries()));
        chkUseIsc3.setSelected(root.getRunSettings().getSecretCodes().isUseIsc3());
        handleUseIsc3Check();

        cmbIsc4.setItems(mappedVariables);
        registerForComboUpdate(cmbIsc4);
        if (root.getRunSettings().getSecretCodes().getIsc4() != null)
            cmbIsc4.getSelectionModel().select(root.getRunSettings().getSecretCodes().getIsc4());
        if (root.getRunSettings().getSecretCodes().getIsc4Retries() != 0)
            txtIsc4Retries.setText(Integer.toString(root.getRunSettings().getSecretCodes().getIsc4Retries()));
        chkUseIsc4.setSelected(root.getRunSettings().getSecretCodes().isUseIsc4());
        handleUseIsc4Check();

        // authentication

        chkIncludeDeltaTest.setSelected(root.getRunSettings().getAuthentication().isIncludeDeltaTest());
        handleIncludeDeltaTestCheck();

        chkIncludeSqnMax.setSelected(root.getRunSettings().getAuthentication().isIncludeSqnMax());
        handleIncludeSqnMaxCheck();

        txtResLength.setText(root.getRunSettings().getAuthentication().getResLength());

        cmbAkaC1.setItems(mappedVariables);
        registerForComboUpdate(cmbAkaC1);
        cmbAkaC1.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC1());

        cmbAkaC2.setItems(mappedVariables);
        registerForComboUpdate(cmbAkaC2);
        cmbAkaC2.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC2());

        cmbAkaC3.setItems(mappedVariables);
        registerForComboUpdate(cmbAkaC3);
        cmbAkaC3.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC3());

        cmbAkaC4.setItems(mappedVariables);
        registerForComboUpdate(cmbAkaC4);
        cmbAkaC4.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC4());

        cmbAkaC5.setItems(mappedVariables);
        registerForComboUpdate(cmbAkaC5);
        cmbAkaC5.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaC5());

        cmbAkaRi.setItems(mappedVariables);
        registerForComboUpdate(cmbAkaRi);
        cmbAkaRi.getSelectionModel().select(root.getRunSettings().getAuthentication().getAkaRi());

        txtRand.setText(root.getRunSettings().getAuthentication().getRand());
        txtSqn.setText(root.getRunSettings().getAuthentication().getSqn());
        txtSqnMax.setText(root.getRunSettings().getAuthentication().getSqnMax());
        txtDelta.setText(root.getRunSettings().getAuthentication().getDelta());
        txtAmf.setText(root.getRunSettings().getAuthentication().getAmf());

        cmbKi.setItems(mappedVariables);
        registerForComboUpdate(cmbKi);
        cmbKi.getSelectionModel().select(root.getRunSettings().getAuthentication().getKi());

        cmbOpc.setItems(mappedVariables);
        registerForComboUpdate(cmbOpc);
        cmbOpc.getSelectionModel().select(root.getRunSettings().getAuthentication().getOpc());

        chkComp1282.setSelected(root.getRunSettings().getAuthentication().isComp1282());
        chkComp1283.setSelected(root.getRunSettings().getAuthentication().isComp1283());
        chkMilenage.setSelected(root.getRunSettings().getAuthentication().isMilenage());
        chkIsimAuth.setSelected(root.getRunSettings().getAuthentication().isIsimAuth());
        chkGsmAlgo.setSelected(root.getRunSettings().getAuthentication().isGsmAlgo());

        // OTA settings

        clmnKeysetName.setCellValueFactory(celldata -> celldata.getValue().keysetNameProperty());
        // clear keyset fields
        showKeyset(null);
        // listen for selection changes and show keyset detail when changed
        tblScp80Keyset.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showKeyset(newValue)
        );
        // initialize list of versions
        for (int i = 0; i < 15; i++)
            cmbKeysetVersion.getItems().add(Integer.toString(i + 1));

        // initialize list of types
        List<String> keysetTypes = new ArrayList<>();
        keysetTypes.add("Algorithm known implicitly by both entities");
        keysetTypes.add("DES");
        keysetTypes.add("AES");
        keysetTypes.add("Proprietary Implementations");
        cmbKeysetType.getItems().addAll(keysetTypes);

        // initialize list of cipher modes
        List<String> cipherBlockModes = new ArrayList<>();
        cipherBlockModes.add("DES - CBC");
        cipherBlockModes.add("3DES - CBC 2 keys");
        cipherBlockModes.add("3DES - CBC 3 keys");
        cipherBlockModes.add("DES - ECB");
        cipherBlockModes.add("AES - CBC");

        cmbKicValuation.setItems(mappedVariables);
        registerForComboUpdate(cmbKicValuation);

        // initialize available key space
        List<String> scp80KeyLengths = new ArrayList<>();
        scp80KeyLengths.add("8");
        scp80KeyLengths.add("16");
        scp80KeyLengths.add("24");
        scp80KeyLengths.add("32");

        cmbKicLength.getItems().addAll(scp80KeyLengths);

        cmbKicMode.getItems().addAll(cipherBlockModes);

        // initialize list of crypto checksum modes
        List<String> ccBlockModes = new ArrayList<>();
        ccBlockModes.add("DES - CBC");
        ccBlockModes.add("3DES - CBC 2 keys");
        ccBlockModes.add("3DES - CBC 3 keys");
        ccBlockModes.add("DES - ECB");
        ccBlockModes.add("AES - CMAC");

        cmbKidValuation.setItems(mappedVariables);
        registerForComboUpdate(cmbKidValuation);

        cmbKidLength.getItems().addAll(scp80KeyLengths);

        cmbKidMode.getItems().addAll(ccBlockModes);

        // initialize CMAC lengths
        List<String> cmacLengths = new ArrayList<>();
        cmacLengths.add("4");
        cmacLengths.add("8");
        cmbCmacLength.getItems().addAll(cmacLengths);

        scp80KeysetLabels = FXCollections.observableArrayList();
        for (SCP80Keyset scp80Keyset : root.getRunSettings().getScp80Keysets())
            scp80KeysetLabels.add(scp80Keyset.getKeysetName());

        txtUdhiFirstByte.setText(root.getRunSettings().getSmsUpdate().getUdhiFirstByte());
        txtScAddress.setText(root.getRunSettings().getSmsUpdate().getScAddress());
        txtTpPid.setText(root.getRunSettings().getSmsUpdate().getTpPid());
        chkTpOa.setSelected(root.getRunSettings().getSmsUpdate().isUseWhiteList());
        handleTpOaCheck();
        txtTpOa.setText(root.getRunSettings().getSmsUpdate().getTpOa());

        // initialize list of PoR format
        List<String> porFormats = new ArrayList<>();
        porFormats.add("PoR as SMS-DELIVER-REPORT");
        porFormats.add("PoR as SMS-SUBMIT");
        cmbPorFormat.getItems().addAll(porFormats);
        cmbPorFormat.setValue(root.getRunSettings().getSmsUpdate().getPorFormat());

        // RFM USIM

        chkIncludeRfmUsim.setSelected(root.getRunSettings().getRfmUsim().isIncludeRfmUsim());
        handleIncludeRfmUsimCheck();

        chkIncludeRfmUsimUpdateRecord.setSelected(root.getRunSettings().getRfmUsim().isIncludeRfmUsimUpdateRecord());
        handleIncludeRfmUsimUpdateRecordCheck();

        chkIncludeRfmUsimExpandedMode.setSelected(root.getRunSettings().getRfmUsim().isIncludeRfmUsimExpandedMode());
        handleIncludeRfmUsimExpandedModeCheck();

        // RFM USIM MSL

        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());

        chkRfmUsimUseCipher.setSelected(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().isUseCipher());

        // initialize list of cipher algorithm
        List<String> cipherAlgos = new ArrayList<>();
        cipherAlgos.add("no cipher");
        cipherAlgos.add("DES - CBC");
        cipherAlgos.add("AES - CBC");
        cipherAlgos.add("XOR");
        cipherAlgos.add("3DES - CBC 2 keys");
        cipherAlgos.add("3DES - CBC 3 keys");
        cipherAlgos.add("DES - ECB");
        cmbRfmUsimCipherAlgo.getItems().addAll(cipherAlgos);
        cmbRfmUsimCipherAlgo.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getCipherAlgo());

        // initialize list of auth verification
        List<String> authVerifs = new ArrayList<>();
        authVerifs.add("No verification");
        authVerifs.add("Redundancy Check");
        authVerifs.add("Cryptographic Checksum");
        authVerifs.add("Digital Signature");
        cmbRfmUsimAuthVerif.getItems().addAll(authVerifs);
        cmbRfmUsimAuthVerif.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getAuthVerification());

        // initialize list of signing algorithm
        List<String> signingAlgos = new ArrayList<>();
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
        cmbRfmUsimSigningAlgo.getItems().addAll(signingAlgos);
        cmbRfmUsimSigningAlgo.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getSigningAlgo());

        // initialize list of counter checking
        List<String> counterCheckings = new ArrayList<>();
        counterCheckings.add("No counter available");
        counterCheckings.add("Counter available no checking");
        counterCheckings.add("Counter must be higher");
        counterCheckings.add("Counter must be one higher");
        cmbRfmUsimCounterCheck.getItems().addAll(counterCheckings);
        cmbRfmUsimCounterCheck.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getCounterChecking());

        // initialize list of PoR requirement
        List<String> porRequirements = new ArrayList<>();
        porRequirements.add("No PoR");
        porRequirements.add("PoR required");
        porRequirements.add("PoR only if error");
        cmbRfmUsimPorRequirement.getItems().addAll(porRequirements);
        cmbRfmUsimPorRequirement.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getPorRequirement());

        chkRfmUsimCipherPor.setSelected(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().isCipherPor());

        // initialize list of PoR security
        List<String> porSecurities = new ArrayList<>();
        porSecurities.add("response with no security");
        porSecurities.add("response with RC");
        porSecurities.add("response with CC");
        porSecurities.add("response with DS");
        cmbRfmUsimPorSecurity.getItems().addAll(porSecurities);
        cmbRfmUsimPorSecurity.setValue(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getPorSecurity());

        // RFM USIM parameters

        txtRfmUsimTar.setText(root.getRunSettings().getRfmUsim().getTar());
        txtRfmUsimTargetEf.setText(root.getRunSettings().getRfmUsim().getTargetEf());
        txtRfmUsimTargetEfBadCase.setText(root.getRunSettings().getRfmUsim().getTargetEfBadCase());

        cmbRfmUsimCustomTargetAcc.setItems(mappedVariables);
        registerForComboUpdate(cmbRfmUsimCustomTargetAcc);
        if (root.getRunSettings().getRfmUsim().getCustomTargetAcc() != null)
            cmbRfmUsimCustomTargetAcc.getSelectionModel().select(root.getRunSettings().getRfmUsim().getCustomTargetAcc());
        if (root.getRunSettings().getRfmUsim().getCustomTargetEf() != null)
            txtRfmUsimCustomTargetEf.setText(root.getRunSettings().getRfmUsim().getCustomTargetEf());

        cmbRfmUsimCustomTargetAccBadCase.setItems(mappedVariables);
        registerForComboUpdate(cmbRfmUsimCustomTargetAccBadCase);
        if (root.getRunSettings().getRfmUsim().getCustomTargetAccBadCase() != null)
            cmbRfmUsimCustomTargetAccBadCase.getSelectionModel().select(root.getRunSettings().getRfmUsim().getCustomTargetAccBadCase());
        if (root.getRunSettings().getRfmUsim().getCustomTargetEfBadCase() != null)
            txtRfmUsimCustomTargetEfBadCase.setText(root.getRunSettings().getRfmUsim().getCustomTargetEfBadCase());

        chkRfmUsimFullAccess.setSelected(root.getRunSettings().getRfmUsim().isFullAccess());
        handleRfmUsimFullAccessCheck();

        // initialize list of available keysets for RFM USIM

        cmbRfmUsimCipheringKeyset.setItems(scp80KeysetLabels);
        if (root.getRunSettings().getRfmUsim().getCipheringKeyset() != null) {
            cmbRfmUsimCipheringKeyset.setValue(root.getRunSettings().getRfmUsim().getCipheringKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmUsim().getCipheringKeyset().getKeysetName())) {
                    lblRfmUsimKic.setText("Kic (hex): " + keyset.getComputedKic());
                    break;
                }
            }
            chkRfmUsimCustomKic.setSelected(root.getRunSettings().getRfmUsim().getCipheringKeyset().isCustomKic());
            handleRfmUsimCustomKicCheck();
            if (chkRfmUsimCustomKic.isSelected())
                txtRfmUsimCustomKic.setText(root.getRunSettings().getRfmUsim().getCipheringKeyset().getComputedKic());
            else
                txtRfmUsimCustomKic.setText("");
        }

        cmbRfmUsimAuthKeyset.setItems(scp80KeysetLabels);
        if (root.getRunSettings().getRfmUsim().getAuthKeyset() != null) {
            cmbRfmUsimAuthKeyset.setValue(root.getRunSettings().getRfmUsim().getAuthKeyset().getKeysetName());
            for (SCP80Keyset keyset : root.getRunSettings().getScp80Keysets()) {
                if (keyset.getKeysetName().equals(root.getRunSettings().getRfmUsim().getAuthKeyset().getKeysetName())) {
                    lblRfmUsimKid.setText("Kid (hex): " + keyset.getComputedKid());
                    break;
                }
            }
            chkRfmUsimCustomKid.setSelected(root.getRunSettings().getRfmUsim().getAuthKeyset().isCustomKid());
            handleRfmUsimCustomKidCheck();
            if (chkRfmUsimCustomKid.isSelected())
                txtRfmUsimCustomKid.setText(root.getRunSettings().getRfmUsim().getAuthKeyset().getComputedKid());
            else
                txtRfmUsimCustomKid.setText("");
        }

        chkUseSpecificKeyset.setSelected(root.getRunSettings().getRfmUsim().isUseSpecificKeyset());
        handleUseSpecificKeysetCheck();
    }

    private void showMappings(VariableMapping mapping) {
        if (mapping != null) {
            // fill mappings with info from mapping object
            txtMappedTo.setText(mapping.getMappedVariable());
            if (mapping.isFixed()) {
                chkFixedVal.setSelected(true);
                txtMccVar.setText(mapping.getValue());
                cmbMccVar.setValue(null);
            } else {
                chkFixedVal.setSelected(false);
                cmbMccVar.setValue(mapping.getMccVariable());
                txtMccVar.setText("");
            }
        } else {
            txtMappedTo.setText("");
            txtMccVar.setText("");
            cmbMccVar.setValue(null);
        }
    }

    private void showKeyset(SCP80Keyset keyset) {
        if (keyset != null) {
            txtKeysetName.setText(keyset.getKeysetName());
            cmbKeysetVersion.setValue(Integer.toString(keyset.getKeysetVersion()));
            cmbKeysetType.setValue(keyset.getKeysetType());
            handleKeysetTypeSelection();
            cmbKicValuation.setValue(keyset.getKicValuation());
            cmbKicLength.setValue(String.valueOf(keyset.getKicKeyLength()));
            cmbKicMode.setValue(keyset.getKicMode());
            cmbKidValuation.setValue(keyset.getKidValuation());
            cmbKidLength.setValue(String.valueOf(keyset.getKidKeyLength()));
            cmbKidMode.setValue(keyset.getKidMode());
            cmbCmacLength.setValue(String.valueOf(keyset.getCmacLength()));
        }
        else {
            txtKeysetName.setText("");
            cmbKeysetVersion.setValue(null);
            cmbKeysetType.setValue(null);
            cmbKicValuation.setValue(null);
            cmbKicLength.setValue(null);
            cmbKicMode.setValue(null);
            cmbKidValuation.setValue(null);
            cmbKidLength.setValue(null);
            cmbKidMode.setValue(null);
            cmbCmacLength.setValue(null);
        }
    }

    public ComboBox<String> getCmbMccVar() {
        return cmbMccVar;
    }

    public MaskerPane getMaskerPane() {
        return maskerPane;
    }

    public TabPane getTabBottom() {
        return tabBottom;
    }

    public TextFlow getTxtInterpretedLog() {
        return txtInterpretedLog;
    }

    public TextArea getTxtCommandResponse() {
        return txtCommandResponse;
    }

    private void registerForComboUpdate(ComboBox<String> comboBox) {
        comboPool.add(comboBox);
    }

    private void updateListForComboBoxes() {
        for (ComboBox<String> comboBox : comboPool) {
            comboBox.setItems(mappedVariables);
        }
    }

    @FXML
    private void handleBtnUpdateMapping() {
        if (mappedVariableExist(txtMappedTo.getText())) {
            VariableMapping selectedMapping = tblMapping.getSelectionModel().getSelectedItem();
            if (chkFixedVal.isSelected()) {
                selectedMapping.setFixed(true);
                selectedMapping.setMccVariable(null);
                selectedMapping.setValue(txtMccVar.getText());
                logger.info(String.format("Update mapping: %s -> %s", selectedMapping.getMappedVariable(),
                        selectedMapping.getValue()));
            } else {
                selectedMapping.setFixed(false);
                selectedMapping.setMccVariable(cmbMccVar.getValue());
                selectedMapping.setValue(null);
                logger.info(String.format("Update mapping: %s -> %s", selectedMapping.getMappedVariable(),
                        selectedMapping.getMccVariable()));
            }
            tblMapping.getColumns().get(1).setVisible(false);
            tblMapping.getColumns().get(1).setVisible(true);
        } else {
            // add new mapping
            String mappedVariable = txtMappedTo.getText();
            boolean fixed = false;
            String mccVariable = cmbMccVar.getValue();
            String value = null;
            if (chkFixedVal.isSelected()) {
                fixed = true;
                mccVariable = null;
                value = txtMccVar.getText();
            }
            VariableMapping mapping = new VariableMapping(mappedVariable, mccVariable, value, fixed);
            application.getMappings().add(mapping);

            mappedVariables.add(mapping.getMappedVariable());
            updateListForComboBoxes();

            if (mapping.isFixed())
                logger.info(String.format("Add mapping: %s -> %s", mapping.getMappedVariable(),
                        mapping.getValue()));
            else
                logger.info(String.format("Add mapping: %s -> %s", mapping.getMappedVariable(),
                        mapping.getMccVariable()));
        }
    }

    @FXML
    private void handleBtnDeleteMapping() {
        if (application.getMappings().size() > 0) {
            logger.info(String.format("Delete mapping: %s",
                    tblMapping.getSelectionModel().getSelectedItem().getMappedVariable()));

            mappedVariables.remove(tblMapping.getSelectionModel().getSelectedItem().getMappedVariable());
            updateListForComboBoxes();

            int selectedIndex = tblMapping.getSelectionModel().getSelectedIndex();
            tblMapping.getItems().remove(selectedIndex);
            showMappings(null);
            tblMapping.getSelectionModel().clearSelection();

        }
    }

    @FXML
    private void handleButtonBrowseProjectFolder() {
        DirectoryChooser projectChooser = new DirectoryChooser();
        projectChooser.setTitle("Select Project Directory");
        String initialDirectory;
        if (root.getRunSettings().getProjectPath() != "C:\\") {
            initialDirectory = new File(root.getRunSettings().getProjectPath()).getAbsolutePath();
            if (initialDirectory == null)
                initialDirectory = "C:\\";
        }
        else
            initialDirectory = "C:\\";
        projectChooser.setInitialDirectory(new File(initialDirectory));
        File projectDir = projectChooser.showDialog(application.getPrimaryStage());
        if (projectDir != null) {
            root.getRunSettings().setProjectPath(projectDir.getAbsolutePath());
            txtProjectFolder.setText(projectDir.getAbsolutePath());
        }
    }

    @FXML
    private void handleButtonGetAtr() {
        try {
            CardTerminal terminal = root.getTerminalFactory().terminals().list().get(
                    root.getRunSettings().getReaderNumber()
            );
            Card connection = terminal.connect("*");
            javax.smartcardio.ATR atr = connection.getATR();
            byte[] atrBytes = atr.getBytes();
            String atrString = Hex.encodeHexString(atrBytes);
            int bytesLength = atrString.length();
            root.getRunSettings().getAtr().setAtrString(atrString.toUpperCase().substring(0, bytesLength-6)); // ATR
            String statusTck = atrString.substring(bytesLength-6);
            root.getRunSettings().getAtr().setStatus(statusTck.substring(0, 4)); // status
            root.getRunSettings().getAtr().setTck(statusTck.substring(4).toUpperCase()); // TCK
            txtAtr.setText(root.getRunSettings().getAtr().getAtrString());
            lblProtocol.setText("Protocol: " + connection.getProtocol()
                    + "; Status: " + root.getRunSettings().getAtr().getStatus()
                    + "; TCK: " + root.getRunSettings().getAtr().getTck());
            connection.disconnect(false);

        } catch (CardException e) {
//            e.printStackTrace();
            logger.error(e.getMessage());
            Alert cardAlert = new Alert(Alert.AlertType.ERROR);
            cardAlert.initModality(Modality.APPLICATION_MODAL);
            cardAlert.initOwner(application.getPrimaryStage());
            cardAlert.setTitle("Card Error");
            cardAlert.setHeaderText("Failed to get ATR");
            cardAlert.setContentText(e.getMessage());
            cardAlert.showAndWait();
        }
    }

    @FXML
    private void handleIncludeAtrCheck() {
        if (chkIncludeAtr.isSelected())
            root.getMenuAtr().setDisable(false);
        else
            root.getMenuAtr().setDisable(true);
    }

    private boolean mappedVariableExist(String testMappedVariable) {
        for (VariableMapping mapping : application.getMappings()) {
            if (mapping.getMappedVariable().equals(testMappedVariable))
                return true;
        }
        return false;
    }

    @FXML
    private void handleIncludeDeltaTestCheck() {
        if (chkIncludeDeltaTest.isSelected())
            root.getMenuDeltaTest().setDisable(false);
        else
            root.getMenuDeltaTest().setDisable(true);
    }

    @FXML
    private void handleIncludeSqnMaxCheck() {
        if (chkIncludeSqnMax.isSelected())
            root.getMenuSqnMax().setDisable(false);
        else
            root.getMenuSqnMax().setDisable(true);
    }

    private void updateListForScp80ComboBoxes() {
        cmbRfmUsimCipheringKeyset.setItems(scp80KeysetLabels);
        cmbRfmUsimAuthKeyset.setItems(scp80KeysetLabels);
    }

    @FXML
    private void handleKeysetTypeSelection() {
        if (cmbKeysetType.getSelectionModel().getSelectedItem().equals("AES")) {
            lblCmacLength.setDisable(false);
            cmbCmacLength.setDisable(false);
            cmbKicMode.setValue("AES - CBC");
            cmbKidMode.setValue("AES - CMAC");
        } else {
            lblCmacLength.setDisable(true);
            cmbCmacLength.setDisable(true);
        }
    }

    @FXML
    private void handleButtonAddScp80Keyset() {
        if (keysetExists(txtKeysetName.getText())) {
            lblAddKeysetErrMsg.setVisible(true);
            lblAddKeysetErrMsg.setText("Keyset with same name already exists.");
        } else {
            // add new keyset
            String name = txtKeysetName.getText();
            int version = Integer.parseInt(cmbKeysetVersion.getValue());
            String type = cmbKeysetType.getValue();
            int cmacLength = 0;
            if (type.equals("AES"))
                cmacLength = Integer.parseInt(cmbCmacLength.getValue());
            String kicVal = cmbKicValuation.getValue();
            int kicLength = Integer.parseInt(cmbKicLength.getValue());
            String kicMode = cmbKicMode.getValue();
            String kidVal = cmbKidValuation.getValue();
            int kidLength = Integer.parseInt(cmbKidLength.getValue());
            String kidMode = cmbKidMode.getValue();
            SCP80Keyset scp80Keyset = new SCP80Keyset(name, version, type, kicVal, kicLength, kicMode, kidVal, kidLength, kidMode, cmacLength);
            application.getScp80Keysets().add(scp80Keyset);

            scp80KeysetLabels.add(scp80Keyset.getKeysetName());
            updateListForScp80ComboBoxes();

            lblAddKeysetErrMsg.setVisible(false);
            logger.info("Added SCP-80 keyset: " + scp80Keyset.toJson());
        }
    }

    @FXML
    private void handleButtonDeleteScp80Keyset() {
        if (application.getScp80Keysets().size() > 0) {
            logger.info("Delete SCP-80 keyset: " + tblScp80Keyset.getSelectionModel().getSelectedItem().getKeysetName());

            scp80KeysetLabels.remove(tblScp80Keyset.getSelectionModel().getSelectedItem().getKeysetName());
            updateListForScp80ComboBoxes();

            int selectedIndex = tblScp80Keyset.getSelectionModel().getSelectedIndex();
            tblScp80Keyset.getItems().remove(selectedIndex);
            showKeyset(null);
            tblScp80Keyset.getSelectionModel().clearSelection();
        }
    }

    private boolean keysetExists(String checkKeysetName) {
        for (SCP80Keyset keyset : application.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(checkKeysetName))
                return true;
        }
        return false;
    }

    @FXML
    private void handleTpOaCheck() {
        if (chkTpOa.isSelected()) {
            lblTpOa.setDisable(false);
            txtTpOa.setDisable(false);
        } else {
            lblTpOa.setDisable(true);
            txtTpOa.setDisable(true);
        }
    }

    @FXML
    private void handleIncludeRfmUsimCheck() {
        if (chkIncludeRfmUsim.isSelected())
            root.getMenuRfmUsim().setDisable(false);
        else
            root.getMenuRfmUsim().setDisable(true);
    }

    @FXML
    private void handleIncludeRfmUsimUpdateRecordCheck() {
        if (chkIncludeRfmUsimUpdateRecord.isSelected())
            root.getMenuRfmUsimUpdateRecord().setDisable(false);
        else
            root.getMenuRfmUsimUpdateRecord().setDisable(true);
    }

    @FXML
    private void handleIncludeRfmUsimExpandedModeCheck() {
        if (chkIncludeRfmUsimExpandedMode.isSelected())
            root.getMenuRfmUsimExpandedMode().setDisable(false);
        else
            root.getMenuRfmUsimExpandedMode().setDisable(true);
    }

    @FXML
    private void handleRfmUsimFullAccessCheck() {
        if (chkRfmUsimFullAccess.isSelected()) {
            lblRfmUsimCustomTarget.setDisable(true);
            cmbRfmUsimCustomTargetAcc.setDisable(true);
            txtRfmUsimCustomTargetEf.setDisable(true);
            lblRfmUsimCustomTargetBadCase.setDisable(true);
            cmbRfmUsimCustomTargetAccBadCase.setDisable(true);
            txtRfmUsimCustomTargetEfBadCase.setDisable(true);
        } else {
            lblRfmUsimCustomTarget.setDisable(false);
            cmbRfmUsimCustomTargetAcc.setDisable(false);
            txtRfmUsimCustomTargetEf.setDisable(false);
            lblRfmUsimCustomTargetBadCase.setDisable(false);
            cmbRfmUsimCustomTargetAccBadCase.setDisable(false);
            txtRfmUsimCustomTargetEfBadCase.setDisable(false);
        }
    }

    @FXML
    private void handleUseSpecificKeysetCheck() {
        if (chkUseSpecificKeyset.isSelected()) {
            lblRfmUsimCipheringKeyset.setDisable(false);
            cmbRfmUsimCipheringKeyset.setDisable(false);
            lblRfmUsimKic.setDisable(false);
            chkRfmUsimCustomKic.setDisable(false);
            txtRfmUsimCustomKic.setDisable(false);
            lblRfmUsimAuthKeyset.setDisable(false);
            cmbRfmUsimAuthKeyset.setDisable(false);
            lblRfmUsimKid.setDisable(false);
            chkRfmUsimCustomKid.setDisable(false);
            txtRfmUsimCustomKid.setDisable(false);
        }
        else {
            lblRfmUsimCipheringKeyset.setDisable(true);
            cmbRfmUsimCipheringKeyset.setDisable(true);
            lblRfmUsimKic.setDisable(true);
            chkRfmUsimCustomKic.setDisable(true);
            txtRfmUsimCustomKic.setDisable(true);
            lblRfmUsimAuthKeyset.setDisable(true);
            cmbRfmUsimAuthKeyset.setDisable(true);
            lblRfmUsimKid.setDisable(true);
            chkRfmUsimCustomKid.setDisable(true);
            txtRfmUsimCustomKid.setDisable(true);
        }
    }

    @FXML
    private void handleRfmUsimCipheringKeysetSelection() {
        for (SCP80Keyset keyset : application.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmUsimCipheringKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmUsimKic.setText("Kic (hex): " + keyset.getComputedKic());
                break;
            }
        }
    }

    @FXML
    private void handleRfmUsimAuthKeysetSelection() {
        for (SCP80Keyset keyset : application.getScp80Keysets()) {
            if (keyset.getKeysetName().equals(cmbRfmUsimAuthKeyset.getSelectionModel().getSelectedItem())) {
                lblRfmUsimKid.setText("Kid (hex): " + keyset.getComputedKid());
                break;
            }
        }
    }

    @FXML
    private void handleRfmUsimCustomKicCheck() {
        if (chkRfmUsimCustomKic.isSelected())
            txtRfmUsimCustomKic.setDisable(false);
        else
            txtRfmUsimCustomKic.setDisable(true);
    }

    @FXML
    private void handleRfmUsimCustomKidCheck() {
        if (chkRfmUsimCustomKid.isSelected())
            txtRfmUsimCustomKid.setDisable(false);
        else
            txtRfmUsimCustomKid.setDisable(true);
    }

    @FXML
    private void handleButtonSetRfmUsimMsl() {
        String mslHexStr = txtRfmUsimMslByte.getText();
        int mslInteger = Integer.parseInt(mslHexStr, 16);
//        logger.info("MSL integer: " + mslInteger);
        if (mslInteger > 31) {
            // MSL integer shoould not be higher than 31 (0x1F)
            Alert mslAlert = new Alert(Alert.AlertType.ERROR);
            mslAlert.initModality(Modality.APPLICATION_MODAL);
            mslAlert.initOwner(application.getPrimaryStage());
            mslAlert.setTitle("Minimum Security Level");
            mslAlert.setHeaderText("Invalid MSL");
            mslAlert.setContentText("MSL value should not exceed '1F'");
            mslAlert.showAndWait();
        } else {
            // set components accordingly
            if (mslInteger == 0) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 1) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 2) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 3) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 4) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 5) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 6) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 7) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("No counter available");
            }
            if (mslInteger == 8) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 9) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 10) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 11) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 12) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 13) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 14) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 15) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter available no checking");
            }
            if (mslInteger == 16) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 17) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 18) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 19) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 20) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 21) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 22) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 23) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be higher");
            }
            if (mslInteger == 24) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 25) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 26) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 27) {
                chkRfmUsimUseCipher.setSelected(false);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 28) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("No verification");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 29) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Redundancy Check");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 30) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Cryptographic Checksum");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            if (mslInteger == 31) {
                chkRfmUsimUseCipher.setSelected(true);
                cmbRfmUsimAuthVerif.getSelectionModel().select("Digital Signature");
                cmbRfmUsimCounterCheck.getSelectionModel().select("Counter must be one higher");
            }
            // set back MSL text field as it may change due to race condition
            txtRfmUsimMslByte.setText(mslHexStr);
        }
    }

    @FXML
    private void handleRfmUsimUseCipherCheck() {
        if (chkRfmUsimUseCipher.isSelected())
            root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setUseCipher(true);
        else
            root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setUseCipher(false);
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().computeMsl();
        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML
    private void handleRfmUsimAuthVerifSelection() {
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setAuthVerification(cmbRfmUsimAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().computeMsl();
        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML
    private void handleRfmUsimCounterCheckingSelection() {
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setCounterChecking(cmbRfmUsimCounterCheck.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().computeMsl();
        txtRfmUsimMslByte.setText(root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().getComputedMsl());
    }

    @FXML
    private void handleInclude3gScriptCheck() {
        if (chkInclude3gScript.isSelected())
            root.getMenuCodes3g().setDisable(false);
        else
            root.getMenuCodes3g().setDisable(true);
    }

    @FXML
    private void handleInclude2gScriptCheck() {
        if (chkInclude2gScript.isSelected())
            root.getMenuCodes2g().setDisable(false);
        else
            root.getMenuCodes2g().setDisable(true);
    }

    @FXML
    private void handleUseIsc2Check() {
        if (chkUseIsc2.isSelected()) {
            lblIsc2.setDisable(false);
            cmbIsc2.setDisable(false);
            lblIsc2Retries.setDisable(false);
            txtIsc2Retries.setDisable(false);
        } else {
            lblIsc2.setDisable(true);
            cmbIsc2.setDisable(true);
            lblIsc2Retries.setDisable(true);
            txtIsc2Retries.setDisable(true);
        }
    }

    @FXML
    private void handleUseIsc3Check() {
        if (chkUseIsc3.isSelected()) {
            lblIsc3.setDisable(false);
            cmbIsc3.setDisable(false);
            lblIsc3Retries.setDisable(false);
            txtIsc3Retries.setDisable(false);
        } else {
            lblIsc3.setDisable(true);
            cmbIsc3.setDisable(true);
            lblIsc3Retries.setDisable(true);
            txtIsc3Retries.setDisable(true);
        }
    }

    @FXML
    private void handleUseIsc4Check() {
        if (chkUseIsc4.isSelected()) {
            lblIsc4.setDisable(false);
            cmbIsc4.setDisable(false);
            lblIsc4Retries.setDisable(false);
            txtIsc4Retries.setDisable(false);
        } else {
            lblIsc4.setDisable(true);
            cmbIsc4.setDisable(true);
            lblIsc4Retries.setDisable(true);
            txtIsc4Retries.setDisable(true);
        }
    }

    public void saveControlState() {
        // project details

        root.getRunSettings().setProjectPath(txtProjectFolder.getText());
        root.getRunSettings().setRequestId(txtRequestId.getText());
        root.getRunSettings().setRequestName(txtRequestName.getText());
        root.getRunSettings().setProfileName(txtProfileName.getText());
        root.getRunSettings().setProfileVersion(Integer.parseInt(txtProfileVersion.getText()));
        root.getRunSettings().setCardImageItemId(txtCardImageItemId.getText());
        root.getRunSettings().setCustomer(txtCustomer.getText());
        root.getRunSettings().setDeveloperName(txtDeveloperName.getText());
        root.getRunSettings().setTesterName(txtTesterName.getText());

        // ATR

        root.getRunSettings().getAtr().setAtrString(txtAtr.getText());
        root.getRunSettings().getAtr().setIncludeAtr(chkIncludeAtr.isSelected());

        // card parameters

        root.getRunSettings().getCardParameters().setCardManagerAid(txtCardManagerAid.getText());
        root.getRunSettings().getCardParameters().setUsimAid(txtUsimAid.getText());
        root.getRunSettings().getCardParameters().setDfUsim(txtDfUsim.getText());
        root.getRunSettings().getCardParameters().setDfGsmAccess(txtDfGsmAccess.getText());
        root.getRunSettings().getCardParameters().setDfTelecom(txtDfTelecom.getText());
        root.getRunSettings().getCardParameters().setIsimAid(txtIsimAid.getText());
        root.getRunSettings().getCardParameters().setDfIsim(txtDfIsim.getText());
        root.getRunSettings().getCardParameters().setCsimAid(txtCsimAid.getText());
        root.getRunSettings().getCardParameters().setDfCsim(txtDfCsim.getText());

        // secret codes values

        root.getRunSettings().getSecretCodes().setInclude3gScript(chkInclude3gScript.isSelected());
        root.getRunSettings().getSecretCodes().setInclude2gScript(chkInclude2gScript.isSelected());

        root.getRunSettings().getSecretCodes().setPin1disabled(chkPin1Disabled.isSelected());
        root.getRunSettings().getSecretCodes().setPin2disabled(chkPin2Disabled.isSelected());

        root.getRunSettings().getSecretCodes().setGpin(cmbGpin.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setGpinRetries(Integer.parseInt(txtGpinRetries.getText()));
        root.getRunSettings().getSecretCodes().setLpin(cmbLpin.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setLpinRetries(Integer.parseInt(txtLpinRetries.getText()));
        root.getRunSettings().getSecretCodes().setGpuk(cmbGpuk.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setGpukRetries(Integer.parseInt(txtGpukRetries.getText()));
        root.getRunSettings().getSecretCodes().setLpuk(cmbLpuk.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setLpukRetries(Integer.parseInt(txtLpukRetries.getText()));
        root.getRunSettings().getSecretCodes().setChv1(cmbChv1.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setChv1Retries(Integer.parseInt(txtChv1Retries.getText()));
        root.getRunSettings().getSecretCodes().setChv2(cmbChv2.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setChv2Retries(Integer.parseInt(txtChv2Retries.getText()));
        root.getRunSettings().getSecretCodes().setPuk1(cmbPuk1.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setPuk1Retries(Integer.parseInt(txtPuk1Retries.getText()));
        root.getRunSettings().getSecretCodes().setPuk2(cmbPuk2.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setPuk2Retries(Integer.parseInt(txtPuk2Retries.getText()));

        root.getRunSettings().getSecretCodes().setIsc1(cmbIsc1.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setIsc1Retries(Integer.parseInt(txtIsc1Retries.getText()));
        root.getRunSettings().getSecretCodes().setIsc2(cmbIsc2.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setIsc2Retries(Integer.parseInt(txtIsc2Retries.getText()));
        root.getRunSettings().getSecretCodes().setUseIsc2(chkUseIsc2.isSelected());
        root.getRunSettings().getSecretCodes().setIsc3(cmbIsc3.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setIsc3Retries(Integer.parseInt(txtIsc3Retries.getText()));
        root.getRunSettings().getSecretCodes().setUseIsc3(chkUseIsc3.isSelected());
        root.getRunSettings().getSecretCodes().setIsc4(cmbIsc4.getSelectionModel().getSelectedItem());
        root.getRunSettings().getSecretCodes().setIsc4Retries(Integer.parseInt(txtIsc4Retries.getText()));
        root.getRunSettings().getSecretCodes().setUseIsc4(chkUseIsc4.isSelected());
        root.getRunSettings().getSecretCodes().setBlockGpuk(chkBlockGpuk.isSelected());
        root.getRunSettings().getSecretCodes().setBlockLpuk(chkBlockLpuk.isSelected());
        root.getRunSettings().getSecretCodes().setBlockPuk1(chkBlockPuk1.isSelected());
        root.getRunSettings().getSecretCodes().setBlockPuk2(chkBlockPuk2.isSelected());

        // authentication settings

        root.getRunSettings().getAuthentication().setIncludeDeltaTest(chkIncludeDeltaTest.isSelected());
        root.getRunSettings().getAuthentication().setIncludeSqnMax(chkIncludeSqnMax.isSelected());

        root.getRunSettings().getAuthentication().setResLength(txtResLength.getText());
        root.getRunSettings().getAuthentication().setAkaC1(cmbAkaC1.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaC2(cmbAkaC2.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaC3(cmbAkaC3.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaC4(cmbAkaC4.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaC5(cmbAkaC5.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setAkaRi(cmbAkaRi.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setRand(txtRand.getText());
        root.getRunSettings().getAuthentication().setDelta(txtDelta.getText());
        root.getRunSettings().getAuthentication().setSqn(txtSqn.getText());
        root.getRunSettings().getAuthentication().setSqnMax(txtSqnMax.getText());
        root.getRunSettings().getAuthentication().setAmf(txtAmf.getText());
        root.getRunSettings().getAuthentication().setKi(cmbKi.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setOpc(cmbOpc.getSelectionModel().getSelectedItem());
        root.getRunSettings().getAuthentication().setComp1282(chkComp1282.isSelected());
        root.getRunSettings().getAuthentication().setComp1283(chkComp1283.isSelected());
        root.getRunSettings().getAuthentication().setMilenage(chkMilenage.isSelected());
        root.getRunSettings().getAuthentication().setIsimAuth(chkIsimAuth.isSelected());
        root.getRunSettings().getAuthentication().setGsmAlgo(chkGsmAlgo.isSelected());

        // SMS update settings

        root.getRunSettings().getSmsUpdate().setUdhiFirstByte(txtUdhiFirstByte.getText());
        root.getRunSettings().getSmsUpdate().setScAddress(txtScAddress.getText());
        root.getRunSettings().getSmsUpdate().setTpPid(txtTpPid.getText());
        root.getRunSettings().getSmsUpdate().setUseWhiteList(chkTpOa.isSelected());
        root.getRunSettings().getSmsUpdate().setTpOa(txtTpOa.getText());
        root.getRunSettings().getSmsUpdate().setPorFormat(cmbPorFormat.getSelectionModel().getSelectedItem());

        // RFM USIM

        root.getRunSettings().getRfmUsim().setIncludeRfmUsim(chkIncludeRfmUsim.isSelected());
        root.getRunSettings().getRfmUsim().setIncludeRfmUsimUpdateRecord(chkIncludeRfmUsimUpdateRecord.isSelected());
        root.getRunSettings().getRfmUsim().setIncludeRfmUsimExpandedMode(chkIncludeRfmUsimExpandedMode.isSelected());

        // RFM USIM MSL
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setComputedMsl(txtRfmUsimMslByte.getText());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setUseCipher(chkRfmUsimUseCipher.isSelected());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setCipherAlgo(cmbRfmUsimCipherAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setAuthVerification(cmbRfmUsimAuthVerif.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setSigningAlgo(cmbRfmUsimSigningAlgo.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setCounterChecking(cmbRfmUsimCounterCheck.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setPorRequirement(cmbRfmUsimPorRequirement.getSelectionModel().getSelectedItem());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setCipherPor(chkRfmUsimCipherPor.isSelected());
        root.getRunSettings().getRfmUsim().getMinimumSecurityLevel().setPorSecurity(cmbRfmUsimPorSecurity.getSelectionModel().getSelectedItem());

        root.getRunSettings().getRfmUsim().setTar(txtRfmUsimTar.getText());
        root.getRunSettings().getRfmUsim().setTargetEf(txtRfmUsimTargetEf.getText());
        root.getRunSettings().getRfmUsim().setTargetEfBadCase(txtRfmUsimTargetEfBadCase.getText());
        root.getRunSettings().getRfmUsim().setFullAccess(chkRfmUsimFullAccess.isSelected());
        if (!root.getRunSettings().getRfmUsim().isFullAccess()) {
            root.getRunSettings().getRfmUsim().setCustomTargetAcc(cmbRfmUsimCustomTargetAcc.getSelectionModel().getSelectedItem());
            root.getRunSettings().getRfmUsim().setCustomTargetEf(txtRfmUsimCustomTargetEf.getText());
            root.getRunSettings().getRfmUsim().setCustomTargetAccBadCase(cmbRfmUsimCustomTargetAccBadCase.getSelectionModel().getSelectedItem());
            root.getRunSettings().getRfmUsim().setCustomTargetEfBadCase(txtRfmUsimCustomTargetEfBadCase.getText());
        }

        root.getRunSettings().getRfmUsim().setUseSpecificKeyset(chkUseSpecificKeyset.isSelected());
        SCP80Keyset rfmUsimCipheringKeyset = new SCP80Keyset();
        rfmUsimCipheringKeyset.setKeysetName(cmbRfmUsimCipheringKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : application.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(rfmUsimCipheringKeyset.getKeysetName())) {
                rfmUsimCipheringKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                rfmUsimCipheringKeyset.setKeysetType(registeredKeyset.getKeysetType());
                rfmUsimCipheringKeyset.setKicValuation(registeredKeyset.getKicValuation());
                rfmUsimCipheringKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                rfmUsimCipheringKeyset.setKicMode(registeredKeyset.getKicMode());
                rfmUsimCipheringKeyset.setKidValuation(registeredKeyset.getKidValuation());
                rfmUsimCipheringKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                rfmUsimCipheringKeyset.setKidMode(registeredKeyset.getKidMode());
                rfmUsimCipheringKeyset.setCmacLength(registeredKeyset.getCmacLength());

                rfmUsimCipheringKeyset.setCustomKic(chkRfmUsimCustomKic.isSelected());
                if (rfmUsimCipheringKeyset.isCustomKic())
                    rfmUsimCipheringKeyset.setComputedKic(txtRfmUsimCustomKic.getText());
                else
                    rfmUsimCipheringKeyset.setComputedKic(registeredKeyset.getComputedKic());

                rfmUsimCipheringKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmUsim().setCipheringKeyset(rfmUsimCipheringKeyset);

        SCP80Keyset rfmUsimAuthKeyset = new SCP80Keyset();
        rfmUsimAuthKeyset.setKeysetName(cmbRfmUsimAuthKeyset.getSelectionModel().getSelectedItem());
        for (SCP80Keyset registeredKeyset : application.getScp80Keysets()) {
            if (registeredKeyset.getKeysetName().equals(rfmUsimAuthKeyset.getKeysetName())) {
                rfmUsimAuthKeyset.setKeysetVersion(registeredKeyset.getKeysetVersion());
                rfmUsimAuthKeyset.setKeysetType(registeredKeyset.getKeysetType());
                rfmUsimAuthKeyset.setKicValuation(registeredKeyset.getKicValuation());
                rfmUsimAuthKeyset.setKicKeyLength(registeredKeyset.getKicKeyLength());
                rfmUsimAuthKeyset.setKicMode(registeredKeyset.getKicMode());
                rfmUsimAuthKeyset.setKidValuation(registeredKeyset.getKidValuation());
                rfmUsimAuthKeyset.setKidKeyLength(registeredKeyset.getKidKeyLength());
                rfmUsimAuthKeyset.setKidMode(registeredKeyset.getKidMode());
                rfmUsimAuthKeyset.setCmacLength(registeredKeyset.getCmacLength());

                rfmUsimAuthKeyset.setComputedKic(registeredKeyset.getComputedKic());

                rfmUsimAuthKeyset.setCustomKid(chkRfmUsimCustomKid.isSelected());
                if (rfmUsimAuthKeyset.isCustomKid())
                    rfmUsimAuthKeyset.setComputedKid(txtRfmUsimCustomKid.getText());
                else
                    rfmUsimAuthKeyset.setComputedKid(registeredKeyset.getComputedKid());

                break;
            }
        }
        root.getRunSettings().getRfmUsim().setAuthKeyset(rfmUsimAuthKeyset);
    }

}
