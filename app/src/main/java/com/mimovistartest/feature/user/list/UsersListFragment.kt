package com.mimovistartest.feature.user.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mimovistartest.R
import com.mimovistartest.base.BaseFragment
import com.mimovistartest.databinding.FragmentUserListBinding
import com.mimovistartest.util.*
import org.koin.android.ext.android.bind
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
        binding.botonprueba?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
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

        Log.d("elfoco", " apsp ${ FirebaseApp.getApps(requireContext())}")

        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images")
        val foodRef = imagesRef.child("food2.jpg")
        val path = Uri.parse("android.resource://com.movistartest/assets/food.jpg")// + R.drawable.mimovistartest_logo)
        val file = Uri.fromFile(File("C:Users/jorge/AndroidStudioProjects/MiMovistarTest/app/food.jpg"))
        val fileBis = Uri.fromFile(File(path.path))
        Log.d("elfoco", " subiendo fichero ${file.path} - $path")
        foodRef.putFile(fileBis).addOnSuccessListener {
            Log.d("elfoco", " fichero subido con exito")
        }.addOnFailureListener{
            Log.d("elfoco", " fichero subido con error $it - ${it.message} - ${it.localizedMessage} - ${it.cause}")
        }
        val uri = storageRef.child("images/food.jpg").downloadUrl.addOnSuccessListener {
            Log.d("elfoco", " fichero descargado con exito ${it.path}")
            it.path
        }.addOnFailureListener {
            Log.d("elfoco", " fichero descargado con error")
        }


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
    }

    data class City (val name: String =  "Getafe", val country: String = "Spain")

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageFile = data?.data
        val imagePath = data?.data?.lastPathSegment?.split("/")?.last() ?: "hola"
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

    private var handlerSearch = Handler(Looper.getMainLooper())
    private val runnableSearch = Runnable {
        viewModel.searchByNameOrEmail(binding.etSearch?.text?.toString() ?: "")
    }
}