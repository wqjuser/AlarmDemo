package com.wqj.alarmdemo;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by wqj on 2017/11/13.
 */

public class JobSchedulerService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }
}
