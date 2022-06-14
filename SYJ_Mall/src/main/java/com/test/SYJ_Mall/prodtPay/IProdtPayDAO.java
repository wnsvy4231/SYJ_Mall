package com.test.SYJ_Mall.prodtPay;

import java.util.List;

/**
 * 상품결제 DAO 인터페이스
 * @author shin
 *
 */
public interface IProdtPayDAO {
	
	int getTest();
	
	/**
	 * 구매할 상품의 정보 가져오기
	 * @param prodtId
	 * @return
	 */
	List<ProdtPayDTO> getProdtPayList(String prodtId);

}
