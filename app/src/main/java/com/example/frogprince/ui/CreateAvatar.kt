package com.example.frogprince.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frogprince.R
import com.example.frogprince.businesslogic.AssessmentAnalyzer
import com.example.frogprince.contract.ProgramManager
import com.example.frogprince.dao.DAOManager
import com.example.frogprince.db.model.AvatarModel
import com.example.frogprince.db.model.UserModel
import kotlinx.android.synthetic.main.activity_create_avatar.*
import java.io.FileNotFoundException
import java.io.IOException

class CreateAvatar : AppCompatActivity() {

    lateinit var manager : DAOManager
    var user: UserModel? = null

    var SELECT_IMAGE = 100
    var SELECT_PHONE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_avatar)
        init()
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    fun init(){
        //hide keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager = DAOManager(this)
        user = ProgramManager.FrogPrinceSystem.user
    }

    fun gotoDashboard(view : View){
        startActivity(Intent(this, Dashboard::class.java))
    }

    fun gotoAvatarList(view : View){
        startActivity(Intent(this, AvatarManagement::class.java))
    }

    fun gotoAllAssessment(view : View){
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
            var txtCreatePhone : EditText = this.findViewById<EditText>(R.id.txtCreatePhone)
            var contacturi : Uri = data?.data ?: return
            var cols = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            var rs = contentResolver.query(contacturi, cols, null, null, null)
            if(rs?.moveToFirst()!!){
                txtCreatePhone.setText(rs.getString(0))
            }
        }

        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){

            var uri = data?.data
            var imageView: ImageView? = findViewById(R.id.imgCreate)
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

    fun createAvatarQuizLater(view : View){

        val userId : String = ProgramManager.FrogPrinceSystem.user?.userName.toString()
        val nickName : String = this.txtCreateNickname.text.toString()
        val memoryAid : String = this.txtCreateAid.text.toString()
        val phoneNum : String = this.txtCreatePhone.text.toString()
        val imageLink : String = this.txtPhotoLink.text.toString()
        val quizStage : String = "1"

        if(nickName.trim().equals("") || memoryAid.trim().equals("")) {
            Toast.makeText(this, "Nickname and Memory Aid information must be provided.", Toast.LENGTH_SHORT).show()

        }else if(nickName.trim().length < 4 || memoryAid.trim().length < 4) {
            Toast.makeText(this, "Nickname, Memory Aid each should be at least 4 characters.", Toast.LENGTH_SHORT).show()

        }else{
            var rowId : Long = manager.avatarDAO.insertAvatar(AvatarModel("", userId, nickName, memoryAid, phoneNum, imageLink, quizStage))
            if(rowId.equals(-1)) {
                this.txtCreateAvaMessage.setText("Error occurred. Please try again.")
            }else{
                if(ProgramManager.pendingAssessments != null) {
                    var pendingAssessments: HashMap<String, String> =
                        ProgramManager.pendingAssessments!!
                    pendingAssessments.put(rowId.toString(), "1")
                }
                this.txtCreateAvaMessage.setText("Avatar is added Successfully.")
                var intent: Intent = Intent(this, ViewAvatar::class.java)
                intent.putExtra("avatarId", rowId.toString())
                startActivity(intent)
            }
        }
    }

    fun createAvatarTakeQuiz(view : View){

        val userId : String = ProgramManager.FrogPrinceSystem.user?.userName.toString()
        val nickName : String = this.txtCreateNickname.text.toString()
        val memoryAid : String = this.txtCreateAid.text.toString()
        val phoneNum : String = this.txtCreatePhone.text.toString()
        val imageLink : String = this.txtPhotoLink.text.toString()
        val quizStage : String = "1"

        if(nickName.trim().equals("") || memoryAid.trim().equals("")) {
            Toast.makeText(this, "Nickname and Memory Aid information must be provided.", Toast.LENGTH_SHORT).show()

        }else if(nickName.trim().length < 4 || memoryAid.trim().length < 4) {
            Toast.makeText(this, "Nickname, Memory Aid each should be at least 4 characters.", Toast.LENGTH_SHORT).show()

        }else{
            var rowId : Long = manager.avatarDAO.insertAvatar(AvatarModel("", userId, nickName, memoryAid, phoneNum, imageLink, quizStage))
            if(rowId.equals(-1)) {
                this.txtCreateAvaMessage.setText("Error occurred. Please try again.")
            }else{
                if(ProgramManager.pendingAssessments != null) {
                    var pendingAssessments: HashMap<String, String> =
                        ProgramManager.pendingAssessments!!
                    pendingAssessments.put(rowId.toString(), "1")
                }
                this.txtCreateAvaMessage.setText("Avatar is added Successfully.")
                var intent : Intent = Intent(this, Assessment_Quiz::class.java)
                intent.putExtra("avatarId", rowId.toString())
                startActivity(intent)
            }
        }

    }

}
