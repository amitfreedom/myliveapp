package com.stream.prettylive.streaming.components.message.barrage;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stream.prettylive.R;
import com.stream.prettylive.streaming.internal.ZEGOLiveStreamingManager;
import com.stream.prettylive.streaming.internal.sdk.ZEGOSDKManager;
import com.stream.prettylive.streaming.internal.sdk.basic.ZEGOSDKUser;
import com.stream.prettylive.streaming.internal.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import im.zego.zegoexpress.entity.ZegoBarrageMessageInfo;

public class BarrageMessageAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<ZegoBarrageMessageInfo> barrageMessageInfoList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zego_uikit_item_inroom_message, parent, false);
        return new ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZegoBarrageMessageInfo message = barrageMessageInfoList.get(position);
        ZEGOSDKUser ZEGOSDKUser = ZEGOSDKManager.getInstance().expressService.getUser(message.fromUser.userID);
        Context context = holder.itemView.getContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        String hostTag = "Host";
        StringBuilder builder = new StringBuilder();
        if (ZEGOLiveStreamingManager.getInstance().isHost(ZEGOSDKUser.userID)) {
            builder.append(hostTag);
            builder.append(" ");
        }

        builder.append(ZEGOSDKUser.userName);
        builder.append(" ");
        builder.append(message.message);
        String source = builder.toString();
        SpannableString string = new SpannableString(source);
        RoundBackgroundColorSpan backgroundColorSpan = new RoundBackgroundColorSpan(context,
            ContextCompat.getColor(context, R.color.pink_top),
            ContextCompat.getColor(context, android.R.color.white));
        if (ZEGOLiveStreamingManager.getInstance().isHost(ZEGOSDKUser.userID)) {
            AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(Utils.sp2px(10, displayMetrics));
            string.setSpan(absoluteSizeSpan, 0, hostTag.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            string.setSpan(backgroundColorSpan, 0, hostTag.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
            ContextCompat.getColor(context, R.color.light_brown));
        int indexOfUser = source.indexOf(ZEGOSDKUser.userName);
        string.setSpan(foregroundColorSpan, indexOfUser, indexOfUser + ZEGOSDKUser.userName.length(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(Utils.sp2px(13, displayMetrics));
        string.setSpan(absoluteSizeSpan, indexOfUser, indexOfUser + ZEGOSDKUser.userName.length(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        TextView textView = holder.itemView.findViewById(R.id.tv_inroom_message);
        textView.setText(string);
    }

    @Override
    public int getItemCount() {
        return barrageMessageInfoList.size();
    }

    public void addMessages(ArrayList<ZegoBarrageMessageInfo> messageList) {
        int size = this.barrageMessageInfoList.size();
        this.barrageMessageInfoList.addAll(messageList);
        notifyItemRangeInserted(size, messageList.size());
    }

    public void addMessage(ZegoBarrageMessageInfo message) {
        int size = this.barrageMessageInfoList.size();
        this.barrageMessageInfoList.add(message);
        notifyItemInserted(size);
    }
}
