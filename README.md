# xy 项目组成
xy 项目由多个程序（工程）组成：

| 程序（工程） | groupId | artifactId | pacakge |
| --- | --- | ---| - |
| xy-common | com.fresh.xy | xy-common | com.fresh.xy.common |
| xy-mybatis-starter | com.fresh.xy | xy-mybatis-starter | com.fresh.xy.mb |
| xy-mybatisplus-starter | com.fresh.xy | xy-mybatisplus-starter | com.fresh.xy.mbp |
| xy-redis-starter | com.fresh.xy | xy-redis-starter | com.fresh.xy.redis |
| xy-rmq-starter | com.fresh.xy | xy-rmq-starter | com.fresh.xy.rmq |
| xy-project-webflux | com.fresh.xy | xy-project-webflux | com.fresh.xy.webflux |
| xy-service-gateway | com.fresh.xy | xy-service-gateway | com.fresh.xy.gateway |
| xy-service-sample | com.fresh.xy | xy-service-sample | com.fresh.xy.sample |
| xy-service-sample2 | com.fresh.xy | xy-service-sample2 | com.fresh.xy.sample2 |

xy 项目下多个程序（工程）有相同的 groupId，多个程序（工程）通过 artifactId 区分。多个程序（工程）有不同的 package name。  

xy-common-parent 为 xy 项目的公共 maven 配置依赖，其 pom.xml 中写入公共的 maven 配置，以上工程 pom.xml 均继承自 xy-common-parent。  

像 xy-common, xy-mybatis-starter, xy-mybatisplus-starter, xy-redis-starter, xy-rmq-starter 这些程序（工程）属于比较公共的包。
为了方便管理，将他们作为 xy-common-parent 的子 module 维护。（在 xy-common-parent 这一级目录创建 git 仓库）  

xy-service-sample-api 是从 xy-service-sample 抽取出来创建的程序，其 groupId: com.fresh.xy，artifactId: xy-service-sample-api，
package: com.fresh.xy.sample 与 xy-service-sample 一致，但注意在 xy-service-sample-api 中 sample 下面的子 package 不能与
xy-service-sample 中 sample 下面的子 package 重名。xy-service-sample2-api 同理。  

为什么要从 xy-service-sample 抽取出 xy-service-sample-api，主要是为了避免循环依赖...  

# xy-common-parent 说明
什么样的程序（工程）应该放到 xy-common-parent 中。应是 xy 项目中比较公共的部分，放置到 xy-common-parent 中的程序（工程）不应该依赖 xy 项目
下放置在 xy-common-parent 之外的程序（工程）而是相反方向依赖。  

xy-common-parent 中的程序（工程）不应该相互依赖。  

一般对此 parent 打包，会将自身即所有子 module 打包。  

子 module 单独打包。没有 -am 参数，假设仓库里面没有此 parent, 或者仓库里面 parent 不是最新的。则此子 module 打的包是无效的（不能被其他工程依赖）。  

子 module 单独打包。没有 -am 参数，子 module 依赖于此 parent 中其他子 module 时，打包失败。
即通过增加 -am 参数，子 module 单独打包时，会将此 module 依赖的其他子 module 同时打包。  


