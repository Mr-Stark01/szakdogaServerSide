package com.szakdogaServer.BusinessLogic;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class PathFinder {
    public PathFinder(String path) throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        File file = new File(path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement();
        NodeList nl = doc.getElementsByTagName("map");
        System.out.println(nl.item(0).getTextContent());
    }
}
