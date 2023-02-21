package com.example.frogprince.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.example.frogprince.R
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.AssessmentDetailModel
import com.example.frogprince.db.model.UserAnswerModel
import com.example.frogprince.db.model.UserModel
import kotlinx.android.synthetic.main.activity_assessment_report.*
import java.util.ArrayList

class Assessment_Report : AppCompatActivity() {

    lateinit var manager : DAOManager
    var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_report)
        hideKeyboard()

        manager = DAOManager(this)
        user = ProgramManager.FrogPrinceSystem.user
        displayUserGuide()
        displayAssessment()
    }

    fun displayUserGuide(){
        if(user != null) {
            this.mainMsg.setText("Hi " + user!!.userName + ", Right on! You're doing truely amazing!")
        }
    }

    fun displayAssessment(){
        if(manager != null && user != null) {

            var assessmentDetails: ArrayList<AssessmentDetailModel>? =
                manager.assessmentDetailDAO.findAssessmentDetailsForUser(user!!.userId)
            if(assessmentDetails != null) {
                var assessmentSize: Int = assessmentDetails!!.size
                if (assessmentSize > 0) {
                    this.assessReportTopic1.setText(assessmentDetails.get(0).subject.toString())
                    this.txtAssessTopicResult1.setText(assessmentDetails.get(0).description.toString())

                    if (assessmentSize > 1) {
                        this.assessReportTopic2.setText(assessmentDetails.get(1).subject.toString())
                        this.txtAssessTopicResult2.setText(assessmentDetails.get(1).description.toString())

                        if (assessmentSize > 2) {
                            this.assessReportTopic3.setText(assessmentDetails.get(2).subject.toString())
                            this.txtAssessTopicResult3.setText(assessmentDetails.get(2).description.toString())

                            if (assessmentSize > 3) {
                                this.assessReportTopic4.setText(assessmentDetails.get(3).subject.toString())
                                this.txtAssessTopicResult4.setText(assessmentDetails.get(3).description.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    fun hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    fun gotoDashboard(view : View){
        startActivity(Intent(this, Dashboard::class.java))
    }

    fun gotoAvaList(view : View){
        startActivity(Intent(this, AvatarManagement::class.java))
    }

    fun gotoCreateAvatar(view : View){
        startActivity(Intent(this, CreateAvatar::class.java))
    }




}