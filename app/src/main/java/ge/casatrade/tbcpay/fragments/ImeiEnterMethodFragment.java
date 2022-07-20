package ge.casatrade.tbcpay.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import ge.casatrade.tbcpay.R;
import info.hoang8f.android.segmented.SegmentedGroup;


public class ImeiEnterMethodFragment extends Fragment {

    private OnShouldScanListener onShouldScanListener;
    private SegmentedGroup methodPicker;
    private boolean isImeiSelected = true;

    public ImeiEnterMethodFragment() {
        // Required empty public constructor
    }

    public static ImeiEnterMethodFragment newInstance() {
        final Bundle args = new Bundle();

        final ImeiEnterMethodFragment fragment = new ImeiEnterMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_choose_scan, container, false);
        methodPicker = view.findViewById(R.id.method_picker);
        methodPicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                isImeiSelected = (R.id.imei_radio == i);
            }
        });

        view.findViewById(R.id.btn_method_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onShouldScanListener != null)
                {
                    onShouldScanListener.onShouldScanClicked(true, isImeiSelected);
                }
            }
        });

        view.findViewById(R.id.btn_method_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onShouldScanListener != null)
                {
                    onShouldScanListener.onShouldScanClicked(false, isImeiSelected);
                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnShouldScanListener) {
            onShouldScanListener = (OnShouldScanListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnShouldScanListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnShouldScanListener {
        // TODO: Update argument type and name
        void onShouldScanClicked(boolean shouldScan, boolean isImeiSelected);
    }
}
