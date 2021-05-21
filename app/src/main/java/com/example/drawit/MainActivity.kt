package com.example.drawit

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.core.view.get
import com.example.drawit.databinding.ActivityMainBinding
import com.example.drawit.databinding.BrushSizeBinding
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var binding2: BrushSizeBinding
    private var mImageButtonCurrentPaint:ImageButton?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        mImageButtonCurrentPaint=binding.pallet[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.palletpressed)
        )
        binding.DrawingView.setBrushSize(20.toFloat())
        binding.ibrush.setOnClickListener {
            ChooseSize()
        }
        binding.gallery.setOnClickListener {
            if(isReadStorage())
            {
                val pickPhotointent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhotointent,GALLERY)
            }
            else
            {
                onStoragePermission()
            }

        }
        binding.back.setOnClickListener {
            binding.DrawingView.onClickUndo()
        }
        binding.redo.setOnClickListener {
            binding.DrawingView.redo()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK)
        {
            if(requestCode== GALLERY)
            {
                try {
                        if(data!!.data!=null)
                        {
                            binding.backImage.visibility=View.VISIBLE
                            binding.backImage.setImageURI(data.data)
                        }
                    else
                        {
                            Toast.makeText(this,"Some error",Toast.LENGTH_LONG).show()
                        }
                }
                catch (e:Exception)
                {
                        e.printStackTrace()
                }
            }
        }
    }
    private fun ChooseSize()
    {
        binding2= BrushSizeBinding.inflate(layoutInflater)
        val brushDialog=Dialog(this)
        brushDialog.setContentView(R.layout.brush_size)
        brushDialog.setTitle("Brush Size: ")
        val smallbtn:ImageButton=brushDialog.findViewById(R.id.small)
        val mediumbtn:ImageButton=brushDialog.findViewById(R.id.medium)
        val largebtn:ImageButton=brushDialog.findViewById(R.id.large)
        smallbtn.setOnClickListener {
            binding.DrawingView.setBrushSize(5.toFloat())

            brushDialog.dismiss()


        }
        mediumbtn.setOnClickListener {
            binding.DrawingView.setBrushSize(10.toFloat())
            brushDialog.dismiss()
        }
        largebtn.setOnClickListener {
            binding.DrawingView.setBrushSize(20.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()



    }
    fun paintClicked(view: View)
    {
        if(view != mImageButtonCurrentPaint)
        {
            val imageButton= view as ImageButton
            val colorTag=imageButton.tag.toString()
            binding.DrawingView.setColor(colorTag)
            imageButton.setImageDrawable( ContextCompat.getDrawable(this,R.drawable.palletpressed))

            mImageButtonCurrentPaint!!.setImageDrawable( ContextCompat.getDrawable(this,R.drawable.palletitem))
            mImageButtonCurrentPaint=view
        }
    }
    private fun onStoragePermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toString()))
        {
            Toast.makeText(this,"Need Permission to add background image",Toast.LENGTH_LONG).show()

        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        
    }

    companion object
    {
        private const val STORAGE_PERMISSION_CODE=1
        private const val GALLERY=2

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== STORAGE_PERMISSION_CODE)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Permission Given",Toast.LENGTH_LONG).show()

            }
            else
            {
                Toast.makeText(this,"Permission Rejected",Toast.LENGTH_LONG).show()

            }
        }
    }
    private fun isReadStorage():Boolean
    {
        val result = ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return result==PackageManager.PERMISSION_GRANTED
    }

}