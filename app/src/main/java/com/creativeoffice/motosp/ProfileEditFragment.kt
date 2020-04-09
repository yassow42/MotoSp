package com.creativeoffice.motosp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.utils.UniversalimageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.view.etUserBio

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
    var secilenMarka: String? = null
    var secilenModel: String? = null

    lateinit var adapterMarka: ArrayAdapter<String>
    lateinit var adapterModel: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentView = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        mDatabaseRef = FirebaseDatabase.getInstance().reference
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

            if (profilPhotoUri != null) {

                var dialogYukleniyor = YukleniyorFragment()
                dialogYukleniyor.show(activity!!.supportFragmentManager, "yukleniyorFragmenti")
                dialogYukleniyor.isCancelable = false





                FirebaseStorage.getInstance().reference.child("users").child(userID.toString()).child("profile_picture").putFile(profilPhotoUri!!) // burada fotografı kaydettik veritabanına.
                    .addOnSuccessListener { UploadTask ->
                        UploadTask.storage.downloadUrl.addOnSuccessListener { itUri ->

                            val downloadUrl = itUri.toString()

                            ////////////Burada storageden veriyi databaseye attık.
                            FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).child("user_details").child("profile_picture").setValue(downloadUrl)
                                .addOnCompleteListener { p0 ->

                                    if (p0.isSuccessful) {
                                        dialogYukleniyor.dismiss()


                                    } else {
                                        Toast.makeText(activity, "Resim Yüklenemedi", Toast.LENGTH_LONG).show()

                                    }
                                }
                        }
                    }
            }

            FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).child("user_details").child("biyografi").setValue(fragmentView.etUserBio.text.toString())
            FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).child("user_details").child("kullanilan_motor_marka").setValue(secilenMarka)
            FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).child("user_details").child("kullanilan_motor_model").setValue(secilenModel)

            Toast.makeText(activity, "Kullanıcı Güncellendi", Toast.LENGTH_SHORT).show()
            activity!!.onBackPressed()
        }


        return fragmentView
    }


    private fun kullaniciBilgileriGuncelle(fragmentView: View) {


        FirebaseDatabase.getInstance().reference.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
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

                adapterMarka = ArrayAdapter<String>(activity!!.applicationContext, android.R.layout.simple_list_item_1, markalar)
                adapterMarka.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMarka.adapter = adapterMarka

                spinnerMarka.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                        secilenMarka = spinnerMarka.selectedItem.toString()
                        if (secilenMarka != "Marka seçiniz") {
                            modeller.clear()
                            for (gelenVeriler in p0.children) {
                                var okunanVeriler = gelenVeriler.getValue(ModelDetaylariData::class.java)
                                if (secilenMarka.equals(okunanVeriler!!.marka.toString())) {

                                    val model = okunanVeriler.model.toString()
                                    modeller.add(model)
                                }

                            }

                            adapterModel = ArrayAdapter<String>(activity!!.applicationContext, android.R.layout.simple_list_item_1, modeller)
                            adapterModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerModel.adapter = adapterModel
                        }
                    }
                }
                spinnerModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                        secilenModel = spinnerModel.selectedItem.toString()


                    }

                }




                FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
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

        }

    }
}