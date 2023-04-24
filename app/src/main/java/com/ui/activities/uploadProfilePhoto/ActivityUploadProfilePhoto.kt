package com.ui.activities.uploadProfilePhoto

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.data.*
import com.data.dataHolders.DataStoreHelper
import com.domain.BaseModels.BodyCandidateImageModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.veriKlick.R
import com.veriKlick.databinding.ActivityUploadProfilePhotoBinding
import com.veriKlick.databinding.LayoutChooseImageFromSourceBinding
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
    private var finalUserImageUri:Uri?=null
    private var viewModel:VMUploadProfilePhoto?=null
    private var imageName:String?=null
    private var imageFile:File?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadProfilePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnJumpBack.setOnClickListener {
            onBackPressed.handleOnBackPressed()
        }

        requestWriteExternamlStoragePermissions {

        }

        viewModel=ViewModelProvider(this).get(VMUploadProfilePhoto::class.java)

        binding.ivUploadImage.setOnClickListener {
            getImageBottomSheet()
        }

        binding.btnUploadImage.setOnClickListener {
            uploadImage()
        }


    }
    val TAG="tavactivityupload"

    val onBackPressed=object:OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            Log.d(TAG, "handleOnBackPressed: on back pressed")
            finish()
        }
    }

    private fun getImageFromCamera()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        imageFile = getEmptyFile("Caputured"+System.currentTimeMillis()+".png","captureImage")
        imageUri = FileProvider.getUriForFile(this,"com.veriKlick.provider",imageFile!!)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        contractorCamera.launch(intent)
    }

    val contractorCamera=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
       try {

           Log.d(TAG, "imageUri: uri $imageUri")
           getImageFromCamera(imageUri!!)

       }catch (e:Exception)
       {
           Log.d(TAG, "error: ${e.printStackTrace()}")
       }

    }

    val contractorGallery=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        try {

            Log.d(TAG, "imageUri: uri $imageUri")
            imageUri = it?.data?.data
           // binding.ivUploadImage.setImageURI(imageUri)

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

    fun getImageBottomSheet()
    {
        val dialog= BottomSheetDialog(this,R.style.AppBottomSheetDialogTheme)
        val dialogbinding= LayoutChooseImageFromSourceBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogbinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogbinding.ivGallery.setOnClickListener {
            getImageFromGallery()
            dialog.dismiss()
        }
        dialogbinding.ivCamera.setOnClickListener {
            getImageFromCamera()
            dialog.dismiss()
        }

        dialog.create()
        dialog.show()

    }


    fun getImageFromGallery()
    {
        val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        contractorGallery.launch(intent)
    }

    private fun uploadProfilePhoto()
    {
        CoroutineScope(Dispatchers.IO+ exceptionHandler).launch {
            try {
                var imageBitmap= uriToBitmap(finalUserImageUri!!)
                if (imageBitmap!=null)
                {
                    val image= convertBitmapToBase64(imageBitmap)
                    val subsId=DataStoreHelper.getMeetingUserId()
                   // binding.ivUploadImage.setImageBitmap(imageBitmap)
                    var imageName=subsId+"/IMG_${System.currentTimeMillis()}.png"
                    Log.d(TAG, "uploadProfilePhoto: image name $imageName")
                    viewModel?.updateUserImage(BodyCandidateImageModel(image,subsId+"/IMG_${System.currentTimeMillis()}.png","upload")){isSuccess, code, msg ->
                    when(code)
                    {
                        200->{
                            Log.d(TAG, "uploadProfilePhoto: $msg")
                        }
                        400->{
                            Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                        }
                        401->{
                            Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                        }
                        500->{
                            Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                        }
                        501->{
                            Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                        }
                        404->{
                            Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                        }
                        503->{
                            Log.d(TAG, "uploadProfilePhoto: $msg $isSuccess $code")
                        }

                    }
                    }
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

