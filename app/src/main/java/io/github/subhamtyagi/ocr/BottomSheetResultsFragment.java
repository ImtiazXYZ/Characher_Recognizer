package io.github.subhamtyagi.ocr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public class BottomSheetResultsFragment extends BottomSheetDialogFragment  implements TextToSpeech.OnInitListener {

    private static final String ARGUMENT_TEXT = "arg_text";

    private Context context;
    private Bundle bundle;
    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_dialog_results, container, false);

        context = getContext();
        bundle = getArguments();

        assert bundle != null;
        assert context != null;

        textToSpeech = new TextToSpeech(getContext(), this);
        TextView resultantText = v.findViewById(R.id.resultant_text);
        ImageButton btnCopy = v.findViewById(R.id.btn_copy);
        ImageButton btnShare = v.findViewById(R.id.btn_share);
        ImageButton btn_voice = v.findViewById(R.id.btn_voice);

        btnCopy.setOnClickListener(v12 -> {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("nonsense_data", bundle.getString(ARGUMENT_TEXT));
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            //Toast.makeText(context,bundle.getString(ARGUMENT_TEXT) , Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btnShare.setOnClickListener(v1 -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, bundle.getString(ARGUMENT_TEXT));
            startActivity(Intent.createChooser(intent, null));
            dismiss();
        });
        btn_voice.setOnClickListener(v1 -> {
            String textToConvert =  bundle.getString(ARGUMENT_TEXT);
            speakText(textToConvert);

            dismiss();
        });

        if (bundle.getString(ARGUMENT_TEXT).trim().isEmpty()) {
            btnCopy.setEnabled(false);
            btnCopy.setAlpha(.3f);
            btnShare.setEnabled(false);
            btnShare.setAlpha(.3f);
            resultantText.setText(R.string.no_results);
        } else {
            resultantText.setText(bundle.getString(ARGUMENT_TEXT));
        }
        return v;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        cancel();
    }

    @Override
    public void dismiss() {
        cancel();
        super.dismiss();
    }

    private void cancel() {

    }

    public static BottomSheetResultsFragment newInstance(String text) {
        BottomSheetResultsFragment fragment = new BottomSheetResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void speakText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (textToSpeech != null) {
                try {
                    textToSpeech.setLanguage(new Locale("bn_BD"));
                    Toast.makeText(context, "Bengali", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    // Fallback to English or handle as needed
                    Toast.makeText(context, "English", Toast.LENGTH_SHORT).show();
                    textToSpeech.setLanguage(Locale.US);
                }

                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int langResult = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                langResult = textToSpeech.isLanguageAvailable(new Locale("bn_BD"));
            }

            if (langResult == TextToSpeech.LANG_AVAILABLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.setLanguage(new Locale("bn_BD"));
                }
                Toast.makeText(context, "Bengali", Toast.LENGTH_SHORT).show();
            } else {
                // Fallback to English or handle as needed
                textToSpeech.setLanguage(Locale.US);
                Toast.makeText(context, "Fallback to English", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle TextToSpeech initialization failure
        }
    }

}