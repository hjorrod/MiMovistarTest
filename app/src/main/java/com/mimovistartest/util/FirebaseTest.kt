package com.mimovistartest.util

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class FirebaseTest (
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseDatabase: FirebaseDatabase
) {
    fun startFirebaseTest(bitmap: Bitmap) {
        // STORAGE
        val storageRef = firebaseStorage.reference
        val imagesRef = storageRef.child("images")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val foodBis = imagesRef.child("foodBis.jpg")

        val uploadTask = foodBis.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d(TAG, " fichero subido desde imageView con error $it - ${it.message} - ${it.localizedMessage} - ${it.cause}")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d(TAG, " fichero subido con exito desde imageView")
        }

        // FIRESTORE
        firebaseFirestore.collection("cities").document("Getafe")
            .set(City("Leganés", "España"))
            .addOnSuccessListener {
                Log.d(TAG, " base de datos escrita con exito")
            }.addOnFailureListener {
                Log.d(TAG, " base de datos escrita con error")
            }

        firebaseFirestore.collection("cities")
            .whereEqualTo("country", "España")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        firebaseFirestore.collection("cities")
            .addSnapshotListener { value, error ->
                Log.d(TAG, "database change")
            }

        // REALTIME DATABASE
        val dRef = firebaseDatabase.reference
        dRef.child("users").child("miles").setValue(UserBis())
    }
}

data class City (val name: String =  "Getafe", val country: String = "Spain")
data class UserBis (val name: String =  "Michael", val first: String = "Miles", val born: Int = 1990)
