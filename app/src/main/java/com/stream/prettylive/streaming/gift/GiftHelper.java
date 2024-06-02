package com.stream.prettylive.streaming.gift;

import static com.stream.prettylive.streaming.internal.utils.Utils.dp2px;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stream.prettylive.R;
import com.stream.prettylive.global.ApplicationClass;
//import com.zegocloud.uikit.ZegoUIKit;
//import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomTextMessage;
//import com.zegocloud.uikit.service.defines.ZegoInRoomCommandListener;
//import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
//import com.zegocloud.uikit.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class GiftHelper {

    private Handler handler = new Handler(Looper.getMainLooper());

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = firebaseDatabase.getReference().child("userInfo");

    private GiftAnimation giftAnimation;
    private String userID;
    private String userName;
    private String otherUserId;
    private String liveType;

    public GiftHelper(ViewGroup animationViewParent, String userID, String userName,String otherUserId,String liveType) {
        giftAnimation = new SVGAAnimation(animationViewParent,otherUserId,liveType);
//        giftAnimation = new VAPAnimation(animationViewParent);
        this.userID = userID;
        this.userName = userName;
        this.otherUserId = otherUserId;
        this.liveType = liveType;



        ref.child(otherUserId).child(liveType).child(otherUserId).child("gifts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                    // Now you can access individual fields
                    if (data != null) {
                        String fileName = (String) data.get("fileName");
                        String gift_count = (String) data.get("gift_count");

                        showAnimation(fileName,gift_count);
//                        Toast.makeText(ApplicationClass.getInstance(), ""+senderId, Toast.LENGTH_SHORT).show();

                        // Use the retrieved data as needed
                    }

//                    GiftModel giftModel = snapshot.getValue(GiftModel.class);
//
//                    setUpdateCoinFirebase(giftModel);
//                    showGiftDialog(giftModel);
//                    showGift(giftModel);
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ApplicationClass.getInstance(), "errrr :"+error, Toast.LENGTH_SHORT).show();

            }
        });


//        try{
//            // when someone send gift,will receive InRoomCommand or InRoomTextMessage
//            ZegoUIKit.addInRoomCommandListener(new ZegoInRoomCommandListener() {
//                @Override
//                public void onInRoomCommandReceived(ZegoUIKitUser fromUser, String command) {
//                    if (!fromUser.userID.equals(userID) && command.contains("gift_type")) {
//                        showAnimation();
//                    }
//                }
//            });
////
////        // when someone send gift,will receive InRoomCommand or InRoomTextMessage
//            ZegoUIKit.getSignalingPlugin().addInRoomTextMessageListener((messages, s) -> {
//                if (!messages.isEmpty()) {
//                    ZegoSignalingInRoomTextMessage message = messages.get(0);
//                    if (!message.senderUserID.equals(userID)) {
//                        showAnimation();
//                    }
//                }
//            });
//
//        }catch (Exception e){
//
//        }
    }

    public View getGiftButton(Context context, long appID, String serverSecret, String roomID) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.presents_icon);
        int size = dp2px(36f, context.getResources().getDisplayMetrics());
        int marginTop = dp2px(10f, context.getResources().getDisplayMetrics());
        int marginBottom = dp2px(16f, context.getResources().getDisplayMetrics());
        int marginEnd = dp2px(8, context.getResources().getDisplayMetrics());
        LayoutParams layoutParams = new LayoutParams(size, size);
        layoutParams.topMargin = marginTop;
        layoutParams.bottomMargin = marginBottom;
        layoutParams.rightMargin = marginEnd;
        imageView.setLayoutParams(layoutParams);
        // click will post json to server
        imageView.setOnClickListener(v -> {

//            final String path = "https://zego-example-server-nextjs.vercel.app/api/send_gift";
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("app_id", appID);
//                jsonObject.put("server_secret", serverSecret);
//                jsonObject.put("room_id", roomID);
//                jsonObject.put("user_id", userID);
//                jsonObject.put("user_name", userName);
//                jsonObject.put("gift_type", 1001);
//                jsonObject.put("gift_count", 1);
//                jsonObject.put("timestamp", System.currentTimeMillis());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            String jsonString = jsonObject.toString();
//            new Thread() {
//                public void run() {
//                    showAnimation();
//                }
//            }.start();
        });
        return imageView;
    }


    /**
     * post json to server,will receive InRoomCommand or InRoomTextMessage callback
     *
     * @param path
     * @param jsonString
     * @param successCallback
     */
    private void httpPost(String path, String jsonString, Runnable successCallback) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            byte[] writebytes = jsonString.getBytes();
            conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
            OutputStream outwritestream = conn.getOutputStream();
            outwritestream.write(jsonString.getBytes());
            outwritestream.flush();
            outwritestream.close();
            int code = conn.getResponseCode();
//            Log.d(ZegoUIKit.TAG, "run: " + code);
            if (code == 200) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buffer = new byte[1024]; //1kb
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                is.close();
                String content = baos.toString();
//                Log.d(ZegoUIKit.TAG, "run() called:" + content);
                handler.post(successCallback);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void showAnimation(String fileName, String gift_count) {
        giftAnimation.startPlay(fileName,gift_count);
    }
}
