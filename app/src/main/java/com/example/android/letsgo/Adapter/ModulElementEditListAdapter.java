package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.letsgo.Activities.ElementDetailActivity;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.Classes.ModulElementMultiplier;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.TouchHelper.ItemTouchHelperAdapter;
import com.example.android.letsgo.Utils.TouchHelper.Listener.OnModulElementListChangedListener;
import com.example.android.letsgo.Utils.TouchHelper.Listener.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ModulElementEditListAdapter extends RecyclerView.Adapter<ModulElementEditListAdapter.ModulElementViewHolder> implements ItemTouchHelperAdapter {
    private List<ModulElement> modulElements;
    private LayoutInflater mInflater;
    private final ModulElementOnClickHandler mClickHandler;
    private Context context;
    private OnModulElementListChangedListener modulElementListChangedListener;
    private final OnStartDragListener mDragStartListener;

    private ClickAdapterListener listener;
    private SparseBooleanArray selectedItems;
    ModulElementViewHolder vh;


    private static int currentSelectedIndex = -1;

    public ModulElementEditListAdapter(Context context, List<ModulElement> modulElements,
                                       ModulElementOnClickHandler clickHandler,
                                       OnModulElementListChangedListener modulElementListChangedListener,
                                       OnStartDragListener dragStartListener, ClickAdapterListener listener){
        this.modulElements = modulElements;
        this.mInflater = LayoutInflater.from(context);
        this.mClickHandler=clickHandler;
        this.context = context;
        this.modulElementListChangedListener = modulElementListChangedListener;
        mDragStartListener = dragStartListener;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(modulElements, i, i + 1);
                Log.e("ListModulElement", modulElements.toString());
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(modulElements, i, i - 1);
                Log.e("ListModulElement", modulElements.toString());
            }
        }
        //Needed to update Listeners (e.g. TextChangedListener) so they update the right Modulelement
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
        modulElementListChangedListener.onNoteListChanged(modulElements);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        modulElements.remove(position);
        modulElementListChangedListener.onNoteListChanged(modulElements);
        notifyItemRemoved(position);
        //Added this to remove bug that crashes app with index out of bounds error after removing Items
        notifyItemRangeChanged(position, modulElements.size() );

    }


    public class ModulElementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public EditText mMultiplierEdit;
        public Spinner spinner;
        public ModulElementEditTextListener editTextListener;
        public ModulElementSpinnerListener spinnerListener;
        public String givenType;
        public String defaultType;
        public int DEFAULT_SPINNER_POSITION = 0;
        ImageView handleView;
        LinearLayout mLayout;



        public ModulElementViewHolder(View view, ModulElementEditTextListener editTextListener, ModulElementSpinnerListener spinnerListener){
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);


            //Edit Time Multiplied Code
            this.mMultiplierEdit = (EditText) view.findViewById(R.id.et_modul_element_edit_list_item_times_multiplied);
            this.editTextListener = editTextListener;
            this.mMultiplierEdit.addTextChangedListener(editTextListener);
            this.mLayout = view.findViewById(R.id.ll_modul_element_edit_list_item);

            handleView=(ImageView) view.findViewById(R.id.iv_modul_element_edit_list_handle);

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

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
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
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        vh = new ModulElementViewHolder(view, new ModulElementEditTextListener(), new ModulElementSpinnerListener());
        return vh;
    }

    @Override
    public void onBindViewHolder(final @NonNull ModulElementViewHolder holder, final int position) {
        ModulElement currentModulElement = modulElements.get(position);
        String modulElementTitle = currentModulElement.getTitle();
        Log.e("OnBinViewHolder", "Was called");

        holder.editTextListener.updatePosition(holder.getAdapterPosition());
        holder.spinnerListener.updatePosition(holder.getAdapterPosition());

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

        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) ==
                        MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.itemView.setActivated(selectedItems.get(position, false));
        applyClickEvents(holder, position);


    }

    private void applyClickEvents(ModulElementViewHolder holder, final int position) {
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedItems.size()>0) {
                    listener.onRowClicked(position);
                }else{
                    Intent intent = new Intent(context, ElementDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("element", modulElements.get(position));
                    context.startActivity(intent);
                }
            }
        });

        holder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //TODO Observe if any bugs regarding textview Focus come up.
                //The Code above may have resolved the issue - if not, the one below will be a (unperfect) solution
                //ViewParent parent = view.getParent();
                //parent.clearChildFocus(view);
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                Log.e("OnLongClickAdapter", "Has Fired");
                return true;
            }
        });
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
            updatePosition(position);
            int timesMultiplied;
            if(editable.toString().equals("")){
                timesMultiplied=Integer.parseInt("0");
            }else{
                timesMultiplied = Integer.parseInt(editable.toString());
            }

            modulElements.get(position).getMultiplier().setTimesMultiplied(timesMultiplied);
        }
    }

    public class ModulElementSpinnerListener implements AdapterView.OnItemSelectedListener {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
            vh.getAdapterPosition();
        }
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            this.updatePosition(position);
            updatePosition(position);
            Log.e("Position", ""+position);
            String spinnerSelection = adapterView.getItemAtPosition(i).toString();
            modulElements.get(position).getMultiplier().setType(spinnerSelection);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }


    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List getSelectedItems() {
        List items =
                new ArrayList(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        modulElements.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }


    public interface ClickAdapterListener {

        void onRowClicked(int position);

        void onRowLongClicked(int position);
    }







}
