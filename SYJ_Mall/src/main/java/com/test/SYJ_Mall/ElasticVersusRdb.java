package com.test.SYJ_Mall;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.SYJ_Mall.elasticsearchDw.IElasticDwService;
import com.test.SYJ_Mall.sqlServerJpa.ISqlServerService;


@Controller
public class ElasticVersusRdb {
	
	
	@Autowired 
	private IElasticDwService elasticService;
	
	@Autowired 
	private ISqlServerService sqlService;
	
	
	// 검색 페이지로 보내준다.
	@RequestMapping(value = "/searchTestEsSql.action", method = { RequestMethod.GET , RequestMethod.POST})
	public String searchTestEsSql(HttpServletRequest request, HttpServletResponse response) {
		
		return "/test/searchtest";
	}
	
	
	
	// RDBMS 를 통해서 검색결과를 가져와준다.
	@RequestMapping(value = "/sqlServerTime.action", method = { RequestMethod.GET , RequestMethod.POST})
	@ResponseBody
	public List<String> sqlServerTime(HttpServletRequest request, HttpServletResponse response) {
		
		String keyword = request.getParameter("search_keyword");
		
		long startTime = System.currentTimeMillis();
		
		List<String> searchResult = sqlService.getSearchData(keyword);
		
		long endTime = System.currentTimeMillis();
		long timeElapsed = endTime - startTime;
		
		searchResult.add(Long.toString(timeElapsed));
		
		return searchResult;
	}	
	
	
	// Elasticsearch 를 통해서 검색결과를 가져와준다.
	@RequestMapping(value = "/elasticTime.action", method = { RequestMethod.GET , RequestMethod.POST})
	@ResponseBody
	public List<String> elasticTime(HttpServletRequest request, HttpServletResponse response) {
		
		String keyword = request.getParameter("search_keyword");
		
		long startTime = System.currentTimeMillis();
		
		List<String> searchResult = elasticService.getSearchData(keyword);
		
		long endTime = System.currentTimeMillis();
		long timeElapsed = endTime - startTime;
		
		searchResult.add(Long.toString(timeElapsed));
		
		
		return searchResult;
	}
	
	
}
