package com.medhelp.callmed2.utils.WorkTofFile.show_file;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShowFile2 {
    public static final String TYPE_ICO = "TYPE_ICO";
    public static final String TYPE_IMAGE = "TYPE_IMAGE";
    public static final String TYPE_PDF="TYPE_PDF";

    public static final int FILE_NO_CREATE = 0;
    public static final int FILE_LOADING = 1;
    public static final int FILE_COMPLETE = 2;
    public static final int FILE_ERROR = 3;

    public static  List<DataList> loadFile = new ArrayList<>();

    public static List<BuilderImage> listBilderImage = new ArrayList<>();

    private BuilderImage builderImage;
    private BuilderImageFile builderImageFile;
    private BuilderPdf builderPdf;


    public ShowFile2()
    {

    }

    private ShowFile2(BuilderImage builder) {
        if (builder != null) {
            this.builderImage = builder;
            clearFromListImageBuilder(builder.inserView);

            //иначе ConcurrentModificationException
            List<BuilderImage> listBI = new ArrayList<>();
            listBI.add(builder);
            listBilderImage.addAll(listBI);

            ((ImageView)builder.inserView).setImageResource(0);

            processingBuilderImage();
        }
    }

    private ShowFile2(BuilderImageFile builder) {
        if (builder != null) {
            this.builderImageFile = builder;
            clearFromListImageBuilder(builder.inserView);

            new SearchLoadFile(builder.context, builder.loadFile, builder.inserView, new SearchLoadFile.SearchListener() {
                @Override
                public void success(File img) {
                    showImage(img);
                }

                @Override
                public void successAddToList(File img) {

                }

                @Override
                public void error(String err) {
                    builder.listener.error(err);
                }
            });
        }
    }

    private ShowFile2(BuilderPdf builder) {
        if (builder != null) {
            this.builderPdf = builder;
            processingBuilderImage();
        }
    }

    public void clearLoadList()
    {
        loadFile = new ArrayList<>();
        listBilderImage = new ArrayList<>();
    }

    //region processing
    private void processingBuilderImage()
    {
            final String nameFile;
            int numItemInList;
            int tmp;

            if(builderImage!=null) {
                nameFile = trancatePathToAName(builderImage.path);
            }
            else
            {
                nameFile= trancatePathToAName(builderPdf.path);
            }

            numItemInList = searchNameInList(nameFile);
            tmp = getStatusItemList(numItemInList);

            switch (tmp) {
                case FILE_NO_CREATE:
                    if(builderImage!=null) {
                        loadFile.add(new DataList(nameFile, FILE_LOADING));

                        builderImage.inserView.post(() -> createFileRun(nameFile));
                    } else
                    {
                        createFileRun(nameFile);
                    }
                    break;

                case FILE_COMPLETE:
                    if(builderImage!=null) {
                        builderImage.inserView.post(() -> searchFileRun(nameFile, numItemInList));
                    }
                    else
                    {
                        searchFileRun(nameFile, numItemInList);
                    }

                    break;

                case FILE_LOADING:
                    loadFile.get(numItemInList).setListener(() -> processingBuilderImage());
                    break;

                case FILE_ERROR:
                    showError(loadFile.get(numItemInList).getError());
                    break;
            }
    }

    private void createFileRun(final String nameFile)
    {
        if(builderImage!=null) {
            new LoadFile(builderImage.context, builderImage.typeFile, nameFile, builderImage.path, builderImage.token, builderImage.inserView, new LoadFile.LoadFileListener() {
                @Override
                public void success(List<File> img) {
                    refreshStatusInListToComplete(img);
                    showImage(img.get(img.size() - 1));
                }

                @Override
                public void error(String err) {
                    refreshStatusInListToError(nameFile, err);
                    showError(err);
                }
            });
        }
        else
        {
            new LoadFile(builderPdf.context, TYPE_PDF, nameFile, builderPdf.path, builderPdf.token,null, new LoadFile.LoadFileListener() {
                @Override
                public void success(List<File> img) {
                    refreshStatusInListToComplete(img);
                    builderPdf.listener.complete(img.get(img.size() - 1));
                }

                @Override
                public void error(String err) {
                    refreshStatusInListToError(nameFile, err);
                    showError(err);
                }
            });

           // nameFile= trancatePathToAName(builderPdf.path);
        }


    }

    private void searchFileRun(String nameFile, int numItemInList)
    {
        new SearchLoadFile(builderImage.context, builderImage.typeFile, nameFile, builderImage.inserView, new SearchLoadFile.SearchListener() {
            @Override
            public void success(File img) {
                showImage(img);
            }

            @Override
            public void successAddToList(File img) {
                loadFile.get(numItemInList).getName().add(img.getName());
                showImage(img);
            }

            @Override
            public void error(String err) {
                if (!err.equals(SearchLoadFile.REFRESH_LINK)) {
                    showError(err);
                } else {
                    deleteFromList(nameFile);
                    processingBuilderImage();
                }
            }
        });
    }
//endregion



    //region utils
    private void deleteFromList(String fName)
    {
        for (DataList dl : loadFile)
        {
            if(dl.getName().get(0).equals(fName))
            {
                loadFile.remove(dl);
                return;
            }
        }
    }

    private void showError(String err)
    {
        if(builderImage!=null) {
            if(builderImage.inserView!=null & builderImage.errorDrawable!=0)
            {
                ((ImageView) builderImage.inserView).setImageResource(builderImage.errorDrawable);
            }

            builderImage.listener.error(err);
        }
        else
        {
            builderPdf.listener.error(err);
        }


    }

    private void showImage(File img)
    {
        if(builderImage!=null) {
            if (builderImage.inserView != null) {
                ((ImageView) builderImage.inserView).setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
            }

            builderImage.listener.complete(img);
        }
        else
        {
            if (builderImageFile.inserView != null) {
                ((ImageView) builderImageFile.inserView).setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
            }

            builderImageFile.listener.complete(img);
        }
    }

    private void refreshStatusInListToComplete(List<File> name)
    {
        for (DataList dl : loadFile)
        {
            if(dl.getName().get(0).equals(name.get(0).getName()))
            {
                dl.setStatus(FILE_COMPLETE);

                if(name.size()>1)
                {
                    dl.getName().add(name.get(1).getName());
                }
                break;
            }

        }
    }

    private void refreshStatusInListToError(String name, String err)
    {
        for (DataList dl : loadFile)
        {
            if(dl.getName().get(0).equals(name)) {
                dl.setStatus(FILE_ERROR);
                dl.setError(err);
            }
        }
    }

    private void refreshStatusInListToComplete(String name)
    {
        for (DataList dl : loadFile)
        {
            if(dl.getName().get(0).equals(name))
                dl.setStatus(FILE_COMPLETE);
        }
    }

    private int getStatusItemList(int numItem) {
        if (numItem == -1) {
            return FILE_NO_CREATE;
        } else
            return loadFile.get(numItem).getStatus();
    }

    private int searchNameInList(String name) {
        for (int i = 0; i < loadFile.size(); i++) {
            if (loadFile.get(i).getName().get(0).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private String trancatePathToAName(String name) {
        int startTrancate = name.indexOf("path=") + 5;
        int entTarancate = name.length();
        return name.substring(startTrancate, entTarancate);
    }

    private void clearFromListImageBuilder(View view) {
        if (listBilderImage.size() == 0)
            return;
        //иначе ConcurrentModificationException
        for(Iterator<BuilderImage> it = listBilderImage.iterator(); it.hasNext();) {
            BuilderImage element = it.next();
            if (element.inserView == view) {
                element.inserView=null;
                it.remove();
            }

        }
    }
    //endregion



    public interface ShowListener {
        void complete(File file);
        void error(String error);
    }



    public static class BuilderPdf {
        private Context context;
        private String token;
        private String path;
        private ShowListener listener;

        public BuilderPdf(Context context) {
            this.context = context;
        }

        public BuilderPdf token(String token) {
            this.token = token;
            return this;
        }

        public BuilderPdf load(String path) {
            this.path = path;
            return this;
        }


        public BuilderPdf setListener(ShowListener listener) {
            this.listener = listener;
            return this;
        }

        public ShowFile2 build() {
            if (testData())
                return new ShowFile2(this);
            else
                return null;
        }

        private Boolean testData() {
            if (context == null) {
                listener.error("input date: context==null");
                return false;
            }

            if (token == null || token.equals("")) {
                listener.error("input date: error token");
                return false;
            }

            if (path == null || path.equals("")) {
                listener.error("input date: error path");
                return false;
            }

            return true;
        }

    }


    public static class BuilderImageFile {
        private Context context;
        private File loadFile;
        private View inserView;
        private ShowListener listener;

        public BuilderImageFile(Context context) {
            this.context = context;
        }

        public BuilderImageFile load(File path) {
            this.loadFile = path;
            return this;
        }

        public BuilderImageFile into(View view) {
            inserView = view;
            return this;
        }

        public BuilderImageFile setListener(ShowListener listener) {
            this.listener = listener;
            return this;
        }

        public ShowFile2 build() {
            if (testData())
                return new ShowFile2(this);
            else
                return null;
        }

        private Boolean testData() {
            if (context == null) {
                listener.error("input date: context==null");
                return false;
            }

            if (loadFile == null) {
                listener.error("input date: path to file error");
                return false;
            }

            if (inserView == null) {
                listener.error("input date: inserView==null");
                return false;
            }
            return true;
        }

    }


    public static class BuilderImage {

        private Context context;
        private String typeFile;
        private String token;
        private String path;
        private View inserView;
        private int errorDrawable;
        private ShowListener listener;

        public BuilderImage(Context context) {
            this.context = context;
        }

        public BuilderImage setType(String type) {
            this.typeFile = type;
            return this;
        }

        public BuilderImage token(String token) {
            this.token = token;
            return this;
        }

        public BuilderImage load(String path) {
            this.path = path;
            return this;
        }

        public BuilderImage into(View view) {
            inserView = view;
            return this;
        }

        public BuilderImage imgError(int drawable) {
            errorDrawable = drawable;
            return this;
        }

        public BuilderImage setListener(ShowListener listener) {
            this.listener = listener;
            return this;
        }

        public ShowFile2 build() {
            if (testData())
                return new ShowFile2(this);
            else
                return null;
        }

        private Boolean testData() {
            if (context == null) {
                listener.error("input date: context==null");
                return false;
            }

            if (typeFile == null || !(typeFile.equals(ShowFile2.TYPE_ICO) || typeFile.equals(ShowFile2.TYPE_IMAGE))) {
                listener.error("input date: type file error");
                return false;
            }

            if (token == null || token.equals("")) {
                listener.error("input date: error token");
                return false;
            }

            if (path == null || path.equals("")) {
                listener.error("input date: error path");
                return false;
            }

            if (inserView == null) {
                listener.error("input date: inserView==null");
                return false;
            }
            return true;
        }
    }

}
