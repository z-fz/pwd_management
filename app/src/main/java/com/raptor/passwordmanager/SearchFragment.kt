package com.raptor.passwordmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_search.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
  // TODO: Rename and change types of parameters
  private var param1: String? = null
  private var param2: String? = null
  private lateinit var main_activity : MainActivity
  private lateinit var list_adapter: ArrayAdapter<String>

  inner class OnQueryListener(callback: CallbackListener) : SearchView.OnQueryTextListener {

    private var callback_ = callback

    override fun onQueryTextSubmit(query: String?): Boolean {
      if (query != null) {
        return DoSearch(query)
      }
      return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
      if (query != null) {
        return DoSearch(query)
      }
      return true
    }

    fun DoSearch(query: String): Boolean {
      main_activity.getDataManager().DoSearch(query)
      callback_()
      return true
    }
  }

  inner class OnSearchResultClick : AdapterView.OnItemClickListener {
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
      main_activity.setChosenItem(main_activity.data_manager.GetSearchResult()[position].first)
      findNavController().navigate(R.id.action_SearchFragment_to_DetailFragment)
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      param1 = it.getString(ARG_PARAM1)
      param2 = it.getString(ARG_PARAM2)
    }
    main_activity = activity as MainActivity

  }

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {

    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_search, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    listview?.setOnItemClickListener(OnSearchResultClick())
    list_adapter = ArrayAdapter<String>(main_activity, R.layout.listview)
    view.findViewById<ListView>(R.id.listview).adapter = list_adapter
    var onSearchUpdated: () -> Unit = {
      var result = main_activity.getDataManager().GetSearchResult()
      list_adapter.clear()
      for (item in result) {
        list_adapter.add(item.second.website_ + " " + item.second.username_ + " " + item.second.password_)
      }
    }
    view.findViewById<SearchView>(R.id.search).setOnQueryTextListener(OnQueryListener(onSearchUpdated))
    onSearchUpdated()
  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    @JvmStatic
    fun newInstance(param1: String, param2: String) =
      SearchFragment().apply {
        arguments = Bundle().apply {
          putString(ARG_PARAM1, param1)
          putString(ARG_PARAM2, param2)
        }
      }
  }
}