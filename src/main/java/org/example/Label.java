package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 对应单个标签的类
class Label {
    private String id;
    private String name;
    private String description;
    private boolean isActive;
    private String color;
    private int sensitivity;
    private String tooltip;
    private List<String> contentFormats;
    private List<Label> children;

    public Label() {
        this.contentFormats = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    // 以下是各个属性的getter和setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public List<String> getContentFormats() {
        return contentFormats;
    }

    public void setContentFormats(List<String> contentFormats) {
        this.contentFormats = contentFormats;
    }

    public List<Label> getChildren() {
        return children;
    }

    public void setChildren(List<Label> children) {
        this.children = children;
    }
}
