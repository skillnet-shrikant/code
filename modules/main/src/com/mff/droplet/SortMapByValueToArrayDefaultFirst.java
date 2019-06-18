package com.mff.droplet;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class SortMapByValueToArrayDefaultFirst extends DynamoServlet {
	
	public static final ParameterName MAP = ParameterName
			.getParameterName("map");
	public static final ParameterName DEFAULT_ID = ParameterName
			.getParameterName("defaultId");
	public static final ParameterName OUTPUT = ParameterName
			.getParameterName("output");
	public static final ParameterName EMPTY = ParameterName
			.getParameterName("empty");
	public static String SORTED_ARRAY = "sortedArray";
	public static String SORTED_ARRAY_SIZE = "sortedArraySize";

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String defaultId = pRequest.getParameter(DEFAULT_ID);
		
		Map map = (Map) pRequest.getObjectParameter(MAP);
		
		vlogDebug("default Item Id: " + defaultId);
		if (this.isLoggingDebug()){
			this.logDebug("input map to be sorted: " + map);
		}

		if (map != null && map.size() != 0) {
			String defaultMapKey = this.getDefaultMapKey(map, defaultId);

			if (this.isLoggingDebug()) {
				this.logDebug("Map size: " + map.size());
				if (defaultMapKey != null) {
					this.logDebug("Default entry was found in the map");
				} else {
					this.logDebug("Default entry was not found in the map");
				}
			}

			Object[] sortedArray = this.getSortedArray(map, defaultMapKey);
			
			pRequest.setParameter(SORTED_ARRAY, sortedArray);
			pRequest.setParameter(SORTED_ARRAY_SIZE, Integer.valueOf(sortedArray.length));
			pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
		} else {
			vlogDebug("map parameters is null or empty");
			pRequest.setParameter(SORTED_ARRAY, (Object) null);
			pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
		}
	}

	protected Object[] getSortedArray(Map pMap, String pDefaultKey) {
		
		LinkedList sortedItems = sortByValue(pMap);

		if (StringUtils.isBlank(pDefaultKey)) {
			return sortedItems.toArray();
		} else {
			LinkedList sortedItemsWithDefaultFirst = new LinkedList();
			Iterator i$ = sortedItems.iterator();
			
			vlogDebug("getSortedArray(): pDefaultKey: " + pDefaultKey);

			while (i$.hasNext()) {
				
				Map.Entry<String, RepositoryItem> mapItem = (Entry<String, RepositoryItem>) i$.next();
				String itemKey = (String) mapItem.getKey();
				
				if (pDefaultKey.equalsIgnoreCase(itemKey)) {
					vlogDebug("getSortedArray(): Deafult Item found, making it as first.");
					sortedItemsWithDefaultFirst.addFirst(mapItem);
				} else {
					sortedItemsWithDefaultFirst.addLast(mapItem);
				}
			}
			
			vlogDebug("getSortedArray(): sortedItemsWithDefaultFirst: " + sortedItemsWithDefaultFirst);

			return sortedItemsWithDefaultFirst.toArray();
		}
	}
	
	private LinkedList sortByValue(Map map) {
		
		LinkedList list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, RepositoryItem>>(){
			
			public int compare( Map.Entry<String, RepositoryItem> o1, Map.Entry<String, RepositoryItem> o2 )
            {
                return (o1.getValue().getRepositoryId()).compareTo( o2.getValue().getRepositoryId());
            }
		});

		return list;
	}

	protected String getDefaultMapKey(Map pMap, String pDefaultId) {
		String defaultMapKey = null;
		if (pMap != null && pDefaultId != null) {
			Set keys = pMap.keySet();
			Object key = null;
			Object value = null;
			RepositoryItem item = null;
			Iterator keyterator = keys.iterator();

			while (keyterator.hasNext()) {
				key = keyterator.next();
				value = pMap.get(key);
				if (value != null && value instanceof RepositoryItem) {
					item = (RepositoryItem) value;
					if (item.getRepositoryId().equals(pDefaultId)) {
						defaultMapKey = (String) key;
						break;
					}
				}
			}

			return defaultMapKey;
		} else {
			return null;
		}
	}
}