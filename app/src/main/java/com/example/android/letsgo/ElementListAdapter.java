package com.example.android.letsgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Element;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ElementListAdapter extends RecyclerView.Adapter<ElementListAdapter.ElementViewHolder> {
    private List<Element> elements;
    private LayoutInflater mInflater;
    private final ElementOnClickHandler mClickHandler;
    boolean isModulEditMode = false;
    private Context context;

    public ElementListAdapter(Context context, List<Element> elements, ElementOnClickHandler clickHandler, boolean isModulEditMode) {
        this.elements = elements;
        this.mInflater = LayoutInflater.from(context);
        this.mClickHandler=clickHandler;
        this.isModulEditMode=isModulEditMode;
        this.context = context;
    }

    public class ElementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ElementViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Element clickedElement = elements.get(adapterPosition);
            mClickHandler.onClick(clickedElement);
        }
    }

    public interface ElementOnClickHandler {
        void onClick(Element clickedElement);
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_element_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, attachImmediately);
        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        Element currentElement = elements.get(position);
        String elementTitle = currentElement.getTitle();
        TextView titleView = holder.itemView.findViewById(R.id.tv_element_list_item_title);
        if(isModulEditMode){
            int color = context.getResources().getColor(R.color.colorPrimary);
            holder.itemView.setBackgroundColor(color);
        }


        titleView.setText(elementTitle);

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }
}
