package com.example.frogprince.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frogprince.R
import com.example.frogprince.db.model.SubQuestionModel
import kotlinx.android.synthetic.main.fragment_negative_question.*
import kotlinx.android.synthetic.main.fragment_positive_question.*

class NegativeQuestionFragment(var subQuestion : SubQuestionModel? = null) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_negative_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(subQuestion != null && subQuestion!!.answerChoices != null
            && subQuestion!!.answerChoices!!.size > 0) {
            //sub question
            this.txtNegativeQuestion.setText(subQuestion!!.description)
            this.rdoNegativeOption1.setText(subQuestion!!.answerChoices!!.get(0).description.toString())
            this.rdoNegativeOption2.setText(subQuestion!!.answerChoices!!.get(1).description.toString())
            this.rdoNegativeOption3.setText(subQuestion!!.answerChoices!!.get(2).description.toString())
            this.rdoNegativeOption4.setText(subQuestion!!.answerChoices!!.get(3).description.toString())
        }

    }

//    public fun setQuestion(q : String){
//        this.txtNegativeQuestion.setText("asdfasdf")
//    }

}