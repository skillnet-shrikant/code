package mff.remote.editor.service;

import java.util.Collection;

import atg.remote.assetmanager.editor.model.AssetViewUpdate;
import atg.remote.assetmanager.editor.model.PropertyEditorAssetViewUpdate;
import atg.remote.assetmanager.editor.model.PropertyUpdate;
import atg.remote.assetmanager.editor.service.AssetEditorInfo;
import atg.remote.assetmanager.editor.service.PromotionAssetServiceImpl;
import atg.repository.RepositoryItem;

public class MFFPromotionAssetService extends PromotionAssetServiceImpl {

	@Override
	public void preValidateNewAsset(AssetEditorInfo pEditorInfo, Collection pUpdates) {
		// TODO Auto-generated method stub
		super.preValidateNewAsset(pEditorInfo, pUpdates);
		
		PropertyUpdate idPropValue = findPropertyFromUpdates("$id", pEditorInfo, pUpdates);
        if ( idPropValue == null) {
        	RepositoryItem item = (RepositoryItem)pEditorInfo.getAssetWrapper().getAsset();
        	if(item == null || item.getRepositoryId() == null) {
        		pEditorInfo.getAssetService().addError("Must set an ID for this item");
        		return;
        	} else if (item != null && item.getRepositoryId() != null && item.getRepositoryId().length() > 10) {
        		pEditorInfo.getAssetService().addError("Promotion ID should be no longer than 10 characters.");
        	}
        }
        else {
            String creationId = (String)idPropValue.getPropertyValue();
            if ( creationId != null && creationId.length() > 10) {
                       pEditorInfo.getAssetService().addError("Promotion ID should be no longer than 10 characters.");
            }
            return;
        }

	}
    protected PropertyUpdate findPropertyFromUpdates(String propName, AssetEditorInfo pEditorInfo, Collection pUpdates){
        if (pUpdates != null) {
            for ( Object update : pUpdates) {
                AssetViewUpdate viewUpdate = (AssetViewUpdate)update;
                if ((viewUpdate instanceof PropertyEditorAssetViewUpdate)) {
                    PropertyEditorAssetViewUpdate propertyEditorViewUpdate = (PropertyEditorAssetViewUpdate)viewUpdate;
                    Collection propertyUpdates = propertyEditorViewUpdate.getPropertyUpdates();
                    if (propertyUpdates != null) {
                        for ( Object propUpdate : propertyUpdates) {
                            PropertyUpdate propertyUpdate = (PropertyUpdate)propUpdate;
                            if (propName.equals(propertyUpdate.getPropertyName())) {
                                if(isLoggingDebug())
                                    logDebug("Retrieved "+propName + " property from pUpdates");
                                return propertyUpdate;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
