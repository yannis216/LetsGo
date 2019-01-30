package com.example.android.letsgo;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.Classes.ModulElementMultiplier;
import com.example.android.letsgo.Utils.TouchHelper.ItemTouchHelperAdapter;
import com.example.android.letsgo.Utils.TouchHelper.Listener.OnModulElementListChangedListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ModulElementEditListAdapter extends RecyclerView.Adapter<ModulElementEditListAdapter.ModulElementViewHolder> implements ItemTouchHelperAdapter {
    private List<ModulElement> modulElements;
    private LayoutInflater mInflater;
    private final ModulElementOnClickHandler mClickHandler;
    private Context context;
    private OnModulElementListChangedListener modulElementListChangedListener;

    public ModulElementEditListAdapter(Context context, List<ModulElement> modulElements, ModulElementOnClickHandler clickHandler, OnModulElementListChangedListener modulElementListChangedListener) {
        this.modulElements = modulElements;
        this.mInflater = LayoutInflater.from(context);
        this.mClickHandler=clickHandler;
        this.context = context;
        this.modulElementListChangedListener = modulElementListChangedListener;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(modulElements, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(modulElements, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        modulElementListChangedListener.onNoteListChanged(modulElements);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        modulElements.remove(position);
        modulElementListChangedListener.onNoteListChanged(modulElements);
        notifyItemRemoved(position);
    }


    public class ModulElementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public EditText mMultiplierEdit;
        public ModulElementEditTextListener editTextListener;
        public ModulElementSpinnerListener spinnerListener;
        public Spinner spinner;
        public String givenType;
        public String defaultType;
        public int DEFAULT_SPINNER_POSITION = 0;



        public ModulElementViewHolder(View view, ModulElementEditTextListener editTextListener, ModulElementSpinnerListener spinnerListener){
            super(view);
            view.setOnClickListener(this);


            //Edit Time Multiplied Code
            this.mMultiplierEdit = (EditText) view.findViewById(R.id.et_modul_element_edit_list_item_times_multiplied);
            this.editTextListener = editTextListener;
            this.mMultiplierEdit.addTextChangedListener(editTextListener);

            //Spinner Code
            this.spinnerListener =spinnerListener;
            this.spinner = view.findViewById(R.id.s_modul_element_edit_list_item_mutliplier_type);
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context, R.array.multiplier_type_array, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spinner.setAdapter(spinnerAdapter);
            this.spinner.setOnItemSelectedListener(spinnerListener);





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
        ModulElementViewHolder vh = new ModulElementViewHolder(view, new ModulElementEditTextListener(), new ModulElementSpinnerListener());
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ModulElementViewHolder holder, final int position) {
        ModulElement currentModulElement = modulElements.get(position);
        String modulElementTitle = currentModulElement.getTitle();

        holder.editTextListener.updatePosition(position);
        holder.spinnerListener.updatePosition(position);

        //TODO Think about setting these standards somewhere else
        //This makes the edittext show the given, pre-existing timeMultiplied
        if(modulElements.get(position).getMultiplier()==null){
            modulElements.get(position).setMultiplier(new ModulElementMultiplier());
        }
        if(modulElements.get(position).getMultiplier().getTimesMultiplied()==0){
           modulElements.get(position).getMultiplier().setTimesMultiplied(1);
        }
        holder.mMultiplierEdit.setText("" +modulElements.get(position).getMultiplier().getTimesMultiplied());

        //Setting Defaults for MultiplierType in case of new ModulElement
        String[] strings = context.getResources().getStringArray(R.array.multiplier_type_array);
        holder.defaultType = strings[holder.DEFAULT_SPINNER_POSITION];
        if(modulElements.get(position).getMultiplier().getType()==null){
            modulElements.get(position).getMultiplier().setType(holder.defaultType);
            holder.spinner.setSelection(holder.DEFAULT_SPINNER_POSITION);
        }
        //Make Sure that Spinner shows right selection also when multiplierType has already been set from Database
        else{
            String givenType = modulElements.get(position).getMultiplier().getType();
            int index = 0;
            for(int i = 0; i<strings.length; i++ ){
                if(givenType.equals(strings[i])) {
                    index = i;
                    break;
                }
            }
            holder.spinner.setSelection(index);
        }
        TextView titleView = holder.itemView.findViewById(R.id.tv_modul_element_edit_list_item_title);
        titleView.setText(modulElementTitle);
    }

    //TODO CHECK IF WORKS
    public List<ModulElement> getModulElements(){
        return this.modulElements;
    }

    @Override
    public int getItemCount() {
        return modulElements.size();
    }

    private class ModulElementEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int timesMultiplied;
            if(editable.toString().equals("")){
                timesMultiplied=Integer.parseInt("0");
            }else{
                timesMultiplied = Integer.parseInt(editable.toString());
            }

            if(modulElements.get(position).getMultiplier()==null){
                modulElements.get(position).setMultiplier(new ModulElementMultiplier());
            }
            modulElements.get(position).getMultiplier().setTimesMultiplied(timesMultiplied);
        }
    }

    private class ModulElementSpinnerListener implements AdapterView.OnItemSelectedListener {
        private int position;
        public void updatePosition(int position) {
            this.position = position;
        }
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String spinnerSelection = adapterView.getItemAtPosition(i).toString();
            if(modulElements.get(position).getMultiplier()==null){
                modulElements.get(position).setMultiplier(new ModulElementMultiplier());
            }
            modulElements.get(position).getMultiplier().setType(spinnerSelection);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }







}
