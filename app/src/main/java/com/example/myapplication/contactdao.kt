package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface contactdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(contactModel: ContactModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(contactModelList: List<ContactModel>)

   @Query("SELECT*FROM ContactModel")
   fun getallcontacts():LiveData<List<ContactModel>>
}