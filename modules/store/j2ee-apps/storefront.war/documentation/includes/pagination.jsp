<section id="paginations-docs" class="docs-section">

	<h2>Pagination</h2>
	<p>Pagination allows the users to quickly view available pages and click between them set up as previous next navigation or click-to-the-page type of navigation.</p>

	<div class="docs-example">
		<h4>Example</h4>
  	<div class="pagination">
      <a class="pagination-prev disabled" href="#">Prev</a>
        <a class="page-num active" href="#">1</a>
        <a class="page-num" href="#">2</a>
        <a class="page-num" href="#">3</a>
        <a class="page-num" href="#">4</a>
        <a class="more-pages" href="#">...</a>
        <a class="page-num" href="#">10</a>
      <a class="pagination-next" href="#">Next</a>
  	</div>
	</div>

	<h4 >HTML</h4>
  <format:prettyPrint>
    <jsp:attribute name="htmlString">
      <div class="pagination">
        <a class="pagination-prev disabled" href="#">Prev</a>
          <a class="page-num active" href="#">1</a>
          <a class="page-num" href="#">2</a>
          <a class="page-num" href="#">3</a>
          <a class="page-num" href="#">4</a>
          <a class="more-pages" href="#">...</a>
          <a class="page-num" href="#">10</a>
        <a class="pagination-next" href="#">Next</a>
      </div>
    </jsp:attribute>
  </format:prettyPrint>

	<h4 >SASS</h4>
<pre class="prettyprint">
.pagination {
  @include clearfix;
  a {
    @include transition;
    background: $white;
    padding: 5px 10px;
    margin: 2px;
    border: 1px solid lighten($black, 20);
    text-decoration: none;
    display:block;
    float:left;
    &:hover{
      text-decoration: none;
      background-color: lighten($black, 90);
    }
    &.active {
      text-decoration: none;
      border-color: transparent;
      color:$white;
      background-color: $link-color;
      pointer-events: none;
      cursor: default;
    }
    &.disabled {
      color: $disabled-link-color;
      text-decoration: none;
      border-color: lighten($disabled-link-color, 40);
      pointer-events: none;
      cursor: default;
    }
  }
}
</pre>

</section>
