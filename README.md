groupId: com.fresh.xy
# 打包
子 module 不应依赖于继承此 parent 的 xy 项目的任何 jar。  

对此 parent 的 pom 打包，会将自身及所有子 module 打包。  

子 module 单独打包。没有 -am 参数，假设仓库里面没有此 parent, 或者仓库里面 parent 不是最新的。
则此子 module 打的包是无效的(不能被其他工程依赖)。  

子 module 单独打包。没有 -am 参数，子 module 依赖于此 parent 中其他子 module 时，打包失败。  

-am 参数。子 module 单独打包时，会将此 module 依赖的其他子 module 同时打包。  

子 module 不应相互依赖。  


