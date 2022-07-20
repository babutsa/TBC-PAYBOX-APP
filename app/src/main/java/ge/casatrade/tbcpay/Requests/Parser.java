package ge.casatrade.tbcpay.Requests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ge.casatrade.tbcpay.Models.BasicInfo;
import ge.casatrade.tbcpay.Models.Category;
import ge.casatrade.tbcpay.Models.CmdButton;
import ge.casatrade.tbcpay.Models.FullInfo;
import ge.casatrade.tbcpay.Models.LogData;
import ge.casatrade.tbcpay.Models.TrackerData;


/**
 * Created by Gio on 2/26/17. For CasaTrade(C)
 */

public class Parser {

    public static ArrayList<BasicInfo> getBasicInfo(String json) throws JSONException {
//        JSONObject object = new JSONObject(json);
//        String status = object.getString("status");
//        if(!status.equals("OK")){
//            throw new JSONException("Imei must have been wrong");
//        }

        ArrayList<BasicInfo> infoArrayList = new ArrayList<>();
        JSONArray infos = new JSONArray(json);
        for (int i = 0; i < infos.length(); i++) {
            JSONObject basicInfo = infos.getJSONObject(i);
            BasicInfo finalInfo = new BasicInfo(basicInfo.getString("id"), basicInfo.getString("name"),
                    basicInfo.getString("deviceid"), basicInfo.getBoolean("iscomplete"), basicInfo.getString("address"));
            infoArrayList.add(finalInfo);
        }

        return infoArrayList;
    }

    public static ArrayList<TrackerData> getImeis(String json) throws JSONException {
//        JSONObject object = new JSONObject(json);
//        String status = object.getString("status");
//        if(!status.equals("OK")){
//            throw new JSONException("Imei must have been wrong");
//        }

        ArrayList<TrackerData> infoArrayList = new ArrayList<>();
        JSONArray infos = new JSONArray(json);
        for (int i = 0; i < infos.length(); i++) {
            JSONObject basicInfo = infos.getJSONObject(i);
            infoArrayList.add(new TrackerData(basicInfo.getString("imei"), basicInfo.getString("serialnumber"), basicInfo.getBoolean("iscomplete"), basicInfo.getString("phone")));
        }

        return infoArrayList;
    }

    public static List<Category> getCategories(String json) throws JSONException {
        JSONArray categories = new JSONArray(json);
        List<Category> categoryList = new ArrayList<>();


        for (int i = 0; i < categories.length(); i++) {
            JSONObject category = categories.getJSONObject(i);
            categoryList.add(new Category(category.getString("id"), category.getString("label")));
        }


        return categoryList;
    }

    public static List<LogData> getLogData(String json) throws JSONException {
        JSONArray categories = new JSONArray(json);
        List<LogData> categoryList = new ArrayList<>();


        for (int i = 0; i < categories.length(); i++) {
            JSONObject logData = categories.getJSONObject(i);
            categoryList.add(new LogData(logData.getString("time"), logData.getString("msg"), logData.getInt("gsm"), logData.getString("status"), logData.getString("id")));
        }


        return categoryList;
    }

    public static List<CmdButton> getCmdButtons(String json) throws JSONException {
        JSONArray categories = new JSONArray(json);
        List<CmdButton> cmdButtons = new ArrayList<>();


        for (int i = 0; i < categories.length(); i++) {
            JSONObject logData = categories.getJSONObject(i);
            cmdButtons.add(new CmdButton(logData.getString("cmd"), logData.getString("label"), logData.getString("color")));
        }


        return cmdButtons;
    }

    public static Boolean checkUpload(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        String status = object.getString("status");
        if (status.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<String> getCompanies(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        String status = object.getString("status");
        if (!status.equals("OK")) {
            throw new JSONException("Imei must have been wrong");
        }

        ArrayList<String> infoArrayList = new ArrayList<>();

        JSONArray infos = object.getJSONArray("data");
        for (int i = 0; i < infos.length(); i++) {
            String controllerName = infos.getString(i);
            infoArrayList.add(controllerName);
        }

        return infoArrayList;
    }

    public static ArrayList<String> getControllers(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        String status = object.getString("status");
        if (!status.equals("OK")) {
            throw new JSONException("Imei must have been wrong");
        }

        ArrayList<String> infoArrayList = new ArrayList<>();
        JSONArray infos = object.getJSONArray("data");
        for (int i = 0; i < infos.length(); i++) {
            String controllerName = infos.getString(i);
            infoArrayList.add(controllerName);
        }

        return infoArrayList;
    }


}
