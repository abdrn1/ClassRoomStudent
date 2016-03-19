package com.abd.classroom1;

import android.util.Log;

import com.esotericsoftware.kryonet.Client;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

/**
 * Created by Abd on 3/7/2016.
 */
public class SendUtil {

    public static void readAndSendFile(String path, Client client, UserLogin currentUser, String[] recivers) throws IOException {

        Log.d("INFO FIle Name", FilenameUtils.getName(path));
        Log.d("INFO", "CONVERT TO ARRA");
        FileChunkMessageV2 fmsg = new FileChunkMessageV2();
        fmsg.setSenderID(currentUser.getUserID());
        fmsg.setSenderName(currentUser.getUserName());
        fmsg.setFileName(FilenameUtils.getName(path));
        fmsg.setRecivers(recivers);
        FileSenderThreadV2 ftV2 = new FileSenderThreadV2(client, path, fmsg);
        ftV2.start();
        Log.d("INFO", "Start Thread");
    }

    public static boolean checkIfFileIsImage(String fileName) {

        String ext = null;
        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < (fileName.length() - 1)) {
            ext = fileName.substring(i + 1).toLowerCase();
        }
        if (ext == null)
            return false;
        else if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") && !ext.equals("gif"))
            return false;
        else
            return true;
    }


}
