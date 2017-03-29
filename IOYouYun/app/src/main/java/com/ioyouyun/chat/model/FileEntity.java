package com.ioyouyun.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 卫彪 on 2016/10/17.
 */

public class FileEntity implements Parcelable{

    private String fileId;
    private int fileLength;
    private int pieceSize;
    private String fileLocal;

    public FileEntity(){

    }

    public FileEntity(String fileId, int fileLength, int pieceSize, String fileLocal) {
        this.fileId = fileId;
        this.fileLength = fileLength;
        this.pieceSize = pieceSize;
        this.fileLocal = fileLocal;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    public void setPieceSize(int pieceSize) {
        this.pieceSize = pieceSize;
    }

    public String getFileLocal() {
        return fileLocal;
    }

    public void setFileLocal(String fileLocal) {
        this.fileLocal = fileLocal;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileId);
        dest.writeInt(fileLength);
        dest.writeInt(pieceSize);
        dest.writeString(fileLocal);
    }

    protected FileEntity(Parcel in) {
        fileId = in.readString();
        fileLength = in.readInt();
        pieceSize = in.readInt();
        fileLocal = in.readString();
    }

    public static final Creator<FileEntity> CREATOR = new Creator<FileEntity>() {
        @Override
        public FileEntity createFromParcel(Parcel in) {
            return new FileEntity(in);
        }

        @Override
        public FileEntity[] newArray(int size) {
            return new FileEntity[size];
        }
    };
}
