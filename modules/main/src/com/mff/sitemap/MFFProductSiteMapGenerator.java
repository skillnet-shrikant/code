package com.mff.sitemap;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryView;
import atg.repository.seo.UrlParameter;
import atg.repository.seo.UrlParameterLookup;
import atg.sitemap.DynamicSitemapGenerator;

public class MFFProductSiteMapGenerator extends DynamicSitemapGenerator {

  private Pattern pattern;
  
  protected void populateUrlParameters(UrlParameter pParameters[], UrlParameterLookup pLookups[]) {
    vlogDebug("populateUrlParameters called");
    label0: for (int i = 0; i < pParameters.length; i++) {
      UrlParameter param = pParameters[i];
      int j = 0;
      do {
        if (j >= pLookups.length) continue label0;
        UrlParameterLookup lookup = pLookups[j];
        String lookupName = lookup.getName();
        if (lookupName != null && param.getItemName().equals(lookupName)) {
          param.setLookup(lookup);
          String value = null;
          try {
            value = lookup.getValue(param.getPropertyName(), null, null);
          } catch (RepositoryException e) {
            value = null;
          }
          if (value != null && param.isEscaped()) 
            value = format(value.toLowerCase());
          param.setValue(value == null ? "" : value);
          continue label0;
        }
        i++;
      } while (true);
    }
    vlogDebug("populateUrlParameters Ended");
  }
  
  public String format(String pString) {
    Matcher matcher = getPattern().matcher(pString);
    return matcher.replaceAll("-");
  }

  public Pattern getPattern() {
    return pattern;
  }

  public void setPattern(Pattern pPattern) {
    pattern = pPattern;
  }
  
  @Override
  protected Query getQuery(Repository pRepository, String pItemDescriptorName)
      throws RepositoryException
    {
      RepositoryView view = pRepository.getView(pItemDescriptorName);
      QueryBuilder qb = view.getQueryBuilder();
      Query[] q = new Query[2];
      Calendar lCal=new GregorianCalendar();
      Date lToday=lCal.getTime();
      QueryExpression expr1 = qb.createPropertyQueryExpression("startDate");
      QueryExpression expr2 = qb.createConstantQueryExpression(lToday);
      QueryExpression expr3 = qb.createPropertyQueryExpression("endDate");
      
      Query[] tmpQ = new Query[2];
      tmpQ[0] = qb.createIsNullQuery(expr1);
      tmpQ[1] = qb.createComparisonQuery(expr1, expr2, QueryBuilder.LESS_THAN_OR_EQUALS);
      q[0]=qb.createOrQuery(tmpQ);
      
      tmpQ = new Query[2];
      tmpQ[0] = qb.createIsNullQuery(expr3);
      tmpQ[1] = qb.createComparisonQuery(expr3, expr2, QueryBuilder.GREATER_THAN_OR_EQUALS);
     
      q[1] = qb.createOrQuery(tmpQ);
      
      Query query = qb.createAndQuery(q);
      return query;
    }
}
