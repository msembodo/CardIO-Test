package com.idemia.tec.jkt.cardiotest.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VariableMapping {

    private StringProperty mappedVariable;
    private StringProperty mccVariable;
    private StringProperty value;
    private boolean fixed;

    public VariableMapping() {}

    public VariableMapping(String mappedVariable, String mccVariable, String value, boolean fixed) {
        this.mappedVariable = new SimpleStringProperty(mappedVariable);
        this.mccVariable = new SimpleStringProperty(mccVariable);
        this.value = new SimpleStringProperty(value);
        this.fixed = fixed;
    }

    public String getMappedVariable() {
        return mappedVariable.get();
    }

    public void setMappedVariable(String mappedVariable) {
        this.mappedVariable = new SimpleStringProperty(mappedVariable);
    }

    public StringProperty mappedVariable() {
        return mappedVariable;
    }

    public String getMccVariable() {
        return mccVariable.get();
    }

    public void setMccVariable(String mccVariable) {
        this.mccVariable = new SimpleStringProperty(mccVariable);
    }

    public StringProperty mccVariable() {
        if (this.fixed)
            return value;
        else
            return mccVariable;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value = new SimpleStringProperty(value);
    }

    public StringProperty value() {
        return value;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

}
