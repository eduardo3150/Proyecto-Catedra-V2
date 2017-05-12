package com.chavez.eduardo.udbtour;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Eduardo_Chavez on 11/5/2017.
 */

public class MarkerItemAdapter extends RecyclerView.Adapter<MarkerItemAdapter.MarkerViewHolder> {
    ArrayList<Place> places = new ArrayList<>();
    Context context;

    public MarkerItemAdapter(ArrayList<Place> places, Context context) {
        this.places = places;
        this.context = context;
    }

    @Override
    public MarkerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_row,parent,false);
        return new MarkerViewHolder(row);
    }

    @Override
    public void onBindViewHolder(MarkerViewHolder holder, int position) {
        Place place = places.get(position);

        Picasso.with(context).load(place.getImagen()).fit().placeholder(R.drawable.loading).error(R.drawable.alert).into(holder.imageMarker);
        holder.markerName.setText(place.getNombre());
        holder.markerDescription.setText(place.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class MarkerViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageMarker;
        private TextView markerName;
        private TextView markerDescription;

        public MarkerViewHolder(View itemView) {
            super(itemView);
            imageMarker = (ImageView) itemView.findViewById(R.id.itemPicture);
            markerName = (TextView) itemView.findViewById(R.id.itemTitle);
            markerDescription = (TextView) itemView.findViewById(R.id.itemDescription);
        }

        public ImageView getImageMarker() {
            return imageMarker;
        }

        public void setImageMarker(ImageView imageMarker) {
            this.imageMarker = imageMarker;
        }

        public TextView getMarkerName() {
            return markerName;
        }

        public void setMarkerName(TextView markerName) {
            this.markerName = markerName;
        }

        public TextView getMarkerDescription() {
            return markerDescription;
        }

        public void setMarkerDescription(TextView markerDescription) {
            this.markerDescription = markerDescription;
        }
    }
}
