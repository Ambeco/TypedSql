package com.tbohne.sqlite.processor.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ImmutableMethods {
	public static <Key, Item> ImmutableMap<Key, Item> collectionToMap(
			Collection<Item> collection, Function<Item, Key> getKey)
	{
		ImmutableMap.Builder<Key, Item> result = ImmutableMap.builder();
		for (Item item : collection) {
			result.put(getKey.apply(item), item);
		}
		return result.build();
	}

	public static <Key, Item> ImmutableMap<Key, Item> listToMap(List<Item> collection, Function<Item, Key> getKey) {
		ImmutableMap.Builder<Key, Item> result = ImmutableMap.builder();
		for (int i = 0; i < collection.size(); i++) {
			Item item = collection.get(i);
			result.put(getKey.apply(item), item);
		}
		return result.build();
	}

	public static <In, Out> ImmutableList<Out> listTransform(ImmutableList<In> collection, Function<In, Out> function) {
		ImmutableList.Builder<Out> result = ImmutableList.builder();
		for (int i = 0; i < collection.size(); i++) {
			result.add(function.apply(collection.get(i)));
		}
		return result.build();
	}
}
