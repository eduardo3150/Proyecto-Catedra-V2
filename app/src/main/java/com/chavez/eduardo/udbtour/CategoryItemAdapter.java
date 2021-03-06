package com.chavez.eduardo.udbtour;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eduardo_Chavez on 11/5/2017.
 */

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.CategoryViewHolder> {
    ArrayList<Place> places = new ArrayList<>();
    Context context;
    ArrayList<String> category = new ArrayList<>();

    private Callback callback;

    public CategoryItemAdapter(ArrayList<Place> places, Context context, ArrayList<String> category) {
        this.places = places;
        this.context = context;
        this.category = category;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_row,parent,false);
        return new CategoryViewHolder(row);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        holder.categoryTitle.setText(category.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback !=null){
                    callback.onButtonClicked(category.get(position));
                    Log.d("Exito", "Feedback Capturado");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTitle = (TextView) itemView.findViewById(R.id.categoryTitle);

        }

        public TextView getCategoryTitle() {
            return categoryTitle;
        }

        public void setCategoryTitle(TextView categoryTitle) {
            this.categoryTitle = categoryTitle;
        }
    }


    /**CREANDO LISTENER **/
    public interface Callback{
        void onButtonClicked(String categoria);
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
