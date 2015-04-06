package com.raizlabs.android.app.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Description:
 */
@Table(databaseName = AppDatabase.NAME)
public class LargeExampleModel extends ExampleModel {

    @Column
    public String anotherName;

    @Column
    public int order;

    @Column
    public String anotherName2;
}
