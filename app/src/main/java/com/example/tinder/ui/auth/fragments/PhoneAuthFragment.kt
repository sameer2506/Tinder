package com.example.tinder.ui.auth.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.example.tinder.R
import com.example.tinder.databinding.FragmentPhoneAuthBinding
import com.example.tinder.security.isValidMobile

class PhoneAuthFragment : Fragment() {

    private lateinit var fragmentContext: Context
    private lateinit var fragmentActivity: FragmentActivity

    private lateinit var binding: FragmentPhoneAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContext = requireContext()
        fragmentActivity = requireActivity()

        binding = FragmentPhoneAuthBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // In this video we will authenticate user using Phone number

        binding.btnContinue.setOnClickListener {
            var phoneNumber = binding.etPhoneNumber.text.toString()

            if (!isValidMobile(phoneNumber)){
                binding.etPhoneNumber.error = "Invalid mobile number."
                return@setOnClickListener
            }

            phoneNumber = "+91$phoneNumber"

            val bundle = bundleOf(
                "phoneNumber" to phoneNumber
            )

            findNavController().navigate(R.id.phone_auth_to_otp_verification_fragment, bundle)
        }
    }


}