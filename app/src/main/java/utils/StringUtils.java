package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ChanIan on 16/4/30.
 */
public class StringUtils {
    public static String getJsonString(InputStream stream){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len=stream.read(buffer))!=-1){
                os.write(buffer,0,len);
            }
            stream.close();
            return os.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
