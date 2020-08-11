package com.idemia.tec.jkt.cardiotest.model;

import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CustomScript {

    private StringProperty customScriptName;
    private String description;

    public CustomScript() {}

    public CustomScript(String customScriptName, String description) {
        this.customScriptName = new SimpleStringProperty(customScriptName);
        this.description = description;
    }

    public String getCustomScriptName() {
        return customScriptName.get();
    }

    public StringProperty customScriptNameProperty() {
        return customScriptName;
    }

    public void setCustomScriptName(String customScriptName) {
        this.customScriptName = new SimpleStringProperty(customScriptName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toJson() { return new Gson().toJson(this); }

}
