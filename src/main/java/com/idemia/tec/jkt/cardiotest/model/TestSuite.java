package com.idemia.tec.jkt.cardiotest.model;

import com.google.gson.Gson;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "testsuite")
public class TestSuite {

    private String name;
    private String packageName;
    private String failures;
    private String errors;
    private String skipped;
    private String tests;
    private String time;

    private String systemErr;

    private List<TestCase> testCases;

    public TestSuite() {}

    public TestSuite(String name, String packageName, String failures, String errors, String skipped, String tests, String time, String systemErr, List<TestCase> testCases) {
        this.name = name;
        this.packageName = packageName;
        this.failures = failures;
        this.errors = errors;
        this.skipped = skipped;
        this.tests = tests;
        this.time = time;
        this.systemErr = systemErr;
        this.testCases = testCases;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    @XmlAttribute(name = "package")
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFailures() {
        return failures;
    }

    @XmlAttribute
    public void setFailures(String failures) {
        this.failures = failures;
    }

    public String getErrors() {
        return errors;
    }

    @XmlAttribute
    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getSkipped() {
        return skipped;
    }

    @XmlAttribute
    public void setSkipped(String skipped) {
        this.skipped = skipped;
    }

    public String getTests() {
        return tests;
    }

    @XmlAttribute
    public void setTests(String tests) {
        this.tests = tests;
    }

    public String getTime() {
        return time;
    }

    @XmlAttribute
    public void setTime(String time) {
        this.time = time;
    }

    public String getSystemErr() {
        return systemErr;
    }

    @XmlElement(name = "system-err")
    public void setSystemErr(String systemErr) {
        this.systemErr = systemErr;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    @XmlElement(name = "testcase")
    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
