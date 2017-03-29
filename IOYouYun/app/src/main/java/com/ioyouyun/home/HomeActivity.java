package com.ioyouyun.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.group.activity.CreateGroupActivity;
import com.ioyouyun.home.adapter.HomeViewPagerAdapter;
import com.ioyouyun.home.fragment.ContactsFragment;
import com.ioyouyun.home.fragment.GroupFragment;
import com.ioyouyun.home.fragment.MessageFragment;
import com.ioyouyun.home.presenter.HomePresenter;
import com.ioyouyun.home.view.HomeView;
import com.ioyouyun.search.SearchActivity;
import com.ioyouyun.profile.ProfileActivity;
import com.ioyouyun.settings.VersionActivity;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.PlatformSharedUtil;
import com.ioyouyun.utils.PushSharedUtil;

public class HomeActivity extends BaseActivity<HomeView, HomePresenter>
        implements HomeView, NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_VERSION = 1000;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeViewPagerAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView avatar;
    private TextView userNameText;
    private TextView uidText;
    private MenuItem homeMemu;
    private MenuItem soundMemu;
    private MenuItem vibrateMemu;
    private MenuItem switchVersionMemu;
    private MenuItem addFriendMemu;
    private CheckBox soundCb;
    private CheckBox vibrateCb;
    private TextView pushTextView;
    private Toolbar toolbar;
    private LayoutInflater inflater;

    private ContactsFragment contactsFragment;

    @Override
    protected HomePresenter initPresenter() {
        return new HomePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void $setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(TextUtils.isEmpty(FunctionUtil.nickname) ? FunctionUtil.nickname : "登录");
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setHomeButtonEnabled(true);  //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //创建返回键，并实现打开关/闭监听

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                homeMemu.setChecked(true);
            }
        };
        mDrawerToggle.syncState();  //初始化状态
        drawerLayout.addDrawerListener(mDrawerToggle); //将DrawerLayout与DrawerToggle绑定
//        drawerLayout.setScrimColor(Color.TRANSPARENT);   //去除侧边阴影
    }

    @Override
    protected void initView() {
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        avatar = (ImageView) view.findViewById(R.id.imageView);
        userNameText = (TextView) view.findViewById(R.id.tv_user_name);
        uidText = (TextView) view.findViewById(R.id.tv_uid);

        Menu menu = navigationView.getMenu();

        homeMemu = menu.findItem(R.id.menu_home);

        MenuItem addFriendMenu = menu.findItem(R.id.menu_add_friend);
        if(FunctionUtil.isOnlinePlatform) addFriendMenu.setVisible(false);

        MenuItem pushMenu = menu.findItem(R.id.menu_push_time);
        View pushView = MenuItemCompat.getActionView(pushMenu);
        pushTextView = (TextView) pushView.findViewById(R.id.tv_push_time);

        soundMemu = menu.findItem(R.id.menu_push_sound);
        View soundView = MenuItemCompat.getActionView(soundMemu);
        soundCb = (CheckBox) soundView.findViewById(R.id.checkbox);

        vibrateMemu = menu.findItem(R.id.menu_push_vibrate);
        View vibrateView = MenuItemCompat.getActionView(vibrateMemu);
        vibrateCb = (CheckBox) vibrateView.findViewById(R.id.checkbox);

        switchVersionMemu = menu.findItem(R.id.menu_switch_version);
        if (PlatformSharedUtil.getInstance().getDeveloper()) {
            switchVersionMemu.setVisible(true);
        } else {
            switchVersionMemu.setVisible(false);
        }
        if (FunctionUtil.isOnlinePlatform) {
            switchVersionMemu.setTitle(R.string.switch_test_version);
            switchVersionMemu.setIcon(R.drawable.ic_test_72px);
        } else {
            switchVersionMemu.setTitle(R.string.switch_online_version);
            switchVersionMemu.setIcon(R.drawable.ic_online_72px);
        }

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

    }

    @Override
    protected void setListener() {
        navigationView.setNavigationItemSelectedListener(this);
        soundCb.setOnClickListener(this);
        vibrateCb.setOnClickListener(this);
        avatar.setOnClickListener(this);
        userNameText.setOnClickListener(this);
        toolbar.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if(TextUtils.isEmpty(FunctionUtil.uid)){
            login();
        }else{
            setPersonalInfo();
        }

        vibrateCb.setChecked(PushSharedUtil.getInstance().getVibration());
        soundCb.setChecked(PushSharedUtil.getInstance().getSound());

        setupViewPager(viewPager);
    }

    private void setPersonalInfo(){
        toolbar.setTitle(FunctionUtil.nickname);
        userNameText.setText(FunctionUtil.nickname);
        uidText.setText(FunctionUtil.uid);
        /*if(!TextUtils.isEmpty(FunctionUtil.nickname)){
            String nickName = FunctionUtil.nickname;
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable = TextDrawable.builder().buildRoundRect(end, Color.parseColor("#cb5372"), 10);
            avatar.setImageDrawable(drawable);
        }*/

        presenter.getPushInfo();
    }

    private void login(){
        FunctionUtil.autoLogin(this, new FunctionUtil.LoginListener() {
            @Override
            public void loginResult(boolean result) {
                if(result){
                    setPersonalInfo();
                }
            }
        });
    }

    @Override
    public void widgetClick(View v) {
        if (v == soundCb) {
            PushSharedUtil.getInstance().setSound(soundCb.isChecked());
        } else if (v == vibrateCb) {
            PushSharedUtil.getInstance().setVibration(vibrateCb.isChecked());
        } else if (v == avatar || v == userNameText) {
            $startActivity(ProfileActivity.class);
        } else if (v == toolbar){
            if(TextUtils.isEmpty(FunctionUtil.uid)){
                login();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MessageFragment.newInstance("message"), "消息");
        adapter.addFragment(contactsFragment = ContactsFragment.newInstance("contacts"), "联系人");
        adapter.addFragment(GroupFragment.newInstance("group"), "群组");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private int startTime = 0;
    private int endTime = 24;

    private void showPushPicker() {
        View view = View.inflate(this, R.layout.layout_push_picker, null);
        final NumberPicker startTimePicker = (NumberPicker) view.findViewById(R.id.np_start_time);
        final NumberPicker endTimePicker = (NumberPicker) view.findViewById(R.id.np_end_time);

        startTimePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        startTimePicker.setMaxValue(24);
        startTimePicker.setMinValue(0);
        startTimePicker.setValue(startTime);
        startTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                startTimePicker.setValue(newVal);
            }
        });

        endTimePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        endTimePicker.setMaxValue(24);
        endTimePicker.setMinValue(0);
        endTimePicker.setValue(endTime);
        endTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                endTimePicker.setValue(newVal);
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.push_alert_time)).setView(view)
                .setPositiveButton(getResources().getString(R.string.string_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.setPushInfo(startTimePicker.getValue(), endTimePicker.getValue());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.string_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        alertDialog.show();
    }

    private void showCreateDialog(){
        final PopupWindow popupWindow = new PopupWindow(this);
        View view = inflater.inflate(R.layout.popup_create_group, null);
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setAnimationStyle(R.style.AnimationPreview);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        view.findViewById(R.id.tv_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                createRoom(1);
                drawerLayout.closeDrawer(navigationView);
            }
        });
        view.findViewById(R.id.tv_chat_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                createRoom(2);
                drawerLayout.closeDrawer(navigationView);
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 创建 1：群 2：聊天室
     */
    private void createRoom(int type){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FLAG, type);
        $startActivity(CreateGroupActivity.class, bundle);
    }

    /**
     *  搜索 1：好友 2：群
     */
    private void addUserOrGroup(int type){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FLAG, type);
        $startActivity(SearchActivity.class, bundle);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

  /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                drawerLayout.closeDrawer(navigationView);
                break;
            case R.id.menu_add_friend:
                addUserOrGroup(1);
                drawerLayout.closeDrawer(navigationView);
                break;
            case R.id.menu_add_group:
                addUserOrGroup(2);
                drawerLayout.closeDrawer(navigationView);
                break;
            case R.id.menu_create_group:
                if(FunctionUtil.isOnlinePlatform){
                    drawerLayout.closeDrawer(navigationView);
                    createRoom(1);
                } else showCreateDialog();
                break;
            case R.id.menu_push_sound:
//                menuItem.setChecked(true);
                boolean isSound = !soundCb.isChecked();
                soundCb.setChecked(isSound);
                PushSharedUtil.getInstance().setSound(isSound);
                break;
            case R.id.menu_push_vibrate:
                boolean isVibrate = !vibrateCb.isChecked();
                vibrateCb.setChecked(isVibrate);
                PushSharedUtil.getInstance().setVibration(isVibrate);
                break;
            case R.id.menu_push_time:
                showPushPicker();
                break;
            case R.id.menu_version:
                drawerLayout.closeDrawer(navigationView);

                Intent intent = new Intent(this, VersionActivity.class);
                startActivityForResult(intent, REQUEST_VERSION);
                break;
            case R.id.menu_switch_version:
                presenter.switchVersion();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VERSION) {
                if (PlatformSharedUtil.getInstance().getDeveloper()) {
                    switchVersionMemu.setVisible(true);
                } else {
                    switchVersionMemu.setVisible(false);
                }
            }
        }
    }

    @Override
    public void setPushTime(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        String start, end;
        if (startTime < 10)
            start = "0" + startTime;
        else
            start = String.valueOf(startTime);
        if (endTime < 10)
            end = "0" + endTime;
        else
            end = String.valueOf(endTime);
        start += ":00";
        end += ":00";
        pushTextView.setText(start + "-" + end);
    }

    @Override
    public void switchVersion() {
        Logger.e("platform:" + PlatformSharedUtil.getInstance().getPlatform());
        // 重启
        Intent rebootIntent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, rebootIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, restartIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void showLoading() {
        loddingDialog.showProgress();
    }

    @Override
    public void hideLoading() {
        loddingDialog.cancleProgress();
    }
}
