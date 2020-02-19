In this Library, user can pick image from gallery and click image from camera 

-set image  into imageview

-get file to upload to server

To get a Git project into your build:
Add it in your root build.gradle at the end of repositories:

    	allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}

 Add the dependency
 
        dependencies {
        	        implementation 'com.github.777665Abhi:ImageHandler:Tag'
        	}



Usage


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
           
We also need to override the onRequestPermissionsResult() to handle the permission automatically.     
           
           override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray ) {
        ImageHandler(this).permissionResponse(requestCode, permissions, grantResults)
    }

Get image file to upload to server in onActivityResult method

          if (resultCode == Activity.RESULT_OK) {
                             // Get a file to upload to server
                             var file = ImageHandler(this).returnFile(requestCode, resultCode, imageIntent)
                         }
                         
You need to declare the File Provider in androidMainfest.xml as below


              <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="com.example.android.fileprovider.as"
            android:exported="false"
            android:grantUriPermissions="true"
            tools:replace="android:authorities">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"
                tools:replace="android:resource" />
        </provider>
        
  here the android:authorities="com.example.android.fileprovider.as"  must be same, as it is used in dependency.
  
  We also need to declare the  android:resource="@xml/file_paths" in project in xml folder under Res folder
  file_paths.xml
         
            <?xml version="1.0" encoding="utf-8"?>
          <paths xmlns:android="http://schemas.android.com/apk/res/android">
          <external-path name="my_images" path="." />
          </paths>
  
        

