package com.ioyouyun.group.biz;

import com.ioyouyun.wchat.GroupEnum;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public interface GroupRequest {

    /**
     * 获取用户群列表
     * @param uid 用户Id
     * @param cat1 群类型,0 为临时群, 1为私密群, 2为公开群, 3为聊天室  可以是多种类型的组合,以","分割
     * @param cat2 群类型：0 表示群组, 1 表示群联盟  可以是多种类型的组合,以","分割
     * @param showtype 显示类型： 1-返回名称，头像;2-返回基本信息；3-返回基本信息+成员数+最近4个成员
     * @param listener
     */
    void getGroupList(long uid, String cat1, String cat2, int showtype, OnGroupListener listener);

    /**
     * 创建群组
     * @param name 群名称
     * @param intra 群简介
     * @param cat1 群类型,0 为临时群,1为私密群,2为公开群,3为聊天室
     * @param cat2 群类型，0 表示群组,1 表示群联盟
     * @param level 群等级，0:50人 1：100人 2：200人 3：500人
     * @param apply 加群验证，yes：需验证 no：直接通过 not_allowed：不允许加入
     * @param listener
     */
    void createGroup(String name, String intra, int cat1, int cat2, int level, GroupEnum.AddGroupApply apply, OnGroupListener listener);

    /**
     * 获取群信息
     * @param gid
     * @param listener
     */
    void getGroupInfo(long gid, OnGroupListener listener);

    /**
     * 获取群成员
     * @param gid 群Id
     * @param role 按角色过滤,默认所有成员,1.普通成员 2.vip用户 3.管理员 4.群主
     * @param count 获取成员个数，默认所有
     * @param listener
     */
    void getGroupMembers(long gid, int role, int count, OnGroupListener listener);

    /**
     * 获取聊天室用户列表
     * @param roomId
     * @param listener
     */
    void getChatRoomMembers(String roomId, OnGroupListener listener);

    /**
     * 退群
     * @param gid
     * @param listener
     */
    void exitGroup(long gid, OnGroupListener listener);

    /**
     * 群组踢人
     * @param gid
     * @param uids
     * @param listener
     */
    void kickGroupUsers(long gid, String uids, OnGroupListener listener);

    /**
     * 解散群
     * @param groupId
     * @param listener
     */
    void deleteGroup(String groupId, OnGroupListener listener);

    /**
     * 添加群成员
     * @param gid
     * @param uids
     * @param listener
     */
    void addGroupUsers(long gid, String uids, OnGroupListener listener);

    /**
     * 申请入群
     * @param gid
     * @param intra
     * @param listener
     */
    void applyAddGroup(long gid, String intra, OnGroupListener listener);

    /**
     * 搜索群
     * @param gid
     * @param listener
     */
    void searchGroupById(long gid, OnGroupListener listener);

    /**
     * 加入聊天室
     * @param roomId
     * @param listener
     */
    void enterChatRoom(String roomId, OnGroupListener listener);

    /**
     * 退出聊天室
     * @param roomId
     * @param listener
     */
    void exitChatRoom(String roomId, OnGroupListener listener);

    /**
     * 修改群基础信息
     * @param gid
     * @param groupName 群名称
     * @param groupIntra 群简介
     * @param groupType 群类型,0 为临时群, 1为私密群, 2为公开群
     * @param listener
     */
    void modifyBasicGroupInfo(long gid, String groupName, String groupIntra, int groupType, OnGroupListener listener);

    /**
     *  修改群扩展信息
     * @param gid 群Id
     * @param uid 操作用户
     * @param mark 用户在群中的别名
     * @param isShowMark 是否显示别名 true 显示
     * @param groupMsgSetType 群消息提醒设置, Remind:提醒,NoPush:发送消息,但是不push,Ignore:完全不接受消息
     * @param listener
     */
    void modifyGroupExendInfo(long gid, String uid, String mark, boolean isShowMark, GroupEnum.GroupMsgSetType groupMsgSetType, OnGroupListener listener);

    /**
     *  修改群设置信息
     * @param gid
     * @param apply 新成员加入审核:yes 需要审核，no 不需要， not_allowed 不允许加入
     * @param auditType 成员加入审核方式
     * @param inviteRole 邀请权限：1 成员可邀请，2 vip之上成员可邀请，3 管理员之上成员可邀请，4 创建者可邀请
     * @param sayRole 发言权限：1 成员可发言，2 vip之上成员可发言，3 管理员之上成员可发言，4 创建者可发言
     * @param memberVisibile 成员可见性：0 所有人可见，1 成员可见，2 vip之上成员可见，3 管理员之上成员可见，4 创建者可见
     * @param infoVisibile 群资料可见性：0 所有人可见，1 成员可见，2 vip之上成员可见，3 管理员之上成员可见，4 创建者可见
     * @param isPass 群是否认证通过:yes 通过，no 不通过
     * @param infoEditRole 资料编辑权限：3 管理员，4 创建者.权限大于设置就可以进行对应的操作
     * @param gagRole 可以设置禁言的权限,3 管理员，4 创建者.权限大于设置就可以进行对应的操作
     * @param listener
     */
    void modifyGroupSettingInfo(long gid, GroupEnum.AddGroupApply apply, GroupEnum.MemberAuditType auditType,
                                int inviteRole, int sayRole, int memberVisibile, int infoVisibile,
                                GroupEnum.SwitchType isPass, int infoEditRole, int gagRole, OnGroupListener listener);

    /**
     * 修改群权限
     * @param gid
     * @param uid 操作人uid
     * @param role 角色，1－普通成员；2-VIP成员；3－管理员；4，群主
     * @param listener
     */
    void modifyGroupMemberRole(long gid, String uid, int role, OnGroupListener listener);

    /**
     * 修改聊天室权限
     * @param roomId
     * @param uid 操作人uid
     * @param role 角色，1－普通成员；3－管理员；4，群主
     * @param listener
     */
    void modifyChatRoomMemberRole(String roomId, String uid, String role, OnGroupListener listener);

    /**
     * 修改群等级
     * @param gid
     * @param level 群组要更新的等级（0：最大50人、1：100人、2：200人、3：500人）
     * @param listener
     */
    void modifyGroupLevel(long gid, int level, OnGroupListener listener);

    /**
     * 设置群组禁言
     * @param gid
     * @param uids 用户id,多个用户id以‘,’分割
     * @param gagTime 15 分钟
     * @param status true:禁言,false:不禁言
     * @param listener
     */
    void setGroupGag(String gid, String uids, long gagTime, boolean status, OnGroupListener listener);

    /**
     * 设置聊天室禁言
     * @param roomId
     * @param uids
     * @param gagTime
     * @param status
     * @param listener
     */
    void setChatRoomGag(String roomId, String uids, long gagTime, boolean status, OnGroupListener listener);

    /**
     * 查询群组禁言帐号
     * @param gid
     * @param listener
     */
    void getGroupGagUsers(long gid, OnGroupListener listener);

    /**
     * 查询聊天室所有禁言帐号
     * @param listener
     */
    void getChatRoomAllGagUsers(OnGroupListener listener);

}
