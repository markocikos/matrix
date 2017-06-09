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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import gnu.io.CommPort;
/**
 * apt-get install librxtx-java
 * Add RXTXcomm.java to build path (See this project's Serial Communication
 * User Lib
 */
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

/**
 * codebase:
 * https://www.henrypoon.com/blog/2011/01/01/serial-communication-in-java
 * -with-example-program/
 *
 * @author Peter Borkuti
 *
 */
public class SerialCommunicationUtil {

	// Arduino Serial Settings
	private int BAUD_RATE = 9600;
	final static int STOP_BIT = SerialPort.STOPBITS_1;
	final static int PARITY = SerialPort.PARITY_NONE;
	final static int DATA_BITS = SerialPort.DATABITS_8;
	final static boolean DTR = true;
	final static int FLOW_CONTROL = SerialPort.FLOWCONTROL_NONE;
	/**
	 * http://stackoverflow.com/questions/10382578/flow-controll-settings-for-
	 * serial-communication-between-java-rxtx-and-arduino
	 *
	 */
	final static boolean RTS = true;

	private SerialReaderListener listener;



	// the timeout value for connecting with the port
	final static int TIMEOUT = 2000;

	// some ascii values for for certain things
	final static int SPACE_ASCII = 32;
	final static int DASH_ASCII = 45;
	final static int NEW_LINE_ASCII = 10;
	private InputStream in;
	private OutputStream out;

	private CommPort commPort = null;
	private boolean isOpen = false;

	public SerialCommunicationUtil(SerialReaderListener listener) {
		this.listener = listener;
	}

	public SerialCommunicationUtil() {
	}

	public boolean isCommPort() {
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> ports = CommPortIdentifier
				.getPortIdentifiers();

		boolean isSerialPort = false;

		while (!isSerialPort && ports.hasMoreElements()) {
			CommPortIdentifier curPort = ports.nextElement();

			isSerialPort =
				(CommPortIdentifier.PORT_SERIAL == curPort.getPortType());
		}

		return isSerialPort;
	}

	public void listPorts() {
		_log.debug("list port");

		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> ports = CommPortIdentifier
				.getPortIdentifiers();

		while (ports.hasMoreElements()) {
			CommPortIdentifier curPort = ports.nextElement();
			_log.debug(
					"Port:" + curPort.getName() + "-" + curPort.getPortType());
		}
	}

	private void initSerialPort(SerialPort port)
			throws UnsupportedCommOperationException, IOException,
			TooManyListenersException {
		// Arduino: 8-N-1, speed : 115200
		port.setSerialPortParams(BAUD_RATE, DATA_BITS, STOP_BIT, PARITY);
		port.setDTR(DTR);
		port.setFlowControlMode(FLOW_CONTROL);
		port.setRTS(RTS);

		in = port.getInputStream();
		out = port.getOutputStream();

		// (new Thread(new SerialWriter(out))).start();

		port.addEventListener(new SerialReader(in, listener));
		port.notifyOnDataAvailable(true);
	}

	private void connect(CommPortIdentifier port) {

		try {
			commPort = port.open("Barking", TIMEOUT);

			SerialPort serialPort = (SerialPort) commPort;

			//touchForCDCReset from Arduino's Serial.java
			//it is needed to restart communication
			//	pull out mouse from the window -> program paused
			//	pull in mouse -> program restarted and reconnect to serial line
			//without these code, after re-connect, nothing read
			serialPort.setSerialPortParams(9600, 8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setDTR(false);
			serialPort.close();

			commPort = port.open("Barking", TIMEOUT);
			serialPort = (SerialPort) commPort;

			initSerialPort(serialPort);

			isOpen = true;
			_log.debug("connected to serial line");

		} catch (PortInUseException e) {
			_log.error(port + " is in use. (" + e.toString()
					+ ")");
		} catch (Exception e) {
			_log.error(
					"Failed to open " + port + "(" + e.toString() + ")");
		}
	}

	public void connectToFirstSerialPort() {
		connectToFirstSerialPort(BAUD_RATE);
	}

	public void connectToFirstSerialPort(int baudRate) {
		_log.debug("connect to first port");

		BAUD_RATE = baudRate;

		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> ports = CommPortIdentifier
				.getPortIdentifiers();

		while (ports.hasMoreElements()) {
			CommPortIdentifier curPort = ports.nextElement();

			_log.debug(
					"Port:" + curPort.getName() + "-" + curPort.getPortType());

			if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				connect(curPort);
				break;
			}

		}
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void stop() {
		if (commPort != null && isOpen) {
			((SerialPort)commPort).notifyOnDataAvailable(false);
			((SerialPort)commPort).removeEventListener();
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			commPort.close();
			commPort = null;
			isOpen = false;
			_log.debug("Serial port closed");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SerialCommunicationUtil.class);
}