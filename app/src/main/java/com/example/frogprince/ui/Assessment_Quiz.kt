package com.example.frogprince.ui


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.frogprince.R
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.MainQuestionModel
import com.example.frogprince.db.model.UserAnswerModel
import com.example.frogprince.db.model.UserModel
import kotlinx.android.synthetic.main.activity_assessment_quiz.*
import kotlinx.android.synthetic.main.fragment_negative_question.*
import kotlinx.android.synthetic.main.fragment_positive_question.*


class Assessment_Quiz : AppCompatActivity() {

    lateinit var manager: DAOManager

    var user : UserModel? = null
    var avatarId: String? = null
    var avatar: AvatarModel? = null
    var mainQuestion: MainQuestionModel? = null
    var quizStage : String? = null

    var navHostFragment: Fragment? = null
    var fragPositive: PositiveQuestionFragment? = null
    var fragNegative: NegativeQuestionFragment? = null
    var answerMode: Boolean = false
    var questionType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_quiz)
        hideKeyboard()

        user = ProgramManager.FrogPrinceSystem.user
        manager = DAOManager(this)
        avatarId = intent.getStringExtra("avatarId")

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentPositiveNegative)

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentPositiveNegative,
                BlankQuizFragment()
            ).commit()

        getAvatar()
        setUserGuide()
        getCurrQuestion()
        displayNames()
        displayMainQuiz()
        linkDifferentUiByYesNo()
    }


    fun getAvatar() {
        if (avatarId != null) {
            avatar = manager.avatarDAO.getAvatarByAvatarId(avatarId!!)
        }
    }

    fun setUserGuide(){
        if(user != null && avatar != null) {
            this.assessQuizMainContentText.setText(
                "Here is an awesome women named " + user!!.userName + ". " +
                        "In this diaray, we are reflecting on her dating experience with " + avatar!!.nickName +". " +
                        "\nWe want to find the right words to best describe her feelings, emotions, " +
                        "and the situations that happened during the interaction with " + avatar!!.nickName +"." +
                        "\nLet's start!"
            )
        }
    }

    fun getCurrQuestion() {
        if (avatar != null) {
            quizStage = avatar!!.quizStage
            if(quizStage !=null && quizStage!!.toInt() > 4){
                //redirect to the view
                Toast.makeText(this, "CONGRATULATIONS! All assessment has completed!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ViewAvatar::class.java)
                if (avatar != null) {
                    intent.putExtra("avatarId", avatar!!.avatarId)
                }
                startActivity(intent)
            }else {
                mainQuestion = manager.mainQuestionDAO.getMainQuestionById(quizStage!!)
            }
        }
    }

    fun displayNames(){
        if(user != null && avatar != null && quizStage != null) {
            this.pageTitle.setText("Assessment Quiz ("+avatar!!.nickName+"): Stage "+quizStage)
            this.section1.setText(user!!.userName+" is thinking of when she dated with "+avatar!!.nickName)
        }
    }

    fun displayMainQuiz() {
        if (mainQuestion != null) {
            this.section2.setText(mainQuestion!!.description)
        }
    }

    fun linkDifferentUiByYesNo() {

        var radioGroup: RadioGroup = findViewById(R.id.rdoGroupYesNoAnswer)

        //lister link
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val rb = findViewById<View>(checkedId) as RadioButton

            answerMode = true
            Toast.makeText(this, "You Selected " + rb.text, Toast.LENGTH_SHORT).show()

            if (mainQuestion != null && mainQuestion!!.subQuestions != null) {

                if (mainQuestion!!.subQuestions!!.size > 0 && rb.text.equals("Yes")) {

                    questionType = "P"
                    fragPositive =
                        PositiveQuestionFragment(
                            mainQuestion!!.subQuestions!!.get(0)
                        )
                    if (fragPositive != null) {

                        getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentPositiveNegative, fragPositive!!).commit()
                    }

                } else if (mainQuestion!!.subQuestions!!.size > 1 && rb.text.equals("No")) {

                    questionType = "N"
                    fragNegative =
                        NegativeQuestionFragment(
                            mainQuestion!!.subQuestions!!.get(1)
                        )
                    if (fragNegative != null) {

                        getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentPositiveNegative, fragNegative!!).commit()

                    }
                }
            }
        })


    }

    fun getForegroundFragment(id: Int): Fragment? {
        var navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentPositiveNegative)
        return navHostFragment?.childFragmentManager?.fragments?.get(id)
    }

    fun hideKeyboard() {
        this.getWindow()
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    fun gotoDashboard(view: View) {
        startActivity(Intent(this, Dashboard::class.java))
    }

    fun gotoAvaList(view: View) {
        startActivity(Intent(this, AvatarManagement::class.java))
    }

    fun gotoAllAssess(view: View) {
        startActivity(Intent(this, Assessment_Report::class.java))
    }

    fun saveAndExitToViewAvatar(view: View) {

        if(!answerMode) {
            Toast.makeText(this, "Please first answer the main question (Yes/No).", Toast.LENGTH_SHORT).show()
        }else{
            var insertSuccess: Boolean = saveUserAnswer(view)
            if (insertSuccess) {
                val intent = Intent(this, ViewAvatar::class.java)
                if (avatar != null) {
                    intent.putExtra("avatarId", avatar!!.avatarId)
                }
                startActivity(intent)
            }
        }
    }

    fun saveTakeNextQuestion(view : View){
        if(!answerMode) {
            Toast.makeText(this, "Please first answer the main question (Yes/No).", Toast.LENGTH_SHORT).show()
        }else {
            var insertSuccess: Boolean = saveUserAnswer(view)
            if (insertSuccess) {
                val intent = Intent(this, Assessment_Quiz::class.java)
                if (avatar != null) {
                    intent.putExtra("avatarId", avatar!!.avatarId)
                }
                startActivity(intent)
            }
        }
    }

    fun saveUserAnswer(view: View): Boolean {
        var resultSaved : Boolean = false

        if (ProgramManager.FrogPrinceSystem.user != null
            && avatar != null
            && mainQuestion != null
            && answerMode
        ) {

            var userAnwer: UserAnswerModel? = null

            if (questionType.equals("P")) {
                if (fragPositive != null && fragPositive!!.subQuestion != null) {

                    // basic ref info
                    var userAnswerId: String? = ""
                    var userId: String? = ProgramManager.FrogPrinceSystem.user!!.userId.toString()
                    var avatarId: String? = avatar!!.avatarId.toString()
                    var mainQuestionId: String? = mainQuestion!!.mainQuestionId.toString()
                    var subQuestionId: String? =
                        fragPositive!!.subQuestion!!.subQuestionId.toString()
                    var positiveOrNegative: String? = questionType
                    var rating: String? = ""
                    var answer: String? = null

                    // radio selection
                    var rgbPositiveSubQuestion: RadioGroup? =
                        findViewById(R.id.radioGroupPositiveSubQuestion)
                    var userPickId: Int? = null
                    var userPickRadio: RadioButton? = null

                    // text input
                    var textAns: String = txtPositiveFreeAns.text.toString()

                    if (textAns.isNotEmpty()) {
                        answer = textAns
                    } else {
                        if (rgbPositiveSubQuestion != null) {
                            userPickId = rgbPositiveSubQuestion!!.getCheckedRadioButtonId()
                            if (userPickId != null) {
                                userPickRadio = findViewById(userPickId!!)
                                if (userPickRadio != null) {
                                    answer = userPickRadio!!.getText().toString()
                                }
                            }
                        }
                    }

                    var rowId: Long? = -1
                    if (answer != null) {

                        if (userAnswerId != null && userId != null && avatarId != null && mainQuestionId != null &&
                            subQuestionId != null && positiveOrNegative != null && rating != null
                        ) {

                            rowId = manager.userAnswerDAO.insertUserAnswer(
                                UserAnswerModel(
                                    userAnswerId, userId, avatarId, mainQuestionId,
                                    subQuestionId, positiveOrNegative, rating, answer
                                )
                            )

                            if (!rowId.equals(-1)) {
                                //successful
                                resultSaved = true

                                //increment quiz number on avatar
                                incrementQuizNumberOfAvatar()

                                Toast.makeText(
                                    this,
                                    "Your answer (" + answer + ") was successfully saved.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                Toast.makeText(
                                    this,
                                    "Error occurred. Try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(this, "Error occurred. Try again.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "You must either select one choice in the radio buttons, or give a text answer.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else if (questionType.equals("N")) {

                if (fragNegative != null && fragNegative!!.subQuestion != null) {

                    // basic ref info
                    var userAnswerId: String? = ""
                    var userId: String? = ProgramManager.FrogPrinceSystem.user!!.userId.toString()
                    var avatarId: String? = avatar!!.avatarId.toString()
                    var mainQuestionId: String? = mainQuestion!!.mainQuestionId.toString()
                    var subQuestionId: String? =
                        fragNegative!!.subQuestion!!.subQuestionId.toString()
                    var positiveOrNegative: String? = questionType
                    var rating: String? = ""
                    var answer: String? = null

                    // radio selection
                    var rgbNegativeSubQuestion: RadioGroup? =
                        findViewById(R.id.radioGroupNegativeSubQuestion)
                    var userPickId: Int? = null
                    var userPickRadio: RadioButton? = null

                    // text input
                    var textAns: String = txtNegativeFreeAns.text.toString()

                    if (textAns.isNotEmpty()) {
                        answer = textAns
                    } else {
                        if (rgbNegativeSubQuestion != null) {
                            userPickId = rgbNegativeSubQuestion!!.getCheckedRadioButtonId()
                            if (userPickId != null) {
                                userPickRadio = findViewById(userPickId!!)
                                if (userPickRadio != null) {
                                    answer = userPickRadio!!.getText().toString()
                                }
                            }
                        }
                    }

                    var rowId: Long? = -1
                    if (answer != null) {

                        if (userAnswerId != null && userId != null && avatarId != null && mainQuestionId != null &&
                            subQuestionId != null && positiveOrNegative != null && rating != null
                        ) {

                            rowId = manager.userAnswerDAO.insertUserAnswer(
                                UserAnswerModel(
                                    userAnswerId, userId, avatarId, mainQuestionId,
                                    subQuestionId, positiveOrNegative, rating, answer
                                )
                            )

                            if (!rowId.equals(-1)) {
                                //successful save
                                resultSaved = true

                                //increment quiz number on avatar
                                incrementQuizNumberOfAvatar()

                                Toast.makeText(
                                    this,
                                    "Your answer (" + answer + ") was successfully saved.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                Toast.makeText(
                                    this,
                                    "Error occurred. Try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(this, "Error occurred. Try again.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "You must either select one choice in the radio buttons, or give a text answer.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        return resultSaved
    }

    fun incrementQuizNumberOfAvatar(){
        if(avatar != null){
            var avatarToUpdate : AvatarModel? = manager.avatarDAO.getAvatarByAvatarId(avatar!!.avatarId)
            if(avatarToUpdate != null) {

                avatarToUpdate!!.quizStage = (avatarToUpdate!!.quizStage.toInt() + 1).toString()
                manager.avatarDAO.modifyAvatar(avatarToUpdate!!)

                if(avatarToUpdate!!.quizStage.toInt() < 5) {
                    if(ProgramManager.pendingAssessments != null) {
                        var pendingAssessments: HashMap<String, String> =
                            ProgramManager.pendingAssessments!!
                        pendingAssessments.put(
                            avatarToUpdate!!.avatarId,
                            avatarToUpdate!!.quizStage
                        )
                    }
                }else{
                    var pendingAssessments: HashMap<String, String> = ProgramManager.pendingAssessments!!
                    if(pendingAssessments != null){
                        pendingAssessments.remove(avatarToUpdate!!.avatarId)
                    }
                }
            }
        }
    }

}