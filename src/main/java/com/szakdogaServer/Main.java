package com.szakdogaServer;

import com.szakdogaServer.BusinessLogic.PathFinder;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {

        PathFinder pathFinder = new PathFinder("src/main/resources/map.tmx");
        //Server server = new Server();
        //server.start(56227);
    }
}