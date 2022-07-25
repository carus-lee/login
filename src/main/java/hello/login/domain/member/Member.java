package hello.login.domain.member;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class Member {
	
	private Long id;
	
	@NotEmpty
	private String loginId; // 로그인ID
	
	@NotEmpty
	private String name; // 사용자이름
	
	@NotEmpty
	private String password; 

}
