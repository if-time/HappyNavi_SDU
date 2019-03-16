package com.trackersurvey.interfaces;

/**
 * 回调接口，用于对下载过程中的各种状态进行监听和回调
 * Created by lenovo on 2017/9/13.
 */

public interface DownloadListener {

    // 通知当前的下载进度
    void onProgress(int progress);

    // 通知下载成功事件
    void onSuccess();

    // 通知下载失败事件
    void onFailed();

    // 通知下载暂停事件
    void onPaused();

    // 通知下载取消事件
    void onCanceled();
}
