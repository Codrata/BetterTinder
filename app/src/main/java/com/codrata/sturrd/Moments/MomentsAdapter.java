package com.codrata.sturrd.Moments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codrata.sturrd.Likes.LikesObject;
import com.codrata.sturrd.Likes.LikesViewHolders;
import com.codrata.sturrd.R;

import java.util.List;

public class MomentsAdapter extends RecyclerView.Adapter<MomentViewHolders>{
    private List<MomentsObject> momentsList;
    private Context context;


    public MomentsAdapter(List<MomentsObject> momentList, Context context){
        this.momentsList = momentList;
        this.context = context;
    }

    @Override
    public MomentViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moments, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutView.setLayoutParams(lp);
        MomentViewHolders rcv = new MomentViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(MomentViewHolders holder, int position) {
        holder.mLayout.setTag(momentsList.get(position).getUserId());
        holder.mMomentName.setText(momentsList.get(position).getName());
        if(!momentsList.get(position).getProfileImageUrl().equals("default"))
            Glide.with(context).load(momentsList.get(position).getProfileImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.mMomentImage);
        if(!momentsList.get(position).getProfileImageUrl().equals("default"))
            Glide.with(context).load(momentsList.get(position).getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mMomentImage1);

    }

    @Override
    public int getItemCount() {
        return this.momentsList.size();
    }
}

