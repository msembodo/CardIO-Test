package com.idemia.tec.jkt.cardiotest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.CustomScript;
import com.idemia.tec.jkt.cardiotest.model.RunSettings;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ExportImportService {

    static Logger logger = Logger.getLogger(ExportImportService.class);

    private String impScriptsDirectory;
    private String expScriptsDirectory;

    @Autowired private RootLayoutController root;

    public boolean importSettings(File importFile, File projectDir, File advSaveVar) throws IOException {
        // inflate zip file to tmp
        File tmpDir = new File("tmp\\");
        if (!tmpDir.exists()) if (tmpDir.mkdir()) logger.info("Created tmp directory in app root");
        if (!unzip(importFile, tmpDir)) return false;

        // create structure in project folder
        impScriptsDirectory = projectDir.getAbsolutePath() + "\\scripts\\";
        File scriptDir = new File(impScriptsDirectory);
        if (!scriptDir.exists()) if (scriptDir.mkdir()) logger.info("Created directory " + scriptDir.getAbsolutePath());

        // parse exported RunSettings (with ObjectMapper), and set as input from user: projectPath & advSaveVariablesPath
        File exportRunSettingsFile = new File("tmp\\ex-run-settings.json");
        ObjectMapper ersMapper = new ObjectMapper();
        RunSettings rs = ersMapper.readValue(exportRunSettingsFile, RunSettings.class);
        rs.setProjectPath(projectDir.getAbsolutePath());
        rs.setAdvSaveVariablesPath(advSaveVar.getAbsolutePath());
        // TODO get readerNumber from current
        rs.setReaderNumber(root.getRunSettings().getReaderNumber());

        // copy custom scripts (if any)
        if (rs.getCustomScriptsSection1().size() > 0) copyScriptFromTmp(rs.getCustomScriptsSection1());
        if (rs.getCustomScriptsSection2().size() > 0) copyScriptFromTmp(rs.getCustomScriptsSection2());
        if (rs.getCustomScriptsSection3().size() > 0) copyScriptFromTmp(rs.getCustomScriptsSection3());

        // save exported RunSettings to run-settings.json
        File runSettingsFile = new File("run-settings.json");
        ObjectMapper rsMapper = new ObjectMapper();
        rsMapper.writerWithDefaultPrettyPrinter().writeValue(runSettingsFile, rs);

        // clean tmp folder
        List<File> tmpFiles = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get("tmp"))) {
            List<String> srcFileNames = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            for (String src : srcFileNames) tmpFiles.add(new File(src));
        }
        for (File tmpFile : tmpFiles) Files.deleteIfExists(tmpFile.toPath()); // delete all files in temporary folder
        return true;
    }

    private void copyScriptFromTmp(List<CustomScript> customScripts) throws IOException {
        for (CustomScript cs : customScripts) {
            File source = new File("tmp\\" + cs.getCustomScriptName());
            File target = new File(impScriptsDirectory + cs.getCustomScriptName());
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public String exportSettings(RunSettings rs, File exportFile) throws IOException {
        // deep copy RunSettings object with json serialization
        ObjectMapper rsMapper = new ObjectMapper();
        RunSettings ers = rsMapper.readValue(rsMapper.writeValueAsString(rs), RunSettings.class); // exported run settings
        ers.setProjectPath(null);
        ers.setAdvSaveVariablesPath(null);
        ers.setReaderNumber(0);

        // populate source files in temporary folder
        File tmpDir = new File("tmp\\");
        if (!tmpDir.exists()) if (tmpDir.mkdir()) logger.info("Created tmp directory in app root");

        // settings file
        File exportRunSettingsFile = new File("tmp\\ex-run-settings.json");
        ObjectMapper ersMapper = new ObjectMapper();
        try { ersMapper.writerWithDefaultPrettyPrinter().writeValue(exportRunSettingsFile, ers); }
        catch (IOException e) { logger.error("Failed copying settings: " + e.getMessage()); }

        // variables file
        File sourceVarFile = new File(rs.getAdvSaveVariablesPath());
        File targetVarFile = new File("tmp\\" + sourceVarFile.getName());
        try { Files.copy(sourceVarFile.toPath(), targetVarFile.toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException e) { logger.error("Failed copying variables: " + e.getMessage()); }

        // custom scripts
        expScriptsDirectory = rs.getProjectPath() + "\\scripts\\";
        if (rs.getCustomScriptsSection1().size() > 0) copyScriptToTmp(rs.getCustomScriptsSection1());
        if (rs.getCustomScriptsSection2().size() > 0) copyScriptToTmp(rs.getCustomScriptsSection2());
        if (rs.getCustomScriptsSection3().size() > 0) copyScriptToTmp(rs.getCustomScriptsSection3());

        // zip all files in temporary folder
        List<File> srcFiles = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get("tmp"))) {
            List<String> srcFileNames = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            for (String src : srcFileNames) srcFiles.add(new File(src));
        }
        if (!zip(srcFiles, exportFile)) return "Failed exporting settings.";

        for (File srcFile : srcFiles) Files.deleteIfExists(srcFile.toPath()); // delete all files in temporary folder
        return "Exported settings to " + exportFile.getAbsolutePath();
    }

    private void copyScriptToTmp(List<CustomScript> customScripts) throws IOException {
        for (CustomScript cs : customScripts) {
            File source = new File(expScriptsDirectory + cs.getCustomScriptName());
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
                while((length = fis.read(bytes)) >= 0) zipOut.write(bytes, 0, length);
                fis.close();
            }
            zipOut.close();
            fos.close();
            return true;
        }
        catch (IOException e) {
            logger.error("Error zipping files: " + e.getMessage());
            return false;
        }
    }

    private boolean unzip(File zipFile, File destDir) {
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.getAbsolutePath()));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            return true;
        } catch (IOException e) {
            logger.error("Error unzipping file: " + e.getMessage());
            return false;
        }
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator))
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        return destFile;
    }

}
