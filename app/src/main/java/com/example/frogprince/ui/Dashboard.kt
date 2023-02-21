package com.example.frogprince.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.frogprince.R
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.businesslogic.AssessmentAnalyzer
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.AssessmentDetailModel
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.UserModel
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.ArrayList

class Dashboard : AppCompatActivity() {

    lateinit var manager : DAOManager
    var message : String? = null
    var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        hideKeyboard()

        manager = DAOManager(this)
        user = ProgramManager.FrogPrinceSystem.user
        message = intent.getStringExtra("message")
        if(user != null){
            this.dashBoardGreeting.setText("Hi " + user!!.userName + ",")
        }
        this.txtMessage.setText(message)
        displayAssessment()
        updatePendingAssessmentsIfEmpty()
    }

    fun onResume(savedInstanceState: Bundle?) {
        super.onResume()
        hideKeyboard()
        manager = DAOManager(this)
        user = ProgramManager.FrogPrinceSystem.user
        displayAssessment()
        updatePendingAssessmentsIfEmpty()
    }

    fun hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    fun updatePendingAssessmentsIfEmpty(){
        if(ProgramManager.pendingAssessments == null && manager != null) {
            ProgramManager.pendingAssessments = HashMap<String, String>()

            var pendingAssessAvatars: ArrayList<AvatarModel> =
                manager.avatarDAO?.findAvatarsForPendingAssessments()

            for(i in pendingAssessAvatars){
                ProgramManager.pendingAssessments!!.put(i.avatarId, i.quizStage)
            }
        }
    }

    fun displayAssessment(){
        if(manager != null && user != null) {

            // analyze
            var analyzer : AssessmentAnalyzer? = null
            if(manager != null && user != null) {
                analyzer = AssessmentAnalyzer(manager!!, user!!)
                if(analyzer != null) {
                    analyzer!!.produceAssessments()
                }
            }

            // display the assessment overview
            var assessmentDetails: ArrayList<AssessmentDetailModel>? =
                manager.assessmentDetailDAO.findAssessmentDetailsForUser(user!!.userId)
            if(assessmentDetails != null) {
                var assessmentSize: Int = assessmentDetails!!.size
                if (assessmentSize > 0) {
                    this.dashBoardTopic1.setText(assessmentDetails.get(0).subject.toString())
                    this.dashBoardTopicAns1.setText(assessmentDetails.get(0).description.toString())

                    if (assessmentSize > 1) {
                        this.dashBoardTopic2.setText(assessmentDetails.get(1).subject.toString())
                        this.dashBoardTopicAns2.setText(assessmentDetails.get(1).description.toString())

                        if (assessmentSize > 2) {
                            if(assessmentDetails.get(2).subject.toString().length > 50){
                                this.dashBoardTopic3.setText((assessmentDetails.get(2).subject.toString()).substring(0, 45)+"...")
                            }else{
                                this.dashBoardTopic3.setText(assessmentDetails.get(2).subject.toString())
                            }
                            //this.dashBoardTopicAns3.setText(assessmentDetails.get(2).description.toString())
                            this.dashBoardTopicAns3.setText(">> To see more, click [View All Analysis]")

                        }
                    }
                }
            }
        }
    }

    fun goToCreateAvatar(view : View){
        val intent = Intent(this, CreateAvatar::class.java)
        startActivity(intent)
    }

    fun gotoManageAvatars(view : View){
        val intent = Intent(this, AvatarManagement::class.java)
        startActivity(intent)
    }

    fun gotoViewAnalysis(view : View){
        // analyze
        var analyzer : AssessmentAnalyzer? = null
        if(manager != null && user != null) {
            analyzer = AssessmentAnalyzer(manager!!, user!!)
            if(analyzer != null) {
                analyzer!!.produceAssessments()
            }
        }
        Toast.makeText(this, "A new set of analysis has completed.", Toast.LENGTH_SHORT).show()
        //navigate to analysis
        val intent = Intent(this, Assessment_Report::class.java)
        startActivity(intent)
    }

    fun gotoFinishAssessment(view : View){

        if(ProgramManager.pendingAssessments != null) {
            var pendingAssessments: HashMap<String, String> = ProgramManager.pendingAssessments!!
            if (pendingAssessments.isEmpty()) {
                Toast.makeText(this, "You do not have a pending assessment.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val avatarId: String = pendingAssessments.keys.toTypedArray()[0]
                pendingAssessments.remove(avatarId)
                val intent = Intent(this, Assessment_Quiz::class.java)
                intent.putExtra("avatarId", avatarId)
                startActivity(intent)
            }
        }
    }

    fun initializeApp(view : View){

        //initialize app
        Toast.makeText(this, "The application is now initializing.", Toast.LENGTH_SHORT).show()
        ProgramManager.manager = DAOManager(this)
        val result : Boolean = ProgramManager.initializeAppWithDefaultSetUp()

        if(result) { //success

            //default user info
            Toast.makeText(this, "User setup has been removed.", Toast.LENGTH_SHORT).show()
            ProgramManager.FrogPrinceSystem.loggedIn = true
            ProgramManager.FrogPrinceSystem.user = null

            ProgramManager.FrogPrinceSystem.forcedInitialization = true

            //move back to initial page
            Toast.makeText(
                this,
                "The app will redirected to the registration page.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun gotoUserInfoUpdate(view : View){
        val intent = Intent(this, UserInfoUpdate::class.java)
        startActivity(intent)
    }

    fun gotoSeeMoreAnal1(view : View){
        val intent = Intent(this, Assessment_Report::class.java)
        startActivity(intent)
    }

    fun gotoSeeMoreAnal2(view : View){
        val intent = Intent(this, Assessment_Report::class.java)
        startActivity(intent)
    }





}