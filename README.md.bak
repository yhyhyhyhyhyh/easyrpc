<p align="center">
    <h3 align="center">EASY-RPC</h3>
    <p align="center">
       http://172.30.3.50/yanjunxiang/easyrpc
        <br>
        <a href="http://172.30.3.50/yanjunxiang/easyrpc"><strong>-- Home Page --</strong></a>
        <br>
    </p>    
</p>


## 接入方式
- pom中引入依赖：<br>
        `<dependency>` <br>
			`<groupId>com.aisino</groupId>` <br>
			`<artifactId>easyrpc-remoting</artifactId>` <br>
			`<version>1.0.0-SNAPSHOT</version>`<br>
		`</dependency>` <br>
1:普通springmvc项目：<br>
- 在配置文件中新增配置<br>  
- project.easyrpc.springboot=false<br>
- project.easyrpc.instanceName=fpdkapi <br>
- project.easyrpc.port=8081 <br>
- 在spring-servlet.xml中注入如下bean: 
<br>
    `<bean id="rpcRequestMappingHandlerMapping" class="com.aisino.mvc.RpcRequestMappingHandlerMapping"/>` <br>
    `<bean id ="easyRpcServer" class="com.aisino.remoting.EasyRpcServer">` <br>
        `<constructor-arg name="instanceName" value="${project.easyrpc.instanceName}"/>`<br>
        `<constructor-arg name="port" value="${project.easyrpc.port}"/>`<br>
        `<constructor-arg name="dataSource" ref="manageDataSource"/>`<br>
        `<constructor-arg name="isIoc" value="true"/>`<br>
    `</bean>`<br>


2:springboot项目： <br>
- 在配置文件中新增配置<br> 
- project.easyrpc.springboot=true <br> 
- project.easyrpc.instance.instanceName=fpdkht <br> 
- project.easyrpc.port=8081  <br> 


3：相关配置含义：<br>
- 是否是springboot项目 project.easyrpc.springboot=true
- 需要暴露的服务对应的唯一标示 project.easyrpc.instanceName=xxx
- 对外暴露服务的系统的通信端口 project.easyrpc.port=xxx

