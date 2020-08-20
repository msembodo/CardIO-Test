package com.idemia.tec.jkt.cardiotest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idemia.tec.jkt.cardiotest.model.CustomScript;
import com.idemia.tec.jkt.cardiotest.model.RunSettings;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ExportImportService {

    static Logger logger = Logger.getLogger(ExportImportService.class);

    private String scriptsDirectory;
    private File exportRunSettingsFile;

    public String export(RunSettings rs, File exportFile) throws IOException {
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

        // zip all files in temporary folder
        List<File> srcFiles = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get("tmp"))) {
            List<String> srcFileNames = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            for (String src : srcFileNames) srcFiles.add(new File(src));
        }
        boolean zipOk = zip(srcFiles, exportFile);

        // delete all files in temporary folder
        for (File srcFile : srcFiles) Files.deleteIfExists(srcFile.toPath());

        if (zipOk) return "Exported settings to " + exportFile.getAbsolutePath();
        else return "Failed exporting settings.";
    }

    private void copyScriptToTmp(List<CustomScript> customScripts) throws IOException {
        for (CustomScript cs : customScripts) {
            File source = new File(scriptsDirectory + cs.getCustomScriptName());
            File target = new File("tmp\\" + cs.getCustomScriptName());
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private boolean zip(List<File> srcFiles, File destZipFile) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(destZipFile.getAbsolutePath());
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (File srcFile : srcFiles) {
                FileInputStream fis = new FileInputStream(srcFile);
                ZipEntry zipEntry = new ZipEntry(srcFile.getName());
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
            zipOut.close();
            fos.close();
            return true;
        } catch (IOException e) {
            logger.error("Error zipping files: " + e.getMessage());
            return false;
        }
    }

}
