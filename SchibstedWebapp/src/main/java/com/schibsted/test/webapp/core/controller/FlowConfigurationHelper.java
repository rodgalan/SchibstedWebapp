/*package com.schibsted.test.webapp.core.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class FlowConfigurationHelper extends JavaPlugin {
	private HttpServer server;
	private String redirectUrl;

	@Override
	public void onEnable() {
		getConfig().addDefault("redirectURL", "");
		getConfig().options().copyDefaults(true);
		saveConfig();

		redirectUrl = getConfig().getString("redirectURL");

		if (redirectUrl == null || redirectUrl.equals("")) {
			getLogger().severe(
					"Please set an url in the config.yml file to redirect all incoming http requests on port 80.");
			getPluginLoader().disablePlugin(this);
			return;
		}
		try {
			server = HttpServer.create(new InetSocketAddress(80), 0);
			server.createContext("/", new HttpHandler() {
				@Override
				public void handle(HttpExchange handle) throws IOException {
					handle.getResponseHeaders().add("Location", redirectUrl);
					handle.sendResponseHeaders(301, 0);
					handle.getResponseBody().close();
				}
			});
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
		} catch (IOException e) {
			getLogger().severe("An error occurred while starting the webserver, is port 80 free?");
			e.printStackTrace();
			getPluginLoader().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		if (server != null)
			server.stop(1);
	}
}*/