package com.medhelp.callmed2.ui._main_page.fragment_scan_passport

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.medhelp.callmed2.databinding.FragmentScanPassportBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_scanner_doc.view_and_alerts.PreviewImageView

class ScanPassportFragment : Fragment() {
    // фаил на распознание должен быть меньше 20мб и 20мп


    var presenter : ScanPassportPresenter? = null
    private var binding: FragmentScanPassportBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanPassportBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter= ScanPassportPresenter(this)
        presenter?.deleteAllFilesInCash()

        binding?.toolbar?.setNavigationOnClickListener{
            (context as MainPageActivity?)!!.showNavigationMenu()
        }

        binding?.imageMain?.setData(requireActivity(),object : PreviewImageView.PreviewImageViewListener {
            override fun startPhoto() {
                startPhoto(1,binding!!.imageMain!!)
            }

            override fun imgDeleted() {
                stateImgNotExist()
            }
        })
        binding?.imageMain2?.setData(requireActivity(),object : PreviewImageView.PreviewImageViewListener {
            override fun startPhoto() {
                startPhoto(2,binding!!.imageMain2!!)
            }

            override fun imgDeleted() {
            }
        })

        binding?.btnRecognizeText?.setOnClickListener {
            val photoURI1 = binding!!.imageMain!!.photoURI
            val photoURI2 = binding!!.imageMain2!!.photoURI

            if(photoURI1 != null || photoURI2 != null)
                presenter!!.sendImagesToSRM(photoURI1,photoURI2)

        }

        // Request camera permissions
        if (permissionGranted()) {

        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        stateImgNotExist()

        presenter?.requestToken()
    }

    fun startPhoto(requestKey : Int, image : PreviewImageView){
        val packageManager = requireActivity().packageManager
        val isCamera = packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

        if(isCamera==null || !isCamera) {
            Toast.makeText(requireContext(), "Нет камеры", Toast.LENGTH_LONG).show()
            return
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {

            val photoURI=presenter?.getNewUriForPhoto()

            if(photoURI!=null)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            image.photoURI = photoURI.toString()
            startActivityForResult(takePictureIntent, requestKey)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (permissionGranted()) {
                //presenter?.getData()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(requireActivity(),
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                1 -> {
                    binding!!.imageMain!!.setImageURIWithProcessing()
                    stateImgExist()
                }
                2 -> {
                    binding!!.imageMain2!!.setImageURIWithProcessing()
                    stateImgExist()
                }
            }
        }else{
            when(requestCode){
                1 -> binding!!.imageMain!!.photoURI = null
                2 -> binding!!.imageMain2!!.photoURI = null
            }
        }
    }


    fun clearImg(){
        binding?.imageMain?.clearImage()
        binding?.imageMain2?.clearImage()
        stateImgNotExist()
    }
    fun stateImgExist(){
        binding?.btnRecognizeText?.isEnabled = true
    }
    fun stateImgNotExist(){
        binding?.btnRecognizeText?.isEnabled = false
    }

    private fun permissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
    companion object {
        //private const val TAG = "CameraXApp"
        //private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 101
        private val REQUIRED_PERMISSIONS =
            mutableListOf (Manifest.permission.CAMERA).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}