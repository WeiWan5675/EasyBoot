package com.weiwan.easyboot.controller.system;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.weiwan.easyboot.model.entity.SysMenu;
import com.weiwan.easyboot.model.Result;
import com.weiwan.easyboot.utils.TreeHelper;
import com.weiwan.easyboot.controller.BaseController;
import com.weiwan.easyboot.model.dto.UserAo;
import com.weiwan.easyboot.model.dto.UserInfo;
import com.weiwan.easyboot.model.dto.UserResourcesVo;
import com.weiwan.easyboot.model.dto.vue.RouteMeta;
import com.weiwan.easyboot.model.dto.vue.VueRouteMenu;
import com.weiwan.easyboot.security.SecurityUtils;
import com.weiwan.easyboot.security.LoginUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "个人用户信息")
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final LoginUserService loginUserService;

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/info")
    public Result<UserInfo> getUserInfo() {
        UserInfo user = loginUserService.userInfo(SecurityUtils.getUserId());
        return Result.ok(user);
    }

    @ApiOperation(value = "获取用户信息")
    @PostMapping("/save_user_info")
    public Result saveUserInfo(@Valid @RequestBody UserAo userAo) {
        loginUserService.saveUserInfo(SecurityUtils.getUserId(), userAo);
        return Result.SUCCESS;
    }

    @ApiOperation(value = "更新密码")
    @PostMapping("/update_password")
    public Result updatePassword(@RequestParam @NotEmpty @Size(min = 6, max = 50) String oldPassword,
        @RequestParam @NotEmpty @Size(min = 6, max = 50) String newPassword) {
        boolean result = loginUserService.updatePassword(SecurityUtils.getUserId(), oldPassword, newPassword);
        if (!result) {
            return Result.error("原密码错误");
        }
        return Result.SUCCESS;
    }

    @ApiOperation(value = "获取资源")
    @GetMapping("/get_resources")
    public Result<UserResourcesVo> getResources() {
        UserInfo user = SecurityUtils.getUser();
        // 角色
        Set<String> roles =
            loginUserService.findRolesByUserId(user.getUserId()).stream().map(v -> v.getCode()).collect(Collectors.toSet());

        List<SysMenu> menus = loginUserService.findMenusByUserId(user.getUserId());
        // 权限
        Set<String> permissions = menus.stream().filter(v -> StringUtils.isNotBlank(v.getPermission()))
            .map(v -> StringUtils.trim(v.getPermission())).collect(Collectors.toSet());

        UserResourcesVo userResourcesVo = new UserResourcesVo(roles, permissions, this.getRoutes(menus));

        return Result.ok(userResourcesVo);
    }

    private List<VueRouteMenu> getRoutes(List<SysMenu> menus) {
        Map<Integer, List<SysMenu>> parentIdGroup = menus.stream().filter(v -> SysMenu.MENU_TYPE_BUTTON != v.getType())
            .collect(Collectors.groupingBy(sysMenu -> sysMenu.getParentId()));
        // 先找到根节点
        List<SysMenu> sysMenus = parentIdGroup.get(TreeHelper.DEFAULT_PARENT_ID);
        if (null == sysMenus || sysMenus.isEmpty()) {
            return Collections.emptyList();
        }
        return this.genRoutes(sysMenus, parentIdGroup);
    }

    private List<VueRouteMenu> genRoutes(List<SysMenu> sysMenus, Map<Integer, List<SysMenu>> parentIdGroup) {
        List<VueRouteMenu> vueRouteMenus = new ArrayList<>();
        sysMenus.forEach(menu -> {
            String uriPath = menu.getUrl();
            // 标准化下后面使用，menu.getUrl() 可能会携带参数
            if (StringUtils.isNotEmpty(uriPath)) {
                UriComponents uriComponents = UriComponentsBuilder.fromUriString(menu.getUrl()).build();
                uriPath = uriComponents.getPath();
            }

            VueRouteMenu route = new VueRouteMenu();
            route.setMeta(new RouteMeta(menu.getName(), menu.getIcon()));
            if (menu.getType() == SysMenu.MENU_TYPE_DIRECTORY) {
                // 如果不设置点击时候目录path相同的会一起展开,child 使用的绝对路径不受影响
                route.setPath("/" + menu.getName());
                route.setComponent(VueRouteMenu.COMPONENT_LAYOUT);
            } else if (menu.getType() == SysMenu.MENU_TYPE_MENU) {
                if (null != menu.getUrl() && !menu.getUrl().startsWith("https://")
                    && !menu.getUrl().startsWith("http://")) {
                    route.setComponent(uriPath + "/index");
                    route.setPath(menu.getUrl());
                } else { // 外部链接
                    route.setComponent(VueRouteMenu.COMPONENT_IFRAME);
                    if (SysMenu.TARGET_MAIN.equals(menu.getTarget())) {// 内部特殊结构,外部前端自动处理
                        route.setPath(StringUtils.isNotEmpty(uriPath) ? uriPath : "/" + menu.getId());
                        route.getMeta().setFrameSrc(menu.getUrl());
                    } else {
                        route.setPath(menu.getUrl());
                    }
                }
            }
            // 名字和组件名字对应才可以keepAlive
            if (StringUtils.isNotEmpty(menu.getUrl())) {
                String[] menuSplit = StringUtils.split(uriPath, "/");
                if (menuSplit.length > 0) {
                    String name = "";
                    for (String s : menuSplit) {
                        name += CaseUtils.toCamelCase(s, true, '_', '-');
                    }
                    route.setName(name);
                }
            }
            // 处理子路由
            List<SysMenu> children = parentIdGroup.get(menu.getId());
            if (null != children && !children.isEmpty()) {
                route.setChildren(this.genRoutes(children, parentIdGroup));
            }
            vueRouteMenus.add(route);
        });
        return vueRouteMenus;
    }
}
