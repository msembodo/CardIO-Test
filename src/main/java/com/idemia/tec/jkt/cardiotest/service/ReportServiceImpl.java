package com.idemia.tec.jkt.cardiotest.service;

import com.idemia.tec.jkt.cardiotest.model.RunSettings;
import com.idemia.tec.jkt.cardiotest.model.VariableMapping;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.sql.Timestamp;

@Service
public class ReportServiceImpl implements ReportService {

    private RunSettings runSettings;

    @Override
    public void createReportFromSettings(RunSettings runSettings) {
        this.runSettings = runSettings;
        boolean reportAsPdf = true;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(runSettings.getProjectPath() + "\\RunAll.html"))) {
            bw.append(composeHtml());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (reportAsPdf) {
            try {
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(runSettings.getProjectPath() + "\\RunAll.pdf"));
                document.open();
                XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(runSettings.getProjectPath() + "\\RunAll.html"));
                document.close();
                Files.deleteIfExists(new File(runSettings.getProjectPath() + "\\RunAll.html").toPath());
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private StringBuilder composeHtml() {
        StringBuilder html = new StringBuilder();
        html.append(createDocumentHeader());
        html.append("\n<div><h1>TEC Testing Report</h1></div>");
        // project details
        html.append(
            "\n<div><h2><b>Project Details</b></h2></div>"
            + createTableHeader()
            + "\n<tr><td>Request ID</td>"
            + "<td>" + runSettings.getRequestId() + "</td></tr>"
            + "\n<tr><td>Request name</td>"
            + "<td>" + runSettings.getRequestName() + "</td></tr>"
            + "\n<tr><td>Profile name</td>"
            + "<td>" + runSettings.getProfileName() + "</td></tr>"
            + "\n<tr><td>Profile version</td>"
            + "<td>" + runSettings.getProfileVersion() + "</td></tr>"
            + "\n<tr><td>Image item ID</td>"
            + "<td>" + runSettings.getCardImageItemId() + "</td></tr>"
            + "\n<tr><td>Customer</td>"
            + "<td>" + runSettings.getCustomer() + "</td></tr>"
            + "\n<tr><td>Developer</td>"
            + "<td>" + runSettings.getDeveloperName() + "</td></tr>"
            + "\n<tr><td>Tester</td>"
            + "<td>" + runSettings.getTesterName() + "</td></tr>"
            + createTableFooter()
        );

        // variable mappings
        html.append("\n<div><h2><b>Variable Mappings</b></h2></div>");
        html.append(createTableHeaderModule());
        html.append(
            "\n<tr><th>Mapped variable</th>"
            + "<th>Value / MCC variable</th></tr>"
        );
        for (VariableMapping mapping : runSettings.getVariableMappings()) {
            html.append("\n<tr id=\"" + mapping.getMappedVariable() + "\"><td>" + mapping.getMappedVariable() + "</td>");
            if (mapping.isFixed())
                html.append("<td>" + mapping.getValue() + "</td></tr>");
            else
                html.append("<td>%" + mapping.getMccVariable() + "</td></tr>");
        }
        html.append(createTableFooter());
        // card parameters
        html.append("\n<div><h2><b>Card Parameters</b></h2></div>");
        html.append(createTableHeaderModule());
        if (!runSettings.getCardParameters().getCardManagerAid().equals(""))
            html.append(
                "\n<tr><td>Card manager AID</td>"
                + "<td>" + runSettings.getCardParameters().getCardManagerAid() + "</td></tr>"
            );
        if (!runSettings.getCardParameters().getUsimAid().equals(""))
            html.append(
                "\n<tr><td>USIM AID</td>"
                + "<td>" + runSettings.getCardParameters().getUsimAid() + "</td></tr>"
            );
        if (!runSettings.getCardParameters().getDfUsim().equals(""))
            html.append(
                "\n<tr><td>DF USIM</td>"
                + "<td>" + runSettings.getCardParameters().getDfUsim() + "</td></tr>"
            );
        if (!runSettings.getCardParameters().getDfGsmAccess().equals(""))
            html.append(
                "\n<tr><td>DF GSM Access</td>"
                + "<td>" + runSettings.getCardParameters().getDfGsmAccess() + "</td></tr>"
            );
        if (!runSettings.getCardParameters().getDfTelecom().equals(""))
            html.append(
                "\n<tr><td>DF Telecom</td>"
                + "<td>" + runSettings.getCardParameters().getDfTelecom() + "</td></tr>"
            );
        if (!runSettings.getCardParameters().getIsimAid().equals(""))
            html.append(
                "\n<tr><td>ISIM AID</td>"
                + "<td>" + runSettings.getCardParameters().getIsimAid() + "</td></tr>"
            );
        if (!runSettings.getCardParameters().getDfIsim().equals(""))
            html.append(
                "\n<tr><td>DF ISIM</td>"
                + "<td>" + runSettings.getCardParameters().getDfIsim() + "</td></tr>"
            );
        if (!runSettings.getCardParameters().getCsimAid().equals(""))
            html.append(
                "\n<tr><td>CSIM AID</td>"
                + "<td>" + runSettings.getCardParameters().getCsimAid() + "</td></tr>"
            );
        if (!runSettings.getCardParameters().getDfCsim().equals(""))
            html.append(
                "\n<tr><td>DF CSIM</td>"
                + "<td>" + runSettings.getCardParameters().getDfCsim() + "</td></tr>"
            );
        html.append(createTableFooter());

        // ATR
        if (runSettings.getAtr().isIncludeAtr()) {
            html.append("\n<div><h2><b>Answer To Reset</b></h2></div>");
            html.append(createTableHeaderModule());
            html.append("\n<tr><td>ATR check</td>");
            if (runSettings.getAtr().isTestAtrOk())
                html.append("<td class=\"ok\">PASSED</td></tr>");
            else {
                String[] messages = runSettings.getAtr().getTestAtrMesssage().split(";");
                html.append("<td class=\"error\">" + String.join("<br>", messages) + "</td></tr>");
            }
            html.append(
                "\n<tr><td>ATR</td>"
                + "<td>" + runSettings.getAtr().getAtrString() + "</td></tr>"
                + "\n<tr><td>Status</td>"
                + "<td>" + runSettings.getAtr().getStatus() + "</td></tr>"
                + "\n<tr><td>TCK</td>"
                + "<td>" + runSettings.getAtr().getTck() + "</td></tr>"
            );
            html.append(createTableFooter());
        }

        // authentication
        if (runSettings.getAuthentication().isIncludeDeltaTest() || runSettings.getAuthentication().isIncludeSqnMax()) {
            html.append("\n<div><h2><b>Authentication</b></h2></div>");
            html.append(createTableHeaderModule());
            if (runSettings.getAuthentication().isIncludeDeltaTest()) {
                html.append("\n<tr><td>Milenage delta test</td>");
                if (runSettings.getAuthentication().isTestDeltaOk())
                    html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getAuthentication().getTestDeltaMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br>", messages) + "</td></tr>");
                }
            }
            if (runSettings.getAuthentication().isIncludeSqnMax()) {
                html.append("\n<tr><td>Milenage SQN max</td>");
                if (runSettings.getAuthentication().isTestSqnMaxOk())
                    html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getAuthentication().getTestSqnMaxMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br>", messages) + "</td></tr>");
                }
            }
            html.append("\n<tr><td>COMP128-2</td>");
            if (runSettings.getAuthentication().isComp1282())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append("\n<tr><td>COMP128-3</td>");
            if (runSettings.getAuthentication().isComp1283())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append("\n<tr><td>Milenage</td>");
            if (runSettings.getAuthentication().isMilenage())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append("\n<tr><td>ISIM auth</td>");
            if (runSettings.getAuthentication().isIsimAuth())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append("\n<tr><td>GSM algo</td>");
            if (runSettings.getAuthentication().isGsmAlgo())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append(
                "\n<tr><td>Authentication AKA - Ci Value 1</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC1()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Authentication AKA - Ci Value 2</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC2()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Authentication AKA - Ci Value 3</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC3()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Authentication AKA - Ci Value 4</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC4()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Authentication AKA - Ci Value 5</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaC5()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Rotation constants (Ri)</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getAkaRi()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Subscriber key (K)</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getKi()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>OPc</td>"
                + "<td>" + getValue(runSettings.getAuthentication().getOpc()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Random challenge (RAND)</td>"
                + "<td>" + runSettings.getAuthentication().getRand() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Signed response (RES) length</td>"
                + "<td>" + Integer.parseInt(runSettings.getAuthentication().getResLength()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>SQN init</td>"
                + "<td>" + runSettings.getAuthentication().getSqn() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>SQN max</td>"
                + "<td>" + runSettings.getAuthentication().getSqnMax() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Authentication Management Field (AMF)</td>"
                + "<td>" + runSettings.getAuthentication().getAmf() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Delta</td>"
                + "<td>" + runSettings.getAuthentication().getDelta() + "</td></tr>"
            );
            html.append(createTableFooter());
        }

        // secret codes
        if (runSettings.getSecretCodes().isInclude3gScript() || runSettings.getSecretCodes().isInclude2gScript()) {
            html.append("\n<div><h2><b>Secret Codes</b></h2></div>");
            html.append(createTableHeaderModule());
            if (runSettings.getSecretCodes().isInclude3gScript()) {
                html.append("\n<tr><td>Secret codes 3G</td>");
                if (runSettings.getSecretCodes().isTestCodes3gOk())
                    html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getSecretCodes().getTestCodes3gMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br>", messages) + "</td></tr>");
                }
            }
            if (runSettings.getSecretCodes().isInclude2gScript()) {
                html.append("\n<tr><td>Secret codes 2G</td>");
                if (runSettings.getSecretCodes().isTestCodes2gOk())
                    html.append("<td class=\"ok\">PASSED</td></tr>");
                else {
                    String[] messages = runSettings.getSecretCodes().getTestCodes2gMessage().split(";");
                    html.append("<td class=\"error\">" + String.join("<br>", messages) + "</td></tr>");
                }
            }
            html.append(
                "\n<tr><td>Global PIN</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getGpin()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Local PIN</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getLpin()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Global PUK</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getGpuk()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Local PUK</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getLpuk()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Global PIN retries</td>"
                + "<td>" + runSettings.getSecretCodes().getGpinRetries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Local PIN retries</td>"
                + "<td>" + runSettings.getSecretCodes().getLpinRetries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Global PUK retries</td>"
                + "<td>" + runSettings.getSecretCodes().getGpukRetries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>Local PUK retries</td>"
                + "<td>" + runSettings.getSecretCodes().getLpukRetries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>CHV1</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getChv1()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>CHV2</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getChv2()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>PUK1</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getPuk1()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>PUK2</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getPuk2()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>CHV1 retries</td>"
                + "<td>" + runSettings.getSecretCodes().getChv1Retries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>CHV2 retries</td>"
                + "<td>" + runSettings.getSecretCodes().getChv2Retries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>PUK1 retries</td>"
                + "<td>" + runSettings.getSecretCodes().getPuk1Retries() + "</td></tr>"
            );
            html.append(
                "\n<tr><td>PUK2 retries</td>"
                + "<td>" + runSettings.getSecretCodes().getPuk2Retries() + "</td></tr>"
            );
            html.append("\n<tr><td>Block Global PUK</td>");
            if (runSettings.getSecretCodes().isBlockGpuk())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append("\n<tr><td>Block Local PUK</td>");
            if (runSettings.getSecretCodes().isBlockLpuk())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append("\n<tr><td>Block PUK1</td>");
            if (runSettings.getSecretCodes().isBlockPuk1())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append("\n<tr><td>Block PUK2</td>");
            if (runSettings.getSecretCodes().isBlockPuk2())
                html.append("<td>YES</td></tr>");
            else
                html.append("<td>NO</td></tr>");
            html.append(
                "\n<tr><td>Issuer Secret Code 1</td>"
                + "<td>" + getValue(runSettings.getSecretCodes().getIsc1()) + "</td></tr>"
            );
            html.append(
                "\n<tr><td>ISC1 retries</td>"
                + "<td>" + runSettings.getSecretCodes().getIsc1Retries() + "</td></tr>"
            );
            if (runSettings.getSecretCodes().isUseIsc2()) {
                html.append(
                    "\n<tr><td>Issuer Secret Code 2</td>"
                    + "<td>" + getValue(runSettings.getSecretCodes().getIsc2()) + "</td></tr>"
                );
                html.append(
                    "\n<tr><td>ISC2 retries</td>"
                    + "<td>" + runSettings.getSecretCodes().getIsc2Retries() + "</td></tr>"
                );
            }
            if (runSettings.getSecretCodes().isUseIsc3()) {
                html.append(
                    "\n<tr><td>Issuer Secret Code 3</td>"
                    + "<td>" + getValue(runSettings.getSecretCodes().getIsc3()) + "</td></tr>"
                );
                html.append(
                    "\n<tr><td>ISC3 retries</td>"
                    + "<td>" + runSettings.getSecretCodes().getIsc3Retries() + "</td></tr>"
                );
            }
            if (runSettings.getSecretCodes().isUseIsc4()) {
                html.append(
                    "\n<tr><td>Issuer Secret Code 4</td>"
                    + "<td>" + getValue(runSettings.getSecretCodes().getIsc4()) + "</td></tr>"
                );
                html.append(
                    "\n<tr><td>ISC4 retries</td>"
                    + "<td>" + runSettings.getSecretCodes().getIsc4Retries() + "</td></tr>"
                );
            }
            html.append(createTableFooter());
        }

        html.append(createDocumentFooter());
        return html;
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
            "\ttext-align: left;\n" +
            "\tpadding: 4px;\n" +
            "\tborder-bottom: 1px solid #ddd;\n" +
            "}\n" +
            "th.error {\n" +
            "\tbackground-color: firebrick;\n" +
            "\tcolor: #F9F9F9;\n" +
            "}\n" +
            "th.warning {\n" +
            "\tbackground-color: darkorange;\n" +
            "\tcolor: #F9F9F9;\n" +
            "}\n" +
//            "tr:hover {background-color: #f5f5f5;}\n" +
            "td.error {\n" +
            "\tbackground-color: #FDEDEC;\n" +
            "\tcolor: #17202A;\n" +
            "}\n" +
            "td.warning {\n" +
            "\tbackground-color: #FEF9E7;\n" +
            "\tcolor: #17202A;\n" +
            "}\n" +
            "td.ok {\n" +
            "\tbackground-color: #EDFBEE;\n" +
            "\tcolor: #17202A;\n" +
            "}" +
            "td.data {\n" +
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

    private String createDocumentFooter() {
        return "\n<div><i>Created by CardIO on " + new Timestamp(System.currentTimeMillis()) + "</i></div>\n</body>\n</html>";
    }

    private String createTableHeader() {
        return "\n<div>\n" + "<table>\n" + "<tbody>";
    }

    private String createTableHeaderModule() {
        return "\n<div>\n" + "<table class=\"module\">\n" + "<tbody>";
    }

    private String createTableFooter() {
        return "\n</tbody>\n" + "</table>\n" + "</div>";
    }

    private String getValue(String mappedVar) {
        String value = "";
        for (VariableMapping mapping : runSettings.getVariableMappings()) {
            if (mapping.getMappedVariable().equals(mappedVar)) {
                if (mapping.isFixed())
                    value = mapping.getValue();
                else
                    value = "%" + mapping.getMccVariable();
                break;
            }
        }
        return value;
    }

}
