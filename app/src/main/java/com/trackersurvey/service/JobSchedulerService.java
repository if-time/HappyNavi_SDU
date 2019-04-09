package com.trackersurvey.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

public class JobSchedulerService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        startMainService();
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        startMainService();
        return false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        startMainService();
    }

    public void startMainService() {
        startService(JobSchedulerMainService.getIntentAlarm(this));
    }
}
