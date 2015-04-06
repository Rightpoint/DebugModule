package com.raizlabs.android.app.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Description:
 */
@Table(databaseName = AppDatabase.NAME)
public class ExampleModel extends BaseModel {

    @Column(columnType = Column.PRIMARY_KEY_AUTO_INCREMENT)
    public long id;

    @Column
    public String name;

    @Column
    public boolean isSet;

    @Column
    public double duble;

    @Column
    public float floatie;

}
