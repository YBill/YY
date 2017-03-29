package com.ioyouyun.group.biz;

import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.GroupEnum;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.util.HttpCallback;

import java.util.List;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public class GroupRequestImpl implements GroupRequest {

    @Override
    public void getGroupList(long uid, String cat1, String cat2, int showtype, final OnGroupListener listener) {
        WeimiInstance.getInstance().getGroupList(uid, cat1, cat2, showtype, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getGroupList success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getGroupList:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void createGroup(String name, String intra, int cat1, int cat2, int level, GroupEnum.AddGroupApply apply, final OnGroupListener listener) {
        WeimiInstance.getInstance().createGroup(name, intra, cat1, cat2, level, apply, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("createGroup success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("createGroup error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void getGroupInfo(long gid, final OnGroupListener listener) {
        WeimiInstance.getInstance().getGroupInfo(gid, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getGroupInfo success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getGroupInfo error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void getGroupMembers(long gid, int role, int count, final OnGroupListener listener) {
        WeimiInstance.getInstance().getGroupUserList(gid, role, count, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getGroupMembers success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getGroupMembers error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void getChatRoomMembers(String roomId, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortRoomUserList(roomId, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getChatRoomMembers success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getChatRoomMembers error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void exitGroup(long gid, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortExitGroup(gid, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("exitGroup success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("exitGroup error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void kickGroupUsers(long gid, String uids, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortGroupuserDel(gid, uids, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("kickGroupUsers success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("kickGroupUsers error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void deleteGroup(String groupId, final OnGroupListener listener) {
        WeimiInstance.getInstance().deleteGroup(groupId, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("deleteGroup success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("deleteGroup error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void addGroupUsers(long gid, String uids, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortGroupuserAdd(gid, uids, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("addGroupUsers success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("addGroupUsers error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void applyAddGroup(long gid, String intra, final OnGroupListener listener) {
        WeimiInstance.getInstance().applyAddGroup(gid, intra, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("applyAddGroup success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("applyAddGroup error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void searchGroupById(long gid, final OnGroupListener listener) {
        WeimiInstance.getInstance().searchGroupById(gid, 2, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("searchUserById success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("searchUserById error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void enterChatRoom(String roomId, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortEnterRoom(roomId, FunctionUtil.uid, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("enterChatRoom success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("enterChatRoom error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void exitChatRoom(String roomId, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortExitRoom(roomId, FunctionUtil.uid, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("exitChatRoom success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("exitChatRoom error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void modifyBasicGroupInfo(long gid, String groupName, String groupIntra, int groupType, final OnGroupListener listener) {
        WeimiInstance.getInstance().modifyBasicGroupInfo(gid, groupName, groupIntra, groupType, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("modifyBasicGroupInfo success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("modifyBasicGroupInfo error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void modifyGroupExendInfo(long gid, String uid, String mark, boolean isShowMark, GroupEnum.GroupMsgSetType groupMsgSetType, final OnGroupListener listener) {
        WeimiInstance.getInstance().mofidyExendInfo(gid, uid, mark, isShowMark, groupMsgSetType, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("mofidyExendInfo success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("mofidyExendInfo error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void modifyGroupSettingInfo(long gid, GroupEnum.AddGroupApply apply, GroupEnum.MemberAuditType auditType, int inviteRole, int sayRole, int memberVisibile,
                                       int infoVisibile, GroupEnum.SwitchType isPass, int infoEditRole, int gagRole, final OnGroupListener listener) {
        WeimiInstance.getInstance().modifyGroupSettingInfo(gid, apply, auditType, inviteRole, sayRole,
                memberVisibile, infoVisibile, isPass, infoEditRole, gagRole, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("modifyGroupSettingInfo success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("modifyGroupSettingInfo error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void modifyGroupMemberRole(long gid, String uid, int role, final OnGroupListener listener) {
        WeimiInstance.getInstance().modifyMemberRole(gid, uid, role, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("modifyGroupMemberRole success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("modifyGroupMemberRole error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void modifyChatRoomMemberRole(String roomId, String uid, String role, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortSetRole(uid, role, roomId, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("modifyChatRoomMemberRole success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("modifyChatRoomMemberRole error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void modifyGroupLevel(long gid, int level, final OnGroupListener listener) {
        WeimiInstance.getInstance().modifyGroupLevel(gid, level, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("modifyGroupLevel success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("modifyGroupLevel error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void setGroupGag(String gid, String uids, long gagTime, boolean status, final OnGroupListener listener) {
        WeimiInstance.getInstance().setGag(uids, gid, gagTime, status, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("setGroupGag success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("setGroupGag error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void setChatRoomGag(String roomId, String uids, long gagTime, boolean status, final OnGroupListener listener) {
        WeimiInstance.getInstance().shortUsersGag(uids, status, roomId, gagTime, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("setChatRoomGag success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("setChatRoomGag error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void getGroupGagUsers(long gid, final OnGroupListener listener) {
        WeimiInstance.getInstance().getGagUsers(gid, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getGroupGagUsers success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getGroupGagUsers error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }

    @Override
    public void getChatRoomAllGagUsers(final OnGroupListener listener) {
        WeimiInstance.getInstance().shortGetGagUsers(new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("getChatRoomAllGagUsers success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("getChatRoomAllGagUsers error:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 120);
    }
}
