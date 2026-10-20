# local-maven：官方仓库缺失构件的本地替身

## net.neoforged:minecraft-dependencies:1.21.1

NeoForm 1.21.1（`1.21.1-20240808.144430`）的 pom 声明了这个依赖，但它在
`maven.neoforged.net` 与所有已知镜像上都是 **404**（1.20.1 / 1.21 / 1.21.4 同样没有）。
它承载的是「Minecraft 原版依赖清单」——ModDevGradle 用它组装编译/运行类路径，
不提供会出现 `NoClassDefFoundError: com/mojang/serialization/DynamicOps` 之类的运行时错误。

因此这里按 Mojang 的 1.21.1 `version.json` 生成了一份等价的 POM（只声明依赖，`packaging=pom`）：

```powershell
$vj = (Invoke-WebRequest "https://piston-meta.mojang.com/v1/packages/1757c8b1fbfc6b638504e1f6cceb05893f7389d5/1.21.1.json" -UseBasicParsing).Content | ConvertFrom-Json
# 遍历 $vj.libraries，把 name（group:artifact:version[:classifier]）逐条写成 <dependency>
```

`build.gradle` 里把该目录作为 `LocalFixes` 仓库放在最前面，命中后即不再向上游请求。

> 换 Minecraft 版本时记得同步重新生成这份 POM。