package com.codrata.sturrd.Chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.codrata.sturrd.R;

import java.util.List;

/**
 * view holder to inflate the audio view bubble
 */

public class AudioRecyclerAdapter extends RecyclerView.Adapter<AudioRecyclerAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private List<AudioChat> audioList;
    private Context context;

    public AudioRecyclerAdapter(List<AudioChat> audioList, Context context) {
        this.audioList = audioList;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_audio_message, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return this.audioList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final LinearLayout mAudioContainer, audioLayout;
        public final ImageView audioImage;
        public final SeekBar seekBar;
        public final ImageView playImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mAudioContainer = itemView.findViewById(R.id.audio_container);
            audioImage = itemView.findViewById(R.id.audio_image_view);
            seekBar = itemView.findViewById(R.id.audio_seek_bar);
            playImage = itemView.findViewById(R.id.audio_play_button);
            audioLayout = itemView.findViewById(R.id.audio_layout);

        }
    }
}
