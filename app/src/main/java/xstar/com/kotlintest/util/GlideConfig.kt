package xstar.com.kotlintest.util

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 *
 */
@GlideModule
class GlideConfig: AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        //不要硬
        builder.setDefaultRequestOptions(RequestOptions.centerCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE))
    }
}