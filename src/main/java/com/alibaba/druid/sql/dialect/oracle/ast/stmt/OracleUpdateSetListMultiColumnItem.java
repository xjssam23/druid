/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleUpdateSetListMultiColumnItem extends OracleUpdateSetListItem {
    private static final long serialVersionUID = 1L;

    private final List<SQLName> columns = new ArrayList<SQLName>();
    private OracleSelect subQuery;

    public OracleUpdateSetListMultiColumnItem() {

    }

    public OracleUpdateSetListMultiColumnItem(OracleSelect subQuery) {

        this.subQuery = subQuery;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.subQuery);
        }

        visitor.endVisit(this);
    }

    public OracleSelect getSubQuery() {
        return this.subQuery;
    }

    public void setSubQuery(OracleSelect subQuery) {
        this.subQuery = subQuery;
    }

    public List<SQLName> getColumns() {
        return this.columns;
    }
}
