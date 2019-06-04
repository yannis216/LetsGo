package com.example.android.letsgo.Adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.letsgo.Classes.Element;
import com.example.android.letsgo.R;
import com.example.android.letsgo.Utils.CallbackHelper;
import com.example.android.letsgo.Utils.PictureUtil;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ElementListAdapter extends RecyclerView.Adapter<ElementListAdapter.ElementViewHolder> {
    private List<Element> elements;
    private LayoutInflater mInflater;
    private final ElementOnClickHandler mClickHandler;
    boolean isModulEditMode = false;
    private Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();


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

        String elementAuthor = currentElement.getCreatorName();
        String authorNameViewText = "by " + elementAuthor;
        TextView authorView = holder.itemView.findViewById(R.id.tv_element_list_item_editor_name);
        authorView.setText(authorNameViewText);

        LinearLayout usedForLinearLayout = holder.itemView.findViewById(R.id.ll_element_list_usedFor_layout);
        usedForLinearLayout.removeAllViews();
        List<String> usedForStrings = currentElement.getUsedFor();
        generateUsedForChips(usedForStrings, usedForLinearLayout);

        String elementPictureUrl =currentElement.getPictureUrl();
        final ImageView elementThumbView = holder.itemView.findViewById(R.id.iv_element_list_item_thumb);
        final ProgressBar progressBar = holder.itemView.findViewById(R.id.pb_element_list_picture_load);
        if(elementPictureUrl == null){
            elementThumbView.setImageDrawable(null);
        }else{
            elementThumbView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            CallbackHelper listenIfImageLoadedSuccessfullyHelper = new CallbackHelper() {
                @Override
                public void onSuccess() {
                    elementThumbView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure() {
                    Log.e("ElementPicture", "Not loaded correctly");
                }
            };
            PictureUtil pictureUtil = new PictureUtil(context, elementThumbView, listenIfImageLoadedSuccessfullyHelper);
            pictureUtil.saveElementThumbnailFromDatabaseToLocalStorage(storage, currentElement);

        }
        if(currentElement.isSelected()){
            titleView.setCheckMarkDrawable(R.drawable.ic_check_primarydark_16dp);
        }else{
            titleView.setCheckMarkDrawable(null);
        }

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    private void generateUsedForChips(List<String> usedForStrings, LinearLayout usedForLinearLayout){
        int count = 0;
        for(String s : usedForStrings){
            if(count>2){
                count=0;
                break;
            }
            //Builds the Textview that holds the usedfor Strings
            TextView textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,20,0);
            textView.setLayoutParams(params);
            textView.setBackgroundColor(context.getResources().getColor(R.color.backgroundUsedForChips));
            textView.setPadding(5,0,5,0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setText("#"+s);
            usedForLinearLayout.addView(textView);
            count = count+1;
        }
    }


}
