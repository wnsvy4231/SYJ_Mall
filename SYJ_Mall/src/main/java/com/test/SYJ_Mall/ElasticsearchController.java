package com.test.SYJ_Mall;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.common.utill.ErrorAlarm;
import com.test.SYJ_Mall.elasticsearch.IElasticService;

/**
 * elasticsearch 컨트롤러
 * 
 * @author shin
 *
 */
@Controller
public class ElasticsearchController {
	
	@Autowired
	private IElasticService service;
	
	@RequestMapping(value = "/elasticsearch.action", method = { RequestMethod.GET , RequestMethod.POST})
	public String elasticMain(HttpServletRequest request, HttpServletResponse response, ErrorAlarm ea) {
		
//		String hostname = "218.48.201.158";
//		int port = 9200;
//		String scheme = "http";
//		HttpHost host = new HttpHost(hostname, port, scheme);
//		RestClientBuilder restClientBuilder = RestClient.builder(host);
//		RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
//		
		int result = service.getConnectElastic(request,response,ea);
		
		System.out.println(result);
		
		return "";
	}
	
}
