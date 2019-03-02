package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ModulListAdapter extends RecyclerView.Adapter<ModulListAdapter.ModulsViewHolder> {
    private List<Modul> moduls;
    private LayoutInflater mInflater;
    private final ModulOnClickHandler mClickHandler;
    Context context2;

    public interface ModulOnClickHandler {
        void onClick(Modul requestedModul);
    }

    public ModulListAdapter(Context context, List<Modul> moduls, ModulOnClickHandler clickHandler){
        mInflater = LayoutInflater.from(context);
        context2 = context;
        this.moduls = moduls;
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
        String modulTitle = currentModul.getTitle();

        TextView titleView = holder.itemView.findViewById(R.id.tv_modul_list_modul_title);
        titleView.setText(modulTitle);


    }

    public int getItemCount(){
        return moduls.size();
    }



}
