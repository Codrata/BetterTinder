package com.codrata.sturrd.Chat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codrata.sturrd.R;

import java.util.List;

/**
 * Created by manel on 10/31/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int AUDIO_MESSAGE = 0, TEXT_MESSAGE = 1;
    private Context context;
    private List<Object> chatList;

    public ChatAdapter(List<Object> matchesList, Context context) {
        this.chatList = matchesList;
        this.context = context;
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position) instanceof AudioMessage) {
            return AUDIO_MESSAGE;
        } else if (chatList.get(position) instanceof ChatObject) {
            return TEXT_MESSAGE;
        }
        return -1;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {

            case AUDIO_MESSAGE:
                View v1 = inflater.inflate(R.layout.item_audio_message, viewGroup, false);
                viewHolder = new AudioViewHolder(v1);
                break;
            case TEXT_MESSAGE:
                View v2 = inflater.inflate(R.layout.item_message, viewGroup, false);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                v2.setLayoutParams(lp);
                viewHolder = new ChatViewHolders(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.item_audio_message, viewGroup, false);
                RecyclerView.LayoutParams l = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(l);
                viewHolder = new ChatViewHolders(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TEXT_MESSAGE:
                ChatViewHolders vh1 = (ChatViewHolders) holder;
                configureChatViewHolder(vh1, position);
                break;
            case AUDIO_MESSAGE:
                AudioViewHolder vh2 = (AudioViewHolder) holder;
                configureAudioViewHolder(vh2, position);
                break;
        }

    }


    private void configureChatViewHolder(ChatViewHolders holder, int position) {
        ChatObject chatObjectList = (ChatObject) chatList.get(position);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.mMessage.getLayoutParams();
        params.leftMargin = 20; params.rightMargin = 20;
        holder.mMessage.setLayoutParams(params);

        holder.mMessage.setText(chatObjectList.getMessage());


        if (chatObjectList.getCurrentUser()) {

            holder.mMessage.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

            holder.mLayout.setGravity(Gravity.END);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                holder.mContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.message_right));


        }else{


            holder.mMessage.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));

            holder.mLayout.setGravity(Gravity.START);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                holder.mContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.message_left));

        }


    }

    private void configureAudioViewHolder(AudioViewHolder holder, int position) {
        AudioMessage audioMessageList = (AudioMessage) chatList.get(position);

        if (audioMessageList.getAudioUrl() != null) {
            holder.audioLayout.setGravity(Gravity.END);
        } else {

            holder.audioLayout.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
