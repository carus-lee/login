package hello.login.web.interceptor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogInterceptor implements HandlerInterceptor{

	public static final String LOG_ID = "logId";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String requestURI = request.getRequestURI();
		String logId = UUID.randomUUID().toString();
		
		request.setAttribute(LOG_ID, logId);
		
		//"@RequestMapping를 사용하는 경우: handlerMethod
		//"정적리소스"를 사용하는 경우: ResourceHttpRequestHandler
		if (handler instanceof HandlerMethod)
		{
			HandlerMethod hm = (HandlerMethod) handler; //호출할 컨트롤러 메서드의 모든정보가 포함되어 있다.
		}
		
		log.info("REQUEST [{}] [{}] [{}]", logId, requestURI, handler);
		
		return true; // true: 다음 컨트롤러 호출(정확히는 다음 handler 호출), false: 여기서 끝
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		log.info("postHandle [{}]", modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	
		String requestURI = request.getRequestURI();
		String logId = (String)request.getAttribute(LOG_ID);
		
		log.info("RESPONSE [{}] [{}] [{}]", logId, requestURI, handler);
		
		if (ex != null)
		{
			log.error("afterCompletion error!!", ex);
		}
	}
	
	

}
