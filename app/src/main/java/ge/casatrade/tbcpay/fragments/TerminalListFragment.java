package ge.casatrade.tbcpay.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ge.casatrade.tbcpay.BasicInfoAdapter;
import ge.casatrade.tbcpay.Constants;
import ge.casatrade.tbcpay.Models.BasicInfo;
import ge.casatrade.tbcpay.PrefferenceManager;
import ge.casatrade.tbcpay.R;
import ge.casatrade.tbcpay.Requests.Parser;
import ge.casatrade.tbcpay.Requests.RequestBuilder;
import ge.casatrade.tbcpay.WebBridge;


/**
 * A simple {@link Fragment} subclass.
 */
public class TerminalListFragment extends Fragment {
    private static final String REQUEST_TAG = "terminal_search";

    private String token;
    private String username;
    private Boolean isImei = true;
    private String lastImei = "";
    private BasicInfoAdapter adapter;
    private ArrayList<BasicInfo> basicInfos;
    private EditText searchView;
    private TerminalListListener interactivityFragment;
    private ProgressBar progressBar;

    public TerminalListFragment() {
        // Required empty public constructor
    }

    public static TerminalListFragment newInstance() {
        final Bundle args = new Bundle();

        final TerminalListFragment fragment = new TerminalListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_terminal_list, container, false);
        this.token = getArguments().getString("token");
        this.username = getArguments().getString("usr");
        this.isImei = getArguments().getBoolean("isImei");


        basicInfos = new ArrayList<>();
        adapter = new BasicInfoAdapter(getActivity(), basicInfos);


        ((TextView) view.findViewById(R.id.txt_title)).setText(R.string.terminal_id);

        ListView listView = view.findViewById(R.id.list_basic_infos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (basicInfos.get(i) != null && interactivityFragment != null) {
                    interactivityFragment.onTerminalPicked(basicInfos.get(i));
                }
            }
        });

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
                basicInfos.clear();
                adapter.notifyDataSetChanged();

                if (charSequence.length() > 0) {
                    getBasicInfo(lastImei);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    public void onPause() {
        super.onPause();

        searchView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TerminalListListener) {
            interactivityFragment = (TerminalListListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement ManualImeiInteractivityFragment.");
        }
    }

    private void getBasicInfo(final String id) {
        WebBridge.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(REQUEST_TAG);

        Map<String, String> params = new HashMap<>();
        params.put("term", id);

        progressBar.setVisibility(View.VISIBLE);
        Log.i("TAG", "AT " + Constants.URL_GET_TERMINALS);
        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_GET_TERMINALS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        Log.i("Tag", "response " + response);
                        try {
                            basicInfos.clear();
                            basicInfos.addAll(Parser.getBasicInfo(response));
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


    public interface TerminalListListener {
        void onTerminalPicked(BasicInfo info);
    }


}
