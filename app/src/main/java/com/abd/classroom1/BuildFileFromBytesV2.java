package com.abd.classroom1;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Abd on 3/11/2016.
 */
public class BuildFileFromBytesV2 {
    RandomAccessFile aFile = null;
    String[] fileRecivers;
    String saveDirectoryPath;

    public BuildFileFromBytesV2(String SaveDirectoryPath)  {

        /// set the directory for saving recived files.
        this.saveDirectoryPath=SaveDirectoryPath;
    }

    public boolean constructFile(FileChunkMessageV2 imMsg) throws IOException {

        if(imMsg.getChunkCounter()==(-1)){ //End OF Fle Packet
            Log.d("INFO", "FIle Should Be closed");

            try {
                aFile.close();
            } catch (Exception ex) {
                Log.d("INFO", "File Already closed");
                return true;
            }

            return true;  /// return true if the file completed
        }
        if (imMsg.getChunkCounter() == 1L) {
            File folders = new File(saveDirectoryPath);

            if (!(folders.exists())) {
                folders.mkdirs();
            }


            aFile = new RandomAccessFile(saveDirectoryPath+imMsg.getFileName(), "rw");
            System.out.println("New File Created");
            aFile.write(imMsg.getChunk());
            fileRecivers = imMsg.getRecivers();
        }else{// File data PAcket
            //  System.out.println("Normal packet =" + Long.toString(imMsg.getChunkCounter()));

            aFile.write(imMsg.getChunk());

        }
        return  false;
    }

}
