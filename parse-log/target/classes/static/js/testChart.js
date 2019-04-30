$.getJSON('data/dataset.json',function drawGroupChart(dataset){
    //定义柱状图的宽高边距属性
    var margin = {top: 20, right:40, bottom: 0, left: 40},
        width = 900,
        height = 300;
    //定义X轴作用范围
    var x0 = d3.scale.ordinal()
        .rangeRoundBands([0, width], .1);

    var x1 = d3.scale.ordinal();
    //定义Y轴作用范围
    var y = d3.scale.linear()
        .range([height, 0]);
    //定义标准颜色样式
    var colorRange = d3.scale.category20();
    var color = d3.scale.ordinal()
        .range(colorRange.range());
    //定义X比例尺
    var xAxis = d3.svg.axis()
        .scale(x0)
        .orient("bottom");
    //定义Y比例尺
    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left")
        .tickFormat(d3.format(".2s"));
    //这里是牵扯到查询会不断切换柱状图，将之前生成的svg删除
    d3.select("#ham-guest-summary-common-group-chart svg").remove();
    //定义鼠标移动时提示信息
    var divTooltip = d3.select("#ham-guest-summary-common-group-chart").append("div").attr("class", "toolTip");
    //创建svg画布preserveAspectRatio，viewBox这两个属性可以支持画布的缩放功能
    var svg = d3.select("#ham-guest-summary-common-group-chart").append("svg")
        .attr("preserveAspectRatio", "xMidYMid meet")
        .attr("viewBox", "0 0 1000 350")
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
    //获取label
    var options = d3.keys(dataset[0]).filter(function(key) { return key !== "label"; });

    dataset.forEach(function(d) {
        d.valores = options.map(function(name) { return {name: name, value: +d[name]}; });
    });

    x0.domain(dataset.map(function(d) { return d.label; }));
    x1.domain(options).rangeRoundBands([0, x0.rangeBand()]);
    y.domain([0, d3.max(dataset, function(d) { return d3.max(d.valores, function(d) { return d.value; }); })]);
    //画X轴
    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);
    //画Y轴
    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("Numbers");

    var bar = svg.selectAll(".bar")
        .data(dataset)
        .enter().append("g")
        .attr("class", "rect")
        .attr("transform", function(d) { return "translate(" + x0(d.label) + ",0)"; });

    bar.selectAll("rect")
        .data(function(d) { return d.valores; })
        .enter().append("rect")
        .attr("width", x1.rangeBand())
        .attr("x", function(d) { return x1(d.name); })
        .attr("y", function(d) { return y(d.value); })
        .attr("value", function(d){return d.name;})
        .attr("height", function(d) { return height - y(d.value); })
        .style("fill", function(d) { return color(d.name); });

    bar.on("mousemove", function(d){
        var scoll = getScrollTop();
        divTooltip.style("left", d3.event.pageX + "px");
        divTooltip.style("top", d3.event.pageY - scoll + "px");
        //divTooltip.style("top", d3.event.pageY+"px");
        divTooltip.style("display", "inline-block");
        var x = d3.event.pageX, y = d3.event.pageY;
        var elements = document.querySelectorAll(':hover');
        var l = elements.length;
        l = l-1;
        var elementData = elements[l].__data__;
        var title_name = "";
        if(elementData.name == "newCreatedAccount"){
            title_name = "New Created Account";
        }else if(elementData.name == "totalGuestAccount"){
            title_name = "Total Guest Account";
        }else if(elementData.name == "activeGuestAccount"){
            title_name = "Active Guest Account";
        }else if(elementData.name == "totalGuestDevice"){
            title_name = "Total Guest Device";
        }
        divTooltip.html((d.label)+"<br>"+title_name+"<br>"+elementData.value);
    });
    bar.on("mouseout", function(d){
        divTooltip.style("display", "none");
    });

}
