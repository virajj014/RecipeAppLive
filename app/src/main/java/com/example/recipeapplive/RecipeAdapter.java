package com.example.recipeapplive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecipeAdapter  extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private  ItemClickListener clickListener;
    private Context context;
    private ArrayList<Recipe> arrRecipes = new ArrayList<>();

    public RecipeAdapter(Context context, ArrayList<Recipe> arrRecipes, ItemClickListener clickListener) {
        this.context = context;
        this.arrRecipes = arrRecipes;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = arrRecipes.get(position);

        holder.recipeNameTextView.setText(recipe.getRecipeName());
        holder.ingredientsTextView.setText(recipe.getIngredients());
        holder.instructionsTextView.setText(recipe.getInstructions());
        loadImageFromUrl(recipe.getImageUrl(), holder.recipeImageView);
holder.recipeCard.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        clickListener.onItemClick(recipe);
    }
});
    }

    @Override
    public int getItemCount() {
        return arrRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView recipeNameTextView, ingredientsTextView, instructionsTextView;
        ImageView recipeImageView;
CardView recipeCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeCard = itemView.findViewById(R.id.recipecard);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            ingredientsTextView = itemView.findViewById(R.id.ingredientsTextView);
            instructionsTextView = itemView.findViewById(R.id.instructionsTextView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
        }
    }


    private void loadImageFromUrl(String imageUrl, ImageView imageView) {
        // Implement your image loading logic here, e.g., using Glide or Picasso
       if(imageUrl != null){
           Glide.with(context).load(imageUrl).placeholder(R.drawable.noimage).error(R.drawable.noimage).into(imageView);
       }
    }

 public interface ItemClickListener{
        public void onItemClick(Recipe recipe);
 }
}
