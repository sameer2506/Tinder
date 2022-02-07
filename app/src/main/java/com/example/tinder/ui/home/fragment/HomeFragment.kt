package com.example.tinder.ui.home.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinder.AppPreferences
import com.example.tinder.databinding.FragmentHomeBinding
import com.example.tinder.model.UserProfile
import com.example.tinder.network.Results
import com.example.tinder.ui.home.adapter.ImageAdapter
import com.example.tinder.ui.home.viewModel.HomeVM
import com.example.tinder.ui.home.viewModel.HomeVMF
import com.example.tinder.utils.log
import com.example.tinder.utils.toast
import com.google.firebase.Timestamp
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class HomeFragment : Fragment(), KodeinAware {


    override val kodein by kodein()

    private val factory: HomeVMF by instance()

    private lateinit var fragmentContext: Context
    private lateinit var fragmentActivity: FragmentActivity

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeVM

    private lateinit var appPreferences: AppPreferences

    private var userList = ArrayList<UserProfile>()
    private var position: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentContext = requireContext()
        fragmentActivity = requireActivity()

        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(fragmentActivity, factory).get(HomeVM::class.java)

        appPreferences = AppPreferences(fragmentContext)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListOfUser()

        binding.dislikeButton.setOnClickListener {
            position += 1
            bindUi(position)
        }

        binding.imgLikeButton.setOnClickListener {
            setMessageId()
            setSenderMessage()
            setReceiverMessage()
            position += 1
            bindUi(position)
        }

    }

    private fun setMessageId() {
        // From the current user
        val receiverId = userList[position].id

        val timestamp = Timestamp.now()
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val netDate = Date(milliseconds)

        val data = HashMap<String, Any>()

        data["id"] = appPreferences.getId()!!
        data["otherId"] = receiverId
        data["name"] = userList[position].name
        data["time"] = netDate

        viewModel.messageId(data)

        // From the sender user
        val data2 = HashMap<String, Any>()

        data["id"] = userList[position].name
        data["otherId"] = appPreferences.getId()!!
        data["name"] = appPreferences.getName().toString()
        data["time"] = netDate

        viewModel.messageId(data2)

    }

    private fun setReceiverMessage() {
        val message = "Hello!"
        val receiverId = userList[position].id
        val senderId = appPreferences.getId()

        val timestamp = Timestamp.now()
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val netDate = Date(milliseconds)

        val data = HashMap<String, Any>()

        data["message"] = message
        data["time"] = netDate
        data["status"] = "receive"

        viewModel.sentReceiverMessage(senderId!!, receiverId, data)
    }

    private fun setSenderMessage() {
        val message = "Hello!"
        val receiverId = userList[position].id
        val senderId = appPreferences.getId()

        val timestamp = Timestamp.now()
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val netDate = Date(milliseconds)

        val data = HashMap<String, Any>()

        data["message"] = message
        data["time"] = netDate
        data["status"] = "send"

        viewModel.sentSenderMessage(senderId!!, receiverId, data)

    }

    private fun getListOfUser() {
        viewModel.listOfAllUser()
        viewModel.listOfAllUser.observe(viewLifecycleOwner) {
            when (it) {
                is Results.Loading -> {
                }
                is Results.Success -> {
                    userList = it.data
                    bindUi(position)
                }
                is Results.Error -> {
                    fragmentContext.toast("Try again later...")
                    log(it.exception.localizedMessage!!)
                }
            }
        }
    }

    private fun bindUi(pos: Int) {

        if (pos == userList.size) {
            binding.noProfile.visibility = View.VISIBLE
            binding.constProfile.visibility = View.GONE
        } else {
            binding.noProfile.visibility = View.GONE
            binding.constProfile.visibility = View.VISIBLE

            val data = userList[pos]
            val year = data.dob.subSequence(5, 9)

            with(binding) {
                tvName.text = data.name
                tvAge.text = year
            }

            val imgList = ArrayList<String>()

            imgList.add(data.image1)
            imgList.add(data.image2)
            imgList.add(data.image3)
            imgList.add(data.image4)
            imgList.add(data.image5)
            imgList.add(data.image6)

            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(fragmentContext)

                val imageAdapter = ImageAdapter(imgList)

                layoutManager = LinearLayoutManager(
                    fragmentContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = imageAdapter
            }

        }


    }
}