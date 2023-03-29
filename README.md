# fresh桶的xy项目的starter
fresh-xy-starter 工程为 fresh 桶的xy项目的starter
# 打包
需要将fresh-xy-starter及其子module打包安装到本地仓库。需要将fresh-xy-starter及其子module打包发布到远端仓库。  

子module不应依赖于继承此starter的xy项目的任何jar。  

对fresh-xy-starter 的 pom打包，会将自身及所有子module打包。  

子module单独打包。没有-am参数，假设仓库里面没有fresh-xy-starter, 或者仓库里面fresh-xy-starter不是最新的。
则此子module打的包是无效的(不能被其他工程依赖)。  

子module单独打包。没有-am参数，子module依赖于此starter中其他子module时，打包失败。  

-am参数。子module单独打包时，会将此module依赖的其他子module同时打包。  

子module不应相互依赖。  


