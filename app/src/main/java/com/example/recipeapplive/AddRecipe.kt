package com.example.recipeapplive
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.recipeapplive.databinding.FragmentAddRecipeBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID


class AddRecipe : Fragment() {
    private lateinit var _binding: FragmentAddRecipeBinding
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var imageViewRecipe: ImageView
    private lateinit var buttonSelectImage: MaterialButton
    private var selectedImageUri: Uri? = null
    private var downloadImageUri: String? = null
    //    private lateinit var editTextRecipeName :
//    private lateinit var editTextIngredients
//    private lateinit var editTextInstructions
    private lateinit var saveRecipeButton : AppCompatButton
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddRecipeBinding.inflate(
            inflater, container, false
        )

        auth = FirebaseAuth.getInstance()
        val db = Firebase.firestore




        imageViewRecipe = binding.imageViewRecipe
        buttonSelectImage = binding.buttonSelectImage

        buttonSelectImage.setOnClickListener {
            openGallery()
        }



        saveRecipeButton = binding.saveRecipeButton
        saveRecipeButton.setOnClickListener {
            val RecipeName = binding.editTextRecipeName.text.toString()
            val Ingredients = binding.editTextIngredients.text.toString()
            val Instructions = binding.editTextInstructions.text.toString()


            println("RecipeImage : ${selectedImageUri}")


            if (selectedImageUri != null && RecipeName.length>0 && Ingredients.length>0 && Instructions.length>0) {
                uploadImageToCloudStorage()
            }
            else{
                Toast.makeText(context, "Please Select an Image and add all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root

    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    private fun uploadImageToCloudStorage(){
        val progressDialog = ProgressDialog(activity)
        // on below line setting title and message for our progress dialog and displaying our progress dialog.
        progressDialog.setTitle("Uploading...")
        progressDialog.setMessage("Uploading your image..")
        progressDialog.show()


        val ref: StorageReference = FirebaseStorage.getInstance().getReference()
            .child(UUID.randomUUID().toString())

        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->

                progressDialog.dismiss()

                taskSnapshot.storage.downloadUrl
                    .addOnSuccessListener { uri ->
                        downloadImageUri = uri.toString()
                        savetodatabase()
                    }
            }.addOnFailureListener { exception ->
                downloadImageUri = null
                progressDialog.dismiss()
                Toast.makeText(context, "Fail to Upload Image.. ${exception}", Toast.LENGTH_SHORT)
                    .show()
                println("Image upload failed: $exception")
            }
    }
    private fun savetodatabase() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Saving Recipe")
        progressDialog.setMessage("Please wait...")
        progressDialog.show()

        val RecipeName = binding.editTextRecipeName.text.toString()
        val Ingredients = binding.editTextIngredients.text.toString()
        val Instructions = binding.editTextInstructions.text.toString()
        val userId = auth.currentUser?.uid
        // Create a Firestore document to store the recipe data
        val recipe = hashMapOf(
            "recipeName" to RecipeName,
            "ingredients" to Ingredients,
            "instructions" to Instructions,
            "imageUrl" to downloadImageUri,
            "owner" to userId
        )

        Firebase.firestore.collection("recipes")
            .add(recipe)
            .addOnSuccessListener { documentReference ->
                progressDialog.dismiss()
                Toast.makeText(context, "Recipe added successfully", Toast.LENGTH_SHORT).show()
//                println("Recipe added with ID: ${documentReference.id}")

                saveRecipeIdToProfile(documentReference.id.toString())
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(context, "Failed to add Recipe", Toast.LENGTH_SHORT).show()
                println("Error adding recipe: $e")
            }
    }


    private fun saveRecipeIdToProfile(recipeId: String){
        val userId = auth.currentUser?.uid
        val userEmail = auth.currentUser?.email
        println("saveRecipeIdToProfile added with ID: ${recipeId}")


        if (userId != null && userEmail != null) {
            val db = Firebase.firestore
            val usersCollection = db.collection("users")

            // Check if the user document exists
            usersCollection.document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // User document exists, update the recipe array
                        val user = documentSnapshot.toObject(User::class.java)
                        if (user != null) {
                            user.recipes.add(recipeId)
                            usersCollection.document(userId).set(user)
                                .addOnSuccessListener {
                                    // Recipe ID added to user document
                                    println("Recipe ID added to user document")
                                }
                                .addOnFailureListener { e ->
                                    println("Failed to update user document: $e")
                                }
                        }
                    } else {
                        // User document doesn't exist, create a new one
                        val newUser = User(userId, userEmail, mutableListOf(recipeId))
                        usersCollection.document(userId).set(newUser)
                            .addOnSuccessListener {
                                // New user document created with the recipe ID
                                println("New user document created with the recipe ID")
                            }
                            .addOnFailureListener { e ->
                                println("Failed to create a new user document: $e")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("Failed to check user document: $e")
                }
        } else {
            println("User not authenticated or missing user ID or email")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri= data.data
            println(selectedImageUri)
            // Now you can use the selectedImageUri to display or process the selected image
            if (selectedImageUri != null) {
                imageViewRecipe.setImageURI(selectedImageUri)
            }
        }
    }

}