package com.stream.prettylive.streaming.activity;


import static com.stream.prettylive.global.DateUtils.getFormattedDateTime;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.stream.prettylive.R;
import com.stream.prettylive.databinding.ActivityLiveStreamingBinding;
import com.stream.prettylive.global.AppConstants;
import com.stream.prettylive.global.ApplicationClass;
import com.stream.prettylive.notification.FCMNotificationSender;
import com.stream.prettylive.streaming.ZEGOSDKKeyCenter;
import com.stream.prettylive.streaming.activity.adapter.CommentAdapter;
import com.stream.prettylive.streaming.activity.adapter.GameViewSwitcherAdapter;
import com.stream.prettylive.streaming.activity.adapter.GiftAdapter;
import com.stream.prettylive.streaming.activity.adapter.GiftViewUserAdapter;
import com.stream.prettylive.streaming.activity.adapter.ViewUserAdapter;
import com.stream.prettylive.streaming.activity.adapter.ViewersListAdapter;
import com.stream.prettylive.streaming.activity.model.ChatMessageModel;
import com.stream.prettylive.streaming.activity.model.PurchageGiftModel;
import com.stream.prettylive.streaming.activity.model.RoomUsers;
import com.stream.prettylive.streaming.components.message.barrage.BottomInputDialogMessage;
import com.stream.prettylive.streaming.functions.AddStreamInfo;
import com.stream.prettylive.streaming.functions.CurrentUserInfo;
import com.stream.prettylive.streaming.functions.EndLiveStatus;
import com.stream.prettylive.streaming.functions.KickOutInfo;
import com.stream.prettylive.streaming.functions.SendGlobalMessage;
import com.stream.prettylive.streaming.functions.UserInfo;
import com.stream.prettylive.streaming.gift.GiftHelper;
import com.stream.prettylive.streaming.internal.ZEGOLiveAudioRoomManager;
import com.stream.prettylive.streaming.internal.ZEGOLiveStreamingManager;
import com.stream.prettylive.streaming.internal.ZEGOLiveStreamingManager.LiveStreamingListener;
import com.stream.prettylive.streaming.internal.business.RoomRequestExtendedData;
import com.stream.prettylive.streaming.internal.business.RoomRequestType;
import com.stream.prettylive.streaming.internal.business.pk.PKService.PKInfo;
import com.stream.prettylive.streaming.internal.sdk.ZEGOSDKManager;
import com.stream.prettylive.streaming.internal.sdk.basic.ZEGOSDKCallBack;
import com.stream.prettylive.streaming.internal.sdk.basic.ZEGOSDKUser;
import com.stream.prettylive.streaming.internal.sdk.components.effect.BeautyDialog;
import com.stream.prettylive.streaming.internal.sdk.express.ExpressService;
import com.stream.prettylive.streaming.internal.sdk.express.IExpressEngineEventHandler;
import com.stream.prettylive.streaming.internal.sdk.zim.IZIMEventHandler;
import com.stream.prettylive.streaming.internal.utils.ToastUtil;
import com.stream.prettylive.ui.home.HomeActivity;
import com.stream.prettylive.ui.home.ui.profile.models.UserDetailsModel;
import com.stream.prettylive.ui.home.ui.profile.models.UserModel;
import com.stream.prettylive.ui.utill.Constant;
import com.stream.prettylive.ui.utill.Convert;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoMixerStartCallback;
import im.zego.zegoexpress.callback.IZegoRoomLoginCallback;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.entity.ZegoEffectsBeautyParam;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMLoggedInCallback;
import im.zego.zim.callback.ZIMRoomEnteredCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMRoomFullInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class LiveStreamingActivity extends AppCompatActivity{
    private static final int LIMIT = 50;
    private ActivityLiveStreamingBinding binding;
    private String liveID;
    private String userId;
    private String otherUserId;
    private boolean isInPIPMode = false; // Track PIP mode state
    private String audienceId;
    private String username;
    private String country;
    private String image;
    private String level;
    private long uid;
    //    private AlertDialog inviteCoHostDialog;
    private AlertDialog zimReconnectDialog;
    private AlertDialog startPKDialog;
    private BeautyDialog beautyDialog;
    private SignInClient signInClient;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private CollectionReference usersRef;
    private UserDetailsModel userDetails;
    private String docId;
    private UserModel userModel;
    private ViewersListAdapter mAdapter1;
    private ViewUserAdapter mAdapter;
    private Query mQuery,mQuery1,mQuery2;
    Animation topAnimantion,bottomAnimation,rightToLeft;
    View giftButton;
    private GiftAdapter giftAdapter;
    private DocumentSnapshot documentSnapshot;
    private GiftViewUserAdapter giftViewUserAdapter;
    List<String>userIds = new ArrayList<>();
    private String userIdForReceiveGift="";
    private String receiverUserName="";
    private String receiverTotalCoins="";
    private int select =1;
    private String totalBeans="0";
    private String hostTotalCoins="0";
    private int giftCount=1;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = firebaseDatabase.getReference().child("userInfo");
    private PurchageGiftModel purchageGiftModel;

    private CommentAdapter commentAdapter;
    private final List<ChatMessageModel> chatMessages = new ArrayList<>();
    private List<UserDetailsModel>topGamer= new ArrayList();
    private int mCurrentPage = 0;
    private Timer mTimer;
    private GameViewSwitcherAdapter gameViewSwitcherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveStreamingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        usersRef = firestore.collection(Constant.LOGIN_DETAILS);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.right_to_left);


        boolean isHost = getIntent().getBooleanExtra("host", true);
        userId = getIntent().getStringExtra("userId");
        otherUserId = getIntent().getStringExtra("userId");
        liveID = getIntent().getStringExtra("liveID");
        username = getIntent().getStringExtra("username");
        country = getIntent().getStringExtra("country_name");
        image = getIntent().getStringExtra("image");
        level = getIntent().getStringExtra("level");
        audienceId = getIntent().getStringExtra("audienceId");
        uid = getIntent().getLongExtra("uid",0);

        ApplicationClass.getSharedpref().saveString(AppConstants.ROOM_ID,liveID);

        Log.i("liveID", "onCreate: ===="+liveID);
        fetchOtherUserDetails(userId);
        if (!isHost){
            currentUserDetails(audienceId);
        }else {
//            hostCoins(otherUserId);

        }

        getUserCoins(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));


        ZEGOLiveStreamingManager.getInstance().addRoomListeners();

        listenSDKEvent();

        getCommentChatMessageFirebase();

        binding.previewStart.setOnClickListener(v -> {
            loginRoom();

        });
        binding.cardUserCount.setOnClickListener(v -> {
            showBottomSheetDialog();
        });
        binding.pipButton.setOnClickListener(v -> {
            enterPIPMode();
        });



        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exitDialog();
            }
        });

        commentAdapter = new CommentAdapter(this, chatMessages);
        binding.recyclerAllMessage.setAdapter(commentAdapter);

        binding.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomInputDialogMessage inputDialog = new BottomInputDialogMessage(LiveStreamingActivity.this,liveID,"video");
                inputDialog.show();
            }
        });



        binding.previewBeauty.setOnClickListener(v -> {
            if (beautyDialog == null) {
                beautyDialog = new BeautyDialog(LiveStreamingActivity.this);
            }
            beautyDialog.show();
        });
        binding.liveBottomMenuBar.setBeautyButtonClickListener(v -> {
            if (beautyDialog == null) {
                beautyDialog = new BeautyDialog(LiveStreamingActivity.this);
            }
            beautyDialog.show();
        });

        if (isHost) {
            // join when click start
            ZEGOSDKManager.getInstance().expressService.openCamera(true);
            ZEGOSDKManager.getInstance().expressService.openMicrophone(true);
            binding.previewStart.setVisibility(View.VISIBLE);
            if (ZEGOSDKManager.getInstance().effectsService.isEffectSDKInit()) {
                binding.previewBeauty.setVisibility(View.VISIBLE);
            }
            binding.mainHostVideo.startPreviewOnly();

            ZEGOSDKUser currentUser = ZEGOSDKManager.getInstance().expressService.getCurrentUser();
            ZEGOLiveStreamingManager.getInstance().setHostUser(currentUser);

//            Toast.makeText(this, "name :"+currentUser.userName, Toast.LENGTH_SHORT).show();
        }
        else {
            // join right now
            ZEGOSDKManager.getInstance().expressService.openCamera(false);
            ZEGOSDKManager.getInstance().expressService.openMicrophone(false);
            binding.previewStart.setVisibility(View.GONE);
            loginRoom();
            checkKickOut(liveID,ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
            realTimeLiveEnd(liveID,otherUserId);



        }

        mQuery = firestore.collection("room_users").document(liveID).collection("viewers")
//                .orderBy("uid", Query.Direction.DESCENDING)
                .whereNotEqualTo("userId", ApplicationClass.getSharedpref().getString(AppConstants.USER_ID))
                .limit(LIMIT);

        mQuery1 = firestore.collection(Constant.GIFTS)
                .orderBy("price", Query.Direction.ASCENDING)
//                .whereEqualTo("gift_type","1000")
                .limit(LIMIT);

        mQuery2 = firestore.collection(Constant.STREAM).document(liveID).collection("current_room_user")
//                .orderBy(Query.Direction.ASCENDING)
//                .whereEqualTo("gift_type","1000")
                .limit(LIMIT);

        setViewersAdapter();

        // add a gift button to liveAudioRoom audience
        GiftHelper giftHelper = new GiftHelper(findViewById(R.id.layout), String.valueOf(uid), username,otherUserId,"1");
        giftButton = giftHelper.getGiftButton(this, ZEGOSDKKeyCenter.appID, ZEGOSDKKeyCenter.serverSecret, liveID);

        // Get reference to the giftButtonContainer
        FrameLayout giftButtonContainer = findViewById(R.id.giftButtonContainer);
        giftButtonContainer.addView(giftButton);

        binding.giftButton.setOnClickListener(v -> {
            showGiftBottomSheetDialog();
        });

        setTopGamerView();
        topGamerList();

//        ZegoExpressEngine.getEngine().enableEffectsBeauty(true);
//        ZegoExpressEngine.getEngine().setEffectsBeautyParam(new ZegoEffectsBeautyParam());

        // Simulate a click on the giftButton
//        giftButton.post(new Runnable() {
//            @Override
//            public void run() {
//                giftButton.performClick();
//            }
//        });

    }

    private void setTopGamerView() {
        gameViewSwitcherAdapter = new GameViewSwitcherAdapter(this, topGamer);
        binding.gameViewPager.setAdapter(gameViewSwitcherAdapter);

        final Handler handler = new Handler();
        try {
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (mCurrentPage == topGamer.size() - 1) {
                        mCurrentPage = 0;
                    } else {
                        mCurrentPage++;
                    }
                    binding.gameViewPager.setCurrentItem(mCurrentPage, true);
                }
            };

            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, 5000, 5000);
        }catch (Exception e){
            Log.i("error", "setTopGamerView: "+e);
        }

        binding.gameViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event if needed
            }
        });
    }
    private void topGamerList() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("login_details")
                .orderBy("receiveGameCoin", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {

                                UserDetailsModel userDetailsModel = document.toObject(UserDetailsModel.class);
                                topGamer.add(userDetailsModel);
                            }
                            gameViewSwitcherAdapter.updateData(topGamer);
                        } else {
                            // Handle the case where the query snapshot is null
                        }
                    } else {
                        // Handle the error
                        Exception exception = task.getException();
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                    }
                });
    }

    private void getCommentChatMessageFirebase() {
        ref.child(liveID).child("video").child(liveID).child("chat_comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chatMessages.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatMessageModel chatMessageModel = dataSnapshot.getValue(ChatMessageModel.class);
                        chatMessages.add(chatMessageModel);
                    }

                    commentAdapter.notifyDataSetChanged();
                    binding.recyclerAllMessage.scrollToPosition(chatMessages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void checkKickOut(String liveId, String userId) {
        new KickOutInfo(new KickOutInfo.Select() {
            @Override
            public void KickOutStatus(int kickValue) {
                if (kickValue==1){
                    ZEGOLiveAudioRoomManager.getInstance().leave();
//                    deleteUserFromViewersCollection(liveId,userModel.getUid());
                    finish();
                }else {

                }

            }
        }).realTimeKickOut(liveId,userId);

    }

    private void realTimeLiveEnd(String roomID, String userId) {
        new EndLiveStatus(new EndLiveStatus.Select() {
            @Override
            public void LiveEndStatus(int endStatus) {
                try {
                    if (endStatus==1){
                        ZEGOLiveStreamingManager.getInstance().leave();
                        finish();
                    }else {

                    }
                }catch (Exception e){

                }
            }
        }).realTimeLiveEnd(roomID,userId);
    }

    private void getUserCoins(String userId) {
        Log.i("Coins123", "userId =: " + userId);
        usersRef.whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Log.e("FirestoreListener", "Listen failed: " + error.getMessage());
                        return;
                    }

                    for (DocumentSnapshot document : value) {
                        if (document.exists()) {
                            // Get the "coins" field from the document
                            String beans = document.getString("coins");
                            String image1 = document.getString("image");
                            docId = document.getString("docId");

                            if (beans != null) {
                                totalBeans=beans;
                            }
                            if (!Objects.equals(image1, "")) {
                                ZEGOLiveAudioRoomManager.getInstance().updateUserAvatarUrl(image1,(userAvatarUrl, errorInfo) -> {

                                    Log.i("3456789", "userAvatarUrl: "+userAvatarUrl);

                                });
                            }else {
                                ZEGOLiveAudioRoomManager.getInstance().updateUserAvatarUrl(Constant.USER_PLACEHOLDER_PATH,(userAvatarUrl, errorInfo) -> {


                                });
                            }

                        }
                    }
                });
    }

    private void showGiftBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout_gift);

        RecyclerView recyclerView = bottomSheetDialog.findViewById(R.id.recycler_gift);
        RecyclerView rv_gift_user = bottomSheetDialog.findViewById(R.id.rv_gift_user);
        Spinner spinner = bottomSheetDialog.findViewById(R.id.spinner);
        MaterialButton button_hot = bottomSheetDialog.findViewById(R.id.button_hot);
        MaterialCardView btn_close = bottomSheetDialog.findViewById(R.id.btn_close);
        MaterialCardView btnSelectAll = bottomSheetDialog.findViewById(R.id.btnSelectAll);
        MaterialButton send = bottomSheetDialog.findViewById(R.id.materialButtonSend);
        TextView txt_beans = bottomSheetDialog.findViewById(R.id.txt_beans);
        TextView txtAll = bottomSheetDialog.findViewById(R.id.txtAll);
        ImageView ivSeat = bottomSheetDialog.findViewById(R.id.ivSeat);

        MaterialButtonToggleGroup toggleGroup = bottomSheetDialog.findViewById(R.id.toggleGroup);

        btn_close.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            giftViewUserAdapter.clearSelection();
        });
        btnSelectAll.setOnClickListener(v -> {
            if (select==1){
                btnSelectAll.setCardBackgroundColor(getResources().getColor(R.color.pink_top));
                txtAll.setTextColor(getResources().getColor(R.color.white));
                ivSeat.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                giftViewUserAdapter.selectAll();
                select =0;
            }
            else if (select==0){
                giftViewUserAdapter.clearSelection();
                btnSelectAll.setCardBackgroundColor(getResources().getColor(R.color.white));
                txtAll.setTextColor(getResources().getColor(R.color.black));
                ivSeat.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                select =1;
            }


        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                giftCount = Integer.parseInt(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        assert toggleGroup != null;
        assert button_hot != null;
        toggleGroup.check(button_hot.getId());
        button_hot.setBackgroundColor(getResources().getColor(R.color.pink_top));
        button_hot.setTextColor(getResources().getColor(R.color.white));
        button_hot.setStrokeColorResource(R.color.pink_top);

        try {
            txt_beans.setText(new Convert().prettyCount(Integer.parseInt(totalBeans)));
        }catch (Exception e){

        }

        assert send != null;
        send.setOnClickListener(V->{
            if (documentSnapshot==null){
                Toast.makeText(this, "Please select gift first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(totalBeans) >= Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("price")))){
                if (Objects.equals(userIdForReceiveGift, "") && userIds.size()==0){
                    Toast.makeText(this, "Select room user first", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendGift(documentSnapshot,bottomSheetDialog,giftCount);

            }else {
                Toast.makeText(this, "Insufficient balance, please recharge first", Toast.LENGTH_SHORT).show();
            }


        });


        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    MaterialButton checkedButton = group.findViewById(checkedId);
                    String title = checkedButton.getText().toString();
                    checkedButton.setBackgroundColor(getResources().getColor(R.color.pink_top));
                    checkedButton.setTextColor(getResources().getColor(R.color.white));
                    checkedButton.setStrokeColorResource(R.color.pink_top);

//                    if (title.equals("Hot")){
//                        mQuery = mFirestore.collection(Constant.GIFTS)
//                                .orderBy("price", Query.Direction.ASCENDING)
//                                .whereEqualTo("gift_type","1000")
//                                .limit(LIMIT);
//
//                        mAdapter.setQuery(mQuery);
//
//                    }else  if (title.equals("Popular")){
//                        mQuery = mFirestore.collection(Constant.GIFTS)
//                                .orderBy("price", Query.Direction.ASCENDING)
//                                .whereEqualTo("gift_type","1001")
//                                .limit(LIMIT);
//
//                        mAdapter.setQuery(mQuery);
//
//                    }
//                    else  if (title.equals("Lucky")){
//                        mQuery = mFirestore.collection(Constant.GIFTS)
//                                .orderBy("price", Query.Direction.ASCENDING)
//                                .whereEqualTo("gift_type","1002")
//                                .limit(LIMIT);
//
//                        mAdapter.setQuery(mQuery);
//
//                    }

                }
                else {
                    MaterialButton checkedButton = group.findViewById(checkedId);
                    checkedButton.setBackgroundColor(getResources().getColor(R.color.white));
                    checkedButton.setTextColor(getResources().getColor(R.color.gray));
//                    checkedButton.setTextSize(R.dimen._14sp);

                }
            }
        });

        giftViewUserAdapter = new GiftViewUserAdapter(mQuery2, new GiftViewUserAdapter.OnActiveUserSelectedListener() {
            @Override
            public void onActiveUserSelected(DocumentSnapshot user) {
                userIdForReceiveGift=user.getString("userID");

            }

            @Override
            public void SelectedUser(String data) {

                if (Objects.equals(data, "1")){
                    mQuery2.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Handle the documents
                            userIds.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                // Access data from the document
                                String ids = document.getString("userID");
                                Log.i("printName123", "SelectedUser: "+ids);
                                userIds.add(ids);
                            }
                        } else {
                            // Handle errors
                            Exception exception = task.getException();
                            if (exception != null) {
                                Log.i("printName123", "exception: "+exception);
                                // Handle the exception
                            }
                        }
                    });
                }
                else {
                    userIds.clear();
                }

            }
        }) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                try {
                    if (getItemCount() == 0) {
                        rv_gift_user.setVisibility(View.GONE);

                    } else {
                        rv_gift_user.setVisibility(View.VISIBLE);

                    }
                }catch (Exception e){

                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e("FirebaseFirestoreException", "onError: "+e );
            }


        };
        rv_gift_user.setAdapter(giftViewUserAdapter);
        giftViewUserAdapter.setQuery(mQuery2);
        giftAdapter = new GiftAdapter(mQuery1, new GiftAdapter.OnGiftSelectedListener() {
            @Override
            public void onGiftSelected(DocumentSnapshot user) {
                documentSnapshot=user;
            }
        }) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
//                    binding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
//                    binding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e("FirebaseFirestoreException", "onError: "+e );
            }


        };
        recyclerView.setAdapter(giftAdapter);
        giftAdapter.setQuery(mQuery1);

        bottomSheetDialog.show();
    }


    private void sendGift(DocumentSnapshot giftModel, BottomSheetDialog bottomSheetDialog, int giftCount) {

//        long timestamp = System.currentTimeMillis();
        Date currentDate = new Date();
        long timestamp = currentDate.getTime();
        Map<String, Object> data = new HashMap<>();
        data.put("senderId", ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
        data.put("diamond", giftModel.getString("price"));
        data.put("receiverId", otherUserId);
//        data.put("receiverId", userIdForReceiveGift);
        data.put("giftId", giftModel.getString("giftId"));
        data.put("fileName", giftModel.getString("fileName"));
        data.put("liveId", liveID);
        data.put("time", timestamp);
        firestore.collection("giftDetails").document(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID)).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                boolean isHost = getIntent().getBooleanExtra("host", true);
                String liveType="1";
                Map<String, Object> data = new HashMap<>();
                data.put("fileName", giftModel.getString("fileName"));
                data.put("giftCoin", giftModel.getString("price"));
                data.put("userId", otherUserId);
                data.put("giftId", giftModel.getString("giftId"));
                data.put("liveType", liveType);
                data.put("gift_count", userIds.size()>0?String.valueOf(userIds.size()):String.valueOf(giftCount));
                data.put("liveId", liveID);
                String key = ref.push().getKey();
                ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").child(key).setValue(data);

                if (!Objects.equals(userIdForReceiveGift, "")) {
                    new CurrentUserInfo(new UserInfo.Select() {
                        @Override
                        public void UserDetailsByUserId(UserDetailsModel userInfoById) {

                            updateGiftSenderCoins(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID),userInfoById.getCoins(),userInfoById.getSenderCoin(),giftModel.getString("price"));

                        }
                    }).getUserDetailsByUserId(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));

                    new UserInfo(new UserInfo.Select() {
                        @Override
                        public void UserDetailsByUserId(UserDetailsModel userInfoById) {
                            updateGiftReceiverCoins(userInfoById.getUserId(),userInfoById.getDiamond(),giftModel.getString("price"));
                            receiverTotalCoins = new Convert().prettyCount(Long.parseLong(Objects.requireNonNull(giftModel.getString("price"))));

                            String fullMsg = "sent gift to "+userInfoById.getUsername()+" "+receiverTotalCoins;

                            new SendGlobalMessage(liveID,fullMsg,"video");
                        }
                    }).getUserDetailsByUserId(userIdForReceiveGift);

                }
                else {

                    for (String id:userIds){
                        new CurrentUserInfo(new UserInfo.Select() {
                            @Override
                            public void UserDetailsByUserId(UserDetailsModel userInfoById) {

                                updateGiftSenderCoins(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID),userInfoById.getCoins(),userInfoById.getSenderCoin(),giftModel.getString("price"));

                            }
                        }).getUserDetailsByUserId(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));

                        new UserInfo(new UserInfo.Select() {
                            @Override
                            public void UserDetailsByUserId(UserDetailsModel userInfoById) {
                                updateGiftReceiverCoins(id,userInfoById.getDiamond(),giftModel.getString("price"));
                                receiverTotalCoins = new Convert().prettyCount(Long.parseLong(Objects.requireNonNull(giftModel.getString("price"))));

                                String fullMsg = "sent gift to "+userInfoById.getUsername()+" "+receiverTotalCoins;

                                new SendGlobalMessage(liveID,fullMsg,"video");
                            }
                        }).getUserDetailsByUserId(id);

                    }
                }

                userIdForReceiveGift="";
                select=0;
                userIds.clear();

                bottomSheetDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LiveStreamingActivity.this, "Internal server error please try again."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
//        sendCustomeMessage("Sends you gift", detail.getImage());
//        Toast.makeText(this, ""+giftModel.getString("giftName"), Toast.LENGTH_SHORT).show();


    }

    private void updateGiftSenderCoins(String senderId, String totalCoins,long senderCoin, String currentPrice) {
        // Reference to the Firestore collection
        CollectionReference detailsRef = firestore.collection(Constant.LOGIN_DETAILS);

        // Create a query to find the document with the given userId
        Query query = detailsRef.whereEqualTo("userId", senderId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get the document ID for the matched document
                    String documentId = document.getId();
                    Long totalCoin = Long.parseLong(totalCoins) - Long.parseLong(currentPrice);
                    Long totalSenderCoin = senderCoin + Long.parseLong(currentPrice);
                    Map<String, Object> updateDetails = new HashMap<>();
                    updateDetails.put("coins", String.valueOf(totalCoin));
                    updateDetails.put("senderCoin", totalSenderCoin);
                    // Update the liveType field from 0 to 1
                    detailsRef.document(documentId)
                            .update(updateDetails)
                            .addOnSuccessListener(aVoid -> {
                                Log.i("Coinsup", "Coins updated successfully for user with ID: " + senderId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UpdateLiveType", "Error updating liveType for user with ID: " + userId, e);
                            });
                }
            } else {
                Log.e("UpdateLiveType", "Error getting documents: ", task.getException());
            }
        });

    }
    private void updateGiftReceiverCoins(String senderId, String totalCoins, String currentPrice) {
        Log.i("Coinsup", "Coins  " + senderId);
        Log.i("Coinsup", "totalCoins " + totalCoins);
        Log.i("Coinsup", "currentPrice " + currentPrice);
        // Reference to the Firestore collection
        CollectionReference detailsRef = firestore.collection(Constant.LOGIN_DETAILS);

        // Create a query to find the document with the given userId
        Query query = detailsRef.whereEqualTo("userId", senderId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get the document ID for the matched document
                    String documentId = document.getId();
                    Long totalCoin = Long.parseLong(totalCoins) + Long.parseLong(currentPrice);
                    Map<String, Object> updateDetails = new HashMap<>();
                    updateDetails.put("diamond", String.valueOf(totalCoin));
                    updateDetails.put("receiveCoin", totalCoin);
                    // Update the liveType field from 0 to 1
                    detailsRef.document(documentId)
                            .update(updateDetails)
                            .addOnSuccessListener(aVoid -> {
                                Log.i("Coinsup", "Coins updated successfully for user with ID: " + senderId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UpdateLiveType", "Error updating liveType for user with ID: " + userId, e);
                            });
                }
            } else {
                Log.e("UpdateLiveType", "Error getting documents: ", task.getException());
            }
        });

    }

    private long pressedTime;
    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
//            finish();
        } else {
//            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            exitDialog();
        }
        pressedTime = System.currentTimeMillis();
    }


    private void enterPIPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(16, 9); // Aspect ratio of PIP screen
            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(aspectRatio);

            enterPictureInPictureMode(pipBuilder.build());
            isInPIPMode = true;
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            // Hide UI elements in PIP mode if necessary
            binding.giftButton.setVisibility(View.INVISIBLE);
            binding.pipButton.setVisibility(View.INVISIBLE);
            binding.topView.setVisibility(View.INVISIBLE);
            binding.previewStart.setVisibility(View.INVISIBLE);
            binding.previewBeauty.setVisibility(View.INVISIBLE);
            binding.messageBtn.setVisibility(View.INVISIBLE);
            binding.gameViewPager.setVisibility(View.INVISIBLE);
            binding.recyclerAllMessage.setVisibility(View.INVISIBLE);
            binding.liveBottomMenuBar.setVisibility(View.INVISIBLE);
        } else {
            // Restore UI elements when PIP mode is exited
            binding.giftButton.setVisibility(View.VISIBLE);
            binding.pipButton.setVisibility(View.VISIBLE);
            binding.topView.setVisibility(View.VISIBLE);
            binding.previewStart.setVisibility(View.GONE);
            binding.previewBeauty.setVisibility(View.GONE);
            binding.messageBtn.setVisibility(View.VISIBLE);
            binding.gameViewPager.setVisibility(View.VISIBLE);
            binding.recyclerAllMessage.setVisibility(View.VISIBLE);
            binding.liveBottomMenuBar.setVisibility(View.VISIBLE);
        }
    }

    private void exitDialog() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to leave ?")
                .setCancelable(false)
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ZEGOLiveStreamingManager.getInstance().leave();
                        boolean isHost = getIntent().getBooleanExtra("host", true);
                        if (isHost){
                            updateLiveStatus(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
                        }
                        startActivity(new Intent(LiveStreamingActivity.this, HomeActivity.class));
                        finishAffinity();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void setViewersAdapter() {
        mAdapter = new ViewUserAdapter(mQuery, new ViewUserAdapter.OnActiveUserSelectedListener() {
            @Override
            public void onActiveUserSelected(DocumentSnapshot user) {

            }
        }) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    binding.rvViewers.setVisibility(View.GONE);
                    binding.txtUserCount.setText("0");

                } else {
                    binding.rvViewers.setVisibility(View.VISIBLE);
                    binding.txtUserCount.setText(""+getItemCount());

                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e("FirebaseFirestoreException", "onError: "+e );
            }


        };
        binding.rvViewers.setAdapter(mAdapter);
    }

    private void fetchOtherUserDetails(String userId) {
        Log.i("test2334", "fetchUserDetails: "+userId);

        usersRef.whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Log.e("test2334", "Listen failed: " + error.getMessage());
                        return;
                    }

                    for (DocumentSnapshot document : value) {
                        userDetails = document.toObject(UserDetailsModel.class);
                        updateUI(userDetails);

                        Log.i("test2334", "other user name "+userDetails.getUsername());
                        Log.i("test2334", "other user uid: "+userDetails.getUid());
                        Log.i("test2334", "other user userId: "+userDetails.getUsername());
//
                    }
                });


    }
    private void currentUserDetails(String userId) {

        usersRef.whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Log.e("test2334", "Listen failed: " + error.getMessage());
                        return;
                    }

                    for (DocumentSnapshot document : value) {
                        userModel = document.toObject(UserModel.class);
//                        saveRoomUsers(userModel);
                        Log.i("test2334", "current user name "+userModel.getUsername());
                        Log.i("test2334", "current user uid: "+userModel.getUid());
                        Log.i("test2334", "current user userId: "+userModel.getUsername());
//
                    }
                });


    }
    private void hostCoins(String userId) {

        usersRef.whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Log.e("test2334", "Listen failed: " + error.getMessage());
                        return;
                    }

                    for (DocumentSnapshot document : value) {
                        if (document!=null){
                            hostTotalCoins=document.getString("coins");
                        }

                        ToastUtil.show(LiveStreamingActivity.this,""+document.getString("coins"));
//
                    }
                });


    }

    private void updateUI(UserDetailsModel userDetails) {
        try {
            if(!Objects.equals(userDetails.getImage(), "")) {
                Glide.with(this).load(userDetails.getImage()).into(binding.ivUserImage);
            }else {
                Glide.with(this).load(Constant.USER_PLACEHOLDER_PATH).into(binding.ivUserImage);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.txtUsername.setText(userDetails.getUsername());
                    binding.txtUid.setText("ID : "+String.valueOf(userDetails.getUid()));
                    binding.txtLevel.setText("Lv"+userDetails.getLevel());
//                    binding.txtCoin.setText(new Convert().prettyCount(Integer.parseInt(userDetails.getCoins())));
                    binding.txtCoin.setText(new Convert().prettyCount(Integer.parseInt(userDetails.getDiamond())));
                }
            });
        }catch (Exception e){
            Log.e("error", "Exception in Update coin: "+e.getMessage() );

        }
    }

    private void saveLiveData(String userId,long uid,String userName,boolean isHost,String liveID,String liveType,String country,String image) {

//        long timestamp = System.currentTimeMillis();
        Date currentDate = new Date();
        long timestamp = currentDate.getTime();
        Map<String, Object> liveDetails = new HashMap<>();
        liveDetails.put("userId", userId);
        liveDetails.put("uid", uid);
        liveDetails.put("username", userName);
        liveDetails.put("photo", image!=""?image:Constant.USER_PLACEHOLDER_PATH);
        liveDetails.put("tag", "");
        liveDetails.put("host", isHost);
        liveDetails.put("liveID", liveID);
        liveDetails.put("liveType", liveType);
        liveDetails.put("liveStatus", "online");
        liveDetails.put("startTime", timestamp);
        liveDetails.put("endTime", timestamp);
        liveDetails.put("startDate", getFormattedDateTime());
        liveDetails.put("endDate", getFormattedDateTime());
        liveDetails.put("country", country);

        // Add the login details to Firestore
        firestore.collection(Constant.LIVE_DETAILS)
                .add(liveDetails)
                .addOnSuccessListener(documentReference -> {
                    Log.i("documentReference", "documentReference: created ");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding details"+e,Toast.LENGTH_SHORT).show();
                    // Handle failure
                    Log.e("MainActivity", "Error adding  details", e);
                });


    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_viewers);

        RecyclerView recyclerViewAudience = bottomSheetDialog.findViewById(R.id.recyclerViewAudience);
        TextView notFound = bottomSheetDialog.findViewById(R.id.notFound);

        mAdapter1 = new ViewersListAdapter(mQuery, new ViewersListAdapter.OnActiveUserSelectedListener() {
            @Override
            public void onActiveUserSelected(DocumentSnapshot user) {
                showCustomAlertDialog(LiveStreamingActivity.this,user);
            }
        }) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    recyclerViewAudience.setVisibility(View.GONE);
                    notFound.setVisibility(View.VISIBLE);

                } else {
                    recyclerViewAudience.setVisibility(View.VISIBLE);
                    notFound.setVisibility(View.GONE);

                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e("FirebaseFirestoreException", "onError: "+e );
            }


        };
        recyclerViewAudience.setAdapter(mAdapter1);
        mAdapter1.setQuery(mQuery);


        bottomSheetDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void showCustomAlertDialog(Context context, DocumentSnapshot user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.custom_dialog_layout, null);
        // Find the ImageView by its ID in the custom layout
        ImageView profile_image = view.findViewById(R.id.profile_image);
        TextView txt_username = view.findViewById(R.id.txt_username);
        TextView txt_uid = view.findViewById(R.id.txt_uid);
        MaterialButton btnKickOut = view.findViewById(R.id.btnKickOut);
        MaterialButton btnSetAdmin = view.findViewById(R.id.btnSetAdmin);
        MaterialButton btnMute = view.findViewById(R.id.btnMute);
        MaterialButton btnClose = view.findViewById(R.id.btnClose);
        LinearLayout ll_firstView1 = view.findViewById(R.id.ll_firstView1);
        LinearLayout ll_firstView2 = view.findViewById(R.id.ll_firstView2);

        try {
            Glide.with(context).load(user.getString("image")).into(profile_image);
            txt_username.setText(user.getString("username"));
            txt_uid.setText("ID : "+user.getString("uid"));

            ZEGOSDKUser hostUser= ZEGOLiveStreamingManager.getInstance().getHostUser();
            if (hostUser != null) {
                ll_firstView1.setVisibility(View.VISIBLE);
                ll_firstView2.setVisibility(View.VISIBLE);
            }else {
                ll_firstView1.setVisibility(View.GONE);
                ll_firstView2.setVisibility(View.GONE);
            }

        }catch (Exception e){

        }
        // Set the custom view
        builder.setView(view);
        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        btnClose.setOnClickListener(v -> {

            dialog.dismiss();
        });
        btnKickOut.setOnClickListener(v -> {
            confirmKickOut(user.getString("liveId"),user.getString("uid"),user.getString("userId"),dialog);
//            dialog.dismiss();
        });
        btnMute.setOnClickListener(v -> {
            dialog.dismiss();
        });
        btnSetAdmin.setOnClickListener(v -> {
            Toast.makeText(context, "coming soon...!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });


        dialog.show();
    }
    private void confirmKickOut(String liveId, String uid, String userId, AlertDialog dialog1) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LiveStreamingActivity.this);


        builder.setMessage("Are you sure want to kickOut this user?");

        // Set a positive button and its click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new AddStreamInfo().deleteJoinedRoomUser(liveId,userId);
                new AddStreamInfo().addStreamKickOut(liveId,uid,userId);
                // Do something when the OK button is clicked
                dialog.dismiss();
                dialog1.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog1.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void loginRoom() {

        ZEGOSDKManager.getInstance().loginRoom(liveID, ZegoScenario.BROADCAST, new ZEGOSDKCallBack() {
            @Override
            public void onResult(int errorCode, String message) {
                if (errorCode != 0) {
                    onJoinRoomFailed();
                } else {
                    onJoinRoomSuccess();

                    boolean isHost = getIntent().getBooleanExtra("host", true);
                    // save live data
                    if (isHost){
                        saveLiveData(userId,uid,username,true,liveID,"0",country,image);
                        new AddStreamInfo().roomActive(liveID, String.valueOf(uid),userId);
                        new AddStreamInfo().addStreamInfo(liveID, String.valueOf(uid),userId, username, image);
                        new SendGlobalMessage(liveID,"welcome to Pretty live","video");

                    }

                }
            }
        });
    }

    private void onJoinRoomFailed() {
        finish();
    }

    private void onJoinRoomSuccess() {
        binding.topView.setVisibility(View.VISIBLE);
        binding.previewStart.setVisibility(View.GONE);
        binding.previewBeauty.setVisibility(View.GONE);
        binding.messageBtn.setVisibility(View.VISIBLE);
        binding.gameViewPager.setVisibility(View.VISIBLE);
        binding.recyclerAllMessage.setVisibility(View.VISIBLE);
        binding.liveBottomMenuBar.setVisibility(View.VISIBLE);
        binding.welcomeText.setVisibility(View.VISIBLE);


        startMarqueeAnimation1();
//        giftButton.post(new Runnable() {
//            @Override
//            public void run() {
//                giftButton.performClick();
//            }
//        });


        boolean isHost = getIntent().getBooleanExtra("host", true);
        if (isHost) {
            ZEGOLiveStreamingManager.getInstance().startPublishingStream();
            // Call the FCMNotificationSender's sendNotification method
            FCMNotificationSender.sendNotificationToDevice("deviceToken", "PrettyLive",""+username+"!!"+" started videoLive" );
            binding.giftButton.setVisibility(View.VISIBLE);
        }else {
            binding.giftButton.setVisibility(View.VISIBLE);
        }

        ZEGOSDKManager.getInstance().expressService.startSoundLevelMonitor();

        int width = binding.getRoot().getWidth() / 4;
        binding.pkOtherVideoIcon.setCircleBackgroundRadius(width / 2);
        binding.pkSelfVideoIcon.setCircleBackgroundRadius(width / 2);
        binding.audienceMixSelfIcon.setCircleBackgroundRadius(width / 2);
        binding.audienceMixOtherIcon.setCircleBackgroundRadius(width / 2);
        binding.mainHostVideoIcon.setCircleBackgroundRadius(width);

        if (!Objects.equals(docId, "")){
            checkGiftExpiration(docId);
        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                binding.welcomeText.setVisibility(View.GONE);
//            }
//        },8000);
    }

    private void checkGiftExpiration(String documentId) {
        try {
            DocumentReference giftDocument = FirebaseFirestore.getInstance().collection("purchaseGift").document(documentId);
            giftDocument.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Date endDate = documentSnapshot.getDate("endDate");
                            purchageGiftModel=documentSnapshot.toObject(PurchageGiftModel.class);
                            if (endDate != null) {
                                // Compare the endDate with the current date
                                if (isGiftExpired(endDate)) {
                                    Toast.makeText(this, "Entry gift has been expired", Toast.LENGTH_SHORT).show();
                                    // Gift has expired, handle accordingly (e.g., show an expired message)
                                } else {
                                    Toast.makeText(this, "Gift is still valid", Toast.LENGTH_SHORT).show();
                                    sendVipGiftEntry(purchageGiftModel);
                                }
                            }
                        } else {
                            // Document does not exist, handle accordingly
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure (e.g., show an error message)
                    });
        }catch (Exception e){

        }
    }

    private void sendVipGiftEntry(PurchageGiftModel purchageGiftModel) {
        String liveType="1";
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("fileName", purchageGiftModel.getFileName());
            data.put("giftCoin", purchageGiftModel.getGiftCoin());
            data.put("userId", otherUserId);
            data.put("giftId", purchageGiftModel.getGiftId());
            data.put("liveType", liveType);
            data.put("gift_count","1");
            data.put("liveId", liveID);
            String key = ref.push().getKey();
            ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").child(key).setValue(data);
        }catch (Exception e){

        }

    }

    private boolean isGiftExpired(Date endDate) {
        // Get the current date
        Date currentDate = new Date();
        Log.i("isGiftExpired", "isGiftExpired: "+currentDate);

        // Compare the endDate with the current date
        return currentDate.after(endDate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }

        // Start listening for Firestore updates
        if (giftAdapter != null) {
            giftAdapter.startListening();
        }
        // Start listening for Firestore updates
        if (mAdapter1 != null) {
            mAdapter1.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        // Start listening for Firestore updates
        if (giftAdapter != null) {
            giftAdapter.stopListening();
        }
        // Start listening for Firestore updates
        if (mAdapter1 != null) {
            mAdapter1.stopListening();
        }


    }



    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            ZEGOLiveStreamingManager.getInstance().leave();
                ZEGOSDKManager.getInstance().disconnectUser();
                ZEGOLiveStreamingManager.getInstance().removeUserData();
                ZEGOLiveStreamingManager.getInstance().removeUserListeners();
//                ZEGOSDKManager.getInstance().expressService.openCamera(false);
//                ZEGOSDKManager.getInstance().expressService.stopPreview();

            boolean isHost = getIntent().getBooleanExtra("host", true);
            if (isHost){
                updateLiveStatus(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
                updateRoomStatus(otherUserId,liveID);
            }
        }
    }

    private void updateRoomStatus(String userId,String mainStreamID) {
        // Reference to the Firestore collection

        Map<String, Object> updatedStreamInfo = new HashMap<>();
        updatedStreamInfo.put("active", "no");

        // Update the document with the new information
        firestore.collection(Constant.ROOM_STATUS)
                .document(mainStreamID)
                .collection("current_room_user")
                .document(userId)
                .update(updatedStreamInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("room_users", "onSuccess: Update successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("room_users", "onFailure: Update failed: " + e.getMessage());
                    }
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        // Start listening for Firestore updates
        if (giftAdapter != null) {
            giftAdapter.stopListening();
        }
        // Start listening for Firestore updates
        if (mAdapter1 != null) {
            mAdapter1.stopListening();
        }
        binding=null;
    }

    private void updateLiveStatus(String userId) {
        // Reference to the Firestore collection
        CollectionReference liveDetailsRef = firestore.collection(Constant.LIVE_DETAILS);

        // Create a query to find the document with the given userId
        Query query = liveDetailsRef.whereEqualTo("userId", userId).whereEqualTo("liveID",liveID);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get the document ID for the matched document
                    String documentId = document.getId();

//                    long timestamp = System.currentTimeMillis();
                    Date currentDate = new Date();
                    long timestamp = currentDate.getTime();
                    Map<String, Object> updateDetails = new HashMap<>();
                    updateDetails.put("liveStatus", "offline");
                    updateDetails.put("endTime", timestamp);
                    updateDetails.put("endDate", getFormattedDateTime());

                    // Update the liveType field from 0 to 1
                    liveDetailsRef.document(documentId)
                            .update(updateDetails)
                            .addOnSuccessListener(aVoid -> {
                                ApplicationClass.getSharedpref().saveString(AppConstants.ROOM_ID,"");
                                Log.i("UpdateLiveType", "liveType updated successfully for user with ID: " + userId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UpdateLiveType", "Error updating liveType for user with ID: " + userId, e);
                            });
                }
            } else {
                Log.e("UpdateLiveType", "Error getting documents: ", task.getException());
            }
        });
    }


    private void startMarqueeAnimation1() {
        // Calculate the duration of the animation based on the length of the text
        int textLength = binding.welcomeText.getText().length();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        long duration = (textLength * 1000) / screenWidth * 300; // Adjust the factor as needed

        // Create a translate animation
        TranslateAnimation animation = new TranslateAnimation(
                screenWidth,          // fromXDelta
                -screenWidth,         // toXDelta
                0,                    // fromYDelta
                0                     // toYDelta
        );

        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(duration);
        animation.setRepeatCount(1);
        animation.setFillAfter(true);

        // Set animation listener if needed
        animation.setAnimationListener(new TranslateAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                // Handle animation end if needed
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

                // Handle animation repeat if needed
            }
        });

        // Start the animation
        binding.welcomeText.startAnimation(animation);
    }

    public void listenSDKEvent() {
        ZEGOSDKManager.getInstance().expressService.addEventHandler(new IExpressEngineEventHandler() {


            @Override
            public void onCameraOpen(String userID, boolean open) {
                onRoomUserCameraOpen(userID, open);
            }

            @Override
            public void onReceiveStreamAdd(List<ZEGOSDKUser> userList) {
                List<ZEGOSDKUser> coHostUserList = new ArrayList<>();
                for (ZEGOSDKUser zegosdkUser : userList) {
                    if (ZEGOLiveStreamingManager.getInstance().isHost(zegosdkUser.userID)) {
                        binding.mainHostVideo.setUserID(zegosdkUser.userID);
                        binding.mainHostVideoIcon.setLetter(zegosdkUser.userName);
                        binding.mainHostVideo.setStreamID(zegosdkUser.getMainStreamID());
                        if (ZEGOLiveStreamingManager.getInstance().getPKInfo() == null) {
                            binding.mainHostVideo.setVisibility(View.VISIBLE);
                            binding.mainHostVideoLayout.setVisibility(View.VISIBLE);
                            binding.mainHostVideo.startPlayRemoteAudioVideo();
                        }
                    } else {
                        if (ZEGOLiveStreamingManager.getInstance().getPKInfo() == null) {
                            coHostUserList.add(zegosdkUser);
                        }
                        try {
                            String uid = zegosdkUser.userID;
                            String userName = zegosdkUser.userName;
                            usersRef.whereEqualTo("uid", Long.parseLong(uid))
                                    .addSnapshotListener((value, error) -> {
                                        if (error != null) {
                                            // Handle error
                                            Log.e("test2334", "Listen failed: " + error.getMessage());
                                            return;
                                        }

                                        assert value != null;

                                        for (DocumentSnapshot document : value) {
                                            String userId =document.getString("userId");
                                            String image1 =document.getString("image");
                                            new AddStreamInfo().addStreamInfo(liveID,uid,userId, userName, image1);
                                        }

                                    });
                        } catch (Exception e) {
                            // Handle exception
                        }
                    }
                }
                if (ZEGOLiveStreamingManager.getInstance().getPKInfo() == null) {
                    binding.liveCohostView.addUser(coHostUserList);
                }
            }

            @Override
            public void onReceiveStreamRemove(List<ZEGOSDKUser> userList) {
                List<ZEGOSDKUser> coHostUserList = new ArrayList<>();
                for (ZEGOSDKUser ZEGOSDKUser : userList) {
                    if (Objects.equals(binding.mainHostVideo.getUserID(), ZEGOSDKUser.userID)) {
                        binding.mainHostVideo.stopPlayRemoteAudioVideo();
                        binding.mainHostVideo.setStreamID("");
                        binding.mainHostVideo.setUserID("");
                        binding.mainHostVideoIcon.setLetter("");
                        binding.mainHostVideoLayout.setVisibility(View.GONE);

                    } else {
                        coHostUserList.add(ZEGOSDKUser);
                    }
                    try {


                        String uid = ZEGOSDKUser.userID;
                        usersRef.whereEqualTo("uid", Long.parseLong(uid))
                                .addSnapshotListener((value, error) -> {
                                    if (error != null) {
                                        // Handle error
                                        Log.e("test2334", "Listen failed: " + error.getMessage());
                                        return;
                                    }

                                    assert value != null;

                                    for (DocumentSnapshot document : value) {
                                        String userId =document.getString("userId");
                                        new AddStreamInfo().deleteStreamInfo(liveID,userId);
                                    }

                                });

                    }catch (Exception e){

                    }
                }
                binding.liveCohostView.removeUser(coHostUserList);
            }

            @Override
            public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode,
                                               JSONObject extendedData) {
                super.onPublisherStateUpdate(streamID, state, errorCode, extendedData);
                ZEGOSDKUser currentUser = ZEGOSDKManager.getInstance().expressService.getCurrentUser();
                if (state == ZegoPublisherState.PUBLISHING) {
                    if (ZEGOLiveStreamingManager.getInstance().isCoHost(currentUser.userID)) {
                        binding.liveCohostView.addUser(currentUser);
                    } else if (ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {

                        binding.mainHostVideo.setUserID(currentUser.userID);
                        binding.mainHostVideoIcon.setLetter(currentUser.userName);
                        binding.mainHostVideo.setStreamID(streamID);
                        if (ZEGOLiveStreamingManager.getInstance().getPKInfo() == null) {
                            binding.mainHostVideoLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (state == ZegoPublisherState.NO_PUBLISH) {
                    if (streamID.endsWith("_host")) {
                        binding.mainHostVideo.setUserID("");
                        binding.mainHostVideoIcon.setLetter("");
                        binding.mainHostVideo.setStreamID("");
                        binding.mainHostVideo.stopPublishAudioVideo();
                        binding.mainHostVideoLayout.setVisibility(View.GONE);
                    } else {
                        binding.liveCohostView.removeUser(currentUser);
                    }
                }
            }

            @Override
            public void onUserLeft(List<ZEGOSDKUser> userList) {
                new SendGlobalMessage(liveID,"left the room","video");

                Log.i("EnteredUser", "onUserLeft:uid =======" + userList.get(0).userID);
                if (ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
                    for (ZEGOSDKUser zegosdkUser : userList) {
                        deleteUserFromViewersCollection(liveID,zegosdkUser.userID);
                    }
                }

                if (!ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
                    PKInfo pkInfo = ZEGOLiveStreamingManager.getInstance().getPKInfo();
                    if (pkInfo != null) {
                        for (ZEGOSDKUser zegosdkUser : userList) {
                            if (zegosdkUser.userID.equals(pkInfo.hostUserID)) {
                                ZEGOLiveStreamingManager.getInstance().stopPKBattle();
                            }
                        }
                    }
                }
            }

            @Override
            public void onUserEnter(List<ZEGOSDKUser> userList) {

                if (!ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
                    Log.i("EnteredUser", "liveId:---------- " + liveID);
                    Log.i("EnteredUser", "userId: ----------------" + userModel.getUserId());
                    Log.i("EnteredUser", "uid: ----------------" + userModel.getUid());
                    saveRoomUsers(userModel);
                    for (ZEGOSDKUser zegosdkUser : userList) {
                        new SendGlobalMessage(liveID,"joined the room","video");

//                        ZEGOSDKManager.getInstance().expressService.sendBarrageMessage(zegosdkUser.userName + " joined the room", (errorCode, messageID) -> {
//
//                        });
                    }
                }

//                if (!ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
                    for (ZEGOSDKUser zegosdkUser : userList) {
//                        Log.i("EnteredUser", "onUserEnter: "+zegosdkUser.userID);
                        if(ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()){
                            binding.txtEnterUser.setText(zegosdkUser.userName+" joined");
                            binding.entryText.setVisibility(View.VISIBLE);
                            binding.entryText.setAnimation(bottomAnimation);
                        }
                    }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.entryText.setVisibility(View.GONE);
                    }
                },2000);

//                    PKInfo pkInfo = ZEGOLiveStreamingManager.getInstance().getPKInfo();
//                    if (pkInfo != null) {
//                        for (ZEGOSDKUser zegosdkUser : userList) {
//                            if (zegosdkUser.userID.equals(pkInfo.hostUserID)) {
//                                ZEGOLiveStreamingManager.getInstance().stopPKBattle();
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onRoomStateChanged(String roomID, ZegoRoomStateChangedReason reason, int errorCode,
                                           JSONObject extendedData) {
                if (reason == ZegoRoomStateChangedReason.RECONNECT_FAILED) {
                    if (zimReconnectDialog != null && zimReconnectDialog.isShowing()) {
                        zimReconnectDialog.dismiss();
                    }
                    AlertDialog.Builder builder = new Builder(LiveStreamingActivity.this);
                    builder.setTitle("ZEGO SDK Disconnected");
                    builder.setMessage("Reconnected ?");
                    builder.setPositiveButton(R.string.ok, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ZEGOSDKUser currentUser = ZEGOSDKManager.getInstance().expressService.getCurrentUser();

                            ZEGOSDKManager.getInstance().expressService.removeRoomData();
                            ZEGOSDKManager.getInstance().expressService.removeUserData();
                            ZEGOSDKManager.getInstance().zimService.removeRoomData();
                            ZEGOSDKManager.getInstance().zimService.removeUserData();
                            ZEGOLiveStreamingManager.getInstance().removeUserData();
                            ZEGOLiveStreamingManager.getInstance().removeRoomData();

                            ZEGOSDKManager.getInstance().zimService.connectUser(currentUser.userID,
                                    currentUser.userName, errorInfo -> {
                                        if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                                            ZEGOSDKManager.getInstance().zimService.loginRoom(roomID,
                                                    new ZIMRoomEnteredCallback() {
                                                        @Override
                                                        public void onRoomEntered(ZIMRoomFullInfo roomInfo,
                                                                                  ZIMError errorInfo) {
                                                            ZEGOSDKManager.getInstance().expressService.loginRoom(roomID,
                                                                    new IZegoRoomLoginCallback() {
                                                                        @Override
                                                                        public void onRoomLoginResult(int errorCode1,
                                                                                                      JSONObject extendedData1) {

                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    builder.create().show();
                }
            }
        });
        ZEGOSDKManager.getInstance().zimService.addEventHandler(new IZIMEventHandler() {



            @Override
            public void onOutgoingRoomRequestAccepted(String requestID, String extendedData) {
                RoomRequestExtendedData data = RoomRequestExtendedData.parse(extendedData);
                if (data != null && data.roomRequestType == RoomRequestType.REQUEST_COHOST) {
                    ExpressService expressService = ZEGOSDKManager.getInstance().expressService;
                    ZEGOSDKUser currentUser = expressService.getCurrentUser();
                    if (ZEGOLiveStreamingManager.getInstance().isAudience(currentUser.userID)) {
                        List<String> permissions = Arrays.asList(permission.CAMERA, permission.RECORD_AUDIO);
                        requestPermissionIfNeeded(permissions, new RequestCallback() {

                            @Override
                            public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                                 @NonNull List<String> deniedList) {
                                ZEGOLiveStreamingManager.getInstance().startCoHost();
                            }
                        });
                    }
                }
            }

            @Override
            public void onConnectionStateChanged(ZIM zim, ZIMConnectionState state, ZIMConnectionEvent event,
                                                 JSONObject extendedData) {
                if (state == ZIMConnectionState.DISCONNECTED) {
                    AlertDialog.Builder builder = new Builder(LiveStreamingActivity.this);
                    builder.setTitle("ZIM DisConnected");
                    builder.setMessage("Reconnected ?");
                    builder.setPositiveButton(R.string.ok, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ZEGOSDKUser currentUser = ZEGOSDKManager.getInstance().expressService.getCurrentUser();
                            String roomID = ZEGOSDKManager.getInstance().expressService.getCurrentRoomID();

                            ZEGOSDKManager.getInstance().zimService.removeRoomData();
                            ZEGOSDKManager.getInstance().zimService.removeUserData();
                            ZEGOLiveStreamingManager.getInstance().removeUserData();
                            ZEGOLiveStreamingManager.getInstance().removeRoomData();
                            ZEGOSDKManager.getInstance().zimService.connectUser(currentUser.userID,
                                    currentUser.userName, new ZIMLoggedInCallback() {
                                        @Override
                                        public void onLoggedIn(ZIMError errorInfo) {
                                            ZEGOSDKManager.getInstance().zimService.loginRoom(roomID,
                                                    new ZIMRoomEnteredCallback() {
                                                        @Override
                                                        public void onRoomEntered(ZIMRoomFullInfo roomInfo,
                                                                                  ZIMError errorInfo) {

                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    zimReconnectDialog = builder.create();
                    zimReconnectDialog.show();
                }
            }




        });

        //        ZEGOSDKManager.getInstance().zimService.addIncomingRoomRequestListener(new IncomingRoomRequestListener() {
        //            @Override
        //            public void onInComingRoomRequestReceived(RoomRequest request) {
        //                ZEGOSDKUser currentUser = ZEGOSDKManager.getInstance().expressService.getcurrentUser();
        //                if (ZEGOLiveStreamingManager.getInstance().isAudience(currentUser.userID)) {
        //                    if (inviteCoHostDialog == null) {
        //                        Builder builder = new Builder(LiveStreamingActivity.this);
        //                        builder.setTitle("you received a new invitation");
        //
        //                        ZEGOSDKUser inviter = ZEGOSDKManager.getInstance().expressService.getUser(request.sender);
        //                        if (inviter != null) {
        //                            builder.setMessage(inviter.userName + " invite you to CoHost");
        //                        }
        //                        builder.setPositiveButton(R.string.ok, new OnClickListener() {
        //                            @Override
        //                            public void onClick(DialogInterface dialog, int which) {
        //                                ExpressService expressService = ZEGOSDKManager.getInstance().expressService;
        //                                expressService.openMicrophone(true);
        //                                expressService.enableCamera(true);
        //                                expressService.startPublishLocalAudioVideo();
        //                                dialog.dismiss();
        //                            }
        //                        });
        //                        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
        //                            @Override
        //                            public void onClick(DialogInterface dialog, int which) {
        //                                dialog.dismiss();
        //                            }
        //                        });
        //                        inviteCoHostDialog = builder.create();
        //                    }
        //                    if (!inviteCoHostDialog.isShowing()) {
        //                        inviteCoHostDialog.show();
        //                    }
        //                }
        //            }
        //
        //            @Override
        //            public void onInComingRoomRequestCancelled(RoomRequest request) {
        //                if (inviteCoHostDialog != null && inviteCoHostDialog.isShowing()) {
        //                    inviteCoHostDialog.dismiss();
        //                }
        //            }
        //
        //            @Override
        //            public void onActionAcceptIncomingRoomRequest(int errorCode, RoomRequest request) {
        //            }
        //
        //            @Override
        //            public void onActionRejectIncomingRoomRequest(int errorCode, RoomRequest request) {
        //            }
        //        });

        ZEGOLiveStreamingManager.getInstance().addLiveStreamingListener(new LiveStreamingListener() {

            @Override
            public void onReceiveStartPKRequest(String requestID, String inviter, String inviterName, String roomId) {
                if (startPKDialog != null && startPKDialog.isShowing()) {
                    return;
                }
                AlertDialog.Builder startPKBuilder = new AlertDialog.Builder(LiveStreamingActivity.this);
                startPKBuilder.setTitle(inviterName + " invite you pkbattle");
                startPKBuilder.setPositiveButton(R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PKInfo pkInfo = new PKInfo(new ZEGOSDKUser(inviter, inviterName), roomId);
                        ZEGOLiveStreamingManager.getInstance().setCurrentPKInfo(pkInfo);
                        ZEGOLiveStreamingManager.getInstance().acceptPKBattleStartRequest(requestID);
                    }
                });
                startPKBuilder.setNegativeButton(R.string.reject, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ZEGOLiveStreamingManager.getInstance().rejectPKBattleStartRequest(requestID);
                    }
                });
                startPKDialog = startPKBuilder.create();
                startPKDialog.setCanceledOnTouchOutside(false);
                startPKDialog.show();
            }

            @Override
            public void onReceiveStopPKRequest(String requestID) {
                PKInfo pkInfo = ZEGOLiveStreamingManager.getInstance().getPKInfo();
                ToastUtil.show(LiveStreamingActivity.this, pkInfo.pkUser.userName + " has Stopped PK");
            }

            @Override
            public void onInComingStartPKRequestTimeout(String requestID) {
                if (startPKDialog != null && startPKDialog.isShowing()) {
                    startPKDialog.dismiss();
                }
            }

            @Override
            public void onInComingStartPKRequestCancelled(String requestID) {
                if (startPKDialog != null && startPKDialog.isShowing()) {
                    startPKDialog.dismiss();
                }
            }

            @Override
            public void onPKStarted() {
                PKInfo pkInfo = ZEGOLiveStreamingManager.getInstance().getPKInfo();
                onRoomPKStarted(pkInfo);
            }

            @Override
            public void onPKEnded() {
                onRoomPKEnded();
            }

            @Override
            public void onPKSEITimeOut(String userID, boolean timeout) {
                if (ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
                    if (userID.equals(binding.pkOtherVideo.getUserID())) {
                        if (timeout) {
                            binding.pkOtherVideoTips.setVisibility(View.VISIBLE);
                            binding.pkOtherVideoMute.setVisibility(View.GONE);
                            binding.pkOtherVideo.mutePlayAudio(true);
                            boolean pkUserMuted = ZEGOLiveStreamingManager.getInstance().isPKUserMuted();
                            if (!pkUserMuted) {
                                ZEGOLiveStreamingManager.getInstance().mutePKUser(true, new IZegoMixerStartCallback() {
                                    @Override
                                    public void onMixerStartResult(int errorCode, JSONObject extendedData) {
                                        if (errorCode == 0) {
                                            binding.pkOtherVideoMute.setText("Unmute user");
                                        }
                                    }
                                });
                            }
                        } else {
                            binding.pkOtherVideoTips.setVisibility(View.GONE);
                            binding.pkOtherVideoMute.setVisibility(View.VISIBLE);
                            binding.pkOtherVideo.mutePlayAudio(false);
                            boolean pkUserMuted = ZEGOLiveStreamingManager.getInstance().isPKUserMuted();
                            if (pkUserMuted) {
                                ZEGOLiveStreamingManager.getInstance().mutePKUser(false, new IZegoMixerStartCallback() {
                                    @Override
                                    public void onMixerStartResult(int errorCode, JSONObject extendedData) {
                                        if (errorCode == 0) {
                                            binding.pkOtherVideoMute.setText("Mute user");
                                        }
                                    }
                                });
                            }
                        }
                    }
                } else {
                    if (ZEGOLiveStreamingManager.getInstance().isHost(userID)) {
                        if (timeout) {
                            if (binding.audienceMixSelfTips.getVisibility() != View.VISIBLE) {
                                binding.audienceMixSelfTips.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (binding.audienceMixSelfTips.getVisibility() != View.GONE) {
                                binding.audienceMixSelfTips.setVisibility(View.GONE);
                            }
                        }
                    } else if (ZEGOLiveStreamingManager.getInstance().isPKUser(userID)) {
                        if (timeout) {
                            if (binding.audienceMixOtherTips.getVisibility() != View.VISIBLE) {
                                binding.audienceMixOtherTips.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (binding.audienceMixOtherTips.getVisibility() != View.GONE) {
                                binding.audienceMixOtherTips.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onPKCameraOpen(String userID, boolean open) {
                if (ZEGOLiveStreamingManager.getInstance().isPKUser(userID)) {
                    onPKUserCameraUpdate(userID, open);
                } else if (ZEGOLiveStreamingManager.getInstance().isHost(userID)) {
                    onHostCameraUpdate(open);
                }
            }

            @Override
            public void onPKMicrophoneOpen(String userID, boolean open) {
            }
        });
    }

    private void deleteUserFromViewersCollection(String streamId, String uid) {


        usersRef.whereEqualTo("uid", Long.parseLong(uid))
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Log.e("test2334", "Listen failed: " + error.getMessage());
                        return;
                    }
                    String userid="";
                    for (DocumentSnapshot document : value) {

                        userid=document.getString("userId");
                        Log.i("deleteUserFromViewersCollection", "deleteUserFromViewersCollection: "+userid);
//
                    }
                    firestore.collection("room_users")
                            .document(streamId)
                            .collection("viewers")
                            .document(userid)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.i("delete_user", "User deleted from viewers collection successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("delete_user", "Failed to delete user from viewers collection: " + e.getMessage());
                                }
                            });
                });



    }

    private void saveRoomUsers(UserModel userDetails) {

//        long timestamp = System.currentTimeMillis();
        Date currentDate = new Date();
        long timestamp = currentDate.getTime();
        RoomUsers roomUsers = new RoomUsers();
        roomUsers.setLiveId(liveID);
        roomUsers.setUserId(userDetails.getUserId());
        roomUsers.setUid(String.valueOf(userDetails.getUid()));
        roomUsers.setUsername(userDetails.getUsername());
        roomUsers.setCountry_name(userDetails.getCountry_name());
        roomUsers.setImage(userDetails.getImage());
        roomUsers.setLevel(userDetails.getLevel());
        roomUsers.setFriends(userDetails.getFriends());
        roomUsers.setFollowers(userDetails.getFollowers());
        roomUsers.setFollowing(userDetails.getFollowing());
        roomUsers.setCoins(userDetails.getCoins());
        roomUsers.setTime(timestamp);

        firestore.collection("room_users").document(liveID).collection("viewers").document(userDetails.getUserId()).set(roomUsers).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.i("room_users", "onSuccess: done");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("room_users", "Exception: err"+e);
            }
        });

//        firestore.collection("room_users").document(streamId).set(roomUsers).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//
//                Log.i("room_users", "onSuccess: done");
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i("room_users", "Exception: err"+e);
//            }
//        });

        // Add the login details to Firestore
//        firestore.collection("room_users")
//                .add(roomUsers)
//                .addOnSuccessListener(documentReference -> {
//                    // Login details added successfully
//                    Toast.makeText(LiveStreamingActivity.this, "saved",
//                            Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(LiveStreamingActivity.this, "Error : "+e,Toast.LENGTH_SHORT).show();
//                    // Handle failure
//                    Log.e("LiveStreamingActivity", "Error adding login details", e);
//                });
    }

    private void onRoomUserCameraOpen(String userID, boolean open) {
        if (ZEGOLiveStreamingManager.getInstance().getPKInfo() == null) {
            if (ZEGOLiveStreamingManager.getInstance().isHost(userID)) {
                if (open) {
                    binding.mainHostVideo.setVisibility(View.VISIBLE);
                    binding.mainHostVideoIcon.setVisibility(View.GONE);
                } else {
                    binding.mainHostVideo.setVisibility(View.INVISIBLE);
                    binding.mainHostVideoIcon.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (ZEGOLiveStreamingManager.getInstance().isHost(userID)) {
                onHostCameraUpdate(open);
            }
        }
    }

    private void onRoomPKStarted(PKInfo pkInfo) {
        ZEGOSDKUser currentUser = ZEGOSDKManager.getInstance().expressService.getCurrentUser();

        binding.liveCohostView.setVisibility(View.GONE);
        binding.pkVideoLayout.setVisibility(View.VISIBLE);
        binding.mainHostVideoLayout.setVisibility(View.GONE);

        ZEGOSDKUser hostUser = ZEGOLiveStreamingManager.getInstance().getHostUser();
        if (ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
            binding.pkOtherVideoLayout.setVisibility(View.VISIBLE);
            binding.pkSelfVideoLayout.setVisibility(View.VISIBLE);
            binding.pkOtherVideo.setUserID(pkInfo.pkUser.userID);
            binding.pkOtherVideoIcon.setLetter(pkInfo.pkUser.userName);
            binding.pkOtherVideo.setStreamID(pkInfo.getPKStream());
            binding.pkOtherVideo.startPlayRemoteAudioVideo();

            binding.pkSelfVideo.setUserID(currentUser.userID);
            binding.pkSelfVideoIcon.setLetter(currentUser.userName);
            binding.pkSelfVideo.startPreviewOnly();
        } else {

            binding.pkOtherVideoLayout.setVisibility(View.INVISIBLE);
            binding.pkSelfVideoLayout.setVisibility(View.INVISIBLE);

            binding.audienceMixSelfIcon.setLetter(hostUser.userName);
            binding.audienceMixOtherIcon.setLetter(pkInfo.pkUser.userName);

            String streamID = ZEGOSDKManager.getInstance().expressService.getCurrentRoomID() + "_mix";
            binding.audienceMixVideo.setStreamID(streamID);
            binding.audienceMixVideo.startPlayRemoteAudioVideo();


        }

        onHostCameraUpdate(hostUser.isCameraOpen());

        binding.pkOtherVideoMute.setOnClickListener(v -> {
            boolean pkUserMuted = ZEGOLiveStreamingManager.getInstance().isPKUserMuted();
            ZEGOLiveStreamingManager.getInstance().mutePKUser(!pkUserMuted, new IZegoMixerStartCallback() {
                @Override
                public void onMixerStartResult(int errorCode, JSONObject extendedData) {
                    if (errorCode == 0) {
                        if (pkUserMuted) {
                            binding.pkOtherVideoMute.setText("Mute user");
                        } else {
                            binding.pkOtherVideoMute.setText("Unmute user");
                        }
                    }
                }
            });
        });
    }

    private void onRoomPKEnded() {
        ZEGOSDKUser hostUser = ZEGOLiveStreamingManager.getInstance().getHostUser();
        binding.pkVideoLayout.setVisibility(View.INVISIBLE);
        if (hostUser != null) {
            binding.mainHostVideoLayout.setVisibility(View.VISIBLE);
        }
        binding.liveCohostView.setVisibility(View.VISIBLE);

        if (ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
            binding.pkOtherVideo.stopPlayRemoteAudioVideo();
            binding.pkOtherVideo.setUserID("");
            binding.pkOtherVideoIcon.setLetter("");
            binding.pkOtherVideo.setStreamID("");

            binding.mainHostVideo.startPreviewOnly();
        } else {
            binding.audienceMixVideo.stopPlayRemoteAudioVideo();
            binding.audienceMixVideo.setStreamID("");

            if (hostUser != null) {
                String hostMainStreamID = hostUser.getMainStreamID();
                if (hostMainStreamID != null) {
                    binding.mainHostVideo.startPlayRemoteAudioVideo();
                }
            }
        }

        if (hostUser != null) {
            onRoomUserCameraOpen(hostUser.userID, hostUser.isCameraOpen());
        }
    }

    private void onPKUserCameraUpdate(String userID, boolean open) {
        if (ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
            if (open) {
                binding.pkOtherVideoIcon.setVisibility(View.INVISIBLE);
                binding.pkOtherVideo.setVisibility(View.VISIBLE);
            } else {
                binding.pkOtherVideoIcon.setVisibility(View.VISIBLE);
                binding.pkOtherVideo.setVisibility(View.INVISIBLE);
            }
        } else {
            if (open) {
                binding.audienceMixOtherIcon.setVisibility(View.INVISIBLE);
            } else {
                binding.audienceMixOtherIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    private void onHostCameraUpdate(boolean open) {
        if (ZEGOLiveStreamingManager.getInstance().isCurrentUserHost()) {
            if (open) {
                binding.pkSelfVideoIcon.setVisibility(View.INVISIBLE);
                binding.pkSelfVideo.setVisibility(View.VISIBLE);
            } else {
                binding.pkSelfVideoIcon.setVisibility(View.VISIBLE);
                binding.pkSelfVideo.setVisibility(View.INVISIBLE);
            }
        } else {
            if (open) {
                binding.audienceMixSelfIcon.setVisibility(View.INVISIBLE);
            } else {
                binding.audienceMixSelfIcon.setVisibility(View.VISIBLE);
            }
        }
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
                if (deniedList.contains(permission.CAMERA)) {
                    message = this.getString(R.string.permission_explain_camera);
                } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.permission_explain_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(permission.CAMERA)) {
                        message = this.getString(R.string.permission_explain_camera);
                    } else if (deniedList.contains(permission.RECORD_AUDIO)) {
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
                if (deniedList.contains(permission.CAMERA)) {
                    message = this.getString(R.string.settings_camera);
                } else if (deniedList.contains(permission.RECORD_AUDIO)) {
                    message = this.getString(R.string.settings_mic);
                }
            } else {
                if (deniedList.size() == 1) {
                    if (deniedList.contains(permission.CAMERA)) {
                        message = this.getString(R.string.settings_camera);
                    } else if (deniedList.contains(permission.RECORD_AUDIO)) {
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