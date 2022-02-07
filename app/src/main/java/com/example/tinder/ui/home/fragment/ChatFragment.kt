package com.example.tinder.ui.home.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tinder.R
import com.example.tinder.databinding.FragmentChatBinding
import com.example.tinder.model.ReceiverListData
import com.example.tinder.network.Results
import com.example.tinder.ui.home.adapter.ReceiverListAdapter
import com.example.tinder.ui.home.viewModel.HomeVM
import com.example.tinder.ui.home.viewModel.HomeVMF
import com.example.tinder.utils.log
import com.example.tinder.utils.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ChatFragment : Fragment(), KodeinAware, ReceiverListAdapter.OnItemClickSelect {

    override val kodein by kodein()

    private val factory: HomeVMF by instance()

    private lateinit var fragmentContext: Context
    private lateinit var fragmentActivity: FragmentActivity

    private lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: HomeVM

    private var receiverList = ArrayList<ReceiverListData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentContext = requireContext()
        fragmentActivity = requireActivity()

        binding = FragmentChatBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(fragmentActivity, factory).get(HomeVM::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getReceiverList()
        viewModel.receiverList.observe(viewLifecycleOwner) {
            when (it) {
                is Results.Loading -> {
                }
                is Results.Success -> {
                    receiverList = it.data
                    bindUi(it.data)
                }
                is Results.Error -> {
                    log(it.exception.localizedMessage!!)
                    fragmentContext.toast("Try again later...")
                }
            }
        }
    }

    private fun bindUi(list: ArrayList<ReceiverListData>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(fragmentContext)
            setHasFixedSize(true)
            adapter = ReceiverListAdapter(list, this@ChatFragment)
        }
    }

    override fun onItemClick(list: ReceiverListData) {
        val bundle = bundleOf(
            "receiveId" to list.otherId
        )
        findNavController().navigate(R.id.action_chat_to_message_fragment, bundle)
    }



}