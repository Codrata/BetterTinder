package com.codrata.sturrd.Cards;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codrata.sturrd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class cardAdapter extends ArrayAdapter<cardObject> {

    Context context;
    DatabaseReference usersDb;
    String currentUId;
    FirebaseAuth mAuth;

    public cardAdapter(Context context, int resourceId, List<cardObject> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final cardObject card_item = getItem(position);

        mAuth = FirebaseAuth.getInstance();

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUId = mAuth.getCurrentUser().getUid();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false);
        }
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final DatabaseReference userDb = usersDb.child(card_item.getUserId());
        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String latitudeA = dataSnapshot.child(currentUId).child("LatLng").child("latitude").getValue().toString();
                final String longitudeA = dataSnapshot.child(currentUId).child("LatLng").child("longitude").getValue().toString();
                userDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String latitudeB = dataSnapshot.child("LatLng").child("latitude").getValue().toString();
                        String longitudeB = dataSnapshot.child("LatLng").child("longitude").getValue().toString();

                        Location locationA = new Location("point A");
                        locationA.setLatitude(Double.parseDouble(latitudeA));
                        locationA.setLongitude(Double.parseDouble(longitudeA));
                        Location locationB = new Location("point B");
                        locationB.setLatitude(Double.parseDouble(latitudeB));
                        locationB.setLongitude(Double.parseDouble(longitudeB));

                        double distance = locationA.distanceTo(locationB);

                        //TODO correct the distance
                        //convert distance to km
                        distance = ((int) Math.round(distance / 1000));
                        String finalDist = String.valueOf(distance);

                        userDb.child("LatLng").child(currentUId).child("distance").setValue(finalDist);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);
        TextView distance = convertView.findViewById(R.id.distance);

        name.setText(card_item.getName() + ", " + card_item.getAge());
        distance.setText(card_item.getDistance() + " km");

        if (!card_item.getProfileImageUrl().equals("default"))
            Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);




        return convertView;

    }





}
