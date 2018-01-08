package ca.mohawk.opendata.hamiltontourism.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.mohawk.opendata.hamiltontourism.LocationActivity;
import ca.mohawk.opendata.hamiltontourism.MainActivity;
import ca.mohawk.opendata.hamiltontourism.R;
import ca.mohawk.opendata.hamiltontourism.Restaurant;
import ca.mohawk.opendata.hamiltontourism.RestaurantActivity;
import ca.mohawk.opendata.hamiltontourism.RestaurantsActivity;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    private Context mContext;

    public RestaurantListAdapter(Context context, ArrayList<Restaurant> restaurants) {
        mContext = context;
        mRestaurants = restaurants;
    }

    @Override
    public RestaurantListAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_item, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantListAdapter.RestaurantViewHolder holder, int position) {
        holder.bindRestaurant(mRestaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }


    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.restaurantImageView)
        ImageView mRestaurantImageView;
        @Bind(R.id.restaurantNameTextView)
        TextView mNameTextView;
        @Bind(R.id.categoryTextView) TextView mCategoryTextView;
        @Bind(R.id.ratingTextView) TextView mRatingTextView;

        private Context mContext;

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            Intent intent = new Intent(view.getContext(), RestaurantActivity.class);
            intent.putExtra("restaurantIndex", position);
            mContext.startActivity(intent);

        }

        public void bindRestaurant(Restaurant restaurant) {
            if (!restaurant.getImageUrl().isEmpty()) {
                Picasso.with(mContext).load(restaurant.getImageUrl()).into(mRestaurantImageView);
            }
            mNameTextView.setText(restaurant.getName());
            mCategoryTextView.setText(restaurant.getCategories().get(0));
            mRatingTextView.setText("Rating: " + restaurant.getRating() + "/5");
        }
    }
}