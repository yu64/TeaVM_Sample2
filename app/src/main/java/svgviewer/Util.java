package svgviewer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static String readResource(String path)
    {
        String lf = System.lineSeparator();
        try(BufferedReader br = Util.openResource(path))
        {
            return br.lines().collect(Collectors.joining(lf));
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static BufferedReader openResource(String path)
    {
        InputStream is = ClassLoader.getSystemResourceAsStream(path);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        return br;
    }

    public static InputStream openByteResource(String path)
    {
        InputStream is = ClassLoader.getSystemResourceAsStream(path);
        BufferedInputStream bis = new BufferedInputStream(is);
        return bis;
    }

    public static <O, I> O convart(I value, Function<I, O> fun, O alt)
    {
        if(value == null)
        {
            return alt;
        }

        O out = fun.apply(value);
        if(out == null)
        {
            return alt;
        }

        return out;
    }

    public static <O> O checkNull(O nullable, O alt)
    {
        return (nullable == null ? alt : nullable);
    }

    public static int updateCount(boolean canAdd, int now, int min, int max)
    {
        return Util.updateCount(canAdd, now, min, max, true);
    }

    public static int updateCount(boolean canAdd, int now, int min, int max, boolean canLoop)
    {
        //L < x < R
        int leftEnd = (canLoop ? max : min);
        int rightEnd = (canLoop ? min : max);

        now += (canAdd ? 1 : -1);
        now = (min > now ? leftEnd : now);
        now = (now > max ? rightEnd : now);

        return now;
    }

}
