package com.example.android.letsgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.letsgo.Classes.ModulElement;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ModulElementDetailListAdapter extends RecyclerView.Adapter<ModulElementDetailListAdapter.ModulElementsViewHolder> {
    private List<ModulElement> modulElements;
    private LayoutInflater mInflater;
    private final ModulElementOnClickHandler mClickHandler;
    Context context2;

    public interface ModulElementOnClickHandler {
        void onClick(ModulElement requestedModulElement);
    }

    public ModulElementDetailListAdapter(Context context, List<ModulElement> modulElements, ModulElementOnClickHandler clickHandler){
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
    public void onBindViewHolder(@NonNull ModulElementDetailListAdapter.ModulElementsViewHolder holder, int position) {
        ModulElement currentModulElement = modulElements.get(position);
        String modulElementTitle = currentModulElement.getTitle();

        TextView titleView = holder.itemView.findViewById(R.id.tv_modul_detail_modulelement_title);
        titleView.setText(modulElementTitle);


    }

    public int getItemCount(){
        return modulElements.size();
    }



}
