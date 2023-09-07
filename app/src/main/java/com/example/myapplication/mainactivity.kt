package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class mainactivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.CAMERA,
    Manifest.permission.READ_CONTACTS
    )
    val permissioncode=78

lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(allpermissongranter()){
            if(isLocationEnabled(this))
            {
                setuplocationlistener()
            }
            else
            {
                showGPSNotEnabledDialog(this)
            }

        }else {
            askforpermission()
        }
        binding.bottombar.setOnItemSelectedListener {menuitem->
        when (menuitem.itemId) {
            R.id.nav_guard -> {
                infiltrated(guardFragment.newInstance())
            }
            R.id.nav_home -> infiltrated(homefragment.newInstance())
            R.id.nav_profile -> infiltrated(profileFragment.newInstance())
            R.id.nav_dashboard -> infiltrated(MapsFragment())
        }

            true
        }
        binding.bottombar.selectedItemId=R.id.nav_home

        val currentUser = FirebaseAuth.getInstance().currentUser
        val name = currentUser?.displayName.toString()
        val mail = currentUser?.email.toString()
        val phoneNumber = currentUser?.phoneNumber.toString()
        val imageUrl = currentUser?.photoUrl.toString()


        val db = Firebase.firestore
        val user = hashMapOf(
            "name" to name,
            "mail" to mail,
            "phoneNumber" to phoneNumber,
            "imageUrl" to imageUrl
        )

        db.collection("users").document(mail).set(user).addOnSuccessListener {

        }.addOnFailureListener {

        }

    }

    private fun setuplocationlistener() {
    val fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        Log.d("loaction89", "onlocationresult: latitude ${location.latitude} ")
                        Log.d("loaction89", "onlocationresult: longitude ${location.longitude} ")

                        val currentUser = FirebaseAuth.getInstance().currentUser

                        val mail = currentUser?.email.toString()


                        val db = Firebase.firestore
                        val locationdata = mutableMapOf<String,Any>(
                            "lat" to location.latitude.toString(),
                            "long" to location.longitude.toString()

                        )

                        db.collection("users").document(mail).update(locationdata).addOnSuccessListener {

                        }.addOnFailureListener {

                        }


                    }

                }
            },
            Looper.myLooper()
        )


    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable GPS")
            .setMessage("required_for_this_app")
            .setCancelable(false)
            .setPositiveButton("enable_now") { _, _ ->
                context.startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }


    private fun isAllPermissionsGranted(): Boolean {
        for (item in permissions) {
            if (ContextCompat
                    .checkSelfPermission(
                        this,
                        item
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun askforpermission() {
        ActivityCompat.requestPermissions(this,permissions,permissioncode)
    }


    private fun infiltrated(newInstance: Fragment) {
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,newInstance)
        transaction.commit()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==permissioncode)
        {
            if(allpermissongranter())
            {
             opencamera()
                setuplocationlistener()
            }else{
                
            }
        }
    }

    private fun opencamera() {
         val intent=Intent("android.media.action.IMAGE_CAPTURE")
        startActivity(intent)
    }

    private fun allpermissongranter(): Boolean {
for (item in permissions)
{
    if(ContextCompat.checkSelfPermission(this,item)!==PackageManager.PERMISSION_GRANTED)
    {
        return false
    }
}
        return true
    }
}