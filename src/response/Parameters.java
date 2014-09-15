package response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Parameters {

    private int code = 200;
    private long length = 0;
    private String suffix = "";
    private String path;
    private String method;

    public Parameters(String requestStr) {
        int firstLine = requestStr.indexOf("\r");
        int spaceIdx = requestStr.indexOf(" ");
        int httpIdx = requestStr.indexOf("HTTP/1.");
        int queryIdx = requestStr.indexOf("?");
        if(firstLine != -1) {
            if(spaceIdx != -1 && spaceIdx <= firstLine)
                method = requestStr.substring(0, spaceIdx).trim();
            if(queryIdx != -1 && queryIdx < httpIdx) {
                path = requestStr.substring(spaceIdx, queryIdx).trim();
            } else {
                path = requestStr.substring(spaceIdx, httpIdx).trim();
            }
            try {
                path =  URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int suffixIdx = path.lastIndexOf(".");
            if (suffixIdx != -1) {
                suffix = path.substring(suffixIdx, path.length());
            }
            switch (method) {
                case "GET":
                case "HEAD":
                    code = 200;
                    break;
                default:
                    code = 405;
            }
        }
    }

    public String getPath() {
        return path;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getMethod() {
        return method;
    }


    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public String getHeader() {
        if(method != null) {
            String type = Type.get(suffix);
            if (type == null)
                type = "content/unknown";
            String header = "HTTP/1.1 " + code + " " + Code.getCode(code) + "\r\n" +
                    "Server: MyalkinServer\r\n" +
                    "Date: " + getTime() + "\r\n" +
                    "Content-Type: " + type + "\r\n" +
                    "Content-Length: " + length + "\r\n" +
                    "Connection: close\r\n\r\n";
            return header;
        }
        return null;
    }
}


