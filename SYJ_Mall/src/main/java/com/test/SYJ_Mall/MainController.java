package com.test.SYJ_Mall;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.SYJ_Mall.main.MainDTO;
import com.test.SYJ_Mall.main.MainService;

class TIME_MAXIMUM {
	public static final int SEC = 60;
	public static final int MIN = 60;
	public static final int HOUR = 24;
	public static final int DAY = 30;
	public static final int MONTH = 12;
}

/**
 * 메인 페이지 컨트롤러 역할
 * 
 * @author joonpyo-hong
 *
 */
@Controller
public class MainController {

	@Autowired
	private MainService service;

	// 메인화면
	@RequestMapping(value = "/main.action", method = { RequestMethod.GET })
	public String main(Model model) {
//		List<MainDTO> list = service.list();
		
		return "/main/Main";
	}

	// 무한 스크롤
	@RequestMapping(value = "/list.action", method = { RequestMethod.POST })
	@ResponseBody
	public Object list(Model model) {
		List<MainDTO> list = service.list();

		return list;
	}

}
