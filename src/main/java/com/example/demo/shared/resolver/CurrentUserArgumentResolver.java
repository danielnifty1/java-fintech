package com.example.demo.shared.resolver;

import com.example.demo.auth.entity.UserEntity;
import com.example.demo.auth.repository.UserEntityRepository;
import com.example.demo.jwt.JwtClaims;
import com.example.demo.jwt.JwtService;
import com.example.demo.shared.annotation.CurrentUser;
import com.example.demo.shared.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

 
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@AllArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserEntityRepository userRepository;
    private final JwtService jwtService; // ✅ inject JwtService

    private static final Logger logger =
    LoggerFactory.getLogger(CurrentUserArgumentResolver.class);

    @Override
public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(CurrentUser.class);
}

@Override
public Object resolveArgument(MethodParameter parameter,
                              ModelAndViewContainer mavContainer,
                              NativeWebRequest webRequest,
                              WebDataBinderFactory binderFactory) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new CustomException("No token provided", HttpStatus.UNAUTHORIZED);
    }

    String token = authHeader.substring(7);

    // ✅ if parameter type is JwtClaims → return raw JWT data
    if (parameter.getParameterType().equals(JwtClaims.class)) {
        return jwtService.extractAllClaims(token);
    }

    // ✅ if parameter type is UserEntity → return user from DB
    String email = jwtService.extractUsername(token);
    String userId = jwtService.extractUserId(token);

    UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

    if (!user.getId().equals(userId)) {
        throw new CustomException("Invalid token", HttpStatus.FORBIDDEN);
    }
    logger.info("the user: {}", user.getId());

//     logger.info("user id: {}", user.getId());
// logger.info("user email: {}", user.getEmail());

    return user;
}
}