import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
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
import test.TestVibe;
import vibe.Vibe;

/**
 * Simple EchoServer using Java-WebSocket API
 */
public class EchoServer extends WebSocketServer {
    private static final String SERIALPORTNAME = "/dev/tty.usbserial-AD01UBSI";
    private static Vibe vibe;
    private static boolean on = false;

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
        
        if (EchoServer.on) {
            EchoServer.vibe.setState(0, 0, 0);
        }
        else {
            EchoServer.vibe.setState(0, 0, Integer.MAX_VALUE);
        }
        
        EchoServer.vibe.sendUpate();
        EchoServer.on = ! EchoServer.on;
        String rspv = EchoServer.vibe.getMessage();
        
		System.out.println( conn + ": " + message );
        System.out.println( "Arduino response: " + rspv);
        
	}

	public static void main( String[] args ) throws InterruptedException , IOException, IllegalArgumentException, PortInUseException {
        CommPortIdentifier portID = TestVibe.getSerialCommPortIDbyName(SERIALPORTNAME);
        if (portID == null) {
            System.err.println("Found no available port "+ SERIALPORTNAME);
            return;
        }
        
        EchoServer.vibe = new Vibe(portID);
        
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
