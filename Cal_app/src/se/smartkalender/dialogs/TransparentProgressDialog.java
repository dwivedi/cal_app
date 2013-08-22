package se.smartkalender.dialogs;

import se.smartkalender.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TransparentProgressDialog extends Dialog {

	public static TransparentProgressDialog show(Context context, CharSequence title,
	        CharSequence message) {
	    return show(context, title, message, false);
	}

	public static TransparentProgressDialog show(Context context, CharSequence title,
	        CharSequence message, boolean indeterminate) {
	    return show(context, title, message, indeterminate, false, null);
	}

	public static TransparentProgressDialog show(Context context, CharSequence title,
	        CharSequence message, boolean indeterminate, boolean cancelable) {
	    return show(context, title, message, indeterminate, cancelable, null);
	}

	public static TransparentProgressDialog show(Context context, CharSequence title,
	        CharSequence message, boolean indeterminate,
	        boolean cancelable, OnCancelListener cancelListener) {
	    TransparentProgressDialog dialog = new TransparentProgressDialog(context);
	    dialog.setTitle(title);
	    dialog.setCancelable(cancelable);
	    dialog.setOnCancelListener(cancelListener);
	 
	    //
	    LinearLayout ll = new LinearLayout(context);
	    LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = vi.inflate(R.layout.transparent_progress_dlg, ll);
	    ((TextView)v.findViewById(R.id.messageText)).setText(message);
	    /* The next line will add the ProgressBar to the dialog. */
	    dialog.addContentView(v, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    dialog.show();

	    return dialog;
	}
	
	public TransparentProgressDialog(Context context) {
	    super(context, R.style.NewDialog);
	}
};