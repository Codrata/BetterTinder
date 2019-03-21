package com.codrata.sturrd.Matches;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codrata.sturrd.Cards.cardObject;
import com.codrata.sturrd.R;

import java.util.List;



public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders>{
    private List<MatchesObject> matchesList;
    private Context context;


    public MatchesAdapter(List<MatchesObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }



    @Override
    public MatchesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(MatchesViewHolders holder, int position) {

        String distanceMatches = matchesList.get(position).getDistance();
        holder.mLayout.setTag(matchesList.get(position).getUserId());
        holder.mMatchName.setText(matchesList.get(position).getName() + ", " + distanceMatches + " km away");
        if(!matchesList.get(position).getProfileImageUrl().equals("default"))
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mMatchImage);

    }


    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}
