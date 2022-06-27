package test;

import java.io.IOException;
import java.net.Socket;

public class Util {
    
    private Util()
    {

    }

    public static int getAnyPort() throws IOException
    {
        try (Socket socket = new Socket())
        {
            socket.bind(null);
            return socket.getLocalPort();
        }
    }
}
