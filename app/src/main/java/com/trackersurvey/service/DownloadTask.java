package com.trackersurvey.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.trackersurvey.interfaces.DownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lenovo on 2017/9/14.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownloadListener listener;

    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;

    public DownloadTask(DownloadListener listener){
        // 下载状态需通过这个listener参数进行回调
        this.listener = listener;
    }

    /**
     * 用于在后台执行具体的下载逻辑
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;

        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File downloaded = new File(dir + "/微足迹.apk");
        if (downloaded.exists()) {
            downloaded.delete();
        }
        try {
            long downloadedLength = 0; // 记录已下载的文件长度
            // 首先从参数中获取到下载的URL地址
            String downloadUrl = params[0];
            // 根据URL地址解析出下载的文件名
            String fileName = "/微足迹.apk";
            // 指定下载到的目录：SD卡Download目录
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);
            Log.i("dongsiyaun", "doInBackground: " + file.getPath());
            // 判断要下载的文件是否已存在
            if (file.exists()){
                // 如果已存在就读取已下载的字节数，这样可以在后面启动断点续传功能
                downloadedLength = file.length();
            }
            // 调用getContentLength()方法来获取待下载文件的总长度
            long contentLength = getContentLength(downloadUrl);
            Log.i("DownloadTask", "contentLength:"+contentLength);
            // 如果待下载文件的总长度等于0，则说明文件有问题，直接返回TYPE_FAILED
            if (contentLength == 0){
                return TYPE_FAILED;
            }else if (contentLength == downloadedLength){
                // 已下载字节和文件总字节数相等，说明已经下载完成
                return TYPE_SUCCESS;
            }
            // 使用OkHttp发送一条网络请求
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    // 断点下载，指定从哪个字节开始下载
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            // 读取服务器响应的数据
            Response response = client.newCall(request).execute();
            if (response != null){
                // 使用java的文件流方式
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength); // 跳过已下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1){
                    if (isCanceled){
                        return TYPE_CANCELED;
                    }else if (isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total += len;
                        savedFile.write(b, 0, len);
                        // 计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null){
                    is.close();
                }
                if (savedFile != null){
                    savedFile.close();
                }
                if (isCanceled && file != null){
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    /**
     * 用于在界面上更新当前的下载进度
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    /**
     * 用于通知最终的下载结果
     * @param status
     */
    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }

    /**
     * 用于获取待下载文件的总长度
     * @param downloadUrl
     * @return
     * @throws IOException
     */
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }
}
