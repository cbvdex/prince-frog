package com.example.frogprince.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.frogprince.db.model.AvatarModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.ArrayList

class AvatarViewModel(application: Application) : AndroidViewModel(application) {

    val avatarsList : MutableLiveData<List<AvatarModel>> = MutableLiveData()

    init {
        //loadAvatars()
    }

//    private fun loadAvatars() {
//        avatarsList.value = AvatarDataSource.avatars
//    }

    public fun loadAvatars(avalist : ArrayList<AvatarModel>) {
        avatarsList.value = ArrayList<AvatarModel>(avalist)
    }

    fun addAvatar(ava : AvatarModel) {
        //AvatarDataSource.avatars.add(ava)
        //loadAvatars()
    }

}