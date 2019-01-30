package com.codrata.sturrd.ChatList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codrata.sturrd.Chat.ChatActivity;
import com.codrata.sturrd.R;

public class ChatListViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mName,
                    mMessage;
    public ImageView mImage;
    public LinearLayout mLayout;
    public ChatListViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mLayout = itemView.findViewById(R.id.layout);
        mName = itemView.findViewById(R.id.name);
        mMessage = itemView.findViewById(R.id.message);
        mImage = itemView.findViewById(R.id.image);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mLayout.getTag().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
