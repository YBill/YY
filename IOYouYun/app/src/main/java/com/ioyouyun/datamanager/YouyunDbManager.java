package com.ioyouyun.datamanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ioyouyun.YouyunApplication;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.FileEntity;
import com.ioyouyun.chat.model.POIInfo;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.receivemsg.msg.MsgBodyTemplate;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.ParseJson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/6/21.
 */
public class YouyunDbManager {

    private static YouyunDbManager youyunDbManager;
    private YouyunDbHelper youyunDbHelper;

    private YouyunDbManager(Context context) {
        if (youyunDbHelper == null)
            youyunDbHelper = YouyunDbHelper.getInstance(context);
    }

    synchronized public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase database = youyunDbHelper.getReadableDatabase();
        return database;
    }

    synchronized public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database = youyunDbHelper.getWritableDatabase();
        return database;
    }

    public static YouyunDbManager getIntance() {
        if (youyunDbManager == null) {
            synchronized (YouyunDbManager.class) {
                if (youyunDbManager == null)
                    youyunDbManager = new YouyunDbManager(YouyunApplication.application);
            }
        }
        return youyunDbManager;
    }

    /**
     * 查询表是否存在
     *
     * @param tableName
     * @return
     */
    private boolean ifExistTable(String tableName) {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT 1 FROM sqlite_master WHERE type='table' and name = ?";
            cursor = db.rawQuery(sql, new String[]{tableName});
            if (cursor.moveToNext()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    /////////////////////////////////创建表//////////////////////////////////

    /**
     * 新建聊天表
     *
     * @param tableName
     * @return
     */
    synchronized private boolean createChatMsgTable(String tableName) {
        String sql = "CREATE TABLE " + tableName + "(" +
                YouyunDbHelper.MSG_ID + " VARCHAR PRIMARY KEY," +
                YouyunDbHelper.FROM_ID + " VARCHAR," +
                YouyunDbHelper.TO_ID + " VARCHAR," +
                YouyunDbHelper.NAME + " VARCHAR," +
                YouyunDbHelper.DATE + " VARCHAR," +
                YouyunDbHelper.TEXT + " VARCHAR," +
                YouyunDbHelper.AUDIOTIME + " VARCHAR," +
                YouyunDbHelper.IMGTHUMBNAIL + " VARCHAR," +
                YouyunDbHelper.ISSHOWTIME + " INTEGER," +
                YouyunDbHelper.AUDIO_IS_READ + " INTEGER," +
                YouyunDbHelper.MSGTYPE + " INTEGER," +
                YouyunDbHelper.CONVTYPE + " INTEGER" +
                ")";
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 新建最近联系人表
     *
     * @return
     */
    synchronized public boolean createRecentContactTable() {
        String tableName = YouyunDbHelper.RECENT_CONTACT_TABLE + FunctionUtil.uid;
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                YouyunDbHelper.FROM_ID + " VARCHAR PRIMARY KEY," +
                YouyunDbHelper.NAME + " VARCHAR," +
                YouyunDbHelper.DATE + " VARCHAR," +
                YouyunDbHelper.TEXT + " VARCHAR," +
                YouyunDbHelper.MSGTYPE + " INTEGER," +
                YouyunDbHelper.CONVTYPE + " INTEGER," +
                YouyunDbHelper.UNREAD_MSG_NUM + " INTEGER" +
                ")";
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /////////////////////////////////消息表//////////////////////////////////////

    /**
     * 插入IM消息
     *
     * @param entity
     * @param name
     * @return
     */
    synchronized public boolean insertChatMessage(ChatMsgEntity entity, String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            createChatMsgTable(tableName);
        }

        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        String sql = "insert into " + tableName +
                "(" + YouyunDbHelper.MSG_ID + "," +
                YouyunDbHelper.FROM_ID + "," +
                YouyunDbHelper.TO_ID + "," +
                YouyunDbHelper.NAME + "," +
                YouyunDbHelper.DATE + "," +
                YouyunDbHelper.TEXT + "," +
                YouyunDbHelper.AUDIOTIME + "," +
                YouyunDbHelper.IMGTHUMBNAIL + "," +
                YouyunDbHelper.ISSHOWTIME + "," +
                YouyunDbHelper.AUDIO_IS_READ + "," +
                YouyunDbHelper.MSGTYPE + "," +
                YouyunDbHelper.CONVTYPE +
                ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            db.execSQL(sql, new Object[]{entity.getMsgId(), entity.getFromId(), entity.getToId(), entity.getName(),
                    entity.getTimestamp(), entity.getText(), entity.getAudioTime(), entity.getImgThumbnail(),
                    entity.getShowTimeInt(entity), entity.getAudioReadInt(entity), entity.getMsgType(),
                    entity.getConvTypeInt(entity)});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 修改图片消息
     *
     * @param imMsg
     * @param msgId
     * @param name
     * @return
     *
     * @deprecated
     */
    synchronized public boolean updateChatImageMsg(String imMsg, String msgId, String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return false;
        }
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(YouyunDbHelper.IMGMSG, imMsg);
            String whereClause = YouyunDbHelper.MSG_ID + "=?";//修改条件
            String[] whereArgs = {msgId};//修改条件的参数
            int update = db.update(tableName, values, whereClause, whereArgs);
            if (update != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 修改显示时间
     *
     * @param entity
     * @param name
     * @return
     */
    synchronized public boolean updateShowTime(ChatMsgEntity entity, String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return false;
        }
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(YouyunDbHelper.ISSHOWTIME, entity.getShowTimeInt(entity));
            String whereClause = YouyunDbHelper.MSG_ID + "=?";//修改条件
            String[] whereArgs = {entity.getMsgId()};//修改条件的参数
            int update = db.update(tableName, values, whereClause, whereArgs);
            if (update != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 修改语音未读
     *
     * @param msgId
     * @param name
     * @return
     */
    synchronized public boolean updateAudioUnRead(String msgId, String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return false;
        }
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(YouyunDbHelper.AUDIO_IS_READ, 1);
            String whereClause = YouyunDbHelper.MSG_ID + "=?";//修改条件
            String[] whereArgs = {msgId};//修改条件的参数
            int update = db.update(tableName, values, whereClause, whereArgs);
            if (update != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 获取最后一条记录
     *
     * @param name
     * @return
     */
    public ChatMsgEntity getLastChatMsgEntity(String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return null;
        }
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + tableName + " order by " + YouyunDbHelper.DATE + " DESC limit 0,1";
        Cursor cursor = null;
        ChatMsgEntity entity = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                entity = new ChatMsgEntity();
                entity.setTimestamp(Long.parseLong(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE))));
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查询会话聊天
     *
     * @param name
     * @return
     */
    public List<ChatMsgEntity> getChatMsgEntityList(String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        List<ChatMsgEntity> lists = new ArrayList<>();
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return lists;
        }
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + tableName + " order by " + YouyunDbHelper.DATE + " ASC";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                ChatMsgEntity entity = new ChatMsgEntity();
                if(cursor.getColumnIndex(YouyunDbHelper.MSGTYPE) >= 0)
                    entity.setMsgType(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.MSGTYPE)));
                entity.setMsgId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.MSG_ID)));
                entity.setFromId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.FROM_ID)));
                entity.setToId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TO_ID)));
                entity.setName(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NAME)));
                entity.setTimestamp(Long.parseLong(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE))));
                if(ChatMsgEntity.CHAT_TYPE_SEND_POS == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_POS == entity.getMsgType()){
                    JSONObject obj = new JSONObject(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                    POIInfo poiInfo = ParseJson.parseJson2T(obj, POIInfo.class);
                    entity.setPoiInfo(poiInfo);
                } else
                    entity.setText(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                entity.setAudioTime(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.AUDIOTIME)));
                entity.setImgThumbnail(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.IMGTHUMBNAIL)));
                if(cursor.getColumnIndex(YouyunDbHelper.ISSHOWTIME) >= 0)
                    entity.setShowTime(entity.getShowTimeBoolean(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.ISSHOWTIME))));
                if(cursor.getColumnIndex(YouyunDbHelper.AUDIO_IS_READ) >= 0)
                    entity.setAudioRead(entity.getAudioReadBoolean(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.AUDIO_IS_READ))));
                if(cursor.getColumnIndex(YouyunDbHelper.CONVTYPE) >= 0)
                    entity.setConvType(entity.getConvTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.CONVTYPE))));

                if(ChatMsgEntity.CHAT_TYPE_SEND_IMAGE == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_IMAGE == entity.getMsgType()
                        || ChatMsgEntity.CHAT_TYPE_SEND_FILE == entity.getMsgType() || ChatMsgEntity.CHAT_TYPE_RECV_FILE == entity.getMsgType()){
                    FileEntity fileEntity = getFileInfoById(db, entity.getMsgId());
                    entity.setFileEntity(fileEntity);
                }
                lists.add(entity);
            }
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 删除聊天，这里直接把表删除了
     *
     * @param name
     * @return
     */
    synchronized public boolean removeChatImageMsg(String name) {
        String tableName = YouyunDbHelper.CHAT_MSG_TABLE + name;
        Logger.v("table:" + tableName);
        if (!ifExistTable(tableName)) {
            Logger.v(tableName + "不存在");
            return false;
        }
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            String sql = "DROP TABLE IF EXISTS " + tableName;
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /////////////////////////////////最近联系人表//////////////////////////////////////

    /**
     * 插入最近联系人
     *
     * @param entity
     * @return
     */
    synchronized public boolean insertRecentContact(ChatMsgEntity entity) {
        String tableName = YouyunDbHelper.RECENT_CONTACT_TABLE + FunctionUtil.uid;
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        // replace into 不存在插入，存在修改
        String sql = "replace into " + tableName +
                "(" + YouyunDbHelper.FROM_ID + "," +
                YouyunDbHelper.NAME + "," +
                YouyunDbHelper.DATE + "," +
                YouyunDbHelper.TEXT + "," +
                YouyunDbHelper.MSGTYPE + "," +
                YouyunDbHelper.CONVTYPE + "," +
                YouyunDbHelper.UNREAD_MSG_NUM +
                ") VALUES(?,?,?,?,?,?,?)";
        try {
            db.execSQL(sql, new Object[]{entity.getOppositeId(), entity.getName(), entity.getTimestamp(),
                    entity.getText(), entity.getMsgType(), entity.getConvTypeInt(entity),
                    entity.getUnreadMsgNum()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 修改联系人未读消息数,也可以用insertRecentContact(entity)修改
     *
     * @param oppositeId
     * @param number  未读消息数
     * @return
     */
    synchronized public boolean updateUnreadNumber(String oppositeId, int number) {
        String tableName = YouyunDbHelper.RECENT_CONTACT_TABLE + FunctionUtil.uid;
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(YouyunDbHelper.UNREAD_MSG_NUM, number);
            String whereClause = YouyunDbHelper.FROM_ID + "=?";//修改条件
            String[] whereArgs = {oppositeId};//修改条件的参数
            int update = db.update(tableName,
                    values, whereClause, whereArgs);
            if (update != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 根据用户Id查询最近联系人
     *
     * @param tid
     * @return
     */
    public ChatMsgEntity getRecentContactById(String tid) {
        String tableName = YouyunDbHelper.RECENT_CONTACT_TABLE + FunctionUtil.uid;
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + tableName
                + " WHERE " + YouyunDbHelper.FROM_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{tid});
            if (cursor.moveToNext()) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setOppositeId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.FROM_ID)));
                entity.setName(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NAME)));
                entity.setTimestamp(Long.parseLong(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE))));
                entity.setText(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                entity.setMsgType(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.MSGTYPE)));
                entity.setConvType(entity.getConvTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.CONVTYPE))));
                entity.setUnreadMsgNum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.UNREAD_MSG_NUM)));
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查询最近联系人
     *
     * @return
     */
    public List<ChatMsgEntity> getRecentContact() {
        String tableName = YouyunDbHelper.RECENT_CONTACT_TABLE + FunctionUtil.uid;
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + tableName + " order by " + YouyunDbHelper.DATE + " DESC";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            List<ChatMsgEntity> lists = new ArrayList<>();
            while (cursor.moveToNext()) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setOppositeId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.FROM_ID)));
                entity.setName(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NAME)));
                entity.setTimestamp(Long.parseLong(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE))));
                entity.setText(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                entity.setMsgType(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.MSGTYPE)));
                entity.setConvType(entity.getConvTypeEnum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.CONVTYPE))));
                entity.setUnreadMsgNum(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.UNREAD_MSG_NUM)));
                lists.add(entity);
            }
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 根据用户Id删除最近联系人
     *
     * @param tid
     * @return
     */
    public boolean deleteRecentContactById(String tid) {
        String tableName = YouyunDbHelper.RECENT_CONTACT_TABLE + FunctionUtil.uid;
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            String whereClause = YouyunDbHelper.FROM_ID + "=?";//条件
            String[] whereArgs = {tid};//条件的参数
            int result = db.delete(tableName, whereClause, whereArgs);
            if (result != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /////////////////////////////////通知表//////////////////////////////////////

    /**
     * 插入通知表信息
     *
     * @param entity
     * @return
     */
    synchronized public boolean insertNotify(MsgBodyTemplate entity) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        String sql = "replace into " + YouyunDbHelper.TABLE_NAME_NOTIFY +
                "(" + YouyunDbHelper.BUSINESS_ID + "," +
                YouyunDbHelper.NOTIFY_TYPE + "," +
                YouyunDbHelper.USERID + "," +
                YouyunDbHelper.USERNAME + "," +
                YouyunDbHelper.GROUPID + "," +
                YouyunDbHelper.GROUPNAME + "," +
                YouyunDbHelper.TEXT + "," +
                YouyunDbHelper.DATE + "," +
                YouyunDbHelper.ISLOOK + "," +
                YouyunDbHelper.ENTER_GROUP_TYPE +
                ") VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            db.execSQL(sql, new Object[]{entity.getBusiness_id(), entity.getType(), entity.getSource().getId(),
                    entity.getSource().getDesc(), entity.getObject().getId(), entity.getObject().getDesc(), entity.getExt().getMsg(),
                    entity.getDate(), entity.getIsLook(), 0});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 查询通知表列表
     *
     * @return
     */
    public List<MsgBodyTemplate> getNotifyList() {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + YouyunDbHelper.TABLE_NAME_NOTIFY;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            List<MsgBodyTemplate> lists = new ArrayList<>();
            while (cursor.moveToNext()) {
                MsgBodyTemplate entity = new MsgBodyTemplate();

                entity.setBusiness_id(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.BUSINESS_ID)));
                entity.setType(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NOTIFY_TYPE)));

                MsgBodyTemplate.SourceEntity sourceEntity = new MsgBodyTemplate.SourceEntity();
                sourceEntity.setId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.USERID)));
                sourceEntity.setDesc(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.USERNAME)));
                entity.setSource(sourceEntity);

                MsgBodyTemplate.ObjectEntity objectEntity = new MsgBodyTemplate.ObjectEntity();
                objectEntity.setId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.GROUPID)));
                objectEntity.setDesc(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.GROUPNAME)));
                entity.setObject(objectEntity);

                MsgBodyTemplate.ExtEntity extEntity = new MsgBodyTemplate.ExtEntity();
                extEntity.setMsg(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.TEXT)));
                entity.setExt(extEntity);

                entity.setDate(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.DATE)));
//                entity.set(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.ENTER_GROUP_TYPE)));
                lists.add(entity);
            }
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查询未读通知
     *
     * @return
     */
    public long getUnreadNotifyNum() {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + YouyunDbHelper.TABLE_NAME_NOTIFY
                + " WHERE " + YouyunDbHelper.ISLOOK + " = 1";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            long count = cursor.getLong(0);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * 清空通知未读数
     **/
    synchronized public boolean clearNotifyUnreadNumber() {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            String sql = "UPDATE " + YouyunDbHelper.TABLE_NAME_NOTIFY + " SET " + YouyunDbHelper.ISLOOK + "=0"
                    + " WHERE " + YouyunDbHelper.ISLOOK + "= 1";
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /////////////////////////////////用户表//////////////////////////////////////

    /**
     * 查询用户表
     * @param uid
     * @return
     */
    public UserInfoEntity getUserInfo(String uid) {
        SQLiteDatabase db = youyunDbManager.getReadableDatabase();
        String sql = "SELECT * FROM " + YouyunDbHelper.TABLE_NAME_USER_INFO + " WHERE " + YouyunDbHelper.USERID + " =?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{uid});
            if (cursor.moveToNext()) {
                UserInfoEntity entity = new UserInfoEntity();
                entity.setId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.USERID)));
                entity.setNickname(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.NICKNAME)));
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 插入用户信息
     *
     * @param entity
     * @return
     */
    synchronized public boolean insertUserInfo(UserInfoEntity entity) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        String sql = "insert into " + YouyunDbHelper.TABLE_NAME_USER_INFO +
                "(" + YouyunDbHelper.USERID + "," +
                YouyunDbHelper.NICKNAME +
                ") VALUES(?,?)";
        try {
            db.execSQL(sql, new Object[]{entity.getId(), entity.getNickname()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /////////////////////////////////文件信息表//////////////////////////////////////

    private FileEntity getFileInfoById(SQLiteDatabase db, String msgId){
        String sql = "SELECT * FROM " + YouyunDbHelper.TABLE_NAME_FILE_INFO
                + " WHERE " + YouyunDbHelper.MSG_ID + " = ?";
        FileEntity entity = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{msgId});
            if (cursor.moveToNext()) {
                entity = new FileEntity();
                entity.setFileId(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.FILE_ID)));
                entity.setFileLocal(cursor.getString(cursor.getColumnIndex(YouyunDbHelper.FILE_LOCAL_PATH)));
                entity.setFileLength(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.FILE_LENGTH)));
                entity.setPieceSize(cursor.getInt(cursor.getColumnIndex(YouyunDbHelper.PIECE_SIZE)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return entity;
    }

    /**
     * 插入文件信息
     *
     * @param msgId
     * @param entity
     * @return
     */
    synchronized public boolean insertFileInfo(String msgId, FileEntity entity) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        String sql = "insert into " + YouyunDbHelper.TABLE_NAME_FILE_INFO +
                "(" + YouyunDbHelper.MSG_ID + "," +
                YouyunDbHelper.FILE_ID + "," +
                YouyunDbHelper.FILE_LENGTH + "," +
                YouyunDbHelper.PIECE_SIZE + "," +
                YouyunDbHelper.FILE_LOCAL_PATH +
                ") VALUES(?,?,?,?,?)";
        try {
            db.execSQL(sql, new Object[]{msgId, entity.getFileId(), entity.getFileLength(),
                    entity.getPieceSize(), entity.getFileLocal()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 修改文件消息
     *
     * @param msgId
     * @param local
     * @return
     */
    synchronized public boolean updateChatFileInfo(String msgId, String local) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(YouyunDbHelper.FILE_LOCAL_PATH, local);
            String whereClause = YouyunDbHelper.MSG_ID + "=?";//修改条件
            String[] whereArgs = {msgId};//修改条件的参数
            int update = db.update(YouyunDbHelper.TABLE_NAME_FILE_INFO, values, whereClause, whereArgs);
            if (update != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 根据用户Id删除文件信息表
     *
     * @param msgId
     * @return
     */
    public boolean deleteFileInfoById(String msgId) {
        SQLiteDatabase db = youyunDbManager.getWritableDatabase();
        try {
            String whereClause = YouyunDbHelper.MSG_ID + "=?";//条件
            String[] whereArgs = {msgId};//条件的参数
            int result = db.delete(YouyunDbHelper.TABLE_NAME_FILE_INFO, whereClause, whereArgs);
            if (result != 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
