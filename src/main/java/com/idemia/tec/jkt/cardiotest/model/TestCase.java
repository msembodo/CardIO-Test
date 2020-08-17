package com.idemia.tec.jkt.cardiotest.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "testcase")
public class TestCase {

    private String name;
    private String className;
    private String time;

    private String error;
    private String failure;

    public TestCase() {}

    public TestCase(String name, String className, String time, String error, String failure) {
        this.name = name;
        this.className = className;
        this.time = time;
        this.error = error;
        this.failure = failure;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    @XmlAttribute(name = "classname") public void setClassName(String className) {
        this.className = className;
    }

    public String getTime() {
        return time;
    }

    @XmlAttribute public void setTime(String time) {
        this.time = time;
    }

    public String getError() {
        return error;
    }

    @XmlElement public void setError(String error) {
        this.error = error;
    }

    public String getFailure() {
        return failure;
    }

    @XmlElement public void setFailure(String failure) {
        this.failure = failure;
    }

}
