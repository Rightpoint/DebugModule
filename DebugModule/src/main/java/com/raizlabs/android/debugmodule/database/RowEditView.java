package com.raizlabs.android.debugmodule.database;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raizlabs.android.debugmodule.R;

/**
 * Description:
 */
public class RowEditView extends LinearLayout {

    private EditText rowValue;

    private TextView rowTitle;

    private RowCritter.ColumnChangeListener listener;

    private Column column;

    public RowEditView(Context context) {
        super(context);
        init(context);
    }

    public RowEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RowEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RowEditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_item_row_edit, this, true);
        rowValue = ((EditText) findViewById(R.id.valueEdit));
        rowValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                column.value = column.toValue(s.length() > 0 ? s.toString() : null);
                listener.onColumnChanged(column);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        rowTitle = (TextView) findViewById(R.id.rowTitle);
    }

    void populate(Column column, RowCritter.ColumnChangeListener columnChangeListener) {
        listener = columnChangeListener;
        this.column = column;
        if(column.value != null) {
            rowValue.setText(String.valueOf(column.value));
        }
        rowTitle.setText(column.columnName);

        Class columnType = column.columnType;
        if (columnType.equals(Integer.class) || columnType.equals(Boolean.class)) {
            rowValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else if (columnType.equals(Float.class) || columnType.equals(Double.class)) {
            rowValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else if (columnType.equals(Long.class)) {
            rowValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else if (columnType.equals(String.class)) {
            rowValue.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }
}
