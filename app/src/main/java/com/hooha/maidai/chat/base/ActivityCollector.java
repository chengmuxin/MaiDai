package com.hooha.maidai.chat.base;

import android.app.Activity;

import com.hooha.maidai.chat.activity.HomeActivity;

import java.util.Stack;


/**
 * Created by: gaom
 * date: 2015/12/29
 * time: 9:51
 * description:
 */
public class ActivityCollector {
    public static Stack<Activity> activityStack = new Stack<>();

    public static void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }

    public static void finishAll() {
        while (!activityStack.isEmpty()) {
            Activity activity = activityStack.pop();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void finishOther() {
        while (activityStack.size() > 1) {
            Activity activity = activityStack.pop();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void finishOtherButMain() {
        while (activityStack.size() > 1) {
            Activity activity = activityStack.pop();
            if (!activity.isFinishing()) {
                if (!activity.getClass().equals(HomeActivity.class)) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }


    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    public static Activity peek() {
        return activityStack.peek();
    }
}
