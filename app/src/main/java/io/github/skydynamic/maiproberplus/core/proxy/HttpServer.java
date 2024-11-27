package io.github.skydynamic.maiproberplus.core.proxy;

import android.util.Log;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import io.github.skydynamic.maiproberplus.GlobalViewModel;
import io.github.skydynamic.maiproberplus.core.utils.WechatRequestUtil;


public class HttpServer extends NanoHTTPD {
    public static int Port = 8284;
    private final static String TAG = "HttpServer";

    protected HttpServer() throws IOException {
        super(Port);
    }

    @Override
    public void start() throws IOException {
        super.start();
        Log.d(TAG, "Http server running on http://localhost:" + Port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.d(TAG, "Serve request: " + session.getUri());
        if (session.getUri().equals("/auth/maimai")) {
            if (GlobalViewModel.INSTANCE.getMaimaiHooking()) {
                return onHooking();
            }
            return redirectToWechatAuthUrl(session, "maimai-dx");
        } else if (session.getUri().equals("/auth/chunithm")) {
            if (GlobalViewModel.INSTANCE.getChuniHooking()) {
                return onHooking();
            }
            return redirectToWechatAuthUrl(session, "chunithm");
        } else if(session.getUri().equals("/0")){
            return redirectToAuthUrlWithRandomParm(session, "maimai");
        } else if(session.getUri().equals("/1")) {
            return redirectToAuthUrlWithRandomParm(session, "chunithm");
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, "");
    }

    private Response onHooking() {
        return newFixedLengthResponse(
                        Response.Status.ACCEPTED, MIME_HTML,
                        "<html><body><h1>查分进程已开始，请耐心等待</h1></body></html>"
                );
    }

    // To avoid fu***ing cache of wechat webview client
    private Response redirectToAuthUrlWithRandomParm(IHTTPSession session, String gameType) {
        Response r = newFixedLengthResponse(Response.Status.REDIRECT, MIME_HTML, "");
        r.addHeader("Location", "http://" + "127.0.0.1:8284" + "/auth/" + gameType + "?random=" + System.currentTimeMillis());
        return r;
    }

    private Response redirectToWechatAuthUrl(IHTTPSession session, String gameType) {
        String url = WechatRequestUtil.getAuthUrl(gameType);
        Log.d(TAG, url);

        Response r = newFixedLengthResponse(Response.Status.REDIRECT, MIME_HTML, "");
        r.addHeader("Location", url);
        r.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        r.addHeader("Pragma", "no-cache");
        r.addHeader("Expires", "0");
        return r;
    }
}
