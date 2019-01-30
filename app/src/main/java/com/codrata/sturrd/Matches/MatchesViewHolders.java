package com.codrata.sturrd.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codrata.sturrd.Chat.ChatActivity;
import com.codrata.sturrd.R;


public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMatchName;
    public ImageView mMatchImage;
    public LinearLayout mLayout;
    public MatchesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mLayout = itemView.findViewById(R.id.layout);
        mMatchName = itemView.findViewById(R.id.MatchName);
        mMatchImage = itemView.findViewById(R.id.MatchImage);
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
