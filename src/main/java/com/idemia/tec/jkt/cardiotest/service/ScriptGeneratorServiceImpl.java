package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScriptGeneratorServiceImpl implements ScriptGeneratorService {

    @Autowired
    private RootLayoutController root;

    @Override
    public StringBuilder generateAtr() {
        String composeAtrScript = ".CALL Mapping.txt\n"
                + ".CALL Options.txt\n\n"
                + ".POWER_ON /PROTOCOL_ON /NEGOTIATE_PROTOCOL [%ATR] (" + root.getRunSettings().getAtr().getStatus() + ")\n"
                + ".GET_PROTOCOL_PARAMETERS\n"
                + ".POWER_OFF";
        
        return new StringBuilder().append(composeAtrScript);
    }

    @Override
    public StringBuilder generateMapping() {
        StringBuilder mappings = new StringBuilder();
        mappings.append(".CALL ..\\variables.txt /LIST_OFF\n\n");
        for (VariableMapping mapping : root.getRunSettings().getVariableMappings()) {
            if (mapping.isFixed())
                mappings.append(String.format(".DEFINE %%%s  %s\n", mapping.getMappedVariable(), mapping.getValue()));
            else
                mappings.append(String.format(".DEFINE %%%s  %%%s\n", mapping.getMappedVariable(), mapping.getMccVariable()));
        }
        return mappings;
    }

}
