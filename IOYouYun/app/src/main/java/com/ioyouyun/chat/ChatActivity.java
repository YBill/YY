package com.ioyouyun.chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseActivity;
import com.ioyouyun.chat.adapter.ChatMsgAdapter;
import com.ioyouyun.chat.adapter.ExpressionGridViewAdapter;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.POIInfo;
import com.ioyouyun.chat.presenter.ChatPresenter;
import com.ioyouyun.chat.util.UnicodeToEmoji;
import com.ioyouyun.chat.view.ChatView;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.group.activity.GroupSetActivity;
import com.ioyouyun.group.activity.GroupSettingActivity;
import com.ioyouyun.home.HomeActivity;
import com.ioyouyun.home.widgets.ScrollChildSwipeRefreshLayout;
import com.ioyouyun.utils.FileUtil;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.widgets.ExpandGridView;
import com.melink.baseframe.utils.DensityUtils;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMM;
import com.melink.bqmmsdk.sdk.BQMMMessageHelper;
import com.melink.bqmmsdk.sdk.IBqmmSendMessageListener;
import com.melink.bqmmsdk.ui.keyboard.BQMMKeyboard;
import com.melink.bqmmsdk.ui.keyboard.IBQMMUnicodeEmojiProvider;
import com.melink.bqmmsdk.widget.BQMMEditView;
import com.melink.bqmmsdk.widget.BQMMSendButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/6/3.
 */
public class ChatActivity extends BaseActivity<ChatView, ChatPresenter> implements ChatView, ChatMsgAdapter.FileClickListener {

    public static final int REQUEST_CODE_SETTING = 1000;
    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_LOCAL = 1002;
    public static final int REQUEST_CODE_FILE = 1003;
    public static final int REQUEST_CODE_LOCATION = 1004;
    private Button buttonSetModeVoice; // 左侧语音键盘按钮
    private View buttonPressToSpeak; // 按住说话按钮
    private TextView textPressToSpeck; // 按住说话文字
    private RelativeLayout editLayout; // 输入框外层View,包括EditText和emoji表情按钮
    private BQMMEditView mEditTextContent; // 输入框
    private CheckBox emojiIcon; // emoji图标
    private Button btnMore; // 右侧更多按钮(加号)
    private BQMMSendButton buttonSend; // 发送按钮,跟当btnMore隐藏时显示
    private View modeView; // 底部更多View,包括表情或拍照等按钮
    private BQMMKeyboard keyboard;
    private View btnContainer; // 底部扩展按钮,拍照和相册等
    private TextView photoGalleryBtn; // 相册
    private TextView takePhotoBtn; // 拍照
    private TextView fileBtn; // 文件
    private TextView locationBtn; // 位置
    private ListView chatListView; // 聊天界面
    private TextView topTitleText; // title
    private TextView clearBtn; // 清空
    private View recordingContainer; // 录音提示框
    private ImageView micImage;
    private TextView recordingHint;
    private ScrollChildSwipeRefreshLayout swipeRefreshLayout;

    private ChatMsgAdapter chatMsgAdapter; // Chat Adapter

    /**
     * true: 显示键盘图标，按住说话按钮
     * false: 显示语音图标，输入框
     */
    private boolean isSpeck = false;
    private PowerManager.WakeLock wakeLock;
    private Integer[] micImages = new Integer[]{
            R.drawable.record_animate_01,
            R.drawable.record_animate_02,
            R.drawable.record_animate_03,
            R.drawable.record_animate_04,
            R.drawable.record_animate_05,
            R.drawable.record_animate_06,
            R.drawable.record_animate_07,
            R.drawable.record_animate_08,
            R.drawable.record_animate_09,
            R.drawable.record_animate_10,
            R.drawable.record_animate_11,
            R.drawable.record_animate_12,
            R.drawable.record_animate_13,
            R.drawable.record_animate_14,
    };
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageResource(micImages[msg.what]);
        }
    };

    private File cameraFile;
    private int intentFlag = 0; // 1: 点击返回按钮时跳到HomeActivity
    private String toUId;
    private String nickName;
    private ConvType convType;
    private int role; // 群或聊天室权限

    private int mPendingShowPlaceHolder = 0;
    private InputMethodManager manager;

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity(){
        if(1 == intentFlag){
            $startActivity(HomeActivity.class);
            finish();
        } else if(0 == intentFlag){
            finish();
        }
    }

    @Override
    protected ChatPresenter initPresenter() {
        return new ChatPresenter();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void $setToolBar() {
        Toolbar toolbar = $findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }

    @Override
    protected void initView() {
        buttonSetModeVoice = $findViewById(R.id.btn_set_mode_voice);
        buttonPressToSpeak = $findViewById(R.id.btn_press_to_speak);
        textPressToSpeck = $findViewById(R.id.tv_press_to_speck);
        editLayout = $findViewById(R.id.edittext_layout);
        editLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        editLayout.requestFocus();
        mEditTextContent = $findViewById(R.id.et_sendmessage);
        emojiIcon = $findViewById(R.id.iv_emoticons_normal);
        btnMore = $findViewById(R.id.btn_more);
        buttonSend = $findViewById(R.id.btn_send);
        modeView = $findViewById(R.id.ll_more);
        keyboard = $findViewById(R.id.chat_msg_input_box);
        btnContainer = $findViewById(R.id.ll_btn_container);
        photoGalleryBtn = $findViewById(R.id.tv_picture);
        takePhotoBtn = $findViewById(R.id.tv_take_photo);
        fileBtn = $findViewById(R.id.tv_file);
        locationBtn = $findViewById(R.id.tv_location);
        chatListView = $findViewById(R.id.lv_chat);
        topTitleText = $findViewById(R.id.tv_title);
        clearBtn = $findViewById(R.id.btn_right);
        recordingContainer = $findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        swipeRefreshLayout = $findViewById(R.id.refresh_layout);
        $setToolBar();
    }

    @Override
    protected void setListener() {
        buttonSetModeVoice.setOnClickListener(this);
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListener());
        mEditTextContent.setOnClickListener(this);
        emojiIcon.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        takePhotoBtn.setOnClickListener(this);
        photoGalleryBtn.setOnClickListener(this);
        fileBtn.setOnClickListener(this);
        locationBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        chatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                emojiIcon.setChecked(false);
                editLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                closeBroad();
                if (modeView.getVisibility() == View.VISIBLE)
                    modeView.setVisibility(View.GONE);
                return false;
            }
        });
        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    editLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // pull refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getHistory(toUId, convType);
            }
        });
        // BQMM listener
        mEditTextContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if(mPendingShowPlaceHolder == 0){
                    if (keyboard.isKeyboardVisible() && isSoftInputShown()) {
                        chatListView.setSelection(chatListView.getAdapter().getCount() - 1);
                        keyboard.hideKeyboard();
                        return false;
                    }
                }else{
                    if (isSoftInputShown()) {
                        /*ViewGroup.LayoutParams params = bqmmKeyboard.getLayoutParams();
                        int distance = getSupportSoftInputHeight();
                        // 调整PlaceHolder高度
                        if (distance != params.height) {
                            params.height = distance;
                            bqmmKeyboard.setLayoutParams(params);
                        }*/
                        return false;
                    } else {
                        if(mPendingShowPlaceHolder == 1){
                            chatListView.setSelection(chatListView.getAdapter().getCount() - 1);
                            keyboard.showKeyboard();
                            mPendingShowPlaceHolder = 0;
                        }else if(mPendingShowPlaceHolder == 2){
                            chatListView.setSelection(chatListView.getAdapter().getCount() - 1);
                            modeView.setVisibility(View.VISIBLE);
                            btnContainer.setVisibility(View.VISIBLE);
                            mPendingShowPlaceHolder = 0;
                        }
                        return false;
                    }
                }

                return true;
            }
        });
        mEditTextContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                emojiIcon.setChecked(false);
                editLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                modeView.setVisibility(View.GONE);
                return false;
            }
        });
        emojiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeView.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.GONE);

                if (keyboard.isKeyboardVisible()) { // PlaceHolder -> Keyboard
                    showSoftInput(mEditTextContent);
                } else if (isSoftInputShown()) { // Keyboard -> PlaceHolder
                    mPendingShowPlaceHolder = 1;
                    hideSoftInput(mEditTextContent);
                } else { // Just show PlaceHolder
                    chatListView.setSelection(chatListView.getAdapter().getCount() - 1);
                    keyboard.showKeyboard();
                }
            }
        });
        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BQMM.getInstance().startShortcutPopupWindowByoffset(ChatActivity.this, s.toString(), buttonSend, 0, DensityUtils.dip2px(ChatActivity.this, 4));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        BQMM.getInstance().setBqmmSendMsgListener(new IBqmmSendMessageListener() {
            /**
             * 单个大表情消息
             */
            @Override
            public void onSendFace(Emoji face) {
                presenter.sendEmoji(toUId, face.getEmoCode(), nickName, convType);
            }

            /**
             * 图文混排消息
             */
            @Override
            public void onSendMixedMessage(List<Object> emojis, boolean isMixedMessage) {
                if (isMixedMessage) {
                    // 图文混排消息
                    // TODO
                } else {
                    // 纯文本消息
                    String msgString = BQMMMessageHelper.getMixedMessageString(emojis);
                    presenter.sendText(toUId, msgString, nickName, convType);
                }
            }
        });

    }

    @Override
    protected void initData() {
        //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "youyun");

        getIntentExtra();

        // initiative BQMM
        BQMM.getInstance().setEditView(mEditTextContent);
        BQMM.getInstance().setKeyboard(keyboard);
        BQMM.getInstance().setSendButton(buttonSend);
        BQMM.getInstance().load();
        UnicodeToEmoji.initPhotos(this);
        BQMM.getInstance().setUnicodeEmojiProvider(new IBQMMUnicodeEmojiProvider() {
            @Override
            public Drawable getDrawableFromCodePoint(int i) {
                return UnicodeToEmoji.EmojiImageSpan.getEmojiDrawable(i);
            }
        });

        // init adapter
        chatMsgAdapter = new ChatMsgAdapter(this);
        chatListView.setAdapter(chatMsgAdapter);
        chatMsgAdapter.setOnFileClickListener(this);

        clearBtn.setText(getResources().getString(R.string.string_setting));
        clearBtn.setVisibility(View.VISIBLE);
        topTitleText.setText(nickName);
        topTitleText.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(chatListView);

        presenter.refreshLocalData(toUId);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_mode_voice:
                isSpeck = !isSpeck;
                if (isSpeck) {
                    closeBroad();
                    buttonSetModeVoice.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn_normal);
                    editLayout.setVisibility(View.GONE);
                    buttonPressToSpeak.setVisibility(View.VISIBLE);
                    modeView.setVisibility(View.GONE);
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                } else {
                    showSoftInput(mEditTextContent);
                    modeView.setVisibility(View.GONE);
                    emojiIcon.setChecked(false);
                    editLayout.setVisibility(View.VISIBLE);
                    buttonSetModeVoice.setBackgroundResource(R.drawable.icon_chat_voice);
                    buttonPressToSpeak.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(mEditTextContent.getText().toString())) {
                        btnMore.setVisibility(View.VISIBLE);
                        buttonSend.setVisibility(View.GONE);
                    } else {
                        btnMore.setVisibility(View.GONE);
                        buttonSend.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.btn_more:

                emojiIcon.setChecked(false);
                if(editLayout.getVisibility() == View.VISIBLE){
                    mEditTextContent.requestFocus();
                    mPendingShowPlaceHolder = 2;
                    closeBroad();
                }else{
                    chatListView.setSelection(chatListView.getAdapter().getCount() - 1);
                    modeView.setVisibility(View.VISIBLE);
                    btnContainer.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_right:
                if (ConvType.group == convType || ConvType.room == convType){
                    // 设置
//                    Bundle bundle = new Bundle();
//                    bundle.putString(KEY_GID, toUId);
//                    $startActivityForResult(GroupSettingActivity.class, bundle, REQUEST_CODE_SETTING);

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_GID, toUId);
                    $startActivityForResult(GroupSetActivity.class, bundle, REQUEST_CODE_SETTING);
                } else if (ConvType.single == convType) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_UID, toUId);
                    bundle.putString(KEY_NICKNAME, nickName);
                    $startActivity(FriendSetActivity.class, bundle);
                }
                break;
            case R.id.tv_take_photo:
                selectPicFromCamera();
                break;
            case R.id.tv_picture:
                selectPicFromLocal();
                break;
            case R.id.tv_file:
                openFile();
                break;
            case R.id.tv_location:
                toLocation();
                break;
            case R.id.btn_send:
                String text = mEditTextContent.getText().toString();
                if (!TextUtils.isEmpty(text))
                    presenter.sendText(toUId, text, nickName, convType);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = $getIntentExtra();
        String source = bundle.getString(SOURCE_KEY);
        if (TextUtils.equals("notify", source)) {
            YouyunDbManager.getIntance().updateUnreadNumber(toUId, 0);
        }

    }

    private static final String INTENT_FLAG_KEY = "intent_flag_key";
    private static final String TOID_KEY = "toid_key";
    private static final String NICKNAME_KEY = "nickname_key";
    private static final String CHAT_TYPE_KEY = "chat_type_key";
    private static final String USER_ROLE_KEY = "user_role_key";
    private static final String SOURCE_KEY = "source_key"; // 消息来源
    public static Intent startActivity(Context context, int intentFlag, String toId, String nickName, int chatType, int role, String sourse){
        Intent intent = new Intent(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SOURCE_KEY, sourse);
        bundle.putInt(INTENT_FLAG_KEY, intentFlag);
        bundle.putString(TOID_KEY, toId);
        bundle.putString(NICKNAME_KEY, nickName);
        bundle.putInt(CHAT_TYPE_KEY, chatType);
        bundle.putInt(USER_ROLE_KEY, role);
        intent.putExtras(bundle);
        return intent;
    }

    private void getIntentExtra() {
        Bundle bundle = $getIntentExtra();
        if (bundle != null) {
            intentFlag = bundle.getInt(INTENT_FLAG_KEY, 0);
            toUId = bundle.getString(TOID_KEY);
            FunctionUtil.TO_UID = toUId;
            nickName = bundle.getString(NICKNAME_KEY);
            role = bundle.getInt(USER_ROLE_KEY, 0);
            int type = bundle.getInt(CHAT_TYPE_KEY, 0); // 0:单聊 1：群聊 2:聊天室
            if (type == 0)
                convType = ConvType.single;
            else if (type == 1)
                convType = ConvType.group;
            else if (type == 2) {
                convType = ConvType.room;
                presenter.enterChatRoom(toUId);
            }

        }
    }

    private String getEmojiStringByUnicode(int unicodeJoy) {
        return new String(Character.toChars(unicodeJoy));
    }

    private List<View> emojiLists;

    private void showEmojiView() {
        /*if (emojiLists == null) {
            emojiLists = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                emojiLists.add(getEmojiItemViews(i));
            }
            expressionViewPager.setAdapter(new ExpressionPagerAdapter(emojiLists));
        }*/
    }

    /**
     * Emoji Item 系统共79个常用表情
     *
     * @param page
     * @return
     */
    private View getEmojiItemViews(int page) {
        List<String> list = new ArrayList<>();
        int start = 21 * page;
        int length;
        if (page == 3)
            length = 79;
        else
            length = (page + 1) * 21;
        for (int i = start; i < length; i++) {
            list.add(getEmojiStringByUnicode(0x1F601 + i));
        }
        View view = View.inflate(ChatActivity.this, R.layout.layout_expression_gridview, null);
        ExpandGridView gridView = (ExpandGridView) view.findViewById(R.id.gridview);
        final ExpressionGridViewAdapter gridViewAdapter = new ExpressionGridViewAdapter(ChatActivity.this, list);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditTextContent.append(gridViewAdapter.getItem(position));
            }
        });

        return view;
    }

    /**
     * 刷新adapter
     *
     * @param list
     */
    private void refreshAdapter(List<ChatMsgEntity> list) {
        chatMsgAdapter.setMsgEntityList(list);
        chatMsgAdapter.notifyDataSetChanged();
    }

    /**
     * 隐藏键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 打开文件管理
     */
    private void openFile(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try{
            startActivityForResult(intent, REQUEST_CODE_FILE);
        }catch(Exception e){
            FunctionUtil.toastMessage("没有正确打开文件管理器");
        }
    }

    /**
     * 位置
     */
    private void toLocation(){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FLAG, 1);
        $startActivityForResult(LocationActivity.class, bundle, REQUEST_CODE_LOCATION);
    }

    /**
     * 拍照
     */
    private void selectPicFromCamera() {
        if (!FunctionUtil.isExitsSdcard()) {
            FunctionUtil.toastMessage("SD卡不存在，不能拍照");
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        long ts = System.currentTimeMillis();
        String mImagePath = FileUtil.getCameraPath() + ts + ".jpg";
        cameraFile = new File(mImagePath);
        Uri uri = Uri.fromFile(cameraFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 相册
     */
    private void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 选择相册图片后处理
     *
     * @param uri
     */
    private String sendLocalImage( Uri uri) {
        String picturePath = "";
        if (uri != null) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
                cursor = null;
            } else {
                File file = new File(uri.getPath());
                if (file.exists()) {
                    picturePath = file.getAbsolutePath();
                }
            }
            if (picturePath == null || picturePath.equals("null") || "".equals(picturePath)) {
                FunctionUtil.toastMessage("找不到图片");
                return null;
            }
        }
        return picturePath;
    }

    /**
     * 发送位置
     * @param info
     */
    private void sendLocation(POIInfo info){
        presenter.sendLocation(toUId, info, nickName, convType);
    }

    /**
     * 按住说话
     */
    private class PressToSpeakListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!FunctionUtil.isExitsSdcard()) {
                        FunctionUtil.toastMessage("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true); // 更换背景, chat_press_speak_btn.xml
                        wakeLock.acquire();
                        textPressToSpeck.setText(getResources().getString(R.string.button_loosen_end));
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        presenter.startRealTimeRecord(toUId, nickName, convType, micImageHandler);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        recordingContainer.setVisibility(View.GONE);
                        FunctionUtil.toastMessage(R.string.recoding_fail);
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    textPressToSpeck.setText(getResources().getString(R.string.button_pushtotalk));
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        presenter.discardRecording(true);
                    } else {
                        // stop recording and send voice file
                        presenter.discardRecording(false);
                    }
                    try {
                        presenter.stopRealTimeRecord();
                    } catch (Exception e) {
                        e.printStackTrace();
                        FunctionUtil.toastMessage("发送失败，请检测服务器是否连接");
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < 0) {
                        recordingHint.setText(getString(R.string.release_to_cancel));
                        recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    return false;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (cameraFile != null && cameraFile.exists()) {
                    String path = cameraFile.getAbsolutePath();
                    presenter.sendImage(toUId, nickName, path, path.substring(path.lastIndexOf("/") + 1), convType);
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) {
                if (data != null) {
                    String path = sendLocalImage(data.getData());
                    if(!TextUtils.isEmpty(path))
                        presenter.sendImage(toUId, nickName, path, path.substring(path.lastIndexOf("/") + 1), convType);
                }
            } else if(requestCode == REQUEST_CODE_FILE){
                Uri uri = data.getData();
                String path = FileUtil.getPath(this, uri);
                Log.e("Bill", "path:" + path);
                presenter.sendFile(toUId, nickName, path, path.substring(path.lastIndexOf("/") + 1), convType);
            } else if (requestCode == REQUEST_CODE_SETTING) {
                finish();
            } else if (requestCode == REQUEST_CODE_LOCATION) {
                if (data != null) {
                    POIInfo info = data.getParcelableExtra("location");
                    if(info != null)
                        sendLocation(info);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld())
            wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ConvType.room == convType && 3 != role && 4 != role) {
            presenter.exitChatRoom(toUId);
        }
        presenter.onDestroy();
        BQMM.getInstance().destory();
    }

    @Override
    public void showChatMsgList(List<ChatMsgEntity> list) {
        refreshAdapter(list);
    }

    @Override
    public void setChatSelection(int position) {
        chatListView.setSelection(position);
    }

    @Override
    public void clearChatContent() {
        mEditTextContent.setText("");
    }

    @Override
    public void onCompleteLoad() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void enterChatRoom(boolean result) {
        if(!result){
            FunctionUtil.toastMessage("进入聊天室失败");
        }
    }

    @Override
    public void exitChatRoom(boolean result) {
        if(!result)
            FunctionUtil.toastMessage("退出聊天室失败");
    }

    @Override
    public void download(ChatMsgEntity entity) {
        presenter.downloadFile(entity);
    }

    /**
     * 关闭软键盘或表情
     */
    private void closeBroad() {
        if (keyboard.isKeyboardVisible()) {
            keyboard.hideKeyboard();
        } else if (isSoftInputShown()) {
            hideSoftInput(mEditTextContent);
        }
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput(View view) {
        view.requestFocus();
        manager.showSoftInput(view, 0);
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput(View view) {
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
//            Log.d("Bill", "getSoftButtonsBarHeight:" + getSoftButtonsBarHeight());
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
//            Log.w("Bill", "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
//        Log.d("Bill", "softInputHeight:" + softInputHeight);
        return softInputHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}
