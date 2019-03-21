package com.codrata.sturrd.Explore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codrata.sturrd.Likes.LikesViewHolders;
import com.codrata.sturrd.R;

import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreViewHolders> {
    private List<ExploreObject> exploreList;
    private Context context;

    public ExploreAdapter(List<ExploreObject> exploreList, Context context){
        this.exploreList = exploreList;
        this.context = context;
    }
    @Override
    public ExploreViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explor_card, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ExploreViewHolders rcv = new ExploreViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(ExploreViewHolders holder, int position) {
        holder.mExploreLayout.setTag(exploreList.get(position).getUserId());
        holder.mName.setText(exploreList.get(position).getName());
        if(!exploreList.get(position).getProfileImageUrl().equals("default"))
            Glide.with(context).load(exploreList
                    .get(position).getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mImage);

    }

    @Override
    public int getItemCount() {
        return this.exploreList.size();
    }
}
