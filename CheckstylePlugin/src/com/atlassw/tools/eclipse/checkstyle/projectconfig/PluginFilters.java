//============================================================================
//
// Copyright (C) 2002-2004  David Schneider
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
//============================================================================

package com.atlassw.tools.eclipse.checkstyle.projectconfig;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.atlassw.tools.eclipse.checkstyle.CheckstylePlugin;
import com.atlassw.tools.eclipse.checkstyle.projectconfig.filters.IFilter;
import com.atlassw.tools.eclipse.checkstyle.util.CheckstyleLog;

/**
 * Register for the filters thats use the
 * <i>com.atlassw.tools.eclipse.checkstyle.checkstyleFilter </i> extension
 * point. Checkstyle filters can be enabled per project.
 * 
 * @author Lars K�dderitzsch
 */
public final class PluginFilters
{

    //
    // constants
    //

    /** constant for the extension point id. */
    private static final String FILTER_EXTENSION_POINT = CheckstylePlugin.PLUGIN_ID + ".filters";

    /** constant for the name attribute. */
    private static final String ATTR_NAME = "name";

    /** constant for the name attribute. */
    private static final String ATTR_INTERNAL_NAME = "internal-name";

    /** constant for the description attribute. */
    private static final String ATTR_DESCRIPTION = "description";

    /** constant for the class attribute. */
    private static final String ATTR_CLASS = "class";

    /** constant for the editorClass attribute. */
    private static final String ATTR_EDITOR = "editorClass";

    /** contant for the readonly attribute. */
    private static final String ATTR_READONLY = "readonly";

    /** constant for the selected attribute. */
    private static final String ATTR_SELECTED = "selected";

    /** constant for the value attribute. */
    private static final String ATTR_VALUE = "value";

    /** the filter prototypes configured to the extension point. */
    private static final IFilter[] FILTER_PROTOTYPES;

    //
    // Initializer
    //

    /**
     * Initialize the configured to the filter extension point.
     */
    static
    {

        IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();

        IConfigurationElement[] elements = pluginRegistry
                .getConfigurationElementsFor(FILTER_EXTENSION_POINT);

        List filters = new ArrayList();

        for (int i = 0; i < elements.length; i++)
        {

            try
            {

                Class filterClass = Class.forName(elements[i].getAttributeAsIs(ATTR_CLASS));

                String name = elements[i].getAttribute(ATTR_NAME);
                String internalName = elements[i].getAttribute(ATTR_INTERNAL_NAME);
                String desc = elements[i].getAttribute(ATTR_DESCRIPTION);
                Class editorClass = elements[i].getAttributeAsIs(ATTR_EDITOR) == null ? null
                        : Class.forName(elements[i].getAttributeAsIs(ATTR_EDITOR));
                boolean readOnly = Boolean.valueOf(elements[i].getAttribute(ATTR_READONLY))
                        .booleanValue();

                IFilter filter = (IFilter) filterClass.newInstance();
                filter.initialize(name, internalName, desc, editorClass, readOnly);

                boolean defaultState = Boolean.valueOf(elements[i].getAttribute(ATTR_SELECTED))
                        .booleanValue();
                String defaultData = elements[i].getAttribute(ATTR_VALUE);

                filter.setEnabled(defaultState);
                //filter.setFilterData(defaultData);

                filters.add(filter);
            }
            catch (Exception e)
            {

                CheckstyleLog.error(e.getLocalizedMessage(), e);
            }
        }

        FILTER_PROTOTYPES = (IFilter[]) filters.toArray(new IFilter[filters.size()]);
    }

    //
    // constructor
    //

    /** Hidden default constructor. */
    private PluginFilters()
    {
    //NOOP
    }

    //
    // methods
    //

    /**
     * Returns the available filters.
     * 
     * @return the available filters.
     */
    public static IFilter[] getConfiguredFilters()
    {

        //Copy the prototypes for the client
        IFilter[] mFilter = new IFilter[FILTER_PROTOTYPES.length];

        //Clone and set the state of the filter
        for (int i = 0; i < mFilter.length; i++)
        {
            mFilter[i] = (IFilter) FILTER_PROTOTYPES[i].clone();
        }

        return mFilter;
    }

    /**
     * Gets a filter prototype by name.
     * 
     * @param name the filter name
     * @return the filter prototype or <code>null</code>
     */
    public static IFilter getByName(String name)
    {

        IFilter filter = null;

        for (int i = 0; i < FILTER_PROTOTYPES.length; i++)
        {
            if (FILTER_PROTOTYPES[i].getName().equals(name))
            {
                filter = (IFilter) FILTER_PROTOTYPES[i].clone();
                break;
            }
        }

        return filter;
    }
}