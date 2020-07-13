package com.yh.mvc;
import com.yh.RemotingException;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RestfulRemotingCommand;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.*;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yingmuxiaoge on 2019/11/16.
 */
public class RpcRequestMappingHandlerMapping extends RequestMappingHandlerMapping implements InitializingBean,BeanFactoryAware,ServletConfigAware {
    @Autowired(required = false)
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    @Autowired(required = false)
    @Qualifier("rpcInterceptorRegistry")
    private RpcInterceptorRegistry rpcInterceptorRegistry;
    @Value("${project.easyrpc.springboot}")
    private boolean isSpringBoot;
    private BeanFactory beanFactory;
    private Map<RequestMappingInfo,HandlerMethod> rpcHandlerMethods = new ConcurrentHashMap<>(64);
    private List<HandlerInterceptor> interceptorList = new ArrayList<HandlerInterceptor>();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private ServletContext servletContext;
    private RpcMockFilterChain rpcMockFilterChain;

    public RpcRequestMappingHandlerMapping(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public RpcRequestMappingHandlerMapping() {
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        handlerMethodsRpcInitialized();
        if (requestMappingHandlerAdapter == null && beanFactory != null) {
            this.requestMappingHandlerAdapter = beanFactory.getBean(RequestMappingHandlerAdapter.class);
        }
        List<Object> objectList = null;
        if (rpcInterceptorRegistry == null && !isSpringBoot) {
            Collection collection = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    getApplicationContext(), MappedInterceptor.class, true, false).values();
            objectList = new ArrayList<>(collection);
        } else {
            objectList = rpcInterceptorRegistry.getRpcInterceptors();
        }
        if (!CollectionUtils.isEmpty(objectList)) {
           for (Object obj : objectList) {
               if (obj instanceof MappedInterceptor) {
                   interceptorList.add((MappedInterceptor)obj);
               } else {
                   interceptorList.add((HandlerInterceptor)obj);
               }
           }
        }
        rpcMockFilterChain = new RpcMockFilterChain(servletContext);
    }

    public void handlerMethodsRpcInitialized() {
        //初始化rpc restful集合
        Map<RequestMappingInfo,HandlerMethod> handlerMethodMap = getHandlerMethods();
        for(Map.Entry<RequestMappingInfo,HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            RestfulRpc restfulRpc = handlerMethod.getMethodAnnotation(RestfulRpc.class);
            if (restfulRpc != null) {
                rpcHandlerMethods.put(entry.getKey(),entry.getValue());
            }
        }
    }
    public Object doRpcHandler(RemotingCommand remotingCommand) throws Exception {
        MockHttpServletRequest request = null;
        MockHttpServletResponse response = null;
        HandlerMethod handlerMethod = null;
        int interceptorIndex = -1;
        Map<String,Object> attributeMap = null;
        try {
            if (!(remotingCommand instanceof RestfulRemotingCommand)) {
                return new RemotingException("请求参数缺少！");
            }
            RestfulRemotingCommand restfulRemotingCommand = (RestfulRemotingCommand)remotingCommand;
            request = setMockHttpServletRequest(restfulRemotingCommand);
            response = setMockHttpServletResponse();
            //先走过滤器
            if (rpcMockFilterChain != null) {
                rpcMockFilterChain.doRpcFilter(request,response);
            }
            String filterResult = new String(response.getContentAsByteArray(),"utf-8");
            if (!StringUtils.isEmpty(filterResult)) {
                return filterResult;
            }
            HandlerExecutionChain chain = getRpcHandlerExecutionChain(restfulRemotingCommand.getCtlUrl(), request,remotingCommand.getVersion());
            if (chain == null) {
                throw new RemotingException("该请求地址不支持easyRpc调用or调用版本对应不上:"+((RestfulRemotingCommand) remotingCommand).getCtlUrl());
            }
            attributeMap = setRequestAttributes(request);
            handlerMethod = (HandlerMethod) chain.getHandler();
            if (!applyPreHandle(request, response, handlerMethod, interceptorIndex)) {
                throw new RemotingException("拦截器applyPreHandle()校验失败"+((RestfulRemotingCommand) remotingCommand).getCtlUrl());
            }
            ModelAndView mv = requestMappingHandlerAdapter.handle(request, response, chain.getHandler());
            applyPostHandle(request, response, mv, handlerMethod);
            byte[] byteArray = response.getContentAsByteArray();
            return new String(byteArray, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            triggerAfterCompletion(request,response,handlerMethod,interceptorIndex,e);
        } finally {
            restRequestAttributes(attributeMap);
        }
        return null;
    }
    private HandlerExecutionChain getRpcHandlerExecutionChain(String url, MockHttpServletRequest request,String version) throws Exception {
        HandlerMethod handlerMethod = null;
        for (Map.Entry<RequestMappingInfo,HandlerMethod> entry : rpcHandlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            Set<String> patternSet = requestMappingInfo.getPatternsCondition().getPatterns();
            if (CollectionUtils.isEmpty(patternSet)) {
                continue;
            }
            RestfulRpc restfulRpc = entry.getValue().getMethodAnnotation(RestfulRpc.class);
            boolean isStop = false;
            for (String pattern : patternSet) {
                if (pattern.equals(url) && restfulRpc.enabled() && restfulRpc.version().equals(version)) {
                    handlerMethod = entry.getValue();
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (handlerMethod == null) {
            return null;
        }
        HandlerExecutionChain chain = getHandler(request);
        return chain;
    }
    private MockHttpServletRequest setMockHttpServletRequest(RestfulRemotingCommand rpcRequestDto) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCharacterEncoding("utf-8");
        request.setContentType(rpcRequestDto.getContentType());
        request.setRequestURI(rpcRequestDto.getCtlUrl());
        request.setMethod(rpcRequestDto.getMethodType());
        if (rpcRequestDto.getContentType().equals(ContentTypeEnum.X_WWW_FORM_URLENCODED.getVal())) {
            request.setParameters(rpcRequestDto.getParamsMap());
        } else if (rpcRequestDto.getContentType().equals(ContentTypeEnum.APPLICATION_JSON.getVal())) {
            request.setContent(rpcRequestDto.getParamJson().getBytes("utf-8"));
        } else {
            throw new RuntimeException("this contentType is not support by easyRpc:"+rpcRequestDto.getContentType());
        }
        return request;
    }
    private MockHttpServletResponse setMockHttpServletResponse() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        return response;
    }
    boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, int interceptorIndex) throws Exception {
        List<HandlerInterceptor> interceptorList = filterInterceptorByRequestURI(request.getRequestURI());
        if (!ObjectUtils.isEmpty(interceptorList)) {
            for (int i = 0; i < interceptorList.size(); i++) {
                HandlerInterceptor interceptor = interceptorList.get(i);
                if (!interceptor.preHandle(request, response, handlerMethod)) {
                    triggerAfterCompletion(request, response, handlerMethod,interceptorIndex,null);
                    return false;
                }
                interceptorIndex = i;
            }
        }
        return true;
    }

    private void applyPostHandle(HttpServletRequest request, HttpServletResponse response,
                                 ModelAndView mv, HandlerMethod handlerMethod) throws Exception {
        List<HandlerInterceptor> interceptorList = filterInterceptorByRequestURI(request.getRequestURI());
        if (!ObjectUtils.isEmpty(interceptorList)) {
            for (int i = interceptorList.size() - 1; i >= 0; i--) {
                HandlerInterceptor interceptor = interceptorList.get(i);
                interceptor.postHandle(request, response, handlerMethod, mv);
            }
        }
    }

    private void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, int interceptorIndex, Exception ex)
            throws Exception {
        List<HandlerInterceptor> interceptorList = filterInterceptorByRequestURI(request.getRequestURI());
        if (!ObjectUtils.isEmpty(interceptorList)) {
            for (int i = interceptorIndex; i >= 0; i--) {
                HandlerInterceptor interceptor = interceptorList.get(i);
                try {
                    interceptor.afterCompletion(request, response,handlerMethod, ex);
                }
                catch (Throwable ex2) {
                    logger.error("easyRpc HandlerInterceptor.afterCompletion threw exception", ex2);
                }
            }
        }
    }
    private List<HandlerInterceptor> filterInterceptorByRequestURI(String uri) {
        List<HandlerInterceptor> matchList = new ArrayList<>();
        for (HandlerInterceptor interceptor : interceptorList) {
            if (interceptor instanceof MappedInterceptor) {
                MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
                if (mappedInterceptor.matches(uri, this.pathMatcher)) {
                    matchList.add(mappedInterceptor.getInterceptor());
                }
            }
            else {
                matchList.add(interceptor);
            }
        }
        return matchList;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    private Map<String,Object> setRequestAttributes(MockHttpServletRequest request) {
        Map<String,Object> attributeMap = new HashMap<>(4,1f);
        LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
        attributeMap.put("previousLocaleContext",previousLocaleContext);
        LocaleContext localeContext = new SimpleLocaleContext(request.getLocale());
        RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
        attributeMap.put("previousAttributes",previousAttributes);
        ServletRequestAttributes requestAttributes = null;
        if (previousAttributes == null || previousAttributes instanceof ServletRequestAttributes) {
            requestAttributes = new ServletRequestAttributes(request);
        }
        if (localeContext != null) {
            LocaleContextHolder.setLocaleContext(localeContext,false);
        }
        if (requestAttributes != null) {
            RequestContextHolder.setRequestAttributes(requestAttributes,false);
            attributeMap.put("requestAttributes",requestAttributes);
        }
        return attributeMap;
    }
    private void restRequestAttributes(Map<String,Object> attributeMap) {
        if (attributeMap != null) {
            LocaleContextHolder.setLocaleContext((LocaleContext) attributeMap.get("previousLocaleContext"), false);
            RequestContextHolder.setRequestAttributes((RequestAttributes) attributeMap.get("previousAttributes"), false);
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributeMap.get("requestAttributes");
            if (servletRequestAttributes != null) {
                servletRequestAttributes.requestCompleted();
            }
        }
    }

    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletContext = servletConfig.getServletContext();
    }
}
