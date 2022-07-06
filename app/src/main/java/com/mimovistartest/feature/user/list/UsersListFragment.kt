package com.mimovistartest.feature.user.list

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.storage.FirebaseStorage
import com.mimovistartest.BuildConfig
import com.mimovistartest.R
import com.mimovistartest.base.BaseFragment
import com.mimovistartest.databinding.FragmentUserListBinding
import com.mimovistartest.util.*
import dagger.hilt.android.AndroidEntryPoint
import dev.skomlach.biometric.compat.*
import javax.inject.Inject

@AndroidEntryPoint
class UsersListFragment :
    BaseFragment<FragmentUserListBinding, UsersListViewModel>(UsersListViewModel::class) {

    override fun getLayoutId(): Int = R.layout.fragment_user_list

    override val viewModel: UsersListViewModel by viewModels()

    @Inject
    lateinit var firebaseTest: FirebaseTest
    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    override fun addBindingVariables() {
        super.addBindingVariables()
        with(binding) {
            viewModel = this@UsersListFragment.viewModel
            setLoadMoreClicked(this@UsersListFragment::onLoadMoreClicked)
            showTestButton = BuildConfig.SHOW_TEST_FIREBASE_BUTTON
        }
    }

    override fun init() {
        super.init()
        setUpView()
    }

    private fun setUpView() {
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

    private var handlerSearch = Handler(Looper.getMainLooper())
    private val runnableSearch = Runnable {
        viewModel.searchByNameOrEmail(binding.etSearch?.text?.toString() ?: "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (binding.showTestButton == true) startTestFirebaseDatabase()
        if (BuildConfig.SHOW_BIOMETRIC_INPUT)
            Handler(Looper.getMainLooper()).post {
                requireActivity().startMyBioAuth()
                //requireActivity().startBioAuth()
            }
    }

    private fun startTestFirebaseDatabase() {
        // CREATE ACTION FOR BUTTON
        binding.botonprueba?.setOnClickListener {
            Intent(Intent.ACTION_PICK).also { intent ->
                intent.type = "image/*"
                galleryActivityResult.launch(intent)
            }
        }

        //Upload from memory (imageView)
        binding.imageViewprueba?.isDrawingCacheEnabled = true
        binding.imageViewprueba?.buildDrawingCache()
        val bitmap = (binding.imageViewprueba?.drawable as BitmapDrawable).bitmap

        firebaseTest.startFirebaseTest(bitmap)
    }

    private val galleryActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Storage upload from mobile gallery
        val imageFile = result.data?.data
        val imagePath = result.data?.data?.lastPathSegment?.split("/")?.last() ?: "hola"
        val storageRef = firebaseStorage.reference
        val imagesRef = storageRef.child("images")
        val foodRef = imagesRef.child(imagePath)
        if (imageFile != null) {
            foodRef.putFile(imageFile).addOnSuccessListener {
                Log.d(TAG, " fichero subido con exito ${it.metadata?.path}")
            }.addOnFailureListener{
                Log.d(TAG, " fichero subido con error $it - ${it.message} - ${it.localizedMessage} - ${it.cause}")
            }
        }
    }
}