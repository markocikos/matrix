/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.matrix.web.internal.internal.configurator;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Čikoš
 */
@Component(immediate = true)
public class MatrixConfigurator {

	@Activate
	public void activate(BundleContext bundleContext) {
		Dictionary<String, Object> servletContextProps = new Hashtable<>();

		servletContextProps.put("websocket.active", Boolean.TRUE);

		bundleContext.registerService(
			ServletContext.class, _servletContext, servletContextProps);
	}

	@Reference(target = "(original.bean=true)")
	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private ServletContext _servletContext;

}