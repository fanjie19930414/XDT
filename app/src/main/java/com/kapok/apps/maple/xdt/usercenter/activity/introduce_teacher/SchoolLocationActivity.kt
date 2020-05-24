package com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.baidu.location.BDLocation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.adapter.SchoolLocationAdapter
import com.kapok.apps.maple.xdt.usercenter.bean.NearBySchoolBean
import com.kapok.apps.maple.xdt.usercenter.presenter.SchoolLocationPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.SchoolLocationView
import com.kapok.apps.maple.xdt.utils.LocationServer
import com.kapok.apps.maple.xdt.utils.PermissionUtils
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.custom.CustomEmptyView
import com.kotlin.baselibrary.custom.ProgressLoading
import com.kotlin.baselibrary.custom.RecycleViewDivider
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.Dp2pxUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_school_location.*

/**
 * 搜索学校页
 * fanjie
 */
class SchoolLocationActivity : BaseMVPActivity<SchoolLocationPresenter>(), SchoolLocationView {
    private lateinit var mProgressLoading: ProgressLoading
    // 附近学校Adapter
    private lateinit var mNearBySchoolAdapter: SchoolLocationAdapter
    // 附近学校列表/热门学校列表
    private lateinit var mNearBySchoolList: MutableList<NearBySchoolBean>
    // 定位城市
    private lateinit var city: String
    // 城市RequestCode
    private val cityRequestCode = 101
    // 学校RequestCode
    private val schoolRequestCode = 102
    // 选中的学校
    private var selectSchoolName: String = ""
    private var selectSchoolId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_location)
        //定位
        getLocation(false)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = SchoolLocationPresenter(this@SchoolLocationActivity)
        mPresenter.mView = this
        mProgressLoading = ProgressLoading.create(this)
        // 获取是否有选中的学校
        val intent = intent
        selectSchoolName = intent.getStringExtra("selectSchoolName")
        selectSchoolId = intent.getIntExtra("selectSchoolId", -1)
        // 初始化rv
        mNearBySchoolList = arrayListOf()
        mNearBySchoolAdapter = SchoolLocationAdapter(this@SchoolLocationActivity, mNearBySchoolList)
        mNearBySchoolAdapter.emptyView = CustomEmptyView.builder(this).setImgRes(R.drawable.icon_state_error)
        rv_schoollocation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_schoollocation.adapter = mNearBySchoolAdapter
        rv_schoollocation.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
    }

    private fun initListener() {
        // 取消
        tv_schoollocation_cancel.setOnClickListener { finish() }
        // 搜索城市列表
        tv_school_location_city.setOnClickListener {
            val intent = Intent()
            intent.putExtra("cityName", tv_school_location_city.text.toString())
            intent.setClass(this, SearchCityListActivity::class.java)
            startActivityForResult(intent, cityRequestCode)
        }
        // 学校搜索列表页面
        tvSearchSchoolLocation.setOnClickListener {
            val intent = Intent()
            intent.putExtra("cityName", tv_school_location_city.text.toString())
            intent.setClass(this, SearchSchoolListActivity::class.java)
            startActivityForResult(intent, schoolRequestCode)
        }
        // 点击学校 返回给Teacher编辑信息页
        mNearBySchoolAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                selectSchoolName = mNearBySchoolList[position].schoolName
                selectSchoolId = mNearBySchoolList[position].schoolId
                val intent = Intent()
                intent.putExtra("selectSchool", mNearBySchoolList[position].schoolName)
                intent.putExtra("selectSchoolId", mNearBySchoolList[position].schoolId)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        // 重新获取定位
        tv_re_schoollocation.setOnClickListener {
            mProgressLoading.showLoading()
            getLocation(true)
        }
    }

    private fun getLocation(showProgress: Boolean) {
        LocationServer.getGps(this, object : LocationServer.LocationResultListener {
            override fun locationFailure(msg: String) {
                if (showProgress) {
                    mProgressLoading.dismiss()
                }
                ToastUtils.showMsg(this@SchoolLocationActivity, msg)
            }

            override fun locationSuccess(location: BDLocation) {
                if (showProgress) {
                    mProgressLoading.dismiss()
                }
                nearbyOrHot(true)
                // 返回坐标信息配置
                val latitude = location.latitude
                val longitude = location.longitude
                city = location.city
                // 调用附近学校接口
                mPresenter.getSchoolNearBy(city, latitude.toString(), longitude.toString(), "")
                // 配置
                tv_schoollocation.text = location.district + location.street + location.streetNumber
                tv_school_location_city.text = location.city
            }
        })
    }

    // 附近学校 热门学校  true 附近  false 热门
    private fun nearbyOrHot(nearby: Boolean) {
        if (nearby) {
            tvCurrentLocation.setVisible(true)
            llCurrentLocation.setVisible(true)
            tvNearBySchool.setVisible(true)
            tvHotSchool.setVisible(false)
        } else {
            tvCurrentLocation.setVisible(false)
            llCurrentLocation.setVisible(false)
            tvNearBySchool.setVisible(false)
            tvHotSchool.setVisible(true)
        }
    }

    // 附近学校列表
    override fun getNearBySchoolList(dataList: MutableList<NearBySchoolBean>?) {
        if (dataList != null) {
            if (dataList.isNotEmpty()) {
                mNearBySchoolList.clear()
                mNearBySchoolList.addAll(dataList)
                mNearBySchoolAdapter.notifyDataSetChanged()
            }
        }
    }

    // 热门学校列表
    override fun getHotSchoolList(dataList: MutableList<NearBySchoolBean>?) {
        if (dataList != null) {
            if (dataList.isNotEmpty()) {
                mNearBySchoolList.clear()
                mNearBySchoolList.addAll(dataList)
                mNearBySchoolAdapter.notifyDataSetChanged()
            } else {
                mNearBySchoolList.clear()
                mNearBySchoolAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            cityRequestCode -> {
                val selectCityName = data?.getStringExtra("selectCityName")
                val selectCityId = data?.getIntExtra("selectCityId", -1)
                tv_school_location_city.text = selectCityName
                // 如果选中的城市 非 定位城市 隐藏附近学校 展示热门学校
                if (city != selectCityName) {
                    nearbyOrHot(false)
                    mPresenter.getSchoolHot(tv_school_location_city.text.toString(), "")
                } else {
                    getLocation(true)
                }
            }
            schoolRequestCode -> {
                val selectSchool = data?.getStringExtra("selectSchool")
                val selectSchoolId = data?.getIntExtra("selectSchoolId", -1)
                val intent = Intent()
                intent.putExtra("selectSchool", selectSchool)
                intent.putExtra("selectSchoolId", selectSchoolId)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("selectSchool", selectSchoolName)
        intent.putExtra("selectSchoolId", selectSchoolId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    // 动态权限
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}