package info.dexplore.dexplore.filter;

import info.dexplore.dexplore.entity.UserEntity;
import info.dexplore.dexplore.provider.JwtProvider;
import info.dexplore.dexplore.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            String token = parseBearerToken(request);
            if(token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtProvider.validate(token);
            if(userId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 유저 찾기
            UserEntity userEntity = userRepository.findByUserId(userId);
            // 유저 역할 추출 및 역할 리스트에 추가
            String role = userEntity.getRole(); // role : ROLE_USER, ROLE_ADMIN
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));

            // 해당 쓰레드에 security context 연결 -> 요청 라이프사이클이 끝나면 소멸
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            // security context에 추가할 Authentication 구현체(여기서는 usernamepassword토큰) 생성
            AbstractAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            //선택 사항: IP주소 세션 ID등 추가로 토큰에 삽입
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // security context에 토큰 추가
            securityContext.setAuthentication(authenticationToken);
            // security context holder에 방금 만든 security context 추가
            SecurityContextHolder.setContext(securityContext);



        } catch (Exception e) {
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if(!hasAuthorization) return null;

        boolean isBearer = authorization.startsWith("Bearer ");
        if(!isBearer) return null;

        String token = authorization.substring(7);
        return token;

    }
}
