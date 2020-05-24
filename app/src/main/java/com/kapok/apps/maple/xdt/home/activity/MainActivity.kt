package com.kapok.apps.maple.xdt.home.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.classlist.bean.ParentClassListBean
import com.kapok.apps.maple.xdt.home.fragment.*
import com.kapok.apps.maple.xdt.utils.WsManager
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.commen.BaseUserInfo
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventChangeUserIdentity
import com.kotlin.baselibrary.rx.event.EventChildrenUserInfoMsg
import com.kotlin.baselibrary.rx.event.EventClassListBean
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 首页
 */
class MainActivity : BaseActivity() {
    // 身份切换事件
    private lateinit var disposableIdentity: Disposable
    // 班级列表跳转班级详情事情
    private lateinit var disposableClass: Disposable
    // fragment 集合
    private lateinit var fragmentList: ArrayList<Fragment>
    // 班级列表Fragment
    private lateinit var classFragment: ClassFragment
    // 我的Fragment
    private lateinit var myFragment: MyFragment
    // 1是老师 2是家长
    private var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 连接WebSocket
        WsManager.getInstance().init()
        initView()
    }

    private fun initView() {
        val intent = intent
        when {
            BaseUserInfo.identity == -1 -> id = intent.getIntExtra("id", id)
            BaseUserInfo.identity == 1 -> id = 2
            BaseUserInfo.identity == 2 -> id = 1
        }
        // fragment 集合
        // 这里需要判断 是老师还是家长 Fragment (测试用老师)
        // 初始化底部导航栏
        // 初始化ViewPager
        // fragment 集合
        fragmentList = arrayListOf()
        // 班级/我的 涉及身份切换
        classFragment = ClassFragment.instance(BaseUserInfo.identity)
        myFragment = MyFragment.instance(BaseUserInfo.identity)
        // fragment 填充
        fragmentList.add(classFragment)
        fragmentList.add(MessageFragment())
        fragmentList.add(FoundFragment())
        fragmentList.add(myFragment)
        // 监听切换身份事件
        disposableIdentity =
            BaseRxBus.mBusInstance.toObservable(EventChangeUserIdentity::class.java)
                .subscribe {
                    val identity = it.data as Int
                    // 切换的身份保留在本地
                    BaseUserInfo.identity = identity
                    classFragment.switchClassListFragment(identity, supportFragmentManager)
                    myFragment.switchFragment(identity, supportFragmentManager)
                }
        // 监听列表跳入班级
        disposableClass = BaseRxBus.mBusInstance.toObservable(EventClassListBean::class.java)
            .subscribe {
                val listBean = it.data as ParentClassListBean
                when (BaseUserInfo.identity) {
                    1 -> {
                        classFragment.jumpParentClassDetailFragment(
                            listBean.classId,
                            listBean.studentId,
                            supportFragmentManager
                        )
                    }
                    2 -> {
                        classFragment.jumpTeacherClassDetailFragment(
                            listBean.classId,
                            supportFragmentManager
                        )
                    }
                }

            }
        // 初始化底部导航栏
        initBottomNavigationBar()
        // 初始化ViewPager
        initViewPager()
    }

    private fun initViewPager() {
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return fragmentList[p0]
            }

            override fun getCount(): Int {
                return fragmentList.size
            }
        }
        viewPager.setCurrentItem(0, true)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 3) {
                    BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新孩子信息"))
                }
                bottomNavigationBar.selectTab(position)
            }
        })

        viewPager.offscreenPageLimit = 4
    }

    private fun initBottomNavigationBar() {
        val classItem = BottomNavigationItem(R.mipmap.menu_class_on, "班级")
            .setActiveColor(resources.getColor(R.color.login_xdt_btn_color_able))
            .setInActiveColor(resources.getColor(R.color.text_xdt_hint))
            .setInactiveIconResource(R.mipmap.menu_class_off)

        val messageItem = BottomNavigationItem(R.mipmap.menu_news_on, "消息")
            .setActiveColor(resources.getColor(R.color.login_xdt_btn_color_able))
            .setInActiveColor(resources.getColor(R.color.text_xdt_hint))
            .setInactiveIconResource(R.mipmap.menu_news_off)

        val foundItem = BottomNavigationItem(R.mipmap.menu_found_on, "发现")
            .setActiveColor(resources.getColor(R.color.login_xdt_btn_color_able))
            .setInActiveColor(resources.getColor(R.color.text_xdt_hint))
            .setInactiveIconResource(R.mipmap.menu_found_off)

        val meItem = BottomNavigationItem(R.mipmap.menu_my_open, "我的")
            .setActiveColor(resources.getColor(R.color.login_xdt_btn_color_able))
            .setInActiveColor(resources.getColor(R.color.text_xdt_hint))
            .setInactiveIconResource(R.mipmap.menu_my_off)

        bottomNavigationBar.backgroundColor = resources.getColor(R.color.common_white)

        bottomNavigationBar.addItem(classItem)
            .addItem(messageItem)
            .addItem(foundItem)
            .addItem(meItem).setFirstSelectedPosition(0).setMode(BottomNavigationBar.MODE_FIXED)
            .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
            .initialise()

        bottomNavigationBar.setTabSelectedListener(object :
            BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {

            }

            override fun onTabUnselected(position: Int) {

            }

            override fun onTabSelected(position: Int) = viewPager.setCurrentItem(position, false)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseRxBus.mBusInstance.unSubscribe(disposableIdentity)
        BaseRxBus.mBusInstance.unSubscribe(disposableClass)
    }
}
