package com.idemia.tec.jkt.cardiotest.controller;

import com.idemia.tec.jkt.cardiotest.CardiotestApplication;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import java.util.ArrayList;
import java.util.List;

@Component
public class SelectReaderController {

    static Logger logger = Logger.getLogger(SelectReaderController.class.getName());

    private CardiotestApplication application;
    private List<CardTerminal> terminals;

    @FXML
    private ComboBox<String> cmbReader;

    @Autowired
    private RootLayoutController root;

    public SelectReaderController() {}

    public void setMainApp(CardiotestApplication application) {
        this.application = application;
    }

    @FXML
    private void initialize() {
        // get list of actual readers
        try {
            terminals = root.getTerminalFactory().terminals().list();
            if (!terminals.isEmpty()) {
                List<String> readerNames = new ArrayList<>();
                for (CardTerminal terminal : terminals)
                    readerNames.add(terminal.getName());
                cmbReader.getItems().addAll(readerNames);

                if (root.getRunSettings().getReaderNumber() == -1)
                    cmbReader.getSelectionModel().select(0);
                else
                    cmbReader.getSelectionModel().select(root.getRunSettings().getReaderNumber());

            } else
                logger.warn("No terminal/reader detected!");

        } catch (CardException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleButtonCancel() {
        application.getSelectReaderDialogStage().close();
    }

    @FXML
    public void handleButtonOk() {
        int selectedReaderIndex = cmbReader.getSelectionModel().getSelectedIndex();
        root.getRunSettings().setReaderNumber(selectedReaderIndex);

        if (selectedReaderIndex != -1)
            root.getLblTerminalInfo().setText(terminals.get(selectedReaderIndex).getName());

        application.getSelectReaderDialogStage().close();
    }

}
