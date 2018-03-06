package com.duowan.meteor.model.view.importcassandra;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by huangshaoqian on 2015/10/12.
 */
public abstract class ImportCassandraTask extends AbstractTaskDepend {

    private static final long serialVersionUID = -7269367182547881286L;
    private String keySpace;
    private String table;
    private String tableKeys;
    private String columns;

    public String getKeySpace() {
        return keySpace;
    }

    public void setKeySpace(String keySpace) {
        this.keySpace = keySpace;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTableKeys() {
        return tableKeys;
    }

    public void setTableKeys(String tableKeys) {
        this.tableKeys = tableKeys;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    @Override
    public void doAssert() {
        super.doAssert();
        assertTrue(StringUtils.isNotBlank(this.table), "table cannot be blank!");
        assertTrue(StringUtils.isNotBlank(this.keySpace), "keySpace cannot be blank!");
        assertTrue(StringUtils.isNotBlank(this.tableKeys), "tableKeys cannot be blank!");
        assertTrue(StringUtils.isNotBlank(this.columns), "columns cannot be blank!");
    }
}
