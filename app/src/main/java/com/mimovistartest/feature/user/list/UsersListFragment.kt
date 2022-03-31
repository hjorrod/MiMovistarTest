package com.mimovistartest.feature.user.list

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mimovistartest.R
import com.mimovistartest.base.BaseFragment
import com.mimovistartest.databinding.FragmentUserListBinding
import com.mimovistartest.util.*
import org.koin.android.ext.android.bind
import java.io.ByteArrayOutputStream
import java.io.File


class UsersListFragment :
    BaseFragment<FragmentUserListBinding, UsersListViewModel>(UsersListViewModel::class) {
    override fun getLayoutId(): Int = R.layout.fragment_user_list

    override fun addBindingVariables() {
        super.addBindingVariables()
        with(binding) {
            setLoadMoreClicked(this@UsersListFragment::onLoadMoreClicked)
        }
    }

    override fun init() {
        super.init()
        setUpView()
    }

    private fun setUpView() {
        binding.userList?.visibility = View.GONE

        //Set up the recycler view
        binding.userList?.let { rV ->
            (rV.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            val adapter = UserListAdapter(
                loadListener = {
                    viewModel.loading(false)
                },
                userClickListener = { userVO ->
                    val directions = UsersListFragmentDirections.navigateToUserDetail(userVO)
                    findNavController().navigate(directions)
                },
                favStarClickListener = { userVO ->
                    viewModel.handleFavEvent(userVO)
                },
                removeClickListener = {
                    viewModel.deleteUser(it)
                }
            )
            adapter.setHasStableIds(true)
            rV.adapter = adapter

            /** add listener to handle when show the Load_More button to load more users **/
            rV.addOnScrollListener(object :
                PaginationListener(rV.layoutManager as LinearLayoutManager) {
                override fun isFilterEnabled(): Boolean = viewModel.isFilterEnabled()
                override fun isLoading(): Boolean = viewModel.isLoading()
                override fun scrollCompleted() {
                    viewModel.scrollCompleted()
                }
            })

            /** SnapHelper is a helper class that helps in snapping any child view of the RecyclerView. */
            val snapHelper = StartSnapHelper()
            rV.onFlingListener = null
            snapHelper.attachToRecyclerView(rV)
        }

        binding.apply {
            /** set Listener to edit text to filter by name **/
            etSearch?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    handlerSearch.removeCallbacks(runnableSearch)
                    handlerSearch.postDelayed(runnableSearch, 1200)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            /** set Listener to checkBox to Sort the list by Name or by Gender **/
            cbSortByName?.setOnCheckedChangeListener { _, isChecked ->
                sortUsersList(isChecked, cbSortByGender?.isChecked ?: false)
            }
            cbSortByGender?.setOnCheckedChangeListener { _, isChecked ->
                sortUsersList(cbSortByName?.isChecked ?: false, isChecked)
            }
        }
    }

    private fun loadUsers() {
        viewModel.loadUsers()
    }

    private fun sortUsersList(byName: Boolean, byGender: Boolean) {
        viewModel.sortBy(when {
            byName && byGender -> SortType.GENDER_NAME
            byGender -> SortType.GENDER
            byName -> SortType.NAME
            else -> SortType.NONE
        })
    }

    /** on reload button clicked **/
    private fun onLoadMoreClicked(view: View) {
        loadUsers()
    }

    override fun onScreenRotated(savedInstanceState: Bundle) {
        super.onScreenRotated(savedInstanceState)
        setUpView()
    }

    override fun onResume() {
        super.onResume()
        startTestFirebaseDatabase()
    }

    private var handlerSearch = Handler(Looper.getMainLooper())
    private val runnableSearch = Runnable {
        viewModel.searchByNameOrEmail(binding.etSearch?.text?.toString() ?: "")
    }

    /**
     * INICIO PRUEBAS FIREBASE - STORAGE - DATABASE
     */

    private fun startTestFirebaseDatabase() {
        // CREATE ACTION FOR BUTTON
        binding.botonprueba?.setOnClickListener {
            Intent(Intent.ACTION_PICK).also { intent ->
                intent.type = "image/*"
                galleryActivityResult.launch(intent)
            }
        }

        Log.d("elfoco", " apsp ${ FirebaseApp.getApps(requireContext())}")

        // STORAGE
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images")

        //Upload from memory (imageView)
        binding.imageViewprueba?.isDrawingCacheEnabled = true
        binding.imageViewprueba?.buildDrawingCache()
        val bitmap = (binding.imageViewprueba?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val foodBis = imagesRef.child("foodBis.jpg")

        val uploadTask = foodBis.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d("elfoco", " fichero subido desde imageView con error $it - ${it.message} - ${it.localizedMessage} - ${it.cause}")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d("elfoco", " fichero subido con exito desde imageView")
        }

        // FIRESTORE
        val databaseRef = FirebaseFirestore.getInstance()
        databaseRef.collection("cities").document("Getafe")
            .set(City("Leganés", "España"))
            .addOnSuccessListener {
                Log.d("elfoco", " base de datos escrita con exito")
            }.addOnFailureListener {
                Log.d("elfoco", " base de datos escrita con error")
            }

        databaseRef.collection("cities")
            .whereEqualTo("country", "España")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("elfoco", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("elfoco", "Error getting documents: ", exception)
            }

        databaseRef.collection("cities")
            .addSnapshotListener { value, error ->
                Log.d("elfoco", "database change")
            }

        // REALTIME DATABASE
        val dRef = FirebaseDatabase.getInstance().reference
        dRef.child("users").child("miles").setValue(UserBis())
    }

    data class City (val name: String =  "Getafe", val country: String = "Spain")
    data class UserBis (val name: String =  "Michael", val first: String = "Miles", val born: Int = 1990)

    private val galleryActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Storage upload from mobile gallery
        val imageFile = result.data?.data
        val imagePath = result.data?.data?.lastPathSegment?.split("/")?.last() ?: "hola"
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images")
        val foodRef = imagesRef.child(imagePath)
        if (imageFile != null) {
            foodRef.putFile(imageFile).addOnSuccessListener {
                Log.d("elfoco", " fichero subido con exito ${it.metadata?.path}")
            }.addOnFailureListener{
                Log.d("elfoco", " fichero subido con error $it - ${it.message} - ${it.localizedMessage} - ${it.cause}")
            }
        }
    }

    /**
     * FIN PRUEBAS FIREBASE - STORAGE - DATABASE
     */
}