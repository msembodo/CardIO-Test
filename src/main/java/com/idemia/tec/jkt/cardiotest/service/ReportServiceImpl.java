package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.model.CustomScript;
import com.idemia.tec.jkt.cardiotest.model.RunSettings;
import com.idemia.tec.jkt.cardiotest.model.SCP80Keyset;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private RunSettings runSettings;

    @Override public void createReportFromSettings(RunSettings runSettings) {
        this.runSettings = runSettings;
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document,
                    new FileOutputStream(runSettings.getProjectPath() + "\\RunAll.pdf"));
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(composeHtml().toString()));
            document.close();
        }
        catch (DocumentException | IOException e) { e.printStackTrace(); }
    }

    private StringBuilder composeHtml() {
        StringBuilder html = new StringBuilder();
        html.append(createDocumentHeader());
        html.append("\n<div><h1>TEC Testing Report</h1></div>");
        // project details
        html.append(
            "\n<div><h2>Project Details</h2></div>"
            + createTableHeaderModule()
            + "\n<tr><td class=\"item\">Request ID</td>"
            + "<td>" + runSettings.getRequestId() + "</td></tr>"
            + "\n<tr><td class=\"item\">Request name</td>"
            + "<td>" + runSettings.getRequestName() + "</td></tr>"
            + "\n<tr><td class=\"item\">Profile name</td>"
            + "<td>" + runSettings.getProfileName() + "</td></tr>"
            + "\n<tr><td class=\"item\">Profile version</td>"
            + "<td>" + runSettings.getProfileVersion() + "</td></tr>"
            + "\n<tr><td class=\"item\">Image item ID</td>"
            + "<td>" + runSettings.getCardImageItemId() + "</td></tr>"
            + "\n<tr><td class=\"item\">Customer</td>"
            + "<td>" + runSettings.getCustomer() + "</td></tr>"
            + "\n<tr><td class=\"item\">Developer</td>"
            + "<td>" + runSettings.getDeveloperName() + "</td></tr>"
            + "\n<tr><td class=\"item\">Tester</td>"
            + "<td>" + runSettings.getTesterName() + "</td></tr>"
            + createTableFooter()
        );

        // variable mappings
//        html.append("\n<div><h2><b>Variable Mappings</b></h2></div>");
//        html.append(createTableHeaderModule());
//        html.append(
//            "\n<tr><th>Mapped variable</th>"
//            + "<th>Value / MCC variable</th></tr>"
//        );
//        for (VariableMapping mapping : runSettings.getVariableMappings()) {
//            html.append("\n<tr id=\"" + mapping.getMappedVariable() + "\"><td>" + mapping.getMappedVariable() + "</td>");
//            if (mapping.isFixed())
//                html.append("<td>" + mapping.getValue() + "</td></tr>");
//            else
//                html.append("<td>%" + mapping.getMccVariable() + "</td></tr>");
//        }
//        html.append(createTableFooter());

        // card parameters
//        html.append("\n<div><h2><b>Card Parameters</b></h2></div>");
//        html.append(createTableHeaderModule());
//        if (!runSettings.getCardParameters().getCardManagerAid().equals(""))
//            html.append(
//                "\n<tr><td>Card manager AID</td>"
//                + "<td>" + runSettings.getCardParameters().getCardManagerAid() + "</td></tr>"
//            );
//        if (!runSettings.getCardParameters().getUsimAid().equals(""))
//            html.append(
//                "\n<tr><td>USIM AID</td>"
//                + "<td>" + runSettings.getCardParameters().getUsimAid() + "</td></tr>"
//            );
//        if (!runSettings.getCardParameters().getDfUsim().equals(""))
//            html.append(
//                "\n<tr><td>DF USIM</td>"
//                + "<td>" + runSettings.getCardParameters().getDfUsim() + "</td></tr>"
//            );
//        if (!runSettings.getCardParameters().getDfGsmAccess().equals(""))
//            html.append(
//                "\n<tr><td>DF GSM Access</td>"
//                + "<td>" + runSettings.getCardParameters().getDfGsmAccess() + "</td></tr>"
//            );
//        if (!runSettings.getCardParameters().getDfTelecom().equals(""))
//            html.append(
//                "\n<tr><td>DF Telecom</td>"
//                + "<td>" + runSettings.getCardParameters().getDfTelecom() + "</td></tr>"
//            );
//        if (!runSettings.getCardParameters().getIsimAid().equals(""))
//            html.append(
//                "\n<tr><td>ISIM AID</td>"
//                + "<td>" + runSettings.getCardParameters().getIsimAid() + "</td></tr>"
//            );
//        if (!runSettings.getCardParameters().getDfIsim().equals(""))
//            html.append(
//                "\n<tr><td>DF ISIM</td>"
//                + "<td>" + runSettings.getCardParameters().getDfIsim() + "</td></tr>"
//            );
//        if (!runSettings.getCardParameters().getCsimAid().equals(""))
//            html.append(
//                "\n<tr><td>CSIM AID</td>"
//                + "<td>" + runSettings.getCardParameters().getCsimAid() + "</td></tr>"
//            );
//        if (!runSettings.getCardParameters().getDfCsim().equals(""))
//            html.append(
//                "\n<tr><td>DF CSIM</td>"
//                + "<td>" + runSettings.getCardParameters().getDfCsim() + "</td></tr>"
//            );
//        html.append(createTableFooter());

        // ATR
        if (runSettings.getAtr().isIncludeAtr()) {
            html.append("\n<div><h2>Answer To Reset</h2></div>");
            html.append(createTableHeaderModule());
            html.append("\n<tr><td class=\"item\">ATR check</td>");
            if (runSettings.getAtr().isTestAtrOk()) html.append("<td class=\"ok\">PASSED</td></tr>");
            else {
                String[] messages = runSettings.getAtr().getTestAtrMesssage().split(";");
                html.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
            }
            html.append(
                "\n<tr><td class=\"item\">ATR</td>"
                + "<td>" + runSettings.getAtr().getAtrString() + "</td></tr>"
                + "\n<tr><td class=\"item\">Status</td>"
                + "<td>" + runSettings.getAtr().getStatus() + "</td></tr>"
                + "\n<tr><td class=\"item\">TCK</td>"
                + "<td>" + runSettings.getAtr().getTck() + "</td></tr>"
            );
            html.append(createTableFooter());
        }

        // authentication
        if (runSettings.getAuthentication().isIncludeDeltaTest() || runSettings.getAuthentication().isIncludeSqnMax()) {
            html.append("\n<div><h2>Authentication</h2></div>");
            html.append("\n<div><h3>Test modules</h3></div>");
            html.append(createTableHeaderModule());
            if (runSettings.getAuthentication().isIncludeDeltaTest()) {
                html.append("\n<tr><td class=\"item\">Milenage delta test</td>");
                if (runSettings.getAuthentication().isTestDeltaOk()) html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getAuthentication().getTestDeltaMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
                }
            }
            else html.append("\n<tr><td class=\"item\">Milenage delta test</td><td>(not included)</td></tr>");
            if (runSettings.getAuthentication().isIncludeSqnMax()) {
                html.append("\n<tr><td class=\"item\">Milenage SQN max</td>");
                if (runSettings.getAuthentication().isTestSqnMaxOk()) html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getAuthentication().getTestSqnMaxMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
                }
            }
            else html.append("\n<tr><td class=\"item\">Milenage SQN max</td><td>(not included)</td></tr>");
            html.append(createTableFooter());

            html.append("\n<div><h3>Authentication options</h3></div>");
            html.append(createTableHeaderModule());
            html.append("\n<tr><td class=\"item\">COMP128-2</td>");
            if (runSettings.getAuthentication().isComp1282()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append("\n<tr><td class=\"item\">COMP128-3</td>");
            if (runSettings.getAuthentication().isComp1283()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append("\n<tr><td class=\"item\">Milenage</td>");
            if (runSettings.getAuthentication().isMilenage()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append("\n<tr><td class=\"item\">ISIM auth</td>");
            if (runSettings.getAuthentication().isIsimAuth()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append("\n<tr><td class=\"item\">GSM algo</td>");
            if (runSettings.getAuthentication().isGsmAlgo()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append(createTableFooter());

            html.append("\n<div><h3>Algo parameters</h3></div>");
            html.append(createTableHeaderModule());
            html.append(
                "\n<tr><td class=\"item\">Authentication AKA - Ci Value 1</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC1()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Authentication AKA - Ci Value 2</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC2()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Authentication AKA - Ci Value 3</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC3()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Authentication AKA - Ci Value 4</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC4()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Authentication AKA - Ci Value 5</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC5()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Rotation constants (Ri)</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaRi()) + "</td></tr>"
            );
            html.append(createTableFooter());

            html.append("\n<div><h3>Test parameters</h3></div>");
            html.append(createTableHeaderModule());
            html.append(
                "\n<tr><td class=\"item\">Subscriber key (K)</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getKi()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">OPc</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getOpc()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Random challenge (RAND)</td>"
                + "<td>" + runSettings.getAuthentication().getRand() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Signed response (RES) length</td>"
                + "<td>" + Integer.parseInt(runSettings.getAuthentication().getResLength()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">SQN init</td>"
                + "<td>" + runSettings.getAuthentication().getSqn() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">SQN max</td>"
                + "<td>" + runSettings.getAuthentication().getSqnMax() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Authentication Management Field (AMF)</td>"
                + "<td>" + runSettings.getAuthentication().getAmf() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Delta</td>"
                + "<td>" + runSettings.getAuthentication().getDelta() + "</td></tr>"
            );
            html.append(createTableFooter());
        }

        // file management

        if (runSettings.getFileManagement().isIncludeLinkFilesTest() || runSettings.getFileManagement().isIncludeRuwiTest() || runSettings.getFileManagement().isIncludeSfiTest()) {
            html.append("\n<div><h2>File Management</h2></div>");
            html.append("\n<div><h3>Test modules</h3></div>");
             /*

            html.append(createTableHeaderModule());
            if (runSettings.getFileManagement().isIncludeLinkFilesTest()) {
                html.append("\n<tr><td class=\"item\">Link File Test</td>");
                if (runSettings.getFileManagement().isTestLinkFilesOk()) html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getFileManagement().getTestLinkFilesMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
                }
            }
            else html.append("\n<tr><td class=\"item\">Link File Test</td><td>(not included)</td></tr>");


            if (runSettings.getFileManagement().isIncludeRuwiTest()) {
                html.append("\n<tr><td class=\"item\">Readable & Updateable when Invalidated</td>");
                if (runSettings.getFileManagement().isTestRuwiOk()) html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getFileManagement().getTestRuwiMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
                }
            }
            else html.append("\n<tr><td class=\"item\">Readable & Updateable when Invalidated</td><td>(not included)</td></tr>");
            if (runSettings.getFileManagement().isIncludeSfiTest()) {
                html.append("\n<tr><td class=\"item\">SFI TEST</td>");
                if (runSettings.getFileManagement().isTestSfiOk()) html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getFileManagement().getTestSfiMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
                }
            }
            else html.append("\n<tr><td class=\"item\">SFI Check</td><td>(not included)</td></tr>");
            html.append(createTableFooter());

             */
        }



        // OTA settings
        html.append("\n<div><h2>SCP-80 Keysets</h2></div>");
        for (SCP80Keyset keyset : runSettings.getScp80Keysets()) {
            html.append("\n<div><h3>" + keyset.getKeysetName() + "</h3></div>");
            html.append(createTableHeaderModule());
            html.append(
                "\n<tr><td class=\"item\">Version</td>"
                + "<td>" + keyset.getKeysetVersion() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Type</td>"
                + "<td>" + keyset.getKeysetType() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">KIC valuation</td>"
                + "<td>" + getValue(keyset.getKicValuation()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">KIC key length</td>"
                + "<td>" + keyset.getKicKeyLength() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">KIC mode</td>"
                + "<td>" + keyset.getKicMode() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">KID valuation</td>"
                + "<td>" + getValue(keyset.getKidValuation()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">KID key length</td>"
                + "<td>" + keyset.getKidKeyLength() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">KID mode</td>"
                + "<td>" + keyset.getKidMode() + "</td></tr>"
            );
            if (keyset.getKidMode().equals("AES - CMAC")) {
                html.append(
                    "\n<tr><td class=\"item\">CMAC length</td>"
                    + "<td>" + keyset.getCmacLength() + "</td></tr>"
                );
            }
            html.append(createTableFooter());
        }
        html.append("\n<div><h2>Envelope & SMS Update Parameters</h2></div>");
        html.append(createTableHeaderModule());
        html.append(
            "\n<tr><td class=\"item\">UDHI first byte</td>"
            + "<td>" + runSettings.getSmsUpdate().getUdhiFirstByte() + "</td></tr>"
        );
        html.append(
            "\n<tr><td class=\"item\">SC address</td>"
            + "<td>" + runSettings.getSmsUpdate().getScAddress() + "</td></tr>"
        );
        html.append(
            "\n<tr><td class=\"item\">TP-PID</td>"
            + "<td>" + runSettings.getSmsUpdate().getTpPid() + "</td></tr>"
        );
        if (runSettings.getSmsUpdate().isUseWhiteList()) {
            html.append(
                "\n<tr><td class=\"item\">TP-OA</td>"
                + "<td>" + runSettings.getSmsUpdate().getTpOa() + "</td></tr>"
            );
        }
        html.append(
            "\n<tr><td class=\"item\">PoR format</td>"
            + "<td>" + runSettings.getSmsUpdate().getPorFormat() + "</td></tr>"
        );
        html.append(createTableFooter());

        // secret codes
        if (runSettings.getSecretCodes().isInclude3gScript() || runSettings.getSecretCodes().isInclude2gScript()) {
            html.append("\n<div><h2>Secret Codes</h2></div>");

            html.append("\n<div><h3>Test modules</h3></div>");
            html.append(createTableHeaderModule());
            if (runSettings.getSecretCodes().isInclude3gScript()) {
                html.append("\n<tr><td class=\"item\">Secret codes 3G</td>");
                if (runSettings.getSecretCodes().isTestCodes3gOk()) html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getSecretCodes().getTestCodes3gMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
                }
            }
            else html.append("\n<tr><td class=\"item\">Secret codes 3G</td><td>(not included)</td></tr>");
            if (runSettings.getSecretCodes().isInclude2gScript()) {
                html.append("\n<tr><td class=\"item\">Secret codes 2G</td>");
                if (runSettings.getSecretCodes().isTestCodes2gOk()) html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getSecretCodes().getTestCodes2gMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
                }
            }
            else html.append("\n<tr><td class=\"item\">Secret codes 2G</td><td>(not included)</td></tr>");
            html.append(createTableFooter());

            html.append("\n<div><h3>Global PIN</h3></div>");
            html.append(createTableHeaderModule());
            html.append("\n<tr><td class=\"item\">PIN1 disabled</td>");
            if (runSettings.getSecretCodes().isPin1disabled()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append(
                "\n<tr><td class=\"item\">Value</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getGpin()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Retries</td>"
                + "<td>" + runSettings.getSecretCodes().getGpinRetries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Unblock key</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getGpuk()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Unblock retries</td>"
                + "<td>" + runSettings.getSecretCodes().getGpukRetries() + "</td></tr>"
            );
            html.append("\n<tr><td class=\"item\">Block Global PUK</td>");
            if (runSettings.getSecretCodes().isBlockGpuk()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append(createTableFooter());

            html.append("\n<div><h3>Local PIN</h3></div>");
            html.append(createTableHeaderModule());
            html.append("\n<tr><td class=\"item\">PIN2 disabled</td>");
            if (runSettings.getSecretCodes().isPin2disabled()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append(
                "\n<tr><td class=\"item\">Value</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getLpin()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Retries</td>"
                + "<td>" + runSettings.getSecretCodes().getLpinRetries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Unblock key</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getLpuk()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Unblock retries</td>"
                + "<td>" + runSettings.getSecretCodes().getLpukRetries() + "</td></tr>"
            );
            html.append("\n<tr><td class=\"item\">Block Local PUK</td>");
            if (runSettings.getSecretCodes().isBlockLpuk()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append(createTableFooter());

            html.append("\n<div><h3>CHV1</h3></div>");
            html.append(createTableHeaderModule());
            html.append(
                "\n<tr><td class=\"item\">Value</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getChv1()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Retries</td>"
                + "<td>" + runSettings.getSecretCodes().getChv1Retries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Unblock key</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getPuk1()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Unblock retries</td>"
                + "<td>" + runSettings.getSecretCodes().getPuk1Retries() + "</td></tr>"
            );
            html.append("\n<tr><td class=\"item\">Block PUK1</td>");
            if (runSettings.getSecretCodes().isBlockPuk1()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append(createTableFooter());

            html.append("\n<div><h3>CHV2</h3></div>");
            html.append(createTableHeaderModule());
            html.append(
                "\n<tr><td class=\"item\">Value</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getChv2()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Retries</td>"
                + "<td>" + runSettings.getSecretCodes().getChv2Retries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Unblock key</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getPuk2()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">Unblock retries</td>"
                + "<td>" + runSettings.getSecretCodes().getPuk2Retries() + "</td></tr>"
            );
            html.append("\n<tr><td class=\"item\">Block PUK2</td>");
            if (runSettings.getSecretCodes().isBlockPuk2()) html.append("<td>YES</td></tr>");
            else html.append("<td>NO</td></tr>");
            html.append(createTableFooter());

            html.append("\n<div><h3>Issuer Secret Codes</h3></div>");
            html.append(createTableHeaderModule());
            html.append(
                "\n<tr><td class=\"item\">Issuer Secret Code 1</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getIsc1()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td class=\"item\">ISC1 retries</td>"
                + "<td>" + runSettings.getSecretCodes().getIsc1Retries() + "</td></tr>"
            );
            if (runSettings.getSecretCodes().isUseIsc2()) {
                html.append(
                    "\n<tr><td class=\"item\">Issuer Secret Code 2</td>"
                    + "<td>" + getValue(runSettings.getSecretCodes().getIsc2()) + "</td></tr>"
                );
                html.append(
                    "\n<tr><td class=\"item\">ISC2 retries</td>"
                    + "<td>" + runSettings.getSecretCodes().getIsc2Retries() + "</td></tr>"
                );
            }
            if (runSettings.getSecretCodes().isUseIsc3()) {
                html.append(
                    "\n<tr><td class=\"item\">Issuer Secret Code 3</td>"
                    + "<td>" + getValue(runSettings.getSecretCodes().getIsc3()) + "</td></tr>"
                );
                html.append(
                    "\n<tr><td class=\"item\">ISC3 retries</td>"
                    + "<td>" + runSettings.getSecretCodes().getIsc3Retries() + "</td></tr>"
                );
            }
            if (runSettings.getSecretCodes().isUseIsc4()) {
                html.append(
                    "\n<tr><td class=\"item\">Issuer Secret Code 4</td>"
                    + "<td>" + getValue(runSettings.getSecretCodes().getIsc4()) + "</td></tr>"
                );
                html.append(
                    "\n<tr><td class=\"item\">ISC4 retries</td>"
                    + "<td>" + runSettings.getSecretCodes().getIsc4Retries() + "</td></tr>"
                );
            }
            html.append(createTableFooter());
        }
        html.append("\n<div><h2>Other Tests</h2></div>");
        if (runSettings.getCustomScriptsSection1().size() > 0) printCustomScriptsReport(html, runSettings.getCustomScriptsSection1());
        if (runSettings.getCustomScriptsSection2().size() > 0) printCustomScriptsReport(html, runSettings.getCustomScriptsSection2());
        if (runSettings.getCustomScriptsSection3().size() > 0) printCustomScriptsReport(html, runSettings.getCustomScriptsSection3());

        html.append(createDocumentFooter());
        return html;
    }

    private void printCustomScriptsReport(StringBuilder htmlBuffer, List<CustomScript> customScripts) {
        for (CustomScript customScript : customScripts) {
            htmlBuffer.append("\n<div><h3>" + customScript.getDescription() + "</h3></div>");
            htmlBuffer.append(createTableHeaderModule());
            htmlBuffer.append("\n<tr><td class=\"item\">Test result</td>");
            if (customScript.isRunCustomScriptOk()) htmlBuffer.append("<td class=\"ok\">PASSED</td></tr>");
            else {
                String[] messages = customScript.getRunCustomScriptMessage().split(";");
                htmlBuffer.append("<td class=\"error\">" + String.join("<br/>", messages) + "</td></tr>");
            }
            htmlBuffer.append(
                "\n<tr><td class=\"item\">Script name</td>"
                + "<td>" + customScript.getCustomScriptName() + "</td></tr>"
            );
            htmlBuffer.append(createTableFooter());
        }
    }

    private String createDocumentHeader() {
        StringBuilder header = new StringBuilder();
        header.append(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" />\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\" />\n" +
            "<title>Run All Report</title>\n" +
            "<style type=\"text/css\">\n" +
            "html,\n" +
            "body {\n" +
            "\theight: 100%;\n" +
            "}\n" +
            "html {\n" +
            "\tfont-size: 16px;\n" +
            "}\n" +
            "body {\n" +
            "\tmargin: 0px;\n" +
            "\tpadding: 0px;\n" +
            "\toverflow-x: hidden;\n" +
            "\tmin-width: 320px;\n" +
            "\tfont-family: Arial, Helvetica, sans-serif;\n" +
            "\tfont-size: 13px;\n" +
            "\tline-height: 1.33;\n" +
            "\tcolor: #212121;\n" +
            "\tfont-smoothing: antialiased;\n" +
            "}\n" +
            "div {\n" +
            "\tmargin-top: 10px;\n" +
            "\tmargin-left: 40px;\n" +
            "\tmargin-right: 40px;\n" +
            "}\n" +
            "h1,\n" +
            "h2,\n" +
            "h3,\n" +
            "h4,\n" +
            "h5 {\n" +
            "\tfont-family: Arial, Helvetica, sans-serif;\n" +
            "\tline-height: 1.33em;\n" +
            "\tmargin: calc(2rem -  0.165em ) 0em 1rem;\n" +
            "\tfont-weight: 400;\n" +
            "\tpadding: 0em;\n" +
            "}\n" +
            "table {\n" +
            "\tborder-collapse: collapse;\n" +
//            "\twidth: 100%;\n" +
            "}\n" +
            "table.module {\n" +
            "\twidth: 100%;\n" +
            "}\n" +
            "th,\n" +
            "td {\n" +
            "\twidth: 50%;\n" +
            "\ttext-align: left;\n" +
            "\tpadding: 4px;\n" +
            "\tborder-bottom: 1px solid #ddd;\n" +
            "}\n" +
            "th.error {\n" +
            "\twidth: 50%;\n" +
            "\tbackground-color: firebrick;\n" +
            "\tcolor: #F9F9F9;\n" +
            "}\n" +
            "th.warning {\n" +
            "\twidth: 50%;\n" +
            "\tbackground-color: darkorange;\n" +
            "\tcolor: #F9F9F9;\n" +
            "}\n" +
//            "tr:hover {background-color: #f5f5f5;}\n" +
            "td.item {\n" +
            "\twidth: 50%;\n" +
//            "\tfont-weight: bold;\n" +
            "\tbackground-color: #EEE8FB;\n" +
            "\tcolor: #17202A;\n" +
            "}\n" +
            "td.error {\n" +
            "\twidth: 50%;\n" +
            "\tbackground-color: #FDEDEC;\n" +
            "\tcolor: #17202A;\n" +
            "}\n" +
            "td.warning {\n" +
            "\twidth: 50%;\n" +
            "\tbackground-color: #FEF9E7;\n" +
            "\tcolor: #17202A;\n" +
            "}\n" +
            "td.ok {\n" +
            "\twidth: 50%;\n" +
            "\tbackground-color: #EDFBEE;\n" +
            "\tcolor: #17202A;\n" +
            "}" +
            "td.data {\n" +
            "\twidth: 50%;\n" +
            "\tfont-family: consolas, Monaco, monospace;\n" +
            "\tfont-size: 13px;\n" +
            "}\n" +
            "ul {\n" +
            "\tmargin: 0px;\n" +
            "\tpadding: 15px;\n" +
            "}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n"
        );
        return header.toString();
    }

    private String createDocumentFooter() { return "\n<div><i>Created by CARDIO on " + new Timestamp(System.currentTimeMillis()) + "</i></div>\n</body>\n</html>"; }

    private String createTableHeader() { return "\n<div>\n" + "<table>\n" + "<tbody>"; }

    private String createTableHeaderModule() { return "\n<div>\n" + "<table class=\"module\">\n" + "<tbody>"; }

    private String createTableFooter() { return "\n</tbody>\n" + "</table>\n" + "</div>\n<br/>"; }

    private String getValue(String mappedVar) {
        String value = "";
        for (VariableMapping mapping : runSettings.getVariableMappings()) {
            if (mapping.getMappedVariable().equals(mappedVar)) {
                if (mapping.isFixed()) value = mapping.getValue();
                else value = "<i>" + mapping.getMccVariable() + "</i>";
                break;
            }
        }
        return value;
    }

}
