package com.ioyouyun.home.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ioyouyun.R;
import com.ioyouyun.base.BaseFragment;
import com.ioyouyun.contacts.ContactDetailActivity;
import com.ioyouyun.contacts.adapter.ContactsListAdapter;
import com.ioyouyun.contacts.model.NearbyUserEntity;
import com.ioyouyun.contacts.presenter.ContactsPresenter;
import com.ioyouyun.contacts.view.ContactView;
import com.ioyouyun.home.widgets.DividerItemDecoration;
import com.ioyouyun.home.widgets.ScrollChildSwipeRefreshLayout;
import com.ioyouyun.utils.FunctionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/8/9.
 */
public class ContactsFragment extends BaseFragment<ContactView, ContactsPresenter> implements ContactView {

    public static final String ARGUMENT_HOME_ID = "HOME_ID";
    private Activity activity;
    private ScrollChildSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ContactsListAdapter contactsListAdapter;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private int flag = 1; // flag 1：HomeActivity 2:InviteMemberActivity

    @Override
    protected ContactsPresenter initPresenter() {
        return new ContactsPresenter(getActivity());
    }

    public static ContactsFragment newInstance(String homeId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_HOME_ID, homeId);
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_contacts);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactsListAdapter = new ContactsListAdapter(getActivity());
        recyclerView.setAdapter(contactsListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        contactsListAdapter.setOnItemClickLitener(new ContactsListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (flag == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_UID, contactsListAdapter.getItem(position).getId());
                    bundle.putString(KEY_NICKNAME, contactsListAdapter.getItem(position).getNickname());
                    $startActivity(ContactDetailActivity.class, bundle);
                }
            }
        });

        swipeRefreshLayout = (ScrollChildSwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(recyclerView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getLocation();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermission();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    private void requestPermission() {
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)){
                new AlertDialog.Builder(activity)
                        .setMessage("申请定位权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).show();
            }else{
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }else{
            presenter.getLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.getLocation();
            } else {
                FunctionUtil.toastMessage("Permission Denied");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * @param flag 1：HomeActivity 2:InviteMemberActivity
     */
   public void loadFragmentData(int flag) {
       this.flag = flag;
       contactsListAdapter.setFlags(true);
    }

    /**
     * conference邀请人列表
     *
     * @return
     */
    public List<String> getConferenceInviteList() {
        List<NearbyUserEntity> nearbyUserEntityList = presenter.getNearbyUserEntityList();
        List<String> list = new ArrayList<>();
        for (NearbyUserEntity entity : nearbyUserEntityList) {
            if (entity.isChecked()) {
                list.add(entity.getId());
            }
        }
        return list;
    }

    /**
     * 群组邀请人列表
     *
     * @return
     */
    public String getGroupInviteList() {
        StringBuffer stringBuffer = null;
        List<NearbyUserEntity> nearbyUserEntityList = presenter.getNearbyUserEntityList();
        for (NearbyUserEntity entity : nearbyUserEntityList) {
            if (entity.isChecked()) {
                if (stringBuffer == null)
                    stringBuffer = new StringBuffer();
                else
                    stringBuffer.append(",");
                stringBuffer.append(entity.getId());
            }
        }
        return stringBuffer == null ? null : stringBuffer.toString();
    }

    private void refreshAdapter(List<NearbyUserEntity> list) {
        contactsListAdapter.setContactsList(list);
        contactsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setListView(List<NearbyUserEntity> list) {
        swipeRefreshLayout.setRefreshing(false);
        refreshAdapter(list);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

}
