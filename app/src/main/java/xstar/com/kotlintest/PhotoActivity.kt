package xstar.com.kotlintest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo.*
import org.jetbrains.anko.imageBitmap
import top.xstar.photolibrary.HelloC
import xstar.com.kotlintest.constant.C
import xstar.com.kotlintest.data.PhotoTrans
import xstar.com.kotlintest.recycler.BaseAdapter
import xstar.com.kotlintest.recycler.BaseVH
import xstar.com.kotlintest.recycler.OnItemClickListener
import xstar.com.kotlintest.recycler.find
import xstar.com.kotlintest.util.*
import java.util.concurrent.TimeUnit


class PhotoActivity : BaseActivity(R.layout.activity_photo) {

    val compositeDisposable = CompositeDisposable()

    var photoBitmap: Bitmap? = null
    var imgAdaper: ImgAdapter? = null
    val IMAGES_REQUEST_CODE = 0x233
    val PERMISSION_INSTALL_REQUEST_CODE = 0x234
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            begPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, requestCode = PERMISSION_INSTALL_REQUEST_CODE)
        }
        images.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, null)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGES_REQUEST_CODE)
        }
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = 0
        } else if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        photoBitmap = BitmapFactory.decodeResource(resources, R.drawable.lam)
        main_photo.layoutParams.width = C.SCREEN_W
        main_photo.layoutParams.height = C.SCREEN_H
        main_photo.setBitmap(photoBitmap!!)
        photo_transforms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)//横向
        val tramsList = listOf(PhotoTrans("原图", 0), PhotoTrans("灰度", C.PHOTO_TRANS_GRAY)
                , PhotoTrans("素描", C.PHOTO_TRANS_SKETCH), PhotoTrans("铅笔画", C.PHOTO_TRANS_PENCIL)
                , PhotoTrans("字符图", C.PHOTO_TRANS_CHARS))
        imgAdaper = ImgAdapter()
        imgAdaper?.bitmap = photoBitmap
        imgAdaper?.datas = tramsList.toMutableList()
        imgAdaper?.onItemClickListner = itemClick
        photo_transforms.adapter = imgAdaper
        val dis = Flowable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            val colors = intArrayOf(Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GRAY, Color.RED, Color.GREEN).iterator()
            Log.e("main_photo", String.format("[%d,%d,%d,%d]", main_photo.left, main_photo.top, main_photo.right, main_photo.bottom))
            var p = main_photo?.parent
            val rect = Rect()
            while (p != null) {
                if (p is View) {
                    p.getDrawingRect(rect)
                }
                p = p.parent
            }
        }
        compositeDisposable.add(dis)
        Log.e("JNI", HelloC.hello())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGES_REQUEST_CODE -> {
                    data?.let {
                        photoBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it.data)
                        main_photo.setBitmap(photoBitmap!!)
                        imgAdaper?.bitmap = photoBitmap!!
                        imgAdaper?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    class ImgAdapter(imgInit: (ImgAdapter.() -> Unit) = {
        itemLayout = R.layout.item_photo_transform
        val imgW = C.SCREEN_W.div(3)
        bindData = { holder, position ->
            holder.find<ImageView>(R.id.img).layoutParams?.width = imgW
            holder.find<ImageView>(R.id.img).layoutParams?.height = imgW
            val trans = datas?.get(position)
            trans?.let {
                val mapDisp = Flowable.just(it.transCode).map {
                    bitmap?.let { b ->
                        synchronized(b) {
                            return@map when (it) {
                                C.PHOTO_TRANS_GRAY -> b.gray()
                                C.PHOTO_TRANS_SKETCH -> b.sketch(2)
                                C.PHOTO_TRANS_PENCIL -> b.pencil(30)
                                C.PHOTO_TRANS_CHARS -> getCharsPicture(b)
                                else ->
                                    b
                            }
                        }
                    }
                }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.computation())
                        .subscribe {
                            holder.find<ImageView>(R.id.img).imageBitmap = it
                        }
                val context = holder.itemView.context
                if (context is BaseActivity) {
                    context.addDispose(mapDisp)
                }
                holder.find<TextView>(R.id.name).text = it.transName
                holder.itemView.setOnClickListener { onItemClickListner?.onItemClick(holder, position, datas!![position]) }
            }
        }
    }) : BaseAdapter<PhotoTrans>({}) {
        var bitmap: Bitmap? = null

        init {
            imgInit()
        }
    }


    val itemClick = object : OnItemClickListener<PhotoTrans> {
        override fun onItemClick(holder: BaseVH, position: Int, item: PhotoTrans) {
            val img = holder.find<ImageView>(R.id.img).drawable
            if (img is BitmapDrawable) {
                main_photo.setBitmap(img.bitmap)
            }
        }
    }

    val colorMatrix = ColorMatrix(floatArrayOf(
            0.9F, 0f, 0.5f, 0f, 0f, //A
            0f, 0.5F, 0f, 0.6f, 0f, //R
            0f, 0f, 0.5F, 0.3f, 0f, //G
            0f, 0f, 0f, 1f, 0f     //B
    ))


}
