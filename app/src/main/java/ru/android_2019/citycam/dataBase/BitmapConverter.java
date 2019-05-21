package ru.android_2019.citycam.dataBase;

import android.arch.persistence.room.TypeConverter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapConverter {

    @TypeConverter
    Bitmap convertToBitmap(byte[] array) {
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    @TypeConverter
    byte[] convertFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }
}
