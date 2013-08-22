package se.smartkalender.dialogs;

import se.smartkalender.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * Create custom Dialog windows for your application
 * Custom dialogs rely on custom layouts wich allow you to 
 * create and use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * @author antoine vianey
 *
 */
public class CustomAlertDialog extends Dialog {

    public CustomAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomAlertDialog(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private String neutralButtonText;
        private View contentView;
        private boolean cancelable = true;

        private DialogInterface.OnClickListener 
                        positiveButtonClickListener,
                        negativeButtonClickListener,
                        neutralButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        
        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
        
		public Builder setNeutralButton(String neutralButtonText,
				OnClickListener listener) {
            this.neutralButtonText = neutralButtonText;
            this.neutralButtonClickListener = listener;
            return this;
		}

        /**
         * Create the custom dialog
         */
        public CustomAlertDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomAlertDialog dialog = new CustomAlertDialog(context, 
            		R.style.NewDialog);
            View layout = inflater.inflate(R.layout.dialog, null);            
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));            
            // set the dialog title
            if (title != null) ((TextView) layout.findViewById(R.id.title)).setText(title);
            else layout.findViewById(R.id.title_layout).setVisibility(View.GONE);
            //
            if (positiveButtonText == null || negativeButtonText == null || neutralButtonText == null ){
            	((LinearLayout) layout.findViewById(R.id.leftSpacer)).setVisibility(View.VISIBLE);
            	((LinearLayout) layout.findViewById(R.id.rightSpacer)).setVisibility(View.VISIBLE);
            }
            if (neutralButtonText == null && negativeButtonText == null && positiveButtonText == null) {
            	((LinearLayout) layout.findViewById(R.id.footerBtns)).setVisibility(View.GONE);
            	((LinearLayout) layout.findViewById(R.id.footerNoBtns)).setVisibility(View.VISIBLE);
            }
            //if (neutralButtonText != null && negativeButtonText != null && positiveButtonText != null) {
            //	((LinearLayout) layout).setPadding(SmartCalendarFactory.dipsToPixels(25), 0, SmartCalendarFactory.dipsToPixels(25), 0);
            //}            
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_POSITIVE);
                                    dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	negativeButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_NEGATIVE);
                                	dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            
            // set the neutral button
            if (neutralButtonText != null) {
                ((Button) layout.findViewById(R.id.neutralButton))
                        .setText(neutralButtonText);
                if (neutralButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.neutralButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	neutralButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_NEUTRAL);
                                	dialog.dismiss();
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.neutralButton).setVisibility(
                        View.GONE);
            }            
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(
                		R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, 
                                new LayoutParams(
                                        LayoutParams.FILL_PARENT, 
                                        LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

		public Dialog show() {
			CustomAlertDialog dialog = create();
			dialog.setCancelable(cancelable);
        	dialog.show();
			return dialog;			
		}

		public Builder setCancelable(boolean b) {
			cancelable = b;
			return this;
		}
    }

}