<section id="primary-nav-docs" class="docs-section">

	<h2>Primary Nav</h2>
	<p>The primary nav is the main category navigation in the header. It can have categories and subcategories revealed on hover.</p>
	<p>It can be horizontal (preferably under the utility) or vertical on left.</p>
	<p>It's class structure will look something like this:</p>

	<div class="docs-example">
	<h4>Example</h4>

	<div class="primary-nav" data-primarynav>
	<nav>
	<section class="primary-nav-item">
		<h2 class="primary-nav-button">
			<a href="#" class="nav-link">Hats</a>
		</h2>
		<ul class="primary-nav-menu" data-flyoutnav>
			<li class="sub-nav-item">
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Stovepipes</a>
				</h3>
				<div class="sub-nav-menu">
					<section>
						<h4 class="sub-nav-title">
							<a href="#" class="nav-link">Stovepipes</a>
						</h4>
						<ul class="sub-nav-list">
							<li>
								<a href="#" class="nav-link">Uncle Sam</a>
							</li>
							<li>
								<a href="#" class="nav-link">Lincoln</a>
							</li>
							<li>
								<a href="#" class="nav-link">Seuss</a>
							</li>
						</ul>
					</section>
				</div>
			</li>
		<li>
			<h3 class="sub-nav-button">
				<a href="#" class="nav-link">Porkpies</a>
			</h3>
			<div class="sub-nav-menu">
				<section>
					<h4 class="sub-nav-title">
						<a href="#" class="nav-link">Porkpies</a>
					</h4>
					<ul class="sub-nav-list">
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
					</ul>
				</section>
			</div>
		</li>
		<li>
			<h3 class="sub-nav-button">
				<a href="#" class="nav-link">Tricorners</a>
			</h3>
			<div class="sub-nav-menu">
				<section>
					<h4 class="sub-nav-title">
						<a href="#" class="nav-link">Tricorners</a>
					</h4>
					<ul class="sub-nav-list">
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
						<li>
							<a href="#" class="nav-link">category</a>
						</li>
					</ul>
				</section>
			</div>
		</li>
		</ul>
	</section>
	<section class="primary-nav-item">
		<h2 class="primary-nav-button">
			<a href="#" class="nav-link">Monocles</a>
		</h2>
		<ul class="primary-nav-menu">
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Ladies</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Mens</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Kids</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
		</ul>
	</section>
	<section class="primary-nav-item">
		<h2 class="primary-nav-button">
			<a href="#" class="nav-link">Knickers</a>
		</h2>
		<ul class="primary-nav-menu">
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Wool</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Lambskin</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Culottes</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
		</ul>
	</section>
	<section class="primary-nav-item">
		<h2 class="primary-nav-button">
			<a href="#" class="nav-link">Canes</a>
		</h2>
		<ul class="primary-nav-menu">
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Woven Ivory</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Swordsticks</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
			<li>
				<h3 class="sub-nav-button">
					<a href="#" class="nav-link">Magic</a>
				</h3>
				<div class="sub-nav-menu"></div>
			</li>
		</ul>
	</section>
	</nav>
	</div>
	</div>

	<h4 >HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="primary-nav" data-primarynav>
			  <nav>
			    <section class="primary-nav-item">
			      <h2 class="primary-nav-button">
			        <a href="#" class="nav-link">Hats</a>
			      </h2>
			      <ul class="primary-nav-menu"  data-flyoutnav>
			        <li class="sub-nav-item">
			          <h3 class="sub-nav-button">
			            <a href="#" class="nav-link">Stovepipes</a>
			          </h3>
			          <div class="sub-nav-menu">
			            <section>
			              <h4 class="sub-nav-title">
			                <a href="#" class="nav-link">Stovepipes</a>
			              </h4>
			              <ul class="sub-nav-list">
			                <li><a href="#" class="nav-link">...</a></li>
			                <li><a href="#" class="nav-link">...</a></li>
			                <li><a href="#" class="nav-link">...</a></li>
			                <li><a href="#" class="nav-link">...</a></li>
			              </ul>
			            </section>
			          </div>
			        </li>
			        <li>
			          <h3 class="sub-nav-button"><a href="#" class="nav-link">...</a></h3>
			          <div class="sub-nav-menu">
			            <section>
			              <h4 class="sub-nav-title"><a href="#" class="nav-link">...</a></h4>
			              <ul class="sub-nav-list">
			                <li><a href="#" class="nav-link">...</a></li>
			                <li><a href="#" class="nav-link">...</a></li>
			                <li><a href="#" class="nav-link">...</a></li>
			                <li><a href="#" class="nav-link">...</a></li>
			              </ul>
			            </section>
			          </div>
			        </li>
			      </ul>
			    </section>
			    <section class="primary-nav-item">...</section>
			    <section class="primary-nav-item">...</section>
			  </nav>
			</div>
		</jsp:attribute>
	</format:prettyPrint>
</section>
