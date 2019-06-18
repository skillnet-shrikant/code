package mff.typeahead.rest.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import mff.typeahead.beans.ResultBean;
import mff.typeahead.beans.SectionBean;
import mff.typeahead.beans.SectionResultsBean;
import mff.typeahead.endeca.EndecaTypeaheadService;
import mff.typeahead.endeca.SearchServiceException;
import mff.typeahead.endeca.adapter.BrandBeanAdapter;
import mff.typeahead.endeca.adapter.DepartmentBeanAdapter;
import mff.typeahead.endeca.adapter.ProductBeanAdapter;
import mff.typeahead.util.StringUtil;

import com.endeca.navigation.ENEQueryResults;
import com.endeca.soleng.urlformatter.seo.SeoUrlFormatter;




/**
 * Resource class for detail typeahead service. Generates detail results
 * from the supplied search term.
 *
 * @author foldenburg
 */

@Path("detail")
public class Detail {

	private EndecaTypeaheadService endecaService;
	private int maxTerms = 10; // default to 10
	private int maxProductResultCount = 5; //default to 5
	private int maxCategoryResultCount = 5; //default to 5
	private int maxBrandResultCount = 5; //default to 5
	private int absoluteMaxResultCount = 50; //default to 50
	private SeoUrlFormatter seoUrlFormatter = null;

	/**
	 * Main resource method for the typeahead detailed results service. Queries custom
	 * Endeca index for term suggestion results.
	 *
	 * @return A SuggestionResultsBean containing term suggestions
	 */

	@GET
	@Path("{term}.js")
	@Produces(MediaType.APPLICATION_JSON)
	public SectionResultsBean getTerms(@PathParam("term") String term,
			@QueryParam("maxTerms") String pMaxTerms,
			@QueryParam("maxCategories") String pMaxCategories,
			@QueryParam("maxBrands") String pMaxBrands,
			@QueryParam("maxProducts") String pMaxProducts)
		{

		endecaService = new EndecaTypeaheadService();

		boolean navSearchEnabled = endecaService.getTypeaheadNavsearchEnabled();
		boolean dimSearchEnabled = endecaService.getTypeaheadDimsearchEnabled();

		// Set some values from the endeca typeahead service.


		setAbsoluteMaxResultCount(endecaService.getAbsoluteMaxTermResultCount());


		setMaxCategoryResultCount(endecaService.getmMaxProductResultCount());
		if (pMaxCategories != null && !pMaxCategories.isEmpty()) {
			setMaxCategoryResultCount(Integer.parseInt(pMaxCategories));
		}


		setMaxBrandResultCount(endecaService.getMaxBrandResultCount());
		if (pMaxBrands != null && !pMaxBrands.isEmpty()) {
			setMaxBrandResultCount(Integer.parseInt(pMaxBrands));
		}

		setMaxProductResultCount(endecaService.getmMaxProductResultCount());
		if (pMaxProducts != null && !pMaxProducts.isEmpty()) {
			setMaxProductResultCount(Integer.parseInt(pMaxProducts));
		}


		SectionResultsBean response = new SectionResultsBean();
		List<ResultBean> brand = new ArrayList<ResultBean>();
		List<ResultBean> departments = new ArrayList<ResultBean>();
		List<ResultBean> product = new ArrayList<ResultBean>();

		String sanitizedSearchTerm = StringUtil.sanitize(term);


		try {
			// Do the endeca query

			ENEQueryResults eneResults = endecaService.performCombinedQuery(
					sanitizedSearchTerm,
					Math.min(maxTerms, absoluteMaxResultCount));


			//Get seoUrlFormatter from StringUtil via static reference in order to reduce object creation as traffic grows.
			StringUtil.initSeoUrlFormatter(); //initializes the formatter and keeps the object for all instances
			seoUrlFormatter = StringUtil.getSeoUrlFormatter();

			// Adapt the raw ENEQuery
			BrandBeanAdapter brandAdapter = new BrandBeanAdapter(eneResults, navSearchEnabled, dimSearchEnabled,seoUrlFormatter,sanitizedSearchTerm,false);
			brand = brandAdapter.getBeansAsList(Math.min(
					maxBrandResultCount, absoluteMaxResultCount));

			SectionBean brands = new SectionBean("Brands", brand);

			DepartmentBeanAdapter deptAdapter = new DepartmentBeanAdapter(eneResults, navSearchEnabled, dimSearchEnabled, seoUrlFormatter, false);
			departments = deptAdapter.getBeansAsList(Math.min(absoluteMaxResultCount, maxCategoryResultCount));

			SectionBean categories = new SectionBean("Category", departments);

			ProductBeanAdapter productAdapter = new ProductBeanAdapter(eneResults);
			product = productAdapter.getBeansAsList(Math.min(absoluteMaxResultCount, maxProductResultCount));

			SectionBean products = new SectionBean("Products", product);

			List<SectionBean> sections = new ArrayList<SectionBean>();

			if (!categories.getLinks().isEmpty()) {
				sections.add(categories);
			}

			if (!brands.getLinks().isEmpty()) {
				sections.add(brands);
			}
			if (!products.getLinks().isEmpty()) {
				sections.add(products);
			}

			response.setSearchTerm(term);
			response.setSections(sections);

		} catch (SearchServiceException e) {
			// On exception, log and return a 500 error

		}

		return response;

	}

	public int getMaxTerms() {
		return maxTerms;
	}

	public void setMaxTerms(int maxTerms) {
		this.maxTerms = maxTerms;
	}

	public int getAbsoluteMaxResultCount() {
		return absoluteMaxResultCount;
	}

	public void setAbsoluteMaxResultCount(int absoluteMaxResultCount) {
		this.absoluteMaxResultCount = absoluteMaxResultCount;
	}

	public int getMaxProductResultCount() {
		return maxProductResultCount;
	}

	public void setMaxProductResultCount(int maxProductResultCount) {
		this.maxProductResultCount = maxProductResultCount;
	}

	public int getMaxCategoryResultCount() {
		return maxCategoryResultCount;
	}

	public void setMaxCategoryResultCount(int maxCategoryResultCount) {
		this.maxCategoryResultCount = maxCategoryResultCount;
	}

	public int getMaxBrandResultCount() {
		return maxBrandResultCount;
	}

	public void setMaxBrandResultCount(int maxBrandResultCount) {
		this.maxBrandResultCount = maxBrandResultCount;
	}

}
