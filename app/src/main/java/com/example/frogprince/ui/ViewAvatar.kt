package com.example.frogprince.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frogprince.R
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.businesslogic.AssessmentAnalyzer
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.SubQuestionModel
import com.example.frogprince.db.model.UserAnswerModel
import com.example.frogprince.db.model.UserModel
import kotlinx.android.synthetic.main.activity_view_avatar.*
import java.util.*


class ViewAvatar : AppCompatActivity() {

    lateinit var manager : DAOManager
    var avatarId : String? = null
    var avatar : AvatarModel? = null
    var avatarName : String? = null
    var userAnswers : ArrayList<UserAnswerModel>? = null
    var reflectionLibraryTitle : String? = "Reflection Library"
    var txtViewAssessStage : String? = null
    var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_avatar)
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setupAvatar()
        populateData()
        displayAssessment()
    }

    override fun onResume() {
        super.onResume()
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setupAvatar()
        populateData()
        displayAssessment()
    }

    fun setupAvatar(){
        manager = DAOManager(this)
        user = ProgramManager.FrogPrinceSystem.user
        avatarId = intent.getStringExtra("avatarId")
        if(avatarId != null) {
            avatar = manager.avatarDAO.getAvatarByAvatarId(avatarId!!)
        }
    }

    fun populateData(){
        if(avatar != null) {
            avatarName = avatar!!.nickName
            this.lblViewTitle.setText("My Experience with "+avatarName+"")
            this.lblViewTop.setText("Nickname: [" + avatarName +"]")
            this.txtViewPhone.setText(avatar!!.phoneNum)
            this.txtViewAid.setText(avatar!!.memoryAid)
            var stage : String = avatar!!.quizStage.toString()
            if(stage.equals("1")){
                stage = "1 (No Assessment Yet)"
            }else if(stage.equals("5")){
                stage = "5 (Completed)"
            }
            this.txtViewAssessmentStage.setText(stage)
        }
            //this.imgView.setImageURI(avatar?.imageLink)  //TO-DO
    }

    fun gotoUpdateAvatar(view : View){
        var intent : Intent = Intent(this, UpdateAvatar::class.java)
        intent.putExtra("avatarId", avatar!!.avatarId)
        startActivity(intent)
    }

    fun deleteAvatar(view : View) {
        if (avatar != null){
            Toast.makeText(this, "The deletion of avatar is requested.", Toast.LENGTH_SHORT).show()
            val resDeleteUserAnswers: Boolean = manager.userAnswerDAO.deleteUserAnswerForAvatarId(avatar!!.avatarId)
            val resDeleteAvatar: Boolean = manager.avatarDAO.deleteAvatar(avatar!!.avatarId)
            if (resDeleteUserAnswers && resDeleteAvatar) {
                Toast.makeText(applicationContext,"The avatar and assessments have been successfully removed.",Toast.LENGTH_SHORT).show()
                gotoAvaList(view)
            } else {
                Toast.makeText(this, "Problem occurred. Try again.", Toast.LENGTH_SHORT).show()
                this.txtViewAvaMessage.setText("Problem occurred. Try again.")
            }
        }else{
            Toast.makeText(this, "Problem occurred. Try again.", Toast.LENGTH_SHORT).show()
            this.txtViewAvaMessage.setText("Problem occurred. Try again.")
        }
    }

    fun gotoAssessQuiz(view : View){
        var intent = Intent(this, Assessment_Quiz::class.java)
        intent.putExtra("avatarId", avatar?.avatarId)
        startActivity(intent)
    }

    fun gotoAvaList(view : View){
        startActivity(Intent(this, AvatarManagement::class.java))
    }

    fun gotoDashboard(view : View){
        startActivity(Intent(this, Dashboard::class.java))
    }

    fun gotoAllAssess(view : View){
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

    @SuppressLint("WrongConstant")
    fun displayAssessment(){
        if(avatar != null && avatar!!.quizStage.toInt() > 1
            && ProgramManager.FrogPrinceSystem.user != null) {

            //populate global data if it is empty
            if(manager != null && ProgramManager.mainQuestions == null){
                ProgramManager.mainQuestions = manager.mainQuestionDAO.listAllMainQuestions()
            }

            //get questions answered for this avatar
            userAnswers = manager.userAnswerDAO.findUserAnswersByUserByAvatar(ProgramManager.FrogPrinceSystem.user!!.userId, avatar!!.avatarId)

            this.lblViewCategory2.setText(reflectionLibraryTitle) //title
            this.lblViewTopic2.setText("")
            this.lblViewTopic3.setText("")
            this.lblViewTopic4.setText("")
            this.lblViewTopic2Ans.setText("")
            this.lblViewTopic3Ans.setText("")
            this.lblViewTopic4Ans.setText("")

            if(userAnswers != null) {

                //hide take-assess button
                if(avatar!!.quizStage.toInt() >=5 || userAnswers!!.size >= 4) {
                    var btnSubmitNextQ: Button = findViewById<Button>(R.id.btnViewContAssess)
                    btnSubmitNextQ.setVisibility(View.INVISIBLE);
                }

                if (userAnswers!!.size > 0) {
                    var ansQ1 : UserAnswerModel = userAnswers!!.get(0)
                    var subQ1 : SubQuestionModel? = manager.subQuestionDAO.findSubQuestion(ansQ1.subQuestionId)
                    if(subQ1 != null) {
                        this.lblViewTopic1.setText(subQ1.description.toString())
                        this.lblViewTopic1Ans.setText(ansQ1.answer.toString())
                    }

                    if (userAnswers!!.size > 1) {
                        var ansQ2 : UserAnswerModel = userAnswers!!.get(1)
                        var subQ2 : SubQuestionModel? = manager.subQuestionDAO.findSubQuestion(ansQ2.subQuestionId)
                        if(subQ2 != null) {
                            this.lblViewTopic2.setText(subQ2.description.toString())
                            this.lblViewTopic2Ans.setText(ansQ2.answer.toString())
                        }

                        if (userAnswers!!.size > 2) {
                            var ansQ3 : UserAnswerModel = userAnswers!!.get(2)
                            var subQ3 : SubQuestionModel? = manager.subQuestionDAO.findSubQuestion(ansQ3.subQuestionId)
                            if(subQ3 != null) {
                                this.lblViewTopic3.setText(subQ3.description.toString())
                                this.lblViewTopic3Ans.setText(ansQ3.answer.toString())
                            }

                            if (userAnswers!!.size > 3) {
                                var ansQ4 : UserAnswerModel = userAnswers!!.get(3)
                                var subQ4 : SubQuestionModel? = manager.subQuestionDAO.findSubQuestion(ansQ4.subQuestionId)
                                if(subQ4 != null) {
                                    this.lblViewTopic4.setText(subQ4.description.toString())
                                    this.lblViewTopic4Ans.setText(ansQ4.answer.toString())
                                }
                            }
                        }
                    }
                }
            }

        }else{
            this.lblViewCategory2.setText(reflectionLibraryTitle) //title
            this.lblViewTopic1.setText("Assessment quiz is not completed.")
            this.lblViewTopic2.setText("")
            this.lblViewTopic3.setText("Please take Assessment quiz, using the botton below.")
            this.lblViewTopic4.setText("")
            this.lblViewTopic1Ans.setText("")
            this.lblViewTopic2Ans.setText("")
            this.lblViewTopic3Ans.setText("")
            this.lblViewTopic4Ans.setText("")
        }
    }







}