package com.putu.sora.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.putu.sora.R
import com.putu.sora.databinding.FragmentPostStoryBinding
import com.putu.sora.extra.Helper
import com.putu.sora.extra.ResultResponse
import com.putu.sora.extra.UserPreferences
import com.putu.sora.ui.activity.CameraActivity
import com.putu.sora.ui.activity.WelcomeActivity
import com.putu.sora.viewmodel.PostStoryViewModel
import com.putu.sora.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PostStoryFragment : Fragment(), MenuProvider {

    private var _postStoryBind: FragmentPostStoryBinding? = null
    private val postStoryBind get() = _postStoryBind

    private lateinit var postStoryViewModel: PostStoryViewModel

    private lateinit var token: String

    private val helper = Helper()

    private var getFile: File? = null

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat: Double? = null

    private var lon: Double? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _postStoryBind = FragmentPostStoryBinding.inflate(inflater, container, false)
        return postStoryBind?.root
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCameraPermission()
        setUpViewModel()

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.new_post)

        postStoryBind?.btnCamera?.setOnClickListener {
            startCameraX()
        }

        postStoryBind?.btnGallery?.setOnClickListener {
            startGallery()
        }

        postStoryBind?.addLocationCheck?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLastLocation()

            } else {
                lat = null
                lon = null

                Toast.makeText(activity, R.string.uncheck_location_check, Toast.LENGTH_SHORT).show()
            }
        }

        postStoryBind?.btnUpload?.setOnClickListener {
            postStory()
        }

        postStoryViewModel.isLoading.observe(viewLifecycleOwner) {
            postStoryBind?.let { it1 ->
                helper.isLoading(
                    it,
                    it1.postStoryProgressBar
                )
            }
        }

        postStoryBind?.descriptionEditText?.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude

                    Toast.makeText(activity, getString(R.string.success_location_check), Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(requireActivity(), getString(R.string.error_location_check), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }

            else -> {

            }
        }
    }

    private fun getCameraPermission() {
        requestCameraPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions[Manifest.permission.CAMERA] ?: false -> {}

            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false -> {}

            else -> {
                AlertDialog.Builder(requireActivity()).apply {
                    setTitle(R.string.permission_alert_title)
                    setMessage(R.string.permission_alert_message)
                    setPositiveButton(R.string.back_alert_button) { _, _ ->
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("picture", File::class.java)
            } else {
                result.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile

            myFile?.let { file ->
                helper.rotateFile(file, isBackCamera)
                postStoryBind?.imgPreview?.setImageBitmap(BitmapFactory.decodeFile(getFile?.path))
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, getString(R.string.choose_pic))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = helper.uriToFile(selectedImage, requireActivity())

            getFile = myFile

            postStoryBind?.imgPreview?.setImageURI(selectedImage)
        }
    }

    private fun postStory() {
        when {
            getFile == null -> {
                val builder = AlertDialog.Builder(requireActivity())
                val alert = builder.create()
                builder
                    .setTitle(R.string.empty_picture_alert_title)
                    .setMessage(R.string.empty_picture_alert_message)
                    .setPositiveButton(R.string.back_alert_button) { _, _ ->
                        alert.cancel()
                    }.show()
            }

            postStoryBind?.descriptionEditText?.text?.isEmpty() == true -> {
                val builder = AlertDialog.Builder(requireActivity())
                val alert = builder.create()
                builder
                    .setTitle(R.string.empty_description_alert_title)
                    .setMessage(R.string.empty_description_alert_message)
                    .setPositiveButton(R.string.back_alert_button) { _, _ ->
                        alert.cancel()
                    }.show()
            }

            else -> {

                val file = helper.compressFile(getFile as File)

                val description = postStoryBind?.descriptionEditText?.text.toString().toRequestBody("text/plain".toMediaType())

                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

                val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

                postStoryViewModel.postStory(token, imageMultipart, description, lat, lon).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultResponse.Loading -> {

                            }

                            is  ResultResponse.Success -> {
                                AlertDialog.Builder(requireActivity()).apply {
                                    setTitle(R.string.success_alert_title)
                                    setMessage(R.string.post_story_success_alert_message)
                                    setPositiveButton(R.string.continue_alert_button) { _, _ ->
                                        activity?.onBackPressedDispatcher?.onBackPressed()
                                    }
                                    create()
                                    show()
                                }
                            }

                            is  ResultResponse.Error -> {
                                when (result.error) {
                                    "Unable to resolve host \"story-api.dicoding.dev\": No address associated with hostname" -> {
                                        val builder = AlertDialog.Builder(requireActivity())
                                        val alert = builder.create()
                                        builder
                                            .setTitle(R.string.failed_response_alert_title)
                                            .setMessage(R.string.failed_response_alert_message)
                                            .setPositiveButton(R.string.back_alert_button) { _, _ ->
                                                alert.cancel()
                                            }.show()
                                    }

                                    else -> {
                                        val builder = AlertDialog.Builder(requireActivity())
                                        val alert = builder.create()
                                        builder
                                            .setTitle(R.string.failed_response_alert_title)
                                            .setMessage(R.string.post_story_failed_alert_message)
                                            .setPositiveButton(R.string.back_alert_button) { _, _ ->
                                                alert.cancel()
                                            }.show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUpViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        postStoryViewModel = ViewModelProvider(this, ViewModelFactory(pref, requireActivity()))[PostStoryViewModel::class.java]

        postStoryViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                this.token = user.token
            } else {
                startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                activity?.onBackPressedDispatcher?.onBackPressed()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _postStoryBind = null
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}