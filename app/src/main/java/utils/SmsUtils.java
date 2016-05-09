package utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ChanIan on 16/5/7.
 */
public class SmsUtils {
    public interface MyCallBack{
        public void beforeBackUp(int max);
        public void afterBackUp(int progress);
    }
    public static boolean backupSms(Context context, String fileName,MyCallBack callBack) {
        //得到内容解析者
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse("content://sms"), new String[]{"address", "date", "body", "type"}, null
                , null, null);
        //创建文件
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //创建序列化器,使用xml文件备份短信
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "info");
            int count = cursor.getCount();
            int process=0;
            callBack.beforeBackUp(count);
            while (cursor.moveToNext()) {
                callBack.afterBackUp(process++);

                serializer.startTag(null, "sms");

                serializer.startTag(null, "address");
                String address = cursor.getString(0);
                serializer.text(address);
                serializer.endTag(null, "address");


                serializer.startTag(null, "date");
                String date = cursor.getString(1);
                serializer.text(date);
                serializer.endTag(null, "date");

                serializer.startTag(null, "body");
                String body = cursor.getString(2);
                serializer.text(body);
                serializer.endTag(null, "body");

                serializer.startTag(null, "type");
                String type = cursor.getString(3);
                serializer.text(type);
                serializer.endTag(null, "type");

                serializer.endTag(null, "sms");
                Thread.sleep(2000);
            }
            serializer.endTag(null, "info");
            serializer.endDocument();
            cursor.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
