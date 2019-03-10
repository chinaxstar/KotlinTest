package xstar.com.kotlintest.recycler

import android.os.Looper
import android.view.View
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author: xstar
 * @since: 2018-05-10.
 */
inline fun <reified R> Flowable<R>.composeUIThread(): Flowable<R> {
    return compose {
        it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

inline fun <reified R> Flowable<R>.composeUIFromNewThread(): Flowable<R> {
    return compose {
        it.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

object RxView {
    /**
     * 创建一个对于view的观察事件对象
     */
    fun onClick(view: View?): Flowable<View> {
        view?.let {

            return Flowable.create(ViewClickOnSubscribe(view), BackpressureStrategy.MISSING)
        }
        return Flowable.empty()
    }
}

class ViewClickOnSubscribe(view: View) : FlowableOnSubscribe<View> {
    val content = view
    override fun subscribe(emitter: FlowableEmitter<View>) {
        checkUIThread()

        val onClick = View.OnClickListener {
            if (!emitter.isCancelled) {
                emitter.onNext(it)
            }
        }

        content.setOnClickListener(onClick)
    }

    /**
     * 日否UI线程 不是抛出异常
     */
    private fun checkUIThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) throw IllegalStateException("not UI Thread exception")
    }

}

object RxViewHelp {
    /**
     *  duration 间隔内点击事件只触发一次
     */
    fun doOnClick(view: View?, duration: Long = 1000, action: (View) -> Unit = {}): Disposable {
        return RxView.onClick(view).throttleFirst(duration, TimeUnit.MILLISECONDS).subscribe { action(view!!) }
    }
}