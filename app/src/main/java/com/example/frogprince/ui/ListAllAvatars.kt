package com.example.frogprince.ui


import androidx.fragment.app.activityViewModels
import com.example.frogprince.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frogprince.adapter.AvatarRecyclerAdapter
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.businesslogic.AssessmentAnalyzer
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.UserModel
import com.example.frogprince.util.Utility
import com.example.frogprince.viewmodel.AvatarViewModel
import kotlinx.android.synthetic.main.fragment_list_all_avatars.*
import java.util.ArrayList

class ListAllAvatars : Fragment() {

    val avatarViewModel : AvatarViewModel by activityViewModels()
    var adapter : AvatarRecyclerAdapter = AvatarRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_all_avatars, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAvatarRecyclerView.layoutManager = LinearLayoutManager(activity)
        listAvatarRecyclerView.adapter = adapter

        loadRefreshData()

        //onclick event
        adapter.onItemClick = { ava ->
            Log.d("Clicked: ", ava.nickName)
            var intent : Intent = Intent(context, ViewAvatar::class.java)
            intent.putExtra("avatarId", ava.avatarId.toString())
            startActivity(intent)
        }

        // set events to buttons

        allAvaNavOne.setOnClickListener{
            val intent = Intent(context, Dashboard::class.java)
            activity?.startActivity(intent)
        }

        allAvaNavTwo.setOnClickListener{
            var manager : DAOManager? = ProgramManager.manager
            var user: UserModel? = ProgramManager.FrogPrinceSystem.user

            if(manager != null && user != null) {
                // analyze
                var analyzer: AssessmentAnalyzer? = null
                if (manager != null && user != null) {
                    analyzer = AssessmentAnalyzer(manager!!, user!!)
                    if (analyzer != null) {
                        analyzer!!.produceAssessments()
                    }
                }
                Toast.makeText(context, "A new set of analysis has completed.", Toast.LENGTH_SHORT)
                    .show()

                //navigate to analysis
                val intent = Intent(context, Assessment_Report::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(context, "Error occurred. Please try again.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        allAvaNavThree.setOnClickListener{
            val intent = Intent(context, CreateAvatar::class.java)
            activity?.startActivity(intent)
        }
    }

    fun loadRefreshData(){
        var avalist : ArrayList<AvatarModel>? = ProgramManager.manager?.avatarDAO?.listAllAvatars()
        if (avalist != null) {
            avatarViewModel.loadAvatars(avalist)
            Utility.println("load avatars - size "+avalist.size)
        }
        avatarViewModel.avatarsList.observe(viewLifecycleOwner, Observer { avaList -> avaList?.let { adapter.setAvatars(it) } })
    }

    fun onResume(view: View, savedInstanceState: Bundle?){
        Utility.println("onResume")
    }

}