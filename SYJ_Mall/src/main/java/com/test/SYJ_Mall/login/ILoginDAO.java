package com.test.SYJ_Mall.login;

import java.util.List;

import com.common.utill.AdverDTO;

/**
 * 로그인 dao 인터페이스
 * @author shin
 *
 */
public interface ILoginDAO {
	
	/**
	 * 로그인 결과
	 * @param userIp	유저 ip
	 * @param id		유저 아이디
	 * @param encpw		유저 비밀번호(암호화된 비밀번호)
	 * @return			로그인 검증 객체
	 */
	List<LoginDTO> loginResult(String userIp,String id,String encpw);
	
	
	/**
	 * 광고 넣기
	 * @return	광고 객체
	 */
	List<AdverDTO> getAdvertiseInfo();
	
	/**
	 * 해당번호 유저 정보 가져오기
	 * @param userSeq	유저고유번호
	 * @return			유저객체
	 */
	List<UserDTO> userInfo(int userSeq);


	/**
	 * 자동로그인 인증 후 고객의 로그인을 기록해줄것
	 * @param userSeq		유저의 고유번호
	 * @param ipaddress		아이피주소
	 */
	void autoLoginPassTrace(int userSeq,String ipaddress);

	
	/**
	 * 회원이 기입한 정보를 회원테이블로 옮겨줄 메서드
	 * @param dto	회원가입 데이터 객체
	 * @return		실패 성공 여부 체크
	 */
	int signUp(SignUpDTO dto);

	
	/**
	 * 회원이 입력한 id가 중복되지 않는지 체크
	 * @param inputId	회원이 회원가입 하기 위해 입력한 아이디
	 * @return			-1 : 사용가능 불가, 1 : 사용가능
	 */
	int signUpIdVerifyCheck(String inputId);

	/**
	 * 회원이 입력한 email이 중복되지 않는지 체크
	 * @param fullEmail		회원이 사용할 이메일 아이디
	 * @return				1 : 성공, -1 : 실패
	 */
	int emailVerifyCheck(String fullEmail);

	/**
	 * 에러요건 디비에 넣어주기
	 * @param errormsg	에러요인
	 * @param ip		에러발생시킨 아이피
	 */
	void errorIntoDb(String errormsg, String ip);

	/**
	 * 유저 아이디 찾아주기
	 * @param email	유저 이메일
	 * @param phone	유저 폰번호
	 * @return		유저 아이디
	 */
	String findUserId(String email, String phone);

	/**
	 * 유저 비밀번호 찾기전 해당 아이디가 존재하는지 확인하는 작업
	 * @param userId	유저 아이디	
	 * @param userEmail	유저 이메일주소
	 * @param userPhone	유저 폰번호
	 * @return			1 : 존재, -1 : 존재하지 않음
	 */
	int findUserPwExist(String userId, String userEmail, String userPhone);

	/**
	 * 비밀번호를 찾고자하는 유저의 비밀번호를 임시비밀번호로 바꾸어준다.
	 * @param userId		유저의 아이디
	 * @param userEmail		유저의 이메일
	 * @param userPhone		유저의 핸드폰번호
	 * @param encInstPw		유저의 임시아이디(암호화된 값)
	 * @return				1 : 성공, -1 : 실패
	 */
	int modifyUserPw(String userId, String userEmail, String userPhone, String encInstPw);

	
	
	
}
