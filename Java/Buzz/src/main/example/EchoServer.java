import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * Simple EchoServer using Java-WebSocket API
 */
public class EchoServer extends WebSocketServer {

	public EchoServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	public EchoServer( InetSocketAddress address ) {
		super( address );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected" );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		System.out.println( conn + " Closed" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println( conn + ": " + message );
	}

	public static void main( String[] args ) throws InterruptedException , IOException {
		int port = 1564;
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		EchoServer s = new EchoServer( port );
		s.start();
		System.out.println( "EchoServer started on port: " + s.getPort() );

		while ( true ) {
		}
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
	}
}
