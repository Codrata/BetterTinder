package com.codrata.sturrd.Moments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codrata.sturrd.LikeProfile;
import com.codrata.sturrd.R;

public class MomentViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMomentName;
    public ImageView mMomentImage, mMomentImage1;
    public LinearLayout mLayout;

    public MomentViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mLayout = itemView.findViewById(R.id.layout_moments);
        mMomentName = itemView.findViewById(R.id.MomentName);
        mMomentImage = itemView.findViewById(R.id.MomentImage);
        mMomentImage1 = itemView.findViewById(R.id.MomentImage1);
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
