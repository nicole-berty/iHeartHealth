package ie.ul.ihearthealth.main_nav_drawer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;

import ie.ul.ihearthealth.R;

public class AlertDialogFragment extends DialogFragment {

    AlertDialogListener listener;
    private AlertDialog dialog;
    private static boolean mEnableButton;
    String value = "";

    public interface AlertDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    public void setListener(AlertDialogListener alertPositiveListener){
        this.listener = alertPositiveListener;
    }

    public static AlertDialogFragment newInstance(int title) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_signin, null))
                // Add action buttons
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        listener.onDialogPositiveClick(AlertDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(AlertDialogFragment.this);
                    }
                });
        return builder.create();
    }
}