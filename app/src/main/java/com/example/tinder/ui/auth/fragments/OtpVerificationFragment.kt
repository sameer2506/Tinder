package com.example.tinder.ui.auth.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tinder.AppPreferences
import com.example.tinder.R
import com.example.tinder.databinding.FragmentOtpVerificationBinding
import com.example.tinder.network.Results
import com.example.tinder.ui.auth.viewModel.AuthVM
import com.example.tinder.ui.auth.viewModel.AuthVMF
import com.example.tinder.ui.home.HomeActivity
import com.example.tinder.utils.log
import com.example.tinder.utils.logError
import com.example.tinder.utils.toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class OtpVerificationFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val factory: AuthVMF by instance()

    private lateinit var fragmentContext: Context
    private lateinit var fragmentActivity: FragmentActivity

    private lateinit var binding: FragmentOtpVerificationBinding
    private lateinit var viewModel: AuthVM

    private lateinit var auth: FirebaseAuth

    private var code: String? = null
    private var verificationCode: String = ""
    private var phoneNumber: String = ""

    private lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContext = requireContext()
        fragmentActivity = requireActivity()

        binding = FragmentOtpVerificationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(fragmentActivity, factory).get(AuthVM::class.java)

        auth = FirebaseAuth.getInstance()

        phoneNumber = requireArguments().getString("phoneNumber")!!

        appPreferences = AppPreferences(fragmentContext)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPhoneNumberVerification(phoneNumber)
        countDown()

        binding.btnOtpVerify.setOnClickListener {
            var codeType: String

            with(binding) {
                codeType =
                    otpEditBox1.text.toString() + otpEditBox2.text.toString() + otpEditBox3.text.toString() + otpEditBox4.text.toString() + otpEditBox5.text.toString() + otpEditBox6.text.toString()
            }

            verifyOtp(verificationCode, codeType)
        }

        binding.tvResendCode.setOnClickListener {
            startPhoneNumberVerification(phoneNumber)
        }

    }

    private fun verifyOtp(verification: String, codeT: String){

        // Now we will setup the View Model and repository class using Kodein Depedency Injection

        val credential = PhoneAuthProvider.getCredential(verification, codeT)

        viewModel.verifyOtp(credential)

        viewModel.verifyOtp.observe(viewLifecycleOwner){
            when(it){
                is Results.Success -> {
                    verificationCode = ""
                    code = ""
                    checkUserStatus()
                }
                is Results.Error -> {
                    log("SignInPhoneFailure: ${it.exception.localizedMessage}")
                    fragmentContext.toast("Try again later...")
                }

                is Results.Loading -> {}
            }
        }


    }

    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)

                verificationCode = verificationId
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val codeSMS = credential.smsCode
                if (codeSMS != null)
                    code = codeSMS
            }

            override fun onVerificationFailed(e: FirebaseException) {
                fragmentContext.toast("Something went wrong. Try again later...")
                logError("onVerificationFailed:$e")

                if (e is FirebaseAuthInvalidCredentialsException) {
                    fragmentContext.toast("Something went wrong. Try again later...")
                    logError("FirebaseAuthInvalidCredentialsException: Invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    fragmentContext.toast("Something went wrong. Try again later...")
                    logError("FirebaseTooManyRequestsException: The SMS quota for the project has been exceeded")
                }
            }
        }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120, TimeUnit.SECONDS)
            .setActivity(fragmentActivity)
            .setCallbacks(mCallbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun countDown() {
        object : CountDownTimer(120000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                // Used for formatting digit to be in 2 digits only
                val f: NumberFormat = DecimalFormat("00")
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                binding.countDown.visibility = View.VISIBLE
                binding.countDown.text =
                    f.format(min) + ":" + f.format(sec)
            }

            // When the task is over it will print 00:00:00 there
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.countDown.text = "00:00"
                binding.countDown.visibility = View.GONE
                binding.tvResendCode.visibility = View.VISIBLE
            }
        }.start()

    }

    private fun checkUserStatus() {
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
                            .navigate(R.id.action_otp_to_user_details_fragment)
                    }
                }
                is Results.Error -> {
                    fragmentContext.toast("Try again later...")
                    log(it.exception.localizedMessage!!)
                }
            }
        }
    }




}