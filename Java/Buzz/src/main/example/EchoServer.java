import data.Vibrator;
import div.FindComPort;
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
        int mod = 0, vib = 0, amp = 0, dur = 1000;
        int ivl = Vibrator.INTERVAL_NOREPEAT;
        char typ = 'S';
        
        // Attempt to parse the message string
        try {
            String msg = message.substring(0, message.length()-2);
            String[] parameters = msg.split(":");
            for (String parameter : parameters) {
                String[] strings = parameter.split("=");
                
                // ID
                if (strings[0].equals("id")) {
                    vib = Integer.parseInt(strings[1]) - 1;
                }
                // Amplitude
                else if (strings[0].equals("mag")) {
                    amp = Integer.parseInt(strings[1]);
                }
                else if (strings[0].equals("mode")) {
                    if (strings[1].equals("triangle")) {
                        typ = 'T';
                    } else if (strings[1].equals("sine")) {
                        typ = 'G';
                    } else if (strings[1].equals("square")) {
                        typ = 'S';
                        dur = 1;
                        ivl = 1;
                    } 
                }
            }
            
        } catch (Exception e) {
            System.err.println("Failed parse! Message: "+message);
            e.printStackTrace(System.err);
        }
        
        EchoServer.vibe.setState(mod, vib, amp, 1, 0, typ);
        
        
        EchoServer.vibe.forceUpdate();
        EchoServer.on = ! EchoServer.on;
        String rspv = EchoServer.vibe.readMessage();
        
		System.out.println( conn + ": " + message );
        System.out.println( "Arduino response: " + rspv);
        
	}

	public static void main( String[] args ) throws InterruptedException , IOException, IllegalArgumentException, PortInUseException {
        CommPortIdentifier portID = FindComPort.getSerialCommPortIDbyName(SERIALPORTNAME);
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
