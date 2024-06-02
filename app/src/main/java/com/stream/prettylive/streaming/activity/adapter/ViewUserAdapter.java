package com.stream.prettylive.streaming.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.stream.prettylive.databinding.ItemActiveUserBinding;
import com.stream.prettylive.databinding.ItemActiveViewersBinding;
import com.stream.prettylive.streaming.activity.model.RoomUsers;
import com.stream.prettylive.ui.home.ui.home.adapter.FirestoreAdapter;
import com.stream.prettylive.ui.utill.Constant;

import java.util.Objects;

public class ViewUserAdapter extends FirestoreAdapter<ViewUserAdapter.ViewHolder> {

    public interface OnActiveUserSelectedListener {

        void onActiveUserSelected(DocumentSnapshot user);

    }

    private ViewUserAdapter.OnActiveUserSelectedListener mListener;


    public ViewUserAdapter(Query query, ViewUserAdapter.OnActiveUserSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewUserAdapter.ViewHolder(ItemActiveViewersBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewUserAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemActiveViewersBinding binding;

        public ViewHolder(ItemActiveViewersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final ViewUserAdapter.OnActiveUserSelectedListener listener) {

            RoomUsers roomUsers = snapshot.toObject(RoomUsers.class);

                binding.name.setText(roomUsers.getUsername());
            binding.name.setText(roomUsers.getUsername());

            if (!Objects.equals(roomUsers.getImage(), "")){
                // Load image
                Glide.with(binding.ivViewer.getContext())
                        .load(roomUsers.getImage())
                        .into(binding.ivViewer);
            }else {
                // Load image
                Glide.with(binding.ivViewer.getContext())
                        .load(Constant.USER_PLACEHOLDER_PATH)
                        .into(binding.ivViewer);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onActiveUserSelected(snapshot);
                    }
                }
            });
        }

    }
}

