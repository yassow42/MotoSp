package com.creativeoffice.motosp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ProfileEditFragment : Fragment() {
    val RESIM_SEC = 100

    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mStorage: StorageReference
    lateinit var mAuth: FirebaseAuth

    var profilPhotoUri: Uri? = null
    var userID: String? = null
    var ref = FirebaseDatabase.getInstance().reference
    lateinit var adapterMarka: ArrayAdapter<String>
    lateinit var adapterModel: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        mStorage = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid


        kullaniciBilgileriGuncelle(fragmentView)



        fragmentView.imgCLose.setOnClickListener {
            activity?.onBackPressed()

        }

        fragmentView.tvFotografDegistir.setOnClickListener {
            var intent = Intent()

            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, RESIM_SEC)
        }


        fragmentView.imgBtnDegisiklikleriKaydet.setOnClickListener {

            degisiklikleriKaydet(fragmentView)

        }


        return fragmentView
    }


    inner class BackgroundResimCompress() : AsyncTask<Uri, Double, ByteArray>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): ByteArray? {
            var myBitmap = MediaStore.Images.Media.getBitmap(context!!.applicationContext.contentResolver, params[0])

            Log.e("Test","orjinal: " + myBitmap!!.byteCount)
            var resimBytes: ByteArray? = null

            for (i in 1..5) {
                resimBytes = convertBitmaptoByte(myBitmap, 50/i)
                Log.e("Test","sıkısmıs: " + resimBytes!!.size.toString())

            }

            return  resimBytes
        }


        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadPhototoFirebase(result)
        }

    }




    private fun convertBitmaptoByte(myBitmap: Bitmap?, i: Int): ByteArray? {
        var stream = ByteArrayOutputStream()
        myBitmap?.compress(Bitmap.CompressFormat.JPEG, i, stream)
        return stream.toByteArray()
    }

    private fun uploadPhototoFirebase(result: ByteArray?) {
        FirebaseStorage.getInstance().reference.child("users").child(userID.toString()).child("profile_picture").putBytes(result!!)
            .addOnSuccessListener { UploadTask ->
                UploadTask.storage.downloadUrl.addOnSuccessListener { itUri ->
                    val downloadUrl = itUri.toString()
                    ////////////Burada storageden veriyi databaseye attık.
                    FirebaseDatabase.getInstance().reference.child("users").child(userID.toString())
                        .child("user_details").child("profile_picture").setValue(downloadUrl)

                }
            }

    }




    fun degisiklikleriKaydet(fragmentView: View) {
        if (profilPhotoUri != null) {
            var comperessed = BackgroundResimCompress()
            comperessed.execute(profilPhotoUri)

/*
            var dialogYukleniyor = YukleniyorFragment()
            dialogYukleniyor.show(activity!!.supportFragmentManager, "yukleniyorFragmenti")
            //  dialogYukleniyor.isCancelable = false

            FirebaseStorage.getInstance().reference.child("users").child(userID.toString()).child("profile_picture").putFile(profilPhotoUri!!) // burada fotografı kaydettik veritabanına.
                .addOnSuccessListener { UploadTask ->
                    UploadTask.storage.downloadUrl.addOnSuccessListener { itUri ->

                        val downloadUrl = itUri.toString()

                        ////////////Burada storageden veriyi databaseye attık.
                        FirebaseDatabase.getInstance().reference.child("users").child(userID.toString())
                            .child("user_details").child("profile_picture").setValue(downloadUrl)
                            .addOnCompleteListener { p0 ->

                                if (p0.isSuccessful) {

                                    dialogYukleniyor.dismiss()
                                } else {
                                    // Toast.makeText(activity, "Resim Yüklenemedi", Toast.LENGTH_LONG).show()
                                    val snackbar = Snackbar.make(fragmentView.tumLayouts, "Resim Yüklenemedi", Snackbar.LENGTH_LONG)
                                    snackbar.show()
                                }
                            }
                    }
                }
            */

        }

        var refDetails = ref.child("users").child(userID.toString()).child("user_details")
        refDetails.child("biyografi").setValue(fragmentView.etUserBio.text.toString())
        refDetails.child("kullanilan_motor_marka").setValue(fragmentView.spinnerMarka.text.toString())
        refDetails.child("kullanilan_motor_model").setValue(fragmentView.spinnerModel.text.toString())
        refDetails.child("sehir").setValue(fragmentView.etSehir.text.toString())
        refDetails.child("ilce").setValue(fragmentView.etIlce.text.toString())

        val snackbar = Snackbar.make(fragmentView.tumLayouts, "Profilin güncellendi", Snackbar.LENGTH_LONG)
        snackbar.show()
        activity?.onBackPressed()

    }

    fun kullaniciBilgileriGuncelle(fragmentView: View) {
        ref.child("users").child(userID.toString()).child("user_details").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.child("biyografi").value?.let {
                    fragmentView.etUserBio.setText(it.toString())
                }

                p0.child("sehir").value?.let {
                    fragmentView.etSehir.setText(it.toString())
                }

                p0.child("ilce").value?.let {
                    fragmentView.etIlce.setText(it.toString())
                }

                p0.child("kullanilan_motor_marka").value?.let {
                    fragmentView.spinnerMarka.setText(it.toString())
                }

                p0.child("kullanilan_motor_model").value?.let {
                    fragmentView.spinnerModel.setText(it.toString())
                }


            }
        })

        ref.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val markalar = ArrayList<String>()
                val modeller = ArrayList<String>()
                markalar.clear()
                modeller.clear()
                markalar.add("Honda")
                markalar.add("Yamaha")
                markalar.add("Suzuki")
                markalar.add("Kawasaki")
                markalar.add("Triumph")

                var adapterMarka = ArrayAdapter<String>(activity!!.applicationContext, android.R.layout.simple_expandable_list_item_1, markalar)
                fragmentView.spinnerMarka.setAdapter(adapterMarka)

                ref.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (p0.hasChildren()) {
                            for (ds in p0.children) {
                                var model = ds.key.toString()
                                modeller.add(model)
                            }
                            var adapterModel = ArrayAdapter<String>(activity!!.applicationContext, android.R.layout.simple_expandable_list_item_1, modeller)
                            fragmentView.spinnerModel.setAdapter(adapterModel)
                        }
                    }

                })




                ref.child("users").child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        fragmentView.etUserBio.setText(p0.child("user_details").child("biyografi").value.toString())
                        var imgURL = p0.child("user_details").child("profile_picture").value.toString()
                        if (imgURL != "default") {
                            Picasso.get().load(imgURL).into(circleEditProfileImage)
                            mProgresBarEdit.visibility = View.GONE
                        }

                    }

                })


            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESIM_SEC && resultCode == AppCompatActivity.RESULT_OK && data!!.data != null) {

            profilPhotoUri = data.data
            circleEditProfileImage.setImageURI(profilPhotoUri)
            mProgresBarEdit.visibility = View.GONE

        }

    }
}