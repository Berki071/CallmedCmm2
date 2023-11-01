package com.medhelp.callmed2.ui._main_page.fragment_scanner_doc

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.medhelp.callmed2.R
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_scanner_doc.view_and_alerts.PreviewImageView
import com.medhelp.callmed2.utils.Different

public class ScannerDocFragment : Fragment() {
    //private val REQUEST_TAKE_PHOTO = 1

    var presenter : ScannerDocPresenter? = null

    lateinit var toolbar : Toolbar
    lateinit var image1 : PreviewImageView
    lateinit var image2 : PreviewImageView
    lateinit var image3 : PreviewImageView
    lateinit var image4 : PreviewImageView
    lateinit var image5 : PreviewImageView
    lateinit var image6 : PreviewImageView
    lateinit var btnSend : Button
    lateinit var dialogSychronization : FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_doc_scanner, container, false)

        presenter= ScannerDocPresenter(this)

        toolbar=rootView.findViewById(R.id.toolbar)
        image1=rootView.findViewById(R.id.image1)
        image2=rootView.findViewById(R.id.image2)
        image3=rootView.findViewById(R.id.image3)
        image4=rootView.findViewById(R.id.image4)
        image5=rootView.findViewById(R.id.image5)
        image6=rootView.findViewById(R.id.image6)
        btnSend=rootView.findViewById(R.id.btnSend)
        dialogSychronization=rootView.findViewById(R.id.dialogSychronization)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.deleteAllFilesInCash()

        toolbar.setNavigationOnClickListener{
            (context as MainPageActivity?)!!.showNavigationMenu()
        }

        image1.setData(requireActivity(),object : PreviewImageView.PreviewImageViewListener {
            override fun startPhoto() {
                startPhoto(1,image1)
            }
            override fun imgDeleted() {}
        })
        image2.setData(requireActivity(),object : PreviewImageView.PreviewImageViewListener {
            override fun startPhoto() {
                startPhoto(2,image2)
            }
            override fun imgDeleted() {}
        })
        image3.setData(requireActivity(),object : PreviewImageView.PreviewImageViewListener {
            override fun startPhoto() {
                startPhoto(3,image3)
            }
            override fun imgDeleted() {}
        })
        image4.setData(requireActivity(),object : PreviewImageView.PreviewImageViewListener {
            override fun startPhoto() {
                startPhoto(4,image4)
            }
            override fun imgDeleted() {}
        })
        image5.setData(requireActivity(),object : PreviewImageView.PreviewImageViewListener {
            override fun startPhoto() {
                startPhoto(5,image5)
            }
            override fun imgDeleted() {}
        })
        image6.setData(requireActivity(),object : PreviewImageView.PreviewImageViewListener {
            override fun startPhoto() {
                startPhoto(6,image6)
            }
            override fun imgDeleted() {}
        })

        btnSend.setOnClickListener{
            if(!presenter!!.isTestAcc())
                savePhoto()
            else{
                presenter!!.deleteAllFilesInCash()
                Different.showAlertInfo(requireActivity(), "", "Успешно отправлено!")
            }

        }

        // Request camera permissions
        if (permissionGranted()) {
            presenter?.getData()
            //presenter?.hideSynchronizationDialog()   //для теста раскоментить, гетдата закоментить
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

    }


//    override fun onResume() {
//        super.onResume()
//        presenter?.changeStatusSync()
//    }

    fun clearAllImage(){
        image1.clearImage()
        image2.clearImage()
        image3.clearImage()
        image4.clearImage()
        image5.clearImage()
        image6.clearImage()
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
                presenter?.getData()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }

        if(resultCode == RESULT_OK){
            when(requestCode){
                1 -> image1.setImageURIWithProcessing()
                2 -> image2.setImageURIWithProcessing()
                3 -> image3.setImageURIWithProcessing()
                4 -> image4.setImageURIWithProcessing()
                5 -> image5.setImageURIWithProcessing()
                6 -> image6.setImageURIWithProcessing()
            }
        }else{
            when(requestCode){
                1 -> image1.photoURI = null
                2 -> image2.photoURI = null
                3 -> image3.photoURI = null
                4 -> image4.photoURI = null
                5 -> image5.photoURI = null
                6 -> image6.photoURI = null
            }
        }
    }

    private fun savePhoto() {
        if(image1.photoURI==null && image2.photoURI==null && image3.photoURI==null && image4.photoURI==null && image5.photoURI==null && image6.photoURI==null){
            Different.showAlertInfo(requireActivity(),null, "Добавьте хоть одну фотографию!")
            return
        }

        var listUriString= mutableListOf<String>()
        if(image1.photoURI!=null)
            listUriString.add(image1.photoURI.toString())
        if(image2.photoURI!=null)
            listUriString.add(image2.photoURI.toString())
        if(image3.photoURI!=null)
            listUriString.add(image3.photoURI.toString())
        if(image4.photoURI!=null)
            listUriString.add(image4.photoURI.toString())
        if(image5.photoURI!=null)
            listUriString.add(image5.photoURI.toString())
        if(image6.photoURI!=null)
            listUriString.add(image6.photoURI.toString())


        presenter?.sendImageToServer(listUriString)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.onDestroyView = true
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