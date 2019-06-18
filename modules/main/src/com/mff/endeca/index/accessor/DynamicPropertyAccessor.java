package com.mff.endeca.index.accessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.GenerativePropertyAccessor;
import atg.repository.search.indexing.IndexingOutputConfig;
import atg.repository.search.indexing.specifier.OutputProperty;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * Generative Dynamic Attribute Accessor
 *
 * @author foldenburg
 */
public class DynamicPropertyAccessor extends GenerativePropertyAccessor {

	private Repository externalRepository;
	private List<String> mapProperties;
	private String itemDescriptor;
	private String matchOn;

	/**
	 * getMetaPropertyValue function returns to the search engine the text value
	 * for the refinement, if present
	 *
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getPropertyNamesAndValues(Context context,
			RepositoryItem item, String propertyName, PropertyTypeEnum type,
			boolean yes) {

		Map<String, Object> dimMap = new HashMap<String, Object>();

		try {

			RepositoryItem dynamicProperties = getExternalRepository().getItem(
					item.getRepositoryId(), getItemDescriptor());

			if (dynamicProperties != null) {

				if (isLoggingDebug())
					logDebug("Processing SKU: " + item.getRepositoryId());

				for (String map : getMapProperties()) {

					if (isLoggingDebug())
						logDebug("processing facet map: " + map);

					Map<String, Object> propMap = new HashMap<String, Object>();
					propMap = (Map<String, Object>) dynamicProperties
							.getPropertyValue(map);
					if (!propMap.isEmpty()) {

						if (isLoggingDebug())
							logDebug("property map: " + propMap);

						for (Map.Entry<String, Object> prop : propMap
								.entrySet()) {

							if (isLoggingDebug()) {
								logDebug("processing entry: " + prop);
								logDebug("key: : " + prop.getKey());
								logDebug("value: " + prop.getValue());
								logDebug("property name from key: "
										+ getPropertyNameFromKey(prop.getKey()));
							}

							dimMap.put(getItemDescriptor() + "."
									+ getPropertyNameFromKey(prop.getKey()),
									prop.getValue());

						}

					} else {

						if (isLoggingDebug())
							logDebug("prop map: " + map + " is EMPTY");

					}
				}

			}

		} catch (RepositoryException e) {
			logError("Repository Error: " + e);
		}

		return dimMap;
	}

	String getPropertyNameFromKey(String pKey) {

		String property = pKey.substring(
				pKey.lastIndexOf(getMatchOn()) + getMatchOn().length(),
				pKey.length()).replaceAll("_", "");

		return property;
	}

	@Override
	public boolean ownsDynamicPropertyName(String arg0,
			IndexingOutputConfig arg1, OutputProperty arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return the externalRepository
	 */
	public Repository getExternalRepository() {
		return externalRepository;
	}

	/**
	 * @param pExternalRepository
	 *            the externalRepository to set
	 */
	public void setExternalRepository(Repository pExternalRepository) {
		externalRepository = pExternalRepository;
	}

	/**
	 * @return the facetMapProperties
	 */

	/**
	 * @return the itemDescriptor
	 */
	public String getItemDescriptor() {
		return itemDescriptor;
	}

	/**
	 * @param pItemDescriptor
	 *            the itemDescriptor to set
	 */
	public void setItemDescriptor(String pItemDescriptor) {
		itemDescriptor = pItemDescriptor;
	}

	/**
	 * @return the matchOn
	 */
	public String getMatchOn() {
		return matchOn;
	}

	/**
	 * @param pMatchOn
	 *            the matchOn to set
	 */
	public void setMatchOn(String pMatchOn) {
		matchOn = pMatchOn;
	}

	/**
	 * @return the mapProperties
	 */
	public List<String> getMapProperties() {
		return mapProperties;
	}

	/**
	 * @param pMapProperties
	 *            the mapProperties to set
	 */
	public void setMapProperties(List<String> pMapProperties) {
		mapProperties = pMapProperties;
	}

}
