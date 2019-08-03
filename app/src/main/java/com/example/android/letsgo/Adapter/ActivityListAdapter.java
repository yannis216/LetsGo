package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Activity;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.CallbackHelper;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ActivityViewHolder> {
    private List<Activity> activityList;
    private LayoutInflater mInflater;
    private final ActivityOnClickHandler mClickHandler;
    Context context2;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface ActivityOnClickHandler {
        void onClick(Activity activity);
    }

    public ActivityListAdapter(Context context, List<Activity> activities, ActivityOnClickHandler clickHandler){
        mInflater = LayoutInflater.from(context);
        context2 = context;
        this.activityList = activities;
        mClickHandler = clickHandler;
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ActivityViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Activity requestedActivity = activityList.get(adapterPosition);
            mClickHandler.onClick(requestedActivity);
        }
    }



    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_activity_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, attachImmediately);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityListAdapter.ActivityViewHolder holder, int position) {
        Activity currentActivity = activityList.get(position);


        //Populate Userinfo
        String userName = currentActivity.getUserName();
        final ImageView userThumbView = holder.itemView.findViewById(R.id.iv_activitylistitem_userpic);
        String userPictureUrl= currentActivity.getUserPicLink();
        TextView userNameView = holder.itemView.findViewById(R.id.tv_activitylistitem_username);
        userNameView.setText(userName);

        //Populate endtime
        long endTimeStamp = currentActivity.getEndTime();
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTimeInMillis(endTimeStamp);
        String date = DateFormat.format("EEEdd.MM. HH:mm", cal).toString();
        String endTimeString = date+"h";

        TextView endTimeView = holder.itemView.findViewById(R.id.tv_activitylistitem_endtime);
        endTimeView.setText(endTimeString);


        if(userPictureUrl == null){

        }else{
            userThumbView.setVisibility(View.INVISIBLE);
            CallbackHelper listenIfImageLoadedSuccessfullyHelper = new CallbackHelper() {
                @Override
                public void onSuccess() {
                    userThumbView.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure() {
                    Log.e("UserPicture", "Not loaded correctly");
                }
            };
            PictureUtil pictureUtil = new PictureUtil(context2, userThumbView, listenIfImageLoadedSuccessfullyHelper);
            pictureUtil.saveUserThumbnailFromDatabaseToLocalStorage(storage, currentActivity.getuId(), currentActivity.getPictureUrl());

        }

        //Populate DI
        TextView activityDiTextView = holder.itemView.findViewById(R.id.tv_activitylistitem_text);
        final ImageView activityDiImageView = holder.itemView.findViewById(R.id.iv_activitylistitem_pic);

        activityDiTextView.setText(currentActivity.getDiComment());
        String activityPictureUrl = currentActivity.getPictureUrl();
        if(activityPictureUrl == null){
        }else{
            activityDiImageView.setVisibility(View.GONE);
            CallbackHelper listenIfImageLoadedSuccessfullyHelper = new CallbackHelper() {
                @Override
                public void onSuccess() {
                    activityDiImageView.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure() {
                    Log.e("ActivityPicture", "Not loaded correctly");
                }
            };
            PictureUtil pictureUtil = new PictureUtil(context2, activityDiImageView, listenIfImageLoadedSuccessfullyHelper);
            pictureUtil.saveActivityImageFromDatabaseToLocalStorage(storage, currentActivity);

        }

        //Populate ModulInfo
        TextView modulTitleView = holder.itemView.findViewById(R.id.tv_activitylistitem_modul_title);
        RatingBar rb = holder.itemView.findViewById(R.id.rb_activitylistitem_modul_DI);
        TextView modulTimeNeededView = holder.itemView.findViewById(R.id.tv_activitylistitem_modul_DI_duration);
        final ImageView modulPicView = holder.itemView.findViewById(R.id.iv_activitylistitem_modul_DI_thumb);

        modulTitleView.setText(currentActivity.getModulTitle());
        rb.setRating(currentActivity.getModulRating());
        long duration = currentActivity.getEndTime() - currentActivity.getStartTime();
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
        modulTimeNeededView.setText(hms);

        String modulPicUrl = currentActivity.getModulPicUrl();
        if(modulPicUrl == null){
        }else{
            modulPicView.setVisibility(View.INVISIBLE);
            CallbackHelper listenIfImageLoadedSuccessfullyHelper = new CallbackHelper() {
                @Override
                public void onSuccess() {
                    modulPicView.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure() {
                    Log.e("ModulPicture", "Not loaded correctly");
                }
            };
            PictureUtil pictureUtil = new PictureUtil(context2, modulPicView, listenIfImageLoadedSuccessfullyHelper);
            pictureUtil.saveModulThumbnailFromDatabaseToLocalStorage(storage, currentActivity.getModulId(), currentActivity.getModulPicUrl());

        }

    }

    public int getItemCount(){
        return activityList.size();
    }







}
