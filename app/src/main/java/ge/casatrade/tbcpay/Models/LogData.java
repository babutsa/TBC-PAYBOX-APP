package ge.casatrade.tbcpay.Models;

/**
 * Created by Giorgi Andriadze.
 */
public class LogData {
    private String id;
    private String timestamp;
    private String message;
    private int signal;
    private String status;

    public LogData(String timestamp, String message, int signal, String status, String id) {
        this.timestamp = timestamp;
        this.message = message;
        this.signal = signal;
        this.status = status;
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSignal() {
        return signal;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
