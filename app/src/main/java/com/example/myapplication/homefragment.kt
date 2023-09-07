package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentHomefragmentBinding
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class homefragment : Fragment() {
    lateinit var inviteAdapter:InviteAdapter
    lateinit var mContext:Context
    private val listcontact:ArrayList<ContactModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }


lateinit var binding: FragmentHomefragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentHomefragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listmemebers= listOf<membermodel>(
            membermodel(
                "Aman",
                "9th buildind, 2nd floor, maldiv road, manali 9th buildind, 2nd floor",
                "90%",
                "220"
            ),
            membermodel(
                "Aditya",
                "10th buildind, 3rd floor, maldiv road, manali 10th buildind, 3rd floor",
                "80%",
                "210"
            ),
            membermodel(
                "uttam",
                "11th buildind, 4th floor, maldiv road, manali 11th buildind, 4th floor",
                "70%",
                "200"
            ),
            membermodel(
                "Ishuvendra",
                "12th buildind, 5th floor, maldiv road, manali 12th buildind, 5th floor",
                "60%",
                "190"
            ),
        )
        val adapter=recyclerview_adapter(listmemebers)




        binding.recycleradapter.layoutManager=LinearLayoutManager(requireContext())
        binding.recycleradapter.adapter=adapter

         inviteAdapter=InviteAdapter(listcontact)
        fetchdatabasecontacts()
        CoroutineScope(Dispatchers.IO).launch {


            insertdatabasecntacts(fetchcontacts())
            withContext(Dispatchers.Main){
                inviteAdapter.notifyDataSetChanged()
            }

        }



       val  inviteRecycler=requireView().findViewById<RecyclerView>(R.id.recycler_invite)
        inviteRecycler.layoutManager=
            LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        inviteRecycler.adapter=inviteAdapter

        val  threedots=requireView().findViewById<ImageView>(R.id.three_dots)
        threedots.setOnClickListener{
            SharedPref.putBoolean(PrefConstants.IS_USER_LOGGED_IN,false)
            FirebaseAuth.getInstance().signOut()
        }
    }

    private  fun fetchdatabasecontacts() {
        val database = MyFamilyDatabase.getDatabase(requireContext())
         database.contactdao().getallcontacts().observe(viewLifecycleOwner){
             listcontact.clear()
             listcontact.addAll(it)
             inviteAdapter.notifyDataSetChanged()
         }
    }


    private suspend fun insertdatabasecntacts(listcontact: ArrayList<ContactModel>) {
        val database=MyFamilyDatabase.getDatabase(requireContext())
        database.contactdao().insertAll(listcontact)
    }


    private fun fetchcontacts(): ArrayList<ContactModel> {
        val cr=requireActivity().contentResolver
        val cursor=cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null)
        val listContacts: ArrayList<ContactModel> = ArrayList()

        if(cursor!=null&&cursor.count>0) {
            while (cursor != null && cursor.moveToNext()) {
              val id=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                if (hasPhoneNumber>0){
                    val pCur=cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
                        arrayOf(id),
                        ""
                    )


                    if (pCur != null && pCur.count > 0) {

                        while (pCur != null && pCur.moveToNext()) {

                            val phoneNum =
                                pCur.getString(pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            listContacts.add(ContactModel(name,phoneNum))

                        }
                        pCur.close()

                    }
                }

            }
            if(cursor!=null){
                cursor.close()
            }
        }
        return listContacts
    }

    companion object {

        @JvmStatic
        fun newInstance()=homefragment()
    }


}