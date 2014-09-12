package response;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Response {


    private int code;
    private long length = 0;
    private String suffix = "";

    public void setLength(long length) {
        this.length = length;
    }

    public void setSuffix(String suffix) {
        if(suffix != null)
            this.suffix = suffix;
    }

    public String getCode() {
        if(code == 0)
            code = 200;
        return code + " " + Code.getCode(code);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public String getHeader() {
        String type = Type.get(suffix);
        if(type == null)
            type = "content/unknown";
        return  "HTTP/1.1 " + getCode() + "\r\n" +
                "Server: WebServer\r\n" +
                "Date: " + getTime() + "\r\n" +
                "Content-Type: " + type + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n\r\n";
    }
}
