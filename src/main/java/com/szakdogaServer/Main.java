package com.szakdogaServer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.graphics.GL20;
import com.szakdogaServer.BusinessLogic.PathFinder;
import com.szakdogaServer.network.Server;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;


public class Main {
    private static Application application;
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        // Mocking up a headless graphics side
        application = new HeadlessApplication(new ApplicationAdapter() {
        });
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
        System.out.println(application.getFiles().getLocalStoragePath());
        /*PathFinder pathFinder = new PathFinder();
        pathFinder.calculateNextStep(null);*/
        Server server = new Server();
        server.start(56227);
    }
}