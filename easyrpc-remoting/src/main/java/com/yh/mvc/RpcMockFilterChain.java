package com.yh.mvc;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcMockFilterChain implements FilterChain {
    private Map<String,Filter> FILTER_CHAIN_MAP = new ConcurrentHashMap<>();
    private PathMatcher pathMatcher = new AntPathMatcher();
    public void doRpcFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        if (!FILTER_CHAIN_MAP.isEmpty()) {
            List<Filter> filterList = new ArrayList<>(FILTER_CHAIN_MAP.size());
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String uri = request.getRequestURI();
            for (Map.Entry<String,Filter> entry : FILTER_CHAIN_MAP.entrySet()) {
               String[] uriAttr = StringUtils.tokenizeToStringArray(entry.getKey(),",");
               if (uriAttr == null || uriAttr.length == 0) {
                   continue;
               }
               for (String st : uriAttr) {
                   if (pathMatcher.match(st,uri)) {
                       filterList.add(entry.getValue());
                       break;
                   }
               }
            }
            if (!CollectionUtils.isEmpty(filterList)) {
                for (Filter filter : filterList) {
                    filter.doFilter(servletRequest,servletResponse,this);
                }
            }
        }
    }
    public RpcMockFilterChain(ServletContext servletContext) {
        Map<String, ? extends FilterRegistration> filterMap = servletContext.getFilterRegistrations();
        setRpcMockFilterChain(filterMap,servletContext);
    }

    private void setRpcMockFilterChain(Map<String, ? extends FilterRegistration> filterMap,ServletContext servletContext) {
        if(!CollectionUtils.isEmpty(filterMap)) {
            for (Map.Entry<String,? extends FilterRegistration> entry : filterMap.entrySet()) {
                try {
                    Class cla = Class.forName(entry.getValue().getClassName());
                    Constructor<?>[] constructors =  cla.getConstructors();
                    if (constructors == null && constructors.length ==0) {
                        continue;
                    }
                    boolean hasNoParamConst = false;
                    for (Constructor constructor : constructors) {
                        Class[] classes = constructor.getParameterTypes();
                        if (classes == null || classes.length ==0) {
                            hasNoParamConst = true;
                            break;
                        }
                    }
                    if (!hasNoParamConst) {
                        continue;
                    }
                    Filter filter = (Filter)cla.newInstance();
                    MockFilterConfig mockFilterConfig = new MockFilterConfig(servletContext,entry.getKey());
                    filter.init(mockFilterConfig);
                    FILTER_CHAIN_MAP.put(StringUtils.collectionToCommaDelimitedString(entry.getValue().getUrlPatternMappings()),filter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {

    }
}
