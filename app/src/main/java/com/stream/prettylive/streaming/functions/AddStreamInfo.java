package com.stream.prettylive.streaming.functions;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.prettylive.global.AppConstants;
import com.stream.prettylive.ui.utill.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AddStreamInfo {
    private static final String TAG = "AddStreamInfo";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection(Constant.LOGIN_DETAILS);

    public void addStreamInfo(String mainStreamID, String uid ,String userId ,String userName, String image) {
        // Create a Map to hold the stream information
        Map<String, Object> streamInfo = new HashMap<>();
        streamInfo.put("mainStreamID", mainStreamID);
        streamInfo.put("uid", uid);
        streamInfo.put("userID", userId);
        streamInfo.put("userName", userName);
        streamInfo.put("image", image);
        db.collection(Constant.STREAM).document(mainStreamID).collection("current_room_user").document(userId).set(streamInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }

    public void deleteStreamInfo(String mainStreamID, String userId) {
        db.collection(Constant.STREAM)
                .document(mainStreamID)
                .collection("current_room_user")
                .document(userId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("room_users", "onSuccess: DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("room_users", "Error deleting document", e);
                    }
                });
    }

    public void deleteJoinedRoomUser(String mainStreamID,String userId) {
        // Get a reference to the document you want to delete
        DocumentReference documentRef = FirebaseFirestore.getInstance()
                .collection(Constant.ROOM_USER)
                .document(mainStreamID)
                .collection("viewers")
                .document(userId);

// Delete the document
        documentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("room_users", "onSuccess: delete done");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("room_users", "Exception: delete error" + e);
                    }
                });
//        // Create a Map to hold the stream information
//        Map<String, Object> streamInfo = new HashMap<>();
//        streamInfo.put("mainStreamID", mainStreamID);
//        streamInfo.put("uid", uid);
//        streamInfo.put("userID", userId);
//        db.collection(Constant.KICK_OUT).document(mainStreamID).collection("current_room_user").document(userId).set(streamInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }
    public void addStreamKickOut(String mainStreamID, String uid ,String userId) {
        // Create a Map to hold the stream information
        Map<String, Object> streamInfo = new HashMap<>();
        streamInfo.put("mainStreamID", mainStreamID);
        streamInfo.put("uid", uid);
        streamInfo.put("userID", userId);
        db.collection(Constant.KICK_OUT).document(mainStreamID).collection("current_room_user").document(userId).set(streamInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }
    public void roomActive(String mainStreamID, String uid ,String userId) {
        // Create a Map to hold the stream information
        Map<String, Object> streamInfo = new HashMap<>();
        streamInfo.put("mainStreamID", mainStreamID);
        streamInfo.put("uid", uid);
        streamInfo.put("userID", userId);
        streamInfo.put("active", "yes");
        db.collection(Constant.ROOM_STATUS).document(mainStreamID).collection("current_room_user").document(userId).set(streamInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }


}
