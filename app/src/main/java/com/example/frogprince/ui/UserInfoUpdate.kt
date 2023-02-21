package com.example.frogprince.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.frogprince.R
import com.example.frogprince.contract.DBContract
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.UserModel
import kotlinx.android.synthetic.main.activity_user_info_update.*

class UserInfoUpdate : AppCompatActivity() {

    lateinit var manager : DAOManager
    var user : UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_update)

        manager = DAOManager(this)
        ProgramManager.manager = DAOManager(this)
        loadUser()
    }

    fun onResume(savedInstanceState: Bundle?) {
        super.onResume()
        loadUser()
    }

    fun loadUser(){
        var userId : String? = ProgramManager.FrogPrinceSystem.user?.userId
        if(userId != null) {
            user = manager.userDAO.getUserByUserId(userId)
            if (user != null) {
                this.txtUserInfoUserName.setText(user!!.userName.toString())
                this.txtUserInfoEmail.setText(user!!.email.toString())
                this.txtUserInfoPasscode.setText(user!!.passCode.toString())
            }
        }
    }

    fun updateUserInfo(view : View){
        if(user != null) {
            val name : String = this.txtUserInfoUserName.text.toString()
            val email : String =this.txtUserInfoEmail.text.toString()
            val passcode : String =this.txtUserInfoPasscode.text.toString()

            if(name.trim().equals("") || email.trim().equals("") || passcode.trim().equals("")) {
                Toast.makeText(this, "All information must be provided.", Toast.LENGTH_SHORT).show()

            }else if(name.trim().length < 4 || email.trim().length < 4 || passcode.trim().length < 4) {
                Toast.makeText(this, "Name, Email, Password each should be at least 4 characters.", Toast.LENGTH_SHORT).show()

            }else {
                manager.userDAO.updateUser(UserModel(user!!.userId, name, email, passcode))

                loadUser()
                Toast.makeText(this, "Updated successfully.", Toast.LENGTH_SHORT).show()
                navDashboard(view)
            }
        }else{
            Toast.makeText(this, "Error occurred. Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    fun navDashboard(view : View){
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
    }

}