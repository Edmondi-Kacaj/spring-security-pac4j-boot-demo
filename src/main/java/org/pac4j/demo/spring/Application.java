package org.pac4j.demo.spring;

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.springframework.annotation.ui.RequireAnyRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Optional;

@Controller
public class Application {

    private static final String PROFILES = "profiles";
    private static final String SESSION_ID = "sessionId";

    @Value("${salt}")
    private String salt;

    @Autowired
    private Config config;

    @Autowired
    private J2EContext j2EContext;

    @Autowired
    private ProfileManager profileManager;

    @RequestMapping("/")
    public String root(Map<String, Object> map) throws HttpAction {
        return index(map);
    }

    @RequestMapping("/index.html")
    public String index(Map<String, Object> map) throws HttpAction {
        map.put(PROFILES, profileManager.getAll(true));
        map.put(SESSION_ID, j2EContext.getSessionStore().getOrCreateSessionId(j2EContext));
        return "index";
    }

    @RequestMapping("/facebook/index.html")
    public String facebook(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/facebook/notprotected.html")
    public String facebookNotProtected(Map<String, Object> map) {
        map.put(PROFILES, profileManager.getAll(true));
        return "notProtected";
    }

    @RequestMapping("/facebookadmin/index.html")
    @RequireAnyRole("ROLE_ADMIN")
    public String facebookadmin(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/facebookcustom/index.html")
    public String facebookcustom(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/twitter/index.html")
    public String twitter(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/form/index.html")
    public String form(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/basicauth/index.html")
    public String basicauth(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/cas/index.html")
    public String cas(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/casrest/index.html")
    public String casrest(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/saml/index.html")
    public String saml(Map<String, Object> map) {
        map.put(PROFILES, profileManager.getAll(true));
        return "samlIndex";
    }

    @RequestMapping("/saml/admin.html")
    public String samlAdmin(Map<String, Object> map) {
        map.put(PROFILES, profileManager.getAll(true));
        return "samlAdmin";
    }

    @RequestMapping("/oidc/index.html")
    public String oidc(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/protected/index.html")
    public String protect(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/dba/index.html")
    public String dba(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/rest-jwt/index.html")
    public String restJwt(Map<String, Object> map) {
        return protectedIndex(map);
    }

    @RequestMapping("/jwt.html")
    public String jwt(Map<String, Object> map) {
        final JwtGenerator generator = new JwtGenerator(new SecretSignatureConfiguration(salt), new SecretEncryptionConfiguration(salt));
        String token = "";
        final Optional<CommonProfile> profile = profileManager.get(true);
        if (profile.isPresent()) {
            token = generator.generate(profile.get());
        }
        map.put("token", token);
        return "jwt";
    }

    @RequestMapping("/loginForm")
    public String loginForm(Map<String, Object> map) {
        final FormClient formClient = (FormClient) config.getClients().findClient("FormClient");
        map.put("callbackUrl", formClient.getCallbackUrl());
        return "form";
    }

    @RequestMapping("/forceLogin")
    @ResponseBody
    public String forceLogin() {

        final Client client = config.getClients().findClient(j2EContext.getRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER));
        try {
            client.redirect(j2EContext);
        } catch (final HttpAction e) {
        }
        return null;
    }

    protected String protectedIndex(Map<String, Object> map) {
        map.put(PROFILES, profileManager.getAll(true));
        return "protectedIndex";
    }

    @ExceptionHandler(HttpAction.class)
    public void httpAction() {
        // do nothing
    }
}
