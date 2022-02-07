package com.example.tinder.ui.auth.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tinder.AppPreferences
import com.example.tinder.R
import com.example.tinder.databinding.FragmentUserDetailsBinding
import com.example.tinder.network.Results
import com.example.tinder.security.isEmpty
import com.example.tinder.ui.auth.viewModel.AuthVM
import com.example.tinder.ui.auth.viewModel.AuthVMF
import com.example.tinder.ui.home.HomeActivity
import com.example.tinder.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class UserDetailsFragment : Fragment(), KodeinAware {

    override val kodein by kodein()

    private val factory: AuthVMF by instance()

    private lateinit var fragmentContext: Context
    private lateinit var fragmentActivity: FragmentActivity

    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var viewModel: AuthVM

    private lateinit var storageReference: StorageReference

    private lateinit var appPreferences: AppPreferences

    private val myCalendar: Calendar = Calendar.getInstance()
    private var dateChosen: Boolean = false

    private var gender: String = ""
    private var interest: String = ""

    private var filePath1: Uri? = null
    private var filePath2: Uri? = null
    private var filePath3: Uri? = null
    private var filePath4: Uri? = null
    private var filePath5: Uri? = null
    private var filePath6: Uri? = null
    private var image1: String? = ""
    private var image2: String? = ""
    private var image3: String? = ""
    private var image4: String? = ""
    private var image5: String? = ""
    private var image6: String? = ""
    private var imageSelected: Int = 0

    private val timeStamp = Timestamp.now()
    private val milliseconds = timeStamp.seconds * 1000 + timeStamp.nanoseconds / 1000000

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            setImage(null)
        }
    }

    private val pickImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            setImage(it)
        }

    private fun setImage(uri: Uri?) {
        if (uri != null) {
            when (imageSelected) {
                1 -> {
                    filePath1 = uri
                }
                2 -> {
                    filePath2 = uri
                }
                3 -> {
                    filePath3 = uri
                }
                4 -> {
                    filePath4 = uri
                }
                5 -> {
                    filePath5 = uri
                }
                6 -> {
                    filePath6 = uri
                }
            }
        }

        when (imageSelected) {
            1 -> {
                with(binding) {
                    Picasso.get().load(filePath1).into(image1)
                    image1.visibility = View.VISIBLE
                    addImage1.visibility = View.GONE
                    uploadImage(filePath1!!, 1)
                }
            }
            2 -> {
                with(binding) {
                    Picasso.get().load(filePath2).into(image2)
                    image2.visibility = View.VISIBLE
                    addImage2.visibility = View.GONE
                    uploadImage(filePath2!!, 2)
                }
            }
            3 -> {
                with(binding) {
                    Picasso.get().load(filePath3).into(image3)
                    image3.visibility = View.VISIBLE
                    addImage3.visibility = View.GONE
                    uploadImage(filePath3!!, 3)
                }
            }
            4 -> {
                with(binding) {
                    Picasso.get().load(filePath4).into(image4)
                    image4.visibility = View.VISIBLE
                    addImage4.visibility = View.GONE
                    uploadImage(filePath4!!, 4)
                }
            }
            5 -> {
                with(binding) {
                    Picasso.get().load(filePath5).into(image5)
                    image5.visibility = View.VISIBLE
                    addImage5.visibility = View.GONE
                    uploadImage(filePath5!!, 5)
                }
            }
            6 -> {
                with(binding) {
                    Picasso.get().load(filePath6).into(image6)
                    image6.visibility = View.VISIBLE
                    addImage6.visibility = View.GONE
                    uploadImage(filePath6!!, 6)
                }
            }
        }
    }


    private fun uploadImage(uri: Uri, count: Int) {
        val pd = ProgressDialog(requireContext())
        pd.setTitle("Uploading image...")
        pd.show()
        val imgName: String = when (count) {
            1 -> {
                "image1"
            }
            2 -> {
                "image2"
            }
            3 -> {
                "image3"
            }
            4 -> {
                "image4"
            }
            5 -> {
                "image5"
            }
            6 -> {
                "image6"
            }
            else -> {
                "image"
            }
        }

        val photoRef = storageReference.child("users/${milliseconds}/photo")
            .child(imgName)
        photoRef.putFile(uri)
            .addOnProgressListener { taskSnapshot ->
                val percentComplete = if (taskSnapshot.totalByteCount > 0) {
                    (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                } else 0
                showProgressNotification(
                    requireContext(),
                    "Shop Details",
                    "Uploading image.",
                    percentComplete
                )
            }.continueWithTask { task ->
                // Forward any executions
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                // Request the public download URL
                photoRef.downloadUrl
            }
            .addOnSuccessListener {
                when (count) {
                    1 -> {
                        image1 = it.toString()
                    }
                    2 -> {
                        image1 = it.toString()
                    }
                    3 -> {
                        image2 = it.toString()
                    }
                }
                showUploadFinishedNotification(requireContext(), "Shop Details", it)
                pd.dismiss()
            }
            .addOnFailureListener {
                log(it.localizedMessage!!)
                showUploadFinishedNotification(requireContext(), "Shop Details", null)
                pd.dismiss()
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContext = requireContext()
        fragmentActivity = requireActivity()

        binding = FragmentUserDetailsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(fragmentActivity, factory).get(AuthVM::class.java)

        storageReference = FirebaseStorage.getInstance().reference

        appPreferences = AppPreferences(fragmentContext)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (appPreferences.getName() == "") {
            binding.etName.setText(appPreferences.getName())
        }

        val date =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }


        binding.birthday.setOnClickListener {
            DatePickerDialog(
                fragmentContext,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = binding.root.findViewById(checkedId)
            gender = radio.text.toString()
        }

        binding.interestRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = binding.root.findViewById(checkedId)
            interest = radio.text.toString()
        }

        binding.linearLayout.setOnClickListener {
            imageSelected = 1
            showChoiceDialog()
        }

        binding.linearLayout3.setOnClickListener {
            imageSelected = 2
            showChoiceDialog()
        }

        binding.linearLayout2.setOnClickListener {
            imageSelected = 3
            showChoiceDialog()
        }

        binding.linearLayout4.setOnClickListener {
            imageSelected = 4
            showChoiceDialog()
        }

        binding.linearLayout5.setOnClickListener {
            imageSelected = 5
            showChoiceDialog()
        }

        binding.linearLayout6.setOnClickListener {
            imageSelected = 6
            showChoiceDialog()
        }


        binding.btnSave.setOnClickListener {
            saveDetails()
        }

    }

    private fun saveDetails(){

        if (!isValidate())
            return

        val name = binding.etName.text.toString()
        val dob = binding.birthday.text.toString()

        val data = HashMap<String, Any>()

        data["name"] = name
        data["id"] = appPreferences.getId()!!
        data["dob"] = dob
        data["gender"] = gender
        data["interest"] = interest
        data["image1"] = image1!!
        data["image2"] = image2!!
        data["image3"] = image3!!
        data["image4"] = image4!!
        data["image5"] = image5!!
        data["image6"] = image6!!

        viewModel.saveUserDetails(data)

        viewModel.saveUserDetails.observe(viewLifecycleOwner){
            when(it){
                is Results.Loading -> {}
                is Results.Success -> {
                    startActivity(Intent(fragmentContext, HomeActivity::class.java))
                    fragmentActivity.finish()
                }
                is Results.Error -> {
                    fragmentContext.toast("Try again later...")
                    log(it.exception.localizedMessage!!)
                }
            }
        }

    }

    private fun isValidate(): Boolean{

        // This function will validate the user details

        if (isEmpty(binding.etName)){
            binding.etName.error = "Name required."
            return false
        }

        if (!dateChosen) {
            fragmentContext.toast("Choose date of birth")
            return false
        }

        if (gender == "") {
            fragmentContext.toast("Choose gender")
            return false
        }

        if (interest == "") {
            fragmentContext.toast("Choose your interest.")
            return false
        }

        if (filePath1 == null) {
            requireContext().toast("Choose 1st image of the shop.")
            log("Choose 1st image of the shop.")
            return false
        }

        if (filePath2 == null) {
            requireContext().toast("Choose 2nd image of the shop.")
            log("Choose 2nd image of the shop.")
            return false
        }

        if (filePath3 == null) {
            requireContext().toast("Choose 3rd image of the shop.")
            log("Choose 3rd image of the shop.")
            return false
        }

        if (filePath4 == null) {
            requireContext().toast("Choose 4th image of the shop.")
            log("Choose 4th image of the shop.")
            return false
        }

        if (filePath5 == null) {
            requireContext().toast("Choose 5th image of the shop.")
            log("Choose 5th image of the shop.")
            return false
        }

        if (filePath6 == null) {
            requireContext().toast("Choose 6th image of the shop.")
            log("Choose 6th image of the shop.")
            return false
        }

        return true

    }

    private fun showChoiceDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.show_image_picker_chocice)
        val cameraButton = dialog.findViewById<LinearLayout>(R.id.camera_btn)
        val galleryButton = dialog.findViewById<LinearLayout>(R.id.gallery_btn)
        cameraButton.setOnClickListener {
            launchCamera()
            dialog.dismiss()
        }
        galleryButton.setOnClickListener {
            launchGallery()
            dialog.dismiss()
        }
        dialog.show()
        val width = fragmentActivity.getHWofScreen().width
        val height = fragmentActivity.getHWofScreen().height
        dialog.window?.setLayout(width, height)
    }

    private fun launchCamera() {
        log("Thread_Name:- ${Thread.currentThread().name}")
        runBlocking {
            launch(Dispatchers.IO) {
                log("Thread_Name IO:- ${Thread.currentThread().name}")
                askCameraPermission()
            }
        }
    }

    private fun launchGallery() {
        log("Thread_Name:- ${Thread.currentThread().name}")
        runBlocking {
            launch(Dispatchers.IO) {
                log("Thread_Name IO:- ${Thread.currentThread().name}")
                askStoragePermission()
            }
        }
    }

    private fun askCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                log("Camera permission already granted")
                clickImage()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                binding.userDetails.showSnackbar(
                    binding.userDetails,
                    "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE,
                    "Ok"
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }


    private fun askStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                log("Camera permission already granted")
                pickImageFromGallery.launch("image/*")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                binding.userDetails.showSnackbar(
                    binding.userDetails,
                    "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE,
                    "Ok"
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun clickImage() {
        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        when (imageSelected) {
            1 -> {
                filePath1 = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    photoFile
                )
                takePicture.launch(filePath1)
            }
            2 -> {
                filePath2 = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    photoFile
                )
                takePicture.launch(filePath2)
            }
            3 -> {
                filePath3 = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    photoFile
                )
                takePicture.launch(filePath3)
            }
            else -> {
                log("No image selected")
            }
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
                AlertDialog.Builder(requireContext())
                    .setTitle("Permission required")
                    .setMessage(
                        "We don't we have permission to use your camera or storage, it is necessary for " +
                                "taking photo for your profile picture. " +
                                "Give us permission to use camera or storage."
                    )
                    .setPositiveButton("Ok") { _, _ ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton("No", null)
                    .create().show()
            }
        }

    private fun updateLabel() {
        dateChosen = true
        val myFormat = "MM/dd/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.birthday.setText(dateFormat.format(myCalendar.time))
    }


}