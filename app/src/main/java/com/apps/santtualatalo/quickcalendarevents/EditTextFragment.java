package com.apps.santtualatalo.quickcalendarevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

//Listener for EditTextFragment events
interface OnEditTextReadyListener
{
    void onEditTextReady(String message);
    void onEditTextCancel();
}

/**
 * Handles creating an edit text fragment for calendar event messages
 */
public class EditTextFragment extends DialogFragment {

    private OnEditTextReadyListener myListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            myListener = (OnEditTextReadyListener) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnEditTextReadyListener");
        }

        // Use the Builder class for convenient dialog construction
        final EditText messageEditText = new EditText(getActivity());
        messageEditText.setText(getArguments().getString("messageKey"));
        //messageEditText.setSelectAllOnFocus(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.create_event_message)
                .setView(messageEditText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(myListener != null)
                        {
                            String message = String.valueOf(messageEditText.getText());
                            myListener.onEditTextReady(message);
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(myListener != null)
                            myListener.onEditTextCancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
