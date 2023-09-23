package com.example.recipeapplive

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapplive.databinding.FragmentHomeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList


class Home : Fragment() ,  RecipeAdapter.ItemClickListener {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!
    private lateinit var recyclerRecipes: RecyclerView
    private lateinit var arrRecipes : ArrayList<Recipe>
    private lateinit var recipeAdapter: RecipeAdapter
    private  lateinit var recipecard: CardView
    private  lateinit var fragment: Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(
            inflater, container, false
        )

        recyclerRecipes = binding.recyclerRecipes
        recyclerRecipes.layoutManager = LinearLayoutManager(context)
        recyclerRecipes.setHasFixedSize(true)


        showRecipes()


//        recipecard.setOnClickListener{
//
//        }



        return binding.root
    }

    fun showRecipes() {
        val db = Firebase.firestore
        arrRecipes = arrayListOf<Recipe>()

        db.collection("recipes").get()
            .addOnSuccessListener { result ->
                println("SHOW RECIPE SUCCESS")
                for (document in result) {
                    val recipeData = document.data
                    try {
                        if (recipeData != null) {
                            // Get the values of the recipe properties
                            val recipeName = recipeData["recipeName"] as String
                            val ingredients = recipeData["ingredients"] as String
                            val instructions = recipeData["instructions"] as String
                            val imageUrl = recipeData["imageUrl"] as String
                            val owner = recipeData["owner"] as String

                            // Create a new Recipe object
                            val recipe = Recipe(recipeName, ingredients, instructions, imageUrl, owner)

                            arrRecipes.add(recipe)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Set the adapter outside the loop
                if (arrRecipes.isNotEmpty()) {
                    println("arrRecipes ${arrRecipes[0].recipeName}")
                    recipeAdapter = RecipeAdapter(requireContext(), arrRecipes , this)
                    recyclerRecipes.adapter = recipeAdapter

                } else {
                    println("arrRecipes is empty")
                }
            }
            .addOnFailureListener { exception ->
                println("SHOW RECIPE SUCCESS  ${exception}")
            }
    }
    companion object {
        fun newInstance(): Fragment {
            return RecipePage()
        }
    }
    override fun onItemClick(recipe: Recipe?) {
        println("${recipe?.recipeName}")
    }
}