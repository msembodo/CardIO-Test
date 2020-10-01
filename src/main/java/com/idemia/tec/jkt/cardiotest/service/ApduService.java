package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.controller.RootLayoutController;
import com.idemia.tec.jkt.cardiotest.model.CheckPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ApduService {

    @Autowired private RootLayoutController root;

    public String verifyGpin() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gGlobalPin1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gGlobalPin1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gGlobalPin1().getP3() + " %";
    }

    public String verifyLpin() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gLocalPin1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gLocalPin1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gLocalPin1().getP3() + " %";
    }

    public String verify3gAdm1() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm1().getP3() + " %";
    }

    public String verify3gAdm2() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm2().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm2().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm2().getP3() + " %";
    }

    public String verify3gAdm3() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm3().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm3().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm3().getP3() + " %";
    }

    public String verify3gAdm4() {
        return "00 20 " + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm4().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm4().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify3g().getVerify3gAdm4().getP3() + " %";
    }

    public String verifyPin1() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv1().getP3() + " %";
    }

    public String verifyPin2() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv2().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv2().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gChv2().getP3() + " %";
    }

    public String verify2gAdm1() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm1().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm1().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm1().getP3() + " %";
    }

    public String verify2gAdm2() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm2().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm2().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm2().getP3() + " %";
    }

    public String verify2gAdm3() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm3().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm3().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm3().getP3() + " %";
    }

    public String verify2gAdm4() {
        return "A0 20 " + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm4().getP1() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm4().getP2() + " "
                + root.getRunSettings().getCustomApdu().getVerify2g().getVerify2gAdm4().getP3() + " %";
    }

    public List<CheckPoint> parsePcomLog(String pcomLog) throws FileNotFoundException {
        File pcomLogFile = new File(pcomLog);
        List<String> pcomLogBuffer = new ArrayList<>();
        Scanner sc = new Scanner(pcomLogFile);
        while (sc.hasNextLine()) pcomLogBuffer.add(sc.nextLine());
        List<CheckPoint> checkPoints = new ArrayList<>();
        for (int i = 0; i < pcomLogBuffer.size(); i++) {
            String line = pcomLogBuffer.get(i);
            if (line.matches("^\\s+#CHECK.+")) {
                Pattern pattern = Pattern.compile("#CHECK\\d{3}");
                Matcher matcher = pattern.matcher(line);
                String checkPointName = "";
                if (matcher.find())
                    checkPointName = line.substring(matcher.start(), matcher.end());
                String checkpointMsg = line.split(":")[1];
//                System.out.println("\n" + checkPointName);
//                System.out.println(checkpointMsg);
                String outputData = "";
                String expectedData = "";
                String status = "";
                String expectedStatus = "";
                boolean success = true;
                int forwardIndex = 1;
                while (!pcomLogBuffer.get(i + forwardIndex).matches("^\\s+#CHECK.+")) {
                    forwardIndex++;
                    if ((i + forwardIndex) < pcomLogBuffer.size())
                        continue;
                    else
                        break;
                }
                for (int j = 1; j < forwardIndex; j++) {
                    // get output data
                    if (pcomLogBuffer.get(i+j).matches("^\\s+Output Data\\s+:\\s.+")) {
                        String[] splitFirstRow = pcomLogBuffer.get(i+j).split(":");
                        outputData = splitFirstRow[1].replaceAll("[^a-fA-F0-9]", "");
                        int k = 1;
                        while (pcomLogBuffer.get(i+j+k).matches("^\\s+:\\s.+")) {
                            String[] splitNextRow = pcomLogBuffer.get(i+j+k).split(":");
                            outputData += splitNextRow[1].replaceAll("[^a-fA-F0-9]", "");
                            k++;
                        }
                    }
                    // get expected data
                    if (pcomLogBuffer.get(i+j).matches("^\\s+Expected Data\\s+:\\s.+")) {
                        String[] splitFirstRow = pcomLogBuffer.get(i+j).split(":");
                        expectedData = splitFirstRow[1].replaceAll("[^a-fA-F0-9xX]", "");
                        int k = 1;
                        while (pcomLogBuffer.get(i+j+k).matches("^\\s+:\\s.+")) {
                            String[] splitNextRow = pcomLogBuffer.get(i + j + k).split(":");
                            expectedData += splitNextRow[1].replaceAll("[^a-fA-F0-9xX]", "");
                            k++;
                        }
                        success = false;
                    }
                    // get status
                    if (pcomLogBuffer.get(i+j).matches("^\\s+Status\\s+:\\s.+")) {
                        String[] splitFirstRow = pcomLogBuffer.get(i+j).split(":");
                        status = splitFirstRow[1].replaceAll("[^a-fA-F0-9]", "");
                    }
                    // get expected status
                    if (pcomLogBuffer.get(i+j).matches("^\\s+Expected Status\\s+:\\s.+")) {
                        String[] splitFirstRow = pcomLogBuffer.get(i+j).split(":");
                        expectedStatus = splitFirstRow[1].replaceAll("[^a-fA-F0-9xX]", "");
                        success = false;
                    }
                }
//                System.out.println("Success         : " + success);
//                System.out.println("Output data     : " + outputData);
//                if (expectedData != "")
//                    System.out.println("Expected data   : " + expectedData);
//                System.out.println("Status          : " + status);
//                if (expectedStatus != "")
//                    System.out.println("Expected status : " + expectedStatus);

                checkPoints.add(new CheckPoint(checkPointName, checkpointMsg, success, outputData, expectedData, status, expectedStatus));
            }
        }
        return checkPoints;
    }

}
