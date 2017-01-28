package ggj2k15.bughole.bugholegraphicsengine.util;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;

import java.nio.ByteBuffer;

import ggj2k15.bughole.bugholegraphicsengine.Settings;

/**
 * Created by adieling on 20.01.17.
 */

public class ServiceLocateSubnetScan {

    public static void locateServerByBroadcast()
    {
        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            //c.setBroadcast(true);

            byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 60666);
                c.send(sendPacket);
                System.out.println(">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    for (int ce = 0; ce < 255; ce++) {
                        for (int d = 0; d < 255; d++) {

                            // Send the broadcast package!
                            try {
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 30333);
                                c.send(sendPacket);

                            } catch (Exception e) {
                            }

                            System.out.println(">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());

                            byte[] bytes = broadcast.getAddress();
                            bytes[3]--;
                            broadcast = InetAddress.getByAddress(bytes);

                        }
                        byte[] bytes = broadcast.getAddress();
                        bytes[2]--;
                        broadcast = InetAddress.getByAddress(bytes);
                    }
                }
           }

            System.out.println( ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            System.out.println( ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
                //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
//                Controller_Base.setServerIp(receivePacket.getAddress());
                Settings.Ip = receivePacket.getAddress().getHostAddress();
                Log.d(">>>> Foud IP Address: ", Settings.Ip);
            }

            //Close the port!
            c.close();
        } catch (IOException ex) {
//            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}