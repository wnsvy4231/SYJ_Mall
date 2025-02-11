package com.common.utill;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 공통 DB 담당 클래스
 * 
 * @author shin
 *
 */
public class CommonDAO {

	// DB 담당
	private Connection conn;
	private CallableStatement stat;
	private ResultSet rs;
	private PreparedStatement pstat;

	public CommonDAO() {
		DBUtil util = new DBUtil();
		conn = util.open();
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 로그인 된 경우에 장바구니 기능 -> 장바구니에서 빼주거나 넣어주기.
	 * 
	 * @param userSeq 유저고유번호
	 * @param prodtId 상품번호
	 * @return 1: 장바구니에 추가, 2: 장바구니에서 삭제, -1: 오류
	 */
	public int setBasketProdt(int userSeq, int prodtId) throws Exception {

		String sql = "{? = call kakao_popular_product_basket(?,?)}";

		stat = conn.prepareCall(sql);
		stat.registerOutParameter(1, java.sql.Types.INTEGER);
		stat.setInt("qoouser_seq", userSeq);
		stat.setInt("product_id", prodtId);

		stat.execute();

		int result = stat.getInt(1);
		stat.close();

		return result;

	}

	/**
	 * 상품정보 알림 기능
	 * 
	 * @param userSeq 유저고유번호
	 * @param prodtId 상품 고유번호
	 * @return 1: 알림정보에 추가, 2: 알림정보에서 제거, -1: 오류
	 */
	public int setAlarmProdt(int userSeq, int prodtId) throws Exception {

		String sql = "{? = call kakao_popular_product_alaram(?,?)}";

		stat = conn.prepareCall(sql);
		stat.registerOutParameter(1, java.sql.Types.INTEGER);
		stat.setInt("qoouser_seq", userSeq);
		stat.setInt("product_id", prodtId);

		stat.execute();

		int result = stat.getInt(1);
		stat.close();

		return result;

	}
	
	/**
	 * 관리자들 이메일 정보 일괄로 가져오기
	 * @return 관리자들 이메일 주소
	 */
	public List<String> getAdminEmailAddress() {

		try {
			
			String sql = "{call kakao_admin_info}";
			stat = conn.prepareCall(sql);
			boolean result = stat.execute();
			
			List<String> adminList = new ArrayList<String>();

			if (result) {
				rs = stat.getResultSet();
				while(rs.next()) {
					adminList.add(rs.getString("adminEmail"));
				}	
			}
			
			stat.close();
			rs.close();
			return adminList;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 마스터 정보 가져오기
	 * @return
	 */
	public MasterDTO getMasterData() {
		
		try {
			
			String sql = "{call kakao_admin_master}";
			
			stat = conn.prepareCall(sql);
			stat.execute();
			MasterDTO dto = new MasterDTO();
			
			rs = stat.getResultSet();
			
			if (rs.next()) {
				
				dto.setMasterEmail(rs.getString("masterEmail"));
				dto.setMasterKey(rs.getString("masterKey"));
				dto.setMasterPw(rs.getString("masterPw"));
			}
			
			
			return dto;
			
		} catch(Exception e) {
			return null;
		}
		
	}
	
	/**
	 * 캅차 비밀키 모두 가져와주는 로직
	 * @param siteKey	사이트 키
	 * @return
	 */
	public String getCaptchaPrivateKey(int adminSeq) {
		
		try {
			
			
			String secureKey = "";
			
			String sql = "{call kakao_admin_captchar(?)}";
			
			stat = conn.prepareCall(sql);
			stat.setInt("admin_seq", adminSeq);
			stat.execute();
			rs = stat.getResultSet();
			
			if (rs.next()) {
				secureKey = rs.getString("secureKey");
			}
			
			return secureKey;
			
		} catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * 유저번호에 해당하는 유저 id 정보 가져오기
	 * @param userSeq
	 * @return
	 */
	public String getUserId(int userSeq) {
		
		try {
			
			String userId = "";
		
			String sql = "{call kakao_common_get_user_id(?)}";
			
			stat = conn.prepareCall(sql);
			stat.setInt("user_seq", userSeq);
			stat.execute();
			rs = stat.getResultSet();
			
			if (rs.next()) {
				userId = rs.getString("userId");
			}
			
			return userId;
			
		} catch(Exception e) {
			return null;
		}
	}
	
	
	
	/**
	 * 어드민 카카오 계정정보 가져오기
	 * @param userSeq
	 * @return
	 */
	public AdminKakaoDTO getAdminKaKaosInfo(int userSeq) {
		
		try {
			
		
			String sql = "{call kakao_common_admin_master_infos(?)}";
			
			stat = conn.prepareCall(sql);
			stat.setInt("admin_seq", userSeq);
			stat.execute();
			rs = stat.getResultSet();
			
			AdminKakaoDTO dto = new AdminKakaoDTO();
			
			if (rs.next()) {
				
				dto.setKakaoId(rs.getString("kakaoId"));
				dto.setKakaoPw(rs.getString("kakaoPw"));
				dto.setMasterKey(rs.getString("masterKey"));
				
			}
			
			stat.close();
			
			return dto;
			
		} catch(Exception e) {
			return null;
		}
	}
	
	

}
