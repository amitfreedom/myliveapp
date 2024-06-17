package com.stream.prettylive.ui.home.ui.profile.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stream.prettylive.R;
import com.stream.prettylive.databinding.ActivityUpdateUserDetailsBinding;
import com.stream.prettylive.global.AppConstants;
import com.stream.prettylive.global.ApplicationClass;
import com.stream.prettylive.ui.auth.models.UserMainResponse;
import com.stream.prettylive.ui.auth.viewmodel.UserViewModel;
import com.stream.prettylive.ui.home.ui.profile.models.FetchImageResponseModel;
import com.stream.prettylive.ui.home.ui.profile.models.MasterModel;
import com.stream.prettylive.ui.home.ui.profile.models.UpdateUserRequest;
import com.stream.prettylive.ui.home.ui.profile.models.UserDetailsModel;
import com.stream.prettylive.ui.home.ui.profile.models.UserUpdateResponseModel;
import com.stream.prettylive.ui.utill.Constant;
import com.stream.prettylive.ui.utill.DeviceUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateUserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UpdateUserDetailsActivi";
    private ActivityUpdateUserDetailsBinding binding;
    private UserViewModel userViewModel;

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private UserDetailsModel userDetails;
    private String device_token ="";
    private ActivityResultLauncher<String[]> intentLauncher;
    private ProgressDialog progressDialog;
    private UserMainResponse masterModel;
    private String uid="";
    private String image="";
    private String username="";
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this,
                            "Can't post notifications without Image permission",
                            Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if (getIntent() != null) {
            uid = getIntent().getStringExtra("uid");
            image = getIntent().getStringExtra("image");
            username = getIntent().getStringExtra("username");
        }

        binding.buttonCamera.setOnClickListener(this);

        intentLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(), fileUri -> {
                    if (fileUri != null) {
                        showProgressBar();
                        try {
                            userViewModel.updateProfileImage(uid, fileUri, this).observe(UpdateUserDetailsActivity.this, new Observer<FetchImageResponseModel>() {
                                @Override
                                public void onChanged(FetchImageResponseModel user) {
                                    hideProgressBar();
                                    if (user != null) {
                                        getUserDataApi();
                                        if (user.getShow()) {
                                            Toast.makeText(UpdateUserDetailsActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(UpdateUserDetailsActivity.this, "Somethings went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        getUserDataApi();
        askNotificationPermission();

        binding.txtUserName.setInputType(InputType.TYPE_NULL); // Disables keyboard input

        binding.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        binding.backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DeviceUtils.getDeviceToken(new DeviceUtils.TokenFetchListener() {
            @Override
            public void onTokenFetchSuccess(String token) {
                device_token=token;
            }

            @Override
            public void onTokenFetchFailure(Exception e) {
                device_token="";
            }
        });

//        updateUI(image,username);
    }

    private void getUserDataApi() {
        int masterId = Integer.parseInt(uid);
        userViewModel.getUser(masterId).observe(UpdateUserDetailsActivity.this, new Observer<UserMainResponse>() {
            @Override
            public void onChanged(UserMainResponse master) {
                if (master != null) {
                    masterModel=master;
                    updateUI(master.getData().getUserProfilePic(), master.getData().getUserNickName());

                } else {
                    Toast.makeText(UpdateUserDetailsActivity.this, "Master Not Found", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_username_update);

        ImageView close = bottomSheetDialog.findViewById(R.id.close);
        EditText etName = bottomSheetDialog.findViewById(R.id.tx_name);
        MaterialCardView save = bottomSheetDialog.findViewById(R.id.buttonSave);
        MaterialCardView cancel = bottomSheetDialog.findViewById(R.id.buttonCancel);

        assert etName != null;
        etName.setText(username);

        assert close != null;
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        assert cancel != null;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        assert save != null;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                updateUserDetails(ApplicationClass.getSharedpref().getString(AppConstants.DB_ID),etName.getText().toString(),bottomSheetDialog);
//                updateUserName(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID),etName.getText().toString().trim(),bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
//        bottomSheetDialog.dismiss();
    }



    private void updateUserDetails(String userID,String userNickName, BottomSheetDialog bottomSheetDialog) {

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(userNickName, masterModel.getData().getEmail(), masterModel.getData().getUid(), true, device_token, DeviceUtils.getDeviceId(UpdateUserDetailsActivity.this), userNickName);

        userViewModel.updateUser(userID, updateUserRequest).observe(UpdateUserDetailsActivity.this, new Observer<UserUpdateResponseModel>() {
            @Override
            public void onChanged(UserUpdateResponseModel user) {
                hideProgressBar();
                bottomSheetDialog.dismiss();
                if (user != null) {
                    username=user.getData().getUserNickName();
                    binding.txtUserName.setText(user.getData().getUserNickName());
                    if (user.getShow()) {
                        Toast.makeText(UpdateUserDetailsActivity.this, user.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateUserDetailsActivity.this, "Somethings went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showProgressBar() {
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    private void hideProgressBar() {
        progressDialog.dismiss();
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Your app can post notifications.
            } else{
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }
    }


    private void updateUI(String image, String username) {
        try {
            binding.txtUserName.setText(username);
            Glide.with(this)
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.profile_avatar_placeholder)
                    .error(R.drawable.profile_avatar_placeholder)
                    .into(binding.profileImage);


        }catch (Exception e){
            Log.i(TAG, "updateUI: ");

        }

    }  private void updateUI1(String username) {
        try {
            binding.txtUserName.setText(username);



        }catch (Exception e){
            Log.i(TAG, "updateUI: ");

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonCamera) {
            launchCamera();
        }
    }

    private void launchCamera() {
        intentLauncher.launch(new String[]{ "image/*" });
    }

}