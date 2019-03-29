package com.codrata.sturrd.Chat;

import android.content.Context;
import android.os.Build;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders>{
    private List<ChatObject> chatList;
    private Context context;


    public ChatAdapter(List<ChatObject> matchesList, Context context){
        this.chatList = matchesList;
        this.context = context;
    }

    @Override
    public ChatViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(ChatViewHolders holder, int position) {

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.mMessage.getLayoutParams();
        params.leftMargin = 20; params.rightMargin = 20;
        holder.mMessage.setLayoutParams(params);

        holder.mMessage.setText(chatList.get(position).getMessage());


        if(chatList.get(position).getCurrentUser()){

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


    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
