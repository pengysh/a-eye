package com.a.eye.common.web.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author emeroad
 */
public class StaticFieldLookUp<T> {

	public interface Filter<T> {
		boolean FILTERED = true;
		boolean INCLUDE = false;

		boolean filter(T serviceType);
	}

	public static class BypassFilter<T> implements Filter<T> {
		@Override
		public boolean filter(T type) {
			return INCLUDE;
		}
	}

	public static class ExcludeFilter<T> implements Filter<T> {
		private final T[] excludeTypeList;

		public ExcludeFilter(T[] excludeTypeList) {
			if (excludeTypeList == null) {
				throw new NullPointerException("excludeTypeList must not be null");
			}
			this.excludeTypeList = excludeTypeList;
		}

		@Override
		public boolean filter(T type) {
			for (T excludeType : excludeTypeList) {
				if (excludeType == type) {
					return FILTERED;
				}
			}
			return Filter.INCLUDE;
		}
	}

	private final Class<?> targetClazz;
	private final Class<T> findClazz;

	public StaticFieldLookUp(Class<?> targetClazz, Class<T> findClazz) {
		if (targetClazz == null) {
			throw new NullPointerException("targetClazz must not be null");
		}
		if (findClazz == null) {
			throw new NullPointerException("findClazz must not be null");
		}
		this.targetClazz = targetClazz;
		this.findClazz = findClazz;
	}

	public List<T> lookup(Filter<T> filter) {
		if (filter == null) {
			throw new NullPointerException("filter must not be null");
		}
		final List<T> lookup = new ArrayList<T>();

		Field[] declaredFields = targetClazz.getDeclaredFields();
		for (Field field : declaredFields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (!Modifier.isPublic(field.getModifiers())) {
				continue;
			}
			if (!field.getType().equals(findClazz)) {
				continue;
			}
			final Object filedObject = getObject(field);

			if (findClazz.isInstance(filedObject)) {
				T type = findClazz.cast(filedObject);
				if (filter.filter(type) == Filter.FILTERED) {
					continue;
				}

				lookup.add(type);
			}
		}
		return lookup;
	}

	public List<T> lookup() {
		return lookup(new BypassFilter<T>());
	}

	private Object getObject(Field field) {
		try {
			return field.get(findClazz);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("service access fail. Caused by:" + ex.getMessage(), ex);
		}
	}

}
