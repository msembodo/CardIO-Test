package com.idemia.tec.jkt.cardiotest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idemia.tec.jkt.cardiotest.model.CustomScript;
import com.idemia.tec.jkt.cardiotest.model.RunSettings;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ExportImportService {

    static Logger logger = Logger.getLogger(ExportImportService.class);

    private String scriptsDirectory;
    private File exportRunSettingsFile;

    public void export(RunSettings rs, File exportFile) throws IOException {
        // deep copy RunSettings object with json serialization
        ObjectMapper rsMapper = new ObjectMapper();
        RunSettings ers = rsMapper.readValue(rsMapper.writeValueAsString(rs), RunSettings.class);
        ers.setProjectPath(null);
        ers.setAdvSaveVariablesPath(null);
        ers.setReaderNumber(0);

        // populate source files in temporary folder
        File tmpDir = new File("tmp\\");
        if (!tmpDir.exists()) tmpDir.mkdir();

        // settings file
        exportRunSettingsFile = new File("tmp\\ex-run-settings.json");
        ObjectMapper ersMapper = new ObjectMapper();
        try { ersMapper.writerWithDefaultPrettyPrinter().writeValue(exportRunSettingsFile, ers); }
        catch (IOException e) { logger.error("Failed copying settings: " + e.getMessage()); }

        // variables file
        File sourceVarFile = new File(rs.getAdvSaveVariablesPath());
        File targetVarFile = new File("tmp\\" + sourceVarFile.getName());
        try { Files.copy(sourceVarFile.toPath(), targetVarFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { logger.error("Failed copying variables: " + e.getMessage()); }

        // custom scripts
        scriptsDirectory = rs.getProjectPath() + "\\scripts\\";
        if (rs.getCustomScriptsSection1().size() > 0) copyScriptToTmp(rs.getCustomScriptsSection1());
        if (rs.getCustomScriptsSection2().size() > 0) copyScriptToTmp(rs.getCustomScriptsSection2());
        if (rs.getCustomScriptsSection3().size() > 0) copyScriptToTmp(rs.getCustomScriptsSection3());

        // TODO: zip all files in temporary folder
        logger.info("Exported settings to " + exportFile.getAbsolutePath());

        // TODO: delete all files in temporary folder
    }

    private void copyScriptToTmp(List<CustomScript> customScripts) throws IOException {
        for (CustomScript cs : customScripts) {
            File source = new File(scriptsDirectory + cs.getCustomScriptName());
            File target = new File("tmp\\" + cs.getCustomScriptName());
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
