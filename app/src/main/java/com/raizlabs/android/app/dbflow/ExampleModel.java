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
    long id;

    @Column
    String name;

    @Column
    boolean isSet;

    @Column
    double duble;

    @Column
    float floatie;

}
