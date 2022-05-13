package com.mimovistartest.feature.user.list

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import com.mimovistartest.R
import com.mimovistartest.base.BaseFragment
import com.mimovistartest.databinding.FragmentUserListBinding
import com.mimovistartest.util.StartSnapHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersListTabletFragment :
    BaseFragment<FragmentUserListBinding, UsersListTabletViewModel>(UsersListTabletViewModel::class) {
    override fun getLayoutId(): Int = R.layout.fragment_user_list

    override val viewModel: UsersListTabletViewModel by viewModels()

    override fun init() {
        super.init()
        setUpView()
    }

    private fun setUpView() {
        //Set up the recycler view
        binding.userListTablet?.let { userRV ->
            (userRV.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            val adapter = UserListTabletAdapter(
                userClickListener = { userVO ->
                    val directions = UsersListFragmentDirections.navigateToUserDetail(userVO)
                    findNavController().navigate(directions)
                },
                favStarClickListener = { userVO ->
                    viewModel.handleFavEvent(userVO)
                }
            )
            adapter.setHasStableIds(true)
            userRV.adapter = adapter

            /** SnapHelper is a helper class that helps in snapping any child view of the RecyclerView. */
            val snapHelper = StartSnapHelper()
            userRV.onFlingListener = null
            snapHelper.attachToRecyclerView(userRV)
        }

        binding.favUserListTablet?.let { favUserRV ->
            (favUserRV.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            val adapter = UserListTabletAdapter(
                userClickListener = { userVO ->
                    val directions = UsersListFragmentDirections.navigateToUserDetail(userVO)
                    findNavController().navigate(directions)
                },
                favStarClickListener = { userVO ->
                    viewModel.handleFavEvent(userVO)
                }
            )
            adapter.setHasStableIds(true)
            favUserRV.adapter = adapter

            /** SnapHelper is a helper class that helps in snapping any child view of the RecyclerView. */
            val snapHelper = StartSnapHelper()
            favUserRV.onFlingListener = null
            snapHelper.attachToRecyclerView(favUserRV)
        }
    }

    override fun onScreenRotated(savedInstanceState: Bundle) {
        super.onScreenRotated(savedInstanceState)
        setUpView()
    }

    override fun onResume() {
        super.onResume()
        //startTestFirebaseDatabase()
        //startBioAuth()
        Handler(Looper.getMainLooper()).postDelayed({
            startMyBioAuth()
        }, 1500L)
    }

    private fun startMyBioAuth() {
        Log.d("elfoco", "startMyBioAuth")
        val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle("titulo")
            setSubtitle("subtitulo")
            setConfirmationRequired(true)
            //setDeviceCredentialAllowed(true)
            setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            setNegativeButtonText("NegativeText")
        }.build()

        val biometricPrompt = BiometricPrompt(requireActivity(), ContextCompat.getMainExecutor(requireContext()), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(requireContext(),
                    "Authentication error! $errorCode - $errString", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(requireContext(),
                    "Authentication succeeded!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(requireContext(),
                    "Authentication failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        Log.d("elfoco", "startMyBioAuth dos")

        val biometricManager = BiometricManager.from(requireContext())
        when(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)){
            BiometricManager.BIOMETRIC_SUCCESS ->{
                Log.d("elfoco", "App can authenticate using biometrics.")
                //biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                Log.d("elfoco", "BIOMETRIC_ERROR_NO_HARDWARE Hardware not available")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{
                Log.d("elfoco", "BIOMETRIC_ERROR_HW_UNAVAILABLE Biometric features are currently unavailable.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->{
                Log.d("elfoco", "BIOMETRIC_ERROR_NONE_ENROLLED The user hasn't associated any biometric credentials with their account.")
            }
            else ->{
                Log.d("elfoco", "Nothing supported")
            }
        }
        biometricPrompt.authenticate(promptInfo)
    }
}