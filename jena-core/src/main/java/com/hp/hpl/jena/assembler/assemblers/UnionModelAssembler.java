/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.hpl.jena.assembler.assemblers;

import com.hp.hpl.jena.assembler.Assembler ;
import com.hp.hpl.jena.assembler.JA ;
import com.hp.hpl.jena.assembler.Mode ;
import com.hp.hpl.jena.graph.Factory ;
import com.hp.hpl.jena.graph.Graph ;
import com.hp.hpl.jena.graph.compose.MultiUnion ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.ModelFactory ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.rdf.model.StmtIterator ;

public class UnionModelAssembler extends ModelAssembler implements Assembler
    {
    
    @Override
    protected Model openEmptyModel( Assembler a, Resource root, Mode mode )
        {
        checkType( root, JA.UnionModel );
        MultiUnion union = new MultiUnion();
        union.addGraph( getRootModel( a, root, mode ) );
        addSubModels( a, root, union, mode );
        return ModelFactory.createModelForGraph( union );
        }

    private Graph getRootModel( Assembler a, Resource root, Mode mode )
        {
        Resource r = getUniqueResource( root, JA.rootModel );
        return r == null ? Factory.empty() : a.openModel( r, mode ).getGraph();
        }

    private void addSubModels( Assembler a, Resource root, MultiUnion union, Mode mode )
        {
        for (StmtIterator it = root.listProperties( JA.subModel ); it.hasNext();)
            {
            Resource resource = getResource( it.nextStatement() );
            union.addGraph( a.openModel( resource, mode ).getGraph() );        
            }
        }

    }
