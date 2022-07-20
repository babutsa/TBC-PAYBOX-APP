package ge.casatrade.tbcpay.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tapadoo.alerter.Alerter;

import ge.casatrade.tbcpay.Models.BasicInfo;
import ge.casatrade.tbcpay.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicInfoFragment extends Fragment {

    private OnBasicNextBtnClicked onBasicNextBtnClicked;
    private EditText phoneTxt;

    public BasicInfoFragment() {
        // Required empty public constructor
    }

    public static BasicInfoFragment newInstance(BasicInfo info) {
        final Bundle args = new Bundle();

        final BasicInfoFragment fragment = new BasicInfoFragment();
        args.putSerializable("info", info);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);

        TextView imeiTxt = view.findViewById(R.id.imei_value);
        final TextView nameTxt = view.findViewById(R.id.name_value);
        TextView terminalIdTxt = view.findViewById(R.id.terminal_id_value);
        TextView snTxt = view.findViewById(R.id.serial_value);
        phoneTxt = view.findViewById(R.id.value_phone);

        final BasicInfo info = (BasicInfo) getArguments().getSerializable("info");
        if(info != null) {
            imeiTxt.setText(info.getImei());
            nameTxt.setText(info.getName());
            terminalIdTxt.setText(info.getId());
            snTxt.setText(info.getSerial());
            if(phoneTxt != null) {
                phoneTxt.setText(info.getPhone());
            }


            (view.findViewById(R.id.upload_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(phoneTxt.getText().length() >= 9) {
                        if (onBasicNextBtnClicked != null) {
                            info.setPhone(phoneTxt.getText().toString());
                            onBasicNextBtnClicked.onBasicNextBtnClicked(info);
                        }
                    }else{
                        Alerter.create(getActivity()).setTitle(R.string.error).setText(R.string.enter_phone_to_continue).setDuration(1000 * 7).setBackgroundColor(R.color.bright_red).show();
                    }
                }
            });
        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnBasicNextBtnClicked) {
            onBasicNextBtnClicked = (OnBasicNextBtnClicked) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnBasicNextBtnClicked.");
        }
    }

    public interface OnBasicNextBtnClicked {
        // TODO: Update argument type and name
        void onBasicNextBtnClicked(BasicInfo lastInfo);
    }

}
