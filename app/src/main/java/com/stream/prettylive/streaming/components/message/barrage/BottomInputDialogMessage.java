package com.stream.prettylive.streaming.components.message.barrage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.prettylive.R;
import com.stream.prettylive.global.AppConstants;
import com.stream.prettylive.global.ApplicationClass;
import com.stream.prettylive.streaming.activity.LiveStreamingActivity;
import com.stream.prettylive.streaming.activity.model.ChatMessageModel;
import com.stream.prettylive.streaming.internal.utils.Utils;
import com.stream.prettylive.ui.auth.models.UserMainResponse;
import com.stream.prettylive.ui.auth.viewmodel.UserViewModel;
import com.stream.prettylive.ui.home.ui.profile.models.UserDetailsModel;
import com.stream.prettylive.ui.utill.Constant;

public class BottomInputDialogMessage extends Dialog {
    private String roomId;
    private String liveType;
    private CollectionReference usersRef;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private  UserDetailsModel userDetails;
    private UserViewModel userViewModel;

    UserMainResponse userDetails1;
    private Context context;

    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("userInfo");
    public BottomInputDialogMessage(Context context, String roomId, String liveType) {
        super(context);
        this.context = context;
        this.roomId = roomId;
        this.liveType = liveType;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup contentView = (ViewGroup) LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottom_input, null, false);
        FrameLayout contentViewParent = new FrameLayout(getContext());
        contentViewParent.addView(contentView);
        setContentView(contentViewParent);
        userViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        usersRef = firestore.collection(Constant.LOGIN_DETAILS);

        int corner = Utils.dp2px(8f, getContext().getResources().getDisplayMetrics());
        float[] outerR = new float[]{corner, corner, corner, corner, corner, corner, corner, corner};
        RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);

        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.getPaint().setColor(Color.parseColor("#222222"));
        contentView.setBackground(shapeDrawable);

        ImageView sendBtn = findViewById(R.id.send_message);
        sendBtn.setEnabled(false);

        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_enabled},
                ContextCompat.getDrawable(getContext(), R.drawable.zego_uikit_icon_send_normal));
        sld.addState(new int[]{}, ContextCompat.getDrawable(getContext(), R.drawable.zego_uikit_icon_send_disable));
        sendBtn.setImageDrawable(sld);

        EditText editText = findViewById(R.id.edit_message_input);

        sendBtn.setOnClickListener(v -> {
            String message = editText.getText().toString();
            if (userDetails1!=null){
                sendCustomMessage(message,userDetails1);
                editText.setText("");
            }else {
                Toast.makeText(getContext(), "Somethings went wrong...", Toast.LENGTH_SHORT).show();
                editText.setText("");
            }

        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendBtn.setEnabled(editText.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (mAuth.getCurrentUser() != null) {
            userInfo(mAuth.getCurrentUser().getUid());
        }

        getUserDataApi();


        RoundRectShape roundRectShape2 = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(roundRectShape2);
        shapeDrawable2.getPaint().setColor(Color.parseColor("#1AFFFFFF"));
        editText.setBackground(shapeDrawable2);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.1f;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable());

        int mode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        window.setSoftInputMode(mode);

        setCanceledOnTouchOutside(true);

        setOnDismissListener(dialog -> {
            hideInputWindow(contentView);
        });

        requestInputWindow(editText);
    }

    private void getUserDataApi() {
        int masterId = Integer.parseInt(ApplicationClass.getSharedpref().getString(AppConstants.UID));
        userViewModel.getUser(masterId).observe((LifecycleOwner) context, new Observer<UserMainResponse>() {
            @Override
            public void onChanged(UserMainResponse master) {
                if (master != null) {
                    userDetails1=master;


                }
            }
        });
    }

    private void userInfo(String userId) {
        usersRef.whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        Log.e("FirestoreListener", "Listen failed: " + error.getMessage());
                        return;
                    }

                    for (DocumentSnapshot document : value) {
                        if (document.exists()) {
                            userDetails = document.toObject(UserDetailsModel.class);
//                            updateUI(userDetails);
                        }
                    }
                });
    }

//    private void sendCustomMessage(String message,UserDetailsModel userDetails) {
//        ChatMessageModel chatMessageModel = new ChatMessageModel();
//        chatMessageModel.setGift("");
//        chatMessageModel.setImage(userDetails.getImage());
//        chatMessageModel.setKey(ref.push().getKey());
//        chatMessageModel.setMessage(message);
//        chatMessageModel.setName(userDetails.getUsername());
//        chatMessageModel.setLevel(userDetails.getLevel());
//        chatMessageModel.setUserId(userDetails.getUserId());
//        sendMessage(chatMessageModel, chatMessageModel.getKey());
//    }
private void sendCustomMessage(String message,UserMainResponse userDetails1) {
    ChatMessageModel chatMessageModel = new ChatMessageModel();
    chatMessageModel.setGift("");
    chatMessageModel.setImage(userDetails1.getData().getUserProfilePic());
    chatMessageModel.setKey(ref.push().getKey());
    chatMessageModel.setMessage(message);
    chatMessageModel.setName(userDetails1.getData().getUserNickName());
    chatMessageModel.setLevel(String.valueOf(userDetails1.getData().getLevel()));
    chatMessageModel.setUserId(userDetails1.getData().getId());
    sendMessage(chatMessageModel, chatMessageModel.getKey());
}

    private void sendMessage(ChatMessageModel chatMessageModel, String key) {
        ref.child(roomId).child(liveType).child(roomId).child("chat_comments").child(key).setValue(chatMessageModel);

    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
            dismiss();
            return true;
        }
        return false;
    }

    public static void hideInputWindow(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void requestInputWindow(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean input = false;
        if (view.isAttachedToWindow()) {
            input = imm.showSoftInput(view, 0);
        }
    }
}
