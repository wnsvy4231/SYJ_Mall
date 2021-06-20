package com.test.SYJ_Mall.login;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

/**
 * 로그인 서비스 인터페이스
 * @author shin
 *
 */
public interface ILoginService {
	
	/**
	 * 접속자의 ip를 체크함
	 * @param request 	request 객체
	 * @return			접속자의 아이피
	 */
	String ipCheck(HttpServletRequest request);
	
	/**
	 * 비밀번호 암호화
	 * @param password 	암호화되지 않은 비밀번호 
	 * @return			암호화된 비밀번호
	 */
	String pwEnc(String password);
	
	/**
	 * 로그인 결과
	 * @param userIp	접속자 ip
	 * @param id		접속자 id
	 * @param pw		접속자 비밀번호
	 * @return			
	 */
	List<LoginDTO> loginResult(String userIp,String id, String pw);
	
	/**
	 * 로그인 성공
	 * @param request	request 객체
	 * @param userSeq	유저 고유번호
	 * @param mode		0 : 자동로그인방지 걸쳐서 성공, 1 : 자동방지 없이 로그인 성공
	 */
	void loginSuccess(HttpServletRequest request, int userSeq,int mode);
	
	
	/**
	 * 자동로그인 방지
	 * @param request	request 객체
	 * @return			request 객체
	 */
	HttpServletRequest AutoLoginBanned(HttpServletRequest request);
	
	/**
	 * 자동로그인 방지 - 사진불러오기
	 * @param request	request 객체
	 * @return			json 객체 - 사진정보
	 */
	JSONObject picCheck(HttpServletRequest request);
	

	/**
	 * 자동로그인 방지인증 후 회원이 로그인한 시간을 기록으로 남길것.
	 * @param userSeq		회원 고유번호
	 * @param ipaddress		ip주소
	 */
	void logUserTrace(int userSeq,String ipaddress);

	
	/**
	 * 회원가입
	 * @param request	request 객체
	 * @return			회원가입 결과 -> 1 : 성공, -1 : 실패
	 */
	int userSignUp(HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException;//회원가입 해주는 로직
	
	/**
	 * 유저회원가입 - 아이디 검증
	 * @param request	request 객체
	 * @return			-1 : 사용불가, 1 : 사용가능
	 */
	int userSignUpIdVerify(HttpServletRequest request);
	
	/**
	 * 유저회원가입 - 비밀번호 검증
	 * @param request	request 객체
	 * @return			-1 : 비밀번호가 안전하지 않음, 1 : 비밀번호가 안전해서 사용가능
	 */
	int userSignUpPwVerify(HttpServletRequest request);
	
	
	/**
	 * RSA 대칭키 암호화
	 * @param request	request 객체
	 * @return			-1 : 오류 , 1 : 성공
	 */	
	int setRSAkey(HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException;
	
	/**
	 * RSA 대칭키 복호화
	 * @param request	request 객체
	 * @return			복호화된 결과값을 map객체로 불러와준다.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	HashMap<String,String> getRSAkey(HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException;
	
	
	/**
	 * 첫번째 로그인 과정 - 광고넘겨주고 rsa키값 넘겨준다
	 * @param request		request 객체
	 * @param errorCode		오류코드 - 로그인오류 : -1, 정상 : 0
	 * @return				정상 : 0 , 비정상 : -1
	 */
	int firstLoginStep(HttpServletRequest request,int errorCode);
	
	/**
	 * 유저회원가입 - 이메일 아이디 중복 체크
	 * @param request	request 객체
	 * @return			1 : 성공, -1 : 실패
	 */
	int userEmailVerify(HttpServletRequest request);
	
	
}