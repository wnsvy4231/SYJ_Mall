package com.common.utill;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.index.IndexResponse;


/**
 * 에러알람 - deprecated
 *
 * 
 * @author shin
 *
 */
public class ErrorAlarm {
	
	private Exception e;
	private String ip;
	
	
	public ErrorAlarm() {
		
	}
	
	public ErrorAlarm(Exception e, String ip) {
		this.e = e;
		this.ip = ip;
	}
	
	public ErrorAlarm(Exception e) {
		this.e = e;
		this.ip = "none";
	}
	
	/**
	 * 에러요인 메시지로 보내주기
	 * 
	 * @param userEmail 유저 이메일(복수여도 상관없음)
	 */
	public void sendErrorMassegeAdmin() {
		CommonDateFormat cd = new CommonDateFormat();
		KafkaConn kc = new KafkaConn("byeanma.kro.kr",9092,"errortopics");
 		
		StringWriter errors = new StringWriter();
		errors.append("<table border='1' style='width:1200px;'>");
		errors.append("<th colspan = '2' style='color:red; font-size: 2em; font-weight: bolder;'>Error Occurred In SYJ_Mall</th>");
		errors.append("<tr><td>machine ip</td><td>Date of occurrence</td></tr>");
		errors.append("<tr><td>");
		errors.append(this.ip);
		errors.append("</td><td>");
		errors.append(cd.formatStringTimeKOR());
		errors.append("</td></tr>");
		errors.append("<tr><td colspan='2' style='color: red; font-size: 1.3em; font-weight: bolder; text-align: center;'>Error Log</td></tr>");
		errors.append("<tr><td colspan='2'>");
		e.printStackTrace(new PrintWriter(errors));
		errors.append("</td></tr></table>");
		
		String errorsMsg = errors.toString();
		errorsMsg = errorsMsg.replaceAll("\\)", ")<br>");
		errorsMsg = errorsMsg.replaceAll("\\###", "<br>###");
		
		
		try {
			kc.kafkaSendMessage(errorsMsg);
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 에러요인 db에 넣어주기 (elasticserarch 에 로그 쌓아주기)
	 */
	public void inputErrorToDb() {
		
		CommonDate cd = new CommonDate();
		ElasticSearchConn ec = new ElasticSearchConn("byeanma.kro.kr", 9200, "http");
		
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		
		HashMap<String,Object> jsonMap = new HashMap<String, Object>();
		Calendar curTimeKor = cd.getPresentTimeKORCal();
		
		jsonMap.put("@timestamp",curTimeKor);
		jsonMap.put("ip",this.ip);
		jsonMap.put("errMsg",errors.toString());
		
		
		String dateNameIndex = cd.getCurrentDateIndex("error_log_index",curTimeKor);
		
		try {
			IndexResponse indexresp = ec.elasticPostData(dateNameIndex,jsonMap);
			ec.connClose();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 에러요인 메시지로 보내주기 + 에러요인 db에 넣어주기
	 */
	public void errorDbAndMail() {
		sendErrorMassegeAdmin();
		inputErrorToDb();
	}
	
	/**
	 * 에러요인 조합 기본 셋팅 1
	 * @param request
	 * @param e
	 * @return
	 */
	public int basicErrorException(HttpServletRequest request,Exception e) {
		
		IpCheck ic = new IpCheck();
		String ip = ic.getClientIP(request);
		
		this.e = e;
		this.ip = ip;
		errorDbAndMail();
		
		return -1;
	}

}
