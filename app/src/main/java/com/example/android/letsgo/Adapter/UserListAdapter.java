package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.letsgo.Classes.User;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.CallbackHelper;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private List<User> userList;
    private LayoutInflater mInflater;
    private final UserOnClickHandler mClickHandler;
    Context context2;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface UserOnClickHandler {
        void onClick(User requestedUser);
    }

    public UserListAdapter(Context context, List<User> userList, UserOnClickHandler clickHandler){
        mInflater = LayoutInflater.from(context);
        context2 = context;
        this.userList = userList;
        mClickHandler = clickHandler;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public UserViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            User requestedUser = userList.get(adapterPosition);
            mClickHandler.onClick(requestedUser);
        }
    }



    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_user_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, attachImmediately);


        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.UserViewHolder holder, int position) {
        User currentUser = userList.get(position);
        String userName = currentUser.getDisplayName();

        final ImageView userThumbView = holder.itemView.findViewById(R.id.iv_user_list_item_thumb);
        String userPictureUrl= currentUser.getProfilePictureUrl();
        final ProgressBar progressBar = holder.itemView.findViewById(R.id.pb_user_list_picture_load);

        TextView nameView = holder.itemView.findViewById(R.id.tv_user_list_user_name);
        nameView.setText(userName);



        if(userPictureUrl == null){

        }else{
            userThumbView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            CallbackHelper listenIfImageLoadedSuccessfullyHelper = new CallbackHelper() {
                @Override
                public void onSuccess() {
                    userThumbView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure() {
                    Log.e("UserPicture", "Not loaded correctly");
                }
            };
            PictureUtil pictureUtil = new PictureUtil(context2, userThumbView, listenIfImageLoadedSuccessfullyHelper);
            pictureUtil.saveUserThumbnailFromDatabaseToLocalStorage(storage, currentUser.getAuthId(), currentUser.getProfilePictureUrl());

        }

    }

    public int getItemCount(){
        return userList.size();
    }







}
