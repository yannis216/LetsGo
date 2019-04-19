package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.CallbackHelper;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ModulDetailElementListAdapter extends RecyclerView.Adapter<ModulDetailElementListAdapter.ModulElementsViewHolder> {
    private List<ModulElement> modulElements;
    private LayoutInflater mInflater;
    private final ModulElementOnClickHandler mClickHandler;
    Context context2;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface ModulElementOnClickHandler {
        void onClick(ModulElement requestedModulElement);
    }

    public ModulDetailElementListAdapter(Context context, List<ModulElement> modulElements, ModulElementOnClickHandler clickHandler){
        mInflater = LayoutInflater.from(context);
        context2 = context;
        this.modulElements = modulElements;
        mClickHandler = clickHandler;
    }

    public class ModulElementsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ModulElementsViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            ModulElement requestedModulElement = modulElements.get(adapterPosition);
            mClickHandler.onClick(requestedModulElement);
        }
    }



    @NonNull
    @Override
    public ModulElementsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_modul_detail_modulelement_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, attachImmediately);


        return new ModulElementsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModulDetailElementListAdapter.ModulElementsViewHolder holder, int position) {
        ModulElement currentModulElement = modulElements.get(position);

        String modulElementTitle = currentModulElement.getTitle();
        String timesMultiplied = ""+currentModulElement.getMultiplier().getTimesMultiplied()+" ";
        String multiplier_type = currentModulElement.getMultiplier().getType();
        String modulElementPictureUrl = currentModulElement.getPictureUrl();

        final ImageView imageView = holder.itemView.findViewById(R.id.iv_modul_detail_modulelement_picture);
        final ProgressBar progressBar = holder.itemView.findViewById(R.id.pb_modul_detail_modulelement_picture_load);
        final CardView cardView = holder.itemView.findViewById(R.id.cv_modul_detail_modulelement_picture);

        TextView titleView = holder.itemView.findViewById(R.id.tv_modul_detail_modulelement_title);
        if(modulElementPictureUrl == null){

        }else{
            imageView.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            CallbackHelper listenIfImageLoadedSuccessfullyHelper = new CallbackHelper() {
                @Override
                public void onSuccess() {
                    imageView.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure() {
                    Log.e("ModulElementPicture", "Not loaded correctly");
                }
            };
            PictureUtil pictureUtil = new PictureUtil(context2, imageView, listenIfImageLoadedSuccessfullyHelper);
            pictureUtil.saveElementThumbnailFromDatabaseToLocalStorage(storage, currentModulElement);

        }




        TextView timesMultipliedView = holder.itemView.findViewById(R.id.tv_modul_detail_modulelement_times_multiplied);
        TextView multiplierTypeView = holder.itemView.findViewById(R.id.tv_modul_detail_modulelement_multiplier_type);
        titleView.setText(modulElementTitle);
        timesMultipliedView.setText(timesMultiplied);
        multiplierTypeView.setText(multiplier_type);



    }

    public int getItemCount(){
        return modulElements.size();
    }



}
