package com.stream.prettylive.ui.home.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stream.prettylive.R;
import com.stream.prettylive.databinding.FragmentHomeBinding;
import com.stream.prettylive.databinding.FragmentProfileBinding;
import com.stream.prettylive.game.teenpatty.BottomSheetGameFragment;
import com.stream.prettylive.game.teenpatty.history.GameListFragment;
import com.stream.prettylive.game.teenpatty.models.GameList;
import com.stream.prettylive.global.AppConstants;
import com.stream.prettylive.global.ApplicationClass;
import com.stream.prettylive.streaming.internal.ZEGOLiveAudioRoomManager;
import com.stream.prettylive.ui.auth.models.UserMainResponse;
import com.stream.prettylive.ui.auth.viewmodel.UserViewModel;
import com.stream.prettylive.ui.follow.activity.FollowFollowingActivity;
import com.stream.prettylive.ui.follow.methods.FirestoreHelper;
import com.stream.prettylive.ui.home.ui.profile.activity.HostRegistrationFormActivity;
import com.stream.prettylive.ui.home.ui.profile.activity.LevelActivity;
import com.stream.prettylive.ui.home.ui.profile.activity.LiveHistoryActivity;
import com.stream.prettylive.ui.home.ui.profile.activity.UpdateUserDetailsActivity;
import com.stream.prettylive.ui.home.ui.profile.models.MasterModel;
import com.stream.prettylive.ui.home.ui.profile.models.UserDetailsModel;
import com.stream.prettylive.ui.lucky_game.GameLuckyActivity;
import com.stream.prettylive.ui.startup.activity.OnboardingActivity;
import com.stream.prettylive.ui.utill.Constant;
import com.stream.prettylive.ui.utill.Convert;
import com.stream.prettylive.ui.vip.VIPActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FragmentProfileBinding binding;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private UserDetailsModel userDetails;
    private UserViewModel userViewModel;
    private String uid="";

    private MasterModel masterModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        uid = ApplicationClass.getSharedpref().getString(AppConstants.UID);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        init();
        getUserDataApi(uid);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your refresh code here
                getUserDataApi(uid);
                // Once the refresh is complete, call this to stop the refreshing indicator
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getUserDataApi(String uid) {
        binding.swipeRefreshLayout.setRefreshing(true);
        int masterId = Integer.parseInt(uid);
        userViewModel.getMaster(masterId).observe(requireActivity(), new Observer<MasterModel>() {
            @Override
            public void onChanged(MasterModel master) {
                if (master != null) {
                    masterModel = master;
                    updateUI1(master);
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    binding.swipeRefreshLayout.setRefreshing(false);


                }
                assert master != null;
                if (master.getShow()) {
                    Toast.makeText(requireActivity(), master.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // In your activity or fragment
    public void gameListPopup() {
        GameListFragment bottomSheetDialogFragment = new GameListFragment(new GameListFragment.OnGameSelectedListener() {
            @Override
            public void onGameSelected(GameList game) {
                if (Objects.equals(game.getTitle(), "Teen Patty")) {
                    showBottomSheetDialog();
                }
                else if (Objects.equals(game.getTitle(), "Fruits loops")) {
                    Toast.makeText(requireActivity(), "coming soon...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottomSheetDialogFragment.show(requireActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }
    public void showBottomSheetDialog() {
        BottomSheetGameFragment bottomSheetDialogFragment = new BottomSheetGameFragment();
        bottomSheetDialogFragment.show(requireActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    private void init() {
        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showBottomSheetDialog();
//                gameListPopup();
            }
        });
        binding.btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent;
//                intent = new Intent(getActivity().getApplication(), GameLuckyActivity.class);
//                startActivity(intent);
//                gameListPopup();
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity(), UpdateUserDetailsActivity.class);
                mainIntent.putExtra("uid",masterModel.getData().getUid());
                mainIntent.putExtra("image",masterModel.getData().getUserProfilePic());
                mainIntent.putExtra("username",masterModel.getData().getUserNickName());
                startActivity(mainIntent);
            }
        });
        binding.btnHostRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainIntent = new Intent(getActivity(), HostRegistrationFormActivity.class);
//                mainIntent.putExtra("uid",String.valueOf(userDetails.getUid()));
//                mainIntent.putExtra("image",userDetails.getImage());
//                startActivity(mainIntent);
            }
        });
        binding.btnLiveHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainIntent = new Intent(getActivity(), LiveHistoryActivity.class);
//                startActivity(mainIntent);
            }
        });
        binding.btnVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainIntent = new Intent(getActivity(), VIPActivity.class);
//                startActivity(mainIntent);
            }
        });
        binding.btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainIntent = new Intent(getActivity(), FollowFollowingActivity.class);
//                mainIntent.putExtra("type","following");
//                startActivity(mainIntent);
            }
        });
        binding.btnFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainIntent = new Intent(getActivity(), FollowFollowingActivity.class);
//                mainIntent.putExtra("type","follow");
//                startActivity(mainIntent);
            }
        });
        binding.btnLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainIntent = new Intent(getActivity(), LevelActivity.class);
//                startActivity(mainIntent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI1(MasterModel userDetails1) {
        try {
            binding.txtUsername.setText(userDetails1.getData().getUserNickName());
            binding.txtUid.setText("ID : "+userDetails1.getData().getUid());
            binding.txtLevel.setText("Lv"+userDetails1.getData().getLevel());
            binding.txtCoin.setText(new Convert().prettyCount(userDetails1.getData().getUcoins()));
            binding.txtDiamond.setText(new Convert().prettyCount(userDetails1.getData().getUdiamonds()));
            // Load image
            Glide.with(requireActivity())
                    .load(userDetails1.getData().getUserProfilePic())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.profile_avatar_placeholder)
                    .error(R.drawable.profile_avatar_placeholder)
                    .into(binding.profileImage);

            binding.txtFollowingCount.setText(userDetails1.getData().getFollowing());
            binding.txtFollowersCount.setText(userDetails1.getData().getFollowers());

            if (!Objects.equals(userDetails1.getData().getUserProfilePic(), "") || userDetails1.getData().getUserProfilePic() != null) {
                ZEGOLiveAudioRoomManager.getInstance().updateUserAvatarUrl(userDetails1.getData().getUserProfilePic(),(userAvatarUrl, errorInfo) -> {
                    Log.i("3456789", "userAvatarUrl: "+userAvatarUrl);

                });
            }
        }catch (Exception e){
            Log.i(TAG, "updateUI: "+e);
        }

    }

    private void showLogoutDialog() {
        // Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Inflate the custom layout/view
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_logout, null);
        builder.setView(dialogView);

        // Get the buttons from the custom layout
        Button buttonNo = dialogView.findViewById(R.id.button_no);
        Button buttonYes = dialogView.findViewById(R.id.button_yes);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Set the button click listeners
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform logout operation
                // For example, you can call a method to handle the logout
                logout();
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void logout() {
        mGoogleSignInClient.signOut();
        ApplicationClass.getSharedpref().clearPreferences();
        Intent mainIntent = new Intent(getActivity(), OnboardingActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}