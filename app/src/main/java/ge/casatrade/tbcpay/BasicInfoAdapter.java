package ge.casatrade.tbcpay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ge.casatrade.tbcpay.Models.BasicInfo;


/**
 * Created by Gio on 12/20/16. For CasaTrade(C)
 */

public class BasicInfoAdapter extends ArrayAdapter<BasicInfo> {

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView imei;
        TextView company;
        TextView serial;
        ImageView completeImg;
    }

    private Context context;


    public BasicInfoAdapter(Context context, ArrayList<BasicInfo> users) {
        super(context, R.layout.item_basic_info, users);

        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        BasicInfo user = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_basic_info, parent, false);
            viewHolder.name = convertView.findViewById(R.id.txt_name);
            viewHolder.imei = convertView.findViewById(R.id.txt_imei);
            viewHolder.company = convertView.findViewById(R.id.txt_company);
            viewHolder.serial = convertView.findViewById(R.id.txt_serial);
            viewHolder.completeImg = convertView.findViewById(R.id.complete_img);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(String.format(Locale.getDefault(), "%s: %s", context.getString(R.string.name), user.getName()));
        if(user.getImei() != null) {
            viewHolder.imei.setVisibility(View.VISIBLE);
            viewHolder.imei.setText(String.format(Locale.getDefault(), "%s: %s", context.getString(R.string.imei), user.getImei()));
        }else{
            viewHolder.imei.setVisibility(View.GONE);
        }
        viewHolder.serial.setText(String.format(Locale.getDefault(), "%s: %s", context.getString(R.string.terminal_id), user.getId()));
        viewHolder.company.setText(String.format(Locale.getDefault(), "%s: %s", context.getString(R.string.address), user.getAddress()));

        if(user.isTerminalComplete()){
            viewHolder.completeImg.setVisibility(View.VISIBLE);
        }else{
            viewHolder.completeImg.setVisibility(View.GONE);
        }

//        viewHolder.company.setText(String.format(Locale.getDefault(), "%s:%s", context.getString(R.string.company), user.getCompany()));


        // Return the completed view to render on screen
        return convertView;
    }
}
