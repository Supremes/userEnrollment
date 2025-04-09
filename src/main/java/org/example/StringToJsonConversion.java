package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringToJsonConversion {
    private static final List<String> ACTION_TYPES = List.of("AddContentHeader", "AddContentFooter");

    public static String toJsonAgain(String input) {
        try {
            // 按行拆分输入字符串
            String[] lines = input.split("\n");

            // 创建一个JSON数组节点来存储每个LABEL的Id和Name键值对
            ArrayNode labelsArray = new ObjectMapper().createArrayNode();

            // 遍历每一行，提取Id和Name键值对
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].startsWith("LABEL:")) {
                    continue;
                } else {
                    String[] parts = lines[i].split(":");
                    if (parts.length < 2 && !parts[0].trim().equals("Children")) {
                        continue;
                    }

                    if (parts[0].trim().equals("Children")) {
                        // 获取刚刚添加的对象节点，添加Children键值对
                        if (!labelsArray.isEmpty()) {
                            ObjectNode lastLabelNode = (ObjectNode) labelsArray.get(labelsArray.size() - 1);
                            lastLabelNode.put("HasChildren", true);
                        }
                        continue;
                    }

                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if ("Id".equals(key)) {
                        ObjectNode labelNode = labelsArray.addObject();
                        labelNode.put("Id", value);
                    } else if ("Name".equals(key)) {
                        // 获取刚刚添加的对象节点，添加Name键值对
                        if (!labelsArray.isEmpty()) {
                            ObjectNode lastLabelNode = (ObjectNode) labelsArray.get(labelsArray.size() - 1);
                            lastLabelNode.put("Name", value);
                        }
                    } else if ("Parent Id".equals(key)) {
                        if (!labelsArray.isEmpty()) {
                            ObjectNode lastLabelNode = (ObjectNode) labelsArray.get(labelsArray.size() - 1);
                            lastLabelNode.put("Parent Id", value);
                        }
                    }
                }
            }

            // 将包含Id和Name键值对的数组节点转换为JSON字符串并输出
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(labelsArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String actionsToJson(String input) {
        try {
            // 按行拆分输入字符串
            String[] lines = input.split("\n");

            // 创建一个JSON数组节点来存储每个LABEL的Id和Name键值对
            ArrayNode labelsArray = new ObjectMapper().createArrayNode();

            // 遍历每一行，提取Id和Name键值对
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].startsWith("LABEL:")) {
                    continue;
                } else {
                    String[] parts = lines[i].split(":");
                    if (parts.length < 2) {
                        continue;
                    }

                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if ("Type".equals(key) && ACTION_TYPES.contains(value)) {
                        ObjectNode labelNode = labelsArray.addObject();
                        labelNode.put("Type", value);
                    } else if ("Text".equals(key)) {
                        // 获取刚刚添加的对象节点，添加Name键值对
                        if (!labelsArray.isEmpty()) {
                            ObjectNode lastLabelNode = (ObjectNode) labelsArray.get(labelsArray.size() - 1);
                            if (!lastLabelNode.has("Text")) {
                                lastLabelNode.put("Text", value);
                            }
                        }
                    }
                }
            }

            // 将包含Id和Name键值对的数组节点转换为JSON字符串并输出
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(labelsArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
