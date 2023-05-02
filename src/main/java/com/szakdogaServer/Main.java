package com.szakdogaServer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.szakdogaServer.network.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;

import java.io.IOException;


public class Main {
    private static Application application;

    public static void main(String[] args) throws IOException {
        // Mocking up a headless graphics side
        Logger logger = LogManager.getLogger(Main.class);
        while (true) {
            application = new HeadlessApplication(new ApplicationAdapter() {
            });
            logger.info("Headless Application setup");
            Gdx.gl20 = Mockito.mock(GL20.class);
            Gdx.gl = Gdx.gl20;
            logger.info("Necessary graphical elements successfully mocked");
            Server server = new Server();
            server.start(56227);
        }
    }
}