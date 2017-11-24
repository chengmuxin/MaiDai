package com.hooha.maidai.chat.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.content.Context;

import com.hooha.maidai.chat.R;


/**
 * Created by MG on 2016/9/12.
 */
public class erweima_dialog  extends Dialog {
        public erweima_dialog(Context context) {
            super(context);
            // TODO Auto-generated constructor stub

        }

        public erweima_dialog(Context context, int theme) {
            super(context, theme);
            // TODO Auto-generated constructor stub
            setContentView(R.layout.dialog_erweima);
        }

        public erweima_dialog(Context context, boolean cancelable,
                        OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
        }
    }


