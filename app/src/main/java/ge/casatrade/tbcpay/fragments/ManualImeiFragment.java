package ge.casatrade.tbcpay.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ge.casatrade.tbcpay.Constants;
import ge.casatrade.tbcpay.Models.BasicInfo;
import ge.casatrade.tbcpay.Models.TrackerData;
import ge.casatrade.tbcpay.R;
import ge.casatrade.tbcpay.Requests.Parser;
import ge.casatrade.tbcpay.Requests.RequestBuilder;
import ge.casatrade.tbcpay.TrackerDataAdapter;
import ge.casatrade.tbcpay.WebBridge;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManualImeiFragment extends Fragment {
    private static final String REQUEST_TAG = "imei_search";

    private String token;
    private String username;
    private Boolean isImei = true;
    private String lastImei = "";
    private TrackerDataAdapter adapter;
    private BasicInfo selectedBasicInfo;
    private ArrayList<TrackerData> imeis;
    private EditText searchView;
    private ManualImeiInteractivityFragment interactivityFragment;
    private ProgressBar progressBar;
    private String searchMethod = "imei";


    public ManualImeiFragment() {
        // Required empty public constructor
    }

    public static ManualImeiFragment newInstance(BasicInfo basicInfo, String searchMethod) {
        final Bundle args = new Bundle();
        args.putSerializable("info", basicInfo);
        args.putString("search", searchMethod);

        final ManualImeiFragment fragment = new ManualImeiFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manual_imei, container, false);
        view.findViewById(R.id.holder).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        selectedBasicInfo = (BasicInfo) getArguments().getSerializable("info");
        searchMethod = getArguments().getString("search");

        imeis = new ArrayList<>();
        adapter = new TrackerDataAdapter(getActivity(), imeis);

        ListView listView = view.findViewById(R.id.list_basic_infos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(imeis.get(i) != null && interactivityFragment != null)
                {
                    interactivityFragment.onManuallyFoundImeiClicked(imeis.get(i));
                }
            }
        });

        ((TextView) view.findViewById(R.id.txt_title)).setText(searchMethod.equals("imei") ? R.string.enter_device_imei : R.string.enter_device_sn);
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.edit_text_imei);
        searchView.setBackgroundColor(Color.WHITE);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastImei = charSequence.toString();
                imeis.clear();
                adapter.notifyDataSetChanged();

                int len = searchMethod.equals("imei") ? 13 : 9;

                if (charSequence.length() > len) {
                    getBasicInfo(lastImei);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    public void onPause()
    {
        super.onPause();

        searchView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ManualImeiInteractivityFragment) {
            interactivityFragment = (ManualImeiInteractivityFragment) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement ManualImeiInteractivityFragment.");
        }
    }

    private void getBasicInfo(final String id) {
        WebBridge.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(REQUEST_TAG);

        Map<String, String> params = new HashMap<>();
        params.put("term", id);
        params.put("method", this.searchMethod);

        progressBar.setVisibility(View.VISIBLE);
        Log.i("TAG", "AT " + Constants.URL_GET_TERMINALS_BY_IMEI);
        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_GET_TERMINALS_BY_IMEI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        Log.i("Tag", "response " + response);
                        try {
                            imeis.clear();
                            imeis.addAll(Parser.getImeis(response));
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
        stringRequest.setTag(REQUEST_TAG);
        WebBridge.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public interface ManualImeiInteractivityFragment
    {
        void onManuallyFoundImeiClicked(TrackerData imei);
    }


}
