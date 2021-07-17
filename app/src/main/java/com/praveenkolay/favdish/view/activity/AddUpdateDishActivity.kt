package com.praveenkolay.favdish.view.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.praveenkolay.favdish.R
import com.praveenkolay.favdish.application.FavDishApplication
import com.praveenkolay.favdish.databinding.ActivityAddUpdateDishBinding
import com.praveenkolay.favdish.databinding.CustomDialogImageSelectionBinding
import com.praveenkolay.favdish.databinding.DialogCustomListBinding
import com.praveenkolay.favdish.model.entity.FavDish
import com.praveenkolay.favdish.utils.Constants
import com.praveenkolay.favdish.view.adapter.CustomListItemAdapter
import com.praveenkolay.favdish.viewModel.FavDishViewModel
import com.praveenkolay.favdish.viewModel.FavDishViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath: String = ""
    private lateinit var mCustomListDialog: Dialog

    private var mFavDishDetails: FavDish? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels{
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    companion object{
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "favDishImagesFolder"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if(intent.hasExtra(Constants.EXTRA_DISH_DETAILS)){
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }

        setUpActionBar()

        mFavDishDetails?.let {
            if (it.id != 0){
                mImagePath = it.image
                Glide.with(this@AddUpdateDishActivity)
                        .load(mImagePath)
                        .centerCrop()
                        .into(mBinding.ivDishImage)

                mBinding.etDishTitle.setText(it.title)
                mBinding.etDishType.setText(it.type)
                mBinding.etDishCategory.setText(it.category)
                mBinding.etDishIngredient.setText(it.ingredients)
                mBinding.etDishCookingTime.setText(it.cookingTime)
                mBinding.etDishCookingDirection.setText(it.cookingDirection)

                mBinding.btnAddDish.text = resources.getString(R.string.lbl_update_dish)
            }
        }

        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etDishType.setOnClickListener(this)
        mBinding.etDishCategory.setOnClickListener(this)
        mBinding.etDishCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)
    }

    // function for toolbar/actionbar with a back button
    private fun setUpActionBar(){
        setSupportActionBar(mBinding.toolbar)
        if (mFavDishDetails != null && mFavDishDetails!!.id != 0){
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_edit_dish)
            }
        }else{
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_add_dish)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){
                R.id.iv_add_dish_image ->{
                    customImageSelectionDialog()
                    return
                }
                R.id.et_dish_type ->{
                    itemListCustomDialog(resources.getString(R.string.et_select_dish_type),Constants.dishType(), Constants.DISH_TYPE)
                    return
                }
                R.id.et_dish_category ->{
                    itemListCustomDialog(resources.getString(R.string.et_select_dish_category),Constants.dishCategory(), Constants.DISH_CATEGORY)
                    return
                }
                R.id.et_dish_cooking_time ->{
                    itemListCustomDialog(resources.getString(R.string.et_select_dish_cookingTime),Constants.dishCookingTIme(), Constants.DISH_COOKING_TIME)
                    return
                }
                R.id.btn_add_dish ->{
                    val title = mBinding.etDishTitle.text.toString().trim { it <= ' '}
                    val type = mBinding.etDishType.text.toString().trim { it <= ' '}
                    val category = mBinding.etDishCategory.text.toString().trim { it <= ' '}
                    val ingredient = mBinding.etDishIngredient.text.toString().trim { it <= ' '}
                    val cookingTime = mBinding.etDishCookingTime.text.toString().trim { it <= ' '}
                    val cookingDirection = mBinding.etDishCookingDirection.text.toString().trim { it <= ' '}

                    when{
                        TextUtils.isEmpty(mImagePath) -> {
                            Toast.makeText(
                                    this@AddUpdateDishActivity,
                                     resources.getString(R.string.err_no_image_selected),
                                     Toast.LENGTH_SHORT
                            ).show()
                        }
                        TextUtils.isEmpty(title) -> {
                            Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    resources.getString(R.string.err_no_title),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(type) -> {
                            Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    resources.getString(R.string.err_no_type),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(category) -> {
                            Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    resources.getString(R.string.err_no_category),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(ingredient) -> {
                            Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    resources.getString(R.string.err_no_ingredient),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(cookingTime) -> {
                            Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    resources.getString(R.string.err_no_cooking_time),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(cookingDirection) -> {
                            Toast.makeText(
                                    this@AddUpdateDishActivity,
                                    resources.getString(R.string.err_no_cooking_direction),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            var dishID = 0
                            var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var favoriteDish = false

                            mFavDishDetails?.let {
                                if(it.id != 0)
                                    dishID = it.id
                                imageSource = it.imageSource
                                favoriteDish = it.favoriteDish
                            }

                            val favDishDetails = FavDish(
                                mImagePath,
                                imageSource,
                                title,
                                type,
                                category,
                                ingredient,
                                cookingTime,
                                cookingDirection,
                                    favoriteDish,
                                    dishID
                            )

                            if(dishID == 0){
                                mFavDishViewModel.insert(favDishDetails)
                                Toast.makeText(
                                        this@AddUpdateDishActivity,
                                        "Successfully added a new Dish details.",
                                        Toast.LENGTH_SHORT
                                ).show()
                                Log.d("Insertion", "Success")
                            }else{
                                mFavDishViewModel.update(favDishDetails)
                                Toast.makeText(
                                        this@AddUpdateDishActivity,
                                        "You have successfully updated your favorite dish details.",
                                        Toast.LENGTH_SHORT
                                ).show()
                                Log.d("Updating", "Success")
                            }

                            finish()
                        }
                    }
                }
            }
        }
    }
    // function to show a dialog
    private fun customImageSelectionDialog(){
        val dialog = Dialog(this)
        val binding: CustomDialogImageSelectionBinding =
                CustomDialogImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCustomDialogCamera.setOnClickListener {

            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, CAMERA)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    alertDialogForRationalPermission()
                }

            }
            ).onSameThread().check()


            dialog.dismiss()
        }

        binding.tvCustomDialogGallery.setOnClickListener {
            Dexter.withContext(this@AddUpdateDishActivity).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object: PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                       val galleryIntent = Intent(Intent.ACTION_PICK,
                               MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                            this@AddUpdateDishActivity,
                            "You have denied the storage permission to select image",
                             Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                    alertDialogForRationalPermission()
                }


            }
            ).onSameThread().check()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA){
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                   // mBinding.ivDishImage.setImageBitmap(thumbnail)
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivDishImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)
                    Log.d("Image Path", mImagePath)

                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
                }
            }
            if (requestCode == GALLERY){
                data?.let {
                    val selectedImageUri = data.data
                    Glide.with(this)
                        .load(selectedImageUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable>{
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                    Log.e("FAILED", "Error in loading image")
                                    return false
                                }

                                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    resource?.let {
                                        val bitmap: Bitmap = resource.toBitmap()
                                        mImagePath = saveImageToInternalStorage(bitmap)
                                        Log.d("ImagePath", mImagePath)
                                    }
                                    return false
                                }

                            }
                            )
                        .into(mBinding.ivDishImage)
                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            Log.d("Cancelled", "User have cancelled image selection")
        }
    }

    private fun alertDialogForRationalPermission(){
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permission required for this feature." +
                " It can be enabled in the Application setting")
            .setPositiveButton("GO TO SETTINGS")
            {_,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    Toast.makeText(this, "Application not found in settings", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel"){dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val steam : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, steam)
            steam.flush()
            steam.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun itemListCustomDialog(title: String, itemList: List<String>, selection: String){
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.customListTitle.text = title

        binding.rvCustomList.layoutManager = LinearLayoutManager(this)

        val adapter = CustomListItemAdapter(this, null, itemList, selection)
        binding.rvCustomList.adapter = adapter
        mCustomListDialog.show()

    }

    fun selectedListItem(item: String, selection: String){
        when(selection){
            Constants.DISH_TYPE -> {
                mCustomListDialog.dismiss()
                mBinding.etDishType.setText(item)
            }

            Constants.DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                mBinding.etDishCategory.setText(item)
            }

            Constants.DISH_COOKING_TIME -> {
                mCustomListDialog.dismiss()
                mBinding.etDishCookingTime.setText(item)
            }
        }
    }
}