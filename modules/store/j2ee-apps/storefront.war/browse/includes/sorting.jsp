<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/mff/droplet/NavigationUrlGenerator"/>

	<%-- Page Variable --%>
	<dsp:getvalueof var="requestUri" value="${requestScope['javax.servlet.forward.request_uri']}"/>

	<%-- product sorting --%>
	<div class="category-sort dropdown" data-dropdown>
		<div class="button secondary dropdown-toggle" id="category-sort-title" aria-controls="category-sort-menu" aria-expanded="false">
			Sort by <span class="icon icon-arrow-down" aria-hidden="true"></span>
		</div>
		<div class="dropdown-menu" id="category-sort-menu" aria-expanded="false">
			<ul class="menu-list" data-action-tag="Sort by" role="menu" aria-labelledby="category-sort-title">
				<%--
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Ns"/>
						<dsp:param name="paramValue" value="product.featured|1"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="product.featured|1">Featured</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				--%>
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Ns"/>
						<dsp:param name="paramValue" value="sku.activePrice|1"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="sku.activePrice|1">High to low</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Ns"/>
						<dsp:param name="paramValue" value="sku.activePrice|0"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="sku.activePrice|0">Low to high</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Ns"/>
						<dsp:param name="paramValue" value="product.description|0"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="product.description|0">A to Z</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Ns"/>
						<dsp:param name="paramValue" value="product.description|1"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="product.description|1">Z to A</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
			<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Ns"/>
						<dsp:param name="paramValue" value="product.startDate|1"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="product.startDate|1">Newest</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				<%--
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Ns"/>
						<dsp:param name="paramValue" value="sku.clearance|1"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="sku.clearance|1">On Sale</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				--%>
			</ul>
		</div>
	</div>

	<%-- items per page --%>
	<dsp:getvalueof var="totalNumRecs" param="totalNumRecs" />
	<c:set var="ippClass" value="" scope="request" />
	<c:if test="${totalNumRecs le 24}">
		<c:set var="ippClass" value="hide" scope="request" />
	</c:if>

	<div class="category-items-per-page dropdown ${ippClass}" data-dropdown>
		<div class="button secondary dropdown-toggle" id="items-per-page-title" aria-controls="items-per-page-menu" aria-expanded="false">
			Items To View <span class="icon icon-arrow-down" aria-hidden="true"></span>
		</div>
		<div class="dropdown-menu" id="items-per-page-menu" aria-expanded="false">
			<ul class="menu-list" data-action-tag="Items To View" role="menu" aria-labelledby="items-per-page-title">
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Nrpp"/>
						<dsp:param name="paramValue" value="24"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="24">24</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Nrpp"/>
						<dsp:param name="paramValue" value="48"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="48">48</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Nrpp"/>
						<dsp:param name="paramValue" value="72"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="72">72</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
				<li role="presentation">
					<dsp:droplet name="NavigationUrlGenerator">
						<dsp:param name="paramName" value="Nrpp"/>
						<dsp:param name="paramValue" value="99999"/>
						<dsp:param name="requestUri" value="${requestUri}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="url" param="url"/>
							<a role="menuitem" tabindex="-1" href="#" data-sortvalue="${url}" data-sortparam="99999">View All</a>
						</dsp:oparam>
					</dsp:droplet>
				</li>
			</ul>
		</div>
	</div>

</dsp:page>
