package com.example.android.letsgo.Utils.TouchHelper;

public interface ItemTouchHelperAdapter {


    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
