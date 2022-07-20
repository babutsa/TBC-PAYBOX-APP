package ge.casatrade.tbcpay.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tapadoo.alerter.Alerter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ge.casatrade.tbcpay.Constants;
import ge.casatrade.tbcpay.PrefferenceManager;
import ge.casatrade.tbcpay.R;
import ge.casatrade.tbcpay.Requests.RequestBuilder;
import ge.casatrade.tbcpay.WebBridge;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */



public class CameraFragment extends Fragment {
    private final String TAG = "CameraFragment";


    private static final int START_CAMERA = 1000;
    private ImageView previewImage;
    private String mCurrentPhotoPath = "";
    private String lastImageName;
    private String imei;

    private Button uploadButton;
    private Button continueButton;

    private String uploadedPicNames = "";
    private ProgressDialog progressDialog;
    private CameraFragmentListener cameraFragmentListener;

    public CameraFragment() {
        // Required empty public constructor
    }


    public static CameraFragment newInstance(String imei) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString("imei", imei);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imei = getArguments().getString("imei");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("ატვირთვა...");
    }

    private void postImage(String base64Image, final String fileName) {

        Map<String, String> params = new HashMap<>();
        String token = PrefferenceManager.getPassword(getActivity());
        String username = PrefferenceManager.getUserName(getActivity());

        params.put("username", username);
        params.put("token", token);
        params.put("img_string", base64Image);
        params.put("name", fileName);

        StringRequest stringRequest = RequestBuilder.basicPostRequestBuilder(params, Constants.URL_POST_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Upload", "Result " + response);

                        progressDialog.cancel();
                        if(uploadedPicNames.length() < 1)
                        {
                            uploadedPicNames += fileName;
                        }else{
                            uploadedPicNames += ";" + fileName;
                        }

                        Alerter.create(getActivity()).setTitle(R.string.image_uploaded).setText(R.string.image_uploaded_successfully).setDuration(1500).setBackgroundColor(R.color.alerter_default_success_background).show();

                        mCurrentPhotoPath = "";
                        previewImage.setImageBitmap(null);
                        uploadButton.setVisibility(View.GONE);
                        cameraFragmentListener.onUploadsFinished(uploadedPicNames);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Alerter.create(getActivity()).setTitle(R.string.error).setText(R.string.could_not_upload).setDuration(1500).setBackgroundColor(R.color.bright_red).show();
                        continueButton.setVisibility(View.VISIBLE);
                        progressDialog.cancel();
                        error.printStackTrace();
                    }
                });

        stringRequest.setTag(TAG);
        WebBridge.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


    @Override
    public void onStop()
    {
        super.onStop();
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
        WebBridge.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(TAG);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        uploadedPicNames = "";
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        previewImage = view.findViewById(R.id.pic_preview);
        continueButton = view.findViewById(R.id.continue_btn);
        continueButton.setVisibility(View.VISIBLE);


        view.findViewById(R.id.take_pic_btn).setVisibility(View.GONE);
        view.findViewById(R.id.take_pic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        view.findViewById(R.id.continue_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentPhotoPath.length() != 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.image_not_uploaded)
                            .setMessage(R.string.skip_sending_image)
                            .setPositiveButton(R.string.skip, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (cameraFragmentListener != null) {
                                        cameraFragmentListener.onUploadsFinished(uploadedPicNames);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.upload, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                    new CompressImageTask().execute(mCurrentPhotoPath);

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }else {
                    if (cameraFragmentListener != null) {
                        cameraFragmentListener.onUploadsFinished(uploadedPicNames);
                    }
                }
            }
        });
        uploadButton = view.findViewById(R.id.upload_btn);
        uploadButton.setVisibility(View.GONE);
        view.findViewById(R.id.upload_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentPhotoPath.length() != 0) {
                    new CompressImageTask().execute(mCurrentPhotoPath);
                }
            }
        });

        //dispatchTakePictureIntent();

        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "ge.casatrade.casautility.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, START_CAMERA);
            }
        }
    }




    //called after camera intent finished
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == START_CAMERA) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                previewImage.setImageBitmap(bitmap);

                if(mCurrentPhotoPath.length() != 0) {
                    new CompressImageTask().execute(mCurrentPhotoPath);
                }

            }else{
                if(cameraFragmentListener != null)
                {
                    cameraFragmentListener.onCameraCanceled();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String imageFileName = imei + "_" + timeStamp;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        lastImageName = imageFileName + ".jpg";
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CameraFragmentListener) {
            cameraFragmentListener = (CameraFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CameraFragmentListener");
        }
    }

    private class CompressImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String path = params[0];

            Bitmap bitmap = BitmapFactory.decodeFile(path);


            bitmap = resize(bitmap, 1920, 1080);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
            return encoded;
        }

        @Override
        protected void onPostExecute(String res) {
            // might want to change "executed" for the returned string passed

            postImage(res, lastImageName);
        }

        private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
            if(image == null)
            {
                return null;
            }

            if (maxHeight > 0 && maxWidth > 0) {
                int width = image.getWidth();
                int height = image.getHeight();
                float ratioBitmap = (float) width / (float) height;
                float ratioMax = (float) maxWidth / (float) maxHeight;

                int finalWidth = maxWidth;
                int finalHeight = maxHeight;
                if (ratioMax > 1) {
                    finalWidth = (int) ((float)maxHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float)maxWidth / ratioBitmap);
                }
                image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
                return image;
            } else {
                return image;
            }
        }

    }

    public interface CameraFragmentListener {
        void onUploadsFinished(String uploadedNames);
        void onCameraCanceled();
    }
}
