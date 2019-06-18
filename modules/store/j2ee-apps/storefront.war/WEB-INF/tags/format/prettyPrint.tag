<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="htmlString"%>

<pre class="prettyprint">
<c:out value="${fn:replace(htmlString,'	','  ')}"/>
</pre>