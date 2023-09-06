package com.medhelp.callmed2.utils.WorkTofFile.show_file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.medhelp.callmed2.utils.timber_log.LoggingTree;
import com.rx2androidnetworking.Rx2AndroidNetworking;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class
LoadFile {

    public static final String NAME_FOLDER_APP ="MedHelperDocuments";  //корневая папка app для хранения
    public static final String NAME_FOLDER_ANALISE="Analise";  //результаты анализов
    public static final String NAME_FOLDER_CHAT="Image";  //прочие картинки
    public static final String NAME_FOLDER_CAMERA ="Camera"; //фото с камеры
    public static final String NAME_FOLDER_DOCUMENTS ="Documents"; //для отправляемых принимаемых документов

    private final String NULLL="nulll";

    private String type;
    private String nameNewFile;
    private String path;
    private String token;
    private LoadFileListener listener;
    private View view;
    private Context context;
    private String dir;

    public LoadFile(Context context, String type, String nameNewFile, String path, String token, View view, LoadFileListener listener )
    {
        this.context=context;
        this.type=type;
        this.nameNewFile=nameNewFile;
        this.path=path;
        this.token=token;
        this.listener=listener;
        this.view=view;
        dir=getDir();

        testDeleteFile(dir);

        load(path+"&token="+token,dir,this.nameNewFile);
    }

    private void testDeleteFile(String dir)
    {
        File file=new File(dir+"/"+nameNewFile);

        if(file.exists())
        {
            file.delete();
        }

        File cacheFile[] =context.getCacheDir().listFiles();

        for(File f : cacheFile)
        {

            if(f.getName().contains(nameNewFile))
            {
                f.delete();
            }
        }
    }


    private String getDir()
    {
        File pathToDownloadFolder= context.getCacheDir();

        File pathToFolderApp=new File(pathToDownloadFolder, NAME_FOLDER_APP);
        if(!pathToFolderApp.exists())
        {
            pathToFolderApp.mkdir();
        }

        if(type.equals(ShowFile2.TYPE_ICO))
        {
            return context.getCacheDir().getAbsolutePath();
        }

        if(type.equals(ShowFile2.TYPE_IMAGE))
        {
            File pathToFolder=new File(pathToFolderApp,NAME_FOLDER_CHAT);
            if(!pathToFolder.exists())
            {
                pathToFolder.mkdir();
            }

            return pathToFolder.getAbsolutePath();
        }

        if(type.equals(ShowFile2.TYPE_PDF))
        {
            File pathToFolder=new File(pathToFolderApp,NAME_FOLDER_ANALISE);
            if(!pathToFolder.exists())
            {
                pathToFolder.mkdir();
            }

            return pathToFolder.getAbsolutePath();
        }

        return "";
    }

    private void load(String path, String dirPath, String fileName)
    {
        if(fileName.equals(""))
        {
            //Timber.e("load, fileName for save empty");
            listener.error("load, fileName for save empty");
            return;
        }

        CompositeDisposable cd = new CompositeDisposable();
        cd.add(LoadImage(path, dirPath,fileName)
                .concatMapSingle(resp ->{
                    if(type==ShowFile2.TYPE_ICO  || type==ShowFile2.TYPE_IMAGE) {
                        return convertingToImg(dirPath, fileName);
                    }
                    else
                        return convertingToPdf(dirPath, fileName);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response ->
                                listener.success(response)
                        , throwable ->
                        {
                            if (throwable instanceof ANError) {
                                ANError anError = (ANError) throwable;
                                if(anError.getErrorCode()==404)
                                {
                                    Timber.e(LoggingTree.getMessageForError(null,"LoadFile/load/1 error 404 "+fileName));
                                }
                                else
                                {
                                    Timber.e(LoggingTree.getMessageForError(null,"LoadFile/load/2 "+((ANError) throwable).getErrorBody()+"  path= "+path));
                                }
                            }
                            else
                            {
                                Timber.e(LoggingTree.getMessageForError(null, "LoadFile/load/3 "+throwable.getMessage()+"  path= "+path+" dirPath "+dirPath+" fileName "+fileName));
                            }

                            deleteFile(dirPath,  fileName);

                            if (throwable.getMessage()!=null && throwable.getMessage().equals(NULLL)) {
                                listener.error(throwable.toString());
                                return;
                            }

                            listener.error(throwable.getMessage());
                        }
                )
        );
    }

    private void deleteFile(String dirPath, String fileName)
    {
        File file=new File(dirPath,fileName);

        if(file.exists())
            file.delete();
    }

    Observable<String> LoadImage(String path, String dirPath, String fileName) {
        return Rx2AndroidNetworking.download(path, dirPath, fileName)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                    }
                })
                .getDownloadObservable();

    }

    Single<List<File>> convertingToImg(String dirPath, String fileName)
    {
        //Log.wtf("mLog", "start convertingToImg");

         File nFile=new File(dirPath,fileName);

        if(!nFile.exists())
        {
            return Single.error(new Throwable("convertingToImg: Файла не существует"));
        }

        String str= gerStringFromFile(nFile.getAbsolutePath());

        Bitmap bitmap= StringBase64ToBitMap(str);
        if(bitmap==null)
            return Single.error(new Throwable(NULLL));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();

        //очистка файла
        PrintWriter writer;
        try {
            writer = new PrintWriter(nFile);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            Timber.e(LoggingTree.getMessageForError(e,"LoadFile/convertingToImg/1 "));
            return Single.error(e);
        }

        //write the bytes in file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(nFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Timber.e(LoggingTree.getMessageForError(e,"LoadFile/convertingToImg/2 "));
            return Single.error(e);
        } catch (IOException e) {
            Timber.e(LoggingTree.getMessageForError(e,"LoadFile/convertingToImg/3 "));
            return Single.error(e);
        }

        File sizeFile=null;
        if(view!=null)
        {
            sizeFile=creteNewFileForViewSize(nFile, view, bitmap);
        }

        List<File> list=new ArrayList<>();
        list.add(nFile);

        if(sizeFile!=null)
            list.add(sizeFile);

        return Single.just(list);
    }

    private File creteNewFileForViewSize(File mainFail, View view ,Bitmap bitmap)
    {
        int mainWith = bitmap.getWidth();
        int mainHeight = bitmap.getHeight();

        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        if(width!=0  && height==0)
            height=width;

        if (width < mainWith || height < mainHeight) {

            List<Integer> sizeList= SearchLoadFile.calculationOfProportionsBitmap(mainWith,mainHeight,width,height);

            if(mainWith<=0  ||  mainHeight<=0  ||  sizeList.get(0)<=0  || sizeList.get(1)<=0)
            {
                Timber.e(LoggingTree.getMessageForError(null,"LoadFile/creteNewFileForViewSize/3  mainWith="+mainWith+" mainHeight "+mainHeight+" sizeList.get(0) "+sizeList.get(0)+" sizeList.get(1) "+sizeList.get(1)));
                return mainFail;
            }

            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, sizeList.get(0), sizeList.get(1), false);

            File newFile = new File(context.getCacheDir().getAbsolutePath(), "&" + width + "&" + height + "&" + mainFail.getName());
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
                Timber.e(LoggingTree.getMessageForError(e,"LoadFile/creteNewFileForViewSize/1 "));
            } catch (IOException e) {
                Timber.e(LoggingTree.getMessageForError(e,"LoadFile/creteNewFileForViewSize/2 "));
            }
            return newFile;
        }
        return null;
    }


    Single<List<File>> convertingToPdf(String dirPath, String fileName)
    {
        File nFile=new File(dirPath,fileName);

        if(!nFile.exists())
        {
            return Single.error(new Throwable("convertingToPdf: Файла не существует"));
        }

        String str= gerStringFromFile(nFile.getAbsolutePath());

        //очистка файла
        PrintWriter writer;
        try {
            writer = new PrintWriter(nFile);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            Timber.e(LoggingTree.getMessageForError(e,"LoadFile/convertingToPdf/1 "));
            return Single.error(e);
        }

        byte[] pdfAsBytes = Base64.decode(str, 0);

        //write the bytes in file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(nFile, false);
            fos.write(pdfAsBytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Timber.e(LoggingTree.getMessageForError(e,"LoadFile/convertingToPdf/2 "));
            return Single.error(e);
        } catch (IOException e) {
            Timber.e(LoggingTree.getMessageForError(e,"LoadFile/convertingToPdf/3 "));
            return Single.error(e);
        }

        List<File> list=new ArrayList<>();
        list.add(nFile);

        return Single.just(list);
    }


    public Bitmap StringBase64ToBitMap(String encodedString) {
        //Log.wtf("mLog", "start StringBase64ToBitMap");
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            Timber.e(LoggingTree.getMessageForError(e,"LoadFile/StringBase64ToBitMap "));
            listener.error(e.getMessage());
            return null;
        }
    }


    private String gerStringFromFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            Timber.e(LoggingTree.getMessageForError(e,"LoadFile/gerStringFromFile "));
            listener.error(e.getMessage());
            return null;
        }
        return contentBuilder.toString();
    }



        public interface LoadFileListener{
        void success(List<File> img);
        void error(String err);
    }

}
