package com.example.recipeapplive
class User(
    var userId: String = "", // User's ID
    var userEmail: String = "", // User's email
    var recipes: MutableList<String> = mutableListOf() // Array to store recipe IDs
)