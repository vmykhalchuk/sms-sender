package org.intrepidus.smsSender;

import java.util.Map;
import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;

import android.util.Log;

public class HTTPServer extends NanoHTTPD {
    private static final String TAG = "org.intrepidus.smsSender.HTTPServer";

    public HTTPServer() throws IOException {
        super("0.0.0.0", 8080);
        start();
        Log.i( TAG, "\nRunning! Point your browers to http://localhost:8080/ \n" );
    }

    public static void main(String[] args) {
        try {
            new HTTPServer();
        }
        catch( IOException ioe ) {
            Log.e( TAG, "Couldn't start server:\n" + ioe );
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
    	String uri = session.getUri();
    	String newUri = uri;
        Log.i( TAG, "Request at uri: '" + uri + "'");
        
        if(!uri.endsWith("/")) {
        	newUri = uri + "/";
        	return newFixedLengthResponse(
        			Response.Status.REDIRECT,
        			NanoHTTPD.MIME_HTML,
        			"<html><head></head><meta http-equiv=\"refresh\" content=\"0; url=" + newUri + "\" /><body>Redirected: <a href=\"" + newUri + "\">" + newUri + "</a></body></html>"
        			);
        }
    	
    	// no switch on strings for java 1.6 which unfortunately we have to use
    	
    	if(uri.equals("/")) {
    		return serveHome(session);
    	} else if(uri.equals("/sms/send/")) {
    		return serveSendSMS(session);
    	}
    	
    	return serve404(session);
    }
    
    public Response serveHome(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();

        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse( msg + "</body></html>\n" );
    }
    
    public Response serveSendSMS(IHTTPSession session) {
    	return newFixedLengthResponse("<html><body>SMS Sent</body></html>");
    }
    
    public Response serve404(IHTTPSession session) {
    	return newFixedLengthResponse("<html><body><h1>404</h1><h3>Page not found</h3></body></html>");
    }
}
