/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package dev.alq;

import java.sql.SQLException;
import java.util.List;

import com.hp.hpl.jena.query.core.Binding;
import com.hp.hpl.jena.query.core.Var;
import com.hp.hpl.jena.query.engine.QueryIterator;
import com.hp.hpl.jena.query.engine1.ExecutionContext;
import com.hp.hpl.jena.query.engine1.iterator.QueryIterRepeatApply;
import com.hp.hpl.jena.sdb.core.CompileContext;
import com.hp.hpl.jena.sdb.core.sqlnode.GenerateSQL;
import com.hp.hpl.jena.sdb.core.sqlnode.SqlNode;
import com.hp.hpl.jena.sdb.layout2.SQLBridge2;
import com.hp.hpl.jena.sdb.sql.RS;
import com.hp.hpl.jena.sdb.sql.SDBExceptionSQL;
import com.hp.hpl.jena.sdb.store.SQLBridge;

public class QueryIterSDB extends QueryIterRepeatApply
{
    // Alternative - no QueryIterSDB, have TableSDB instead. 
    // PlanElementSDB wraps this table.
    private OpSQL opSQL ;
    private CompileContext context ;
    
    public QueryIterSDB(OpSQL opSQL, QueryIterator input, 
                        CompileContext context, 
                        ExecutionContext execCxt)
    {
        super(input, execCxt) ;
        this.context = context ;
        this.opSQL = opSQL ;
    }
    
    @Override
    protected QueryIterator nextStage(Binding binding)
    {
        //TODO SUBSTITUTE binding
        // Maybe OpSQL also needs the original tree.
        //return store.getQueryCompiler().exec(store, block2, binding, super.getExecContext()) ;
        return exec(context, binding, getExecContext()) ;
    }
    
    public QueryIterator exec(CompileContext context,Binding binding, ExecutionContext execCxt)
    {
        // REMOVE
        // All BGPs put all their named variables into the output so no real work to do here. 
        List<Var> projectVars = QP.projectVars(execCxt.getQuery()) ;
        
        // XXX Store.createBridge
        SQLBridge bridge = new SQLBridge2() ;
        SqlNode sqlNode = QP.toSqlTopNode(opSQL.getSqlNode(), projectVars, bridge) ;
        String sqlStmt = GenerateSQL.toSQL(sqlNode) ;
        if ( true )
            System.out.println(sqlStmt) ;
        try {
            java.sql.ResultSet jdbcResultSet = context.getStore().getConnection().execQuery(sqlStmt) ;
            if ( false )
                // Destructive
                RS.printResultSet(jdbcResultSet) ;
            try {
                return bridge.assembleResults(jdbcResultSet, binding, execCxt) ;
            } finally { jdbcResultSet.close() ; }
        } catch (SQLException ex)
        {
            throw new SDBExceptionSQL("SQLException in executing SQL statement", ex) ;
        }
    }
}

/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */