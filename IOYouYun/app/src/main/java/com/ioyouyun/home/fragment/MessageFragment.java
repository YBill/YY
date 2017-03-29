package com.ioyouyun.home.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseFragment;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.home.widgets.DividerItemDecoration;
import com.ioyouyun.message.adapter.MessageListAdapter;
import com.ioyouyun.message.presenter.MessagePresenter;
import com.ioyouyun.message.view.MessageView;
import com.ioyouyun.utils.FunctionUtil;

import java.util.List;

/**
 * Created by 卫彪 on 2016/8/9.
 */
public class MessageFragment extends BaseFragment<MessageView, MessagePresenter> implements MessageView {

    public static final String ARGUMENT_HOME_ID = "HOME_ID";
    private RecyclerView recyclerView;
    private MessageListAdapter messageAdapter;

    public static MessageFragment newInstance(String homeId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_HOME_ID, homeId);
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected MessagePresenter initPresenter() {
        return new MessagePresenter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_message);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageAdapter = new MessageListAdapter(getActivity());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        messageAdapter.setOnItemClickLitener(new MessageListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                presenter.onItemClick(position);
            }
        });
        messageAdapter.setOnItemLongClickListener(new MessageListAdapter.OnItemLongClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                final ChatMsgEntity entity = presenter.getMessageList().get(position);
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(entity.getName())
                        .setItems(new String[]{"删除该聊天"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                YouyunDbManager.getIntance().deleteRecentContactById(entity.getOppositeId());
                                presenter.getRecentContact();

                                String name = FunctionUtil.jointTableName(entity.getOppositeId());
                                List<ChatMsgEntity> list = YouyunDbManager.getIntance().getChatMsgEntityList(name);
                                if (YouyunDbManager.getIntance().removeChatImageMsg(name)) {
                                    for (ChatMsgEntity entity : list) {
                                        if (ChatMsgEntity.CHAT_TYPE_SEND_IMAGE == entity.getMsgType()
                                                || ChatMsgEntity.CHAT_TYPE_RECV_IMAGE == entity.getMsgType()
                                                || ChatMsgEntity.CHAT_TYPE_SEND_FILE == entity.getMsgType()
                                                || ChatMsgEntity.CHAT_TYPE_RECV_FILE == entity.getMsgType()) {
                                            YouyunDbManager.getIntance().deleteFileInfoById(entity.getMsgId());
                                        }
                                    }
                                }

                            }
                        })
                        .create();
                dialog.show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getRecentContact();
//        presenter.receiveNotify();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    /**
     * 刷新List
     *
     * @param list
     */
    private void refreshAdapter(List<ChatMsgEntity> list) {
        if (messageAdapter != null) {
            messageAdapter.setMessageList(list);
            messageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void refreshList(List<ChatMsgEntity> list) {
        refreshAdapter(list);
    }

    @Override
    public void showNotify(long count) {

    }

    @Override
    public void refreshMessageList(ChatMsgEntity entity) {
        if (entity != null)
            presenter.receiveMessage(entity);
    }

    @Override
    public void refreshMessageNotify() {
        presenter.receiveNotify();
    }

}
