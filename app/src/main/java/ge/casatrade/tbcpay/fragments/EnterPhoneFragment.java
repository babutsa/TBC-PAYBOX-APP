package ge.casatrade.tbcpay.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ge.casatrade.tbcpay.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnterPhoneFragment.OnEnterPhoneFragment} interface
 * to handle interaction events.
 */
public class EnterPhoneFragment extends Fragment {

    private OnEnterPhoneFragment mListener;
    private EditText phoneText;

    public EnterPhoneFragment() {
        // Required empty public constructor
    }

    public static EnterPhoneFragment newInstance() {
        final Bundle args = new Bundle();

        final EnterPhoneFragment fragment = new EnterPhoneFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_phone, container, false);
        phoneText = view.findViewById(R.id.txt_phone);

        view.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPhoneEntered(phoneText.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEnterPhoneFragment) {
            mListener = (OnEnterPhoneFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnEnterPhoneFragment {
        // TODO: Update argument type and name
        void onPhoneEntered(String phone);
    }
}
