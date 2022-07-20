package ge.casatrade.tbcpay.Models;

/**
 * Created by Gio on 2/26/17. For CasaTrade(C)
 */

public class FullInfo {
    private String transmissionDate;
    private Float lat;
    private Float lng;
    private String sats;
    private String deg;
    private Boolean engine;
    private String batteryPower;
    private String mainPower;


    public FullInfo()
    {}

    public FullInfo(String transmissionDate, Float lat, Float lng, String sats, String deg, Boolean engine, String batteryPower, String mainPower)
    {
        this.transmissionDate = transmissionDate;
        this.lat = lat;

        this.lng = lng;
        this.sats = sats;
        this.deg = deg;
        this.engine = engine;
        this.batteryPower = batteryPower;
        this.mainPower = mainPower;
    }

    public String getMainPower() {
        return mainPower;
    }

    public void setMainPower(String mainPower) {
        this.mainPower = mainPower;
    }

    public String getBatteryPower() {
        return batteryPower;
    }

    public void setBatteryPower(String batteryPower) {
        this.batteryPower = batteryPower;
    }

    public Boolean getEngine() {
        return engine;
    }

    public void setEngine(Boolean engine) {
        this.engine = engine;
    }

    public String getDeg() {
        return deg;
    }

    public void setDeg(String deg) {
        this.deg = deg;
    }

    public String getSats() {
        return sats;
    }

    public void setSats(String sats) {
        this.sats = sats;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public String getTransmissionDate() {
        return transmissionDate;
    }

    public void setTransmissionDate(String transmissionDate) {
        this.transmissionDate = transmissionDate;
    }
}
