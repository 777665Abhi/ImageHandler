package com.abhisheksingh.camerhandle

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

open class ImageHandler(private val mContext: Context) {
    var currentPhotoPath: String? = null
    /*===============PERMISSION*/
    /**This method check Camera permission*/
    private fun isCameraPermissionAvailable(): Boolean {
        val result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /**This method request Camera permission*/
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(mContext as Activity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
    }

    /**Check if storage permission available */
    fun isWritePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /**Request write permission */
    fun requestWritePermission() {
        ActivityCompat.requestPermissions(
            mContext as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE
        )
    }

    /**===============BASIC FUNCTION*/

    /**This method show dialog  to take photo*/
    /**Select image from gallery */
    open fun showImagePickerDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Select Option")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    dialog.dismiss()
                    setImageByCamera()
                }
                options[item] == "Choose From Gallery" -> {
                    dialog.dismiss()
                    setImageByGallery()
                }
                options[item] == "Cancel" -> dialog.dismiss()
            }
        })
        builder.show()

    }

    /**This method request Camera to take photo*/
    open fun setImageByCamera() {
        when {
            !isCameraPermissionAvailable() -> requestCameraPermission()
            !isWritePermission() -> requestWritePermission()
            else -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(mContext.packageManager)?.also {
                        // Create the File where the photo should go
                        fileToUpload = try {
                            createImageFile()
                        } catch (ex: IOException) {

                            null
                        }
                        // Continue only if the File was successfully created
                        fileToUpload?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                mContext,
                                "com.example.android.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(mContext as Activity, takePictureIntent, REQUEST_CAMERA, null)
                        }
                    }
                }
            }
        }
    }

    /**This method request Gallery to take photo*/
    open fun setImageByGallery() {
        when {
            !isCameraPermissionAvailable() -> requestCameraPermission()
            !isWritePermission() -> requestWritePermission()
            else -> {
                val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(mContext as Activity, pickPhoto, REQUEST_GALLERY, null)
            }
        }
    }

    /**This method will set image to your image view  */
    fun setImageInView(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?, imageView: ImageView) {
        when (requestCode) {
            REQUEST_CAMERA -> if (resultCode == Activity.RESULT_OK) {
                val myBitmap: Bitmap = BitmapFactory.decodeFile(location)
                imageView!!.setImageBitmap(myBitmap)

            }
            REQUEST_GALLERY -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.data
                imageView!!.setImageURI(selectedImage)

            }
        }
    }


    /*IMAGE BY CAMERA
    * Crate a file to save image capture*/
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            location = absolutePath

        }
    }

    companion object {
        const val REQUEST_CAMERA: Int = 0
        const val REQUEST_GALLERY: Int = 1
        const val REQUEST_WRITE = 2
        var location: String? = null
        var returnBitmap: Bitmap? = null
        var fileToUpload: File? = null
    }

    /**========================File to upload*/

    fun returnFile(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?): File {
        when (requestCode) {
            REQUEST_CAMERA -> if (resultCode == Activity.RESULT_OK) {
                fileToUpload
            }
            REQUEST_GALLERY -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent!!.data
                returnBitmap = uriToBitmap(selectedImage)
                fileToUpload = bitmapToFile(returnBitmap!!)
            }
        }
        return fileToUpload!!
    }


    /**Get Path for Image URI*/
    fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = mContext!!.contentResolver.query(uri, projection, null, null, null) ?: return null
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(column_index)
        cursor.close()
        return s
    }

    /** Get file for Image Bitmap */
    fun bitmapToFile(bitmap: Bitmap): File {
        return File(getPath(getUriFromBitmap(bitmap)))
    }

    /** Image URI to Bitmap */
    fun uriToBitmap(uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(mContext.contentResolver, uri)
    }

    /**Get Uri for imageBitMap*/
    fun getUriFromBitmap(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(mContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

}