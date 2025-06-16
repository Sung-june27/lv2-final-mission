package finalmission.global.config;

import finalmission.global.error.exception.UnauthorizedException;
import finalmission.global.util.CookieUtil;
import finalmission.member.domain.LoginMember;
import finalmission.member.domain.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        String token = CookieUtil.extractValueFromCookie(cookies, "token");
        if (token.isEmpty() || !jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Claims claims = jwtTokenProvider.getClaimsFromToken(token);

        Long id = Long.valueOf(claims.getSubject());
        String name = claims.get("name", String.class);
        Role role = claims.get("role", Role.class);

        return new LoginMember(id, name, role);
    }
}
