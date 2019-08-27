package com.sudoprototype.firstsmartbois.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sudoprototype.firstsmartbois.MainGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 768;
		config.title = "My First Smartbois";
		new LwjglApplication(new MainGdxGame(), config);
	}
}
