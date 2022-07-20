package ge.casatrade.tbcpay;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Gio on 2/25/17. For CasaTrade(C)
 */

public class PrefferenceManager {

    private static final String PREF_NAME = "ge.casatrade.tbcutil";
    private static final String KEY_PASSWD = "ge.casatrade.tbcutil.password";
    private static final String KEY_USERNAME = "ge.casatrade.tbcutil.username";

    public static void savePassword(Context context, String password)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PASSWD, password).apply();
    }
    public static String getPassword(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);

        String string = prefs.getString(KEY_PASSWD, null);

        return string;
    }

    public static void saveUserName(Context context, String userName)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USERNAME, userName).apply();
    }


    public static void clear(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static String getUserName(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);

        String userString = prefs.getString(KEY_USERNAME, null);

        return userString;
    }
}
