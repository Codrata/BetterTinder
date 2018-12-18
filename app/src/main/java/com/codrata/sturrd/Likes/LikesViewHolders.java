package com.codrata.sturrd.Likes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codrata.sturrd.Fragments.CardFragment;
import com.codrata.sturrd.LikeProfile;
import com.codrata.sturrd.R;

public class LikesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mLikeName;
    public ImageView mLikeImage;
    public LinearLayout mLayout;

    public LikesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mLayout = itemView.findViewById(R.id.like_layout);
        mLikeName = itemView.findViewById(R.id.LikeName);
        mLikeImage = itemView.findViewById(R.id.LikeImage);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), LikeProfile.class);
        Bundle b = new Bundle();
        b.putString("likeId", mLayout.getTag().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
