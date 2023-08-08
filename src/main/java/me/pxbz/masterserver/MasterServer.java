package me.pxbz.masterserver;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "masterserver",
        name = "MasterServer",
        version = "1.0",
        description = "Velocity plugin for network server's Proxy server, central plugin for the network's cross-server communication system",
        authors = {"pxbz"}
)
public class MasterServer {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        CentralServer server = new CentralServer(6969, logger);
        server.start();
        SQLManager.connect();
    }

    @Inject
    private ProxyServer server;
}
