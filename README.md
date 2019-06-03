In this Library, user can pick image from gallery and click image from camera ,
-set image  into imageview
-get file to upload to server

Usage

To check if permission of camera is enabled
    ImageHandler(this).isCameraPermissionAvailable()

Request Permission for camera
    ImageHandler(this).requestCameraPermission()

To check if permission of Write  is enabled
    ImageHandler(this).isWritePermission()

Request Permission for Write
    ImageHandler(this).requestWritePermission()

To show image popup and select from either camera or gallery
    ImageHandler(this).showImagePickerDialog()

To set image from camera
     ImageHandler(this).setImageByCamera()

To set image from gallery
       ImageHandler(this).setImageByGallery()

To set Image selected into imageview in onActivityResult method
          override fun onActivityResult(requestCode: Int, resultCode: Int, imageIntent: Intent?) {
               super.onActivityResult(requestCode, resultCode, imageIntent)
               // setting the image in image View
               ImageHandler(this).setImageInView(requestCode, resultCode, imageIntent, ivImage)

               if (resultCode == Activity.RESULT_OK) {
                   // Get a file to upload to server
                   var file = ImageHandler(this).returnFile(requestCode, resultCode, imageIntent)
               }
           }

Get image file to upload to server in onActivityResult method
          if (resultCode == Activity.RESULT_OK) {
                             // Get a file to upload to server
                             var file = ImageHandler(this).returnFile(requestCode, resultCode, imageIntent)
                         }

