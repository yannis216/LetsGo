package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.R;

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
            if(elements.get(getAdapterPosition()).isSelected()) {
                elements.get(getAdapterPosition()).setSelected(false);
            }else{
                elements.get(getAdapterPosition()).setSelected(true);
            }
            Log.e("Clicked Element", elements.get(getAdapterPosition()).toString());
            Log.e("Element Selected?", String.valueOf(elements.get(getAdapterPosition()).isSelected()));
            mClickHandler.onClick(elements.get(getAdapterPosition()));
            notifyDataSetChanged();
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
        CheckedTextView titleView = holder.itemView.findViewById(R.id.ctv_element_list_item_title);
        titleView.setText(elementTitle);
        if(currentElement.isSelected()){
            titleView.setCheckMarkDrawable(R.drawable.baseline_check_circle_black_24dp);
        }else{
            titleView.setCheckMarkDrawable(null);
        }

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }


}
