package com.test.SYJ_Mall.elasticsearch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.common.utill.ErrorAlarm;

public interface IElasticService {

	int getConnectElastic(HttpServletRequest request, HttpServletResponse response, ErrorAlarm ea);

}
