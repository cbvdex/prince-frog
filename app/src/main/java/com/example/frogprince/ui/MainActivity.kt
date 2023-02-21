package com.example.frogprince.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frogprince.R
import com.example.frogprince.contract.DBContract
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.dao.UserDAO
import com.example.frogprince.db.model.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var manager : DAOManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideKeyboard()
        manager = DAOManager(this)
        ProgramManager.manager = DAOManager(this)

        autoLoginWithLatestUser()
        updatePendingAssessmentsIfEmpty()
    }

    fun hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // this method keeps the user logged in, once her registration has completed before.
    override fun onResume() {
        super.onResume()

        manager = DAOManager(this)
        ProgramManager.manager = DAOManager(this)

        autoLoginWithLatestUser()
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

    fun autoLoginWithLatestUser(){
        if(! ProgramManager.FrogPrinceSystem.forcedInitialization) {
            takeLastRegisteredUser()
            if (ProgramManager.FrogPrinceSystem.loggedIn) {
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
            }
        }
    }

    fun registerNewUser(v: View){

            val name : String = this.txtUserName.text.toString()
            val email : String = this.txtEmail.text.toString()
            val passcode : String = this.txtPasscode.text.toString()

            if(name.trim().equals("") || email.trim().equals("") || passcode.trim().equals("")) {
                Toast.makeText(this, "All information must be provided.", Toast.LENGTH_SHORT).show()

            }else if(name.trim().length < 4 || email.trim().length < 4 || passcode.trim().length < 4) {
                Toast.makeText(this, "Name, Email, Password should be at least 4 characters.", Toast.LENGTH_SHORT).show()

            }else{

                val rowId : Long = manager.userDAO.insertUser(UserModel(email, name, email, passcode))

                if(rowId.compareTo(-1) == 0) {
                    this.txtRegistrationResult.setText("The email "+" already exists in the database. Please try other email address.")
                }else{

                    ProgramManager.FrogPrinceSystem.loggedIn = true
                    val userLoggedIn : UserModel? = manager.userDAO.getUserByRowId(rowId.toString());

                    if(userLoggedIn != null) {
                        ProgramManager.FrogPrinceSystem.user = userLoggedIn
                        val intent : Intent = Intent(this, Dashboard::class.java)
                        intent.putExtra(
                            "message",
                            userLoggedIn.userName.toUpperCase() + ", your registration has completed successfully. Enjoy!"
                        )
                        startActivity(intent)
                    }
                }

            }

        }

    fun takeLastRegisteredUser(){
        if(manager != null) {
            var currUser: UserModel? = manager!!.userDAO.getLatestUser()
            if (currUser != null) {
                ProgramManager.FrogPrinceSystem.loggedIn = true
                ProgramManager.FrogPrinceSystem.user = currUser
            }
        }
    }



}