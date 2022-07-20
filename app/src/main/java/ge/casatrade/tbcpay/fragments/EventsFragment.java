package ge.casatrade.tbcpay.fragments;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ge.casatrade.tbcpay.Constants;
import ge.casatrade.tbcpay.Models.BasicInfo;
import ge.casatrade.tbcpay.Models.Category;
import ge.casatrade.tbcpay.Models.CmdButton;
import ge.casatrade.tbcpay.Models.LogData;
import ge.casatrade.tbcpay.R;
import ge.casatrade.tbcpay.Requests.Parser;
import ge.casatrade.tbcpay.Requests.RequestBuilder;
import ge.casatrade.tbcpay.WebBridge;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_INFO = "info";
    private static final String REQUEST_TAG = "event_get";
    private Spinner spinner;
    private ArrayAdapter spinnerAdapter;
    private String selectedCategory = "all";
    private ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    private BasicInfo selectedInfo;
    private ArrayList<Category> categories = new ArrayList<>();

    private TextView txtLog;
    private TextView txtStatus;
    private LinearLayout btnHolder;
    private ImageView imgSignal;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private String lastLogDate;
    private String lastId;
    private EventsFragmentListener listener;
    private ImageButton timerStartStopBtn;

    private String logData;
    private EditText timerText;
    private String originalTimerTxt = "60";

    private boolean isTimerRunning = false;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int val = Integer.parseInt(timerText.getText().toString());
            val++;
            timerText.setText(String.valueOf(val));
            timerHandler.postDelayed(this, 1000);

        }
    };

    private Handler newEventsHandler = new Handler();
    private Runnable newEventsRunnable = new Runnable() {

        @Override
        public void run() {
            getNewEvents();
        }
    };


    public EventsFragment() {
        // Required empty public constructor
    }


    public static EventsFragment newInstance(BasicInfo info) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INFO, info);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedInfo = (BasicInfo) getArguments().getSerializable(ARG_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        spinner = view.findViewById(R.id.categories_spinner);
        txtLog = view.findViewById(R.id.txt_log);
        btnHolder = view.findViewById(R.id.btn_holder);
        timerText = view.findViewById(R.id.txt_timer);
        imgSignal = view.findViewById(R.id.signal_strength);
        txtStatus = view.findViewById(R.id.txt_status);
        progressBar = view.findViewById(R.id.command_progress);
        ((TextView) view.findViewById(R.id.device_name)).setText(selectedInfo.getName());


        view.findViewById(R.id.next_btn).setVisibility(View.INVISIBLE);

        if (!selectedInfo.isTerminalComplete()) {
            view.findViewById(R.id.next_btn).setVisibility(View.VISIBLE);

            view.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onFinishClicked();
                }
            });
        }

        timerStartStopBtn = view.findViewById(R.id.timer_start);
        timerStartStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTimerRunning) {
                    stopTimer();
                } else {
                    startTimer();
                }
            }
        });

        view.findViewById(R.id.timer_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerText.setText("0");
            }
        });

        view.findViewById(R.id.clear_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logData = "";
                txtLog.setText(logData);
            }
        });


        txtLog.setMovementMethod(new ScrollingMovementMethod());

        spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = categories.get(i).getId();
                logData = "";
                txtLog.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lastLogDate = format.format(new Date());
        lastId = "0";
        getCategories();
        getCommands();

        return view;
    }

    private void stopTimer() {
        isTimerRunning = false;
        timerStartStopBtn.setImageResource(R.drawable.baseline_play_arrow_24);
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void startTimer() {
        isTimerRunning = true;
        originalTimerTxt = timerText.getText().toString();
        timerStartStopBtn.setImageResource(R.drawable.baseline_pause_24);
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EventsFragmentListener) {
            listener = (EventsFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement EventsFragmentListener.");
        }
    }

    private void getNewEvents() {
        if (getContext() == null) {

            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("terminalid", selectedInfo.getId());

        Log.i("TAG", "LAST DATE " + lastLogDate);
        params.put("from", lastLogDate);
        params.put("lastid", lastId);
        params.put("categoryid", selectedCategory);

        WebBridge.getInstance(getContext().getApplicationContext()).getRequestQueue().cancelAll(REQUEST_TAG);
        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_GET_EVENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Tag", "Events response " + response);
                        newEventsHandler.postDelayed(newEventsRunnable, 1100);

                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(logData);
                        try {
                            List<LogData> logs = Parser.getLogData(response);
                            for (LogData log : logs) {
                                String timeStamp = log.getTimestamp().split(" ")[1];

                                stringBuilder.append(timeStamp).append(": ").append(log.getMessage()).append("<br/>");
                            }

                            if (logs.size() > 0) {
                                //lastLogDate = logs.get(0).getTimestamp();
                                lastId = logs.get(0).getId();

                                //ToDo: Kill request or Check if activcity attached
                                txtStatus.setText(String.format("%s: %s", getString(R.string.status), logs.get(0).getStatus()));
                                int strengthImg = R.drawable.baseline_signal_cellular_0_bar_24;
                                switch (logs.get(0).getSignal()) {
                                    case 0:
                                        strengthImg = R.drawable.baseline_signal_cellular_0_bar_24;
                                        break;
                                    case 1:
                                        strengthImg = R.drawable.baseline_signal_cellular_1_bar_24;
                                        break;
                                    case 2:
                                        strengthImg = R.drawable.baseline_signal_cellular_2_bar_24;
                                        break;
                                    case 3:
                                        strengthImg = R.drawable.baseline_signal_cellular_3_bar_24;
                                        break;
                                    case 4:
                                        strengthImg = R.drawable.baseline_signal_cellular_4_bar_24;
                                        break;
                                }
                                imgSignal.setImageResource(strengthImg);

                                logData = stringBuilder.toString();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    txtLog.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    txtLog.setText(Html.fromHtml(stringBuilder.toString()));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//
//                        final int scrollAmount = txtLog.getLayout().getLineBottom(txtLog.getLineCount()) - txtLog.getHeight();
//                        // if there is no need to scroll, scrollAmount will be <=0
//                        if (scrollAmount > 0)
//                            txtLog.scrollTo(0, scrollAmount);
//                        else
//                            txtLog.scrollTo(0, 0);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        newEventsHandler.postDelayed(newEventsRunnable, 1000);
                    }
                });

        stringRequest.setTag(REQUEST_TAG);
        Context context = getActivity();
        if (context != null) {
            WebBridge.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }


    private void getCategories() {
        HashMap<String, String> params = new HashMap<>();
        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_GET_CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Tag", "response " + response);
                        categories.clear();
                        try {
                            categories.addAll(Parser.getCategories(response));
                            categories.add(0, new Category("all", getString(R.string.all)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spinnerAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        Context context = getActivity();
        if (context != null) {
            WebBridge.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private void getCommands() {
        HashMap<String, String> params = new HashMap<>();
        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_GET_COMMANDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Tag", "response " + response);
                        try {
                            addCmdButtons(Parser.getCmdButtons(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Context context = getActivity();
        if (context != null) {
            WebBridge.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private void sendCommand(String command) {
        HashMap<String, String> params = new HashMap<>();
        params.put("cmd", command);
        params.put("terminalid", selectedInfo.getId());
        progressBar.setVisibility(View.VISIBLE);
        btnHolder.setVisibility(View.INVISIBLE);
        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_EXECUTE_COMMAND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(progressBar != null){
                            progressBar.setVisibility(View.INVISIBLE);
                            btnHolder.setVisibility(View.VISIBLE);
                        }
                        Log.i("Tag", "response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").toLowerCase().equals("ok")) {
                                listener.commandSuccess();
                            } else {
                                listener.commandFailed();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.commandFailed();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(progressBar != null){
                            progressBar.setVisibility(View.INVISIBLE);
                            btnHolder.setVisibility(View.VISIBLE);
                        }
                        listener.commandFailed();
                    }
                });

        Context context = getActivity();
        if (context != null) {
            WebBridge.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private void addCmdButtons(List<CmdButton> cmdButtonList) {
        for (CmdButton cmdButton : cmdButtonList) {
            Button button = new Button(getContext());
            button.setText(cmdButton.getLabel());
            button.getBackground().setColorFilter(cmdButton.getColor(), PorterDuff.Mode.MULTIPLY);
            button.setTag(cmdButton.getCommand());
            button.setOnClickListener(this);
            btnHolder.addView(button);
        }
        btnHolder.requestLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        newEventsHandler.postDelayed(newEventsRunnable, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        newEventsHandler.removeCallbacks(newEventsRunnable);
    }

    @Override
    public void onClick(View view) {
        Log.i("TAG", "Button Cmd " + view.getTag());
        sendCommand((String) view.getTag());
    }

    public interface EventsFragmentListener {
        void onFinishClicked();

        void commandSuccess();

        void commandFailed();
    }

}
