

package webSocketServer;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.eclipse.jetty.util.ssl.SslContextFactory;



/**
 * 
 */
public class WebSocketTest
{
	private static int port = 8082;
	 public static void main(String[] args) throws Exception 
	 {
		 /*StdErrLog logger = new StdErrLog();
		  logger.setLevel(StdErrLog.LEVEL_ALL);
		 Log.setLog(logger);*/
	        Server server = new Server();
	        WebSocketHandler wsHandler = new WebSocketHandler() {
	            @Override
	            public void configure(WebSocketServletFactory factory) {
	                factory.register(MyWebSocketHandler.class);
	            }
	        };	        
   
	        HttpConfiguration http_config = new HttpConfiguration();
	        
	        //http_config.setSecureScheme("https");
	        http_config.setSecurePort(8443);
	        http_config.setOutputBufferSize(32768);

	        
	        SslContextFactory sslContextFactory = new SslContextFactory(true);

	        HttpConfiguration https_config = new HttpConfiguration(http_config);
	        https_config.addCustomizer(new SecureRequestCustomizer());

	        ServerConnector https = new ServerConnector(server,
	                new HttpConnectionFactory(http_config));
	        server.setConnectors(new Connector[] { https });
	        Handler param = new ParamHandler();

	        
	        HandlerList list = new HandlerList(); 
	        list.setHandlers(new Handler[] {param, wsHandler}); 
	        server.setHandler(list);


	        server.start();
	        server.join();
	    }
	 
	 public static class ParamHandler extends AbstractHandler
	    {
	        public void handle( String target,
	                            Request baseRequest,
	                            HttpServletRequest request,
	                            HttpServletResponse response ) throws IOException,
	                                                          ServletException
	        {
	        	if (target.equals("/targ"))
	        	{
	        		((Request)request).setHandled(true);
	        	}
	        	else
	        	{
	        		((Request)request).setHandled(false);
	        	}
	        }
	    }
}
