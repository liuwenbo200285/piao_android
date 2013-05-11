package com.wenbo.piao.dialog;


import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class LoginDialog extends DialogFragment {

	public static LoginDialog newInstance(String title) { 
		LoginDialog fragment = new LoginDialog(); 
        Bundle args = new Bundle(); 
        args.putString("title", title); 
        fragment.setArguments(args); 
        return fragment; 
    } 
   
    @Override 
    public Dialog onCreateDialog(Bundle savedInstanceState) { 
        String title = getArguments().getString("title"); 
        Dialog dialog =  new AlertDialog.Builder(getActivity())
        .setIcon(R.drawable.ic_dialog_alert) 
        .setMessage(title)
        .setPositiveButton("确定",null)
        .create();
//        Window window = dialog.getWindow();
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.alpha = 0.8f;// 这里设置透明度
//		window.setAttributes(lp);
		return dialog;
    }
}
