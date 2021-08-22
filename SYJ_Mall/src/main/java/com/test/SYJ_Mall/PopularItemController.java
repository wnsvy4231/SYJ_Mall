package com.test.SYJ_Mall;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.common.utill.KakaoCookie;
import com.test.SYJ_Mall.login.UserDTO;
import com.test.SYJ_Mall.popularItem.IPopularService;
import com.test.SYJ_Mall.popularItem.PopularItemDTO;

@Controller
public class PopularItemController {
	@Autowired
	private IPopularService service;
	
	//인기페이지 처음 메인화면
	@RequestMapping(value = "/popularMain.action", method = { RequestMethod.GET,RequestMethod.POST })
	public String popularItemMain(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		UserDTO userInfo = (UserDTO)session.getAttribute("userinfo");
		
		//마지막 페이지 정보 쿠키에 넘기는 작업
		KakaoCookie ck = new KakaoCookie();
		ck.generateCookie(response, "lastPage", "popularMain");
		
		int result = -1;//결과값
		
		//1. 로그인 되어 있는경우
		if (userInfo != null) {
			int userSeq = userInfo.getUserSeq();//유저 고유번호
			String basketList = "";
			result = service.getPopularProductList(request,1,userSeq,basketList);
		} 
		//2. 로그인 되어있지 않은 경우
		else {
			String basketList = "";
			result = service.getPopularProductList(request,1,0,basketList);
			
		}

		if (result == 1) {
			return "/tiles/popularItem.layout";
		} else {
			return "/testwaiting/kakaoerror";
		}	
	}
	
	
	//인기페이지 -> 스크롤 내렸을 경우 물품 계속 불러오기 처이
	@RequestMapping(value = "/popularItemAjax.action", method = { RequestMethod.POST })
	@ResponseBody
	public List<PopularItemDTO> popularItemAjax(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		UserDTO userInfo = (UserDTO)session.getAttribute("userinfo");
		
		int paging = Integer.parseInt(request.getParameter("paging"));
		
		try {
			
			//1. 로그인 되어있는 경우
			if (userInfo != null) {
				int userSeq = userInfo.getUserSeq();//유저 고유번호
				return service.getPopularProductAjax(paging,userSeq);
			}
			//2. 로그인 되어있지 않은 경우
			else {
				String cookieList = "";//쿠키리스트
				return service.getPopularProductAjax(paging,cookieList);
				//return 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	//인기페이지 -> 장바구니에 담기
	@RequestMapping(value = "/popularItemBasketInput.action", method = { RequestMethod.GET })
	@ResponseBody
	public int popularItemBasketInput(HttpServletRequest request, HttpServletResponse response) {
		
		int productId = Integer.parseInt(request.getParameter("productId"));//상품번호
		HttpSession session = request.getSession();
		
		
		try {
			
			UserDTO userInfo = (UserDTO)session.getAttribute("userinfo");
			
			//1. 로그인 되어 있지 않은 경우
			if (userInfo == null) {
				System.out.println("로그인 안되어있음");
			} 
			//2. 로그인 되어 있는경우
			else {
				int userSeq = userInfo.getUserSeq();//유저 고유번호
				int result = service.inputItemBasket(userSeq,productId);
				
				if (result == -2) {
					throw new Exception();
				}
				
				return result;
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			
			return -2;
		}
		
		return -2;
	}
	
	
	
	
	
	
	
}
