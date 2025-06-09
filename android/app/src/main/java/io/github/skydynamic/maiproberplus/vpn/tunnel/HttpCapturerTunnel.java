package io.github.skydynamic.maiproberplus.vpn.tunnel;

import android.util.Log;

import androidx.annotation.NonNull;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Locale;

import io.github.skydynamic.maiproberplus.Application;
import io.github.skydynamic.maiproberplus.core.proxy.handle.InterceptHandler;

public class HttpCapturerTunnel extends Tunnel {
    private static final String TAG = "HttpCapturerTunnel";

    public HttpCapturerTunnel(InetSocketAddress serverAddress, Selector selector) throws Exception {
        super(serverAddress, selector);
    }

    public HttpCapturerTunnel(SocketChannel innerChannel, Selector selector) throws Exception {
        super(innerChannel, selector);
    }

    @Override
    protected void onConnected(ByteBuffer buffer) throws Exception {
        onTunnelEstablished();
    }

    @Override
    protected void beforeSend(ByteBuffer buffer) {
        String body = new String(buffer.array());
        if (!body.contains("HTTP")) return;

        // Extract http target from http packet
        String[] lines = body.split("\r\n");
        String url = getUrl(lines);
        Log.d(TAG, "HTTP url: " + url);

        // If it's a auth redirect request, catch it
        if (url.contains("tgk-wcaime.wahlap.com")) {
            Log.d(TAG, "Auth request caught!");
            InterceptHandler.onAuthHook(url, Application.application.configManager.getConfig());
        }
    }

    @NonNull
    private static String getUrl(String[] lines) {
        String path = lines[0].split(" ")[1];
        String host = "";
        for (String line : lines) {
            if (line.toLowerCase(Locale.ROOT).startsWith("host")) {
                host = line.substring(4);
                while (host.startsWith(":") || host.startsWith(" ")) {
                    host = host.substring(1);
                }
                while (host.endsWith("\n") || host.endsWith("\r") || host.endsWith(" ")) {
                    host = host.substring(0, host.length() - 1);
                }
            }
        }
        if (!path.startsWith("/")) path = "/" + path;

        return "http://" + host + path;
    }

    @Override
    protected void afterReceived(ByteBuffer buffer) {
    }

    @Override
    protected boolean isTunnelEstablished() {
        return true;
    }

    @Override
    protected void onDispose() {
        // TODO Auto-generated method stub
    }
}
