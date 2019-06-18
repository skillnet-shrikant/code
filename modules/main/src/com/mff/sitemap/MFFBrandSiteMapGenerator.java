package com.mff.sitemap;

import java.util.ArrayList;
import java.util.List;

import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.OptionalInt;
import com.endeca.navigation.RefinementConfig;
import com.endeca.navigation.RefinementConfigList;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;

import atg.sitemap.DynamicSitemapGenerator;
import atg.sitemap.SitemapGeneratorService;
import atg.sitemap.SitemapTools;

public class MFFBrandSiteMapGenerator extends DynamicSitemapGenerator {

  private static final String ENCODING_UTF8 = "UTF-8";
  private static final String BRAND_DIM_NAME = "product.brand";
  
  private String mEndecaMdexHost;
  private int mEndecaMdexPort;
  
	public String getEndecaMdexHost() {
    return mEndecaMdexHost;
  }

  public void setEndecaMdexHost(String pEndecaMdexHost) {
    mEndecaMdexHost = pEndecaMdexHost;
  }

  public int getEndecaMdexPort() {
    return mEndecaMdexPort;
  }

  public void setEndecaMdexPort(int pEndecaMdexPort) {
    mEndecaMdexPort = pEndecaMdexPort;
  }

  @SuppressWarnings("unchecked")
	public void generateSitemapUrls(String pItemDescriptorName,
			SitemapGeneratorService pSitemapGeneratorService, String pSiteId) {
	  vlogDebug("Generating brand Sitemap urls - Started");
	  List<String>lBrandUrls  = fetchAllBrandsFromEndeca();
	  if(lBrandUrls!=null && lBrandUrls.size()>0) {
			SitemapTools sitemapTools = pSitemapGeneratorService
					.getSitemapTools();
			StringBuilder sb = new StringBuilder();
			sitemapTools.appendSitemapHeader(sb);
			for(String lBrandUrl : lBrandUrls) {
			  String url = SitemapTools.addPrefixToUrl(getUrlPrefix(), lBrandUrl);
		    sb.append(sitemapTools.generateSitemapUrlXml(
		        SitemapTools.escapeURL(url), getChangeFrequency(),
		        getPriority().toString(),
		        pSitemapGeneratorService.isDebugMode()));
			}
			sitemapTools.appendSitemapFooter(sb);
			sitemapTools.writeSitemap(sb, getSitemapFilePrefix(), 1);
			}
		vlogDebug("Generating brand Sitemap urls - Ended");
	}

  
  private List<String> fetchAllBrandsFromEndeca() {
    vlogDebug("fetching all brands  - Started");
    
    try {
      ENEConnection lENEConnection = new HttpENEConnection(getEndecaMdexHost(), getEndecaMdexPort());
      UrlENEQuery lQuery = new UrlENEQuery("N=0",ENCODING_UTF8); //initial query to dimendion id of brand
      lQuery.setNavNumERecs(0);
      ENEQueryResults lResult = lENEConnection.query(lQuery);
      if(lResult.containsNavigation()) {
        Dimension dim = lResult.getNavigation().getCompleteDimensions().getDimension(BRAND_DIM_NAME);
        if(dim!=null ) {
          vlogDebug("brand dim id -{0}",dim.getId());
          lQuery = new UrlENEQuery("N=0",ENCODING_UTF8);//second query to get refinements of brand
          lQuery.setNavNumERecs(0);
          //lQuery.setNe(""+dim.getId());
          RefinementConfigList refList = new RefinementConfigList();
          RefinementConfig refConf = new RefinementConfig(dim.getId());
          refConf.setExposed(true);
          //OptionalInt refCount = new OptionalInt(9999);
          refConf.setDynamicRefinementCount(new OptionalInt(9999));
          refList.add(refConf);
          lQuery.setNavRefinementConfigs(refList);
          lResult = lENEConnection.query(lQuery);
          if(lResult.containsNavigation()) {
            dim = lResult.getNavigation().getCompleteDimensions().getDimension(BRAND_DIM_NAME);
            if(dim!=null ) {
              List<String> lBrandUrls = new ArrayList<String>();
              if( dim.getImplicitLocations()!=null) {
                for(DimLocation dimLoc : (List<DimLocation>)dim.getImplicitLocations()) {
                  String lBrandSeoUrl = getSeoUrl(dimLoc.getDimValue());
                  if(!lBrandUrls.contains(lBrandSeoUrl)) {
                    lBrandUrls.add(lBrandSeoUrl);
                  }else {
                    vlogDebug("repeated after seo - {0}",lBrandSeoUrl);
                  }
                }
              }
              for(DimVal lDimVal : (List<DimVal>)dim.getRefinements()) {
                String lBrandSeoUrl = getSeoUrl(lDimVal);
                if(!lBrandUrls.contains(lBrandSeoUrl)) {
                    lBrandUrls.add(lBrandSeoUrl);
                }else {
                  vlogDebug("repeated after seo - {0}",lBrandSeoUrl);
                }
              }
              vlogDebug("No of brands generated - {0}", lBrandUrls.size());
              vlogDebug("fetching all brands  - ended");
              return lBrandUrls;
            }else {
              logError("brand query results brand dim is null");
            }
            
          }else {
            vlogError("brand query results dont have navigtion");
          }
        }else {
          vlogError("N=0 brand dim is null");
        }
      }else {
        vlogError("N=0 results dont have navigtion");
      }
    } catch (UrlENEQueryParseException e) {
      vlogError(e.getMessage());
    } catch (ENEQueryException e) {
      vlogError(e.getMessage());
    }
    vlogDebug("brand urls is Null");
    vlogDebug("fetching all brands  - ended");
    return null;
  }
  
  private String getSeoUrl(DimVal pDimVal) {
    StringBuffer seoUrl = new StringBuffer();
    seoUrl.append("/brand/");
    seoUrl.append(cleanString(pDimVal.getName()));
    seoUrl.append("/_/N-");
    seoUrl.append(pDimVal.getId());
    return seoUrl.toString();
  }
  
  private  String cleanString(String value) {
    String result = "";
    if(value!=null) {
      result = value.toLowerCase().replaceAll("&\\S+?;|[^a-zA-Z0-9\\s]", "")
          .replaceAll("\\s+", "-");
    }
    return result;
  }
	
}
