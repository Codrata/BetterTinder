package com.codrata.sturrd.Explore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codrata.sturrd.LikeProfile;
import com.codrata.sturrd.R;

public class ExploreViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mName, mDistance;
    public ImageView mImage;
    public LinearLayout mExploreLayout;

    public ExploreViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mExploreLayout = itemView.findViewById(R.id.explore_cards);
        mName = itemView.findViewById(R.id.nameExplore);
        mDistance = itemView.findViewById(R.id.distanceExplore);
        mImage = itemView.findViewById(R.id.imageEplore);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), LikeProfile.class);
        Bundle b = new Bundle();
        b.putString("likeId", mExploreLayout.getTag().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);

    }
}
