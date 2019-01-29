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

public class ModulElementEditListAdapter extends RecyclerView.Adapter<ModulElementEditListAdapter.ModulElementViewHolder> {
    private List<ModulElement> modulElements;
    private LayoutInflater mInflater;
    private final ModulElementOnClickHandler mClickHandler;
    private Context context;

    public ModulElementEditListAdapter(Context context, List<ModulElement> modulElements, ModulElementOnClickHandler clickHandler) {
        this.modulElements = modulElements;
        this.mInflater = LayoutInflater.from(context);
        this.mClickHandler=clickHandler;
        this.context = context;
    }

    public class ModulElementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ModulElementViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();
            ModulElement clickedModulElement = modulElements.get(adapterPosition);
            mClickHandler.onClick(clickedModulElement);

            //TODO Design: Make an Imageview that has previousply been gone to visible (with a green checkmark) or simply make it a checkbox


        }
    }

    public interface ModulElementOnClickHandler {
        void onClick(ModulElement clickedModulElement);
    }

    @NonNull
    @Override
    public ModulElementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_modul_element_edit_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, attachImmediately);
        return new ModulElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModulElementViewHolder holder, int position) {
        ModulElement currentModulElement = modulElements.get(position);
        String modulElementTitle = currentModulElement.getTitle();
        TextView titleView = holder.itemView.findViewById(R.id.tv_modul_element_edit_list_item_title);
        titleView.setText(modulElementTitle);

    }

    @Override
    public int getItemCount() {
        return modulElements.size();
    }
}
