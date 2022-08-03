package hello.login.web.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

	private final LoginService loginService;
	private final SessionManager sessionManager;
	
	@GetMapping("/login")
	public String loginForm(@ModelAttribute("loginForm") LoginForm form)
	{
		return "/login/loginForm";
	}
	
//	@PostMapping("/login")
	public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response)
	{
		if (bindingResult.hasErrors())
		{
			return "login/loginForm";
		}

		Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
		
		if (loginMember == null)
		{
			bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
			return "login/loginForm";
		}
		
		// 로그인 성공 처리
		// 쿠키에 시간정보를 주지 않으면 세션 쿠키 (브라우저 종료시 쿠키 제거)
		Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
		response.addCookie(idCookie);
		
		return "redirect:/";
	}
	
//	@PostMapping("/login")
	public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response)
	{
		if (bindingResult.hasErrors())
		{
			return "login/loginForm";
		}
		
		Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
		
		if (loginMember == null)
		{
			bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
			return "login/loginForm";
		}
		
		// 로그인 성공 처리
		// 세션 관리자를 통해 세션을 생성하고, 회원데이터 보관
		sessionManager.createSession(loginMember, response);
		
		return "redirect:/";
	}
	
	@PostMapping("/login")
	public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request)
	{
		if (bindingResult.hasErrors())
		{
			return "login/loginForm";
		}
		
		Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
		
		if (loginMember == null)
		{
			bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
			return "login/loginForm";
		}
		
		/*
		 *  로그인 성공 처리
		 *  * 세션이 있으면 기존세션을, 없으면 신규세션을 반환
		 *  * 세션을 생성하려면 request.getSession(true), 디폴트가 true
		 */
		HttpSession session = request.getSession();
		// request.getSession(true)와 request.getSession(false)의 차이
		// - true : 세션이 있으면 기존세션을, 없으면 신규세션을 생성하여 반환
		// - false: 세션이 있으면 기존세션을, 없으면 신규세션을 생성하지 않고 null 반환
		
		// 세션에 로그인 회원정보 보관
		session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
		
		return "redirect:/";
	}
	
//	@PostMapping("/logout")
	public String logout(HttpServletResponse response)
	{
		expireCookie(response, "memberId");
		return "redirect:/";
	}
	
//	@PostMapping("/logout")
	public String logoutV2(HttpServletRequest request, HttpServletResponse response)
	{
		sessionManager.expire(request, response);
		return "redirect:/";
	}
	
	@PostMapping("/logout")
	public String logoutV3(HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session = request.getSession(false);
		if (session != null)
		{
			session.invalidate();
		}
		
		return "redirect:/";
	}
	
	// 쿠키 삭제
	private void expireCookie(HttpServletResponse response, String cookieName)
	{
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}
