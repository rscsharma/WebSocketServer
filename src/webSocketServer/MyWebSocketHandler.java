/* Copyright 2016 Jack Henry Software */

package webSocketServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

/**
 * 
 */
/* @WebSocket */
public class MyWebSocketHandler implements WebSocketListener
{
	private Session outbound;
	private Socket socket;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len)
	{
		System.out.printf("Echoing back message [%s]%n", payload);
		// echo the message back
		//outbound.getRemote().sendString(message, null);
		try
		{
			// Sending to host
			dataOutputStream.write(payload);				
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason)
	{
		this.outbound = null;
		System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
	}

	@Override
	public void onWebSocketConnect(Session session)
	{
		this.outbound = session;
		System.out.println("Connect: " + session.getRemoteAddress().getAddress());
		String server = session.getUpgradeRequest().getHeader("Server");

		try
		{
			socket = new Socket(server, 23);
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());

			new Thread()
			{				
			    public void run() {
			        while(socket.isConnected())
			        {
			        	try
						{
			        		byte[] inputArray = new byte[8192];
							int byteCount = dataInputStream.read(inputArray);
							// Happens when user types Exit in Quest's terminal window
							if (byteCount < 0)
							{
								ByteBuffer buf = ByteBuffer.wrap(inputArray, 0, 0);
							
								if ((outbound != null) && (outbound.isOpen()))
								{
									outbound.getRemote().sendBytes(buf);
								}
								socket.close();
								break;
							}
							
							ByteBuffer buf = ByteBuffer.wrap(inputArray, 0, byteCount);
							
							if ((outbound != null) && (outbound.isOpen()))
							{
								outbound.getRemote().sendBytes(buf);
							}
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    }
			}.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onWebSocketError(Throwable cause)
	{
		System.out.println("Error: " + cause.getMessage());
	}

	@Override
	public void onWebSocketText(String message)
	{
	}
}
