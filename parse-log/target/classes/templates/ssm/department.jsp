<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<%@ include file="/WEB-INF/view/include/head.jsp" %>
<html>
<head>
    <title>部门管理</title>
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
    //进入创建人才信息的页面
    function createDepart() {
        window.location.href="${ctx}/department/create?"+Math.random();
    }

    function updateDepart() {

        var selectedTr = $('table.table tbody tr.selected');
        if(selectedTr&&selectedTr.length>0){
            window.location.href="${ctx}/department/updateDepart/"+selectedTr.find('td')[0].innerHTML+"?"+Math.random();
        }else{
            alert('请选择一行数据');
        }
    }

    function deleteDepart() {
        var selectedTr = $('table.table tbody tr.selected');
        if(selectedTr&&selectedTr.length>0){
            window.location.href="${ctx}/department/deleteDepart/"+selectedTr.find('td')[0].innerHTML+"?"+Math.random();
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
                            <form id="talentForm" action="${ctx}/department" method="post">
                                <input type="hidden" name="pageNum" value="${departList.currentPage}">
                                <div class="col-md-2">
                                    <input type="text" name="departName" value="${depart.departName}"
                                           class="form-control" placeholder="输入部门">
                                </div>
                                <div class="col-md-2">
                                    <input type="text" name="departCode" value="${depart.departCode}"
                                           class="form-control" placeholder="输入部门编号">
                                </div>
                                <div class="col-md-1">
                                    <button type="button" class="btn btn-primary btn-sm" onclick="search()">查询
                                    </button>
                                </div>
                                <div class="col-md-1">
                                    <button type="button" class="btn btn-success btn-sm" onclick="createDepart()" >新增
                                    </button>
                                </div>
                                <div class="col-md-1">
                                    <button type="button" class="btn btn-info btn-sm" onclick="updateDepart()">修改
                                    </button>
                                </div>
                                <div class="col-md-1">
                                    <button type="button" class="btn btn-danger btn-sm" onclick="deleteDepart()">删除
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
                                <th>部门</th>
                                <th>部门编号</th>
                                <th>部门类型</th>
                                <th>部门电话</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${departList.list}" var="depart">
                                <tr>
                                    <td style="display:none">${depart.id}</td>
                                    <td>${depart.departName}</td>
                                    <td>${depart.departCode}</td>
                                    <td>${depart.departType}</td>
                                    <td>${depart.departPhone}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        ${departList}
                    </div>
                </div>
                <!-- END BORDERED TABLE -->
            </div>
        </div>
    </div>
</div>
</body>
</html>
