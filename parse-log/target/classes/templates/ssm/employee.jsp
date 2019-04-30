<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<%@ include file="/WEB-INF/view/include/head.jsp" %>
<html>
<head>
    <title>流动管理</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="keywords" content="" />
    <script type="application/x-javascript"> addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); } </script>
    <!-- bootstrap-css -->
    <link rel="stylesheet" href="static/css/bootstrap.css">
    <!-- //bootstrap-css -->
    <!-- Custom CSS -->
    <link href="static/css/style.css" rel='stylesheet' type='text/css' />
    <!-- font CSS -->
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,100,100italic,300,300italic,400italic,500,500italic,700,700italic,900,900italic' rel='stylesheet' type='text/css'>
    <!-- font-awesome icons -->
    <link rel="stylesheet" href="static/css/font.css" type="text/css"/>
    <link href="static/css/font-awesome.css" rel="stylesheet">
    <!-- //font-awesome icons -->

    <script src="static/js/modernizr.js"></script>
    <script src="static/js/jquery.cookie.js"></script>
    <script src="static/js/screenfull.js"></script>
    <script>
        $(function () {
            $('#supported').text('Supported/allowed: ' + !!screenfull.enabled);

            if (!screenfull.enabled) {
                return false;
            }



            $('#toggle').click(function () {
                screenfull.toggle($('#container')[0]);
            });
        });
    </script>
    <!-- charts -->
    <script src="static/js/raphael-min.js"></script>
    <script src="static/js/morris.js"></script>
    <link rel="stylesheet" href="static/css/morris.css">
    <!-- //charts -->
    <!--skycons-icons-->
    <script src="static/js/skycons.js"></script>
    <!--//skycons-icons-->
</head>
<style>
    table.table tbody tr:hover{
        background-color: #D9EDF7;
        cursor: pointer;
    }
    .selected{
        background-color: #5BC0DE;
    }
</style>
<script>

    function quitTalent() {
        var selectedTr = $('table.table tbody tr.selected');
        if(selectedTr&&selectedTr.length>0){
            window.location.href="${ctx}/employee/quitEmp/"+selectedTr.find('td')[0].innerHTML+"?"+Math.random();
        }else{
            alert('请选择一行数据');
        }
    }

    function EntryTalent() {

        var selectedTr = $('table.table tbody tr.selected');
        if(selectedTr&&selectedTr.length>0){
            window.location.href="${ctx}/employee/entryEmp/"+selectedTr.find('td')[0].innerHTML+"?"+Math.random();
        }else{
            alert('请选择一行数据');
        }
    }


    function transTalent() {
        var selectedTr = $('table.table tbody tr.selected');
        if(selectedTr&&selectedTr.length>0){
            window.location.href="${ctx}/employee/transferEmp/"+selectedTr.find('td')[0].innerHTML+"?"+Math.random();
        }else{
            alert('请选择一行数据');
        }
    }


    function search(){
        //为了避免在非第一页条件的查询page页仍为1
        $('#talentForm').find('input[name="pageNum"]').val(1);
        $('#talentForm').submit();
    }

    $(document).ready(function () {
        $('table.table tbody tr').on('click',function (e) {
            if($(this).hasClass('selected')){
                $(this).removeClass('selected');
            }else{
                //取消掉其他tr列的selected class
                $('table.table tbody tr').removeClass('selected');

                //给被点击的tr加class
                $(this).addClass('selected');
            }
        });
        //分页按钮点击事件
        $("ul.pager a").on("click",function (e) {
            if($(this).hasClass("prev")){
                $('#talentForm input[name="pageNum"]').val(parseInt($('#talentForm input[name="pageNum"]').val())-1);
            }else{
                $('#talentForm input[name="pageNum"]').val(parseInt($('#talentForm input[name="pageNum"]').val())+1);
            }
            $('#talentForm').submit();
        })
    });
</script>
<body>

<!-- MAIN CONTENT -->
<div class="main-content">
    <div class="container-fluid">

        <div class="row">
            <div class="col-md-12">
                <div class="panel">
                    <div class="panel-heading" style="padding-bottom: 0px !important;">
                        <div class="row">
                            <form id="talentForm" action="${ctx}/employee" method="post">
                                <input type="hidden" name="pageNum" value="${empList.currentPage}">
                                <div class="col-md-2">
                                    <input type="text" name="name" value="${emp.name}"
                                           class="form-control" placeholder="输入姓名">
                                </div>
                                <div class="col-md-2">
                                    <input type="text" name="tCode" value="${emp.tCode}"
                                           class="form-control" placeholder="输入工号">
                                </div>
                                <div class="col-md-1">
                                    <button type="button" class="btn btn-primary btn-sm" onclick="search()">查询
                                    </button>
                                </div>
                                <div class="col-md-1">
                                    <button type="button" class="btn btn-success btn-sm" onclick="quitTalent()" >离职
                                    </button>
                                </div>
                                <div class="col-md-1">
                                    <button type="button" class="btn btn-info btn-sm" onclick="EntryTalent()">入职
                                    </button>
                                </div>
                                <div class="col-md-1">
                                    <button type="button" class="btn btn-danger btn-sm" onclick="transTalent()">调动
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div class="panel-body">
                        <table class="table table-bordered">
                            <thead>
                            <tr>
                                <th style="display: none">id</th>
                                <th>姓名</th>
                                <th>性别</th>
                                <th>生日</th>
                                <th>部门</th>
                                <th>岗位</th>
                                <th>婚姻状况</th>
                                <th>教育背景</th>
                                <th>毕业院校</th>
                                <th>专业</th>
                                <th>人才来源</th>
                                <th>开始工作日期</th>
                                <th>离职日期</th>
                                <th>状态</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${empList.list}" var="emp">
                                <tr>
                                    <td style="display:none">${emp.id}</td>
                                    <td>${emp.name}</td>
                                    <td>${'1' eq emp.sexual?'男':'女'}</td>
                                    <td><fmt:formatDate value="${emp.birthday}" pattern="yyyy-MM-dd"/></td>
                                    <td>${emp.department.departName}</td>
                                    <td>${emp.position.posName}</td>
                                    <td>${'1' eq emp.marriage?'已婚':'未婚'}</td>
                                    <td>${emp.eduBackground}</td>
                                    <td>${emp.gradInst}</td>
                                    <td>${emp.profession}</td>
                                    <td>${emp.talentSource}</td>
                                    <td><fmt:formatDate value="${emp.startupDate}" pattern="yyyy-MM-dd"/></td>
                                    <td><fmt:formatDate value="${emp.unemployDate}" pattern="yyyy-MM-dd "/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${'0' eq emp.status}">
                                                未入职
                                            </c:when>
                                            <c:when test="${'1' eq emp.status}">
                                                在职
                                            </c:when>
                                            <c:otherwise>
                                                离职
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        ${empList}
                    </div>
                </div>
                <!-- END BORDERED TABLE -->
            </div>
        </div>
    </div>
</div>

</body>
</html>