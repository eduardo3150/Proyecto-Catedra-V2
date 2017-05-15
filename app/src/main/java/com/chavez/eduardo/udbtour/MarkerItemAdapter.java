package com.chavez.eduardo.udbtour;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Eduardo_Chavez on 11/5/2017.
 */

public class MarkerItemAdapter extends RecyclerView.Adapter<MarkerItemAdapter.MarkerViewHolder> implements Filterable {
    ArrayList<Place> places = new ArrayList<>();
    ArrayList<Place> mPlacesFilter = new ArrayList<>();
    ArrayList<Place> placeItem = new ArrayList<>();
    Context context;

    public MarkerItemAdapter(ArrayList<Place> places, Context context) {
        this.places = places;
        this.context = context;
        mPlacesFilter = places;
    }

    @Override
    public MarkerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_row, parent, false);
        return new MarkerViewHolder(row);
    }

    @Override
    public void onBindViewHolder(MarkerViewHolder holder, int position) {
        final Place place = mPlacesFilter.get(position);

        Picasso.with(context).load(place.getImagen()).fit().placeholder(R.drawable.loading).error(R.drawable.alert).into(holder.imageMarker);
        holder.markerName.setText(place.getNombre());
        holder.markerDescription.setText(place.getDescripcion());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeItem.clear();
                placeItem.add(place);
                if (place.getCategoria().equals("Sitios que recomiendo")) {
                    new AlertDialog.Builder(context)
                            .setTitle(place.getNombre())
                            .setMessage("¿Que desea hacer?")
                            .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(context, EditarCustom.class);
                                    intent.putExtra("ID_", place.getId());
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Ver", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(context, MapsActivity.class);
                                    intent.putExtra("Markers", placeItem);
                                    context.startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("Markers", placeItem);
                    context.startActivity(intent);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("Markers", mPlacesFilter);
                context.startActivity(intent);

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlacesFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if (charString.isEmpty()) {
                    mPlacesFilter = places;

                } else {
                    ArrayList<Place> filteredList = new ArrayList<>();
                    for (Place tmp : places) {
                        if (tmp.getNombre().toLowerCase().contains(charString)) {
                            filteredList.add(tmp);
                        }
                    }
                    mPlacesFilter = filteredList;

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mPlacesFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mPlacesFilter = (ArrayList<Place>) results.values;
                notifyDataSetChanged();
            }
        };
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
