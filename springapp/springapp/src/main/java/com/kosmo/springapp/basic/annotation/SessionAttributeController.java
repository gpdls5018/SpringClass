package com.kosmo.springapp.basic.annotation;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

/*
	@SessionAttributes() 어노테이션]
	
	- 서블릿 API(HttpSession)를 사용하지 않고 세션처리를 하기 위한 어노테이션
	- 클래스 앞에 붙인다.
	- 세션변수(세션 영역에 저장한 속성명)에 값을 저장하려면
	  컨트롤러 메소드의 매개변수 중 모델계열(Model, Map, ModelMap)
	   을 추가하여 add계열("세변변수명",값)으로 저장하면 그 이름으로
	   세션영역에도 저장된다.(리퀘스트 영역뿐만 아니라) 즉 별도로 세션영역에 저장하지 않아도 된다.
	   
	예] @SessionAttributes("세션변수명")
	
	 ※세션변수명이 여러개일때
	 @SessionAttributes({"세션변수명1","세션변수명2",...})
	 
	-세션영역에서 값 읽어올때
	
	 컨트롤러 메소드(@ModelAttribute(value="세션변수명")  String 세션값담을변수)
	 ※만약 세션영역에 세션변수명이 저장되어 있지 않다면 무조건 에러
	 
	-세션해제
	컨트롤러 메소드(SessionStatus session){
		session.setComplete();
	}
*/
/*
	@SessionAttribute 어노테이션 사용
	
	1.커맨드 객체(DTO) 사용 안하는 경우
	
	@SessionAttribute({"속성명1","속성명2",...}) 속성명은 폼의 파라미터 명과 반드시 일치 시켜라
	
	 [로그인] - 모델계열에 사용자가 입력한 아이디와 비번을 저장하면 세션영역에도 저장된다
	 login(Model model,@RequestParam Map map){
		회원 여부 판단후 회원이라면 model에 map저장
	         	회원이 아니라면 model에 에러메시지 저장
	 }
	 
	 [로그아웃]
	 logout(SessionStatus status){
		status.setComplete();
	}
	 	
	 [로그인여부 판단]
	 isLogin(@ModelAttribute("속성명1") String id, Model model){
		//메소드 안으로 들어 온다는 얘기는 세션영역에 "속성명1" 존재한다는 말. 고로 로그인이 되었다
	    //세션영역에 "속성명1" 없다면 무조건 500에러 - @ExceptionHandler 나 설정파일로 에러 처리
	      model에 로그인되었다는 정보를 저장
		
	}
	
	2.커맨드 객체 사용하는 경우
	
	-※빈 설정 파일(XML)에 <annotation-driven/>태그 추가(스프링 레거시인 경우. 스프링 부트는 제외)
	
	@SessionAttribute(types=커맨드객체 클래스명.class)
	
	 [로그인] - 아이디와 비번을 커맨드객체로 받는다 
	 			이 때 커맨드 객체는 회원이든 아니든 무조건 세션영역에 저장된다
	            세션 영역에 저장될 때 소문자로 시작하는 커맨드객체 클래스명(loginCommand)이 키값이 된다
	            value값은 당연히 커맨드 객체가 된다
		
	 login(Model model, LoginCommand cmd, SessionStatus status){
		회원이 아닌 경우를 판단해서
	         세션영역에 저장된 커맨드를 객체를 status.setComplete()로 삭제해야한다
	         model에는 에러메시지 저장
	 }
	 
	 [로그아웃]
	 logout(SessionStatus status){
		status.setComplete();
	}
	
	 [로그인여부 판단]
	 isLogin(@ModelAttribute("loginCommand") LoginCommad cmd, Model model){
		//메소드 안으로 들어 온다는 애기는 세션영역에 "loginCommand" 존재한다는 말 고로 로그인이 되었다
	    //세션영역에 "loginCommand" 없다면 무조건 500에러 - @ExceptionHandler 나 설정파일로 에러 처리
	      model에 로그인되었다는 정보를 저장
	}
*/

//[2.커맨드 객체 미 사용시]
//***세션영역에 저장할 속성명은 폼의 파라미터명과 일치시켜라
//@SessionAttributes({"id","pwd"})

//[3.커맨드 객체 사용시]
@SessionAttributes(types = AuthenticationCommand.class)//매개변수의 AuthenticationCommand 객체가 무조건(회원이든 아니든)생성되므로 자동으로 세션 영역에 저장됨
@Controller
public class SessionAttributeController {
	/*
	//[1.서블릿 API 사용(HttpSession)]
	//로그인 처리
	@RequestMapping("/Annotation/SessionAttributeLogin.do")
	public String login(HttpSession session, @RequestParam Map paramMap, Model model) {
		
		//데이타 저장
		//회원여부 판단
		if("KIM".equals(paramMap.get("id")) && "1234".equals(paramMap.get("pwd"))){
			//로그인 처리-세션영역에 필요값 저장
			session.setAttribute("id", paramMap.get("id"));
		}
		else {
			session.setAttribute("loginError", "아이디와 비번 불일치");
		}
		
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
	
	//로그인 여부 판단
	@RequestMapping("/Annotation/SessionAttributeIsLogin.do")
	public String isLogin(HttpSession session, Model model) {
		
		//로그인 여부 판단-세션영역에 존재 유무로 판단 후 데이타 저장
		model.addAttribute("isLoginMessageAPI",session.getAttribute("id")==null? "로그인하세요" : "로그인 되었습니다");
		
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
	
	//로그아웃
	@RequestMapping("/Annotation/SessionAttributeLogout.do")
	public String isLogout(HttpSession session) {
		
		//로그아웃 처리-세션영역에 저장된 속성 삭제
		session.invalidate();
		
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
	*/
	
	
	//@SessionAttributes 사용 : 로그인 여부 판단 시 로그인하지 않은 경우 에러 처리용 추가
	@ExceptionHandler({HttpSessionRequiredException.class})
	public String error(Model model) {
		model.addAttribute("loginError", "로그인 후 이용하세요");
		
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
	
	/*
	//[2.커맨드 객체 미 사용시]
	//로그인 처리
	@RequestMapping("/Annotation/SessionAttributeLogin.do")
	public String login(@RequestParam Map paramMap, Model model) {
			
		//데이타 저장
		//회원여부 판단
		if("KIM".equals(paramMap.get("id")) && "1234".equals(paramMap.get("pwd"))){
			//로그인 처리-세션영역에 필요값 저장
			//***@SessionAttributes 사용시 세션영역와 리퀘스트영역 동시 저장
			model.addAllAttributes(paramMap);
		}
		else {
			model.addAttribute("loginError", "아이디와 비번 불일치");
		}
			
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
		
	//로그인 여부 판단] 세션 영역에 반드시 "id"라는 속성(키가) 존재 해야 한다.
	//				그렇지 않으면 에러(Expected session attribute 'id')
	@RequestMapping("/Annotation/SessionAttributeIsLogin.do")
	public String isLogin(@ModelAttribute("id") String id, Model model) {
			
		//로그인이 된 상태
		model.addAttribute("isLoginMessage", id+"님 로그인 되었습니다");
			
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
		
	//로그아웃
	@RequestMapping("/Annotation/SessionAttributeLogout.do")
	public String isLogout(SessionStatus status) {
			
		//로그아웃 처리-세션영역에 저장된 속성 삭제
		status.setComplete();
			
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
	*/
	
	//[3.커맨드 객체 사용시]
	//로그인 처리
	@RequestMapping("/Annotation/SessionAttributeLogin.do")
	public String login(AuthenticationCommand auth, Model model, SessionStatus status) {
				
		//데이타 저장
		//회원여부 판단
		/*
		※ 세션영역에 저장될 때 속성명(키값)은 자동으로 소문자로 시작하는
			커맨드 클래스명이 키값(authenticationCommand)이 됨			
			매개변수의 AuthenticationCommand 객체가 무조건(회원이든 아니든)생성되고 자동으로 세션 영역에 저장됨
			즉 회원이 아닌 경우는 세션영역에서 삭제해야 한다
			단, 빈 설정 파일에 <annotation-driven/>태그 추가해야됨(스프링 레거시)
			  
			예]session.setAttribute("AuthenticationCommand", auth)식으로 저장됨
		*/
		if(!("KIM".equals(auth.getId()) && "1234".equals(auth.getPwd()))){ //회원이 아닐 때
				
			//무조건 커맨드 객체를 세션영역에 저장하기 때문에
			//회원이 아닌 경우는 삭제 해줘야한다
			status.setComplete();
			model.addAttribute("loginError", "아이디와 비번 불일치");
		}
				
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
			
		//로그인 여부 판단] 세션 영역에 반드시 "id"라는 속성(키가) 존재 해야 한다.
		//				그렇지 않으면 에러(Expected session attribute 'id')
		@RequestMapping("/Annotation/SessionAttributeIsLogin.do")
		public String isLogin(@ModelAttribute("authenticationCommand") AuthenticationCommand auth, Model model) {
				
		//로그인이 된 상태
		model.addAttribute("isLoginMessage", auth.getId()+"님 로그인 되었습니다");
				
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
			
	//로그아웃
	@RequestMapping("/Annotation/SessionAttributeLogout.do")
	public String isLogout(SessionStatus status) {
				
		//로그아웃 처리-세션영역에 저장된 속성 삭제
		status.setComplete();
				
		//뷰정보 반환
		return "04_annotation/Annotation";
	}
}
