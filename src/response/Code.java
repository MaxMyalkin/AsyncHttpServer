package response;

import java.util.HashMap;

public class Code {
    private static final HashMap<Integer, String> codes = new HashMap<>();

    static {
        codes.put(200, "OK");
        codes.put(403, "Forbidden");
        codes.put(404, "Not Found");
        codes.put(405, "Method Not Allowed");
    }

    public static String getCode(Integer code) {
        return codes.get(code);
    }
}
