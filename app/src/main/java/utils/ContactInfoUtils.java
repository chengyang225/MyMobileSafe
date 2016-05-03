package utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChanIan on 16/5/3.
 */
public class ContactInfoUtils  {
    public class ContactInfo{
       public String name;
       public String phone;
    }
    public static List<ContactInfo> getContacts(Context context){
        List<ContactInfo> contactInfos=new ArrayList<>();

        //定义口令
        Uri uri=Uri.parse("content://com.android.contacts/raw_contacts");
        Uri datauri=Uri.parse("content://com.android.contacts/data");
        //获取内容解析者
        ContentResolver resolver = context.getContentResolver();
        //执行查询方法
        Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
        //取值
        while (cursor.moveToNext()){
            //创建通讯录对象
            ContactInfo contactInfo = new ContactInfoUtils().new ContactInfo();
            String contact_id = cursor.getString(0);
            if(contact_id!=null) {

                Cursor datacursor = resolver.query(datauri, new String[]{"data1", "mimetype"},
                        "raw_contact_id=?", new String[]{contact_id}, null);
               //获取联系人姓名和号码
                while (datacursor.moveToNext()){
                    String data1 = datacursor.getString(0);
                    String mimetype = datacursor.getString(1);
                    if("vnd.android.cursor.item/name".equals(mimetype)) {
                        contactInfo.name=data1;
                    }else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                        contactInfo.phone=data1;
                    }
                }
            }
            contactInfos.add(contactInfo);
        }
        return contactInfos;
    }
}
