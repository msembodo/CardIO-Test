package com.idemia.tec.jkt.cardiotest.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AdvSaveVariable {

    private StringProperty definedVariable;
    private StringProperty value;

    public AdvSaveVariable() {}

    public AdvSaveVariable(String definedVariable, String value) {
        this.definedVariable = new SimpleStringProperty(definedVariable);
        this.value = new SimpleStringProperty(value);
    }

    public String getDefinedVariable() {
        return definedVariable.get();
    }

    public void setDefinedVariable(String definedVariable) {
        this.definedVariable.set(definedVariable);
    }

    public StringProperty definedVariable() {
        return definedVariable;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public StringProperty value() {
        return value;
    }

}
