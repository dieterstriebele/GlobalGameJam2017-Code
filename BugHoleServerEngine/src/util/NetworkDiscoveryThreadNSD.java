package util;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/**
 * Created by adieling on 17.01.2017.
 */
public class NetworkDiscoveryThreadNSD implements Runnable {

    @Override
    public void run() {

        try {

            while (true) {

                // Create a JmDNS instance
                JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

                // Register a service
                ServiceInfo serviceInfo = ServiceInfo.create("_bughole._tcp.local.", "Bughole tcp streamer", 47111, "Our godlike great bughole server!");
                jmdns.registerService(serviceInfo);

                // Wait a bit
//                Thread.sleep(25000);

                // Unregister all services
//                jmdns.unregisterAllServices();

            }
        } catch (IOException ex) {
            Logger.Error(NetworkDiscoveryThreadNSD.class.getName(),ex);
        }

    }

    public static NetworkDiscoveryThreadNSD getInstance() {

        return DiscoveryThreadHolder.INSTANCE;
    }

    private static class DiscoveryThreadHolder {

        private static final NetworkDiscoveryThreadNSD INSTANCE = new NetworkDiscoveryThreadNSD();
    }

}
