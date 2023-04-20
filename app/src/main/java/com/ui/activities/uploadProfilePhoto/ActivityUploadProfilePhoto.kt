package com.ui.activities.uploadProfilePhoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.domain.BaseModels.BodyCandidateImageModel
import com.veriKlick.R
import com.veriKlick.databinding.ActivityUploadProfilePhotoBinding
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ActivityUploadProfilePhoto : AppCompatActivity() {
    private lateinit var binding: ActivityUploadProfilePhotoBinding
    private var imageUri:Uri?=null
    private var imageFile:File?=null
    private var finalUserImageUri:Uri?=null
    private var viewModel:VMUploadProfilePhoto?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadProfilePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        requestWriteExternamlStoragePermissions {

        }

        viewModel=ViewModelProvider(this).get(VMUploadProfilePhoto::class.java)

        binding.ivUploadImage.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            imageFile = getEmptyFile("Caputured"+System.currentTimeMillis()+".png","captureImage")
            imageUri = FileProvider.getUriForFile(this,"com.veriKlick.provider",imageFile!!)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            contractorCamera.launch(intent)
        }

        binding.btnUploadImage.setOnClickListener {
            uploadImage()
        }


    }
    val TAG="tavactivityupload"

    val contractorCamera=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
       try {

           Log.d(TAG, "imageUri: uri $imageUri")
           //val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyPhoto.jpg")
           // binding.ivUploadImage.setImageURI(imageUri)
          // Log.d(TAG, ": contractor file $file ${this.applicationContext.packageName + ".provider"}")
//            var fileTemp=FileProvider.getUriForFile(this,"com.veriKlick.provider",getEmptyFile("Caputured"+System.currentTimeMillis()+".png"))
          /* val uri = FileProvider.getUriForFile(
               this,
               this.applicationContext.packageName + ".provider",
               file
           )*/

               getImageFromCamera(imageUri!!)

       }catch (e:Exception)
       {
           Log.d(TAG, "error: ${e.printStackTrace()}")
       }

    }
    var desUri=Uri.parse("")

    fun getImageFromCamera(sourceUri: Uri)
    {

        try {
            desUri =getTempFileNameInCache()

            Log.d(TAG, "getImageFromCamera: source uri $sourceUri desUri $desUri")
           // binding.ivUploadImage.setImageURI(sourceUri)
            var option=UCrop.Options()
            option.setCircleDimmedLayer(true)
            option.setCropGridColumnCount(3)
            option.setCropGridRowCount(3)
                option.withAspectRatio("100".toString().toFloat(),"100".toString().toFloat())

            UCrop.of(sourceUri,desUri!!)
                .withOptions(option)
                .start(this)

        }catch (e:Exception)
        {
            showCustomToast(getString(R.string.txt_something_went_wrong))
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            binding.ivUploadImage.setImageURI(resultUri)
            finalUserImageUri=resultUri
            Log.d(TAG, "onActivityResult: destUri $desUri")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.d(TAG, "onActivityResult: error ")
        }
    }

    private fun uploadImage()
    {
        if (finalUserImageUri!=null)
        {
            uploadProfilePhoto()
        }else
        {
            showCustomSnackbarOnTop(getString(R.string.txt_please_select_photo_first))
        }

    }


    private fun uploadProfilePhoto()
    {
        CoroutineScope(Dispatchers.IO+ exceptionHandler).launch {
            try {
                var imageBitmap= uriToBitmap(finalUserImageUri!!)
                if (imageBitmap!=null)
                {
                    val image= convertBitmapToBase64(imageBitmap)
                   // binding.ivUploadImage.setImageBitmap(imageBitmap)
                    /*viewModel?.updateUserImage(BodyCandidateImageModel()){isSuccess, errorCode, msg ->

                    } */
                }else
                {
                    showCustomSnackbarOnTop(getString(R.string.txt_something_went_wrong))
                }

            }catch (e:Exception)
            {
                Log.d(TAG, "uploadProfilePhoto: ")
            }
        }

    }

}

