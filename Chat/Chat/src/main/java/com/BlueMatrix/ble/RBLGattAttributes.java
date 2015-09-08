/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.BlueMatrix.ble;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class RBLGattAttributes {
	private static HashMap<String, String> attributes = new HashMap<String, String>();
	public static String BLE_SHIELD_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static String BLE_SHIELD_CUSTOMECOMMAND = "0000fff1-0000-1000-8000-00805f9b34fb";
	public static String BLE_SHIELD_REGULARCOMMAND = "0000fff2-0000-1000-8000-00805f9b34fb";
	public static String BLE_SHIELD_TEXTCOMMAND = "0000fff3-0000-1000-8000-00805f9b34fb";

	static {
		// RBL Services.
		attributes.put("0000fff0-0000-1000-8000-00805f9b34fb","BLE Shield Service");

		// RBL Characteristics.
		attributes.put(BLE_SHIELD_CUSTOMECOMMAND, "BLE_SHIELD_CUSTOMECOMMAND");
		attributes.put(BLE_SHIELD_TEXTCOMMAND, "BLE_SHIELD_TEXTCOMMAND");

	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}
