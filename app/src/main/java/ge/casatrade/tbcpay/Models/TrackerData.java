package ge.casatrade.tbcpay.Models;

/**
 * Created by Giorgi Andriadze.
 */
public class TrackerData {
    private String imei;
    private String serial;
    private String phone;
    private boolean isComplete;

    public TrackerData(String imei, String serial, boolean isComplete, String phone) {
        this.imei = imei;
        this.serial = serial;
        this.isComplete = isComplete;
        this.phone = phone;
    }

    public String getImei() {
        return imei;
    }

    public String getSerial() {
        return serial;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public String getPhone() {
        return phone;
    }
}
