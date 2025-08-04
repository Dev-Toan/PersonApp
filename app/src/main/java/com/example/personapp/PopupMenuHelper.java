package com.example.personapp;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class PopupMenuHelper {

    public interface PopupMenuListener {
        void onEditClick(Event event, int position);
        void onDeleteClick(Event event, int position);
    }

    public static void showPopupMenu(Context context, View anchorView, Event event, int position, PopupMenuListener listener) {
        // Inflate popup layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.popup_event_menu, null);

        // Create popup window
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Set background drawable
        popupWindow.setBackgroundDrawable(context.getDrawable(android.R.color.transparent));

        // Set elevation
        popupWindow.setElevation(10f);

        // Find buttons
        TextView btnEdit = popupView.findViewById(R.id.btnEdit);
        TextView btnDelete = popupView.findViewById(R.id.btnDelete);

        // Set click listeners
        btnEdit.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (listener != null) {
                listener.onEditClick(event, position);
            }
        });

        btnDelete.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (listener != null) {
                listener.onDeleteClick(event, position);
            }
        });

        // Show popup
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.getHeight());
    }
}