package com.example.frogprince.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frogprince.R
import com.example.frogprince.db.model.SubQuestionModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_positive_question.*

class PositiveQuestionFragment(var subQuestion : SubQuestionModel? = null) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_positive_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(subQuestion != null && subQuestion!!.answerChoices != null
            && subQuestion!!.answerChoices!!.size > 0) {
            //sub question
            this.txtPositiveQuestion.setText(subQuestion!!.description)
            this.rdoPositiveOption1.setText(subQuestion!!.answerChoices!!.get(0).description.toString())
            this.rdoPositiveOption2.setText(subQuestion!!.answerChoices!!.get(1).description.toString())
            this.rdoPositiveOption3.setText(subQuestion!!.answerChoices!!.get(2).description.toString())
            this.rdoPositiveOption4.setText(subQuestion!!.answerChoices!!.get(3).description.toString())
        }

    }

}