package com.test.SYJ_Mall.popularItem;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.utill.KakaoCookie;

@Service
public class PopularService implements IPopularService{
	
	@Autowired
	private IPoupularDAO dao;
	
	
	//인기상품 보여주기
	@Override
	public int getPopularProductList(HttpServletRequest request, int paging, int userSeq, String basketList) {
		try {
			
			List<PopularItemDTO> popularDtoList = dao.getPopularItem(paging,userSeq,basketList);
			request.setAttribute("popularDtoList", popularDtoList);
			request.setAttribute("seleted", "popular");//상단 오늘/신규/인기/마이 중에서 인기를 선택해주는 로직
			
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	//인기상품 보여주기 -> ajax 처리 : 로그인 한 경우
	@Override
	public List<PopularItemDTO> getPopularProductAjax(int paging, int userSeq) {
		
		return dao.getPopularItem(paging,userSeq,"");
	}

	//인기상품 보여주기 -> ajax 처리 : 로그인 안한 경우
	@Override
	public List<PopularItemDTO> getPopularProductAjax(int paging, String cookieList) {
		
		return dao.getPopularItem(paging,0,"");
	}
	
	
	
	//쿠키객체 만들어주기
	@Override
	public Object generateCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		
		KakaoCookie ck = new KakaoCookie();
		//Object result = ck.generateCookie(response, cookieName, ,60*60*24*3);
		
		return null;
	}
	
	//회원이 선택한 상품 장바구니에 넣어주기
	@Override
	public int inputItemBasket(int userSeq, int productId) {
	
		return dao.inputItemBasket(userSeq,productId);
	}
	
	//회원이 선택한 상품 장바구니에서 빼주기
	@Override
	public int outputItemBasket(int userSeq, int productId) {
		
		return dao.outputItemBasket(userSeq,productId);
	}

	//쿠키에 존재하는 장바구니 내역 가져오기
	@Override
	public String getCookieBasket(HttpServletRequest request, HttpServletResponse response) {
		
		KakaoCookie kc = new KakaoCookie();
		String basketList = (String)kc.getCookieInfo(request, "basketList");
		StringBuffer sb = new StringBuffer();
		
		//쿠키내에 장바구니 내역이 없는경우
		if(basketList == null) {
			
			kc.generateCookie(response, "basketList", "", 60*60*24*7);//쿠키생성 7 일단위로 생성
			String newBasketList = (String)kc.getCookieInfo(request, "basketList");
			
			return newBasketList;
			
		} else {
		//쿠키내에 장바구니 내역이 있는경우	
			
			if (basketList.length() != 0) {
				basketList = basketList.substring(0,basketList.length()-1);
				return basketList;//기존쿠키 넘기기
			} else {
				return basketList;//기존쿠키 넘기기
			}
			
			
		}
		
	}


}
