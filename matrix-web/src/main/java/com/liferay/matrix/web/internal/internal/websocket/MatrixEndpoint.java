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

package com.liferay.matrix.web.internal.internal.websocket;

import com.liferay.matrix.web.internal.internal.serial.SerialCommunicationUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marko Čikoš
 */
@Component(
	immediate = true,
	property = {"org.osgi.http.websocket.endpoint.path=/o/matrixEndpoint"},
	service = Endpoint.class
)
public class MatrixEndpoint extends Endpoint {

	@Override
	public void onOpen(final Session session, EndpointConfig endpointConfig) {
		final SerialCommunicationUtil serialCommunicationUtil =
			new SerialCommunicationUtil();

		session.addMessageHandler(
			new MessageHandler.Whole<String>() {

				@Override
				public void onMessage(String text) {
					_log.debug(text);

					serialCommunicationUtil.listPorts();
				}

			}
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(MatrixEndpoint.class);

}