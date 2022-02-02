package ar.edu.itba.paw.webapp.security.api.basic;

import ar.edu.itba.paw.webapp.security.api.exception.InvalidUsernamePasswordException;
import ar.edu.itba.paw.webapp.security.api.model.AuthenticationTokenDetails;
import ar.edu.itba.paw.webapp.security.api.model.Authority;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BasicAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationTokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        BasicAuthenticationToken auth = (BasicAuthenticationToken) authentication;
        String[] credentials;
        try {
            credentials = new String(Base64.getDecoder().decode(auth.getToken())).split(":");
        } catch (IllegalArgumentException iae) {
            throw new BadCredentialsException("Invalid basic header");
        }
        if(credentials.length != 2) {
            throw new InvalidUsernamePasswordException("Invalid username/password");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(credentials[0]);
        String authenticationToken = tokenService.issueToken(credentials[0],
                mapToAuthority(userDetails.getAuthorities()));
        AuthenticationTokenDetails tokenDetails = tokenService.parseToken(authenticationToken);
        BasicAuthenticationToken trustedAuth = new BasicAuthenticationToken(credentials[0], credentials[1], userDetails.getAuthorities(), tokenDetails);
        trustedAuth.setToken(authenticationToken);
        return trustedAuth;
    }

    private Set<Authority> mapToAuthority(Collection<? extends GrantedAuthority> authorities) {
        return  authorities.stream()
                .map(grantedAuthority -> Authority.valueOf(grantedAuthority.toString()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (BasicAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
