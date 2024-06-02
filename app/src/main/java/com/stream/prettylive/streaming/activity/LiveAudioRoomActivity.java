package com.stream.prettylive.streaming.activity;

import static com.stream.prettylive.global.DateUtils.getFormattedDateTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.opensource.svgaplayer.SVGASoundManager;
import com.opensource.svgaplayer.utils.log.SVGALogger;
import com.stream.prettylive.R;
import com.stream.prettylive.databinding.ActivityLiveAudioRoomBinding;
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
import com.stream.prettylive.streaming.activity.model.GiftModel;
import com.stream.prettylive.streaming.activity.model.PurchageGiftModel;
import com.stream.prettylive.streaming.activity.model.RoomUsers;
import com.stream.prettylive.streaming.components.message.barrage.BottomInputDialog;
import com.stream.prettylive.streaming.components.message.barrage.BottomInputDialogMessage;
import com.stream.prettylive.streaming.functions.AddStreamInfo;
import com.stream.prettylive.streaming.functions.CurrentUserInfo;
import com.stream.prettylive.streaming.functions.EndLiveStatus;
import com.stream.prettylive.streaming.functions.KickOutInfo;
import com.stream.prettylive.streaming.functions.SendGlobalMessage;
import com.stream.prettylive.streaming.functions.UserInfo;
import com.stream.prettylive.streaming.gift.GiftHelper;
import com.stream.prettylive.streaming.internal.ZEGOLiveAudioRoomManager;
import com.stream.prettylive.streaming.internal.business.RoomRequestExtendedData;
import com.stream.prettylive.streaming.internal.business.RoomRequestType;
import com.stream.prettylive.streaming.internal.business.audioroom.LiveAudioRoomLayoutConfig;
import com.stream.prettylive.streaming.internal.sdk.ZEGOSDKManager;
import com.stream.prettylive.streaming.internal.sdk.basic.ZEGOSDKCallBack;
import com.stream.prettylive.streaming.internal.sdk.basic.ZEGOSDKUser;
import com.stream.prettylive.streaming.internal.sdk.express.IExpressEngineEventHandler;
import com.stream.prettylive.streaming.internal.sdk.zim.IZIMEventHandler;
import com.stream.prettylive.streaming.internal.utils.ToastUtil;
import com.stream.prettylive.streaming.internal.utils.Utils;
import com.stream.prettylive.ui.home.HomeActivity;
import com.stream.prettylive.ui.home.ui.profile.models.UserDetailsModel;
import com.stream.prettylive.ui.home.ui.profile.models.UserModel;
import com.stream.prettylive.ui.utill.Constant;
import com.stream.prettylive.ui.utill.Convert;

import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoStreamResourceMode;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoPlayerConfig;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zim.callback.ZIMRoomAttributesOperatedCallback;
import im.zego.zim.entity.ZIMError;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class LiveAudioRoomActivity extends AppCompatActivity {

    private ActivityLiveAudioRoomBinding binding;
    private static final int LIMIT = 50;
    private FirebaseFirestore mFirestore;
    private Query mQuery,mQuery1,mQuery2;

    private String roomID;
    private String userId;
    private String userIdForReceiveGift="";
    private String receiverTotalCoins="";
    private String otherUserId;
    private String username;
    private String audienceId;
    private String country;
    private String image;
    private long uid;
    private LiveAudioRoomLayoutConfig seatLayoutConfig;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private CollectionReference usersRef;
    private UserDetailsModel userDetails;
    private String level;
    private String docId;

    View giftButton;
    private ArrayList<GiftModel> countryList;
    private GiftAdapter mAdapter;
    private ViewersListAdapter mAdapter1;
    private ViewUserAdapter viewUserAdapter;
    private GiftViewUserAdapter giftViewUserAdapter;
    private DocumentSnapshot documentSnapshot;
    private UserModel userModel;
    private String totalBeans="0";
    private int giftCount=1;
    private int select =1;
    private PurchageGiftModel purchageGiftModel;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = firebaseDatabase.getReference().child("userInfo");
    List<String>userIds = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private final List<ChatMessageModel> chatMessages = new ArrayList<>();
    private List<UserDetailsModel>topGamer= new ArrayList();
    private int mCurrentPage = 0;
    private Timer mTimer;
    private GameViewSwitcherAdapter gameViewSwitcherAdapter;

    String TAG = "LiveAudioRoomActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveAudioRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        usersRef = firestore.collection(Constant.LOGIN_DETAILS);

        SVGALogger.INSTANCE.setLogEnabled(true);
        SVGASoundManager.INSTANCE.init();



        mFirestore = FirebaseFirestore.getInstance();

        mQuery1 = mFirestore.collection(Constant.GIFTS)
                .orderBy("price", Query.Direction.ASCENDING)
//                .whereEqualTo("gift_type","1000")
                .limit(LIMIT);



        boolean isHost = getIntent().getBooleanExtra("host", true);
        userId = getIntent().getStringExtra("userId");
        otherUserId = getIntent().getStringExtra("userId");
        roomID = getIntent().getStringExtra("liveID");
        username = getIntent().getStringExtra("username");
        country = getIntent().getStringExtra("country_name");
        image = getIntent().getStringExtra("image");
        level = getIntent().getStringExtra("level");
        audienceId = getIntent().getStringExtra("audienceId");
        uid = getIntent().getLongExtra("uid",0);
        ApplicationClass.getSharedpref().saveString(AppConstants.ROOM_ID,roomID);
        if (TextUtils.isEmpty(roomID)) {
            finish();
            return;
        }

        if (!isHost){
            currentUserDetails(audienceId);
        }else {

        }

        mQuery = firestore.collection("room_users").document(roomID).collection("viewers")
//                .orderBy("uid", Query.Direction.DESCENDING)
                .whereNotEqualTo("userId", ApplicationClass.getSharedpref().getString(AppConstants.USER_ID))
                .limit(LIMIT);
        getCommentChatMessageFirebase();
        getUserCoins(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
        fetchUserDetails(userId);
        setViewersAdapter();

        commentAdapter = new CommentAdapter(this, chatMessages);
        binding.recyclerAllMessage.setAdapter(commentAdapter);

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exitDialog();
            }
        });

        binding.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomInputDialogMessage inputDialog = new BottomInputDialogMessage(LiveAudioRoomActivity.this,roomID,"audio");
                inputDialog.show();
            }
        });




        seatLayoutConfig = new LiveAudioRoomLayoutConfig();
        seatLayoutConfig.rowSpacing = Utils.dp2px(8, getResources().getDisplayMetrics());
        ZEGOLiveAudioRoomManager.getInstance().init(seatLayoutConfig);
        binding.seatContainer.setLayoutConfig(seatLayoutConfig);

        ZEGOSDKManager.getInstance().expressService.openCamera(false);
        ZEGOSDKManager.getInstance().expressService.addEventHandler(new IExpressEngineEventHandler() {


            @Override
            public void onUserEnter(List<ZEGOSDKUser> userList) {
                super.onUserEnter(userList);
                for (ZEGOSDKUser zegoUser : userList) {
                    Log.e("test23345", "onUserEnter : " + zegoUser.userName);
                }
            }

            @Override
            public void onUserLeft(List<ZEGOSDKUser> userList) {
                super.onUserLeft(userList);
                for (ZEGOSDKUser zegoUser : userList) {
                    Log.e("test23345", "onUserEnter : " + zegoUser.userName);
                    try {
                        new SendGlobalMessage(roomID,"left from live","audio");
//                        userInfo("left from live",mAuth.getUid());
//                        ZEGOSDKManager.getInstance().expressService.sendBarrageMessage(zegoUser.userName + " left the room", (errorCode, messageID) -> {
//
//                        });
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onReceiveStreamAdd(List<ZEGOSDKUser> userList) {
                super.onReceiveStreamAdd(userList);

                for (ZEGOSDKUser zegoUser : userList) {
                    try {

                        String mainStreamID = zegoUser.getMainStreamID(); // Assuming mainStreamID is accessible from ZEGOSDKUser
                        String uid = zegoUser.userID;
                        String userName = zegoUser.userName;
                        String image = ZEGOLiveAudioRoomManager.getInstance().getUserAvatar(zegoUser.userID);


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
                                        new AddStreamInfo().addStreamInfo(roomID,uid,userId, userName, image);
                                    }

                                });

                            try {
//                                ZEGOSDKManager.getInstance().expressService.sendBarrageMessage(zegoUser.userName + " joined the room", (errorCode, messageID) -> {
//
//                                });
                            }catch (Exception e){

                            }



                        Log.i("onRoomStreamUpdate123", "onReceiveStreamAdd: " + zegoUser.userName);
                    } catch (Exception e) {
                        // Handle exception
                    }
                }
            }

            @Override
            public void onReceiveStreamRemove(List<ZEGOSDKUser> userList) {
                super.onReceiveStreamRemove(userList);
                for(ZEGOSDKUser zegoUser : userList){
                    try {

                        String uid = zegoUser.userID;
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
                                        new AddStreamInfo().deleteStreamInfo(roomID,userId);
                                    }

                                });


                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList,
                                           JSONObject extendedData) {
                super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData);
                for (ZegoStream zegoStream : streamList) {

                    if (updateType == ZegoUpdateType.ADD) {
                        ZegoPlayerConfig config = new ZegoPlayerConfig();
                        config.resourceMode = ZegoStreamResourceMode.ONLY_RTC;
                        ZEGOSDKManager.getInstance().expressService.startPlayingStream(zegoStream.streamID, config);
                    } else {
                        ZEGOSDKManager.getInstance().expressService.stopPlayingStream(zegoStream.streamID);
                    }
                }
            }

            @Override
            public void onRoomStreamExtraInfoUpdate(String roomID, ArrayList<ZegoStream> streamList) {
                super.onRoomStreamExtraInfoUpdate(roomID, streamList);
                for(ZegoStream zegoUser : streamList){
                    try {
                        Log.i("onRoomStreamUpdate123", "onReceiveStreamRemove: "+zegoUser.user.userName);
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onRoomExtraInfoUpdate(String roomID, ArrayList<ZegoRoomExtraInfo> roomExtraInfoList) {
                super.onRoomExtraInfoUpdate(roomID, roomExtraInfoList);
                for(ZegoRoomExtraInfo zegoUser : roomExtraInfoList){
                    try {
                        Log.i("onRoomStreamUpdate123", "onReceiveStreamRemove: "+zegoUser.updateUser.userName);
                    }catch (Exception e){

                    }
                }
            }
        });
        ZegoScenario chatRoom = ZegoScenario.HIGH_QUALITY_CHATROOM;
        binding.topView.setVisibility(View.VISIBLE);
        ZEGOSDKManager.getInstance().loginRoom(roomID, chatRoom, new ZEGOSDKCallBack() {
            @Override
            public void onResult(int errorCode, String message) {
                if (errorCode != 0) {
                    Log.e(TAG, "onRoomLoginResult: error: " + errorCode);
                    finish();
                } else {

                    if (isHost) {
                        binding.giftButton1.setVisibility(View.VISIBLE);
                        // save live data
                        saveLiveData(userId,uid,username,true,roomID,"1",country,image);
                        new AddStreamInfo().roomActive(roomID, String.valueOf(uid),userId);
                        ZEGOLiveAudioRoomManager.getInstance().setHostAndLockSeat();
                        ZEGOLiveAudioRoomManager.getInstance().takeSeat(0, new ZIMRoomAttributesOperatedCallback() {
                            @Override
                            public void onRoomAttributesOperated(String roomID, ArrayList<String> errorKeys,
                                                                 ZIMError errorInfo) {

                            }
                        });
                        // Call the FCMNotificationSender's sendNotification method
                        FCMNotificationSender.sendNotificationToDevice("deviceToken", "PrettyLive",""+username+"!!"+" started AudioParty" );

                        try {
                            String mainStreamID = ZEGOLiveAudioRoomManager.getInstance().generateUserStreamID(String.valueOf(uid),roomID); // Assuming mainStreamID is accessible from ZEGOSDKUser
                            String uid1 = String.valueOf(uid);
                            String userName = username;
                            String image1 = image;
//                        String image = ZEGOLiveAudioRoomManager.getInstance().generateUserStreamID("","");
//                        String image = String.valueOf(ZEGOLiveAudioRoomManager.getInstance().getAudioRoomSeatList().stream());

                            new AddStreamInfo().addStreamInfo(roomID,uid1,userId, userName, image1);
//                            new AddStreamInfo().deleteStreamInfo(roomID,userId);
                            new SendGlobalMessage(roomID,"welcome to PrettyLive","audio");
//                            userInfo("welcome to PrettyLive",mAuth.getUid());
                        } catch (Exception e) {
                            // Handle exception
                        }

                    }else {
                        binding.giftButton1.setVisibility(View.VISIBLE);
                        if (userModel!=null){
                            saveRoomUsers(userModel);
                        }

//                        userInfo("joined the live",mAuth.getUid());
//
                        new SendGlobalMessage(roomID,"joined the live","audio");

                    }
                    if (!Objects.equals(docId, "")){
                        checkGiftExpiration(docId);
                    }
                    initListenerAfterLoginRoom();
                }
            }
        });
        // add a gift button to liveAudioRoom audience
        GiftHelper giftHelper = new GiftHelper(findViewById(R.id.layout), String.valueOf(uid), username,otherUserId,"0");
        giftButton = giftHelper.getGiftButton(this, ZEGOSDKKeyCenter.appID, ZEGOSDKKeyCenter.serverSecret, roomID);

        // Get reference to the giftButtonContainer
        FrameLayout giftButtonContainer = findViewById(R.id.giftButtonContainer);
        giftButtonContainer.addView(giftButton);

        binding.cardUserCount.setOnClickListener(v -> {
            showViewersBottomSheetDialog();
        });

        binding.giftButton1.setOnClickListener(v -> {
            showBottomSheetDialog();
        });


        mQuery2 = mFirestore.collection(Constant.STREAM).document(roomID).collection("current_room_user")
//                .orderBy(Query.Direction.ASCENDING)
//                .whereEqualTo("gift_type","1000")
                .limit(LIMIT);

        if (!isHost){
            checkKickOut(roomID,ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
            realTimeLiveEnd(roomID,otherUserId);
        }

        setTopGamerView();
        topGamerList();


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
            Log.i(TAG, "setTopGamerView: "+e);
        }

        binding.gameViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event if needed
            }
        });
    }

    private void realTimeLiveEnd(String roomID, String userId) {
        new EndLiveStatus(new EndLiveStatus.Select() {
            @Override
            public void LiveEndStatus(int endStatus) {
                if (endStatus==1){
                    ZEGOLiveAudioRoomManager.getInstance().leave();
                    deleteUserFromViewersCollection(roomID,userModel.getUid());
                    finish();
                }else {

                }
            }
        }).realTimeLiveEnd(roomID,userId);
    }

    private void checkKickOut(String liveId, String userId) {
        new KickOutInfo(new KickOutInfo.Select() {
            @Override
            public void KickOutStatus(int kickValue) {
                if (kickValue==1){
                    ZEGOLiveAudioRoomManager.getInstance().leave();
                    deleteUserFromViewersCollection(roomID,userModel.getUid());
                    finish();
                }else {

                }

            }
        }).realTimeKickOut(liveId,userId);

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
                    }
                });


    }

    private void showViewersBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_viewers);

        RecyclerView recyclerViewAudience = bottomSheetDialog.findViewById(R.id.recyclerViewAudience);
        TextView notFound = bottomSheetDialog.findViewById(R.id.notFound);

        mAdapter1 = new ViewersListAdapter(mQuery, new ViewersListAdapter.OnActiveUserSelectedListener() {
            @Override
            public void onActiveUserSelected(DocumentSnapshot user) {
                showCustomAlertDialog(LiveAudioRoomActivity.this,user);

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

            ZEGOSDKUser hostUser = ZEGOLiveAudioRoomManager.getInstance().getHostUser();
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

    private void userInfo(String message,String userId) {
        usersRef.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                UserDetailsModel userDetails = document.toObject(UserDetailsModel.class);
                                sendCustomMessage(message, userDetails);
                            }
                        }
                    } else {
                        Log.e("FirestoreListener", "Error getting documents: ", task.getException());
                    }
                });
//        usersRef.whereEqualTo("userId", userId)
//                .addSnapshotListener((value, error) -> {
//                    if (error != null) {
//                        // Handle error
//                        Log.e("FirestoreListener", "Listen failed: " + error.getMessage());
//                        return;
//                    }
//
//                    for (DocumentSnapshot document : value) {
//                        if (document.exists()) {
//                            userDetails = document.toObject(UserDetailsModel.class);
//                            Log.i(TAG, "sendCustomMessage: ");
//
//                        }
//                    }
//                    if (userDetails != null) {
//                        sendCustomMessage(message,userDetails);
//                    }
//
//                });
    }

    public void sendCustomMessage(String message,UserDetailsModel userDetails) {
        Log.i(TAG, "sendCustomMessage: "+message);
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setGift("");
        chatMessageModel.setImage(userDetails.getImage());
        chatMessageModel.setKey(ref.push().getKey());
        chatMessageModel.setMessage(message);
        chatMessageModel.setName(userDetails.getUsername());
        chatMessageModel.setLevel(userDetails.getLevel());
        chatMessageModel.setUserId(userDetails.getUserId());
        sendMessage(chatMessageModel, chatMessageModel.getKey());
        Log.i("jnsdhhjdgsfhjdgsfhjdgsfhjf", "sendCustomMessage: hjhjghjdghjgfghjxghjghjxghjghjgfdsfdsfds");
    }

    private void sendMessage(ChatMessageModel chatMessageModel, String key) {
        ref.child(roomID).child("audio").child(roomID).child("chat_comments").child(key).setValue(chatMessageModel);

    }



    private void getCommentChatMessageFirebase() {
        ref.child(roomID).child("audio").child(roomID).child("chat_comments").addValueEventListener(new ValueEventListener() {
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


    private void confirmKickOut(String liveId, String uid, String userId, AlertDialog dialog1) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LiveAudioRoomActivity.this);


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

    // viewers list
    private void setViewersAdapter() {
        viewUserAdapter = new ViewUserAdapter(mQuery, new ViewUserAdapter.OnActiveUserSelectedListener() {
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
        binding.rvViewers.setAdapter(viewUserAdapter);
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
//                            String image1 ="https://firebasestorage.googleapis.com/v0/b/mydreamlive-c1586.appspot.com/o/images%2FYM8itLzKNsm5orQzeXPy?alt=media&token=d9f663b6-3242-48f1-be60-32317dbca562";

                            if (beans != null) {
                                totalBeans=beans;
                            }


                        }
                    }
                });
    }


    private void showBottomSheetDialog() {
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
                if (getItemCount() == 0) {
                    rv_gift_user.setVisibility(View.GONE);

                } else {
                    rv_gift_user.setVisibility(View.VISIBLE);

                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e("FirebaseFirestoreException", "onError: "+e );
            }


        };
        rv_gift_user.setAdapter(giftViewUserAdapter);
        giftViewUserAdapter.setQuery(mQuery2);

        mAdapter = new GiftAdapter(mQuery1, new GiftAdapter.OnGiftSelectedListener() {
            @Override
            public void onGiftSelected(DocumentSnapshot user) {
                documentSnapshot=user;
            }
        }) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e("FirebaseFirestoreException", "onError: "+e );
            }


        };
        recyclerView.setAdapter(mAdapter);

        mAdapter.setQuery(mQuery1);

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
        data.put("liveId", roomID);
        data.put("time", timestamp);
        firestore.collection("giftDetails").document(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID)).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                boolean isHost = getIntent().getBooleanExtra("host", true);
                String liveType="0";
                Map<String, Object> data = new HashMap<>();
                data.put("fileName", giftModel.getString("fileName"));
                data.put("giftCoin", giftModel.getString("price"));
                data.put("userId", otherUserId);
                data.put("giftId", giftModel.getString("giftId"));
                data.put("liveType", liveType);
                data.put("gift_count", userIds.size()>0?String.valueOf(userIds.size()):String.valueOf(giftCount));
                data.put("liveId", roomID);
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

                            new SendGlobalMessage(roomID,fullMsg,"audio");
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

                                new SendGlobalMessage(roomID,fullMsg,"audio");
                            }
                        }).getUserDetailsByUserId(id);

                    }
                }



                userIdForReceiveGift="";
                select=0;
                userIds.clear();
//                userInfo("send a gift",mAuth.getUid());
//                new SendGlobalMessage(roomID,"send a gift","audio");



                bottomSheetDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LiveAudioRoomActivity.this, "Internal server error please try again."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void exitDialog() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to leave ?")
                .setCancelable(false)
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ZEGOLiveAudioRoomManager.getInstance().leave();
                        boolean isHost = getIntent().getBooleanExtra("host", true);
                        if (isHost){
                            updateLiveStatus(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
                            updateRoomStatus(otherUserId,roomID);
                        }
                        startActivity(new Intent(LiveAudioRoomActivity.this, HomeActivity.class));
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

    private void fetchUserDetails(String userId) {
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
                        Log.e("test2334", "Listen UserDetailsModel: ");
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
                    binding.txtCoin.setText(new Convert().prettyCount(Integer.parseInt(userDetails.getDiamond())));
                }
            });
        }catch (Exception e){
            Log.e(TAG, "Exception in Update coin: "+e.getMessage() );

        }
    }

    private void saveLiveData(String userId, long uid, String userName, boolean isHost, String liveID, String liveType, String country,String image) {

        Date currentDate = new Date();
        long timestamp = currentDate.getTime();
        Map<String, Object> liveDetails = new HashMap<>();
        liveDetails.put("userId", userId);
        liveDetails.put("uid", uid);
        liveDetails.put("username", userName);
        liveDetails.put("photo", !Objects.equals(image, "") ?image:Constant.USER_PLACEHOLDER_PATH);
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
    private void initListenerAfterLoginRoom() {
        ZEGOSDKManager.getInstance().zimService.addEventHandler(new IZIMEventHandler() {
            @Override
            public void onOutgoingRoomRequestAccepted(String requestID, String extendedData) {
                RoomRequestExtendedData data = RoomRequestExtendedData.parse(extendedData);
                if (data != null && data.roomRequestType == RoomRequestType.REQUEST_TAKE_SEAT) {
                    int seatIndex = ZEGOLiveAudioRoomManager.getInstance().findFirstAvailableSeatIndex();
                    if (seatIndex != -1) {
                        ZEGOLiveAudioRoomManager.getInstance()
                                .takeSeat(seatIndex, new ZIMRoomAttributesOperatedCallback() {
                                    @Override
                                    public void onRoomAttributesOperated(String roomID, ArrayList<String> errorKeys,
                                                                         ZIMError errorInfo) {

                                    }
                                });
                    } else {
                        ToastUtil.show(LiveAudioRoomActivity.this, "Cannot find available seat");
                    }
                }
            }

            @Override
            public void onUserAvatarUpdated(String userID, String url) {
                super.onUserAvatarUpdated(userID, url);
                binding.seatContainer.onUserAvatarUpdated(userID, url);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            ZEGOLiveAudioRoomManager.getInstance().leave();
            boolean isHost = getIntent().getBooleanExtra("host", true);
            if (isHost){
                updateLiveStatus(ApplicationClass.getSharedpref().getString(AppConstants.USER_ID));
                updateRoomStatus(otherUserId,roomID);

            }else {
//                ZEGOSDKManager.getInstance().expressService.sendBarrageMessage(userModel.getUsername() + " left the room", (errorCode, messageID) -> {
//                    Toast.makeText(this, "okkkkk"+errorCode, Toast.LENGTH_SHORT).show();
//                });
                deleteUserFromViewersCollection(roomID,userModel.getUid());
//                Toast.makeText(this, "okkkkk", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
        // Start listening for Firestore updates
        if (mAdapter1 != null) {
            mAdapter1.startListening();
        }
        // Start listening for Firestore updates
        if (viewUserAdapter != null) {
            viewUserAdapter.startListening();
        }if (giftViewUserAdapter != null) {
            giftViewUserAdapter.startListening();
        }
    }

    private void deleteUserFromViewersCollection(String streamId, long uid) {

        usersRef.whereEqualTo("uid", uid)
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
                                    try {
//                                        userInfo("left from live",mAuth.getUid());
                                        new SendGlobalMessage(streamId,"left from live","audio");
//                                        ZEGOSDKManager.getInstance().expressService.sendBarrageMessage(" left the room", (errorCode, messageID) -> {
//
//                                        });
                                    }catch (Exception e){

                                    }
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
        roomUsers.setLiveId(roomID);
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

        firestore.collection("room_users").document(roomID).collection("viewers").document(userDetails.getUserId()).set(roomUsers).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        // Start listening for Firestore updates
        if (mAdapter1 != null) {
            mAdapter1.stopListening();
        }
        // Start listening for Firestore updates
        if (viewUserAdapter != null) {
            viewUserAdapter.stopListening();
        }if (giftViewUserAdapter != null) {
            giftViewUserAdapter.stopListening();
        }
    }

    private void updateLiveStatus(String userId) {
        // Reference to the Firestore collection
        CollectionReference liveDetailsRef = firestore.collection(Constant.LIVE_DETAILS);

        // Create a query to find the document with the given userId
        Query query = liveDetailsRef.whereEqualTo("userId", userId).whereEqualTo("liveID",roomID);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get the document ID for the matched document
                    String documentId = document.getId();
                    String liveId = document.getString("liveID");

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
//        if (mAdapter != null) {
//            mAdapter.stopListening();
//        }
//        // Start listening for Firestore updates
//        if (mAdapter1 != null) {
//            mAdapter1.stopListening();
//        }
//        // Start listening for Firestore updates
//        if (viewUserAdapter != null) {
//            viewUserAdapter.stopListening();
//        }
//
//        binding = null;
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
//                                    Toast.makeText(this, "Gift has expired", Toast.LENGTH_SHORT).show();
                                    // Gift has expired, handle accordingly (e.g., show an expired message)
                                } else {
//                                    Toast.makeText(this, "Gift is still valid", Toast.LENGTH_SHORT).show();

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
        try {
            String liveType="0";
            Map<String, Object> data = new HashMap<>();
            data.put("fileName", purchageGiftModel.getFileName());
            data.put("giftCoin", purchageGiftModel.getGiftCoin());
            data.put("userId", otherUserId);
            data.put("giftId", purchageGiftModel.getGiftId());
            data.put("liveType", liveType);
            data.put("gift_count","1");
            data.put("liveId", roomID);
            String key = ref.push().getKey();
            ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").child(key).setValue(data);
        }catch (Exception e){

        }

    }
    private boolean isGiftExpired(Date endDate) {
        // Get the current date
        Date currentDate = new Date();

        // Compare the endDate with the current date
        return currentDate.after(endDate);
    }
}