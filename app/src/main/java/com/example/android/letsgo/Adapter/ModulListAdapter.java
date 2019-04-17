package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.SocialModulInfo;
import com.example.android.letsgo.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ModulListAdapter extends RecyclerView.Adapter<ModulListAdapter.ModulsViewHolder> {
    private List<Modul> moduls;
    private  List<SocialModulInfo> socialModulInfos;
    private LayoutInflater mInflater;
    private final ModulOnClickHandler mClickHandler;
    Context context2;

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

        TextView titleView = holder.itemView.findViewById(R.id.tv_modul_list_modul_title);
        titleView.setText(modulTitle);

        TextView editorNameView = holder.itemView.findViewById(R.id.tv_modul_list_item_editor_name);
        String editorNameViewText = "by " + currentModul.getEditorName();
        editorNameView.setText(editorNameViewText);

        TextView doneCountView = holder.itemView.findViewById(R.id.tv_modul_list_item_times_done);
        TextView avgDurationView = holder.itemView.findViewById(R.id.tv_modul_list_item_avg_duration);
        RatingBar avgRatingView = holder.itemView.findViewById(R.id.rb_modul_list_item);
        Log.e("Adapter", ""+ position);
        Log.e("SMI", ""+currentSocialModulInfo);

        if(currentSocialModulInfo!=null) {
            doneCountView.setText(String.valueOf(currentSocialModulInfo.getDoneCount()));
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(currentSocialModulInfo.getDurationAvg()),
                    TimeUnit.MILLISECONDS.toMinutes(currentSocialModulInfo.getDurationAvg()) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(currentSocialModulInfo.getDurationAvg()) % TimeUnit.MINUTES.toSeconds(1));

            avgDurationView.setText("~ " +hms);

            if(currentSocialModulInfo.getRatingNum()>0) {
                avgRatingView.setRating(currentSocialModulInfo.getRating());
            }else{
                avgRatingView.setVisibility(View.GONE);
            }

        }else{
            doneCountView.setText("0");
            avgDurationView.setText(" -");
            avgRatingView.setVisibility(View.GONE);
        }


    }

    public int getItemCount(){
        return moduls.size();
    }



}
