/*
 * Copyright (c) 2025-2026, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.baomibing.work.util;


import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Utility class for checking and determining conditions, with methods that always return a boolean type
 *
 * @author zening (316279829@qq.com)
 */
public abstract class Checker {

	public static <T> boolean BeNotNull(T reference) {
		if (reference == null) {
			return false;
		}
		return true;
	}
	
	public static <T> boolean BeNull(T reference) {
		return !BeNotNull(reference);
	}
	

	public static <T> boolean BeNotNull(Iterable<T> collection) {
		if (IterableUtils.isEmpty(collection)) {
			return false;
		}
		return true;
	}
	
	public static <T> boolean BeNull(Iterable<T> collection) {
		return !BeNotNull(collection);
	}
	

	public static <T> Boolean BeNotEmpty(Iterable<T> coll) {
		return !IterableUtils.isEmpty(coll);
	}
	
	public static <T> Boolean BeEmpty(Iterable<T> coll) {
		return !BeNotEmpty(coll);
	}

	public static <K, V> boolean BeNotEmpty(Map<K, V> map) {
		return MapUtils.isNotEmpty(map);
	}
	
	public static <K, V> boolean BeEmpty(Map<K, V> map) {
		return !BeNotEmpty(map);
	}

	public static <T> Boolean BeNotEmpty(T[] arr) {
		return ArrayUtils.isNotEmpty(arr);
	}
	
	public static <T> Boolean BeEmpty(T[] arr) {
		return !BeNotEmpty(arr);
	}
	

	public static Boolean BeNotEmpty(CharSequence cs) {
		return StringUtils.isNotBlank(cs);
	}
	
	public static Boolean BeEmpty(CharSequence cs) {
		return !BeNotEmpty(cs);
	}
	

	public static Boolean BeNotBlank(CharSequence cs) {
		return StringUtils.isNotBlank(cs);
	}
	
	public static Boolean BeBlank(CharSequence cs) {
		return StringUtils.isBlank(cs);
	}
	
	
	public static Boolean BeGreaterThan(Number a, Number b) {
		if (a == null || b == null) {
			return false;
		}
		return a.doubleValue() - b.doubleValue() > 0;
	}
	
	
	public static Boolean BeGreaterOrEqualThan(Number a, Number b) {
		if (a == null || b == null) {
			return false;
		}
		return a.doubleValue() > b.doubleValue() || a.doubleValue() == b.doubleValue();
	}
	
	public static Boolean beEqualThan(Number a, Number b) {
		if (a == null || b == null) {
			return false;
		}
		return a.doubleValue() == b.doubleValue();
	}
	
	public static Boolean BeNotEqual(Object a, Object b) {
		return !a.toString().equals(b.toString());
	}
	
}
