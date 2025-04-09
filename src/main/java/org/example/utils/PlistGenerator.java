package org.example.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Map;

public class PlistGenerator {
    public static String generatePlistResponse() throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // 创建根元素
        Document doc = docBuilder.newDocument();
        Element plist = doc.createElement("plist");
        plist.setAttribute("version", "1.0");
        doc.appendChild(plist);

        // 创建主字典
        Element dict = doc.createElement("dict");
        plist.appendChild(dict);

        // 添加PayloadType
        addKeyValuePair(doc, dict, "PayloadType", "Configuration");

        // 创建PayloadContent数组
        Element array = doc.createElement("array");
        dict.appendChild(array);

        // 添加SCEP配置字典
        Element scepDict = createPayloadDict(doc,
                "com.apple.security.scep"); // 替换实际参数
        array.appendChild(scepDict);

        // 添加MDM配置字典
        Element mdmDict = createPayloadDict(doc,
                "com.apple.mdm",
                Map.of(
                        "AssignedManagedAppleID", "junkang.du@citrixcom2.appleid.com",
                        "EnrollmentMode", "BYOD"
                ));
        array.appendChild(mdmDict);

        // 转换为XML字符串
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }

    private static void addKeyValuePair(Document doc, Element parent, String key, String value) {
        Element keyElem = doc.createElement("key");
        keyElem.appendChild(doc.createTextNode(key));
        parent.appendChild(keyElem);

        Element valueElem = doc.createElement("string");
        valueElem.appendChild(doc.createTextNode(value));
        parent.appendChild(valueElem);
    }

    private static Element createPayloadDict(Document doc, String payloadType) {
        Element dict = doc.createElement("dict");
        addKeyValuePair(doc, dict, "PayloadType", payloadType);
        return dict;
    }

    private static Element createPayloadDict(Document doc, String payloadType, Map<String, String> properties) {
        Element dict = doc.createElement("dict");
        addKeyValuePair(doc, dict, "PayloadType", payloadType);
        properties.forEach((k, v) -> addKeyValuePair(doc, dict, k, v));
        return dict;
    }
}