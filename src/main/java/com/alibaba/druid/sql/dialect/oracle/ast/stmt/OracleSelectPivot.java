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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleSelectPivot extends OracleSelectPivotBase {
    private static final long serialVersionUID = 1L;

    private boolean xml;
    private final List<Item> items = new ArrayList<Item>();
    private final List<SQLExpr> pivotFor = new ArrayList<SQLExpr>();
    private final List<Item> pivotIn = new ArrayList<Item>();

    public OracleSelectPivot() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.pivotFor);
            acceptChild(visitor, this.pivotIn);
        }

        visitor.endVisit(this);
    }

    public List<Item> getPivotIn() {
        return this.pivotIn;
    }

    public List<SQLExpr> getPivotFor() {
        return this.pivotFor;
    }

    public boolean isXml() {
        return this.xml;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void setXml(boolean xml) {
        this.xml = xml;
    }

    public static class Item extends OracleSQLObject {
        private static final long serialVersionUID = 1L;

        private String alias;
        private SQLExpr expr;

        public Item() {

        }

        public String getAlias() {
            return this.alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public SQLExpr getExpr() {
            return this.expr;
        }

        public void setExpr(SQLExpr expr) {
            this.expr = expr;
        }

        protected void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.expr);
            }

            visitor.endVisit(this);
        }
    }
}
