package com.irm.parselog.parselog.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.irm.parselog.parselog.service.ChartService;
import com.irm.parselog.parselog.vo.ChartDataVo;

@Controller
@RequestMapping("/log")
public class ChartController {
	
	private static final Logger log = LoggerFactory.getLogger(ChartController.class);
	
	@Autowired
	ChartService chartService;
	
	@RequestMapping(value = "/chart")
	public String chart () {
		return "chart";
	}
	
	@RequestMapping(value = "/refreshChart", method = {RequestMethod.POST})
	@ResponseBody
	public Map refreshChart(@RequestParam("items") String items, @RequestParam("limitNo") int limitNo) {
		//test		
		log.info("selectItem : " + items);				
		log.info("result : " + chartService.aggregateCountByItems(items, limitNo));
		log.info("show chart successfully!");
		
		List<ChartDataVo> list =  chartService.aggregateCountByItems(items, limitNo);
		Map result = chartService.executeAggregate(list);
		return result;
	}
}
