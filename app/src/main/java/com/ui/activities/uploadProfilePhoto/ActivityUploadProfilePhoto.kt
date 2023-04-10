package com.ui.activities.uploadProfilePhoto

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.data.getEmptyFile
import com.data.requestStoragePermissions
import com.data.setHandler
import com.data.showCustomToast
import com.veriKlick.databinding.ActivityUploadProfilePhotoBinding
import com.yalantis.ucrop.UCrop
import java.io.File


class UploadProfilePhoto : AppCompatActivity() {
    private lateinit var binding: ActivityUploadProfilePhotoBinding
    private var imageUri:Uri?=null
    private var imageFile:File?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadProfilePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        requestStoragePermissions {

        }

        binding.ivUploadImage.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            imageFile = getEmptyFile("Caputured"+System.currentTimeMillis()+".png","captureImage")
            imageUri = FileProvider.getUriForFile(this,"com.veriKlick.provider",imageFile!!)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            contractorCamera.launch(intent)
        }

    }
    val TAG="tavactivityupload"

    val contractorCamera=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
       try {
         /*  val image=it.data?.extras?.get("data") as Bitmap
           var imageUri=it.data
           binding.ivUploadImage.scaleType=ImageView.ScaleType.CENTER_CROP
           binding.ivUploadImage.setImageBitmap(image)*/
          // val imageUri =it.data?.extras?.get("data") as Uri

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
        val projDir=File(filesDir,"cropped")
        if (!projDir.exists())
            projDir.mkdirs();

//       val file = File(projDir, "Caputured"+System.currentTimeMillis()+".png")
//        file.createNewFile()
   val file =     File.createTempFile("cropped", ".png",cacheDir)
//        desUri=FileProvider.getUriForFile(this,"com.veriKlick.provider",file)
         desUri = Uri.fromFile(file)

        //var fileDes= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()

        Log.d(TAG, "getImageFromCamera: source uri $sourceUri desUri $desUri")
        binding.ivUploadImage.setImageURI(sourceUri)
        UCrop.of(sourceUri,desUri!!)
//            .withAspectRatio("100".toFloat(), "100".toFloat())
//            .withMaxResultSize(500,500)
            .start(this)

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            binding.ivUploadImage.setImageURI(resultUri)
            Log.d(TAG, "onActivityResult: destUri $desUri")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.d(TAG, "onActivityResult: error ")
        }
    }


}

