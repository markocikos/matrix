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

package com.liferay.matrix.web.internal.internal.serial;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;

public class SerialReader implements SerialPortEventListener {

	InputStream in;

	private volatile StringBuffer tmp = new StringBuffer("");

	private final SerialReaderListener listener;

	public SerialReader(InputStream in, SerialReaderListener listener) {
		this.in = in;
		this.listener = listener;
	}

	public synchronized void read() {
		byte[] buffer = new byte[1024];
		int len = -1;
		try {
			while (((len = this.in.read(buffer)) > 0)) {
				tmp.append(new String(buffer, 0, len));
				if (listener.eventReader(tmp.toString())) {
					tmp.setLength(0);
				};
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			System.out.println("Data available");
			read();
			break;
		}
	}

}