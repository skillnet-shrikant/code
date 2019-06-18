<section id="utility-nav-docs" class="docs-section">

	<h2>Utility Nav</h2>
	<p>The utility nav is the non-category navigation bar that appears in the header for most eCommerce sites. It commonly holds links like login, cart, email sign up, store locator, etc. </p>
	<p>The utility nav will have a class structure that looks like this:</p>

	<div class="docs-example">
		<h4>Example</h4>

		<div class="utility-nav">
			<nav>
				<ul>
					<li class="utility-nav-item utility-email-signup dropdown" data-dropdown>
						<div class="utility-nav-header dropdown-toggle">
							<a href="#">Email signup</a>
						</div>
						<div class="utility-nav-menu dropdown-menu">
							<form>
								//login form here
							</form>
						</div>
					</li>
					<li class="utility-nav-item utility-store-locator dropdown" data-dropdown>
						<div class="utility-nav-header dropdown-toggle">
							<a href="#">Store locator</a>
						</div>
						<div class="utility-nav-menu dropdown-menu">
							//store locator form here
						</div>
					</li>
					<li class="utility-nav-item utility-gift-card dropdown" data-dropdown>
						<div class="utility-nav-header dropdown-toggle">
							<a href="#">Gift Cards</a>
						</div>
						<div class="utility-nav-menu dropdown-menu">
							//gift card menu here
						</div>
					</li>
					<li class="utility-nav-item utility-login dropdown" data-dropdown>
						<div class="utility-nav-header dropdown-toggle">
							<a href="#">Login/sign in/account</a>
						</div>
						<div class="utility-nav-menu dropdown-menu">
							//login menu here
						</div>
					</li>
					<li class="utility-nav-item utility-mini-cart">
						<div class="mini-cart">
							<div class="mini-cart-header">
								<a href="#">
									<span aria-hidden="true" class="icon icon-cart mini-cart-icon"></span>
									<span class="mini-cart-count">1</span>
									Cart
								</a>
							</div>

							<%@ include file="miniCartContent.jspf" %>
						</div>
					</li>
				</ul>
			</nav>
		</div>

		</div>
	<h4 >HTML</h4>
	<format:prettyPrint>
    <jsp:attribute name="htmlString">
			<div class="utility-nav">
			  <nav>
			    <ul>
			      <li class="utility-nav-item utility-email-signup dropdown" data-dropdown>
			        <div class="utility-nav-header dropdown-toggle">
			          <a href="#">Email signup</a>
			        </div>
			        <div class="utility-nav-menu dropdown-menu">
			          <form>
			            //login form here
			          </form>
			        </div>
			      </li>
			      <li class="utility-nav-item utility-store-locator dropdown" data-dropdown>
			        <div class="utility-nav-header dropdown-toggle">
			          <a href="#">Store locator</a>
			        </div>
			        <div class="utility-nav-menu dropdown-menu">
			          //store locator form here
			        </div>
			      </li>
			      <li class="utility-nav-item utility-gift-card dropdown" data-dropdown>
			        <div class="utility-nav-header dropdown-toggle">
			          <a href="#">Gift Cards</a>
			        </div>
			        <div class="utility-nav-menu dropdown-menu">
			          //gift card menu here
			        </div>
			      </li>
			      <li class="utility-nav-item utility-login dropdown" data-dropdown>
			        <div class="utility-nav-header dropdown-toggle">
			          <a href="#">Login/sign in/account</a>
			        </div>
			        <div class="utility-nav-menu dropdown-menu">
			          //login menu here
			        </div>
			      </li>
			      <li class="utility-nav-item utility-mini-cart">
			        <div class="mini-cart">
			          <div class="mini-cart-header">
			            <a href="#">
			              <span aria-hidden="true" class="icon icon-cart mini-cart-icon"></span>
			              <span class="mini-cart-count">1</span>
			             Cart
			            </a>
			          </div>
			          // include miniCartContent.jspf here
			        </div>
			      </li>
			    </ul>
			  </nav>
			</div>
		</jsp:attribute>
  </format:prettyPrint>
</section>