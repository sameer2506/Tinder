package com.example.tinder.ui.auth.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tinder.AppPreferences
import com.example.tinder.R
import com.example.tinder.databinding.FragmentAuthBinding
import com.example.tinder.network.Results
import com.example.tinder.ui.auth.viewModel.AuthVM
import com.example.tinder.ui.auth.viewModel.AuthVMF
import com.example.tinder.ui.home.HomeActivity
import com.example.tinder.utils.log
import com.example.tinder.utils.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.math.log
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class AuthFragment : Fragment() , KodeinAware{

    override val kodein by kodein()

    private val factory: AuthVMF by instance()

    private lateinit var fragmentContext: Context
    private lateinit var fragmentActivity: FragmentActivity

    private lateinit var binding: FragmentAuthBinding
    private lateinit var viewModel: AuthVM

    private lateinit var appPreferences: AppPreferences

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val regCode: Int = 123

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentContext = requireContext()
        fragmentActivity = requireActivity()

        binding = FragmentAuthBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(fragmentActivity, factory).get(AuthVM::class.java)

        appPreferences = AppPreferences(fragmentContext)

        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUserIsLogin()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(fragmentActivity, gso)

        binding.googleLogin.setOnClickListener {
            signInGoogle()
        }

        binding.phoneNumberLogin.setOnClickListener {
            findNavController().navigate(R.id.auth_to_phone_auth_fragment)
        }

    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, regCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == regCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            fragmentContext.toast("Try again later...")
            log(e.toString())
        }
    }

    private fun updateUI(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    appPreferences.saveId(account.email.toString())
                    appPreferences.saveName(account.displayName.toString())

                    checkUserStatus()
                }
            }
    }

    private fun checkUserStatus(){
        viewModel.getUserDetails()

        viewModel.getUserDetails.observe(viewLifecycleOwner) {
            when (it) {
                is Results.Loading -> {
                }
                is Results.Success -> {
                    if (it.data) {
                        startActivity(Intent(fragmentContext, HomeActivity::class.java))
                        fragmentActivity.finish()
                    } else {
                        findNavController()
                            .navigate(R.id.action_auth_to_user_details_fragment)
                    }
                }
                is Results.Error -> {
                    fragmentContext.toast("Try again later...")
                    log(it.exception.localizedMessage!!)
                }
            }
        }
    }

    private fun checkUserIsLogin(){
        viewModel.checkUserLogin()

        viewModel.checkUserLogin.observe(viewLifecycleOwner) {
            when (it) {
                is Results.Success -> {
                    if (it.data) {
                        checkUserStatus()
                    }
                }
                is Results.Error -> {
                    log("Error in testing")
                }

                is Results.Loading -> {
                }
            }
        }
    }



}