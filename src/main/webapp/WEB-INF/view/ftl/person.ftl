<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="/spring-mvc-view/js/jquery-1.10.2.js"></script>
</head>
<body>
<input class="searchbutton" id="btn-search-down" type="button" style="margin-left:132px;" value="查询"  />
<!-- 测试freemarker -->
${key!}
<script>
//查询
$("#btn-search-down").click(function(){
	//alert('');
	$.post("entrust",{name:"test",age:18},function(data) {
		alert(data);
	});
});
</script>
<br/>
<br/>
|${basePath}|
</body>
</html>
