package hello.login.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.PatternMatchUtils;

import hello.login.web.SessionConst;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@NoArgsConstructor
public class LoginCheckFilter implements Filter {
	
	private static final String[] whiteList = {"/", "/members/add", "/login", "/logout", "/css/*"};
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		try
		{
			log.info("로그 체크필터 시작 {}", requestURI);
			
			if (isLoginCheckPath(requestURI))
			{
				log.info("인증 체크로직 실행 {}", requestURI);
				HttpSession session = httpRequest.getSession(false);
				if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null)
				{
					log.info("미인증 사용자 요청 {}", requestURI);
					// 로그인페이지로 리다이렉트
					httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
					
					return;
				}
			}
			
			chain.doFilter(httpRequest, httpResponse);

		}
		catch (Exception e)
		{
			throw e;//예외 로깅은 가능하지만, 톰캣까지 예외를 보내주어야 함.
		}
		finally 
		{
			log.info("인증 체크필터 종료 {}", requestURI);
		}
		
	}
	
	/*
	 * 화이트 리스트의 경우 인증체크 X
	 */
	private boolean isLoginCheckPath(String requestURI)
	{
		return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
	}

	
}