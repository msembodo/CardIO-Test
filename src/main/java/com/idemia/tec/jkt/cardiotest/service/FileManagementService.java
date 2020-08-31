package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.FileManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileManagementService {

    @Autowired private RootLayoutController root;

    public StringBuilder generateLinkFileTest(FileManagement fileManagement) {
        StringBuilder linkFileBuffer = new StringBuilder();
        linkFileBuffer.append(
            ".CALL Mapping.txt /LIST_OFF\n"
            + ".CALL Options.txt /LIST_OFF\n\n"
            + ".POWER_ON\n"
        );
        linkFileBuffer.append(".POWER_OFF\n");
        return linkFileBuffer;
    }

}
