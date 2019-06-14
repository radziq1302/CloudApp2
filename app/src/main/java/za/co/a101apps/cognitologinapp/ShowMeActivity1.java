package za.co.a101apps.cognitologinapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShowMeActivity1 extends AppCompatActivity {

    private static final String TAG = "Cognito";
    private static final int RESULT_LOAD_IMAGE = 1;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private Uri selectedImageUri;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private File sciecha;
    private ImageView imageV;
    private ImageView imageV2;
    private boolean downloadDone=false;
    private boolean firstIV=false;
    private int column_index;
    private String imagePath;
    private String chooseMe;

    private Context context;
    private String sc;
    private String LOG_TAG="Zdjecia_showme1";
    private List<String> zBazy;
    private List<String> zBazy69;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        setContentView(R.layout.activity_show_me);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sciecha = new File(Environment.getExternalStorageDirectory()+"/CloudFit/");
        if(!sciecha.exists()) sciecha.mkdir();

        imageV=(ImageView)findViewById(R.id.imageView);
        imageV2=(ImageView)findViewById(R.id.second_IV);
        context=getApplicationContext();
        //AWSMobileClient.getInstance().initialize(this).execute();
        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case GUEST:
                        Log.i("userState", "user is in guest mode");
                        break;
                    case SIGNED_OUT:
                        Log.i("userState", "user is signed out");
                        break;
                    case SIGNED_IN:
                        Log.i("userState", "user is signed in");
                        break;
                    case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                        Log.i("userState", "need to login again");
                        break;
                    case SIGNED_OUT_FEDERATED_TOKENS_INVALID:
                        Log.i("userState", "user logged in via federation, but currently needs new tokens");
                        break;
                    default:
                        Log.e("userState", "unsupported");
                }
            }
        });
        final CognitoSettings cognitoSettings = new CognitoSettings(this);

        /*Identity pool credentials provider*/
        Log.i(TAG, "getting Identity Pool credentials provider");
        credentialsProvider = cognitoSettings.getCredentialsProvider();
        /*get user - User Pool*/
        Log.i(TAG, "getting user Pool user");
        CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();

        final TextView temp = findViewById(R.id.tempol);
        Button buttonDownload = findViewById(R.id.buttonDownload);
        Button buttonChoose = findViewById(R.id.choose_btn);
        Button buttonUpload = findViewById(R.id.buttonUpload);
        buttonChoose.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i,RESULT_LOAD_IMAGE);
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed(1);
            }
        });
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider,Region.getRegion( Regions.US_EAST_2 ));
        String prefix =  credentialsProvider.getIdentityId()+"/1/";
        List<S3ObjectSummary> s3objects = s3Client.listObjects("s3-cloud2",prefix).getObjectSummaries();
        //s3objects.get(1).getKey();
        //s3objects.size();
        zBazy=new ArrayList<String>();
        zBazy69=new ArrayList<String>();
        Iterator <S3ObjectSummary> iter = s3objects.iterator();
        while(iter.hasNext()){
            zBazy.add(iter.next().getKey().substring(49));
        }
        Iterator <S3ObjectSummary> iter1 = s3objects.iterator();
        while(iter1.hasNext()){
            zBazy69.add(iter1.next().getKey());
        }
        Log.v("zbazy69", s3objects.size()+"");
        String[] zBazy1 = new String[zBazy.size()];
        zBazy1 = zBazy.toArray(zBazy1);

        final String[] zBazy2 = zBazy1;
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowMeActivity1.this);
                builder.setTitle("Zdjecia");
                builder.setItems(zBazy2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp.setText(zBazy2[which]);
                        downloadWithTransferUtility(zBazy69.get(which),"/"+which);
                        Log.v("CoPobiera", zBazy69.get(which));
/*
                        String sciezkaPliku=sciecha+"/"+String.valueOf(which)+".png";
                        Log.v("musiDzialac", sciezkaPliku);
                        Uri UriSciezkaPliku = Uri.parse(sciezkaPliku);
                        File fileDown = new File(UriSciezkaPliku.getPath());
                        //}

                        //us-east-2:88d2c292-f64c-4aca-a667-b284fa4ce053
                        imageV.setImageURI(UriSciezkaPliku);*/
                    }
                })
                ;
                builder.show();

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            selectedImageUri=data.getData();

            imageV.setImageURI(selectedImageUri);
            sc = getRealPathFromUri(context, selectedImageUri);

            Log.v("sciezka", sc);//selectedImageUri.getPath());

        }
    }

    private void proceed(final int action) {
        final CognitoSettings cognitoSettings = new CognitoSettings(this);

        /*Identity pool credentials provider*/
        Log.i(TAG, "getting Identity Pool credentials provider");
        credentialsProvider = cognitoSettings.getCredentialsProvider();
        /*get user - User Pool*/
        Log.i(TAG, "getting user Pool user");
        CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();

        Log.i(TAG, "calling getSessionInBackground....");
        currentUser.getSessionInBackground(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {

                if (userSession.isValid()) {
                    Log.i(TAG, "user session valid, getting token...");
                    // Get id token from CognitoUserSession.
                    String idToken = userSession.getIdToken().getJWTToken();
                    Log.v("idtoken", userSession.getIdToken().getJWTToken()+"");
                    if (idToken.length() > 0) {
                        // Set up as a credentials provider.
                        Log.i(TAG, "got id token - setting credentials using token");
                        Map<String, String> logins = new HashMap<>();
                        logins.put("cognito-idp.us-east-2.amazonaws.com/us-east-2_JbhhbQMsj", idToken);
                        credentialsProvider.setLogins(logins);

                        Log.i(TAG, "using credentials for the logged in user");

                        /*refresh provider off main thread*/
                        Log.i(TAG, "refreshing credentials provider in asynctask..");
                        new ShowMeActivity1.RefreshAsyncTask().execute(action);

                    } else {
                        Log.i(TAG, "no token...");
                    }
                } else {
                    Log.i(TAG, "user session not valid - using identity pool credentials - guest user");
                }


                uploadWithTransferUtility(sc);

            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                Log.i(TAG, " Not logged in! using identity pool credentials for guest user");

                uploadWithTransferUtility(sc);

            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
    }
    private class RefreshAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            Log.i(TAG, "in asynctask doInBackground()");
            credentialsProvider.refresh();
            return integers[0];
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Integer action) {
            Log.i(TAG, "in asynctask onPostExecute()");

            uploadWithTransferUtility(sc);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadWithTransferUtility(String path) {
        try {
            AWSMobileClient.getInstance().getTokens().getIdToken().getClaim("sub");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri path1 = Uri.parse(path);
        File file = new File(path1.getPath());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d(TAG, "READ_EXTERNAL_STORAGE permission not granted! Requesting...");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        Log.v("pliczek",path1.getPath()+" abc"+file.length() + " cbd"); //path.getLastPathSegment()

        TransferNetworkLossHandler.getInstance(context);
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider,Region.getRegion( Regions.US_EAST_2 ));

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();
        Log.v("credential", credentialsProvider.getIdentityId()+"");
        String getS3Key=credentialsProvider.getIdentityId()+"/1/"+file.getName(); //public/" +file.getName();
        //String S3Key=credentialsProvider.getIdentityId()+"/cognito/ad757aec-7bdc-427e-9e12-5cf6f24d9249/1/"+file.getName();
        Log.v("s3key", credentialsProvider.getIdentityId()+"");

        Log.v("filename", "filename"+file.length());
        Log.d(TAG, "Uploading file from " + file.getPath() + " to " + getS3Key);


        TransferObserver uploadObserver =
                transferUtility.upload("s3-cloud2",getS3Key, file);


        uploadObserver.setTransferListener(new TransferListener() {
                                               @Override
                                               public void onStateChanged(int id, TransferState state) {
                                                   if (TransferState.COMPLETED == state) {
                                                       Log.i(TAG, "Upload completed");
                                                       Toast.makeText(ShowMeActivity1.this, "Zdjęcie wysłane", Toast.LENGTH_SHORT).show();
                                                   }
                                               }

                                               @Override
                                               public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                                                   float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                                                   int percentDone = (int) percentDonef;

                                                   Log.i("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                                                           + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                                               }

                                               @Override
                                               public void onError(int id, Exception ex) {
                                                   Log.i(TAG, "upload error: " + ex.getLocalizedMessage());
                                               }
                                           }
        );

    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;


        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private void downloadWithTransferUtility(String key, String it) {
        final String it2=it;
        TransferNetworkLossHandler.getInstance(context);
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider,Region.getRegion( Regions.US_EAST_2 ));
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        TransferObserver downloadObserver =
                transferUtility.download("s3-cloud2",
                        key,
                        new File(sciecha+it+".png"));

        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    downloadDone=true;
                    String sciezkaPliku=sciecha+"/"+String.valueOf(it2)+".png";//1.png
                    Log.v("musiDzialac", sciezkaPliku);
                    Uri UriSciezkaPliku = Uri.parse(sciezkaPliku);
                    File fileDown = new File(UriSciezkaPliku.getPath());
                    if (!firstIV){
                    imageV.setImageURI(UriSciezkaPliku);
                    firstIV=true;}
                    else
                        imageV2.setImageURI(UriSciezkaPliku);
                }
                if(state == TransferState.CANCELED || state == TransferState.FAILED || state == TransferState.WAITING_FOR_NETWORK) {
                    Log.v("downloadObserver","No nie poszło");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d(LOG_TAG, "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == downloadObserver.getState()) {
            // Handle a completed upload.
            downloadDone=true;

        }

        Log.d(LOG_TAG, "Bytes Transferrred: " + downloadObserver.getBytesTransferred());
        Log.d(LOG_TAG, "Bytes Total: " + downloadObserver.getBytesTotal());
    }

}
