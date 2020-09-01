package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.FileManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileManagementService {

    @Autowired private RootLayoutController root;

    public StringBuilder generateFilemanagementLinkFile(FileManagement fileManagement) {
        StringBuilder deltaTestBuffer = new StringBuilder();
        deltaTestBuffer.append(
                ".SET_BUFFER L " + "\n\n"
                        + ".CALL Mapping.txt /LIST_OFF\n"
                        + ".CALL Options.txt /LIST_OFF\n\n"
                        + ".DEFINE %RAND "+ "\n\n"
                        + ".POWER_ON\n"
        );
        deltaTestBuffer.append(".POWER_OFF\n");
        return deltaTestBuffer;
    }

}
