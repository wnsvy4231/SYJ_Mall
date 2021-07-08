package com.test.SYJ_Mall;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.common.utill.StringFormatClass;
import com.test.SYJ_Mall.login.ILoginService;
import com.test.SYJ_Mall.login.LoginDTO;
import com.test.SYJ_Mall.login.SignUpDTO;
/**
 * 메인 컨트롤러 역할
 * @author shin
 *
 */
@Controller
public class LoginController {
	
	@Autowired
	private ILoginService logService;

	/*---------------------------------------------------로그인 페이지------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	// 처음에 로그인 페이지로 보내주는 곳
	@RequestMapping(value = "/login.action", method = { RequestMethod.GET,RequestMethod.POST })
	public String login(HttpServletRequest request, HttpServletResponse response, String site) {
		
		// 승환이형 작업 할 것
		//System.out.println(site);
		
		int result = logService.firstLoginStep(request,0,0);//result : 0 -> 에러없음
		
		if (result == 0) return "/login/UserLogin";//에러가 없는경우 -> 로그인 페이지로 넘겨준다.
		else return "/testwaiting/kakaoerror";//에러페이지로 보내준다.
		
	}
	
	
	//로그인페이지에서 정보를 넘겨주는곳
	@RequestMapping(value = "/loginVerification.action", method = { RequestMethod.POST })
	public String loginVerification(HttpServletRequest request, HttpServletResponse response) {
			
			String ip = "";//아이피 주소

			try {
				request.setCharacterEncoding("UTF-8");//인코딩 타입 설정
				
				ip = logService.ipCheck(request);
				
				Map<String,String> map = logService.getRSAkey(request);
				
				String id = map.get("id");//아이디
				String pw = map.get("pw");//비밀번호
				
				String encPw = logService.pwEnc(pw);//상대방이 입력한 pw를 암호화작업해준다.--> 여기서 문제가 생기는거 같은데.
			
				
				List<LoginDTO> loginResult = logService.loginResult(ip, id, encPw);
				int userSeq = loginResult.get(0).getUserSeq();//유저 고유 코드
				int loginCode = loginResult.get(0).getLoginCode();//로그인 결과
				
				
				if (loginCode == 0) {// 로그인 성공
					System.out.println("로그인 성공");
					
					logService.loginSuccess(request,userSeq);//로그인 인증티켓 발급
					
					return "redirect:/main.action";//메인페이지로 이동
					
				} else if (loginCode == 1 || loginCode == -1) {//로그인 실패 : 잘못된 로그인 정보 and 벤당한 아이피 들어오는경우
					System.out.println("잘못된 로그인 정보");
					
					int result = logService.firstLoginStep(request,-1,1);

					if (result == 0) return "/login/UserLogin";
					else return "/testwaiting/kakaoerror";//문제생겼을시에 에러페이지로 이동
					
				} else if (loginCode == 3) {//로그인 성공 : 하지만 비밀번호를 변경해줘야한다.
					System.out.println("비밀번호 변경 요망");
					
					//아래에서 기본적으로 정보와 rsa키를 넘겨야한다.
					int result = logService.userRedefinedPw(request,userSeq,id,ip);
					
					if (result == 1) return "/login/UserLoginPwRedefine";
					else return "/testwaiting/kakaoerror";//문제생겼을시에 에러페이지로 이동
					
					
				} else {//보안정책을 따라야하는 경우 --> 사진을 골라야한다. --> ***보안정책 실패하는 경우도 넣어줘야 하는데***;
					System.out.println("보안정책을 따라야한다.");
					
					HttpSession session = request.getSession();
					session.setAttribute("userSeq", userSeq);
					session.setAttribute("userIp", ip);
					
					//자동로그인 방지 알고리즘
					request = logService.AutoLoginBanned(request);
					
					return "/login/UserAutoLoginCheck";
					
				}
				
			} catch(Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				
				//위의 에러를 디비에 넣어줘야 한다.
				logService.errorEruptionTodb(errors.toString(),ip);
			
				return "/testwaiting/kakaoerror";
			}
			
			
				
			
			
	}
	
	//자동로그인 방지
	@RequestMapping(value = "/userautologinCheck.action", method = { RequestMethod.POST })
	public void autologinCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		PrintWriter out = response.getWriter();
		JSONObject obj = logService.picCheck(request);
		
		out.print(obj);			
	}
	
	//자동로그인 방지 -> 통과한경우
	@RequestMapping(value = "/userautologinPass.action", method = { RequestMethod.GET })
	public String autologinPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
				
		
		HttpSession session = request.getSession();
		int userSeq = (Integer)session.getAttribute("userSeq");
		String userIp = (String)session.getAttribute("userIp");
		
		
		logService.loginSuccess(request,userSeq);//로그인 인증티켓 발급
		logService.logUserTrace(userSeq,userIp);//로그인 기록 남겨주기
		
		System.out.println(session.getAttribute("userinfo"));
		
		//여기서 자동로그인때 사용한 세션변수 삭제해줘야한다. -> 메모리 누수 방지
		session.removeAttribute("userSeq");
		session.removeAttribute("userIp");
		session.removeAttribute("picName");
		session.removeAttribute("clickNum");
		session.removeAttribute("sucessCount");
		
		return "/testwaiting/waiting";
	}
	
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*---------------------------------------------------로그인 페이지------------------------------------------------------------------*/
	
	
	
	
	
	
	/*---------------------------------------------------회원가입 페이지------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	
	//회원가입 페이지로 보내주는 곳
	@RequestMapping(value = "/userSignUp.action", method = { RequestMethod.GET })
	public String signUp(HttpServletRequest request, HttpServletResponse response) {
			
		//비밀번호를 암호하하기 위해서 RSA 대칭키를 써줄것이다.
		try {
			int result = logService.setRSAkey(request);//rsa대칭키 생성
		} catch(Exception e) {
			e.getMessage();
		}
		
		return "/login/usersignup";
	}	
	
	//회원가입 페이지 - main
	@RequestMapping(value = "/userSignUpGo.action", method = { RequestMethod.POST })
	public String signUpGo(HttpServletRequest request, HttpServletResponse response,SignUpDTO dto) throws UnsupportedEncodingException {
		
		request.setCharacterEncoding("UTF-8");
		
		try {
			int result = logService.userSignUp(request,dto);
		} catch(Exception e) {
			e.printStackTrace();
		}
				
		return "/login/UserLoginSuccess";
	}
	
	
	//회원가입 페이지 - 아이디 검증(ajax)
	@RequestMapping(value = "/userSignUpIdCheck.action", method = { RequestMethod.GET })
	public void idVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		int result = logService.userSignUpIdVerify(request);// -1 을 가져오거나 1을 가져오는데 뭐지?
		
		out.print(result);
	}
	
	//회원가입 페이지 - 비밀번호 검증(ajax)
	@RequestMapping(value = "/userSignUpPwCheck.action", method = { RequestMethod.POST })
	public void pwVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		int result = logService.userSignUpPwVerify(request);
		
		
		out.print(result);
	}
	
	//회원가입 페이지 - 사용자 이메일 아이디 검증(ajax)
	@RequestMapping(value = "/userEmailVerifyCheck.action", method = { RequestMethod.GET })
	public void emailVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		int result = logService.userEmailVerify(request);
		
		out.print(result);
	}
	
	//회원가입 페이지 - 사용자 전화번호  검증(ajax)
	@RequestMapping(value = "/userPhoneVerifyCheck.action", method = { RequestMethod.GET })
	public void phoneNumVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		int result = logService.userPhoneNumVerify(request);
		
		
		out.print(result);
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*---------------------------------------------------회원가입 페이지------------------------------------------------------------------*/
	
	
	
	
	
	
	
	/*---------------------------------------------------아이디 찾기--------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	
	//아이디 찾기 페이지로 보내준다.
	@RequestMapping(value = "/userFindId.action", method = { RequestMethod.GET })
	public String findId(HttpServletRequest request, HttpServletResponse response) {
			
		return "/login/UserIdFind";
	}
	
	
	//아이디 찾기 확인로직
	@RequestMapping(value = "/userFindIdCheck.action", method = { RequestMethod.POST })
	public void findIdCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String email = request.getParameter("inputmail");
		String phone = request.getParameter("inputphone");
		
		PrintWriter out = response.getWriter();
		JSONObject obj = logService.findUserId(email,phone);
	
		out.print(obj);
		
	}
	
	//아이디 찾기 확인로직
	@RequestMapping(value = "/userFindIdCheckFinal.action", method = { RequestMethod.POST })
	public String findIdCheckFinal(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String userId = request.getParameter("throwUserId");
		String phone = request.getParameter("throwUserPhone");
			
		request.setAttribute("userId", userId);
		request.setAttribute("phone", phone);
		
		return "/login/UserIdFindCheck";
		
	}
	
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*---------------------------------------------------아이디 찾기--------------------------------------------------------------------*/
	
	
	
	
	
	
	
	/*---------------------------------------------------비밀번호 찾기--------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	//비밀번호 찾기 로직
	@RequestMapping(value = "/userFindpw.action", method = { RequestMethod.GET })
	public String findUserPw(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		
		return "/login/UserFindPw";
	}
	
	//비밀번호 찾기전 해당 정보가 유효한지 체크해준다.
	@RequestMapping(value = "/userFindpwCheck.action", method = { RequestMethod.POST })
	public void findUserPwCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//여기서 ajax로 넘어온 데이터를 체크하여 존재하는 아이디가 맞고 비밀번호를 바꿀 준비가 되었는지 확인작업을 진행해준다.
		String userId = request.getParameter("userId");
		String userEmail = request.getParameter("userEmail");
		String userPhone = request.getParameter("userPhone");
		
		PrintWriter out = response.getWriter();
		int result = logService.findUserPw(userId,userEmail,userPhone);
		
		
		out.print(result);
		//return "/login/UserIdFindCheck";
	}
	
	
	//비밀번호 찾기 -> 비밀번호를 변경해주고 회원 이메일로 변경된 임시비밀번호를 발급해준다.
	@RequestMapping(value = "/userFindpwSend.action", method = { RequestMethod.POST })
	public String findUserPwSend(HttpServletRequest request, HttpServletResponse response,StringFormatClass sfc) throws IOException {
		
		//여기서 ajax로 넘어온 데이터를 체크하여 존재하는 아이디가 맞고 비밀번호를 바꿀 준비가 되었는지 확인작업을 진행해준다.
		String userId = request.getParameter("kakaoId");
		String userEmail = request.getParameter("kakaoMail");
		String userPhone = request.getParameter("kakaoPhone");
		
		System.out.println(userId);
		System.out.println(userEmail);
		System.out.println(userPhone);
		
		//임시비밀번호 생성
		int result = logService.sendPw(userId,userEmail,userPhone);
		
		if (result == 1) {
			String sendEmail = sfc.maskingMail(userEmail);
			request.setAttribute("sendEmail", sendEmail);
			return "/login/UserFindPwSendEmail";
		} else {//에러가 발생한경우
			return "/testwaiting/kakaoerror";
		}	
	}
	
	//비밀번호 찾기 : 임시비번 -> 비밀번호 변경
	@RequestMapping(value = "/userRedefinedPw.action", method = { RequestMethod.POST })
	public String userPwRedefined(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		request.setCharacterEncoding("UTF-8");//인코딩 타입 설정
		
		int result = logService.remodiftUserPw(request);
		
		if(result == 1) {
			return "/testwaiting/waiting";
		} else {
			return "/testwaiting/kakaoerror";
		}
			
		
	}
	
	
	
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*------------------------------------------------------------------------------------------------------------------------------*/
	/*---------------------------------------------------비밀번호 찾기--------------------------------------------------------------------*/
	
	
}
