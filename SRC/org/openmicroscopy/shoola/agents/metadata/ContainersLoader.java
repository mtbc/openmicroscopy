/*
 * org.openmicroscopy.shoola.agents.metadata.ContainersLoader 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.metadata;


//Java imports

//Third-party libraries

//Application-internal dependencies
import java.util.Collection;

import org.openmicroscopy.shoola.agents.metadata.browser.TreeBrowserSet;
import org.openmicroscopy.shoola.agents.metadata.view.MetadataViewer;
import org.openmicroscopy.shoola.env.data.util.StructuredDataResults;
import org.openmicroscopy.shoola.env.data.views.CallHandle;

import pojos.DataObject;

/** 
 * Loads the containers of a given object.
 * This class calls one of the <code>loadContainers</code> methods in the
 * <code>MetadataHandlerView</code>.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class ContainersLoader 	
	extends MetadataLoader
{

	/** Either <code>DatasetData</code> or <code>ProjectData</code>. */
	private Class		type;
	
	/** The ID of the parent of the {@link #refNode}. */
	private long 		id;
	
	private StructuredDataResults data;
	
	/** Handle to the async call so that we can cancel it. */
    private CallHandle  handle;
    
	/**
	 * Creates a new instance.
	 * 
	 * @param viewer	The viewer this data loader is for.
     *                  Mustn't be <code>null</code>.
	 * @param refNode	The node of reference. Mustn't be <code>null</code>.
	 * @param type		The type of the parent of the {@link #refNode}.
	 * @param id		The ID of the parent of the ref node.
	 */
	public ContainersLoader(MetadataViewer viewer, TreeBrowserSet refNode,
						Class type, long id)
	{
		super(viewer, refNode);
		this.type = type;
		this.id = id;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param viewer The viewer this data loader is for.
	 * 				 Mustn't be <code>null</code>.
	 * @param data	
	 */
	public ContainersLoader(MetadataViewer viewer, StructuredDataResults data)
	{
		super(viewer, null);
		if (data == null)
			throw new IllegalArgumentException("No element to handle.");
		this.data = data;
		type = data.getRelatedObject().getClass();
		id = ((DataObject) data.getRelatedObject()).getId();
	}
	
	/** 
	 * Loads the folders containing the object. 
	 * @see MetadataLoader#cancel()
	 */
	public void load()
	{
		handle = mhView.loadContainers(type, id, -1L, this);
	}
	
	/** 
	 * Cancels the data loading. 
	 * @see MetadataLoader#cancel()
	 */
	public void cancel() { handle.cancel(); }

	/**
     * Feeds the result back to the viewer.
     * @see MetadataLoader#handleResult(Object)
     */
    public void handleResult(Object result) 
    {
    	if (viewer.getState() == MetadataViewer.DISCARDED) return;  //Async cancel.
    	if (data == null) 
    		viewer.setContainers(refNode, result);
    	else {
    		data.setParents((Collection) result);
    	}
    }
    
}
