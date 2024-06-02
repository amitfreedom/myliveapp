package com.stream.prettylive.ui.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.stream.prettylive.R;
import com.stream.prettylive.databinding.ActivityMainBinding;
import com.stream.prettylive.global.AppConstants;
import com.stream.prettylive.global.ApplicationClass;
import com.stream.prettylive.notification.FCMNotificationSender;
import com.stream.prettylive.streaming.activity.CallWaitActivity;
import com.stream.prettylive.streaming.activity.LiveAudioRoomActivity;
import com.stream.prettylive.streaming.activity.LiveStreamingActivity;
import com.stream.prettylive.streaming.internal.ZEGOLiveStreamingManager;
import com.stream.prettylive.streaming.internal.ZEGOCallInvitationManager;
import com.stream.prettylive.streaming.internal.business.call.CallExtendedData;
import com.stream.prettylive.streaming.internal.business.call.FullCallInfo;
import com.stream.prettylive.streaming.internal.sdk.ZEGOSDKManager;
import com.stream.prettylive.streaming.internal.sdk.basic.ZEGOSDKUser;
import com.stream.prettylive.ui.home.HomeActivity;
import com.stream.prettylive.ui.home.ui.profile.models.UserDetailsModel;
import com.stream.prettylive.ui.utill.Constant;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zim.callback.ZIMCallInvitationSentCallback;
import im.zego.zim.entity.ZIMCallInvitationSentInfo;
import im.zego.zim.entity.ZIMError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity123";
    private ActivityMainBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private CollectionReference usersRef;
    private UserDetailsModel userDetails;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications permission granted",Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "FCM can't post notifications without POST_NOTIFICATIONS permission",
                            Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        usersRef = firestore.collection(Constant.LOGIN_DETAILS);


        enablePermission();



        try {
            ZEGOSDKUser localUser = ZEGOSDKManager.getInstance().expressService.getCurrentUser();
            if (localUser!=null){
                binding.liveUserinfoUserid.setText(localUser.userID);
                binding.liveUserinfoUsername.setText(localUser.userName);
            }
        }catch (Exception e){

        }


        binding.startLiveStreaming.setOnClickListener(v -> {
            List<String> permissions = Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
            requestPermissionIfNeeded(permissions, new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                     @NonNull List<String> deniedList) {
                    if (allGranted) {
                        if (getSaltString(userDetails.getUserId()) != null) {
                        Intent intent = new Intent(MainActivity.this, LiveStreamingActivity.class);
                        intent.putExtra("host", true);
                        intent.putExtra("liveID", getSaltString(userDetails.getUserId()));
                        intent.putExtra("userId", userDetails.getUserId());
                        intent.putExtra("username", userDetails.getUsername());
                        intent.putExtra("uid", userDetails.getUid());
                        intent.putExtra("country_name", userDetails.getCountry_name());
                        intent.putExtra("image", userDetails.getImage());
                        intent.putExtra("level", userDetails.getLevel());
                        startActivity(intent);
                        }
                    }
                }
            });
        });



        binding.pipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                enterPIPMode();
            }
        });



        binding.startLiveAudioroom.setOnClickListener(v -> {
//            String liveID = binding.liveIdAudioRoom.getEditText().getText().toString();
//            if (TextUtils.isEmpty(liveID)) {
//                binding.liveIdAudioRoom.setError("please input liveID");
//                return;
//            }
            List<String> permissions = Arrays.asList(Manifest.permission.RECORD_AUDIO);
            requestPermissionIfNeeded(permissions, new RequestCallback() {
                @Override
                public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                     @NonNull List<String> deniedList) {
                    if (allGranted) {
                        if (getSaltString(userDetails.getUserId()) != null){
                            Intent intent = new Intent(MainActivity.this, LiveAudioRoomActivity.class);
                            intent.putExtra("host", true);
                            intent.putExtra("liveID", getSaltString(userDetails.getUserId()));
                            intent.putExtra("userId", userDetails.getUserId());
                            intent.putExtra("username", userDetails.getUsername());
                            intent.putExtra("uid", userDetails.getUid());
                            intent.putExtra("country_name", userDetails.getCountry_name());
                            intent.putExtra("image", userDetails.getImage());
                            intent.putExtra("level", userDetails.getLevel());
                            startActivity(intent);
                        }
                    }
                }
            });
        });


        // if LiveStreaming,init after user login,may receive pk request.
        ZEGOLiveStreamingManager.getInstance().init();
        // if Call invitation,init after user login,may receive call request.
//        ZEGOCallInvitationManager.getInstance().init();
//        Intent intent = new Intent(this, CallBackgroundService.class);
//        startService(intent);

//        fetchUserDetails(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
        fetchUserDetails(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));




//        binding.subscribeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String deviceToken = "fB3hNLrZQbOfqHEeoigQAX:APA91bEXpEnclLTkl05Hj1pxfg2NPpQwQ0nc_591-agntE0EjRw3-m4HT3ATjp6SarqVbas3_J3VnoxvtvE3vyL18alqJ_Zk21u_TzORgfEA8xvp9AkmDO9TqMaOt_Yv-KcdkqeoscHn";
//                String title = "Notification Title";
//                String body = "Notification Body";
//
////                // Call the FCMNotificationSender's sendNotification method
//                FCMNotificationSender.sendNotificationToDevice(deviceToken, title, body);
//            }
//        });
//
//        binding.logTokenButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get token
//                // [START log_reg_token]
//                FirebaseMessaging.getInstance().getToken()
//                        .addOnCompleteListener(new OnCompleteListener<String>() {
//                            @Override
//                            public void onComplete(@NonNull Task<String> task) {
//                                if (!task.isSuccessful()) {
//                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                                    return;
//                                }
//
//                                // Get new FCM registration token
//                                String token = task.getResult();
//
//                                // Log and toast
//                                String msg = getString(R.string.msg_token_fmt, token);
//                                Log.d(TAG, msg);
//                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                // [END log_reg_token]
//            }
//        });

        askNotificationPermission();
    }

    private void enterPIPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(16, 9); // Aspect ratio of PIP screen
            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(aspectRatio);

            enterPictureInPictureMode(pipBuilder.build());

        }
    }

//    @Override
//    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
//        if (isInPictureInPictureMode) {
//            // Hide UI elements in PIP mode if necessary
//            binding.pipButton.setVisibility(View.INVISIBLE);
//        } else {
//            // Restore UI elements when PIP mode is exited
//            binding.pipButton.setVisibility(View.VISIBLE);
//        }
//    }

    private void enablePermission() {
        List<String> permissions;
        permissions = Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);

        requestPermissionIfNeeded(permissions, new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                 @NonNull List<String> deniedList) {
                if (grantedList.contains(Manifest.permission.CAMERA)) {
                    ZEGOSDKManager.getInstance().expressService.openCamera(true);
                    binding.videoView.startPreviewOnly();
                }
            }
        });
    }


    private void askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


//
    @Override
    protected void onResume() {
        super.onResume();
//        binding.mainHostVideo.startPreviewOnly();
    }

    protected String getSaltString(String SALTCHARS) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 20) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();

        Log.i("saltStr", "getSaltString: "+saltStr);
        return saltStr;

    }

    private void fetchUserDetails(String userId) {

        usersRef.whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Log.e("test2334", "Listen failed: " + error.getMessage());
                        return;
                    }

                    for (DocumentSnapshot document : value) {
                        userDetails = document.toObject(UserDetailsModel.class);
//
                    }
                });


    }

    protected void onPause() {
        super.onPause();
//        if (isFinishing()) {
//            ZEGOSDKManager.getInstance().disconnectUser();
//            ZEGOLiveStreamingManager.getInstance().removeUserData();
//            ZEGOLiveStreamingManager.getInstance().removeUserListeners();
//            ZEGOSDKManager.getInstance().expressService.openCamera(false);
//            ZEGOSDKManager.getInstance().expressService.stopPreview();
//        }
    }



    private void requestPermissionIfNeeded(List<String> permissions, RequestCallback requestCallback) {
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }
        if (allGranted) {
            requestCallback.onResult(true, permissions, new ArrayList<>());
            return;
        }

        PermissionX.init(this).permissions(permissions).onExplainRequestReason((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(Manifest.permission.CAMERA)) {
                    message = this.getString(R.string.permission_explain_camera);
                } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.permission_explain_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(Manifest.permission.CAMERA)) {
                        message = this.getString(R.string.permission_explain_camera);
                    } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                        message = this.getString(R.string.permission_explain_mic);
                    }
                } else {
                    message = this.getString(R.string.permission_explain_camera_mic);
                }
            }
            scope.showRequestReasonDialog(deniedList, message, getString(R.string.ok));
        }).onForwardToSettings((scope, deniedList) -> {
            String message = "";
            if (permissions.size() == 1) {
                if (deniedList.contains(Manifest.permission.CAMERA)) {
                    message = this.getString(R.string.settings_camera);
                } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.settings_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(Manifest.permission.CAMERA)) {
                        message = this.getString(R.string.settings_camera);
                    } else if (deniedList.contains(Manifest.permission.RECORD_AUDIO)) {
                        message = this.getString(R.string.settings_mic);
                    }
                } else {
                    message = this.getString(R.string.settings_camera_mic);
                }
            }
            scope.showForwardToSettingsDialog(deniedList, message, getString(R.string.settings),
                    getString(R.string.cancel));
        }).request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                 @NonNull List<String> deniedList) {
                if (requestCallback != null) {
                    requestCallback.onResult(allGranted, grantedList, deniedList);
                }
            }
        });
    }



}