package com.codrata.sturrd.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.codrata.sturrd.R;

public class AudioViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout audioLayout, audioContainer;

    public AudioViewHolder(View itemView) {
        super(itemView);
        audioLayout = itemView.findViewById(R.id.audio_layout);
        audioContainer = itemView.findViewById(R.id.audio_container);


    }
}

