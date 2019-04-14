package io.github.neelkamath.goalninja;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

class MiniDeadline {
    private Date date;
    private ParseFile image;
    private transient ImageStatus imageStatus;
    private boolean isCompleted;
    private String note;
    private String text;

    interface GetDrawableCallback {
        void done(@Nullable Drawable drawable, @Nullable ParseException parseException);
    }

    enum ImageStatus {
        NOT_SET,
        NOT_RETRIEVABLE,
        RETRIEVABLE
    }

    MiniDeadline() {
        this.note = this.note == null ? "" : this.note;
    }

    String getText() {
        return this.text;
    }

    void setText(String str) {
        this.text = str;
    }

    Date getDate() {
        return this.date;
    }

    void setDate(Date date) {
        this.date = date;
    }

    String getNote() {
        return this.note;
    }

    void setNote(String str) {
        if (str == null) {
            str = "";
        }
        this.note = str;
    }

    void setImage(@Nullable Drawable drawable) {
        if (drawable == null) {
            this.image = null;
            this.imageStatus = ImageStatus.NOT_SET;
            return;
        }
        this.imageStatus = ImageStatus.NOT_RETRIEVABLE;
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ((BitmapDrawable) drawable).getBitmap().compress(CompressFormat.PNG, 100, byteArrayOutputStream);
        drawable = new ParseFile("image.png", byteArrayOutputStream.toByteArray());
        drawable.saveInBackground(new SaveCallback() {
            public void done(ParseException parseException) {
                if (parseException == null) {
                    MiniDeadline.this.image = drawable;
                    MiniDeadline.this.imageStatus = ImageStatus.RETRIEVABLE;
                    return;
                }
                Log.e("save", parseException.getMessage());
            }
        });
    }

    void getImage(final GetDrawableCallback getDrawableCallback, final Context context) {
        setImageStatus(context);
        switch (this.imageStatus) {
            case NOT_SET:
                getDrawableCallback.done(null, null);
                return;
            case NOT_RETRIEVABLE:
                return;
            default:
                this.image.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] bArr, ParseException parseException) {
                        getDrawableCallback.done(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bArr, 0, bArr.length)), parseException);
                    }
                });
                return;
        }
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public void setCompleted(boolean z) {
        this.isCompleted = z;
    }

    private void setImageStatus(Context context) {
        if (this.imageStatus == null) {
            if (this.image == null) {
                this.imageStatus = ImageStatus.NOT_SET;
            } else {
                this.imageStatus = Internet.isNotConnected(context) != null ? ImageStatus.NOT_RETRIEVABLE : ImageStatus.RETRIEVABLE;
            }
            this.imageStatus = this.image == null ? ImageStatus.NOT_SET : ImageStatus.RETRIEVABLE;
        }
    }

    ImageStatus getImageStatus(Context context) {
        setImageStatus(context);
        return this.imageStatus;
    }
}
