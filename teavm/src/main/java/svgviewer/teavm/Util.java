package svgviewer.teavm;

import java.util.HashMap;
import java.util.Map;

import org.teavm.jso.browser.Location;

public class Util {
    

    public static Map<String, String> getMapFromQuery(Location loc)
    {
        Map<String, String> out = new HashMap<>();
        String[] pair = loc.getSearch().replaceFirst("\\?", "").split("&");
        for(String p : pair)
        {
            String[] keyAndValue = p.split("=");
            out.put(keyAndValue[0], keyAndValue[1]);
        }

        return out;
    }
}
