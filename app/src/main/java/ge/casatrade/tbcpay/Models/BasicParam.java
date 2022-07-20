package ge.casatrade.tbcpay.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gio on 7/21/17. For CasaTrade(C)
 */

public class BasicParam implements Parcelable {
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    private final String key;
    private final String value;

    public BasicParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    private BasicParam(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BasicParam> CREATOR = new Parcelable.Creator<BasicParam>() {
        @Override
        public BasicParam createFromParcel(Parcel in) {
            return new BasicParam(in);
        }

        @Override
        public BasicParam[] newArray(int size) {
            return new BasicParam[size];
        }
    };
}