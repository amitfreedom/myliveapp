package com.stream.prettylive.ui.toplist.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.stream.prettylive.databinding.ListTopUserBinding;
import com.stream.prettylive.ui.home.ui.home.adapter.FirestoreAdapter;
import com.stream.prettylive.ui.home.ui.profile.models.UserDetailsModel;
import com.stream.prettylive.ui.utill.Constant;

import java.text.DecimalFormat;
import java.util.Objects;

public class TopUserAdapter extends FirestoreAdapter<TopUserAdapter.ViewHolder> {
    public interface OnUserSelectedListener {
        void onUserSelected(DocumentSnapshot user);
    }

    private TopUserAdapter.OnUserSelectedListener mListener;
    String listType;


    public TopUserAdapter(Query query, OnUserSelectedListener listener, String type) {
        super(query);
        mListener = listener;
        this.listType = type;
    }




    @NonNull
    @Override
    public TopUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopUserAdapter.ViewHolder(ListTopUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(TopUserAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener,position,listType);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ListTopUserBinding binding;

        public ViewHolder(ListTopUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @SuppressLint("SetTextI18n")
        public void bind(final DocumentSnapshot snapshot,
                         final OnUserSelectedListener listener, int position, String listType) {

           try{
               UserDetailsModel userDetailsModel = snapshot.toObject(UserDetailsModel.class);


               if (Objects.equals(userDetailsModel.getImage(), "")){
                   Glide.with(binding.ivProfileImage.getContext())
                           .load(Constant.USER_PLACEHOLDER_PATH)
                           .into(binding.ivProfileImage);
               }else {
                   Glide.with(binding.ivProfileImage.getContext())
                           .load(userDetailsModel.getImage())
                           .into(binding.ivProfileImage);
               }
               binding.txtSn.setText(String.valueOf(position+1));
               binding.txtUserName.setText(userDetailsModel.getUsername());
               binding.txtUserUid.setText("Lv "+userDetailsModel.getLevel());
               if (Objects.equals(listType, "receiveCoin")){
                   binding.txtTotalCoin.setText(prettyCount(userDetailsModel.getReceiveCoin()));
               }
               if (Objects.equals(listType, "senderCoin")){
                   binding.txtTotalCoin.setText(prettyCount(userDetailsModel.getSenderCoin()));
               }if (Objects.equals(listType, "receiveGameCoin")){
                   binding.txtTotalCoin.setText(prettyCount(userDetailsModel.getReceiveGameCoin()));
               }


               itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (listener != null) {
                           listener.onUserSelected(snapshot);
                       }
                   }
               });
           }catch (Exception e){

           }
        }

    }

    public static String prettyCount(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.00").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat().format(numValue);
        }
    }
}
