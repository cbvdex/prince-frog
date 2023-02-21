package com.example.frogprince.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.frogprince.R
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.businesslogic.AssessmentAnalyzer
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.UserModel
import kotlinx.android.synthetic.main.activity_update_avatar.*
import kotlinx.android.synthetic.main.activity_view_avatar.*
import java.io.FileNotFoundException
import java.io.IOException

class UpdateAvatar : AppCompatActivity() {

    lateinit var manager : DAOManager
    var avatarId : String? = null
    var avatar : AvatarModel? = null
    var user: UserModel? = null

    var SELECT_IMAGE = 100
    var SELECT_PHONE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_avatar)
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        manager = DAOManager(this)
        user = ProgramManager.FrogPrinceSystem.user
        populateData()
    }

    override fun onResume() {
        super.onResume()
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        manager = DAOManager(this)
        populateData()
    }

    fun populateData() {
        avatar = null
        avatarId = intent.getStringExtra("avatarId")
        if(avatarId != null){
            avatar = manager.avatarDAO.getAvatarByAvatarId(avatarId!!)
        }
        if(avatar != null) {
            this.txtUpdateNickname.setText(avatar!!.nickName)
            this.txtUpdatePhone.setText(avatar!!.phoneNum)
            this.txtUpdateAid.setText(avatar!!.memoryAid)
            //this.imgUpdate.setImageURI(avatar!!.imageLink)  //TO-DO

            //hide take-assess button
            if (avatar!!.quizStage.toInt() >=5) {
                var btnUpdateSaveTakeQuiz: Button = findViewById<Button>(R.id.btnUpdateSaveTakeQuiz)
                btnUpdateSaveTakeQuiz.setVisibility(View.INVISIBLE);
            }
        }
    }

    fun selectPhone(view : View){
        var i = Intent(Intent.ACTION_PICK)
        i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(i, SELECT_PHONE)
    }

    fun selectPhoto(view : View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SELECT_PHONE && resultCode == Activity.RESULT_OK){
            var txtUpdatePhone : EditText = this.findViewById<EditText>(R.id.txtUpdatePhone)
            var contacturi : Uri = data?.data ?: return
            var cols = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            var rs = contentResolver.query(contacturi, cols, null, null, null)
            if(rs?.moveToFirst()!!){
                txtUpdatePhone.setText(rs.getString(0))
            }
        }

        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){

            var uri = data?.data
            var imageView: ImageView? = findViewById(R.id.imgUpdate)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun saveChangeOnly(view : View){

        if(avatar != null){
            val avatarId : String = avatar!!.avatarId
            val userId : String = ProgramManager.FrogPrinceSystem.user?.userName.toString()
            val nickName : String = this.txtUpdateNickname.text.toString()
            val memoryAid : String = this.txtUpdateAid.text.toString()
            val phoneNum : String = this.txtUpdatePhone.text.toString()
            val imageLink : String = this.txtUpdateAvaPhotoLink.text.toString()
            val quizStage : String = avatar!!.quizStage

            if(nickName.trim().equals("") || memoryAid.trim().equals("")) {
                Toast.makeText(this, "Nickname and Memory Aid information must be provided.", Toast.LENGTH_SHORT).show()

            }else if(nickName.trim().length < 4 || memoryAid.trim().length < 4) {
                Toast.makeText(this, "Nickname, Memory Aid each should be at least 4 characters.", Toast.LENGTH_SHORT).show()

            }else{
                var rowId : Int = manager.avatarDAO.modifyAvatar(
                    AvatarModel(avatarId, userId, nickName, memoryAid, phoneNum, imageLink, quizStage))

                if(rowId.equals(-1)) {
                    Toast.makeText(this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                    this.txtUpdateAvaMessage.setText("Error occurred. Please try again.")
                }else{
                    Toast.makeText(this, "Successfully Updated.", Toast.LENGTH_SHORT).show()
                    gotoViewAvatar(view)
                }
            }
        }

    }

    fun gotoViewAvatar(view : View){
        var intent : Intent = Intent(this, ViewAvatar::class.java)
        intent.putExtra("avatarId", avatar!!.avatarId)
        startActivity(intent)
    }

    fun deleteAvatar(view : View){
        if(avatar != null) {
            Toast.makeText(this, "The deletion of avatar is requested.", Toast.LENGTH_SHORT).show()
            val resDeleteUserAnswers: Boolean = manager.userAnswerDAO.deleteUserAnswerForAvatarId(avatar!!.avatarId)
            val resDeleteAvatar: Boolean = manager.avatarDAO.deleteAvatar(avatar!!.avatarId)
            if (resDeleteUserAnswers && resDeleteAvatar) {
                Toast.makeText(this, "The avatar and assessments have been successfully removed.", Toast.LENGTH_SHORT).show()
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

    fun saveChangeFinishQuiz(view : View){
        if(avatar != null){
            val avatarId : String = avatar!!.avatarId
            val userId : String = ProgramManager.FrogPrinceSystem.user?.userName.toString()
            val nickName : String = this.txtUpdateNickname.text.toString()
            val memoryAid : String = this.txtUpdateAid.text.toString()
            val phoneNum : String = this.txtUpdatePhone.text.toString()
            val imageLink : String = this.txtUpdateAvaPhotoLink.text.toString()
            val quizStage : String = avatar!!.quizStage

            if(nickName.trim().equals("") || memoryAid.trim().equals("")) {
                Toast.makeText(this, "Nickname and Memory Aid information must be provided.", Toast.LENGTH_SHORT).show()

            }else if(nickName.trim().length < 4 || memoryAid.trim().length < 4) {
                Toast.makeText(this, "Nickname, Memory Aid each should be at least 4 characters.", Toast.LENGTH_SHORT).show()

            }else{
                var rowId : Int = manager.avatarDAO.modifyAvatar(
                    AvatarModel(avatarId, userId, nickName, memoryAid, phoneNum, imageLink, quizStage))
                if(rowId.equals(-1)) {
                    Toast.makeText(this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                    this.txtUpdateAvaMessage.setText("Error occurred. Please try again.")
                }else{
                    Toast.makeText(this, "Successfully Updated.", Toast.LENGTH_SHORT).show()
                    gotoAssessQuiz(view)
                }
            }
        }
    }

    fun gotoAvaList(view : View){
        startActivity(Intent(this, AvatarManagement::class.java))
    }

    fun gotoAssessQuiz(view : View){
        var intent = Intent(this, Assessment_Quiz::class.java)
        intent.putExtra("avatarId", avatar?.avatarId)
        startActivity(intent)
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

}