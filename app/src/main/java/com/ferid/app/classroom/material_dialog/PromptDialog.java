package com.ferid.app.classroom.material_dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ferid.app.classroom.R;
import com.ferid.app.classroom.interfaces.OnPrompt;

/**
 * Created by ferid.cafer on 4/3/2015.
 */
public class PromptDialog extends Dialog {
    private Context context;

    private TextView title;
    private EditText content;
    private Button positiveButton;

    private OnPrompt onPrompt;

    public PromptDialog(Context context__) {
        super(context__);
        setContentView(R.layout.prompt_dialog);

        context = context__;

        getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));

        title = (TextView) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        positiveButton = (Button) findViewById(R.id.positiveButton);

        content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    promptPositive();
                    return true;
                }
                return false;
            }
        });
    }

    public void setTitle(String value) {
        title.setText(value);
    }

    public void setPositiveButton(String value) {
        positiveButton.setText(value);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptPositive();
            }
        });
    }

    public void setOnPositiveClickListener(OnPrompt onPrompt) {
        this.onPrompt = onPrompt;
    }

    private void promptPositive() {
        if (onPrompt != null)
            onPrompt.OnPrompt(content.getText().toString());
    }

    /**
     * Set all characters capital letter
     */
    public void setAllCaps() {
        content.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
    }

    @Override
    public void show() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        super.show();
    }
}