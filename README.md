# TRouter
一个简单的模块解耦路由实现
(简单演示, 忽略代码质量)

主要是APT注解处理器的应用

*TODO* 路由跳转AOP拦截器

使用:
```
TRouter.getInstance()
                .setPath("/login/LoginActivity")
                .addStringField("info", "TRouter Msg")
                .navigation()
```

```
@TRouterAnt(path = "/login/LoginActivity")
```
