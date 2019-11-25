package com.AbhishekSingh.myapplication

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.abhisheksingh.camerhandle.ImageHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonClick()
    }

    /*Handle the Click Listener*/
    private fun buttonClick() {
        btPhoto.setOnClickListener {
            ImageHandler(this).showImagePickerDialog()
        }
        btCamera.setOnClickListener {
            ImageHandler(this).setImageByCamera()
        }
        btGallery.setOnClickListener {
            ImageHandler(this).setImageByGallery()
        }
    }

   /*Here we get the Image and file to upload*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageIntent)
        // setting the image in image View
        ImageHandler(this).setImageInView(requestCode, resultCode, imageIntent, ivImage)

        if (resultCode == Activity.RESULT_OK) {
            // Get a file to upload to server
            var file = ImageHandler(this).returnFile(requestCode, resultCode, imageIntent)
        }
    }

    /*To handle the result of requested permissions */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        ImageHandler(this).permissionResponse(requestCode, permissions, grantResults)
    }

}
