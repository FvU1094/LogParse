<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp" %>
<%@ include file="/WEB-INF/view/include/head.jsp" %>
<!doctype html>
<html lang="en">

<head>
    <title>Home</title>
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
<script>
    $(document).ready(function () {

        $('#sidebar-nav a').on('click', function (e) {
            var href = $(this).attr('data-href');
            if(href){
                $('#main').prop('src',href);
            }
        });
    })


</script>
<body>
<!-- WRAPPER -->
<div id="wrapper">
    <!-- NAVBAR -->



            <br>
            <section class="title-bar">
                <div class="logo" style="margin-left:90px">
                    <h1><a href="index.html"><img src="static/images/logo.png" alt="" />&nbspHR</a></h1>
                </div>
                <div class="full-screen">
                    <section class="full-top">
                        <button id="toggle"><i class="fa fa-arrows-alt" aria-hidden="true"></i></button>
                    </section>
                </div>
                <div class="w3l_search">
                    <form action="#" method="post">
                        <input type="text" name="search" value="Search" onfocus="this.value = '';" onblur="if (this.value == '') {this.value = 'Search';}" required="">
                        <button class="btn btn-default" type="submit"><i class="fa fa-search" aria-hidden="true"></i></button>
                    </form>
                </div>
                <div class="header-right" style="margin-right:20px">
                    <div class="profile_details_left">
                        <div class="header-right-left">
                            <!--notifications of menu start -->
                            <ul class="nofitications-dropdown">
                                <li class="dropdown head-dpdn">
                                    <c:if test="${username!=null }">${username }</c:if>
                                </li>
                                <div class="clearfix"> </div>
                            </ul>
                        </div>
                        <div class="profile_details">
                            <ul>
                                <li class="dropdown profile_details_drop">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                                        <div class="profile_img">
                                            <span class="prfil-img"><i class="fa fa-user" aria-hidden="true"></i></span>
                                            <div class="clearfix"></div>
                                        </div>
                                    </a>
                                    <ul class="dropdown-menu drp-mnu">

                                        <li> <a href="${ctx}/login"><i class="fa fa-sign-out"></i> Logout</a> </li>
                                    </ul>
                                </li>
                            </ul>
                        <div class="clearfix"> </div>

                    </div>

                </div>
                <div class="clearfix"> </div>
            </section>



    <!-- END NAVBAR -->
    <!-- LEFT SIDEBAR -->

    <div align="right">
        <!-- MAIN CONTENT -->
        <iframe id="main" src="" style="height: 66%;width: 96%;border: 0;"
                frameborder="no"></iframe>

    </div>

    <div id="sidebar-nav" >
        <nav class="main-menu">
            <ul>
                <li>
                    <a href="#" data-href="${ctx}/positions" class="active">
                        <i class="fa fa-home nav_icon"></i>
                        <span class="nav-text">
					岗位管理
					</span>
                    </a>
                </li>
                <li>
                    <a href="#" data-href="${ctx}/department" class="">
                        <i class="fa fa-check-square-o nav_icon"></i>
                        <span class="nav-text">
				部门管理
				</span>
                    </a>
                </li>
                <li>
                    <a href="#" data-href="${ctx}/user" class="">
                        <i class="fa fa-bar-chart nav_icon"></i>
                        <span class="nav-text">
						用户管理
					</span>
                    </a>
                </li>
                <li>
                    <a href="#" data-href="${ctx}/talent" class="">
                        <i class="fa fa-cogs" aria-hidden="true"></i>
                        <span class="nav-text">
					人才信息管理
				</span>
                    </a>
                </li>

                <li class="has-subnav">
                    <a href="javascript:;">
                        <i class="fa fa-file-text-o nav_icon"></i>
                        <span class="nav-text">员工流动管理</span>
                        <i class="icon-angle-right"></i><i class="icon-angle-down"></i>
                    </a>
                    <ul>
                        <li>
                            <a class="subnav-text" data-href="${ctx}/employee" href="#" >
                                员工调动
                            </a>
                        </li>
                        <li>
                            <a class="subnav-text" href="#" data-href="${ctx}/transfer" >
                                流转查询
                            </a>
                        </li>
                    </ul>
                </li>
                <li class="has-subnav">
                    <a href="javascript:;">
                        <i class="fa fa-file-text-o nav_icon"></i>
                        <span class="nav-text">报表管理</span>
                        <i class="icon-angle-right"></i><i class="icon-angle-down"></i>
                    </a>
                    <ul>
                        <li>
                            <a class="subnav-text" href="#" data-href="${ctx}/departchart">
                                部门人员统计
                            </a>
                        </li>
                        <li>
                            <a class="subnav-text" href="#" data-href="${ctx}/positionchart">
                                岗位人员统计
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>
            <ul class="logout">
                <li>
                    <a href="${ctx}/login">
                        <i class="icon-off nav-icon"></i>
                        <span class="nav-text">
			Logout
			</span>
                    </a>
                </li>
            </ul>
        </nav>
        </div>

    <!-- END LEFT SIDEBAR -->
    <!-- MAIN -->

    <!-- END MAIN -->
    <div class="clearfix"></div>
    <!-- footer -->
    <div class="footer">
        <p>Copyright &copy; 2018.Company name All rights reserved.</p>
    </div>
    <!-- //footer -->
</div>
<!-- END WRAPPER -->
<!-- Javascript -->
</body>

</html>

