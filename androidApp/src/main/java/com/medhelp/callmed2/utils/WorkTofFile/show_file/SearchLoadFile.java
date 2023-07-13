package com.medhelp.callmed2.utils.WorkTofFile.show_file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.medhelp.callmed2.utils.timber_log.LoggingTree;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class SearchLoadFile {
    public static final String REFRESH_LINK="REFRESH_LINK";

    private Context context;
    private String type;

    private String fileName;
    private File file;

    private SearchListener listener;
    private View view;

    public SearchLoadFile(Context context, String type, String fileName,View view,SearchListener listener)
    {
        this.context=context;
        this.type=type;
        this.fileName=fileName;
        this.listener=listener;
        this.view=view;

        File fileDir=getDir();
        if(fileDir==null)
        {
            listener.error("SearchLoadFile fileDir == null");
            return;
        }

        File f=null;
        if(type.equals(ShowFile2.TYPE_ICO)  || type.equals(ShowFile2.TYPE_IMAGE))
        {
           f= searchToCache(fileName);
        }

        if(f==null)
            searchFileInFileDir(fileDir);
        else
        {
            listener.success(f);
        }

    }


    public SearchLoadFile(Context context, File file,View view,SearchListener listener)
    {
        this.context=context;
        this.file=file;
        this.listener=listener;
        this.view=view;

        File f=searchToCacheForWithFile(file.getName());


        if(f==null) {
            listener.success(creteNewFileForViewSize(file, view, ((ImageView)view).getMaxWidth(),((ImageView)view).getMaxHeight()));
        }
        else
        {
            listener.success(f);
        }

    }


    private File getDir()
    {
        File pathToDownloadFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File pathToFolderApp=new File(pathToDownloadFolder,LoadFile.NAME_FOLDER_APP);
        if(!pathToFolderApp.exists())
        {
            pathToFolderApp.mkdir();
        }

        if(type.equals(ShowFile2.TYPE_ICO))
        {
            return context.getCacheDir();
        }

        if(type.equals(ShowFile2.TYPE_IMAGE))
        {
            File pathToFolder=new File(pathToFolderApp,LoadFile.NAME_FOLDER_CHAT);
            if(!pathToFolder.exists())
            {
                pathToFolder.mkdir();
            }

            return pathToFolder;
        }

        if(type.equals(ShowFile2.TYPE_PDF))
        {
            File pathToFolder=new File(pathToFolderApp,LoadFile.NAME_FOLDER_ANALISE);
            if(!pathToFolder.exists())
            {
                pathToFolder.mkdir();
            }

            return pathToFolder;
        }

        return null;
    }


    private File searchToCacheForWithFile(String fileName)
    {
        int width = ((ImageView)view).getMaxWidth();
        int height = ((ImageView)view).getMaxHeight();

        String allName="&"+width+"&"+height+"&"+fileName;

        File cachePath[] =context.getCacheDir().listFiles();

        for(File f : cachePath)
        {

            if(f.getName().equals(allName))
            {
                return f;
            }
        }

        return null;
    }

    private File searchToCache(String fileName)
    {
        if(view==null)
            return null;

        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        String allName="&"+width+"&"+height+"&"+fileName;

        File cachePath[] =context.getCacheDir().listFiles();

        for(File f : cachePath)
        {

            if(f.getName().equals(allName))
            {
                return f;
            }
        }

        return null;
    }


    private void searchFileInFileDir(File file)
    {
        File f[]= file.listFiles();

        if(f.length<=0)
        {
            listener.error(REFRESH_LINK);
            return;
        }

        for(File tmp  :  f)
        {
            String ddd=tmp.getName();
            if(tmp.getName().equals(fileName))
            {
                if(view==null)
                    return;

                listener.successAddToList(creteNewFileForViewSize(tmp,view,view.getMeasuredWidth(),view.getMeasuredHeight()));
                return;
            }
        }

        listener.error(REFRESH_LINK);
    }


    private File creteNewFileForViewSize(File mainFail, View view, int width, int height)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(mainFail.getAbsolutePath());

        int mainWith = bitmap.getWidth();
        int mainHeight = bitmap.getHeight();

        if(width!=0  && height==0)
            height=width;

        if (width < mainWith || height < mainHeight) {

            List<Integer> sizeList=calculationOfProportionsBitmap(mainWith,mainHeight,width,height);

            if(mainWith<=0  ||  mainHeight<=0  ||  sizeList.get(0)<=0  || sizeList.get(1)<=0)
            {
                Timber.e(LoggingTree.getMessageForError(null, "SearchLoadFile/creteNewFileForViewSize/4  mainWith="+mainWith+" mainHeight "+mainHeight+" sizeList.get(0) "+sizeList.get(0)+" sizeList.get(1) "+sizeList.get(1)));
                return mainFail;
            }

            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, sizeList.get(0), sizeList.get(1), false);

            File newFile = new File(context.getCacheDir().getAbsolutePath(), "&" + width + "&" + height + "&" + mainFail.getName());
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                Timber.e(LoggingTree.getMessageForError(e,"SearchLoadFile/creteNewFileForViewSize/1 "));
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(newFile);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                Timber.e(LoggingTree.getMessageForError(e,"SearchLoadFile/creteNewFileForViewSize/2 "));
            } catch (IOException e) {
                Timber.e(LoggingTree.getMessageForError(e,"SearchLoadFile/creteNewFileForViewSize/3 "));
            }

            return newFile;
        }

        return mainFail;
    }

    public static List<Integer> calculationOfProportionsBitmap(int wBitmap, int hBitmap, int wView, int hView)
    {
        int newWith;
        int newHeight;

        if(wBitmap>hBitmap)
        {
            newWith=wView;
            newHeight=(newWith*hBitmap)/wBitmap;
        }
        else
        {
            newHeight=hView;
            newWith=(newHeight*wBitmap)/hBitmap;
        }

        if(newWith<=wView  &&  newHeight<=hView)
            return Arrays.asList(newWith, newHeight);

        if(!(wBitmap>hBitmap))
        {
            newWith=wView;
            newHeight=(newWith*hBitmap)/wBitmap;
        }
        else
        {
            newHeight=hView;
            newWith=(newHeight*wBitmap)/hBitmap;
        }


        if(newWith<=wView  &&  newHeight<=hView)
            return Arrays.asList(newWith, newHeight);
        else
            return null;

    }


    public interface SearchListener{
        void success(File img);
        void successAddToList(File img);
        void error(String err);
    }
}
