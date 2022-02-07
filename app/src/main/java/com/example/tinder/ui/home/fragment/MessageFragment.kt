package com.example.tinder.ui.home.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinder.AppPreferences
import com.example.tinder.R
import com.example.tinder.databinding.FragmentMessageBinding
import com.example.tinder.security.isEmpty
import com.example.tinder.ui.home.adapter.MessageListAdapter
import com.example.tinder.ui.home.viewModel.HomeVM
import com.example.tinder.ui.home.viewModel.HomeVMF
import com.google.firebase.Timestamp
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*


class MessageFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val factory: HomeVMF by instance()

    private lateinit var fragmentContext: Context
    private lateinit var fragmentActivity: FragmentActivity

    private lateinit var binding: FragmentMessageBinding
    private lateinit var viewModel: HomeVM

    private lateinit var appPreferences: AppPreferences

    private var receiveId: String = ""
    private var senderId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentContext = requireContext()
        fragmentActivity = requireActivity()

        binding = FragmentMessageBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(fragmentActivity, factory).get(HomeVM::class.java)

        appPreferences = AppPreferences(fragmentContext)

        receiveId = requireArguments().getString("receiveId")!!
        senderId = appPreferences.getId()!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchMessageList(senderId, receiveId)

        viewModel.messageList.observe(viewLifecycleOwner) {
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(fragmentContext)
                setHasFixedSize(true)
                adapter = MessageListAdapter(it)
            }
        }

        binding.imgSendMessage.setOnClickListener {

            if (isEmpty(binding.etMessage)) {
                binding.etMessage.error = "Message can't be empty."
                return@setOnClickListener
            }

            val message: String = binding.etMessage.text.toString()

            setSenderMessage(message)
            setReceiverMessage(message)
        }

    }

    private fun setReceiverMessage(message: String) {

        val timestamp = Timestamp.now()
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val netDate = Date(milliseconds)

        val data = HashMap<String, Any>()

        data["message"] = message
        data["time"] = netDate
        data["status"] = "receive"

        viewModel.sentReceiverMessage(senderId, receiveId, data)
    }

    private fun setSenderMessage(message: String) {

        val timestamp = Timestamp.now()
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val netDate = Date(milliseconds)

        val data = HashMap<String, Any>()

        data["message"] = message
        data["time"] = netDate
        data["status"] = "send"

        viewModel.sentSenderMessage(senderId, receiveId, data)

    }


}