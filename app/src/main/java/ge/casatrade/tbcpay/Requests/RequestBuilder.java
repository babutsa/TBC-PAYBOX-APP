package ge.casatrade.tbcpay.Requests;

import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import ge.casatrade.tbcpay.PrefferenceManager;


/**
 * Created by Gio on 2/26/17. For CasaTrade(C)
 */

public class RequestBuilder {

    private static String user;
    private static String passwd;

    private static String userName;

    public static void setUserName(String user){
        RequestBuilder.userName = user;
    }

    public static void setParams(String user, String passwd){
        RequestBuilder.user = user;
        RequestBuilder.passwd = passwd;
    }

    public static StringRequest basicPostRequestBuilder(final Map<String, String> params, final String url,
                                                        Response.Listener<String> responseListener,
                                                        Response.ErrorListener errorListener)
    {
        return new StringRequest(Request.Method.POST, url,
               responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> temp = new HashMap<>();
                if(params != null){
                    temp = params;
                }
                temp.put("username", RequestBuilder.userName);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();


                String credentials =  user + ":" + passwd;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", auth);


                return params;
            }
        };
    }


}
