package ge.casatrade.tbcpay.Models;

import java.io.Serializable;

/**
 * Created by Gio on 2/26/17. For CasaTrade(C)
 */

public class BasicInfo implements Serializable{
    private String id;
    private String name;
    private String imei;
    private String serial;
    private String address;
    private String phone;
    private boolean isTerminalComplete;


    public BasicInfo(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public BasicInfo(String id, String name, String imei, boolean isTerminalComplete, String address)
    {
        this.id = id;
        this.name = name;
        this.imei = imei;
        this.isTerminalComplete = isTerminalComplete;
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getId() {
        return id;
    }

    public boolean isTerminalComplete() {
        return isTerminalComplete;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }
}
