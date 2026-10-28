package org.shaloman.pj.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 역할(Role) 기반 API 접근 제어 필터.
 *
 * 시스템 관리 API는 SYS_ADMIN 권한만 접근 가능.
 * 콘텐츠 API는 모든 인증된 사용자가 접근 가능.
 *
 * 주의: /api/menus/tree (콘텐츠용 메뉴 조회)는 모든 사용자 허용,
 *       /api/menus, /api/menus/{id}, /api/menus/order, /api/menus/{id}/move (관리용)은 SYS_ADMIN만.
 */
public class RoleAuthorizationFilter extends OncePerRequestFilter {

    // 시스템 관리 API — SYS_ADMIN만 접근 (정확히 매칭)
    private static final Set<String> ADMIN_EXACT_PATHS = Set.of(
        "/api/users",
        "/api/roles",
        "/api/permissions",
        "/api/group-codes",
        "/api/common-codes"
    );

    // /api/menus 하위 경로 중 SYS_ADMIN만 접근 (tree 제외)
    // /api/menus, /api/menus/{id}, /api/menus/order, /api/menus/{id}/move, /api/menus/{id}/children
    // /api/menus/tree 는 모든 사용자 허용

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (isAdminOnlyApi(path, method)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                sendForbidden(response);
                return;
            }

            String roleGroup = extractRoleGroup(auth);
            if (!"SYS_ADMIN".equals(roleGroup)) {
                sendForbidden(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAdminOnlyApi(String path, String method) {
        // 1. 정확히 매칭하는 admin API
        for (String adminPath : ADMIN_EXACT_PATHS) {
            if (path.equals(adminPath) || path.startsWith(adminPath + "/")) {
                // GET /api/roles 는 모든 사용자 허용 (드롭다운용)
                if (path.equals("/api/roles") && "GET".equals(method)) {
                    return false;
                }
                // GET /api/roles/{id} 도 모든 사용자 허용
                if (path.startsWith("/api/roles/") && "GET".equals(method)) {
                    return false;
                }
                return true;
            }
        }

        // 2. /api/menus 하위 — tree와 특정 하위 경로 제외
        if (path.equals("/api/menus")) {
            return true; // 관리용
        }
        if (path.startsWith("/api/menus/")) {
            // /api/menus/tree — 모든 사용자 허용 (콘텐츠용 메뉴 조회)
            if (path.equals("/api/menus/tree")) {
                return false;
            }
            // 나머지 /api/menus/{id}, /api/menus/order 등 — SYS_ADMIN만
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private String extractRoleGroup(Authentication auth) {
        Object details = auth.getDetails();
        if (details instanceof Map) {
            Object roleGroup = ((Map<String, Object>) details).get("roleGroup");
            return roleGroup != null ? roleGroup.toString() : null;
        }
        return null;
    }

    private void sendForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"시스템 관리 권한이 필요합니다.\",\"status\":403}");
    }
}