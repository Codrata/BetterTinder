package com.codrata.sturrd.Likes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codrata.sturrd.Likes.LikesObject;
import com.codrata.sturrd.Likes.LikesViewHolders;
import com.codrata.sturrd.Likes.LikesAdapter;
import com.codrata.sturrd.R;

import java.util.List;

public class LikesAdapter extends RecyclerView.Adapter<LikesViewHolders>{
    private List<LikesObject> likeList;
    private Context context;


    public LikesAdapter(List<LikesObject> likeList, Context context){
        this.likeList = likeList;
        this.context = context;
    }

    @Override
    public LikesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like_list_fragment, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        LikesViewHolders rcv = new LikesViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(LikesViewHolders holder, int position) {
        holder.mLayout.setTag(likeList.get(position).getUserId());
        holder.mLikeName.setText(likeList.get(position).getName());
        if(!likeList.get(position).getProfileImageUrl().equals("default"))
            Glide.with(context).load(likeList.get(position).getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mLikeImage);

    }

    @Override
    public int getItemCount() {
        return this.likeList.size();
    }
}

