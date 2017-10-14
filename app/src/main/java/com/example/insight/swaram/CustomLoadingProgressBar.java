package com.example.insight.swaram;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;


public class CustomLoadingProgressBar extends ProgressDialog {
    private AnimationDrawable animation;

    public static ProgressDialog creator(Context context) {
        CustomLoadingProgressBar dialog = new CustomLoadingProgressBar(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public CustomLoadingProgressBar(Context context) {
        super(context);
    }

    public CustomLoadingProgressBar(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_loading_progress_bar);

        ImageView la = (ImageView) findViewById(R.id.animation);
        la.setBackgroundResource(R.drawable.custom_progress_dialog_animation);
        animation = (AnimationDrawable) la.getBackground();
    }

    @Override
    public void show() {
        super.show();
        animation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animation.stop();
    }
}
