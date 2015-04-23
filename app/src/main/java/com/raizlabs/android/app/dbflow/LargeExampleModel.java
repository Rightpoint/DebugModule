package com.raizlabs.android.app.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
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

    @Column(columnType = Column.FOREIGN_KEY,
        references = {@ForeignKeyReference(columnType = long.class, columnName = "example_id", foreignColumnName = "id")})
    ExampleModel exampleModel;

}
