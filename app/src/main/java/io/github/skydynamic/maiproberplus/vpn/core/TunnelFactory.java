package io.github.skydynamic.maiproberplus.vpn.core;

import android.util.Log;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import io.github.skydynamic.maiproberplus.core.proxy.HttpRedirectServer;
import io.github.skydynamic.maiproberplus.vpn.tunnel.HttpCapturerTunnel;
import io.github.skydynamic.maiproberplus.vpn.tunnel.RawTunnel;
import io.github.skydynamic.maiproberplus.vpn.tunnel.Tunnel;

public class TunnelFactory {
    private final static String TAG = "TunnelFactory";

    public static Tunnel wrap(SocketChannel channel, Selector selector) throws Exception {
        return new RawTunnel(channel, selector);
    }

    public static Tunnel createTunnelByConfig(InetSocketAddress destAddress, Selector selector) throws Exception {
        Log.d(TAG, destAddress.getHostName() + ":" + destAddress.getPort());
        if (destAddress.getAddress() != null)
        {
            Log.d(TAG, destAddress.getAddress().toString());
        }
        if (destAddress.getHostName().endsWith("wahlap.com") && destAddress.getPort() == 80) {
                Log.d(TAG, "Request for wahlap.com caught");
                return new HttpCapturerTunnel(
                        new InetSocketAddress("127.0.0.1", HttpRedirectServer.Port), selector);
        } else if (destAddress.isUnresolved()) {
            return new RawTunnel(new InetSocketAddress(destAddress.getHostName(), destAddress.getPort()), selector);
        } else {
            return new RawTunnel(destAddress, selector);
        }
    }
}
