/*
 * Copyright (C) 2015 Ferid Cafer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.ferid.app.classroom.interfaces.OnClick;
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
        String input = content.getText().toString();

        if (isAlphanumeric(input)) {
            if (onPrompt != null) {
                onPrompt.OnPrompt(input);
            }
        } else {
            showInvalidInputAlert();
        }
    }

    /**
     * Set all characters capital letter
     */
    public void setAllCaps() {
        content.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
    }

    @Override
    public void show() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        super.show();
    }

    /**
     * Validate if the input is alphanumeric
     * @param s String
     * @return Boolean
     */
    private boolean isAlphanumeric(String s){
        String pattern = "^[a-zA-Z0-9\\s]*$";
        if (s.matches(pattern)) {
            return true;
        }
        return false;
    }

    /**
     * Show alert to warn to enter only alphanumeric characters
     */
    private void showInvalidInputAlert() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
        customAlertDialog.setMessage(context.getString(R.string.enterAlphanumeric));
        customAlertDialog.setPositiveButtonText(context.getString(R.string.ok));
        customAlertDialog.setOnClickListener(new OnClick() {
            @Override
            public void OnPositive() {
                //show input dialog
                show();
            }

            @Override
            public void OnNegative() {
                //do nothing
            }
        });

        //hide input dialog
        hide();
        //show alert dialog
        customAlertDialog.showDialog();
    }
}