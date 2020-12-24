package com.raptor.passwordmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DetailFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_detail, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    button_second?.setOnClickListener {
      findNavController().navigate(R.id.action_DetailFragment_to_SearchFragment)
    }
    var act = (activity as MainActivity)
    var data = act.getDataManager().GetDataAt(act.getChosenItem())
    detail_website?.setText(data.website_)
    detail_email?.setText(data.email_)
    detail_username?.setText(data.username_)
    detail_password?.setText(data.password_)
    detail_comments?.setText(data.comments_)
  }
}