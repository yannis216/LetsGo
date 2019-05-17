package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.SocialModulInfo;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.CallbackHelper;
import com.example.android.letsgo.Utils.PictureUtil;
import com.example.android.letsgo.Utils.UsedForSorter;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ModulListAdapter extends RecyclerView.Adapter<ModulListAdapter.ModulsViewHolder> {
    private List<Modul> moduls;
    private  List<SocialModulInfo> socialModulInfos;
    private LayoutInflater mInflater;
    private final ModulOnClickHandler mClickHandler;
    Context context2;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface ModulOnClickHandler {
        void onClick(Modul requestedModul);
    }

    public ModulListAdapter(Context context, List<Modul> moduls, List<SocialModulInfo> socialModulInfos, ModulOnClickHandler clickHandler){
        mInflater = LayoutInflater.from(context);
        context2 = context;
        this.moduls = moduls;
        this.socialModulInfos = socialModulInfos;
        mClickHandler = clickHandler;
    }

    public class ModulsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ModulsViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Modul requestedModul = moduls.get(adapterPosition);
            //TODO Send SocialModulInfos here
            mClickHandler.onClick(requestedModul);
        }
    }



    @NonNull
    @Override
    public ModulsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_modul_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, attachImmediately);


        return new ModulsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModulListAdapter.ModulsViewHolder holder, int position) {
        Modul currentModul = moduls.get(position);
        SocialModulInfo currentSocialModulInfo = socialModulInfos.get(position);
        String modulTitle = currentModul.getTitle();

        final ImageView modulThumbView = holder.itemView.findViewById(R.id.iv_modul_list_item_thumb);
        String modulPictureUrl=currentModul.getPictureUrl();
        final ProgressBar progressBar = holder.itemView.findViewById(R.id.pb_modul_list_modul_picture_load);

        TextView titleView = holder.itemView.findViewById(R.id.tv_modul_list_modul_title);
        titleView.setText(modulTitle);

        TextView editorNameView = holder.itemView.findViewById(R.id.tv_modul_list_item_editor_name);
        String editorNameViewText = "by " + currentModul.getEditorName();
        editorNameView.setText(editorNameViewText);

        TextView doneCountView = holder.itemView.findViewById(R.id.tv_modul_list_item_times_done);
        TextView avgDurationView = holder.itemView.findViewById(R.id.tv_modul_list_item_avg_duration);
        RatingBar avgRatingView = holder.itemView.findViewById(R.id.rb_modul_list_item);
        ImageView durationClock = holder.itemView.findViewById(R.id.iv_modul_list_item_duration_clock);

        if(currentSocialModulInfo!=null) {
            doneCountView.setText(String.valueOf(currentSocialModulInfo.getDoneCount()));
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(currentSocialModulInfo.getDurationAvg()),
                    TimeUnit.MILLISECONDS.toMinutes(currentSocialModulInfo.getDurationAvg()) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(currentSocialModulInfo.getDurationAvg()) % TimeUnit.MINUTES.toSeconds(1));

            avgDurationView.setText("~ " +hms);

            if(currentSocialModulInfo.getRatingNum()>0) {
                avgRatingView.setVisibility(View.VISIBLE);
                avgRatingView.setRating(currentSocialModulInfo.getRating());
            }else{
                avgRatingView.setVisibility(View.GONE);
            }
            if(!(currentSocialModulInfo.getDoneCount()==0)){
                avgDurationView.setVisibility(View.VISIBLE);
                durationClock.setVisibility(View.VISIBLE);
            }else{
                avgDurationView.setVisibility(View.GONE);
                durationClock.setVisibility(View.GONE);
            }
        }else{
            doneCountView.setText("0");
            avgDurationView.setText(" -");
            avgRatingView.setVisibility(View.GONE);
            avgDurationView.setVisibility(View.GONE);
            durationClock.setVisibility(View.GONE);
        }

        UsedForSorter sorter = new UsedForSorter(moduls.get(position), context2);
        Map<String, Integer> sortedUsedFors = sorter.getMostImportantUsedFor();
        final List<String> usedForStrings = new ArrayList<String>();
        for(int i = 0; i <3 && i <sortedUsedFors.size() ; i++ ){
            usedForStrings.add(sortedUsedFors.keySet().toArray()[i].toString());
        }

        for(String s : usedForStrings){
            //Builds the Textview that holds the usedfor Strings
            LinearLayout usedForLinearLayout = holder.itemView.findViewById(R.id.ll_modul_list_usedFor_layout);
            TextView textView = new TextView(context2);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,20,0);
            textView.setLayoutParams(params);
            textView.setBackgroundColor(context2.getResources().getColor(R.color.backgroundUsedForChips));
            textView.setPadding(5,0,5,0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setText(s);
            usedForLinearLayout.addView(textView);
        }

        if(modulPictureUrl == null){

        }else{
            modulThumbView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            CallbackHelper listenIfImageLoadedSuccessfullyHelper = new CallbackHelper() {
                @Override
                public void onSuccess() {
                    modulThumbView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure() {
                    Log.e("ModulElementPicture", "Not loaded correctly");
                }
            };
            PictureUtil pictureUtil = new PictureUtil(context2, modulThumbView, listenIfImageLoadedSuccessfullyHelper);
            pictureUtil.saveModulThumbnailFromDatabaseToLocalStorage(storage, currentModul);

        }

    }

    public int getItemCount(){
        return moduls.size();
    }







}
