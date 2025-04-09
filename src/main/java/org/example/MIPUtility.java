package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MIPUtility {
    private static final String APPLICATION_ID = "af3bb6bc-ff68-4f8b-ab66-e3f2868ed3b4";
    private static final String APP_NAME = "MipSdk-Sample-Apps";
    private static final String APP_VERSION = "1.0";
    private static final String USER_NAME = "altuser3@smblr.onmicrosoft.com";
    private static final String DEMO_PATH = "/home/junkangd/CEM/SDK/MIP/bins/debug/x86_64/upe_sample";


    @PostMapping(path = "/listLabel")
    public String listLabel(@RequestParam("token") String token) {
        String params = " --username=" + USER_NAME + " --appname=" + APP_NAME + " --appid=" + APPLICATION_ID +
                " --appversion="+ APP_VERSION + " --token=" + token + " --listLabels";
        String labels = executeCommand(params);
//        parseYaml(labels);
        return StringToJsonConversion.toJsonAgain(labels);
    }

    @PostMapping(path = "/computeActions")
    public String computeActions(@RequestParam("labelId") String labelId, @RequestParam("token") String token) {
        String params = " --username=" + USER_NAME + " --appname=" + APP_NAME + " --appid=" + APPLICATION_ID +
                " --appversion="+ APP_VERSION + " --newLabelId=" + labelId + " --computeActions" + " --token=" + token;
        String actions = executeCommand(params);
        System.out.println("format before: " + actions);
        String res = StringToJsonConversion.actionsToJson(actions);
        System.out.println("format after: " + res);
        return res;
    }

    private String executeCommand(String params) {
        StringBuilder res = new StringBuilder();
        String[] command = {"/bin/bash", "-c", DEMO_PATH + params};
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                res.append(line).append("\n");
            }
//            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    public void parseYaml(String yamlString) {
        String str = yamlString.replaceFirst("CXM-116459:", "CXM-116459");
        Yaml yaml = new Yaml();
        Object obj = yaml.load(str);

        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;

            // 这里假设"Engine added with id"这部分信息不需要解析处理，重点处理LABEL相关内容
            // 获取所有LABEL相关的内容，可能是多个，所以用列表存储
            List<Map<String, Object>> labelList = new ArrayList<>();
            for (Object value : map.values()) {
                if (value instanceof Map && ((Map<?,?>) value).containsKey("Id")) {
                    labelList.add((Map<String, Object>) value);
                }
            }

            // 遍历解析每个LABEL的信息
            for (Map<String, Object> labelMap : labelList) {
                String id = (String) labelMap.get("Id");
                String name = (String) labelMap.get("Name");
                String description = (String) labelMap.get("Description");
                boolean isActive = (boolean) labelMap.get("IsActive");
                String color = (String) labelMap.get("Color");
                int sensitivity = (int) labelMap.get("Sensitivity");
                String tooltip = (String) labelMap.get("Tooltip");

                System.out.println("LABEL - Id: " + id);
                System.out.println("LABEL - Name: " + name);
                System.out.println("LABEL - Description: " + description);
                System.out.println("LABEL - IsActive: " + isActive);
                System.out.println("LABEL - Color: " + color);
                System.out.println("LABEL - Sensitivity: " + sensitivity);
                System.out.println("LABEL - Tooltip: " + tooltip);

                // 如果LABEL有子LABEL（Children），也进行解析
                if (labelMap.containsKey("Children")) {
                    List<Map<String, Object>> childrenList = (List<Map<String, Object>>) labelMap.get("Children");
                    for (Map<String, Object> childMap : childrenList) {
                        String childId = (String) childMap.get("Id");
                        String childName = (String) childMap.get("Name");
                        String childDescription = (String) childMap.get("Description");
                        boolean childIsActive = (boolean) childMap.get("IsActive");
                        String childColor = (String) childMap.get("Color");
                        int childSensitivity = (int) childMap.get("Sensitivity");
                        String childTooltip = (String) childMap.get("Tooltip");

                        System.out.println("  CHILD LABEL - Id: " + childId);
                        System.out.println("  CHILD LABEL - Name: " + childName);
                        System.out.println("  CHILD LABEL - Description: " + childDescription);
                        System.out.println("  CHILD LABEL - IsActive: " + childIsActive);
                        System.out.println("  CHILD LABEL - Color: " + childColor);
                        System.out.println("  CHILD LABEL - Sensitivity: " + childSensitivity);
                        System.out.println("  CHILD LABEL - Tooltip: " + childTooltip);
                    }
                }
            }
        }
    }
}
