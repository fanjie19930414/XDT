package com.kapok.apps.maple.xdt.homework.presenter

import android.content.Context
import com.kapok.apps.maple.xdt.homework.bean.CommonComment
import com.kapok.apps.maple.xdt.homework.model.model_instance.HomeWorkModelInstance
import com.kapok.apps.maple.xdt.homework.presenter.view.SettingCommentView
import com.kotlin.baselibrary.ex.execute
import com.kotlin.baselibrary.presenter.BasePresenter
import com.kotlin.baselibrary.rx.BaseObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class SettingCommentPresenter(context: Context): BasePresenter<SettingCommentView>(context) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun unSubscribe() {
        disposable.dispose()
    }

    // 获取常用评语
    fun getCommonComment(teacherId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val getCommonCommentModel = HomeWorkModelInstance()
            getCommonCommentModel.getCommonComment(teacherId)
                .execute(object : BaseObserver<MutableList<CommonComment>?>(mView) {
                    override fun onNext(t: MutableList<CommonComment>?) {
                        super.onNext(t)
                        mView.getCommonComment(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }

    // 创建评语
    fun createCommonComment(content: String,teacherId: Int) {
        if (checkNetWork()) {
            mView.onShowDialog()
            val createCommentModel = HomeWorkModelInstance()
            createCommentModel.createComment(content, teacherId)
                .execute(object : BaseObserver<String>(mView) {
                    override fun onNext(t: String) {
                        super.onNext(t)
                        mView.createComment(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        disposable.add(d)
                    }
                })
        }
    }
}